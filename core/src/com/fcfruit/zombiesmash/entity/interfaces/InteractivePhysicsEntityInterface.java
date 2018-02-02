package com.fcfruit.zombiesmash.entity.interfaces;

/**
 * Created by Lucas on 2018-02-01.
 */

public interface InteractivePhysicsEntityInterface extends InteractiveEntityInterface, PhysicsEntityInterface
{
    void setUsingPowerfulJoint(boolean usingPowerfulJoint);
    boolean isUsingPowerfulJoint();
}
