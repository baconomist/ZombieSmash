package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.entity.interfaces.MultiGroundEntityInterface;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.MovableEntityInterface;

/**
 * Created by Lucas on 2018-02-19.
 */

public class MultiGroundEntity implements MultiGroundEntityInterface
{
    private int initialGround;
    private int currentGround;
    private boolean isMovingToNewGround;

    private MovableEntityInterface movableEntity;
    private com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity;

    public MultiGroundEntity(com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity, MovableEntityInterface movableEntity)
    {
        this.isMovingToNewGround = false;

        this.drawableEntity = drawableEntity;
        this.movableEntity = movableEntity;
    }

    @Override
    public void update(float delta)
    {
        this.isMovingToNewGround = this.isMovingToNewGround &&
                Math.abs(this.drawableEntity.getPosition().y - Environment.physics.getGroundBodies().get(this.currentGround).getPosition().y) < 0.01f;
    }

    @Override
    public void changeToGround(int ground)
    {
        if (ground != this.currentGround)
        {
            float y = Environment.physics.getGroundBodies().get(ground).getPosition().y - Environment.physics.getGroundBodies().get(this.currentGround).getPosition().y;
            y = y + 0.01f; // Prevents zombie from getting stuck in the ground
            this.movableEntity.moveBy(new Vector2(0, y));
            this.isMovingToNewGround = true;
            this.currentGround = ground;
        }
    }

    @Override
    public void resetToInitialGround()
    {
        this.currentGround = this.initialGround;
    }

    @Override
    public int getInitialGround()
    {
        return this.initialGround;
    }

    @Override
    public void setInitialGround(int initialGround)
    {
        this.initialGround = initialGround;
        this.changeToGround(this.initialGround);
    }

    @Override
    public int getCurrentGround()
    {
        return this.currentGround;
    }

    @Override
    public boolean isMovingToNewGround()
    {
        return this.isMovingToNewGround;
    }
}
