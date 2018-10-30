package com.fcfruit.monstersmash.entity.interfaces;

import com.fcfruit.monstersmash.entity.interfaces.PhysicsEntityInterface;

/**
 * Created by Lucas on 2018-02-11.
 */

public interface ExplodableEntityInterface extends UpdatableEntityInterface, PhysicsEntityInterface
{

    void explode();
    boolean shouldExplode();
}
