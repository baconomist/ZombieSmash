package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by Lucas on 2018-01-10.
 */

public class DrawableGraphicsEntity implements DrawableEntityInterface
{

    private Sprite sprite;
    private Vector2 position;

    private float alpha;

    public DrawableGraphicsEntity(com.badlogic.gdx.graphics.g2d.Sprite sprite)
    {
        this.sprite = sprite;
        this.position = new Vector2();

        this.alpha = 1;
    }

    @Override
    public void draw(SpriteBatch spriteBatch)
    {
        this.sprite.draw(spriteBatch);

        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.sprite.getX(), this.sprite.getY(), 0)));
        pos.y = Environment.physicsCamera.position.y*2 - pos.y;
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
        pos.y = Environment.gameCamera.position.y*2 - pos.y;
        this.sprite.setPosition(pos.x, pos.y);
        this.position = new Vector2(position.x, position.y);
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
        size.y = Environment.physicsCamera.position.y*2 - size.y;
        return new Vector2(size.x, size.y);
    }

    public com.badlogic.gdx.graphics.g2d.Sprite getSprite(){
        return this.sprite;
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

        this.sprite.setAlpha(alpha);
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
