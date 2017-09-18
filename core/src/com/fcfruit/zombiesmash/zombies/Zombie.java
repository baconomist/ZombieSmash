package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import com.fcfruit.zombiesmash.Physics;
import com.fcfruit.zombiesmash.screens.GameScreen;

/**
 * Created by Lucas on 2017-07-21.
 */

public class Zombie{

    private ZombieBody body;

    public float mass;


    public boolean physicsEnabled;

    public Zombie(OrthographicCamera cam, Physics p) {

        body = new ZombieBody(this, cam, p);

        physicsEnabled = true;

    }

    public void draw(SpriteBatch batch, float delta){
        // Body.update() which is inside draw has to be called before everything, or else some instructions to skeleton will not function.
        // I.E. skeleton.findBone("left_arm").setRotation(90); will not work before this body.draw() call

        body.draw(batch, delta);

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

    public ZombieBody getBody(){
        return body;
    }


}








