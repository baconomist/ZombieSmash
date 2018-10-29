package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.entity.interfaces.ExplodableEntityInterface;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by Lucas on 2018-02-11.
 */

public class ExplodableEntity implements ExplodableEntityInterface, com.fcfruit.zombiesmash.entity.interfaces.AnimatableEntityInterface
{
    private static final int NUMRAYS = 10;

    Body physicsBody;
    private Vector2 explosion_position;
    float explosionForce;

    public boolean exploded;
    public boolean isAnimating;

    public float explosionRadiusX = 5;
    public float explosionRadiusY = 5;

    public Array<ParticleEntity> particles = new Array<ParticleEntity>();

    public AnimatableGraphicsEntity animatableGraphicsEntity;

    private ParticleEntity particle = null;

    public ExplodableEntity(com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface physicsEntityInterface, float explosionForce)
    {
        this.physicsBody = physicsEntityInterface.getPhysicsBody();
        this.explosionForce = explosionForce;
        this.exploded = false;

        this.animationSetup();
    }

    private void animationSetup()
    {
        TextureAtlas atlas = Environment.assets.get("effects/explosion/explosion.atlas", TextureAtlas.class);
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("effects/explosion/explosion.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
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
        for (Object particle : this.particles.toArray(ParticleEntity.class))
        {
            Environment.particleEntityPool.returnParticle((ParticleEntity)particle);
            this.particles.removeValue((ParticleEntity) particle, true);
        }
        Environment.drawableRemoveQueue.add(this);
    }

    @Override
    public void update(float delta)
    {
        if (isAnimating)
        {
            this.animatableGraphicsEntity.update(delta);
        }

        for (Object particleEntity : this.particles.toArray(ParticleEntity.class))
        {
            particle = (ParticleEntity) particleEntity;

            particle.update(delta);
            if (Math.abs(particle.physicsBody.getLinearVelocity().x) < this.explosionForce/10f
                    && Math.abs(particle.physicsBody.getLinearVelocity().y) < this.explosionForce/10f)
            {
                Environment.particleEntityPool.returnParticle(particle);
                this.particles.removeValue(particle, true);
            }
            else if (Math.abs(particle.physicsBody.getPosition().x - explosion_position.x) > this.explosionRadiusX
                    || Math.abs(particle.physicsBody.getPosition().y - explosion_position.y) > this.explosionRadiusY)
            {
                Environment.particleEntityPool.returnParticle(particle);
                this.particles.removeValue(particle, true);
            }
        }
    }

    @Override
    public void explode()
    {
        int numRays = ExplodableEntity.NUMRAYS;
        for (int i = 0; i < numRays; i++)
        {
            float angle = (float) Math.toRadians((i / (float) numRays) * 180 - 90);
            Vector2 rayDir = new Vector2((float) Math.sin(angle), (float) Math.cos(angle));
            ParticleEntity particle = Environment.particleEntityPool.getParticle(this.physicsBody.getPosition(), rayDir, NUMRAYS, this.explosionForce, 5f);
            this.particles.add(particle); // create the particle
        }

        // New Vector2 so that its a new instance of position not pointer
        this.explosion_position = new Vector2(physicsBody.getPosition());

        this.exploded = true;
        this.isAnimating = true;

        this.animatableGraphicsEntity.setPosition(new Vector2(this.physicsBody.getPosition().x, 0f));

        if(Environment.settings.isVibrationsEnabled())
            Gdx.input.vibrate(250);
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
    public Skeleton getSkeleton()
    {
        return this.animatableGraphicsEntity.getSkeleton();
    }

    @Override
    public AnimationState getState()
    {
        return this.animatableGraphicsEntity.getState();
    }

    @Override
    public TextureAtlas getAtlas()
    {
        return this.animatableGraphicsEntity.getAtlas();
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
    public float getAlpha()
    {
        return this.animatableGraphicsEntity.getAlpha();
    }

    @Override
    public void setAlpha(float alpha)
    {
        this.animatableGraphicsEntity.setAlpha(alpha);
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
