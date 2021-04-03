package ch.m1m.nas.lib;

import ch.m1m.nas.platform.api.Platform;
import ch.m1m.nas.platform.*;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformFactory {

    private static final Logger log = LoggerFactory.getLogger(PlatformFactory.class);
    private static Platform platform;

    private PlatformFactory() {

        initPlatformModule();
    }

    public static Platform getInstance() {
        if (platform == null) {
            initPlatformModule();
        }
        return platform;
    }

    private static void initPlatformModule() {
        log.info("running on osName={}", SystemUtils.OS_NAME);
        if (SystemUtils.IS_OS_MAC_OSX) {
            platform = new PlatformOSX();

        } else if (SystemUtils.IS_OS_WINDOWS) {
            platform = new PlatformWindows();
            
        } else {
            log.error("platform {} not yet implemented", SystemUtils.OS_NAME);
        }
    }
}
