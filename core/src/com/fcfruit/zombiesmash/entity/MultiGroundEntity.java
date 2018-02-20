package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.MovableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.MultiGroundEntityInterface;

/**
 * Created by Lucas on 2018-02-19.
 */

public class MultiGroundEntity implements MultiGroundEntityInterface {
    private int currentGround;
    private boolean isMovingToNewGround;

    private MovableEntityInterface movableEntity;
    private DrawableEntityInterface drawableEntity;

    public MultiGroundEntity(DrawableEntityInterface drawableEntity, MovableEntityInterface movableEntity) {
        this.currentGround = 0;
        this.isMovingToNewGround = false;

        this.drawableEntity = drawableEntity;
        this.movableEntity = movableEntity;
    }

    @Override
    public void update(float delta) {
        this.isMovingToNewGround = this.isMovingToNewGround &&
                Math.abs(this.drawableEntity.getPosition().y - Environment.physics.getGroundBodies().get(this.currentGround).getPosition().y) < 0.01f;
    }

    @Override
    public void changeToGround(int ground) {
        if (ground != this.currentGround) {
            float y = Environment.physics.getGroundBodies().get(ground).getPosition().y - Environment.physics.getGroundBodies().get(this.currentGround).getPosition().y;
            this.movableEntity.moveBy(new Vector2(0, y));
            this.isMovingToNewGround = true;
            this.currentGround = ground;
        }
    }

    @Override
    public int getCurrentGround() {
        return this.currentGround;
    }

    @Override
    public boolean isMovingToNewGround() {
        return this.isMovingToNewGround;
    }
}
