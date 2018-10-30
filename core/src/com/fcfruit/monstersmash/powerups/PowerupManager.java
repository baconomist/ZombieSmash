package com.fcfruit.monstersmash.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.interfaces.PowerupInterface;
import com.fcfruit.monstersmash.entity.interfaces.UpdatableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.event.LevelEventListener;
import com.fcfruit.monstersmash.powerups.explodable.ExplodablePowerup;

/**
 * Created by Lucas on 2018-03-31.
 */

public class PowerupManager implements UpdatableEntityInterface, LevelEventListener
{
    private Array<PowerupInterface> powerups;

    private Array<PowerupCrate> crates;

    public boolean isSlowMotionEnabled = false;

    public PowerupManager()
    {
        this.powerups = new Array<PowerupInterface>();
        this.crates = new Array<PowerupCrate>();

        Environment.updatableAddQueue.add(this);
        Environment.levelEventListenerAddQueue.add(this);
    }

    private Vector3 grenadePosition;

    public void addPowerup(PowerupInterface powerup)
    {
        this.powerups.add(powerup);
        Environment.screens.gamescreen.get_ui_stage().add_powerup(powerup);
    }

    public void addCrate(PowerupCrate powerupCrate)
    {
        this.crates.add(powerupCrate);
    }

    public boolean has_room_for_powerup()
    {
        int non_active_powerups = 0;
        for (PowerupInterface powerupInterface : this.powerups)
        {
            if (!powerupInterface.isActive())
            {
                non_active_powerups++;
            }
        }

        int opening_crates = 0;
        for (PowerupCrate powerupCrate : this.crates)
        {
            if (powerupCrate.isOpening())
            {
                opening_crates++;
            }
        }
        return non_active_powerups < 4 && opening_crates < 4 - non_active_powerups;
    }

    @Override
    public void update(float delta)
    {
        Array<PowerupInterface> copy = new Array<PowerupInterface>();
        for (PowerupInterface powerup : this.powerups)
        {
            copy.add(powerup);
        }
        for (PowerupInterface powerup : copy)
        {
            if (powerup.hasCompleted())
                this.powerups.removeValue(powerup, true);
        }

        Array<PowerupCrate> copy_crates = new Array<PowerupCrate>();
        for (PowerupCrate powerupCrate : this.crates)
        {
            copy_crates.add(powerupCrate);
        }
        for (PowerupCrate powerupCrate : copy_crates)
        {
            if (powerupCrate.isOpen())
                this.crates.removeValue(powerupCrate, true);
        }
    }

    @Override
    public void onCameraMoved()
    {
        this.powerups.clear();
        this.grenadePosition = null;
    }

    @Override
    public void onLevelEnd()
    {

    }

    public Vector3 getGrenadeSpawnPosition(com.fcfruit.monstersmash.powerups.explodable.ExplodablePowerup grenadePowerup)
    {
        if (grenadePosition == null)
        {
            Vector3 pos;

            pos = Environment.physicsCamera.unproject(Environment.screens.gamescreen.get_ui_stage().getViewport().project(new Vector3(grenadePowerup.getUIDrawable().getX(), grenadePowerup.getUIDrawable().getY(), 0)));
            pos.y = Environment.physicsCamera.position.y*2 - pos.y;

            grenadePosition = pos;
        }

        return grenadePosition;
    }
}
