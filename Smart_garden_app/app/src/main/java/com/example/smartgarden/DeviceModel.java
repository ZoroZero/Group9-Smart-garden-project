package app.advance.hcmut.cse.smartgardensystem;

public class DeviceModel {
    private String deviceId = "";
    private String deviceName = "";
    private String deviceType = "";
    private String userId = "";
    private String path = "";

    public String getDeviceId() {return deviceId; }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceTitle) {
        this.deviceName = deviceTitle;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getUserId() {return userId; }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPath(String path) { this.path = path; }

    public String getPath() {return path; }
}