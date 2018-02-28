package com.fcfruit.zombiesmash.entity.interfaces;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Lucas on 2018-01-25.
 */

public interface MovableEntityInterface
{
    void update(float delta);
    void moveBy(Vector2 moveBy);
    void moveTo(Vector2 moveTo);
    boolean isMoving();
    void clearMoveQueue();
    void setSpeed(float speed);
}
