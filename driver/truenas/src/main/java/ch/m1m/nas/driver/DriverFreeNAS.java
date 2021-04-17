package ch.m1m.nas.driver;

import ch.m1m.nas.driver.api.Driver;
import ch.m1m.nas.lib.Config;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

// started developing against FreeNAS 11.2
// documentation: http://api.freenas.org/
//
// FreeNAS 11.3 hast updated the api to v2.0
// TrueNAS 12.0 uses also v2.0 api
// documentation: https://www.truenas.com/docs/hub/additional-topics/api/rest_api/
//
// http://freenas.local/api/v2.0/pool
//
// possible candidates:
// http://freenas.local/api/v2.0/alert/list
// http://freenas.local/api/v2.0/disk
//
public class DriverFreeNAS implements Driver {

    private static Logger log = LoggerFactory.getLogger(DriverFreeNAS.class);

    private String nasHostUrl;
    private String nasBaseUrl;
    private String nasUser;
    private String nasUserPassword;
    private String nasVersion;

    private Config config;
    private Client nasClient;

    private static String HEADER_CONTENT_TYPE = "Content-Type";

    public DriverFreeNAS(Config config) {
        this.config = config;

        nasBaseUrl = config.getNasAdminUI();
        if (nasBaseUrl == null) {
            throw new IllegalArgumentException("config value for NasAdminUI is null");
        }
        if (!nasBaseUrl.endsWith("/")) {
            nasBaseUrl += "/";
        }

        nasHostUrl = nasBaseUrl;
        assignNasBaseUrl(1);

        nasUser = config.getNasUserId();
        nasUserPassword = config.getNasUserPassword();
        if (nasUserPassword == null) nasUserPassword = "";

        // configure the REST client
        //
        HttpAuthenticationFeature featureBasicAuth = null;
        if (nasUser != null) {
            featureBasicAuth = HttpAuthenticationFeature.basic(nasUser, nasUserPassword);
        }
        nasClient = ClientBuilder.newBuilder()
                .connectTimeout(7, TimeUnit.SECONDS)
                .readTimeout(7, TimeUnit.SECONDS)
                .build();

        if (featureBasicAuth != null) {
            nasClient.register(featureBasicAuth);
        } else {
            log.debug("no BasicAuth feature to register");
        }

        //nasClient.property(ClientProperties.CONNECT_TIMEOUT, 7 * 1_000);
        //nasClient.property(ClientProperties.READ_TIMEOUT, 7 * 1_000);
    }

    @Override
    public NasStatus getStatus() {

        NasStatus status = NasStatus.UNKNOWN;

        log.debug("call REST api to query the FreeNAS pools: {}", nasBaseUrl);
        if (isHigherVersionThan_11_3()) {
            status = queryPool();
        } else {
            status = queryStorageVolume();
        }
        log.debug("getStatus() returns: {}", status.toString());

        return status;
    }

    /*
     * This version upgrades silently from v1.0 to v2.0 protocol in the
     * case the first response ist not parsable by JSON e.g. throws an exception.
     *
     */
    @Override
    public String getVersion() {

        int retryCount = 0;
        String version = null;

        do {
            String targetUrl = nasBaseUrl + "/system/version/";
            log.info("making rest call to url={}", targetUrl);
            try {
                Response response = nasClient.target(targetUrl)
                        .request(MediaType.APPLICATION_JSON)
                        .get();

                int httpStatus = response.getStatus();
                log.info("response http status: {}", httpStatus);
                String contentType = response.getHeaderString(HEADER_CONTENT_TYPE);
                log.info("response mediaType: {}", contentType);

                String responseJsonString = response.readEntity(String.class);
                log.debug("response JSON: {}", responseJsonString);

                if (responseJsonString != null) {
                    String respLower = responseJsonString.toLowerCase();
                    if (respLower.startsWith("\"truenas")) {
                        version = responseJsonString.replaceAll("\"", "");
                        break;
                    }
                }

                JsonReader jsonReader = Json.createReader(new StringReader(responseJsonString));
                JsonObject jsonObject = jsonReader.readObject();
                version = jsonObject.getString("fullversion");

// javax.ws.rs.ProcessingException: java.net.UnknownHostException: freenas.local
// break loop when host is not found
//
//            } catch (UnknownHostException e) {
//                int x = 15;

            } catch (Exception e) {
                if (retryCount == 1) {
                    version = "unknown";
                }
                log.error("Exception in rest call", e);

                // make a protocol upgrade and try again
                //
                assignNasBaseUrl(2);
                log.info("update protocol to: {}", nasBaseUrl);
                retryCount++;
                log.info("retry version call...");
            }
        } while (version == null);

        log.debug("getVersion() returns: {}", version);
        nasVersion = version;
        return version;
    }

