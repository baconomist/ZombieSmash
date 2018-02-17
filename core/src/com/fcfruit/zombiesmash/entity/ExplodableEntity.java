package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.ExplodableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-02-11.
 */

public class ExplodableEntity implements ExplodableEntityInterface
{
    String state;

    Body physicsBody;
    float explosionForce;

    private boolean exploded;

    public Array<ExplosionEntityParticle> particles = new Array<ExplosionEntityParticle>();

    public ExplodableEntity(PhysicsEntityInterface physicsEntityInterface, float explosionForce)
    {
        this.state = "nominal";
        this.physicsBody = physicsEntityInterface.getPhysicsBody();
        this.explosionForce = explosionForce;
        this.exploded = false;
    }

    @Override
    public void update(float delta)
    {
        for(ExplosionEntityParticle particle: this.particles){
            particle.update(delta);
            if(Math.abs(particle.physicsBody.getLinearVelocity().x) < 5f
                    && Math.abs(particle.physicsBody.getLinearVelocity().y) < 5f){
                Environment.physics.getWorld().destroyBody(particle.physicsBody);
                this.particles.removeValue(particle, true);
            }
        }

    }

    @Override
    public void explode()
    {
        int numRays = ExplosionEntityParticle.NUMRAYS;
        for (int i = 0; i < numRays; i++) {
            float angle = (float)Math.toDegrees((i / (float)numRays) * 360);
            Vector2 rayDir = new Vector2( (float) Math.sin(angle),(float) Math.cos(angle) );
            particles.add(new ExplosionEntityParticle(Environment.physics.getWorld(), this.physicsBody.getPosition(), rayDir)); // create the particle
        }

        this.exploded = true;
    }

    @Override
    public boolean shouldExplode()
    {
        Gdx.app.log("this", ""+this.getPhysicsBody().getLinearVelocity());
        return !this.exploded && (Math.abs(this.physicsBody.getLinearVelocity().x) > 6f || Math.abs(this.physicsBody.getLinearVelocity().y) > 6f);
    }

    @Override
    public Body getPhysicsBody()
    {
        return this.physicsBody;
    }
}
