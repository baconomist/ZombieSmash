package com.fcfruit.zombiesmash.entity.interfaces;

/**
 * Created by Lucas on 2018-02-19.
 */

public interface MultiGroundEntityInterface extends UpdatableEntityInterface
{

    void changeToGround(int ground);
    void resetToInitialGround();
    void setInitialGround(int initialGround);
    int getInitialGround();
    int getCurrentGround();
    boolean isMovingToNewGround();
}
