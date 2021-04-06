package ch.m1m.nas.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;


public class AboutDialog {

    private static final Logger LOG = LoggerFactory.getLogger(AboutDialog.class);

    private static JPanel panelContainer;

    // https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html

    private AboutDialog() {
    }

    public static void show() {

        String programName = "NASControl";

        LOG.debug("called show()");

        // place the icon
        //
        JLabel labelIcon = new JLabel(TrayIconUI.getAppIcon());
        JPanel panelIcon = new JPanel(new GridBagLayout());
        panelIcon.add(labelIcon);

        // program name
        //
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panelProgram = new JPanel(new GridBagLayout());
        gbc.insets = new Insets(5, 0, 0, 0);
        panelProgram.add(new Label(programName), gbc);

        // place text
        //
        JPanel textPanel = new JPanel(new GridBagLayout());

        // Version info
        //
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 0, 0);
        textPanel.add(new Label("Version:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 0, 0);
        textPanel.add(new Label(Version.getProjectVersion()), gbc);

        // License info
        //
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        textPanel.add(new Label("License:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 0, 0);
        textPanel.add(new Label("LGPL"), gbc);

        // Credit info
        //
        // https://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel
        //
        String credit1 = "<html>Icons made by <a href=\"https://www.flaticon.com/authors/itim2101\" title=\"itim2101\">itim2101</a></html>";
        String credit2 = "<html>from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></html>";
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        textPanel.add(new JLabel(credit1), gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        textPanel.add(new JLabel(credit2), gbc);

        // construct dialog panel
        //
        if (panelContainer == null) {
            panelContainer = new JPanel(new BorderLayout());
            panelContainer.add(panelIcon, BorderLayout.NORTH);
            panelContainer.add(panelProgram, BorderLayout.CENTER);
            panelContainer.add(textPanel, BorderLayout.SOUTH);
        }

        // show dialog
        //
        LOG.debug("AboutDialog isShowing() {}", panelContainer.isShowing());
        if (!panelContainer.isShowing()) {
            String dialogTitle = String.format("%s About", programName);
            JOptionPane.showMessageDialog(null, panelContainer, dialogTitle, JOptionPane.DEFAULT_OPTION);
        }
    }
}
