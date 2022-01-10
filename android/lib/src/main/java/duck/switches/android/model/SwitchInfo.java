package duck.switches.android.model;

public class SwitchInfo {

    private final String address;
    private final String deviceId;
    private final String ssid;
    private final String fwVersion;
    private final SwitchState switchState;
    private final PowerOnState powerOnSate;

    public SwitchInfo(String address, String deviceId, String ssid, String fwVersion,
                      SwitchState switchState, PowerOnState powerOnSate) {
        this.address = address;
        this.deviceId = deviceId;
        this.ssid = ssid;
        this.fwVersion = fwVersion;
        this.switchState = switchState;
        this.powerOnSate = powerOnSate;
    }

    public String getAddress() {
        return address;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getSsid() {
        return ssid;
    }

    public String getFwVersion() {
        return fwVersion;
    }

    public SwitchState getSwitchState() {
        return switchState;
    }

    public PowerOnState getPowerOnSate() {
        return powerOnSate;
    }
}
