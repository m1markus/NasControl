package ch.m1m.nas;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class PlatformGeneric implements Platform {

    private static Logger log = LoggerFactory.getLogger(PlatformGeneric.class);
    private static Platform platformGeneric;
    private static Platform platformSpecific;

    private PlatformGeneric() {
        log.info("running on osName={}", SystemUtils.OS_NAME);
        initPlatformModule();
    }

    public static Platform getInstance() {
        if (platformGeneric == null) {
            platformGeneric = new PlatformGeneric();
        }
        return platformGeneric;
    }

    private static void initPlatformModule() {
        if (SystemUtils.IS_OS_MAC_OSX) {
            platformSpecific = new PlatformOSX();
        } else {
            log.error("platform {} not yet implemented", SystemUtils.OS_NAME);
        }
    }

    @Override
    public boolean isTrayIconModeDark() {
        boolean isDark = false;
        if (platformSpecific != null) {
            isDark = platformSpecific.isTrayIconModeDark();
        }
        log.info("isTrayIconModeDark() returns value: {}", isDark);
        return isDark;
    }

    public void setApplicationIcon(ImageIcon icon) {
        if (platformSpecific != null) {
            platformSpecific.setApplicationIcon(icon);
        }
    }
}
