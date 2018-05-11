package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by Lucas on 2017-11-18.
 */

public class NightLevel extends Level
{

    public NightLevel(int level_id)
    {

        super(level_id);

        this.sprite = new Sprite(new Texture(Gdx.files.internal("maps/night_map/night_map_new.png")));
        // Move to the left to show only playable map
        this.sprite.setPosition(-956, 0);

        this.objective = new House();
        this.objective.setPosition(7f, 0f);

    }

}
