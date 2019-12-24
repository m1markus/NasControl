package ch.m1m.nas;

import javax.swing.*;

public interface Platform {

    boolean isTrayIconModeDark();

    void setApplicationIcon(ImageIcon icon);
}
