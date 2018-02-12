package com.fcfruit.zombiesmash.entity.interfaces;

/**
 * Created by Lucas on 2018-02-11.
 */

public interface AnimatableEntityInterface
{
    void update(float delta);

    int timesAnimationCompleted();
    void setAnimation(String animation);
    String getCurrentAnimation();
}
