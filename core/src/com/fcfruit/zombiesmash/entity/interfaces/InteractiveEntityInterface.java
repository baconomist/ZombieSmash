package com.fcfruit.zombiesmash.entity.interfaces;

import com.badlogic.gdx.math.Polygon;

/**
 * Created by Lucas on 2018-01-06.
 */

public interface InteractiveEntityInterface extends InputCaptureEntityInterface, com.fcfruit.zombiesmash.entity.interfaces.UpdatableEntityInterface
{

    boolean isTouching();

    Polygon getPolygon();

}
