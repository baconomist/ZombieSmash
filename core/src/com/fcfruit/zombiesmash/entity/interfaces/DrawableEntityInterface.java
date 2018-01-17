package com.fcfruit.zombiesmash.entity.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.spine.SkeletonRenderer;

/**
 * Created by Lucas on 2018-01-06.
 */

public interface DrawableEntityInterface {

    void draw(SpriteBatch batch);
    void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer);

    void update(float delta);

    Vector2 getPosition();
    void setPosition(Vector2 position);

    float getAngle();
    void setAngle(float angle);

    Vector2 getSize();

    void dispose();

}
