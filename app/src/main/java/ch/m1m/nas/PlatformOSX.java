package ch.m1m.nas;

import com.apple.eawt.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class PlatformOSX implements Platform {

    private static final Logger log = LoggerFactory.getLogger(PlatformOSX.class);

    public PlatformOSX() {
        log.info("create instance PlatformOSX");
    }

    /* run: defaults read -g AppleInterfaceStyle
     *
     */
    @Override
    public boolean isTrayIconModeDark() {
        try {
            // check for exit status only. Once there are more modes than "Dark" and "default", we might need to analyze string contents..
            final Process proc = Runtime.getRuntime().exec(new String[]{"defaults", "read", "-g", "AppleInterfaceStyle"});
            proc.waitFor(100, TimeUnit.MILLISECONDS);
            return proc.exitValue() == 0;
        } catch (Exception e) {
            // IllegalThreadStateException thrown by proc.exitValue(), if process didn't terminate
            log.warn("falling back to default (light) mode");
            return false;
        }
    }

    @Override
    public void setApplicationIcon(ImageIcon icon) {
        Application.getApplication().setDockIconImage(icon.getImage());
    }
}
