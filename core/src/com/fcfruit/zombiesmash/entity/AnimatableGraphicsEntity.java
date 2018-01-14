package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
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

    private String animation;

    public AnimatableGraphicsEntity(Skeleton skeleton, AnimationState state, TextureAtlas atlas){
        this.skeleton = skeleton;
        this.state = state;
        this.atlas = atlas;
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
        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.skeleton.getRootBone().getX(), this.skeleton.getRootBone().getY(), 0)));
        return new Vector2(pos.x, Environment.physicsCamera.viewportHeight - pos.y);
    }

    @Override
    public void setPosition(Vector2 position)
    {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(position, 0)));
        this.skeleton.setPosition(pos.x, Environment.gameCamera.viewportHeight - pos.y);
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


    public void setAnimation(String animation){
        this.animation = animation;
        state.setAnimation(0, animation, true);
    }

    public String getAnimation(){
        return this.animation;
    }

    public Skeleton getSkeleton(){
        return this.skeleton;
    }

    public TextureAtlas getAtlas(){
        return this.atlas;
    }

    @Override
    public Vector2 getSize()
    {
        Vector3 size = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.skeleton.getData().getWidth()*this.skeleton.getRootBone().getScaleX(), skeleton.getData().getHeight()*this.skeleton.getRootBone().getScaleY(), 0)));
        size.y = Environment.physicsCamera.viewportHeight - size.y;
        return new Vector2(size.x, size.y);
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch)
    {

    }

}
