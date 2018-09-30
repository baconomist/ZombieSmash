package com.fcfruit.zombiesmash.brains;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fcfruit.zombiesmash.entity.ParticleEntity;

public class BrainPool
{
    private Brain[] brains;

    public BrainPool()
    {
        this.brains = new Brain[150];

        for(int i = 0; i < this.brains.length/3; i++)
        {
            brains[i] = new Brain(1);
        }
        for(int i = this.brains.length/3 - 1; i < this.brains.length/1.5; i++)
        {
            brains[i] = new Brain(2);
        }
        for(int i = (int) (this.brains.length/1.5 - 1) ; i < this.brains.length; i++)
        {
            brains[i] = new Brain(3);
        }
    }

    public Brain getBrain(int value, Vector2 position, Vector2 velocity)
    {
        for(Brain brain : this.brains)
        {
            if(!brain.enabled && brain.value == value)
            {
                brain.enable(position, velocity);
                return brain;
            }
        }

        Gdx.app.error("BrainPool", "No available brain in pool. Make sure brains are being returned to the pool or" +
                " Increase max pool brain limit [default is 150]");

        // Create new blood if not available in pool
        Brain brain = new Brain(2);
        brain.enable(position, velocity);
        return brain;
    }

    public void returnBrain(Brain brainEntity)
    {
        boolean brainReturned = false;
        for (Brain poolbrain : this.brains)
        {
            if (brainEntity == poolbrain)
            {
                poolbrain.disable();
                brainReturned = true;
                break;
            }
        }

        // If body not in pool, delete it
        if(!brainReturned)
        {
            Gdx.app.error("brainPool", "Deleting brain not from pool originally...");
            brainEntity.disable();
        }
    }

}
