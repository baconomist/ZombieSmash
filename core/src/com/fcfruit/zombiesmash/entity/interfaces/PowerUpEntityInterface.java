package com.fcfruit.zombiesmash.entity.interfaces;

import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;

/**
 * Created by Lucas on 2018-03-07.
 */

public interface PowerUpEntityInterface extends InteractiveEntityInterface, DrawableEntityInterface
{
    void update(float delta);

    void activate();

    DrawableGraphicsEntity getUIDrawable();

}
