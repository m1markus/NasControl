package ch.m1m.nas;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NASControl {

    private static final String PROGRAM_NAME = "NASControl";
    private static final String OPT_HELP = "help";
    private static final String OPT_VERSION = "version";

    private static Logger log = LoggerFactory.getLogger(NASControl.class);

    private static Config config;
    private static DriverFreeNAS nasDriver;
    private static DriverInterface.NasStatus lastNasStatus;
    private static Platform platform;

    private static int queryIntervalSeconds = 10;
    private static boolean isDarkMode;


    public static void main(String... args) {

        CommandLine clArgs = setupAndParseArgs(args);

        List<String> liArgs = Arrays.asList(args);
        log.info("start {} {} with args: {}", PROGRAM_NAME, Version.getProjectVersion(), liArgs);

        config = ConfigUtils.loadConfiguration();
        nasDriver = new DriverFreeNAS(config);

        try {
            // set application icon
            //
            InputStream stream = nasDriver.getClass().getResourceAsStream("/images/nascontrol_icon.png");
            ImageIcon applIcon = new ImageIcon(ImageIO.read(stream));
            platform = PlatformGeneric.getInstance();
            platform.setApplicationIcon(applIcon);

            String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();
            log.info("system look and feel: {}", systemLookAndFeel);

            if (systemLookAndFeel == null) {
                systemLookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
                log.info("changed look and feel to: {}", systemLookAndFeel);
            }
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel(systemLookAndFeel);
            // Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);

        } catch (Exception e) {
            log.error("Something went wrong", e);
            System.exit(1);
        }

        isDarkMode = platform.isTrayIconModeDark();

        lastNasStatus = DriverInterface.NasStatus.UNKNOWN;
        String iconName = getTrayIconNameFromStatus(lastNasStatus, isDarkMode);

        createTrayIconMenue(iconName);

        executeStatusLoop(config);
    }

    public static void executeStatusLoop(Config config) {

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
                createTrayIconMenue(iconName);
            }

            try {
                TimeUnit.SECONDS.sleep(queryIntervalSeconds);
            } catch (InterruptedException e) {
                log.error("sleep() interruped", e);
            }
        }
    }

    public static CommandLine setupAndParseArgs(String... args) {

        // https://commons.apache.org/proper/commons-cli/usage.html
        //
        Options options = new Options();
        options.addOption("h", OPT_HELP, false, "print usage");
        options.addOption("v", OPT_VERSION, false, "print version information");

        CommandLineParser parser = new DefaultParser();
        CommandLine clArgs = null;

        try {
            clArgs = parser.parse(options, args);

            if (clArgs.hasOption(OPT_HELP)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(PROGRAM_NAME + " [options]", options);
                System.exit(0);
            }

            if (clArgs.hasOption(OPT_VERSION)) {
                System.out.println(PROGRAM_NAME + " version " + Version.getProjectVersion());
                System.exit(0);
            }

        } catch (ParseException e) {
            log.error("parsing command line: {}", e.getMessage());
            System.exit(1);
        }

        return clArgs;
    }

    private static void createTrayIconMenue(String systemTrayIconName) {
        if (!SystemTray.isSupported()) {
            log.error("SystemTray is not supported");
            System.exit(1);
        }
        final PopupMenu popup = new PopupMenu();

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
            log.debug("actual {} old icons in the systemTray", numOldIcons);
            for (int ii = 0; ii < numOldIcons; ii++) {
                if (oldIcons[ii] != null) {
                    tray.remove(oldIcons[ii]);
                }
            }

            // set the new icon
            //
            tray.add(trayIcon);

        } catch (AWTException e) {
            log.error("TrayIcon could not be added");
            System.exit(1);
        }

        // Create a popup menu components
        //
        MenuItem sendWOL = new MenuItem("Send WoL");
        MenuItem sendShutdown = new MenuItem("Send Shutdown");
        MenuItem openWebUI = new MenuItem("Open WebUI");

        MenuItem exitItem = new MenuItem("Exit");

        //sendWOL.setEnabled(false);

        popup.add(sendWOL);
        popup.add(openWebUI);
        popup.addSeparator();
        popup.add(sendShutdown);

        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "This dialog box is run from System Tray");
            }
        });

        sendWOL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendWakeOnLan();
            }
        });

        openWebUI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openWebUI();
            }
        });

        sendShutdown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nasDriver.shutdown();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
    }

    private static void sendWakeOnLan() {
        // FIXME: make config value for MAC and Broadcast address
        String strMacAddress = config.getMacAddress();
        String strBroadcastAddress = config.getBroadcastAddress();

        log.info("sending WoL packet for MAC {} to {}", strMacAddress, strBroadcastAddress);

        try {
            WakeOnLan.send(strMacAddress, strBroadcastAddress, 7);
            WakeOnLan.send(strMacAddress, strBroadcastAddress, 9);

        } catch (IOException e) {
            log.error("Failed to send WoL packet ", e);
        }
    }

    private static void openWebUI() {
        String urlNasWebUI = config.getNasAdminUI();
        String shellCommand = String.format("open %s", urlNasWebUI);

        log.info("try to spawn subprocess: {}", shellCommand);
        try {
            Runtime.getRuntime().exec(shellCommand);
        } catch (IOException e) {
            log.error("Failed to spawn browser", e);
        }
    }

    private static Image createImage(String path, String description) {
        URL imageURL = NASControl.class.getResource(path);
        if (imageURL == null) {
            log.error("Resource not found: {}", path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    private static String getTrayIconNameFromStatus(DriverInterface.NasStatus status, boolean isDarkMode) {
        String iconName = null;
        switch (status) {
            case UNKNOWN:
                iconName = "cloud-computing-gray-512x512.png";
                if (isDarkMode) iconName = "cloud-computing-gray-512x512.png";
                break;
            case SUCCESS:
                iconName = "cloud-computing-black-success-512x512.png";
                if (isDarkMode) iconName = "cloud-computing-white-success-512x512.png";
                break;
            default:
                iconName = "cloud-computing-black-error-512x512.png";
                if (isDarkMode) iconName = "cloud-computing-white-error-512x512.png";
        }
        return iconName;
    }
}
