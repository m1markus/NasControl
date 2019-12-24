package ch.m1m.nas;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.ClasspathLocationStrategy;
import org.apache.commons.configuration2.io.CombinedLocationStrategy;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileSystemLocationStrategy;
import org.apache.commons.configuration2.io.HomeDirectoryLocationStrategy;
import org.apache.commons.configuration2.io.ProvidedURLLocationStrategy;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

// https://commons.apache.org/proper/commons-configuration/userguide/user_guide.html
// https://commons.apache.org/proper/commons-configuration/userguide/quick_start.html

public class ConfigUtils {

    /**
     * Default properties file from classpath.
     */
    private static final String DEFAULTS_PROPERTIES = "defaults.properties";

    private static final String KEY_BROADCAST_ADDRESS = "network.broadcast_address";
    private static final String KEY_NAS_MAC_ADDRESS = "nas.mac_address";
    private static final String KEY_NAS_ADMINUI_URL = "nas.adminui_url";
    private static final String KEY_NAS_USER_ID = "nas.user_id";
    private static final String KEY_NAS_USER_PASSWORD = "nas.user_password";
    private static final String KEY_VERSION = "version";
    private static final String KEY_PROGRAM_NAME = "program_name";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtils.class);

    private static final List<String> CONFIG_FILE_NAMES = Arrays.asList(
            ".nascontrol.conf",
            "nascontrol.conf",
            "nascontrol.cfg"
    );

    public static Config loadConfiguration() {
        File userDir = FileUtils.getUserDirectory();
        LOGGER.info("user directory: {}", userDir.toString());

        File configFile = null;
        for (String fileName : CONFIG_FILE_NAMES) {

            String configFileName = userDir + "/" + fileName;

            configFile = new File(configFileName);
            if (configFile.exists()) {
                LOGGER.info("load existing configuration from: {}", configFileName);
                // First come, first served
                break;
            }
        }

        if (configFile == null) {
            throw new RuntimeException("No config file found");
        }

        List<FileLocationStrategy> fileLocationStrategies = Arrays.asList(
                new ProvidedURLLocationStrategy(),
                new HomeDirectoryLocationStrategy(),
                new FileSystemLocationStrategy(),
                new ClasspathLocationStrategy());
        FileLocationStrategy fileLocationStrategy = new CombinedLocationStrategy(fileLocationStrategies);

        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builderDefaults =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setEncoding(StandardCharsets.UTF_8.name())
                                .setLocationStrategy(fileLocationStrategy)
                                .setFile(new File(DEFAULTS_PROPERTIES)));
        FileBasedConfigurationBuilder<FileBasedConfiguration> builderOther =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setEncoding(StandardCharsets.UTF_8.name())
                                .setLocationStrategy(fileLocationStrategy)
                                .setFile(configFile));

        CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
        try {
            compositeConfiguration.addConfiguration(builderDefaults.getConfiguration());
            compositeConfiguration.addConfiguration(builderOther.getConfiguration());
        } catch (ConfigurationException e) {
            LOGGER.error("Apache configuration failed", e);
        }

        return mapConfigItems(compositeConfiguration, new Config());
    }

    private static Config mapConfigItems(Configuration apacheConfig, Config config) {
        config.setBroadcastAddress(getValueFromApacheConfig(apacheConfig, KEY_BROADCAST_ADDRESS));
        config.setMacAddress(getValueFromApacheConfig(apacheConfig, KEY_NAS_MAC_ADDRESS));
        config.setNasAdminUI(getValueFromApacheConfig(apacheConfig, KEY_NAS_ADMINUI_URL));
        config.setNasUserId(getValueFromApacheConfig(apacheConfig, KEY_NAS_USER_ID));
        config.setNasUserPassword(getValueFromApacheConfig(apacheConfig, KEY_NAS_USER_PASSWORD));
        config.setVersion(getValueFromApacheConfig(apacheConfig, KEY_VERSION));
        config.setProgramName(getValueFromApacheConfig(apacheConfig, KEY_PROGRAM_NAME));
        return config;
    }

    private static String getValueFromApacheConfig(Configuration apacheConfig, String key) {
        String value = apacheConfig.getString(key);
        LOGGER.info("mapped key={} value={}", key, value);
        return value;
    }
}
