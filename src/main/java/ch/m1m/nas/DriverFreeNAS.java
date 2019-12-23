package ch.m1m.nas;

import org.glassfish.jersey.client.ClientProperties;
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
import java.util.ListIterator;

// documentation: http://api.freenas.org/

public class DriverFreeNAS implements DriverInterface {

    private static Logger log = LoggerFactory.getLogger(DriverFreeNAS.class);

    private String nasBaseUrl;
    private String nasUser;
    private String nasUserPassword;

    private Config config;
    private Client nasClient;


    public DriverFreeNAS(Config config) {
        this.config = config;

        nasBaseUrl = config.getNasAdminUI();
        if (nasBaseUrl == null) {
            throw new IllegalArgumentException("config value for NasAdminUI is null");
        }
        if (!nasBaseUrl.endsWith("/")) {
            nasBaseUrl += "/";
        }
        nasBaseUrl += "api/v1.0";

        nasUser = config.getNasUserId();
        nasUserPassword = config.getNasUserPassword();
        if (nasUserPassword == null) nasUserPassword = "";

        // configure the REST client
        //
        HttpAuthenticationFeature featureBasicAuth = null;
        if (nasUser != null) {
            featureBasicAuth = HttpAuthenticationFeature.basic(nasUser, nasUserPassword);
        }
        nasClient = ClientBuilder.newClient();
        if (featureBasicAuth != null) {
            nasClient.register(featureBasicAuth);
        } else {
            log.debug("no BasicAuth feature to register");
        }

        nasClient.property(ClientProperties.CONNECT_TIMEOUT, 3 * 1_000);
        nasClient.property(ClientProperties.READ_TIMEOUT, 5 * 1_000);
    }

    @Override
    public NasStatus getStatus() {

        NasStatus status;
        log.debug("call REST api to query the FreeNAS pools: {}", nasBaseUrl);
        status = queryStorageVvolume();
        log.debug("getStatus() returns: {}", status.toString());

        return status;
    }

    @Override
    public String getVersion() {
        String version = "unknown";
        String targetUrl = nasBaseUrl + "/system/version/";
        log.info("making rest call to url={}", targetUrl);
        try {
            Response response = nasClient.target(targetUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            int httpStatus = response.getStatus();
            log.info("response http status: {}", httpStatus);

            String responseJsonString = response.readEntity(String.class);
            log.debug("response JSON: {}", responseJsonString);

            JsonReader jsonReader = Json.createReader(new StringReader(responseJsonString));
            JsonObject jsonObject = jsonReader.readObject();
            version = jsonObject.getString("fullversion");

        } catch (Exception e) {
            log.error("Exception in rest call", e);
        }
        log.debug("getVersion() returns: {}", version);
        return version;
    }

    @Override
    public void shutdown() {
        sendShutdown();
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

    private NasStatus queryStorageVvolume() {

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
