package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
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

        super();

        RubeSceneLoader loader = new RubeSceneLoader(Environment.physics.world);
        RubeScene rubeScene = loader.loadScene(Gdx.files.internal("maps/night_map/night_map_rube.json"));

        this.body = rubeScene.getBodies().get(0);
        for(Fixture f : this.body.getFixtureList()){
            f.setUserData(this);
        }

    }
}
