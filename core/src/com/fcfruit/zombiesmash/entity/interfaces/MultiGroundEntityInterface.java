package com.fcfruit.zombiesmash.entity.interfaces;

/**
 * Created by Lucas on 2018-02-19.
 */

public interface MultiGroundEntityInterface
{
    void update(float delta);
    void changeToGround(int ground);
    void resetToInitialGround();
    int getInitialGround();
    int getCurrentGround();
    boolean isMovingToNewGround();
}
