package com.fcfruit.zombiesmash.entity.interfaces;

/**
 * Created by Lucas on 2018-02-11.
 */

public interface ExplodableEntityInterface extends PhysicsEntityInterface
{
    void update(float delta);
    void explode();
    boolean shouldExplode();
}
