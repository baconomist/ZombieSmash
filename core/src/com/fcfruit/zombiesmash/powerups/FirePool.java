package com.fcfruit.zombiesmash.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.effects.BodyFire;
import com.fcfruit.zombiesmash.effects.Fire;
import com.fcfruit.zombiesmash.entity.interfaces.BurnableEntityInterface;

public class FirePool
{
    Fire[] fires;
    BodyFire[] bodyFires;

    public FirePool()
    {
        this.fires = new Fire[150];
        for(int i = 0; i < this.fires.length; i++)
        {
            this.fires[i] = new Fire();
        }

        this.bodyFires = new BodyFire[150];
        for(int i = 0; i < this.bodyFires.length; i++)
        {
            this.bodyFires[i] = new BodyFire();
        }
    }

    public Fire getFire(Vector2 particlePos, Vector2 rayDir)
    {
        for (Fire fire : this.fires)
        {
            if (!fire.enabled)
            {
                fire.enable(particlePos, rayDir);
                return fire;
            }
        }
        Gdx.app.error("FirePool", "No available particle in pool. Make sure fires are being returned to the pool or" +
                " Increase max pool particle limit [default is 150]");

        // Create new blood if not available in pool
        Fire fire = new Fire();
        fire.enable(particlePos, rayDir);
        return fire;
    }

    public void returnFire(Fire fire)
    {
        boolean particleReturned = false;
        for (Fire poolFire : this.fires)
        {
            if (fire == poolFire)
            {
                poolFire.disable();
                particleReturned = true;
                break;
            }
        }

        // If body not in pool, delete it
        if(!particleReturned)
        {
            Gdx.app.error("FirePool", "Deleting fire not from pool originally...");
            fire.disable();
            Environment.physics.destroyBody(fire.getPhysicsBody());
        }
    }


    public BodyFire attachFireToBurnable(BurnableEntityInterface burnableEntityInterface)
    {
        for (BodyFire bodyFire : this.bodyFires)
        {
            if (!bodyFire.enabled)
            {
                bodyFire.enable(burnableEntityInterface);
                return bodyFire;
            }
        }
        Gdx.app.error("FirePool", "No available bodyFire in pool. Make sure bodyFires are being returned to the pool or" +
                " Increase max pool particle limit [default is 150]");

        // Create new blood if not available in pool
        BodyFire bodyFire = new BodyFire();
        bodyFire.enable(burnableEntityInterface);
        return bodyFire;
    }

    public void returnBodyFire(BodyFire bodyFire)
    {
        boolean fireReturned = false;
        for (BodyFire poolBodyFire : this.bodyFires)
        {
            if (bodyFire == poolBodyFire)
            {
                bodyFire.disable();
                fireReturned = true;
                break;
            }
        }

        // If body not in pool, delete it
        if(!fireReturned)
        {
            Gdx.app.error("FirePool", "Deleting bodyFire not from pool originally...");
            bodyFire.disable();
            bodyFire.dispose();
        }
    }
}
