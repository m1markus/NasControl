package ch.m1m.nas;

public class Config {

    private String macAddress;

    private String broadcastAddress;

    private String nasAdminUI;


    public void init() {
        macAddress = "70:85:C2:DD:18:6F";
        broadcastAddress = "192.168.1.255";
        nasAdminUI = "http://freenas.local";
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getBroadcastAddress() {
        return broadcastAddress;
    }

    public void setBroadcastAddress(String broadcastAddress) {
        this.broadcastAddress = broadcastAddress;
    }

    public String getNasAdminUI() {
        return nasAdminUI;
    }

    public void setNasAdminUI(String nasAdminUI) {
        this.nasAdminUI = nasAdminUI;
    }
}
