package com.fcfruit.zombiesmash.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface;
import com.fcfruit.zombiesmash.entity.interfaces.UpdatableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.event.LevelEventListener;
import com.fcfruit.zombiesmash.powerups.grenade.GrenadePowerup;

/**
 * Created by Lucas on 2018-03-31.
 */

public class PowerupManager implements UpdatableEntityInterface, LevelEventListener
{
    private Array<PowerupInterface> powerups;

    public PowerupManager()
    {
        this.powerups = new Array<PowerupInterface>();
    }

    public void addPowerup(PowerupInterface powerup)
    {
        this.powerups.add(powerup);
        Environment.gameScreen.get_ui_stage().add_powerup(powerup);
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
    }

    @Override
    public void onCameraMoved()
    {
        this.powerups.clear();
    }

    public Vector3 getGrenadeSpawnPosition(GrenadePowerup grenadePowerup)
    {
        Vector3 pos;

        pos = Environment.physicsCamera.unproject(Environment.gameScreen.get_ui_stage().getViewport().project(new Vector3(grenadePowerup.getUIDrawable().getX(), grenadePowerup.getUIDrawable().getY(), 0)));
        pos.y = Environment.physicsCamera.viewportHeight - pos.y;

        for (PowerupInterface powerupInterface : this.powerups)
        {
            if (powerupInterface instanceof GrenadePowerup && powerupInterface.isActive())
            {
                pos = Environment.physicsCamera.unproject(Environment.gameScreen.get_ui_stage().getViewport().project(new Vector3(powerupInterface.getUIDrawable().getX(), powerupInterface.getUIDrawable().getY(), 0)));
                pos.y = Environment.physicsCamera.viewportHeight - pos.y;
                break;
            }
        }

        return pos;
    }
}
