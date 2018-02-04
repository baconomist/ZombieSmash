package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;

/**
 * Created by Lucas on 2018-01-13.
 */

public class AnimatableGraphicsEntity implements DrawableEntityInterface
{

    private Skeleton skeleton;
    private AnimationState state;
    private TextureAtlas atlas;

    private Vector2 position;

    private String animation;

    private int timesAnimationCompleted;

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
        this.skeleton.setPosition(pos.x, Environment.gameCamera.viewportHeight - pos.y);
        this.position = position;
    }

    @Override
    public float getAngle()
    {
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

    public int timesAnimationCompleted()
    {
        return this.timesAnimationCompleted;
    }

    public String getAnimation()
    {
        return this.animation;
    }

    public Skeleton getSkeleton()
    {
        return this.skeleton;
    }

    public TextureAtlas getAtlas()
    {
        return this.atlas;
    }

    public AnimationState getState()
    {
        return this.state;
    }

    public void restartAnimation()
    {
        // Restart animation
        Gdx.app.log("restart", "res");
        state.setAnimation(0, this.animation, true);
    }

    @Override
    public Vector2 getSize()
    {
        float[] verticies = ((BoundingBoxAttachment)this.skeleton.findSlot("bounding_box").getAttachment()).getVertices();
        Vector3 size = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3((verticies[2] - verticies[0]) * this.skeleton.getRootBone().getScaleX(),
                verticies[5] * this.skeleton.getRootBone().getScaleY(), 0)));
        size.y = Environment.physicsCamera.viewportHeight - size.y;
        return new Vector2(size.x, size.y);
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
