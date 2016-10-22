package com.example.franklin.setlistmanager;

import android.text.format.Time;

/**
 * Created by Franklin on 10/22/2016.
 */

public class SetList {
    private String name;
    private Time date_created, date_modified;

    public SetList(String name){
        this.name = name;
        this.date_modified = new Time();
        this.date_created = new Time();
        this.date_modified.setToNow();
        this.date_created.setToNow();
    }

    void update() {
        date_modified.setToNow();
    }

    public String getName() {
        return name;
    }

    public Time getDate_created() {
        return date_created;
    }

    public Time getDate_modified() {
        return date_modified;
    }
}
