package com.fcfruit.zombiesmash.entity.interfaces;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Lucas on 2018-03-07.
 */

public interface PowerUpInterface extends UpdatableEntityInterface
{

    void activate();

    Sprite getUIDrawable();

}
