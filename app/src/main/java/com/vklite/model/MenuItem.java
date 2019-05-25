package com.vklite.model;

import android.support.annotation.NonNull;

import com.vklite.R;

public class MenuItem {
    private String item;
    private Integer id;

    public static final MenuItem[] items = {
            new MenuItem("Friends", R.drawable.ic_person),
            new MenuItem("Groups", R.drawable.ic_group),
            new MenuItem("Photos", R.drawable.ic_camera),
            new MenuItem("Videos", R.drawable.ic_video)
    };

    private MenuItem(String item, Integer id) {
        this.item = item;
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public Integer getImageID() {
        return id;
    }

    @NonNull
    public String toString() {
        return this.item;
    }
}