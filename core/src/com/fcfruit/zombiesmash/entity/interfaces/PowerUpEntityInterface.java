package com.fcfruit.zombiesmash.entity.interfaces;

/**
 * Created by Lucas on 2018-03-07.
 */

public interface PowerUpEntityInterface extends InteractiveEntityInterface, DrawableEntityInterface
{
    void update(float delta);
    void activate();
}
