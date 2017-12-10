package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;

/**
 * Created by Lucas on 2017-11-18.
 */

public class House extends Objective {


    House() {

        polygon = new Polygon(new float[]{0, 0, 1050, 0, 1050, 700, 0, 700});

    }


}
