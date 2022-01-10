package duck.switches.android.phone.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import duck.switches.android.model.Switch;
import duck.switches.android.model.SwitchInfo;
import duck.switches.android.phone.databinding.SwitchViewBinding;

public class SwitchAdapter extends ListAdapter<Switch, SwitchViewHolder>
        implements DragAndDropHandler {

    private List<Switch> items = new ArrayList<>();
    private Map<Switch, SwitchInfo> infos = new HashMap<>();
    private SwitchAdapterEvent clickEvent;
    private SwitchAdapterEvent toggleEvent;
    private SwitchAdapterEvent updateEvent;

    public SwitchAdapter() {
        super(new DiffCallback());
    }

    public void setItems(List<Switch> items) {
        this.items = items;
        this.infos = new HashMap<>();
        submitList(this.items);
    }

    public void update(Switch aSwitch, SwitchInfo switchInfo) {
        infos.put(aSwitch, switchInfo);
        notifyItemChanged(items.indexOf(aSwitch));
    }

    public void setOnClick(SwitchAdapterEvent clickEvent) {
        this.clickEvent = clickEvent;
    }

    public void setOnToggle(SwitchAdapterEvent toggleEvent) {
        this.toggleEvent = toggleEvent;
    }

    public void setOnUpdate(SwitchAdapterEvent updateEvent) {
        this.updateEvent = updateEvent;
    }

    @NonNull
    @Override
    public SwitchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new SwitchViewHolder(SwitchViewBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SwitchViewHolder holder, int position) {
        Switch aSwitch = getItem(position);
        SwitchInfo switchInfo = infos.get(aSwitch);
        holder.bindTo(aSwitch, switchInfo, clickEvent, toggleEvent);
    }

    @Override
    public void move(int from, int to) {
        Collections.swap(items, from, to);
        notifyItemMoved(from, to);
    }

    @Override
    public void moveEnd() {
        List<Switch> items = getCurrentList();
        int order = 0;
        for (Switch aSwitch : items) {
            order++;
            aSwitch.setOrder(order);
            if (updateEvent != null) {
                updateEvent.execute(aSwitch, infos.get(aSwitch));
            }
        }
    }

    public static class DiffCallback extends DiffUtil.ItemCallback<Switch> {
        @Override
        public boolean areItemsTheSame(@NonNull Switch oldItem, @NonNull Switch newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Switch oldItem, @NonNull Switch newItem) {
            return oldItem.equals(newItem);
        }
    }
}
