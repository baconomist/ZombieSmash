package com.fcfruit.zombiesmash;

import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.zombies.Part;

/**
 * Created by Lucas on 2017-07-21.
 */

public class PartPhysics {

    private Part part;

    private Vector2 gravity;

    private float ground;

    private Vector2 currentVelocity;

    private float maxSpeed;

    public PartPhysics(Part p){

        part = p;

        gravity = new Vector2(0f, -9.8f);

        ground = 50f;

        currentVelocity = new Vector2(0f, 0f);

        maxSpeed = 100f;

    }

    public void update(float delta){

        if(part.isPhysicsEnabled) {

            act(delta);

        }

    }

    private void act(float delta){

        if(part.isGravityEnabled) {

            if (part.getY() > ground) {
                applyGravity(delta);
            } else {
                currentVelocity.x = 0f;
                currentVelocity.y = 0f;
            }
        }

    }

    private void applyGravity(float delta){
        Vector2 velocity = getVelocity(delta);

        if(part.getY() + velocity.y > ground) {

            part.setPosition(part.getX() + velocity.x, part.getY() + velocity.y);

        }
        else{

            part.setPosition(part.getX() + velocity.x, part.getY() + (velocity.y + ground + ground - (part.getY() + velocity.y + ground)));

        }

        if(velocity.x < maxSpeed) {
            currentVelocity.x = velocity.x;
        }
        if(velocity.y < maxSpeed) {
            currentVelocity.y = velocity.y;
        }

    }

    private Vector2 getVelocity(float delta){
        return new Vector2(currentVelocity.x + (part.mass*gravity.x) * delta, currentVelocity.y + (part.mass*gravity.y) * delta);
    }

}
