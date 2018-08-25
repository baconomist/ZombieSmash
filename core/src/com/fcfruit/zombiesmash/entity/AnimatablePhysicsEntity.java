package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.entity.interfaces.AnimatableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;

public class AnimatablePhysicsEntity implements AnimatableEntityInterface, PhysicsEntityInterface
{

    private AnimatableGraphicsEntity animatableGraphicsEntity;

    private Body physicsBody;

    public AnimatablePhysicsEntity(Skeleton skeleton, AnimationState state, TextureAtlas atlas, Body physicsBody)
    {
        this.animatableGraphicsEntity = new AnimatableGraphicsEntity(skeleton, state, atlas);

        this.physicsBody = physicsBody;
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        this.animatableGraphicsEntity.draw(batch);
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.animatableGraphicsEntity.draw(batch, skeletonRenderer);
    }

    @Override
    public void update(float delta)
    {
        this.animatableGraphicsEntity.setPosition(this.physicsBody.getPosition());
        this.animatableGraphicsEntity.setAngle((float) Math.toDegrees(this.physicsBody.getAngle()));

        this.animatableGraphicsEntity.update(delta);
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
    public Vector2 getPosition()
    {
        return this.physicsBody.getPosition();
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.animatableGraphicsEntity.setPosition(position);
        this.physicsBody.setTransform(position, this.physicsBody.getAngle());
    }

    @Override
    public float getAngle()
    {
        return this.animatableGraphicsEntity.getAngle();
    }

    @Override
    public void setAngle(float angle)
    {
        this.physicsBody.setTransform(this.physicsBody.getPosition(), (float) Math.toRadians(angle));
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
    public Body getPhysicsBody()
    {
        return this.physicsBody;
    }

    @Override
    public void dispose()
    {
        this.animatableGraphicsEntity.dispose();
    }

}
