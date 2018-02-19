package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.physics.Physics;

import java.util.ArrayList;

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
        this.sprite.setPosition(-956, this.sprite.getY());

        this.objective = new House();
        this.objective.setPosition(7f, 0f);

        this.json = new JsonReader();
        this.data = json.parse(Gdx.files.internal("maps/night_map/levels/" + this.level_id + ".json"));

        Environment.physicsCamera.position.x = Level.positions.get(data.get(0).name).x;
        Environment.physicsCamera.update();
        Environment.gameCamera.position.x = Environment.physicsCamera.position.x * Physics.PIXELS_PER_METER;
        Environment.gameCamera.update();
        Environment.physics.constructPhysicsBoundries();

        this.spawners = new ArrayList<Spawner>();
        for (JsonValue v : this.data.get(0))
        {
            this.spawners.add(new Spawner(v));
        }

    }

}
