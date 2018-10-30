package com.fcfruit.monstersmash.entity.interfaces;

import com.badlogic.gdx.math.Vector2;
import com.fcfruit.monstersmash.effects.BodyFire;
import com.fcfruit.monstersmash.effects.Fire;

public interface BurnableEntityInterface
{
    void attach_fire(BodyFire fire);
    void onBurned();
    BodyFire getBodyFire();

    Vector2 getPosition();
    float getAngle();
}
