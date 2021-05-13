package ch.m1m.nas.app;

import ch.m1m.nas.driver.DriverFreeNAS;
import ch.m1m.nas.driver.api.Driver;
import ch.m1m.nas.lib.Config;
import ch.m1m.nas.lib.PlatformFactory;
import ch.m1m.nas.lib.WakeOnLanDatagramPacketFactory;
import ch.m1m.nas.platform.api.Platform;

import dorkbox.util.CacheUtil;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// https://github.com/dorkbox/SystemTray
//
// https://docs.oracle.com/javase/tutorial/uiswing/misc/systemtray.html

public class TrayIconUI {

    private static final Logger LOG = LoggerFactory.getLogger(TrayIconUI.class);
    private static ImageIcon appIcon;

    private final Config config;
    private final DriverFreeNAS nasDriver;
    private final Platform platform;

    private Driver.NasStatus lastNasStatus;
    private int queryIntervalSeconds = 10;
    private boolean isDarkMode;
    private dorkbox.systemTray.SystemTray systemTray;

    public TrayIconUI(Config config) {
        platform = PlatformFactory.getInstance();
        isDarkMode = platform.isTrayIconModeDark();

        try {
            // set application icon
            //
            final BufferedImage image = ImageIO.read(createInputStreamForImageResource("images/nascontrol_icon.png"));
            appIcon = new ImageIcon(image);
            platform.setApplicationIcon(appIcon);

            // Setup look and feel
            String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();
            LOG.info("system look and feel: {}", systemLookAndFeel);

            // FIXME: not working
            systemLookAndFeel = null;
            if (systemLookAndFeel == null) {
                systemLookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
                LOG.info("changed look and feel to: {}", systemLookAndFeel);
            }

            UIManager.setLookAndFeel(systemLookAndFeel);
            // Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);

        } catch (Exception e) {
            LOG.error("Something went wrong", e);
            System.exit(1);
        }

        this.config = config;
        lastNasStatus = Driver.NasStatus.UNKNOWN;
        nasDriver = new DriverFreeNAS(config);

        String iconName = getTrayIconNameFromStatus(lastNasStatus, isDarkMode);
        createTrayIconMenu(iconName);
    }

    public static ImageIcon getAppIcon() {

        return appIcon;
    }

