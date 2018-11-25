package com.fcfruit.monstersmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.fcfruit.monstersmash.entity.interfaces.PowerupInterface;
import com.fcfruit.monstersmash.powerups.PowerupCrate;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.effects.helicopter.DeliveryHelicopter;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.powerups.explodable.GrenadePowerup;
import com.fcfruit.monstersmash.powerups.explodable.MolotovPowerup;
import com.fcfruit.monstersmash.powerups.gun_powerup.PistolPowerup;
import com.fcfruit.monstersmash.powerups.gun_powerup.RiflePowerup;
import com.fcfruit.monstersmash.powerups.rock_powerup.RockPowerup;
import com.fcfruit.monstersmash.powerups.rocket.RocketPowerup;
import com.fcfruit.monstersmash.powerups.time.TimePowerup;
import com.fcfruit.monstersmash.ui.Message;
import com.fcfruit.monstersmash.zombies.ArmoredZombie;
import com.fcfruit.monstersmash.zombies.BigZombie;
import com.fcfruit.monstersmash.zombies.Bone_BossZombie;
import com.fcfruit.monstersmash.zombies.CrawlingZombie;
import com.fcfruit.monstersmash.zombies.GirlZombie;
import com.fcfruit.monstersmash.zombies.GrandmaZombie;
import com.fcfruit.monstersmash.zombies.Zombie;
import com.fcfruit.monstersmash.zombies.PoliceZombie;
import com.fcfruit.monstersmash.zombies.RegZombie;
import com.fcfruit.monstersmash.zombies.SuicideZombie;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by Lucas on 2017-11-27.
 */

public class Spawner
{
    public static HashMap<String, Class> entityType = new HashMap<String, Class>();

    static
    {
        entityType.put("reg_zombie", RegZombie.class);
        //entityType.put("girl_zombie", GirlZombie.class);
        entityType.put("police_zombie", PoliceZombie.class);
        entityType.put("big_zombie", BigZombie.class);
        entityType.put("suicide_zombie", SuicideZombie.class);
        entityType.put("armored_zombie", ArmoredZombie.class);
        entityType.put("crawling_zombie", CrawlingZombie.class);
        entityType.put("grandma_zombie", GrandmaZombie.class);
        entityType.put("bone_boss_zombie", Bone_BossZombie.class);

        entityType.put("helicopter", DeliveryHelicopter.class);

        entityType.put("rifle", RiflePowerup.class);
        entityType.put("rock", RockPowerup.class);
        entityType.put("pistol", PistolPowerup.class);
        entityType.put("grenade", GrenadePowerup.class);
        entityType.put("molotov", MolotovPowerup.class);
        entityType.put("time", TimePowerup.class);
        entityType.put("rocket", RocketPowerup.class);
    }

    private int index;
    private String type;
    public JsonValue data;
    private int spawnedEntities;

    private double accumilator = 0d;
    private double random_delay = 0d;

    private int quantity;
    private float init_delay;
    private float spawn_delay;
    private boolean random_spawn_delay;

    private boolean initDelayEnabled;

    private Array<DrawableEntityInterface> spawnableEntities;

    public Spawner(JsonValue data, int index)
    {
        this.index = index;
        this.type = data.name;
        this.data = data;
        this.spawnedEntities = 0;

        try
        {
            this.quantity = data.getInt("quantity");
        } catch (Exception e)
        {
            this.quantity = 1;
            Gdx.app.debug("Spawner", "Quantity not found. Defaulting to 1");
        }

        try
        {
            this.spawn_delay = data.getFloat("spawn_delay");
        } catch (Exception e)
        {
            this.random_spawn_delay = true;
            Gdx.app.debug("Spawner", "Spawn Delay not set. Defaulting to random spawn delay.");
        }

        this.init_delay = data.getFloat("init_delay");

        this.initDelayEnabled = init_delay != 0;

        this.spawnableEntities = new Array<DrawableEntityInterface>();

        this.load_all_entities();

    }

    private void load_all_entities()
    {
        for (int i = 0; i < this.quantity; i++)
        {
            this.spawnableEntities.add(this.loadEntity());
        }
    }

