package duck.switches.android.phone.ui;

import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

import duck.switches.android.model.Switch;
import duck.switches.android.model.SwitchState;
import duck.switches.android.phone.R;
import duck.switches.android.service.SwitchRegistry;
import duck.switches.android.service.SwitchService;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SwitchTileService extends TileService {
    private Switch aSwitch;
    private SwitchState switchState;
    private SwitchService switchService;

    @Override
    public void onStartListening() {
        aSwitch = new SwitchRegistry(this).getDefaultSwitch();
        switchService = new SwitchService();
        setSwitchUnavailable();
        updateSwitch();
    }

    @Override
    public void onClick() {
        if (aSwitch != null) {
            if (isSwitchUnavailable()) {
                updateSwitch();
            } else {
                SwitchState state = isSwitchOn() ? SwitchState.OFF : SwitchState.ON;
                switchService.setSwitchState(aSwitch, state, result -> {
                    ThreadUtils.runOnUiThread(() -> {
                        if (!result) {
                            setSwitchUnavailable();
                        } else {
                            if (state == SwitchState.ON) {
                                setSwitchOn(aSwitch.getName());
                            } else {
                                setSwitchOff(aSwitch.getName());
                            }
                        }
                    });
                });
            }
        }
    }

    private void updateSwitch() {
        if (aSwitch != null) {
            switchService.getInfo(aSwitch, result -> {
                ThreadUtils.runOnUiThread(() -> {
                    if (result == null) {
                        setSwitchUnavailable();
                    } else {
                        if (result.getSwitchInfo().getSwitchState() == SwitchState.ON) {
                            setSwitchOn(result.getSwitch().getName());
                        } else {
                            setSwitchOff(result.getSwitch().getName());
                        }
                    }
                });
            });
        }
    }

    private boolean isSwitchUnavailable() {
        return switchState == null;
    }

    private boolean isSwitchOn() {
        return switchState == SwitchState.ON;
    }

    private void setSwitchUnavailable() {
        updateTile(Tile.STATE_INACTIVE, R.drawable.icon_offline, getString(R.string.app_name));
    }

    private void setSwitchOn(String name) {
        switchState = SwitchState.ON;
        updateTile(Tile.STATE_ACTIVE, R.drawable.icon_power, name);
    }

    private void setSwitchOff(String name) {
        switchState = SwitchState.OFF;
        updateTile(Tile.STATE_INACTIVE, R.drawable.icon_power, name);
    }

    private void updateTile(int tileState, int iconId, String name) {
        Tile tile = getQsTile();
        tile.setState(tileState);
        tile.setIcon(Icon.createWithResource(this, iconId));
        tile.setLabel(name);
        tile.updateTile();
    }
}
