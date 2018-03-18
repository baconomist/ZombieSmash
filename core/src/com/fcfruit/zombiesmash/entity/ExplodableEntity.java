package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.AnimatableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.ExplodableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-02-11.
 */

public class ExplodableEntity implements ExplodableEntityInterface, AnimatableEntityInterface
{
    String state;

    Body physicsBody;
    float explosionForce;

    private boolean exploded;
    private boolean isAnimating;

    private float explosionRadiusX = 3;
    private float explosionRadiusY = 3;

    public Array<ExplosionEntityParticle> particles = new Array<ExplosionEntityParticle>();

    private AnimatableGraphicsEntity animatableGraphicsEntity;

    public ExplodableEntity(PhysicsEntityInterface physicsEntityInterface, float explosionForce)
    {
        this.state = "nominal";
        this.physicsBody = physicsEntityInterface.getPhysicsBody();
        this.explosionForce = explosionForce;
        this.exploded = false;

        this.animationSetup();
    }

    private void animationSetup()
    {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("effects/explosion/explosion.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("effects/explosion/explosion.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(1f); // Slow all animations down to 70% speed.

        state.addListener(new AnimationState.AnimationStateAdapter()
        {
            @Override
            public void complete(AnimationState.TrackEntry entry)
            {
                onAnimationComplete(entry);
                super.complete(entry);
            }
        });

        this.animatableGraphicsEntity = new AnimatableGraphicsEntity(skeleton, state, atlas);
        this.animatableGraphicsEntity.setAnimation("explosion");
    }

    private void onAnimationComplete(AnimationState.TrackEntry entry)
    {
        this.isAnimating = false;
    }

    @Override
    public void update(float delta)
    {
        if (isAnimating)
        {
            this.animatableGraphicsEntity.update(delta);
        }

        for (ExplosionEntityParticle particle : this.particles)
        {
            particle.update(delta);
            if (Math.abs(particle.physicsBody.getLinearVelocity().x) < 5f
                    && Math.abs(particle.physicsBody.getLinearVelocity().y) < 5f)
            {
                Environment.physics.destroyBody(particle.physicsBody);
                this.particles.removeValue(particle, true);
            }
            if (Math.abs(particle.physicsBody.getPosition().x - this.physicsBody.getPosition().x) > this.explosionRadiusX
                    || Math.abs(particle.physicsBody.getPosition().y - this.physicsBody.getPosition().y) > this.explosionRadiusY)
            {
                Environment.physics.destroyBody(particle.physicsBody);
                this.particles.removeValue(particle, true);
            }
        }

    }

    @Override
    public void explode()
    {
        int numRays = ExplosionEntityParticle.NUMRAYS;
        for (int i = 0; i < numRays; i++)
        {
            float angle = (float) Math.toDegrees((i / (float) numRays) * 360);
            Vector2 rayDir = new Vector2((float) Math.sin(angle), (float) Math.cos(angle));
            this.particles.add(new ExplosionEntityParticle(Environment.physics.getWorld(), this.physicsBody.getPosition(), rayDir)); // create the particle
        }

        this.exploded = true;
        this.isAnimating = true;

        this.animatableGraphicsEntity.setPosition(new Vector2(this.physicsBody.getPosition().x, 0f));
    }

    @Override
    public boolean shouldExplode()
    {
        return !this.exploded && (Math.abs(this.physicsBody.getLinearVelocity().x) > 6f || Math.abs(this.physicsBody.getLinearVelocity().y) > 6f);
    }

    @Override
    public Body getPhysicsBody()
    {
        return this.physicsBody;
    }


    @Override
    public int timesAnimationCompleted()
    {
        return this.animatableGraphicsEntity.timesAnimationCompleted();
    }

    @Override
    public void setAnimation(String animation)
    {
        this.animatableGraphicsEntity.setAnimation(animation);
    }

    @Override
    public String getCurrentAnimation()
    {
        return this.animatableGraphicsEntity.getCurrentAnimation();
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        if (this.isAnimating)
        {
            this.animatableGraphicsEntity.draw(batch, skeletonRenderer);
        }
    }

    @Override
    public Vector2 getPosition()
    {
        return this.animatableGraphicsEntity.getPosition();
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.animatableGraphicsEntity.setPosition(position);
    }

    @Override
    public float getAngle()
    {
        return this.animatableGraphicsEntity.getAngle();
    }

    @Override
    public void setAngle(float angle)
    {
        this.animatableGraphicsEntity.setAngle(angle);
    }

    @Override
    public Vector2 getSize()
    {
        return this.animatableGraphicsEntity.getSize();
    }

    @Override
    public void dispose()
    {
        this.animatableGraphicsEntity.dispose();
    }

    //Unused
    @Override
    public void draw(SpriteBatch batch)
    {

    }

}
