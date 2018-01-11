package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;

/**
 * Created by Lucas on 2018-01-10.
 */

public class DrawableGraphicsEntity implements DrawableEntityInterface
{

    private Sprite sprite;
    private Skeleton skeleton;

    public DrawableGraphicsEntity(Sprite sprite)
    {
        this.sprite = sprite;
    }

    public DrawableGraphicsEntity(Skeleton skeleton){
        this.skeleton = skeleton;
    }

    @Override
    public void draw(SpriteBatch spriteBatch)
    {
        this.sprite.draw(spriteBatch);
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        skeletonRenderer.draw(batch, skeleton);
    }

    @Override
    public void update(float delta)
    {

    }

    @Override
    public Vector2 getPosition()
    {
        if(this.sprite != null){
            Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.sprite.getX(), this.sprite.getY(), 0)));
            return new Vector2(pos.x, Environment.physicsCamera.viewportHeight - pos.y);
        }
        else if(this.skeleton != null){
            Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.skeleton.getRootBone().getX(), this.skeleton.getRootBone().getY(), 0)));
            return new Vector2(pos.x, Environment.physicsCamera.viewportHeight - pos.y);
        }

        return null;

    }

    @Override
    public void setPosition(Vector2 vector2)
    {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(vector2, 0)));
        if(this.sprite != null){
            this.sprite.setPosition(pos.x, Environment.gameCamera.viewportHeight - pos.y);
        }
        else if(this.skeleton != null){
            this.skeleton.setPosition(pos.x, Environment.gameCamera.viewportHeight - pos.y);
        }
    }

    @Override
    public float getAngle()
    {
        if(this.sprite != null){
           return this.sprite.getRotation();
        }
        else if(this.skeleton != null){
            return this.skeleton.getRootBone().getWorldRotationX();
        }
        return 0;
    }

    @Override
    public void setAngle(float angle)
    {
        if(this.sprite != null){
            this.sprite.setRotation(angle);
        }
        else if(this.skeleton != null){
           this.skeleton.getRootBone().setRotation(angle);
        }
    }

    @Override
    public Vector2 getSize()
    {
        if(this.sprite != null){
            Vector3 size = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.sprite.getWidth(), this.sprite.getHeight(), 0)));
            return new Vector2(size.x, size.y - Environment.physicsCamera.viewportHeight);
        }
        return null;
    }

}
