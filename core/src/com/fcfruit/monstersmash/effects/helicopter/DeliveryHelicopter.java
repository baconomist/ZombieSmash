package com.fcfruit.monstersmash.effects.helicopter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.fcfruit.monstersmash.powerups.PowerupCrate;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.effects.helicopter.Helicopter;
import com.fcfruit.monstersmash.level.Spawner;

public class DeliveryHelicopter extends Helicopter
{

    private JsonValue data;
    private double powerup_spawn_delay;
    private double powerup_init_delay;
    private int powerupsToSpawn;
    private int spawnedPowerups;

    private double powerupInitDelayTimer;
    private double powerupSpawnTimer;

    private boolean init_delay_spawned = false;

    public DeliveryHelicopter(JsonValue data)
    {
        super();

        this.data = data;
        this.powerup_spawn_delay = this.data.getDouble("powerup_spawn_delay")*1000;
        this.powerup_init_delay = this.data.getDouble("powerup_init_delay")*1000;
        this.powerupsToSpawn = this.data.get("powerups").size;
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);

        if(!this.isInLevel())
        {
            this.powerupInitDelayTimer = System.currentTimeMillis();
            this.powerupSpawnTimer = System.currentTimeMillis();
        }
        else if(System.currentTimeMillis() - this.powerupInitDelayTimer >= this.powerup_init_delay)
        {
            if(!this.init_delay_spawned)
            {
                this.clearMoveQueue();
                this.spawnCrate(this.data.get("powerups").get(this.spawnedPowerups).asString());
                this.powerupSpawnTimer = System.currentTimeMillis();
                this.init_delay_spawned = true;
            }
            else if(System.currentTimeMillis() - this.powerupSpawnTimer >= this.powerup_spawn_delay && this.spawnedPowerups < this.powerupsToSpawn)
            {
                this.clearMoveQueue();
                this.spawnCrate(this.data.get("powerups").get(this.spawnedPowerups).asString());
                this.powerupSpawnTimer = System.currentTimeMillis();
            }
            else if(this.spawnedPowerups >= this.powerupsToSpawn)
                this.moveTo(new Vector2(-20, 8));
        }

    }

    private void spawnCrate(String powerup)
    {
        try
        {
            PowerupCrate tempCrate;
            com.fcfruit.monstersmash.entity.interfaces.PowerupInterface tempPowerup;
            tempPowerup = (com.fcfruit.monstersmash.entity.interfaces.PowerupInterface) Spawner.entityType.get(powerup).getDeclaredConstructor().newInstance();
            tempCrate = new PowerupCrate(tempPowerup);

            tempCrate.setPosition(this.getPosition());
            tempCrate.changeToGround(1);

            Environment.drawableBackgroundAddQueue.add(tempCrate);

            this.spawnedPowerups += 1;

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Gdx.app.debug("Spawner", "Added Crate");
    }

}
