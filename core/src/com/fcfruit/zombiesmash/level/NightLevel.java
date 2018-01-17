package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.fcfruit.zombiesmash.Environment;

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

        this.spawners = new ArrayList<Spawner>();
        for (JsonValue v : this.data.get(0))
        {
            this.spawners.add(new Spawner(v));
        }

    }

    public void moveCameraToNextPosition()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (isCameraMoving)
                {
                    if (Environment.physicsCamera.position.x < positions.get(currentCameraPosition).x)
                    {
                        Environment.physicsCamera.position.x += 0.1f * Gdx.graphics.getDeltaTime();
                    } else if (Environment.physicsCamera.position.x > positions.get(currentCameraPosition).x)
                    {
                        Environment.physicsCamera.position.x -= 0.1f * Gdx.graphics.getDeltaTime();
                    } else
                    {
                        isCameraMoving = false;
                    }
                    updateCamera();
                }
            }
        }).start();
    }


    @Override
    public void update(float delta)
    {

        this.currentCameraPosition = this.data.get(this.currentJsonItem).name;

        if (!isCameraMoving)
        {
            super.update(delta);
            for (Spawner spawner : spawners)
            {
                spawner.update();
            }


            if (zombiesDead)
            {
                if (this.data.size - 1 == this.currentJsonItem)
                {
                    this.levelEnd = true;
                } else
                {

                    this.clear();

                    this.currentJsonItem += 1;
                    this.spawners = new ArrayList<Spawner>();

                    for (JsonValue jsonValue : this.data.get(this.currentJsonItem))
                    {
                        this.spawners.add(new Spawner(jsonValue));
                    }

                    this.isCameraMoving = true;
                    this.zombiesDead = false;
                    this.moveCameraToNextPosition();
                }
            }
        }

    }


}