    @Override
    public void shutdown() {
        sendShutdown();
    }

    private void assignNasBaseUrl(int protocolMajorVersion) {
        nasBaseUrl = String.format("%sapi/v%d.0", nasHostUrl, protocolMajorVersion);
    }

    private boolean isHigherVersionThan_11_3() {
        // TrueNAS-12.0-U1
        boolean rc = false;

        try {
            if (nasVersion != null) {
                Scanner scan = new Scanner(nasVersion);
                scan.useDelimiter("-");
                String ignorePorductName = scan.next();
                String version = scan.next();
                BigDecimal numVersion = new BigDecimal(version);
                BigDecimal numOld_11_2 = new BigDecimal("11.2");
                if (numVersion.compareTo(numOld_11_2) == 1) {
                    rc = true;
                }
            }
        } catch (NoSuchElementException e) {
            // just ignore
        }

        return rc;
    }

    private void sendShutdown() {

        String targetUrl = nasBaseUrl + "/system/shutdown/";
        log.info("making rest call to url={}", targetUrl);

        try {
            Response response = nasClient.target(targetUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(null));

            int httpStatus = response.getStatus();
            log.info("response http status: {}", httpStatus);

        } catch (Exception e) {
            log.error("Exception in rest call", e);
        }
    }

    private NasStatus queryPool() {

        NasStatus rc = NasStatus.UNKNOWN;

        String targetUrl = nasBaseUrl + "/pool";
        String jsonString = null;

        log.debug("making rest call to url={}", targetUrl);

        try {
            Response response = nasClient.target(targetUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            int httpStatus = response.getStatus();
            log.debug("response http status: {}", httpStatus);

            if (httpStatus == 200) {
                String responseJsonString = response.readEntity(String.class);
                log.debug("response JSON: {}", responseJsonString);

                // parse the response with JSON-P (json processing)
                //
                JsonReader jsonReader = Json.createReader(new StringReader(responseJsonString));
                JsonArray jsonArray = jsonReader.readArray();
                ListIterator respStatusIterator = jsonArray.listIterator();

                while (respStatusIterator.hasNext()) {
                    JsonObject statusObject = (JsonObject) respStatusIterator.next();
                    boolean isHealthy = statusObject.getBoolean("healthy");
                    //String valStatus = statusObject.getString("healthy");
                    String valName = statusObject.getString("name");
                    log.debug("name: {} healthy: {}", valName, isHealthy);
                    if (isHealthy) {
                        rc = NasStatus.SUCCESS;
                    } else {
                        rc = NasStatus.ERROR;
                        log.warn("at least one Pool is unhealthy");
                        break;
                    }
                }
            }
        } catch (ProcessingException e) {
            log.error("ProcessingException in rest call", e);

        } catch (Exception e) {
            log.error("Exception in rest call", e);
        }

        return rc;
    }

    /*
     * Used up to version 11.2 to determine the status.
     * From 11.3 on we use queryPool().
     */
    private NasStatus queryStorageVolume() {

        NasStatus rc = NasStatus.UNKNOWN;

        String targetUrl = nasBaseUrl + "/storage/volume/";
        String jsonString = null;

        log.debug("making rest call to url={}", targetUrl);

        try {
            Response response = nasClient.target(targetUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            int httpStatus = response.getStatus();
            log.debug("response http status: {}", httpStatus);

            if (httpStatus == 200) {
                String responseJsonString = response.readEntity(String.class);
                log.debug("response JSON: {}", responseJsonString);

                // parse the response with JSON-P (json processing)
                //
                JsonReader jsonReader = Json.createReader(new StringReader(responseJsonString));
                JsonArray jsonArray = jsonReader.readArray();
                ListIterator respStatusIterator = jsonArray.listIterator();

                while (respStatusIterator.hasNext()) {
                    JsonObject statusObject = (JsonObject) respStatusIterator.next();
                    String valStatus = statusObject.getString("status");
                    String valName = statusObject.getString("name");
                    log.debug("name: {} status: {}", valName, valStatus);
                    if ("HEALTHY".equalsIgnoreCase(valStatus)) {
                        rc = NasStatus.SUCCESS;
                    } else {
                        rc = NasStatus.ERROR;
                        log.warn("at least one Pool is unhealthy");
                        break;
                    }
                }
            }
        } catch (ProcessingException e) {
            log.error("ProcessingException in rest call", e);

        } catch (Exception e) {
            log.error("Exception in rest call", e);
        }

        return rc;
    }
}
