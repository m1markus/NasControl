package ch.m1m.nas;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import dorkbox.os.OS;
import dorkbox.systemTray.Checkbox;
import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.Separator;
import dorkbox.systemTray.SystemTray;
import dorkbox.util.CacheUtil;
import dorkbox.util.Desktop;

/**
 * Icons from 'SJJB Icons', public domain/CC0 icon set
 */
public
class XTestTray {

    /*
    public static final URL BLUE_CAMPING = XTestTray.class.getResource("accommodation_camping.glow.0092DA.32.png");
    public static final URL BLACK_FIRE = XTestTray.class.getResource("amenity_firestation.p.000000.32.png");

    public static final URL BLACK_MAIL = XTestTray.class.getResource("amenity_post_box.p.000000.32.png");
    public static final URL GREEN_MAIL = XTestTray.class.getResource("amenity_post_box.p.39AC39.32.png");

    public static final URL BLACK_BUS = XTestTray.class.getResource("transport_bus_station.p.000000.32.png");
    public static final URL LT_GRAY_BUS = XTestTray.class.getResource("transport_bus_station.p.999999.32.png");

    public static final URL BLACK_TRAIN = XTestTray.class.getResource("transport_train_station.p.000000.32.png");
    public static final URL GREEN_TRAIN = XTestTray.class.getResource("transport_train_station.p.39AC39.32.png");
    //public static final URL LT_GRAY_TRAIN = XTestTray.class.getResource("transport_train_station.p.666666.32.png");
    */

    public static URL IMG_CLOUD_GRAY;

    // from issue 123
    //public static final URL NOTIFY_IMAGE = XTestTray.class.getResource("RemoteNotifications.png");

    public static void main(String[] args) {
        // make sure JNA jar is on the classpath!
        IMG_CLOUD_GRAY = XTestTray.class.getResource("/images/cloud-computing-gray-512x512.png");
        new XTestTray();
    }

    private SystemTray systemTray = null;
    private ActionListener callbackGray = null;

    public XTestTray() {

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
        //systemTray.setStatus("No Mail");

        /*
        callbackGray = e -> {
            final MenuItem entry = (MenuItem) e.getSource();
            systemTray.setStatus(null)
                    .setImage(IMG_CLOUD_GRAY);

            entry.setCallback(null);
//                systemTray.setStatus("Mail Empty");
            systemTray.getMenu().remove(entry);
            entry.remove();
            System.err.println("POW");
        }; */

        Menu mainMenu = systemTray.getMenu();

        /*
        MenuItem greenEntry = new MenuItem("Green Mail", e -> {
            final MenuItem entry = (MenuItem) e.getSource();
            systemTray.setStatus("Some Mail!");
            systemTray.setImage(IMG_CLOUD_GRAY);

            entry.setCallback(callbackGray);
            entry.setImage(IMG_CLOUD_GRAY);
            entry.setText("Delete Mail");
            entry.setTooltip(null); // remove the tooltip
//                systemTray.remove(menuEntry);
        });
        greenEntry.setImage(IMG_CLOUD_GRAY);
        // case does not matter
        greenEntry.setShortcut('G');
        greenEntry.setTooltip("This means you have green mail!");
        mainMenu.add(greenEntry);


        Checkbox checkbox = new Checkbox("Euro € Mail", e -> System.err.println("Am i checked? " + ((Checkbox) e.getSource()).getChecked()));
        checkbox.setShortcut('€');
        mainMenu.add(checkbox);

        mainMenu.add(new Separator());
        */

        mainMenu.add(new MenuItem("About", e -> {
            try {
                Desktop.browseURL("https://git.dorkbox.com/dorkbox/SystemTray");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }));
/*
        mainMenu.add(new MenuItem("Temp Directory", e -> {
            try {
                Desktop.browseDirectory(OS.TEMP_DIR.getAbsolutePath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }));
/*
        mainMenu.add(new MenuItem("Notify", e -> {
            final MenuItem entry = (MenuItem) e.getSource();
            systemTray.setStatus("Notification!");
            systemTray.setImage(IMG_CLOUD_GRAY);

            entry.setImage(IMG_CLOUD_GRAY);
            entry.setText("Did notify");
            System.err.println("NOTIFICATION!");
        }));

        Menu submenu = new Menu("Options", IMG_CLOUD_GRAY);
        submenu.setShortcut('t');

        MenuItem disableMenu = new MenuItem("Disable menu", IMG_CLOUD_GRAY, e -> {
            MenuItem source = (MenuItem) e.getSource();
            source.getParent().setEnabled(false);
        });
        submenu.add(disableMenu);

        submenu.add(new MenuItem("Hide tray", IMG_CLOUD_GRAY, e -> systemTray.setEnabled(false)));
        submenu.add(new MenuItem("Remove menu", IMG_CLOUD_GRAY, e -> {
            MenuItem source = (MenuItem) e.getSource();
            source.getParent().remove();
        }));

        submenu.add(new MenuItem("Add new entry to tray",
                e -> systemTray.getMenu().add(new MenuItem("Random " + new Random().nextInt(10)))));
        mainMenu.add(submenu);

        MenuItem entry = new MenuItem("Type: " + systemTray.getType().toString());
        entry.setEnabled(false);
        systemTray.getMenu().add(entry);

 */

        systemTray.getMenu().add(new MenuItem("Quit", e -> {
            systemTray.shutdown();
            //System.exit(0);  not necessary if all non-daemon threads have stopped.
        })).setShortcut('q'); // case does not matter
    }
}
