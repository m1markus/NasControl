package ch.m1m.nas;

import javax.swing.*;

public interface Platform {

    public boolean isTrayIconModeDark();

    public void setApplicationIcon(ImageIcon icon);
}
