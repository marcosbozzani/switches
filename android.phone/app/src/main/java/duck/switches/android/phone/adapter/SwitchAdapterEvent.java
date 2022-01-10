package duck.switches.android.phone.adapter;

import duck.switches.android.model.Switch;
import duck.switches.android.model.SwitchInfo;

public interface SwitchAdapterEvent {
    void execute(Switch aSwitch, SwitchInfo switchInfo);
}
