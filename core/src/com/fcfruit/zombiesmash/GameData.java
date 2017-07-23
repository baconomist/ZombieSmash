package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Lucas on 2017-07-23.
 */

public class GameData {

    private JsonReader json;
    private JsonValue data;

    private int coins;
    private float xp;
    private int levelsCompleted;

    public GameData(){
        json = new JsonReader();
        data = json.parse(Gdx.files.internal("GameData.json"));

        coins = data.getInt("coins");
        xp = data.getFloat("xp");
        levelsCompleted = data.getInt("levelsCompleted");

        Gdx.app.log("coins", "" + coins);

    }


}
