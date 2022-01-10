package duck.switches.android.phone.adapter;

import android.content.res.ColorStateList;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import duck.switches.android.model.Switch;
import duck.switches.android.model.SwitchInfo;
import duck.switches.android.model.SwitchState;
import duck.switches.android.phone.R;
import duck.switches.android.phone.databinding.SwitchViewBinding;

public class SwitchViewHolder extends RecyclerView.ViewHolder implements DragAndDropItemHandler {
    private final CardView switchView;
    private final TextView titleView;
    private final ImageView toggleView;

    public SwitchViewHolder(@NonNull SwitchViewBinding binding) {
        super(binding.getRoot());
        switchView = binding.getRoot();
        titleView = binding.switchViewTitle;
        toggleView = binding.switchViewToggle;
    }

    public void bindTo(Switch aSwitch,
                       SwitchInfo switchInfo,
                       SwitchAdapterEvent clickEvent,
                       SwitchAdapterEvent toggleEvent) {
        titleView.setText(aSwitch.getName());
        titleView.setOnClickListener(view -> {
            if (clickEvent != null) clickEvent.execute(aSwitch, switchInfo);
        });
        toggleView.setOnClickListener(view -> {
            if (toggleEvent != null) toggleEvent.execute(aSwitch, switchInfo);
        });

        if (switchInfo == null) {
            toggleView.setImageResource(R.drawable.icon_offline);
            toggleView.setImageTintList(getTint(R.color.gray_300));
            toggleView.setBackgroundTintList(getTint(R.color.gray_400));
        } else {
            toggleView.setImageResource(R.drawable.icon_power);
            if (switchInfo.getSwitchState() == SwitchState.ON) {
                toggleView.setImageTintList(getTint(R.color.white));
                toggleView.setBackgroundTintList(getTint(R.color.green_800));
            } else {
                toggleView.setImageTintList(getTint(R.color.gray_600));
                toggleView.setBackgroundTintList(getTint(R.color.gray_400));
            }
        }
    }

    private ColorStateList getTint(@ColorRes int colorId) {
        return ColorStateList.valueOf(ContextCompat.getColor(switchView.getContext(), colorId));
    }

    @Override
    public void select() {
        switchView.setScaleX(1.2f);
        switchView.setScaleY(1.2f);
    }

    @Override
    public void clear() {
        switchView.setScaleX(1.0f);
        switchView.setScaleY(1.0f);
    }
}
