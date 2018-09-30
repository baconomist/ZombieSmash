package com.fcfruit.zombiesmash.entity.interfaces;

import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.effects.BodyFire;
import com.fcfruit.zombiesmash.effects.Fire;

public interface BurnableEntityInterface
{
    void attach_fire(BodyFire fire);
    void onBurned();
    BodyFire getBodyFire();

    Vector2 getPosition();
    float getAngle();
}
