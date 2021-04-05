package ch.m1m.nas.platform;

import ch.m1m.nas.platform.api.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

// https://github.com/dustinkredmond/FXTrayIcon
// http://www-inf.int-evry.fr/cours/java/javatutorial/uiswing/misc/systemtray.html
// https://stackoverflow.com/questions/60837862/detect-dark-windows-taskbar

public class PlatformWindows implements Platform {

    private static final Logger LOG = LoggerFactory.getLogger(PlatformWindows.class);

    public PlatformWindows() {
        String regTreeKey = "HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion";
        String productName = WindowsReqistry.readRegistry(regTreeKey, "ProductName");
        String displayVersion = WindowsReqistry.readRegistry(regTreeKey, "DisplayVersion");
        LOG.info("create instance PlatformWindows: {} {}", productName, displayVersion);
    }

    @Override
    public boolean isTrayIconModeDark() {
        boolean isDarkMode = false;
        String regTreeKey = "HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize";

        // FIXME: This is not the fastest method to query the windows registry
        //
        String valueThemeLight = WindowsReqistry.readRegistry(regTreeKey, "SystemUsesLightTheme");
        if (valueThemeLight != null) {
            // if DarkTheme is activated the output is: SystemUsesLightTheme 0x0
            //
            valueThemeLight = valueThemeLight.toLowerCase();
            if ("0x0".equals(valueThemeLight)) {
                isDarkMode = true;
            }
        }
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
        return String.format("start %s", url);
    }
}
