package duck.switches.android.phone.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import duck.switches.android.model.PowerOnState;
import duck.switches.android.model.Switch;
import duck.switches.android.model.SwitchInfo;
import duck.switches.android.phone.databinding.SwitchActivityBinding;
import duck.switches.android.service.SwitchRegistry;
import duck.switches.android.service.SwitchService;

public class SwitchActivity extends AppCompatActivity {
    private int switchId;
    private Switch aSwitch;
    private SwitchService switchService;
    private SwitchRegistry switchRegistry;
    private SwitchActivityBinding binding;
    private int lastCheckedId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SwitchActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        switchRegistry = new SwitchRegistry(this);
        switchId = getIntent().getIntExtra("switchId", 0);

        binding.grpOffline.setVisibility(View.GONE);
        binding.grpDeviceInfo.setVisibility(View.GONE);

        if (switchId == 0) {
            binding.btnRemove.setVisibility(View.GONE);
            binding.loading.setVisibility(View.GONE);
        } else {
            aSwitch = switchRegistry.get(switchId);
            binding.edtName.setText(aSwitch.getName());
            binding.edtLocalAddress.setText(aSwitch.getLocalAddress());

            switchService = new SwitchService();
            switchService.getInfo(aSwitch, result -> {
                ThreadUtils.runOnUiThread(() -> {
                    binding.loading.setVisibility(View.GONE);
                    if (result == null) {
                        binding.grpOffline.setVisibility(View.VISIBLE);
                    } else {
                        binding.grpDeviceInfo.setVisibility(View.VISIBLE);
                        SwitchInfo info = result.getSwitchInfo();

                        binding.rbtOff.setTag(PowerOnState.OFF);
                        binding.rbtOff.setOnClickListener(this::setPowerOnState);
                        check(binding.rbtOff, info.getPowerOnSate() == PowerOnState.OFF);

                        binding.rbtOn.setTag(PowerOnState.ON);
                        binding.rbtOn.setOnClickListener(this::setPowerOnState);
                        check(binding.rbtOn, info.getPowerOnSate() == PowerOnState.ON);

                        binding.rbtStay.setTag(PowerOnState.STAY);
                        binding.rbtStay.setOnClickListener(this::setPowerOnState);
                        check(binding.rbtStay, info.getPowerOnSate() == PowerOnState.STAY);

                        binding.txtDeviceId.setText(info.getDeviceId());
                        binding.txtSsid.setText(info.getSsid());
                        binding.txtFwVersion.setText(info.getFwVersion());
                    }
                });
            });
        }

        binding.btnSave.setOnClickListener(view -> save());
        binding.btnRemove.setOnClickListener(view -> remove());
    }

    private void check(RadioButton radioButton, boolean state) {
        radioButton.setChecked(state);
        if (state) {
            lastCheckedId = radioButton.getId();
        }
    }

    private void setPowerOnState(View view) {
        PowerOnState state = (PowerOnState) view.getTag();
        switchService.setPowerOnState(aSwitch, state, ok -> {
            if (ok) {
                lastCheckedId = view.getId();
            } else {
                binding.grpPowerOnState.check(lastCheckedId);
            }
        });
    }

    private void save() {
        String name = binding.edtName.getText().toString();
        if (name.isEmpty()) {
            validationError("Name cannot be empty");
            return;
        }

        String localAddress = binding.edtLocalAddress.getText().toString();
        if (localAddress.isEmpty()) {
            validationError("Local Address cannot be empty");
            return;
        }

        Switch aSwitch;
        if (switchId == 0) {
            aSwitch = new Switch();
        } else {
            aSwitch = switchRegistry.get(switchId);
        }
        aSwitch.setName(name);
        aSwitch.setLocalAddress(localAddress);

        switchRegistry.add(aSwitch);
        finish();
    }

    private void remove() {
        switchRegistry.remove(switchId);
        finish();
    }

    private void validationError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }
}
