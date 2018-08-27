package com.fcfruit.zombiesmash.effects.helicopter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.MovableEntity;
import com.fcfruit.zombiesmash.entity.interfaces.AnimatableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.MovableEntityInterface;
import com.fcfruit.zombiesmash.level.Spawner;
import com.fcfruit.zombiesmash.powerups.grenade.GrenadePowerup;
import com.fcfruit.zombiesmash.powerups.gun_powerup.PistolPowerup;
import com.fcfruit.zombiesmash.powerups.gun_powerup.RiflePowerup;
import com.fcfruit.zombiesmash.powerups.rock_powerup.RockPowerup;
import com.fcfruit.zombiesmash.powerups.time.TimePowerup;

import java.util.HashMap;

public class DeliveryHelicopter extends Helicopter
{

    private JsonValue data;
    private double powerup_spawn_delay;
    private double powerup_init_delay;
    private int powerupsToSpawn;
    private int spawnedPowerups;

    private double powerupInitDelayTimer;
    private double powerupSpawnTimer;

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
            if(System.currentTimeMillis() - this.powerupSpawnTimer >= this.powerup_spawn_delay && this.spawnedPowerups < this.powerupsToSpawn)
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
            com.fcfruit.zombiesmash.powerups.PowerupCrate tempCrate;
            com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface tempPowerup;
            tempPowerup = (com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface) Spawner.entityType.get(powerup).getDeclaredConstructor().newInstance();
            tempCrate = new com.fcfruit.zombiesmash.powerups.PowerupCrate(tempPowerup);

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
