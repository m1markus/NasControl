package ch.m1m.nas.lib;

import ch.m1m.nas.platform.api.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

// https://github.com/dustinkredmond/FXTrayIcon

// http://www-inf.int-evry.fr/cours/java/javatutorial/uiswing/misc/systemtray.html

public class PlatformWindows implements Platform {

    private static final Logger log = LoggerFactory.getLogger(PlatformWindows.class);

    public PlatformWindows() {
        log.info("create instance PlatformWindows");
    }

    @Override
    public boolean isTrayIconModeDark() {
        return false;
    }

    @Override
    public void setApplicationIcon(ImageIcon icon) {
        //Taskbar taskbar = Taskbar.getTaskbar();
        //taskbar.setIconImage(icon.getImage());
    }
}
