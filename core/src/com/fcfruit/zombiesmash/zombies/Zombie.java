package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Physics;
import com.fcfruit.zombiesmash.screens.GameScreen;

import java.util.HashMap;

/**
 * Created by Lucas on 2017-07-21.
 */

public class Zombie{

    private ZombieBody body;

    public int id;

    public boolean physicsEnabled;

    public Zombie() {

        body = new ZombieBody(this);

        physicsEnabled = true;

    }

    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer, float delta){
        // Body.update() which is inside draw has to be called before everything, or else some instructions to skeleton will not function.
        // I.E. skeleton.findBone("left_arm").setRotation(90); will not work before this body.draw() call

        body.draw(batch, skeletonRenderer, delta);

        update(delta);
    }

    private void update(float delta){



    }

    public void touchDown(float x, float y, int pointer){
        body.touchDown(x, y, pointer);
    }

    public void touchDragged(float x, float y, int pointer){
        body.touchDragged(x, y, pointer);
    }

    public void touchUp(float x, float y, int pointer){
        body.touchUp(x, y, pointer);
    }




    public void constructPhysicsBody(World world){
        body.constructPhysicsBody(world);
    }

    public void setPosition(float x, float y){
        body.setPosition(x, y);
    }


    public Part getPartFromPhysicsBody(Body physicsBody){

        return body.getPartFromPhysicsBody(physicsBody);

    }

    public HashMap<String, Part> getParts(){
        return body.getParts();
    }


    public void destroy(){
        body.destroy();
    }


}








