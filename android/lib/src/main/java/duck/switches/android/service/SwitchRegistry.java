package duck.switches.android.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import duck.switches.android.model.Switch;

public class SwitchRegistry {
    private static class Data {
        public int lastId = 0;
        public int defaultSwitchId = 0;
        public Map<Integer, Switch> table = new HashMap<>();
    }

    private Data data;
    private final ObjectMapper mapper;
    private final SharedPreferences sharedPreferences;

    public SwitchRegistry(Context context) {
        mapper = new ObjectMapper(new YAMLFactory());
        final String name = SwitchRegistry.class.getName();
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        data = load();
    }

    public SwitchRegistry refresh() {
        data = load();
        return this;
    }

    public ArrayList<Switch> getAll() {
        ArrayList<Switch> values = new ArrayList<>(data.table.values());
        Collections.sort(values, (o1, o2) -> o1.getOrder() - o2.getOrder());
        return values;
    }

    public Switch get(int id) {
        return data.table.get(id);
    }

    public void add(Switch aSwitch) {
        if (aSwitch.getId() == 0) {
            data.lastId++;
            aSwitch.setId(data.lastId);
            aSwitch.setOrder(data.lastId);
        }
        data.table.put(aSwitch.getId(), aSwitch);
        persist(data);
    }

    public void remove(int id) {
        data.table.remove(id);
        persist(data);
    }

    public Switch getDefaultSwitch() {
        return get(data.defaultSwitchId);
    }

    public void setDefaultSwitch(Switch aSwitch) {
        data.defaultSwitchId = aSwitch.getId();
        persist(data);
    }

    private Data load() {
        String dataString = sharedPreferences.getString("data", null);
        if (dataString == null) {
            return new Data();
        }
        try {
            return mapper.readValue(dataString, Data.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("ApplySharedPref")
    private void persist(Data data) {
        String dataString;
        try {
            dataString = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        sharedPreferences.edit().putString("data", dataString).commit();
    }
}
