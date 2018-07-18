package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.math.Polygon;

/**
 * Created by Lucas on 2017-11-18.
 */

public class House extends Objective
{


    House() {

        width = 1050;
        height = 800;

        polygon = new Polygon(new float[]{0, 0, width, 0, width, height, 0, height});

    }


}
