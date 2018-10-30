package com.fcfruit.monstersmash.entity.interfaces;

import com.fcfruit.monstersmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.PhysicsEntityInterface;

/**
 * Created by Lucas on 2018-02-01.
 */

public interface InteractivePhysicsEntityInterface extends UpdatableEntityInterface, InteractiveEntityInterface, PhysicsEntityInterface
{
    void setUsingPowerfulJoint(boolean usingPowerfulJoint);
    void overrideTouching(boolean touching, float screenX, float screenY, int p);
    boolean isUsingPowerfulJoint();
}
