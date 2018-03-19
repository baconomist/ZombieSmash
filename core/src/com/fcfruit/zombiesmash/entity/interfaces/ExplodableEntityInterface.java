package com.fcfruit.zombiesmash.entity.interfaces;

/**
 * Created by Lucas on 2018-02-11.
 */

public interface ExplodableEntityInterface extends UpdatableEntityInterface,  PhysicsEntityInterface
{

    void explode();
    boolean shouldExplode();
}
