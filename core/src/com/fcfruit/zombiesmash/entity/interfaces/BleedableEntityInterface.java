package com.fcfruit.zombiesmash.entity.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Lucas on 2018-02-02.
 */

public interface BleedableEntityInterface
{
    void draw(SpriteBatch batch);
    void update(float delta);
}
