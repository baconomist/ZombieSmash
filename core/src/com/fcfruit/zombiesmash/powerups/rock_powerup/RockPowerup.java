package com.fcfruit.zombiesmash.powerups.rock_powerup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface;
import com.fcfruit.zombiesmash.zombies.NewZombie;

import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Lucas on 2018-03-19.
 */

public class RockPowerup implements PowerupInterface
{
    private Sprite ui_image;

    private Rock[] rocks;

    private double duration;
    private double durationTimer;

    private double timeBetweenRocks;
    private double rockSpawnTimer;

    private int rocksSpawned;

    private boolean isActive;

    public RockPowerup()
    {
        this.duration = 10000000;
        this.timeBetweenRocks = 50;

        this.ui_image = new Sprite(new Texture(Gdx.files.internal("powerups/rock/rock_ui.png")));
        this.rocks = new Rock[new Random().nextInt(6) + 6];

        this.rocksSpawned = 0;

        this.isActive = false;

    }

    @Override
    public void update(float delta)
    {
        // If rocks haven't been spawned yet spawn rocks with a time delay.
        if (this.rocksSpawned != this.rocks.length && System.currentTimeMillis() - this.rockSpawnTimer >= timeBetweenRocks)
        {

            Rock rock = new Rock();
            rock.setPosition(new Vector2(this.getClosestRockSpawnZombiePosition() + (float) new Random().nextInt(2000) / 1000f, (float) new Random().nextInt(10) / 10f + 4.5f));
            Environment.level.addDrawableEntity(rock);

            this.rockSpawnTimer = System.currentTimeMillis();

            this.rocksSpawned++;
        }

    }

    @Override
    public void activate()
    {
        Environment.level.addUpdatableEntity(this);
        this.durationTimer = System.currentTimeMillis();
        this.rockSpawnTimer = System.currentTimeMillis();

        this.isActive = true;
    }


    private Float getClosestRockSpawnZombiePosition()
    {
        HashMap<Float, NewZombie> zombieDistances = new HashMap<Float, NewZombie>();

        for (DrawableEntityInterface drawableEntity : Environment.level.getDrawableEntities())
        {
            if (drawableEntity instanceof NewZombie && ((NewZombie) drawableEntity).isInLevel())
            {
                zombieDistances.put(Math.abs(Environment.level.objective.getPosition().x - drawableEntity.getPosition().x), ((NewZombie) drawableEntity));
            }
        }


        if (zombieDistances.size() > 0)
            return zombieDistances.get(Collections.min(zombieDistances.keySet())).getPosition().x;
        else
            return Environment.physicsCamera.position.x + new Random().nextInt(4);


    }

    @Override
    public boolean hasCompleted()
    {
        return System.currentTimeMillis() - this.durationTimer >= this.duration;
    }

    @Override
    public boolean isActive()
    {
        return this.isActive;
    }

    @Override
    public Sprite getUIDrawable()
    {
        return this.ui_image;
    }
}
