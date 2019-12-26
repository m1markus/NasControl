package ch.m1m.nas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AboutDialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(AboutDialog.class);

    private static Dialog dialog;

    private AboutDialog() {

    }
/*
    public static void show() {
        try {
            BufferedImage image = ImageIO.read(ImagePanel.class.getClassLoader().getResource("images/nascontrol_icon.png"));
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ImagePanel(image));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }
*/
    public static void show() {
        LOGGER.debug("called show()");

        Frame f = new Frame();
        dialog = new Dialog(f, "NASControl About", true);

        // https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html

        dialog.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // place icon here
        // ...
        //
        //d.add( ... icon );

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        dialog.add(new Label("Version:"), c);
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0, 20, 0, 0);
        dialog.add(new Label(Version.getProjectVersion()), c);

        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        dialog.add(new Label("License:"), c);
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(0, 20, 0, 0);
        dialog.add(new Label("LGPL"), c);

        Button b = new Button("OK");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AboutDialog.dialog.setVisible(false);
            }
        });
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.insets = new Insets(20, 0, 20, 0);
        c.anchor = GridBagConstraints.CENTER;
        dialog.add(b, c);

        //new IconAdapter(icon);

        //d.add(TrayIconUI.getAppIcon());

        String creditIcons1 = "Icons made by itim2101";
        String creditIcons2 = "from www.flaticon.com";

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 0, 0);
        dialog.add(new Label(creditIcons1), c);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 0, 0);
        dialog.add(new Label(creditIcons2), c);

        c.gridy = 5;
        //dialog.add(TrayIconUI.getAppIcon().getImage(), c);

        dialog.setSize(250, 250);
        dialog.setVisible(true);
    }
}
