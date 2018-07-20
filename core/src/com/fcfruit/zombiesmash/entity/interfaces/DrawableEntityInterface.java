package com.fcfruit.zombiesmash.entity.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.SkeletonRenderer;

/**
 * Created by Lucas on 2018-01-06.
 */

public interface DrawableEntityInterface extends UpdatableEntityInterface
{

    void draw(SpriteBatch batch);
    void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer);

    Vector2 getPosition();
    void setPosition(Vector2 position);

    float getAngle();
    void setAngle(float angle);

    float getAlpha();
    void setAlpha(float alpha);

    Vector2 getSize();

    void dispose();

}
