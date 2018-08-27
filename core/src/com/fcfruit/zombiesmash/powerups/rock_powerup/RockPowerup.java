package com.fcfruit.zombiesmash.powerups.rock_powerup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.ArrayList;
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

    private double timeBetweenRocks;
    private double rockSpawnTimer;

    private int rocksSpawned;

    private boolean isActive;

    public RockPowerup()
    {
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
        if (this.rocksSpawned < this.rocks.length && System.currentTimeMillis() - this.rockSpawnTimer >= timeBetweenRocks)
        {
            Rock rock = new Rock();
            Gdx.app.log("", ""+getRockSpawnPosition());
            rock.setPosition(new Vector2(this.getRockSpawnPosition() + (float) new Random().nextInt(2000) / 1000f, (float) new Random().nextInt(10) / 10f + 4.5f));
            Environment.drawableBackgroundAddQueue.add(rock);

            this.rockSpawnTimer = System.currentTimeMillis();

            this.rocksSpawned++;
        }

    }

    @Override
    public void activate()
    {
        this.rockSpawnTimer = System.currentTimeMillis();

        this.isActive = true;
    }



    /*
     * This method finds the biggest group of zombies to spawn rocks on for maximal DAMAGE!
     * */
    private Float getRockSpawnPosition()
    {
        /*HashMap<Float, Zombie> zombieDistances = new HashMap<Float, Zombie>();

        for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : Environment.level.getDrawableEntities())
        {
            if (drawableEntity instanceof Zombie && ((Zombie) drawableEntity).isAlive() && ((Zombie) drawableEntity).isInLevel())
            {
                zombieDistances.put(Math.abs(Environment.level.objective.getPosition().x - drawableEntity.getPosition().x), ((Zombie) drawableEntity));
            }
        }


        if (zombieDistances.size() > 0)
            return zombieDistances.get(Collections.min(zombieDistances.keySet())).getPosition().x;
        else
            return Environment.physicsCamera.position.x + new Random().nextInt(4);*/

        float group_distance_increment = 2.5f;
        float offset = (Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth/2);

        HashMap<Float, Integer> zombie_groups = new HashMap<Float, Integer>();
        HashMap<Float, Float> zombie_positions = new HashMap<Float, Float>();

        // <= is important!
        for(float f = group_distance_increment; f <= Environment.gameCamera.viewportWidth; f+=group_distance_increment)
        {
            zombie_groups.put(f, 0);

            for(DrawableEntityInterface drawableEntityInterface : Environment.level.getDrawableEntities())
            {
                if(drawableEntityInterface instanceof Zombie && ((Zombie) drawableEntityInterface).isAlive() && ((Zombie) drawableEntityInterface).isInLevel())
                {
                    Zombie zombie = (Zombie) drawableEntityInterface;
                    if(zombie.getPosition().x > f-5f && zombie.getPosition().x < f)
                    {
                        zombie_groups.put(f, zombie_groups.get(f)+1);
                        zombie_positions.put(f, zombie.getPosition().x);
                    }
                }
            }
        }

        int max = Collections.max(zombie_groups.values());
        for(float key : zombie_groups.keySet())
        {
            if(zombie_positions.get(key) != null && zombie_groups.get(key) == max)
                return zombie_positions.get(key);
        }
        return Environment.physicsCamera.position.x + new Random().nextInt(4);
    }

    @Override
    public boolean hasCompleted()
    {
        return this.isActive() && this.rocksSpawned == this.rocks.length;
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
