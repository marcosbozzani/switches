package duck.switches.android.tv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;

import duck.switches.android.model.Switch;
import duck.switches.android.model.SwitchInfo;
import duck.switches.android.model.SwitchInfoResult;
import duck.switches.android.model.SwitchListener;
import duck.switches.android.model.SwitchState;
import duck.switches.android.service.SwitchRegistry;
import duck.switches.android.service.SwitchService;
import duck.switches.android.tv.BuildConfig;
import duck.switches.android.tv.R;
import duck.switches.android.tv.model.Option;
import duck.switches.android.tv.presenter.CardPresenter;
import duck.switches.android.tv.presenter.OptionPresenter;
import duck.switches.android.tv.presenter.SwitchPresenter;
import duck.switches.android.util.AppUtils;
import duck.switches.android.util.ThreadUtils;

public class MainFragment extends BrowseSupportFragment {

    private SwitchService switchService;
    private SwitchRegistry switchRegistry;
    private ArrayObjectAdapter camerasAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeadersState(HEADERS_HIDDEN);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(ContextCompat.getColor(requireContext(), R.color.green_900));

        switchService = new SwitchService();
        switchRegistry = new SwitchRegistry(requireContext());

        ArrayObjectAdapter adapter = new ArrayObjectAdapter(new ListRowPresenter());
        adapter.add(createSwitchesRow());
        adapter.add(createOptionsRow());
        setAdapter(adapter);

        prepareEntranceTransition();
    }

    @Override
    public void onResume() {
        super.onResume();
        switchRegistry.refresh();
        camerasAdapter.clear();
        for (Switch aSwitch : switchRegistry.getAll()) {
            camerasAdapter.add(aSwitch);
            switchService.addListener(new SwitchListener(aSwitch, this::update));
        }
        switchService.startListening();
        startEntranceTransition();
    }

    @Override
    public void onPause() {
        super.onPause();
        switchService.stopListening();
        switchService.removeAllListeners();
    }

    private void update(SwitchInfoResult result) {
        ThreadUtils.runOnUiThread(() -> {
            if (result != null) {
                Switch aSwitch = result.getSwitch();
                int position = camerasAdapter.indexOf(aSwitch);
                SwitchInfo switchInfo = result.getSwitchInfo();
                camerasAdapter.notifyItemRangeChanged(position, 1, switchInfo);
            }
        });
    }

    private ListRow createSwitchesRow() {
        SwitchPresenter presenter = new SwitchPresenter();
        camerasAdapter = new ArrayObjectAdapter(presenter);
        presenter.setOnClickListener(aSwitch -> {
            SwitchInfo switchInfo = presenter.getPayload(aSwitch);
            if (switchInfo == null) {
                switchService.getInfo(aSwitch, this::update);
            }else {
                SwitchState state = switchInfo.getSwitchState() == SwitchState.ON
                        ? SwitchState.OFF
                        : SwitchState.ON;
                switchService.setSwitchState(aSwitch, state, ok -> {
                    ThreadUtils.runOnUiThread(() -> {
                        if (!ok) {
                            final String message = aSwitch.getName() + " error";
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                        }
                        switchService.getInfo(aSwitch, this::update);
                    });
                });
            }
        });
        presenter.setOnCreateContextMenuListener((aSwitch, menu) -> {
            menu.add("Edit").setOnMenuItemClickListener(menuItem -> {
                Intent intent = new Intent(requireContext(), SwitchActivity.class);
                intent.putExtra("switchId", aSwitch.getId());
                startActivity(intent);
                return true;
            });
            menu.add("Move").setOnMenuItemClickListener(menuItem -> {
                presenter.moveMode.start();
                return true;
            });
        });
        presenter.setOnMoveListener(new CardPresenter.OnMoveListener<Switch>() {
            @Override
            public void onMoveLeft(Switch aSwitch) {
                int from = camerasAdapter.indexOf(aSwitch);
                int to = from - 1;
                if (to >= 0) {
                    camerasAdapter.move(from, to);
                }
            }

            @Override
            public void onMoveRight(Switch aSwitch) {
                int from = camerasAdapter.indexOf(aSwitch);
                int to = from + 1;
                if (to < camerasAdapter.size()) {
                    camerasAdapter.move(from, to);
                }
            }

            @Override
            public void onMoveUp(Switch aSwitch) {

            }

            @Override
            public void onMoveDown(Switch aSwitch) {

            }
        });
        presenter.moveMode.addOnMoveModeListener(new CardPresenter.MoveMode.OnMoveModeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {
                for (Switch aSwitch : switchRegistry.getAll()) {
                    aSwitch.setOrder(camerasAdapter.indexOf(aSwitch));
                    switchRegistry.add(aSwitch);
                }
            }
        });
        return new ListRow(new HeaderItem("Switches"), camerasAdapter);
    }

    private ListRow createOptionsRow() {
        OptionPresenter presenter = new OptionPresenter();
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenter);
        adapter.add(new Option("Add", R.drawable.icon_add));
        adapter.add(new Option("Settings", R.drawable.icon_settings));
        adapter.add(new Option("Exit", R.drawable.icon_close));
        presenter.setOnClickListener(option -> {
            switch (option.getName()) {
                case "Add":
                    Intent intent = new Intent(requireContext(), SwitchActivity.class);
                    startActivity(intent);
                    break;
                case "Settings":
                    String version = String.format("%s (%s)", BuildConfig.BUILD_TIME, BuildConfig.BUILD_TYPE);
                    Toast.makeText(requireContext(), version, Toast.LENGTH_LONG).show();
                    break;
                case "Exit":
                    AppUtils.exit(requireContext());
                    break;
            }
        });
        return new ListRow(new HeaderItem("Options"), adapter);
    }
}