package com.fcfruit.monstersmash.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.effects.BleedBlood;
import com.fcfruit.monstersmash.entity.ParticleEntity;

public class ParticleEntityPool
{
    ParticleEntity[] particleEntities;

    public ParticleEntityPool()
    {
        this.particleEntities = new ParticleEntity[150];

        for(int i = 0; i < this.particleEntities.length; i++)
        {
            this.particleEntities[i] = new ParticleEntity();
        }
    }

    public ParticleEntity getParticle(Vector2 particlePos, Vector2 rayDir, float NUMRAYS, float blastPower, float drag)
    {
        for (ParticleEntity particleEntity : this.particleEntities)
        {
            if (!particleEntity.enabled)
            {
                particleEntity.enable(particlePos, rayDir, NUMRAYS, blastPower, drag);
                return particleEntity;
            }
        }
        Gdx.app.error("ParticlePool", "No available particle in pool. Make sure particles are being returned to the pool or" +
                " Increase max pool particle limit [default is 100]");

        // Create new blood if not available in pool
        ParticleEntity particleEntity = new ParticleEntity();
        particleEntity.enable(particlePos, rayDir, NUMRAYS, blastPower, drag);
        return particleEntity;
    }

    public void returnParticle(ParticleEntity particleEntity)
    {
        boolean particleReturned = false;
        for (ParticleEntity poolParticle : this.particleEntities)
        {
            if (particleEntity == poolParticle)
            {
                poolParticle.disable();
                particleReturned = true;
                break;
            }
        }

        // If body not in pool, delete it
        if(!particleReturned)
        {
            Gdx.app.error("ParticlePool", "Deleting particle not from pool originally...");
            particleEntity.disable();
            Environment.physics.destroyBody(particleEntity.physicsBody);
        }
    }
}
