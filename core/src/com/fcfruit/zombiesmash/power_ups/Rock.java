package com.fcfruit.zombiesmash.power_ups;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;

/**
 * Created by Lucas on 2017-12-02.
 */

public class Rock extends PowerUp{

    public Rock(float x, float y){

        sprite = new Sprite(new Texture(Gdx.files.internal("powerups/rock/rock.png")));

        RubeSceneLoader loader = new RubeSceneLoader(Environment.physics.getWorld());
        RubeScene scene = loader.loadScene(Gdx.files.internal("powerups/rock/rock_rube.json"));

        physicsBody = scene.getBodies().get(0);
        physicsBody.setUserData(this);

        physicsBody.setTransform(x, y, physicsBody.getAngle());

        sprite.setSize(scene.getImages().get(0).width*Physics.PIXELS_PER_METER, scene.getImages().get(0).height* Physics.PIXELS_PER_METER);
        sprite.setOriginCenter();

        Gdx.app.log("rock created", ""+sprite);

    }





}
