package duck.switches.android.phone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import duck.switches.android.model.Switch;
import duck.switches.android.model.SwitchInfoResult;
import duck.switches.android.model.SwitchListener;
import duck.switches.android.model.SwitchState;
import duck.switches.android.phone.R;
import duck.switches.android.phone.adapter.DragAndDrop;
import duck.switches.android.phone.adapter.SwitchAdapter;
import duck.switches.android.phone.databinding.MainActivityBinding;
import duck.switches.android.service.SwitchRegistry;
import duck.switches.android.service.SwitchService;

public class MainActivity extends AppCompatActivity {
    private MainActivityBinding binding;
    private SwitchAdapter adapter;
    private SwitchRegistry switchRegistry;
    private SwitchService switchService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DragAndDrop.enable(binding.recyclerView);

        switchService = new SwitchService();
        switchRegistry = new SwitchRegistry(this);

        adapter = new SwitchAdapter();
        adapter.setOnClick((aSwitch, switchInfo) -> {
            Intent intent = new Intent(this, SwitchActivity.class);
            intent.putExtra("switchId", aSwitch.getId());
            startActivity(intent);
        });
        adapter.setOnToggle((aSwitch, switchInfo) -> {
            if (switchInfo == null) {
                switchService.getInfo(aSwitch, this::update);
            } else {
                SwitchState state = switchInfo.getSwitchState() == SwitchState.ON
                        ? SwitchState.OFF
                        : SwitchState.ON;
                switchService.setSwitchState(aSwitch, state, ok -> {
                    ThreadUtils.runOnUiThread(() -> {
                        if (!ok) {
                            final String message = aSwitch.getName() + " error";
                            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                        }
                        switchService.getInfo(aSwitch, this::update);
                    });
                });
            }
        });
        adapter.setOnUpdate((aSwitch, switchInfo) -> {
            switchRegistry.add(aSwitch);
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setItemAnimator(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        switchRegistry.refresh();
        adapter.setItems(switchRegistry.getAll());
        for (Switch aSwitch : switchRegistry.getAll()) {
            switchService.addListener(new SwitchListener(aSwitch, this::update));
        }
        switchService.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        switchService.stopListening();
        switchService.removeAllListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btn_add) {
            Intent intent = new Intent(this, SwitchActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void update(SwitchInfoResult result) {
        ThreadUtils.runOnUiThread(() -> {
            if (result != null) {
                adapter.update(result.getSwitch(), result.getSwitchInfo());
            }
        });
    }

}
