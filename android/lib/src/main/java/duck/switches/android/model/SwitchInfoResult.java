package duck.switches.android.model;

public class SwitchInfoResult {
    private final Switch aSwitch;
    private final SwitchInfo switchInfo;

    public SwitchInfoResult(Switch aSwitch, SwitchInfo switchInfo) {
        this.aSwitch = aSwitch;
        this.switchInfo = switchInfo;
    }

    public Switch getSwitch() {
        return aSwitch;
    }

    public SwitchInfo getSwitchInfo() {
        return switchInfo;
    }
}
