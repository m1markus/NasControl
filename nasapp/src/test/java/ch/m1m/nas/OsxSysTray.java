package ch.m1m.nas;

import java.io.IOException;
import java.net.URL;

import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.Separator;
import dorkbox.systemTray.SystemTray;
import dorkbox.util.CacheUtil;
import dorkbox.util.Desktop;

/**
 * Icons from 'SJJB Icons', public domain/CC0 icon set
 */
public class OsxSysTray {

    public static URL IMG_CLOUD_GRAY;

    public static void main(String[] args) {
        IMG_CLOUD_GRAY = XTestTray.class.getResource("/images/cloud-computing-gray-512x512.png");
        new OsxSysTray();
    }

    private SystemTray systemTray = null;

    public OsxSysTray() {

        SystemTray.DEBUG = true; // for test apps, we always want to run in debug mode
        // SystemTray.FORCE_TRAY_TYPE = SystemTray.TrayType.Swing;
        // for test apps, make sure the cache is always reset. These are the ones used, and you should never do this in production.
        CacheUtil.clear("SysTrayExample");

        // SwingUtil.setLookAndFeel(null); // set Native L&F (this is the System L&F instead of CrossPlatform L&F)
        // SystemTray.SWING_UI = new CustomSwingUI();


        SystemTray.FORCE_TRAY_TYPE = SystemTray.TrayType.Awt;
        this.systemTray = SystemTray.get("SysTrayExample");
        if (systemTray == null) {
            throw new RuntimeException("Unable to load SystemTray!");
        }

        systemTray.installShutdownHook();
        systemTray.setTooltip("Mail Checker");
        systemTray.setImage(IMG_CLOUD_GRAY);

        Menu mainMenu = systemTray.getMenu();

        mainMenu.add(new MenuItem("About", e -> {
            try {
                Desktop.browseURL("https://git.dorkbox.com/dorkbox/SystemTray");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }));

        mainMenu.add(new Separator());

        systemTray.getMenu().add(new MenuItem("Quit", e -> {
            systemTray.shutdown();
            //System.exit(0);  not necessary if all non-daemon threads have stopped.
        })).setShortcut('q'); // case does not matter
    }
}
