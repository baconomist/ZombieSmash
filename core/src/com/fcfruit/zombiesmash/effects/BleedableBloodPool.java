package com.fcfruit.zombiesmash.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.fcfruit.zombiesmash.Environment;

public class BleedableBloodPool
{

    private BodyDef bodyDef;
    private FixtureDef fixtureDef;

    private BleedBlood[] bleedBlood;
    
    public BleedableBloodPool()
    {
        // create 200 blood particles
        this.bleedBlood = new BleedBlood[400];
        // Fill array with bodies
        for(int i = 0; i < this.bleedBlood.length; i++)
        {
            this.bleedBlood[i] = new BleedBlood();
        }
    }

    public BleedBlood getBlood(Vector2 center, Vector2 direction)
    {
        for (BleedBlood blood : this.bleedBlood)
        {
            if (!blood.enabled)
            {
                blood.enable(center, direction);
                return blood;
            }
        }
        Gdx.app.error("BloodPool", "No available blood in pool. Make sure blood is being returned to the pool or" +
                " Increase max pool blood limit [default is 400]");

        // Create new blood if not available in pool
       /*BleedBlood bleedBlood = new BleedBlood();
        bleedBlood.enable(center, direction);
        return bleedBlood;*/
       return null;
    }

    public void returnBlood(BleedBlood blood)
    {
        boolean bloodReturned = false;
        for (BleedBlood poolBlood : this.bleedBlood)
        {
            if (blood == poolBlood)
            {
                poolBlood.disable();
                bloodReturned = true;
                break;
            }
        }

        // If body not in pool, delete it
        if(!bloodReturned)
        {
            Gdx.app.error("BloodPool", "Deleting body not from pool originally...");
            blood.disable();
            Environment.physics.destroyBody(blood.getPhysicsBody());
        }
    }

}
