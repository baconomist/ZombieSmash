package com.fcfruit.monstersmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.interfaces.AnimatableEntityInterface;

/**
 * Created by Lucas on 2018-01-13.
 */

public class AnimatableGraphicsEntity implements AnimatableEntityInterface
{

    private Skeleton skeleton;
    private AnimationState state;
    private TextureAtlas atlas;

    private Vector2 position;

    private String animation;

    private int timesAnimationCompleted;

    private float alpha;

    public AnimatableGraphicsEntity(Skeleton skeleton, AnimationState state, TextureAtlas atlas)
    {
        this.skeleton = skeleton;
        this.state = state;
        this.atlas = atlas;
        this.position = new Vector2();
        this.timesAnimationCompleted = 0;

        this.state.addListener(new AnimationState.AnimationStateAdapter()
        {
            @Override
            public void complete(AnimationState.TrackEntry entry)
            {
                onAnimationComplete(entry);
                super.complete(entry);
            }
        });

        this.alpha = 1;
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        skeletonRenderer.draw(batch, skeleton);
    }

    @Override
    public void update(float delta)
    {
        this.state.update(delta); // Update the animation getUpTimer.

        this.state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.

        this.skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

    }

    @Override
    public Vector2 getPosition()
    {
        return this.position;
    }

    @Override
    public void setPosition(Vector2 position)
    {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(position, 0)));
        this.skeleton.setPosition(pos.x, Environment.gameCamera.position.y*2 - pos.y);
        this.position = position;
    }

    @Override
    public float getAngle()
    {
        if(this.skeleton.getFlipX())
            return this.skeleton.getRootBone().getWorldRotationX() + 180;
        else
            return this.skeleton.getRootBone().getWorldRotationX();
    }

    @Override
    public void setAngle(float angle)
    {
        this.skeleton.getRootBone().setRotation(angle);
    }

    private void onAnimationComplete(AnimationState.TrackEntry entry)
    {
        this.timesAnimationCompleted++;
    }

    public void setAnimation(String animation)
    {
        if (this.animation != animation)
        {
            this.onAnimationChange();
            this.state.setAnimation(0, animation, true);
            this.animation = animation;
        }
    }

    private void onAnimationChange()
    {
        this.timesAnimationCompleted = 0;
    }

    @Override
    public int timesAnimationCompleted()
    {
        return this.timesAnimationCompleted;
    }

    @Override
    public String getCurrentAnimation()
    {
        return this.animation;
    }

    @Override
    public Skeleton getSkeleton()
    {
        return this.skeleton;
    }

    @Override
    public TextureAtlas getAtlas()
    {
        return this.atlas;
    }

    @Override
    public AnimationState getState()
    {
        return this.state;
    }

    public void restartAnimation()
    {
        // Restart animation
        state.setAnimation(0, this.animation, true);
    }

    @Override
    public Vector2 getSize()
    {
        Vector3 size;
        if(this.skeleton.findSlot("bounding_box") != null)
        {
            float[] verticies = ((BoundingBoxAttachment)this.skeleton.findSlot("bounding_box").getAttachment()).getVertices();
            size = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3((verticies[2] - verticies[0]) * this.skeleton.getRootBone().getScaleX(),
                    (verticies[5] - verticies[3]) * this.skeleton.getRootBone().getScaleY(), 0)));
            size.y = Environment.physicsCamera.position.y * 2 - size.y;
        }
        else
        {
            size = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.skeleton.getData().getWidth() * this.skeleton.getRootBone().getScaleX(),
                    this.skeleton.getData().getHeight() * this.skeleton.getRootBone().getScaleY(), 0)));
            size.y = Environment.physicsCamera.position.y * 2 - size.y;
        }
        return new Vector2(size.x, size.y);
    }

    @Override
    public float getAlpha()
    {
        return this.alpha;
    }

    @Override
    public void setAlpha(float alpha)
    {
        if(alpha >= 0 && alpha <= 1)
        {
            this.alpha = alpha;
        }

        if(alpha < 0)
            this.alpha = 0;

        if(alpha > 1)
            this.alpha = 1;


        for(Slot slot : this.skeleton.getSlots())
        {
            slot.getColor().a = this.alpha;
        }

    }

    @Override
    public void dispose()
    {

    }

    // Unused
    @Override
    public void draw(SpriteBatch batch)
    {

    }

}
