package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;

import java.util.ArrayList;


/**
 * Created by Lucas on 2018-01-06.
 */

public class DrawableEntity implements DrawableEntityInterface
{
    private Sprite sprite;
    private Body physicsBody;

    public DrawableEntity(Sprite sprite, Body physicsBody)
    {
        this.sprite = sprite;
        this.physicsBody = physicsBody;
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    @Override
    public void update(float delta) {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(physicsBody.getPosition().x, physicsBody.getPosition().y, 0)));
        sprite.setPosition(pos.x - sprite.getWidth() / 2, Environment.gameCamera.viewportHeight - pos.y - sprite.getHeight()/2);
        sprite.setRotation((float) Math.toDegrees(physicsBody.getAngle()));
    }

    @Override
    public Vector2 getPosition() {
        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(sprite.getX(), sprite.getY(), 0)));
        return new Vector2(pos.x, Environment.physicsCamera.viewportHeight - pos.y);
    }

    @Override
    public void setPosition(Vector2 position) {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(position, 0)));
        sprite.setPosition(pos.x, Environment.gameCamera.viewportHeight - pos.y);
    }

    @Override
    public float getAngle() {
        return sprite.getRotation();
    }

    @Override
    public void setAngle(float angle) {
        sprite.setRotation(angle);
    }

    @Override
    public Vector2 getSize() {
        Vector3 size = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(sprite.getWidth(), sprite.getHeight(), 0)));
        return new Vector2(size.x, Environment.physicsCamera.viewportHeight - size.y);
    }

    @Override
    public Body getPhysicsBody() {
        return physicsBody;
    }
}
