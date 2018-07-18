package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by Lucas on 2017-11-18.
 */

public class NightLevel extends Level
{

    public NightLevel(int level_id)
    {

        super(level_id);

        cameraPositions.put("left", new Vector2(8.9f, 0));
        cameraPositions.put("right", new Vector2(22.6f, 0));
        cameraPositions.put("middle", new Vector2(15.9f, 0));

        this.sprite = new Sprite(new Texture(Gdx.files.internal("maps/night_map/night_map.png")));
        // Move to the left to show only playable map
        this.sprite.setPosition(-956, 0);

        this.objective = new House();
        this.objective.setPosition(13f, 0f);


    }

}
