package com.example.franklin.setlistmanager;

import android.text.format.Time;

/**
 * Created by Franklin on 10/22/2016.
 */

public class SetList {
    private String name;
    private Time date_created, date_modified;
    private String ownerUID;

    public SetList() {
    }

    public SetList(String name, String ownerID){
        this.name = name;
        this.date_modified = new Time();
        this.date_created = new Time();
        this.date_modified.setToNow();
        this.date_created.setToNow();
        this.ownerUID = ownerID;
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

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate_created(Time date_created) {
        this.date_created = date_created;
    }

    public void setDate_modified(Time date_modified) {
        this.date_modified = date_modified;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }
}
