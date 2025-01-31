package com.fcfruit.monstersmash.entity.interfaces;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Lucas on 2018-01-25.
 */

public interface MovableEntityInterface extends UpdatableEntityInterface
{
    void moveBy(Vector2 moveBy);
    void moveTo(Vector2 moveTo);
    boolean isMoving();
    void clearMoveQueue();
    void setSpeed(float speed);
}
