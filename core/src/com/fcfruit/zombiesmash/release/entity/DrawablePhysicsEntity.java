package com.fcfruit.zombiesmash.release.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.release.Environment;
import com.fcfruit.zombiesmash.release.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.release.entity.interfaces.PhysicsEntityInterface;


/**
 * Created by Lucas on 2018-01-06.
 */

public class DrawablePhysicsEntity implements DrawableEntityInterface, PhysicsEntityInterface
{
    private Sprite sprite;
    private Body physicsBody;

    public DrawablePhysicsEntity(Sprite sprite, Body physicsBody)
    {
        this.sprite = sprite;
        this.physicsBody = physicsBody;
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        sprite.draw(batch);
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
    }

    @Override
    public void update(float delta)
    {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(physicsBody.getPosition().x, physicsBody.getPosition().y, 0)));
        sprite.setPosition(pos.x - sprite.getWidth() / 2, Environment.gameCamera.viewportHeight - pos.y - sprite.getHeight() / 2);
        sprite.setRotation((float) Math.toDegrees(physicsBody.getAngle()));
    }

    @Override
    public Vector2 getPosition()
    {
        return this.physicsBody.getTransform().getPosition();
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.physicsBody.setTransform(position, this.physicsBody.getAngle());
    }

    @Override
    public float getAngle()
    {
        return sprite.getRotation();
    }

    @Override
    public void setAngle(float angle)
    {
        this.physicsBody.setTransform(this.physicsBody.getPosition(), (float)Math.toRadians(angle));
    }

    @Override
    public Vector2 getSize()
    {
        Vector3 size = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(sprite.getWidth(), sprite.getHeight(), 0)));
        return new Vector2(size.x, Environment.physicsCamera.viewportHeight - size.y);
    }

    public Body getPhysicsBody()
    {
        return physicsBody;
    }


    @Override
    public void dispose()
    {

    }
}
