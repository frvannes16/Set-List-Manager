package com.example.franklin.setlistmanager.helpers;

/**
 * Created by Franklin on 10/22/2016.
 */

public class Song {

    private String name;
    private int bpm;

    public Song() {
    }

    public Song(String name, int bpm) {
        this.name = name;
        this.bpm = bpm;
    }

    public String getName() {
        return name;
    }

    public int getBpm() {
        return bpm;
    }
}
