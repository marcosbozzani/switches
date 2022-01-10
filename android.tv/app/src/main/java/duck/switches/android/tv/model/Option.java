package duck.switches.android.tv.model;

import androidx.annotation.DrawableRes;

public class Option {
    private String name;
    private @DrawableRes
    int iconID;

    public Option(String name, int iconID) {
        this.name = name;
        this.iconID = iconID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }
}