    public void executeStatusLoop() {

        while (true) {

            boolean forceCreateIcon = false;

            // let the driver detect the actual version
            nasDriver.getVersion();

            Driver.NasStatus nasStatus = nasDriver.getStatus();
            if (nasStatus == Driver.NasStatus.SUCCESS) {
                queryIntervalSeconds = 30;
            }

            Driver.NasStatus forcedNasStatus = config.getNasForcedStatus();
            if (forcedNasStatus != Driver.NasStatus.UNKNOWN) {
                LOG.warn("forced nas status is set by config overwriting actual status {} with config status {}",
                        nasStatus, forcedNasStatus);
                nasStatus = forcedNasStatus;
            }

            // update the tray icon only when needed to prevent flickering
            //
            if (lastNasStatus != nasStatus) {
                lastNasStatus = nasStatus;
                forceCreateIcon = true;
            }

            // update dark mode flag if changed
            //
            boolean isDarkModeNow = platform.isTrayIconModeDark();
            if (isDarkModeNow != isDarkMode) {
                isDarkMode = isDarkModeNow;
                forceCreateIcon = true;
            }

            String iconName = getTrayIconNameFromStatus(nasStatus, isDarkMode);
            if (forceCreateIcon) {
                javafx.application.Platform.runLater(() -> {
                    updateTrayIcon(iconName);
                });
            }

            try {
                TimeUnit.SECONDS.sleep(queryIntervalSeconds);
            } catch (InterruptedException e) {
                LOG.error("sleep() interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void updateTrayIcon(String systemTrayIconName) {
        LOG.info("update icon with image from {}", systemTrayIconName);
        systemTray.setImage(createInputStreamForImageResource(systemTrayIconName));
    }

    private void createTrayIconMenu(String systemTrayIconName) {
        createTrayIconMenuLinux(systemTrayIconName);
    }

    private void createTrayIconMenuLinux(String systemTrayIconName) {
        final String progName = "NasControl";
        LOG.info("create icon with image from {}", systemTrayIconName);

        // for test apps, we always want to run in debug mode
        dorkbox.systemTray.SystemTray.DEBUG = true;

        // for test apps, make sure the cache is always reset. These are the ones used, and you should never do this in production.
        CacheUtil.clear(progName);

        //dorkbox.systemTray.SystemTray.FORCE_TRAY_TYPE = dorkbox.systemTray.SystemTray.TrayType.Swing;
        //
        //dorkbox.systemTray.util.
        //
        // SwingUtil.setLookAndFeel(null); // set Native L&F (this is the System L&F instead of CrossPlatform L&F)
        //
        //dorkbox.systemTray.SystemTray.SWING_UI = new dorkbox.systemTray.ui.osx._OsxNativeTray();
        //
        // osx icon is shown but menu is not reacting
        //dorkbox.systemTray.SystemTray.SWING_UI = new dorkbox.systemTray.util.LinuxSwingUI();

        if (SystemUtils.IS_OS_MAC_OSX) {
            dorkbox.systemTray.SystemTray.FORCE_TRAY_TYPE = dorkbox.systemTray.SystemTray.TrayType.Awt;
        }
        systemTray = dorkbox.systemTray.SystemTray.get(progName);
        if (systemTray == null) {
            throw new RuntimeException("Unable to load SystemTray!");
        }

        systemTray.installShutdownHook();
        systemTray.setImage(createInputStreamForImageResource(systemTrayIconName));
        systemTray.setTooltip("NAS checker");
        //systemTray.setStatus("No Mail");

        dorkbox.systemTray.Menu mainMenu = systemTray.getMenu();

        dorkbox.systemTray.MenuItem menuItemSendWoL = new dorkbox.systemTray.MenuItem("Send WoL", e -> {
            //final dorkbox.systemTray.MenuItem entry = (dorkbox.systemTray.MenuItem) e.getSource();
            sendWakeOnLan();
        });
        mainMenu.add(menuItemSendWoL);

        dorkbox.systemTray.MenuItem menuItemOpenWebUI = new dorkbox.systemTray.MenuItem("Open WebUI", e -> {
            openWebUI();
        });
        mainMenu.add(menuItemOpenWebUI);

        mainMenu.add(new dorkbox.systemTray.Separator());

        dorkbox.systemTray.MenuItem menuItemSendShutdown = new dorkbox.systemTray.MenuItem("Send Shutdown", e -> {
            nasDriver.shutdown();
        });
        mainMenu.add(menuItemSendShutdown);

        mainMenu.add(new dorkbox.systemTray.Separator());

        dorkbox.systemTray.MenuItem menuItemAbout = new dorkbox.systemTray.MenuItem("About...", e -> {
            AboutDialog.show();
        });
        mainMenu.add(menuItemAbout);

        mainMenu.add(new dorkbox.systemTray.Separator());

        dorkbox.systemTray.MenuItem menuItemExit = new dorkbox.systemTray.MenuItem("Exit", e -> {
            LOG.info("menu Exit pressed...");
            systemTray.remove();
            System.exit(0);
        });
        mainMenu.add(menuItemExit);
    }

    private void sendWakeOnLan() {
        LOG.info("sendWakeOnLan() called...");
        String macAddress = config.getMacAddress();
        String bcAddress = config.getBroadcastAddress();

        LOG.info("broadcast address: {} mac address: {}", bcAddress, macAddress);

        List<DatagramPacket> packets = Stream.of(7, 9)
                .map(port -> WakeOnLanDatagramPacketFactory.newInstance(
                        macAddress, bcAddress, port))
                .collect(Collectors.toList());

        try (DatagramSocket socket = new DatagramSocket()) {

            for (DatagramPacket packet : packets) {
                LOG.debug("send broadcast datagram packet to port={}", packet.getPort());
                socket.send(packet);
            }

        } catch (IOException e) {
            LOG.error("Failed to send WoL packet ", e);
        }
    }

    private void openWebUI() {
        String urlNasWebUI = config.getNasAdminUI();
        String shellCommand = platform.getShellCommandDisplayURL(urlNasWebUI);

        LOG.info("try to spawn sub process: {}", shellCommand);
        try {
            Runtime.getRuntime().exec(shellCommand);
        } catch (IOException e) {
            LOG.error("Failed to spawn browser", e);
        }
    }

    private Image createImage(String resPath) {
        // osx needs a leading slash e.g: /images
        if (!resPath.startsWith("/")) {
            resPath = "/" + resPath;
        }
        URL imageURL = getClass().getResource(resPath);
        if (imageURL == null) {
            LOG.error("Resource not found: {}", resPath);
            return null;
        } else {
            return (new ImageIcon(imageURL)).getImage();
        }
    }

    private InputStream createInputStreamForImageResource(String resPath) {
        // linux port with stream resource without leading / for images
        return getClass().getClassLoader().getResourceAsStream(resPath);
    }

    private String getTrayIconNameFromStatus(Driver.NasStatus status, boolean isDarkMode) {
        String iconName;
        switch (status) {
            case UNKNOWN:
                iconName = "cloud-computing-gray-512x512.png";
                if (isDarkMode) {
                    iconName = "cloud-computing-gray-512x512.png";
                }
                break;
            case SUCCESS:
                iconName = "cloud-computing-black-success-512x512.png";
                if (isDarkMode) {
                    iconName = "cloud-computing-white-success-512x512.png";
                }
                break;
            default:
                iconName = "cloud-computing-black-error-512x512.png";
                if (isDarkMode) {
                    iconName = "cloud-computing-white-error-512x512.png";
                }
        }
        // linux port with stream resource without leading / for images
        // osx needs the /images
        return "images/" + iconName;
    }
}
