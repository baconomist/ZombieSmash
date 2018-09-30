package com.fcfruit.zombiesmash.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.BurnableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;

public class BodyFire implements DrawableEntityInterface
{

    private AnimatableGraphicsEntity animatableGraphicsEntity;
    private BurnableEntityInterface burnableEntityInterface;

    private double burnTimer;
    private double timeBeforeBurned = 5000;

    public boolean enabled = false;

    public BodyFire()
    {
        TextureAtlas atlas = Environment.assets.get("effects/fire/fire.atlas");
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("effects/fire/fire.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).

        this.animatableGraphicsEntity = new AnimatableGraphicsEntity(skeleton, state, atlas);
        this.animatableGraphicsEntity.setAnimation("single_flame");
    }

    public void enable(BurnableEntityInterface burnableEntityInterface)
    {
        this.burnableEntityInterface = burnableEntityInterface;
        this.burnableEntityInterface.attach_fire(this);

        this.burnTimer = System.currentTimeMillis();
        this.enabled = true;
    }

    public void disable()
    {
        this.burnableEntityInterface = null;
        this.enabled = false;
    }

    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.animatableGraphicsEntity.draw(batch, skeletonRenderer);
    }

    @Override
    public void update(float delta)
    {
        this.animatableGraphicsEntity.setPosition(this.burnableEntityInterface.getPosition());
        this.animatableGraphicsEntity.setAngle(this.burnableEntityInterface.getAngle());
        this.animatableGraphicsEntity.update(delta);

        if(System.currentTimeMillis() - this.burnTimer >= this.timeBeforeBurned)
        {
            Environment.firePool.returnBodyFire(this);
        }
        else if(System.currentTimeMillis() - this.burnTimer >= this.timeBeforeBurned - (this.timeBeforeBurned*1f/4f))
        {
            this.burnableEntityInterface.onBurned();
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
    public Vector2 getSize()
    {
        return this.animatableGraphicsEntity.getSize();
    }

    @Override
    public void dispose()
    {
        this.animatableGraphicsEntity.dispose();
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch)
    {
        this.animatableGraphicsEntity.draw(batch);
    }

}
