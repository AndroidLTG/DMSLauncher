package com.example.dmsv4.dmslauncher.GetSet;

/**
 * Created by DMSv4 on 12/2/2015.
 */
import android.graphics.drawable.Drawable;

public class AppsDetail {
    public CharSequence label;
    public CharSequence name;
    public Drawable icon;

    public CharSequence getLabel() {
        return label;
    }

    public void setLabel(CharSequence label) {
        this.label = label;
    }

    public CharSequence getName() {
        return name;
    }

    public void setName(CharSequence name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
