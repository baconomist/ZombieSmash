package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
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

    public String touchedLimb;

    public boolean isHanging;

    public boolean isAnimating;

    public boolean isPhysicsEnabled;

    public Zombie() {

        body = new ZombieBody(this);

        touchedLimb = "none";

        isHanging = false;

        isAnimating = true;

        isPhysicsEnabled = true;

    }

    public void draw(SpriteBatch batch, float delta){
        // Body.update() which is inside draw has to be called before everything, or else some instructions to skeleton will not function.
        // I.E. skeleton.findBone("left_arm").setRotation(90); will not work before this body.draw() call

        body.draw(batch, delta);

        update(delta);
    }

    private void update(float delta){

        if(!isHanging) {
            touchedLimb = body.getTouchedLimb();
        }

        //!isHanging works, cus if hanging false, then go to else, if not draging, ishanging = false
        if(!touchedLimb.equals("none") && Gdx.input.isTouched()) {
            isHanging = true;
            isAnimating = false;
        } else if (!isDraging() && !Gdx.input.isTouched()){
            isHanging = false;
            isAnimating = true;
            touchedLimb = "none";
        }

        // Simplified if statements.
        body.isPhysicsEnabled = !isAnimating;

        if(isHanging) {
            body.hangFromLimb(touchedLimb, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        }

    }

    private boolean isDraging(){

        if(Gdx.input.getDeltaX() != 0f || Gdx.input.getDeltaY() != 0f) {

            return Gdx.input.getDeltaX() <= 500 && Gdx.input.getDeltaX() >= -500 && Gdx.input.getDeltaY() <= 500 && Gdx.input.getDeltaY() >= -500;

        }

        return false;

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








