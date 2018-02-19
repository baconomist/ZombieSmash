package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.zombies.BigZombie;
import com.fcfruit.zombiesmash.zombies.GirlZombie;
import com.fcfruit.zombiesmash.zombies.NewZombie;
import com.fcfruit.zombiesmash.zombies.PoliceZombie;
import com.fcfruit.zombiesmash.zombies.RegularZombie;
import com.fcfruit.zombiesmash.zombies.SuicideZombie;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-11-27.
 */

public class Spawner
{

    static HashMap<String, Vector2> positions = new HashMap<String, Vector2>();

    static
    {
        positions.put("left", new Vector2(-1, 0));
        positions.put("right", new Vector2(21, 0));
        positions.put("middle_left", new Vector2(3, 0));
        positions.put("middle_right", new Vector2(16, 0));
    }

    static HashMap<String, Class> zombieType = new HashMap<String, Class>();

    static
    {
        zombieType.put("reg_zombie", RegularZombie.class);
        zombieType.put("girl_zombie", GirlZombie.class);
        zombieType.put("police_zombie", PoliceZombie.class);
        zombieType.put("big_zombie", BigZombie.class);
        zombieType.put("suicide_zombie", SuicideZombie.class);
    }

    String type;

    JsonValue data;

    private int spawnedZombies;

    private double timer;

    private boolean initDelayEnabled;

    private int quantity;
    private float init_delay;
    private float spawn_delay;

    public Spawner(JsonValue data)
    {

        this.data = data;

        this.spawnedZombies = 0;
        this.timer = System.currentTimeMillis();

        this.type = data.name;
        this.quantity = data.getInt("quantity");
        this.init_delay = data.getFloat("init_delay");
        this.spawn_delay = data.getFloat("spawn_delay");

        this.initDelayEnabled = init_delay != 0;

    }

    private void spawnZombie()
    {

        try
        {
            NewZombie tempZombie;
            tempZombie = (NewZombie) zombieType.get(type).getDeclaredConstructor(Integer.class).newInstance(Environment.level.getDrawableEntities().size() + 1);
            tempZombie.setup();

            if (data.getString("position").contains("left"))
            {
                tempZombie.setDirection(0);
            } else if (data.getString("position").contains("right"))
            {
                tempZombie.setDirection(1);
            }

            tempZombie.setPosition(new Vector2(positions.get(data.getString("position")).x - 2, positions.get(data.getString("position")).y));

            Gdx.app.log("zombie", "added");
            Environment.level.addDrawableEntity(tempZombie);
            this.spawnedZombies += 1;

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void update()
    {
        if (this.quantity > this.spawnedZombies)
        {
            if (this.initDelayEnabled && System.currentTimeMillis() - this.timer >= this.init_delay * 1000)
            {
                this.initDelayEnabled = false;
                spawnZombie();
                this.timer = System.currentTimeMillis();
            }

            if (!initDelayEnabled && System.currentTimeMillis() - timer >= spawn_delay * 1000)
            {
                this.timer = System.currentTimeMillis();
                spawnZombie();
            }
        }
    }

    public int spawnedZombies(){return this.spawnedZombies;}
    public int zombiesToSpawn(){return this.quantity;}

}
