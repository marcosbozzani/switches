package duck.switches.android.phone.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class LockMode {
    public static final boolean OFF = false;
    public static final boolean ON = true;
    private final SharedPreferences sharedPreferences;

    public LockMode(Context context) {
        final String name = LockMode.class.getName();
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    @SuppressLint("ApplySharedPref")
    public void setState(boolean state) {
        sharedPreferences.edit().putBoolean("state", state).commit();
    }

    public boolean getState() {
        return sharedPreferences.getBoolean("state", false);
    }
}
