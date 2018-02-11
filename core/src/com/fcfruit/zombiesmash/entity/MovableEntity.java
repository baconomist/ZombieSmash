package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.MovableEntityInterface;

/**
 * Created by Lucas on 2018-01-20.
 */

public class MovableEntity implements MovableEntityInterface
{
    DrawableEntityInterface drawableEntity;
    private Vector2 moveToPosition;
    private float speed;

    public MovableEntity(DrawableEntityInterface drawableEntity)
    {
        this.drawableEntity = drawableEntity;
        this.speed = 1f;
    }

    public void update(float delta)
    {
        if (this.isMoving())
        {
            // Stops zombie from glitching slightly
            // Only move if ammount to move is > 0.01
            if(Math.abs(this.drawableEntity.getPosition().x - this.moveToPosition.x) > 0.01d)
            {
                if (this.drawableEntity.getPosition().x < this.moveToPosition.x)
                {
                    this.drawableEntity.setPosition(new Vector2(this.drawableEntity.getPosition().x + speed * delta, this.drawableEntity.getPosition().y));
                } else if (this.drawableEntity.getPosition().x > this.moveToPosition.x)
                {
                    this.drawableEntity.setPosition(new Vector2(this.drawableEntity.getPosition().x - speed * delta, this.drawableEntity.getPosition().y));
                }
            }

            // Stops zombie from glitching slightly
            // Only move if ammount to move is > 0.01
            if(Math.abs(this.drawableEntity.getPosition().y - this.moveToPosition.y) > 0.01d)
            {
                if (this.drawableEntity.getPosition().y < this.moveToPosition.y)
                {
                    this.drawableEntity.setPosition(new Vector2(this.drawableEntity.getPosition().x, this.drawableEntity.getPosition().y + speed * delta));
                } else if (this.drawableEntity.getPosition().y > this.moveToPosition.y)
                {
                    this.drawableEntity.setPosition(new Vector2(this.drawableEntity.getPosition().x, this.drawableEntity.getPosition().y - speed * delta));
                }
            }

        }

    }

    public void moveBy(Vector2 moveBy)
    {
        this.moveToPosition = new Vector2(this.drawableEntity.getPosition().x + moveBy.x, this.drawableEntity.getPosition().y + moveBy.y);
    }

    public void moveTo(Vector2 moveTo)
    {
        this.moveToPosition = moveTo;
    }

    public boolean isMoving()
    {
        return this.moveToPosition != null && (Math.abs(this.drawableEntity.getPosition().x - this.moveToPosition.x) > 0.01f || Math.abs(this.drawableEntity.getPosition().y - this.moveToPosition.y) > 0.01f);
    }

    public void clear(){
        this.moveToPosition = null;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

}
