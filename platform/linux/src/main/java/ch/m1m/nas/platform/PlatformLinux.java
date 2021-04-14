package ch.m1m.nas.platform;

import ch.m1m.nas.platform.api.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

// https://awesomeopensource.com/project/dorkbox/SystemTray

public class PlatformLinux implements Platform {

    private static final Logger LOG = LoggerFactory.getLogger(PlatformLinux.class);

    public PlatformLinux() {

        LOG.info("create instance PlatformLinux: {} {}", "", "");
    }

    @Override
    public boolean isTrayIconModeDark() {
        boolean isDarkMode = true;
        LOG.info("isTrayIconModeDark() returns {}", isDarkMode);
        return isDarkMode;
    }

    @Override
    public void setApplicationIcon(ImageIcon icon) {
        //Taskbar taskbar = Taskbar.getTaskbar();
        //taskbar.setIconImage(icon.getImage());
    }

    @Override
    public String getShellCommandDisplayURL(String url) {

        return String.format("xdg-open %s", url);
    }
}

