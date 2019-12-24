package ch.m1m.nas;

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

public class TrayIconUI {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrayIconUI.class);

    private final Config config;

    private final DriverFreeNAS nasDriver;

    private final Platform platform;

    private DriverInterface.NasStatus lastNasStatus = DriverInterface.NasStatus.UNKNOWN;

    private int queryIntervalSeconds = 10;

    private boolean isDarkMode;

    public TrayIconUI(Config config, DriverFreeNAS nasDriver) {
        platform = PlatformGeneric.getInstance();
        isDarkMode = platform.isTrayIconModeDark();

        try {

            // set application icon
            //
            final InputStream stream = getClass().getClassLoader().getResourceAsStream("images/nascontrol_icon.png");
            final BufferedImage image = ImageIO.read(stream);
            ImageIcon appIcon = new ImageIcon(image);
            platform.setApplicationIcon(appIcon);

            // Setup look and feel
            String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();
            LOGGER.info("system look and feel: {}", systemLookAndFeel);

            if (systemLookAndFeel == null) {
                systemLookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
                LOGGER.info("changed look and feel to: {}", systemLookAndFeel);
            }

            UIManager.setLookAndFeel(systemLookAndFeel);
            // Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);

        } catch (Exception e) {
            LOGGER.error("Something went wrong", e);
            System.exit(1);
        }

        this.config = config;
        this.nasDriver = nasDriver;

        String iconName = getTrayIconNameFromStatus(lastNasStatus, isDarkMode);

        createTrayIconMenu(iconName);
    }

    public void executeStatusLoop() {

        while (true) {

            boolean forceCreateIcon = false;

            String nasVersion = nasDriver.getVersion();

            DriverInterface.NasStatus nasStatus = nasDriver.getStatus();
            if (nasStatus == DriverInterface.NasStatus.SUCCESS) {
                queryIntervalSeconds = 30;
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
                createTrayIconMenu(iconName);
            }

            try {
                TimeUnit.SECONDS.sleep(queryIntervalSeconds);
            } catch (InterruptedException e) {
                LOGGER.error("sleep() interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void createTrayIconMenu(String systemTrayIconName) {
        if (!SystemTray.isSupported()) {
            LOGGER.error("SystemTray is not supported");
            System.exit(1);
        }

        // set icons
        //
        String imagePath = "/images/" + systemTrayIconName;

        final TrayIcon trayIcon = new TrayIcon(createImage(imagePath, "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        try {
            // remove all old icons
            //
            TrayIcon[] oldIcons = tray.getTrayIcons();
            int numOldIcons = oldIcons.length;
            LOGGER.debug("actual {} old icons in the systemTray", numOldIcons);
            for (int ii = 0; ii < numOldIcons; ii++) {
                if (oldIcons[ii] != null) {
                    tray.remove(oldIcons[ii]);
                }
            }

            // set the new icon
            //
            tray.add(trayIcon);

        } catch (AWTException e) {
            LOGGER.error("TrayIcon could not be added");
            System.exit(1);
        }

        // Create a popup menu components
        //
        MenuItem sendWOL = new MenuItem("Send WoL");
        sendWOL.addActionListener(e -> sendWakeOnLan());

        MenuItem sendShutdown = new MenuItem("Send Shutdown");
        sendShutdown.addActionListener(e -> nasDriver.shutdown());

        MenuItem openWebUI = new MenuItem("Open WebUI");
        openWebUI.addActionListener(e -> openWebUI());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            tray.remove(trayIcon);
            System.exit(0);
        });

        final PopupMenu popup = new PopupMenu();
        popup.add(sendWOL);
        popup.add(openWebUI);
        popup.addSeparator();
        popup.add(sendShutdown);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        trayIcon.addActionListener(e -> JOptionPane.showMessageDialog(null,
                "This dialog box is run from System Tray"));
    }

    private void sendWakeOnLan() {
        List<DatagramPacket> packets = Stream.of(7, 9)
                .map(port -> WakeOnLanDatagramPacketFactory.newInstance(
                        config.getMacAddress(), config.getBroadcastAddress(), port))
                .collect(Collectors.toList());

        try (DatagramSocket socket = new DatagramSocket()) {

            for (DatagramPacket packet : packets) {
                LOGGER.debug("send broadcast datagram packet to port={}", packet.getPort());
                socket.send(packet);
            }

        } catch (IOException e) {
            LOGGER.error("Failed to send WoL packet ", e);
        }
    }

    private void openWebUI() {
        String urlNasWebUI = config.getNasAdminUI();
        String shellCommand = String.format("open %s", urlNasWebUI);

        LOGGER.info("try to spawn sub process: {}", shellCommand);
        try {
            Runtime.getRuntime().exec(shellCommand);
        } catch (IOException e) {
            LOGGER.error("Failed to spawn browser", e);
        }
    }

    private Image createImage(String path, String description) {
        URL imageURL = getClass().getResource(path);
        if (imageURL == null) {
            LOGGER.error("Resource not found: {}", path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    private String getTrayIconNameFromStatus(DriverInterface.NasStatus status, boolean isDarkMode) {
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
        return iconName;
    }
}
