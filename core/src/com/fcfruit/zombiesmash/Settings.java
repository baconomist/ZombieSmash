package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Lucas on 2017-09-18.
 */

public class Settings
{

    private Preferences data;

    public Settings()
    {
        this.data = Gdx.app.getPreferences("settings");
    }

    public void setMusicVolume(float volume)
    {
        this.data.putFloat("musicVolume", volume);
        this.data.flush();
    }

    public float getMusicVolume()
    {
        return this.data.getFloat("musicVolume", 100f);
    }

    public void setSfxVolume(float volume)
    {
        this.data.putFloat("sfxVolume", volume);
        this.data.flush();
    }

    public float getSfxVolume()
    {
        return this.data.getFloat("sfxVolume", 100f);
    }

    public void setMusicEnabled(boolean audioEnabled)
    {
        this.data.putBoolean("musicEnabled", audioEnabled);
        this.data.flush();
    }

    public boolean isMusicEnabled()
    {
        return this.data.getBoolean("musicEnabled", true);
    }

    public void setSfxEnabled(boolean enabled)
    {

        this.data.putBoolean("sfxEnabled", enabled);
        this.data.flush();
    }

    public boolean isSfxEnabled()
    {
        return this.data.getBoolean("sfxEnabled", true);
    }

    public void setRecentAppsButtonEnabled(boolean enabled)
    {
        this.data.putBoolean("enableRecentAppsButton", enabled);
        this.data.flush();
    }

    public boolean isRecentAppsButtonEnabled()
    {
        return this.data.getBoolean("enableRecentAppsButton", false);
    }

    public void setVibrationsEnabled(boolean enabled)
    {
        this.data.putBoolean("vibrationsEnabled", enabled);
        this.data.flush();
    }

    public boolean isVibrationsEnabled()
    {
        return this.data.getBoolean("vibrationsEnabled");
    }

    public void setGoreEnabled(boolean enabled)
    {
        this.data.putBoolean("goreEnabled", enabled);
        this.data.flush();
    }

    public boolean isGoreEnabled()
    {
        return this.data.getBoolean("goreEnabled", true);
    }

}
