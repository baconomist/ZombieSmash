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
    private Vector2 position;

    public DrawableGraphicsEntity(Sprite sprite)
    {
        this.sprite = sprite;
        this.position = new Vector2();
    }

    @Override
    public void draw(SpriteBatch spriteBatch)
    {
        this.sprite.draw(spriteBatch);

        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.sprite.getX(), this.sprite.getY(), 0)));
        pos.y = Environment.physicsCamera.viewportHeight - pos.y;
        this.position = new Vector2(pos.x, pos.y);
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
        this.sprite.setPosition(pos.x, Environment.gameCamera.viewportHeight - pos.y);
        this.position = position;
    }

    @Override
    public float getAngle()
    {
        return this.sprite.getRotation();
    }

    @Override
    public void setAngle(float angle)
    {
        this.sprite.setRotation(angle);
    }

    @Override
    public Vector2 getSize()
    {
        Vector3 size = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.sprite.getWidth(), this.sprite.getHeight(), 0)));
        return new Vector2(size.x, size.y - Environment.physicsCamera.viewportHeight);
    }

    public Sprite getSprite(){
        return this.sprite;
    }

    @Override
    public void dispose()
    {

    }

    // Unused
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
    }
    @Override
    public void update(float delta)
    {
    }

}
