package com.fcfruit.monstersmash.entity.interfaces;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Lucas on 2018-03-07.
 */

public interface PowerupInterface extends UpdatableEntityInterface
{

    void activate();
    boolean hasCompleted();
    boolean isActive();

    Sprite getUIDrawable();

}
