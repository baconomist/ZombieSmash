package com.fcfruit.zombiesmash.entity.interfaces;

/**
 * Created by Lucas on 2018-08-02.
 */

public interface DestroyableEntityInterface extends UpdatableEntityInterface
{
    void update(float delta);
    void destroy();
}
