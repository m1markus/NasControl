package ch.m1m.nas;

public class Config {

    private String macAddress;

    private String broadcastAddress;

    private String nasAdminUI;

    private String nasUserId;

    private String nasUserPassword;

    private String version;

    private String programName;

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

    public String getNasUserId() {
        return nasUserId;
    }

    public void setNasUserId(String nasUserId) {
        this.nasUserId = nasUserId;
    }

    public String getNasUserPassword() {
        return nasUserPassword;
    }

    public void setNasUserPassword(String nasUserPassword) {
        this.nasUserPassword = nasUserPassword;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }
}
