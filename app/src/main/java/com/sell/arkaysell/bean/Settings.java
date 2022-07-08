package com.sell.arkaysell.bean;

/**
 * Created by INDIA on 23-02-2017.
 */

public class Settings {

    private boolean music = false;
    private boolean sound = true;
    private boolean vibration = true;

    public Settings() {
    }

    public Settings(boolean music, boolean sound, boolean vibration) {
        this.music = music;
        this.sound = sound;
        this.vibration = vibration;
    }

    public boolean isMusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }

    @Override
    public String toString() {
        return "music : " + isMusic() +
                " Souund : " + isSound() +
                " vibration : " + isVibration();
    }
}
