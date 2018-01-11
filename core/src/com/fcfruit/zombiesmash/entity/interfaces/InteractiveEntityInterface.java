package com.fcfruit.zombiesmash.entity.interfaces;

/**
 * Created by Lucas on 2018-01-06.
 */

public interface InteractiveEntityInterface {
    void update(float delta);

    void onTouchDown(float x, float y, int p);
    void onTouchDragged(float x, float y, int p);
    void onTouchUp(float x, float y, int p);

    boolean isTouching();

}
