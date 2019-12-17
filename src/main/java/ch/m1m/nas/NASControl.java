package ch.m1m.nas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class NASControl {

    private static Logger log = LoggerFactory.getLogger(NASControl.class);

    private static Config config;

    public static void main(String... args) {

        List<String> liArgs = Arrays.asList(args);
        log.info("start NASControl with args: {}", liArgs);

        config = ConfigUtils.loadConfiguration();

        try {
            String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();
            log.info("system look and feel: {}", systemLookAndFeel);

            if (systemLookAndFeel == null) {
                systemLookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
                log.info("changed look and feel to: {}", systemLookAndFeel);
            }
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel(systemLookAndFeel);

        } catch (Exception e) {
            log.error("Something went wrong", e);
            System.exit(1);
        }

        // Turn off metal's use of bold fonts
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        createAndShowGUI();
    }

    private static void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            log.error("SystemTray is not supported");
            System.exit(1);
        }
        final PopupMenu popup = new PopupMenu();

        // set icons
        //
        String imagePath = "/images/cloud-computing-gray-512x512.png";
        //String imagePath = "/images/cloud-computing-white-512x512.png";
        //String imagePath = "/images/cloud-computing-white-error-512x512.png";
        //String imagePath = "/images/cloud-computing-black-512x512.png";
        //String imagePath = "/images/cloud-computing-black-error-512x512.png";

        final TrayIcon trayIcon = new TrayIcon(createImage(imagePath, "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        //
        MenuItem sendWOL = new MenuItem("Send WoL");
        MenuItem openWebUI = new MenuItem("Open WebUI");
        MenuItem exitItem = new MenuItem("Exit");

        //sendWOL.setEnabled(false);

        popup.add(sendWOL);
        popup.add(openWebUI);

        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            log.error("TrayIcon could not be added");
            System.exit(1);
        }

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
}
