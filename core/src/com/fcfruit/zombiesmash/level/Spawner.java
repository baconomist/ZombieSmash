package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.effects.helicopter.DeliveryHelicopter;
import com.fcfruit.zombiesmash.powerups.grenade.GrenadePowerup;
import com.fcfruit.zombiesmash.powerups.gun_powerup.PistolPowerup;
import com.fcfruit.zombiesmash.powerups.gun_powerup.RiflePowerup;
import com.fcfruit.zombiesmash.powerups.rock_powerup.RockPowerup;
import com.fcfruit.zombiesmash.powerups.rocket.RocketPowerup;
import com.fcfruit.zombiesmash.powerups.time.TimePowerup;
import com.fcfruit.zombiesmash.zombies.BigZombie;
import com.fcfruit.zombiesmash.zombies.GirlZombie;
import com.fcfruit.zombiesmash.zombies.Zombie;
import com.fcfruit.zombiesmash.zombies.PoliceZombie;
import com.fcfruit.zombiesmash.zombies.RegularZombie;
import com.fcfruit.zombiesmash.zombies.SuicideZombie;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by Lucas on 2017-11-27.
 */

public class Spawner
{

    HashMap<String, Vector2> positions = new HashMap<String, Vector2>();

    public static HashMap<String, Class> entityType = new HashMap<String, Class>();

    static
    {
        entityType.put("reg_zombie", RegularZombie.class);
        entityType.put("girl_zombie", GirlZombie.class);
        entityType.put("police_zombie", PoliceZombie.class);
        entityType.put("big_zombie", BigZombie.class);
        entityType.put("suicide_zombie", SuicideZombie.class);

        entityType.put("helicopter", DeliveryHelicopter.class);

        entityType.put("rifle", RiflePowerup.class);
        entityType.put("rock", RockPowerup.class);
        entityType.put("pistol", PistolPowerup.class);
        entityType.put("grenade", GrenadePowerup.class);
        entityType.put("time", TimePowerup.class);
        entityType.put("rocket", RocketPowerup.class);
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

        this.create_spawn_positions();

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
            Gdx.app.debug("Spawner", "Quantity not found. Defaulting to 1");
        }

        this.init_delay = data.getFloat("init_delay");
        this.spawn_delay = data.getFloat("spawn_delay");

        this.initDelayEnabled = init_delay != 0;

    }

    private void create_spawn_positions()
    {
        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(Environment.level.sprite.getX(), Environment.level.sprite.getY(), 0)));

        // x position relative to the camera
        // 0.1f on y to keep zombie out of the ground
        positions.put("left", new Vector2(pos.x + 2f, 0.1f));
        positions.put("right", new Vector2(pos.x + 38.54f, 0.1f));
        positions.put("middle_left", new Vector2(pos.x + 8.59f, 0.1f));
        positions.put("middle_right", new Vector2(pos.x + 32f, 0.1f));
    }

    private void spawnZombie()
    {

        try
        {
            int direction = 0;
            if (data.getString("position").contains("left"))
            {
                direction = 0;
            } else if (data.getString("position").contains("right"))
            {
                direction = 1;
            }

            Zombie tempZombie;
            tempZombie = (Zombie) entityType.get(type).getDeclaredConstructor(Integer.class).newInstance(Environment.level.getDrawableEntities().size() + 1);
            tempZombie.setup(direction);
            tempZombie.setPosition(new Vector2(positions.get(data.getString("position")).x, positions.get(data.getString("position")).y));

            if(Environment.powerupManager.isSlowMotionEnabled)
                tempZombie.getState().setTimeScale(tempZombie.getState().getTimeScale()/TimePowerup.timeFactor);

            try
            {
                tempZombie.setInitialGround(data.getInt("depth"));
            } catch (Exception e)
            {
                tempZombie.setInitialGround(new Random().nextInt(1));
            }

            Environment.level.addDrawableEntity(tempZombie);

            Gdx.app.debug("Spawner", "Added Zombie");

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void spawnCrate()
    {
        try
        {
            com.fcfruit.zombiesmash.powerups.PowerupCrate tempCrate;
            com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface tempPowerup;
            tempPowerup = (com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface) entityType.get(this.data.getString("type")).getDeclaredConstructor().newInstance();
            tempCrate = new com.fcfruit.zombiesmash.powerups.PowerupCrate(tempPowerup);

            tempCrate.setPosition(new Vector2(Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth/2 + (float)new Random().nextInt(40)/10f + 2f, 8));
            tempCrate.changeToGround(0);

            Environment.level.addDrawableEntity(tempCrate);

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Gdx.app.debug("Spawner", "Added Crate");
    }

    private void spawnHelicopter()
    {
        DeliveryHelicopter tempHelicopter = new DeliveryHelicopter(this.data);
        tempHelicopter.setPosition(new Vector2(40, 8));
        Environment.drawableAddQueue.add(tempHelicopter);
    }

    private void spawnEntity()
    {
        if (this.type.contains("zombie"))
            this.spawnZombie();
        else if(this.type.contains("heli"))
            this.spawnHelicopter();
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
            else if (!this.initDelayEnabled && System.currentTimeMillis() - timer >= spawn_delay * 1000)
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
