package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.PowerUpEntityInterface;
import com.fcfruit.zombiesmash.power_ups.PowerupCrate;
import com.fcfruit.zombiesmash.power_ups.Rifle;
import com.fcfruit.zombiesmash.zombies.BigZombie;
import com.fcfruit.zombiesmash.zombies.GirlZombie;
import com.fcfruit.zombiesmash.zombies.NewZombie;
import com.fcfruit.zombiesmash.zombies.PoliceZombie;
import com.fcfruit.zombiesmash.zombies.RegularZombie;
import com.fcfruit.zombiesmash.zombies.SuicideZombie;

import org.lwjgl.Sys;

import java.util.HashMap;
import java.util.Random;

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

    static HashMap<String, Class> powerupType = new HashMap<String, Class>();

    static
    {
        powerupType.put("rifle", Rifle.class);
    }

    String type;

    JsonValue data;

    private int spawnedEntities;

    private double timer;

    private boolean initDelayEnabled;

    private int quantity;
    private float init_delay;
    private float spawn_delay;

    public Spawner(JsonValue data)
    {

        this.data = data;

        this.spawnedEntities = 0;
        this.timer = System.currentTimeMillis();

        this.type = data.name;

        try
        {
            this.quantity = data.getInt("quantity");
        }
        catch (Exception e){
            this.quantity = 1;
            System.err.println("Quantity not found. Defaulting to 1");
        }

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
            tempZombie.setPosition(new Vector2(positions.get(data.getString("position")).x, positions.get(data.getString("position")).y));

            try
            {
                tempZombie.setInitialGround(data.getInt("depth"));
            } catch (Exception e)
            {
                tempZombie.setInitialGround(new Random().nextInt(2));
            }

            if (data.getString("position").contains("left"))
            {
                tempZombie.setDirection(0);
            } else if (data.getString("position").contains("right"))
            {
                tempZombie.setDirection(1);
            }

            Environment.level.addDrawableEntity(tempZombie);

            Gdx.app.log("Spawner", "Added Zombie");

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void spawnCrate()
    {
        try
        {
            PowerupCrate tempCrate;
            PowerUpEntityInterface tempPowerup;
            tempPowerup = (PowerUpEntityInterface) this.powerupType.get(this.data.getString("type")).getDeclaredConstructor().newInstance();
            tempCrate = new PowerupCrate(tempPowerup);

            tempCrate.setPosition(new Vector2(2, 4));

            Environment.level.addDrawableEntity(tempCrate);

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Gdx.app.log("Spawner", "Added Crate");
    }

    private void spawnEntity()
    {
        if (this.type.contains("zombie"))
            this.spawnZombie();
        else
            this.spawnCrate();

        this.spawnedEntities += 1;

    }

    public void update()
    {
        if (this.quantity > this.spawnedEntities)
        {
            if (this.initDelayEnabled && System.currentTimeMillis() - this.timer >= this.init_delay * 1000)
            {
                this.initDelayEnabled = false;
                this.spawnEntity();
                this.timer = System.currentTimeMillis();
            }

            if (!initDelayEnabled && System.currentTimeMillis() - timer >= spawn_delay * 1000)
            {
                this.timer = System.currentTimeMillis();
                this.spawnEntity();
            }
        }
    }

    public int spawnedEntities()
    {
        return this.spawnedEntities;
    }

    public int entitiesToSpawn()
    {
        return this.quantity;
    }

}
