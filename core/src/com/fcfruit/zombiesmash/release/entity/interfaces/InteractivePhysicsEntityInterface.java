package com.fcfruit.zombiesmash.release.entity.interfaces;

/**
 * Created by Lucas on 2018-02-01.
 */

public interface InteractivePhysicsEntityInterface extends UpdatableEntityInterface, InteractiveEntityInterface, com.fcfruit.zombiesmash.release.entity.interfaces.PhysicsEntityInterface
{
    void setUsingPowerfulJoint(boolean usingPowerfulJoint);
    void overrideTouching(boolean touching, float screenX, float screenY, int p);
    boolean isUsingPowerfulJoint();
}