    private Zombie loadZombie()
    {

        try
        {
            int direction = 0;
            if (data.getString("position").equals("left"))
            {
                direction = 0;
            } else if (data.getString("position").equals("right"))
            {
                direction = 1;
            }

            Zombie tempZombie;
            tempZombie = (Zombie) entityType.get(type).getDeclaredConstructor(Integer.class).newInstance((this.spawnableEntities.size + 1) * (this.index + 1));
            tempZombie.setup(direction);

            Gdx.app.debug("Spawner", "Added Zombie");

            return tempZombie;

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;

    }

    private DrawableEntityInterface loadMessage()
    {
        Message tempMessage = new Message();
        tempMessage.setContent(this.data.getString("content"));
        Gdx.app.debug("Spawner", "Added Message");
        return tempMessage;
    }

    private DrawableEntityInterface loadCrate()
    {
        try
        {
            PowerupCrate tempCrate;
            PowerupInterface tempPowerup;
            tempPowerup = (PowerupInterface) entityType.get(this.data.getString("type")).getDeclaredConstructor().newInstance();
            tempCrate = new PowerupCrate(tempPowerup);

            tempCrate.setPosition(new Vector2(Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2 + (float) new Random().nextInt(40) / 10f + 2f, 8));
            tempCrate.changeToGround(0);

            Gdx.app.debug("Spawner", "Added Crate");

            return tempCrate;

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private DrawableEntityInterface loadHelicopter()
    {
        DeliveryHelicopter tempHelicopter = new DeliveryHelicopter(this.data);
        tempHelicopter.setPosition(new Vector2(40, 8));
        Gdx.app.debug("Spawner", "Added Helicopter");
        return tempHelicopter;
    }

    private DrawableEntityInterface loadEntity()
    {
        if (this.type.contains("zombie"))
            return this.loadZombie();
        else if (this.type.contains("heli"))
            return this.loadHelicopter();
        else if (this.type.contains("message"))
            return this.loadMessage();
        else
            return this.loadCrate();
    }

    private void spawnEntity()
    {
        DrawableEntityInterface entity = this.spawnableEntities.get(spawnedEntities);

        if (entity instanceof Message)
        {
            Environment.screens.gamescreen.get_ui_stage().setMessage((Message) entity);
        } else
        {
            if (entity instanceof Zombie)
            {
                this.setZombiePosition((Zombie) entity);
                if (Environment.powerupManager.isSlowMotionEnabled)
                    ((Zombie) this.spawnableEntities.get(spawnedEntities)).getState().setTimeScale(((Zombie) entity).getState().getTimeScale() / TimePowerup.timeFactor);
                ((Zombie) entity).onSpawned();
            }

            Environment.level.addDrawableEntity(this.spawnableEntities.get(spawnedEntities));
        }
        this.spawnedEntities += 1;
    }

    private void setZombiePosition(Zombie zombie)
    {
        float pos_x = Environment.physicsCamera.position.x;
        pos_x = (data.getString("position").equals("left") ? (pos_x - Environment.physicsCamera.viewportWidth/2 - 2) : (pos_x + Environment.physicsCamera.viewportWidth/2 + 2));
        float offset = (zombie.getDirection() == 0 ? -zombie.getSize().x/2 :  zombie.getSize().x/2);

        zombie.setPosition(new Vector2(pos_x + offset, 0));

        // Prevents graphic glitch at position (0, 0)
        zombie.update(Gdx.graphics.getDeltaTime());
        
        try
        {
            zombie.setInitialGround(data.getInt("depth"));
        } catch (Exception e)
        {
            zombie.setInitialGround(Math.round(new Random().nextFloat()));
        }
    }

    public void update(float delta)
    {
        if (this.random_spawn_delay)
            random_delay = Math.random() * 2.5f + 0.1f; // Random * 2.5 seconds with 0.1 seconds as lowest delay

        if (this.quantity > this.spawnedEntities)
        {
            this.accumilator += Math.min(delta, 0.25f);
            if (this.initDelayEnabled && this.accumilator * 1000 >= this.init_delay * 1000)
            {
                this.initDelayEnabled = false;
                this.accumilator = 0;
                this.spawnEntity();
            } else if (!this.initDelayEnabled && this.accumilator * 1000 >= spawn_delay * 1000 + random_delay * 1000)
            {
                this.accumilator = 0;
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
