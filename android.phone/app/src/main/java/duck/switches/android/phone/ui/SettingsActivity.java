package duck.switches.android.phone.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import duck.switches.android.model.Switch;
import duck.switches.android.phone.BuildConfig;
import duck.switches.android.phone.R;
import duck.switches.android.phone.databinding.SettingsActivityBinding;
import duck.switches.android.service.SwitchRegistry;

public class SettingsActivity extends AppCompatActivity {
    private SettingsActivityBinding binding;
    private SwitchRegistry switchRegistry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        switchRegistry = new SwitchRegistry(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String version = String.format("%s (%s)", BuildConfig.BUILD_TIME, BuildConfig.BUILD_TYPE);
        binding.txtVersion.setText(version);

        ArrayList<Switch> items = switchRegistry.getAll();
        int item_layout = R.layout.dropdown_item;
        ArrayAdapter<Switch> adapter = new ArrayAdapter<>(this, item_layout, items);
        binding.cmbDefaultSwitch.setAdapter(adapter);
        binding.cmbDefaultSwitch.setOnItemClickListener((parent, view, position, id) -> {
            switchRegistry.setDefaultSwitch(adapter.getItem(position));
        });

        Switch defaultSwitch = switchRegistry.getDefaultSwitch();
        if (defaultSwitch != null) {
            binding.cmbDefaultSwitch.setText(defaultSwitch.getName(), false);
        }
    }
}
