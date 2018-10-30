package com.fcfruit.monstersmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.MovableEntityInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-01-20.
 */

public class MovableEntity implements MovableEntityInterface
{
    DrawableEntityInterface drawableEntity;
    private Vector2 moveToPosition;
    private float speed;
    private ArrayList<Vector2> moveQueue;
    
    private double sensitivity = 0.1d;

    public MovableEntity(com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface drawableEntity)
    {
        this.drawableEntity = drawableEntity;
        this.speed = 1f;

        this.moveQueue = new ArrayList<Vector2>();
    }

    @Override
    public void update(float delta)
    {
        if (this.isMoving())
        {
            // Stops zombie from glitching slightly
            // Only move if ammount to move is > this.sensitivity
            if (Math.abs(this.drawableEntity.getPosition().x - this.moveToPosition.x) > this.sensitivity)
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
            // Only move if ammount to move is > this.sensitivity
            if (Math.abs(this.drawableEntity.getPosition().y - this.moveToPosition.y) > this.sensitivity)
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

        if (!this.isMoving() && this.moveQueue.size() > 0)
        {
            this.moveToPosition = this.moveQueue.get(0);
            this.moveQueue.remove(0);
        }

    }

    @Override
    public void moveBy(Vector2 moveBy)
    {
        if (this.moveQueue.size() > 0)
            this.moveQueue.add(new Vector2(this.moveQueue.get(this.moveQueue.size() - 1).x + moveBy.x, this.moveQueue.get(this.moveQueue.size() - 1).y + moveBy.y));
        else
            this.moveQueue.add(new Vector2(this.drawableEntity.getPosition().x + moveBy.x, this.drawableEntity.getPosition().y + moveBy.y));
    }

    @Override
    public void moveTo(Vector2 moveTo)
    {
        this.moveQueue.add(moveTo);
    }

    @Override
    public boolean isMoving()
    {
        if(this.moveToPosition == null && this.moveQueue.size() > 0)
        {
            this.moveToPosition = this.moveQueue.get(0);
            this.moveQueue.remove(0);
        }
        return this.moveToPosition != null && (Math.abs(this.drawableEntity.getPosition().x - this.moveToPosition.x) > this.sensitivity || Math.abs(this.drawableEntity.getPosition().y - this.moveToPosition.y) > this.sensitivity);
    }

    @Override
    public void clearMoveQueue()
    {
        this.moveToPosition = null;
        this.moveQueue.clear();
    }

    @Override
    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

}
