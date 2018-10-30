package com.fcfruit.monstersmash.entity.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Lucas on 2018-02-02.
 */

public interface BleedableEntityInterface extends UpdatableEntityInterface
{
    void draw(SpriteBatch batch);

    void enable_bleeding();
    void disable_bleeding();
}
