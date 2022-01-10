package duck.switches.android.model;

public class SwitchListener {
    private final Seq seq = new Seq(-1);
    private final Switch aSwitch;
    private final Callback<SwitchInfoResult> callback;

    public SwitchListener(Switch aSwitch, Callback<SwitchInfoResult> callback) {
        this.aSwitch = aSwitch;
        this.callback = callback;
    }

    public Seq getSeq() {
        return seq;
    }

    public Switch getSwitch() {
        return aSwitch;
    }

    public Callback<SwitchInfoResult> getCallback() {
        return callback;
    }
}
