package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.zombies.Body;

/**
 * Created by Lucas on 2017-07-21.
 */

public class BodyPhysics {

    private Body body;

    private Vector2 gravity;

    private float ground;

    private Vector2 currentVelocity;

    private Vector2 maxSpeed;

    public BodyPhysics(Body b){

        body = b;

        gravity = new Vector2(0f, -9.8f);

        ground = 50f;

        currentVelocity = new Vector2(0f, 0f);

        maxSpeed = new Vector2(-10f, -100f);

    }

    public void applyGravity(float delta){

        if (body.getY() > ground) {

            Vector2 velocity = getVelocity(delta);

            float x;
            float y;

            float bodyX = body.getX();
            float bodyY = body.getY();

            if (body.getY() + velocity.y >= ground) {
                x = bodyX + velocity.x;
                y = bodyY + velocity.y;
            } else {
                x = bodyX + velocity.x;
                y = bodyY + (ground + velocity.y + ground - (bodyY + velocity.y + ground));
            }

            body.skeleton.setPosition(x, y);


            if (velocity.x > maxSpeed.x && velocity.x < maxSpeed.x*-1) {
                currentVelocity.x = velocity.x;
            }
            if (velocity.y > maxSpeed.y) {
                currentVelocity.y = velocity.y;
            }

        } else {
            currentVelocity.x = 0f;
            currentVelocity.y = 0f;
        }

    }

    private Vector2 getVelocity(float delta){

        return new Vector2(currentVelocity.x + (body.mass*gravity.x) * delta, currentVelocity.y + (body.mass*gravity.y) * delta);

    }

    public void applyRagdoll(float delta){
        body.getRagdollParts();
    }

}
