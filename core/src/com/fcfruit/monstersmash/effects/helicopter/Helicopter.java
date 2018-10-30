package com.fcfruit.monstersmash.effects.helicopter;

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
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.monstersmash.entity.MovableEntity;
import com.fcfruit.monstersmash.entity.interfaces.AnimatableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.MovableEntityInterface;

public class Helicopter implements AnimatableEntityInterface, MovableEntityInterface
{
    private com.fcfruit.monstersmash.entity.AnimatableGraphicsEntity animatableGraphicsEntity;
    private com.fcfruit.monstersmash.entity.MovableEntity movableEntity;

    public Helicopter()
    {
        TextureAtlas atlas = Environment.assets.get("effects/helicopter/helicopter.atlas", TextureAtlas.class);
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("effects/helicopter/helicopter.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        //state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

        this.animatableGraphicsEntity = new com.fcfruit.monstersmash.entity.AnimatableGraphicsEntity(skeleton, state, atlas);
        this.animatableGraphicsEntity.setAnimation("default");

        this.movableEntity = new com.fcfruit.monstersmash.entity.MovableEntity(this.animatableGraphicsEntity);

        this.moveTo(new Vector2(-20, 8));
        this.setSpeed(10);
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.animatableGraphicsEntity.draw(batch, skeletonRenderer);
    }

    @Override
    public void update(float delta)
    {
        this.animatableGraphicsEntity.update(delta);
        this.movableEntity.update(delta);

        if(this.getPosition().x <= -19)
            Environment.drawableRemoveQueue.add(this);
    }

    protected boolean isInLevel()
    {
        Helicopter i = this;

        return i.getPosition().x > Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2 - 0.5f
                && i.getPosition().x < Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth / 2 + 0.5f
                && i.getPosition().y > Environment.physicsCamera.position.y - Environment.physicsCamera.viewportHeight / 2 - 0.5f
                && i.getPosition().y < Environment.physicsCamera.position.y + Environment.physicsCamera.viewportHeight / 2 + 0.5f;
    }

    @Override
    public void moveBy(Vector2 moveBy)
    {
        this.movableEntity.moveBy(moveBy);
    }

    @Override
    public void moveTo(Vector2 moveTo)
    {
        this.movableEntity.moveTo(moveTo);
    }

    @Override
    public boolean isMoving()
    {
        return this.movableEntity.isMoving();
    }

    @Override
    public void clearMoveQueue()
    {
        this.movableEntity.clearMoveQueue();
    }

    @Override
    public void setSpeed(float speed)
    {
        this.movableEntity.setSpeed(speed);
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
    public void dispose()
    {
        this.animatableGraphicsEntity.dispose();
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch)
    {

    }
}
