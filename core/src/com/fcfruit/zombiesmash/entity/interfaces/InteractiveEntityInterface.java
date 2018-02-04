package com.fcfruit.zombiesmash.entity.interfaces;

import com.badlogic.gdx.math.Polygon;

/**
 * Created by Lucas on 2018-01-06.
 */

public interface InteractiveEntityInterface
{
    void update(float delta);

    void onTouchDown(float screenX, float screenY, int pointer);
    void onTouchDragged(float screenX, float screenY, int pointer);
    void onTouchUp(float screenX, float screenY, int pointer);

    boolean isTouching();

    Polygon getPolygon();

}
