package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Lucas on 2017-09-18.
 */

public class Settings {

    private JsonReader json;
    private JsonValue data;

    private boolean enableLight;
    private int lightIntensity;
    private float brightness;

    private boolean enableGameAudio;
    private float musicVolume;
    private float sfxVolume;

    public Settings(){
        json = new JsonReader();
        data = json.parse(Gdx.files.internal("Settings.json"));

        enableLight = data.get("graphics").getBoolean("enableLight");
        lightIntensity = data.get("graphics").getInt("lightIntensity");
        brightness = data.get("graphics").getFloat("brightness");

        enableGameAudio = data.get("audio").getBoolean("enableGameAudio");
        musicVolume = data.get("audio").getFloat("musicVolume");
        sfxVolume = data.get("audio").getFloat("sfxVolume");


    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public float getBrightness(){return brightness;}

    public int getLightIntensity() {
        return lightIntensity;
    }

    public boolean isEnableGameAudio() {
        return enableGameAudio;
    }

    public boolean isEnableLight() {
        return enableLight;
    }


}
