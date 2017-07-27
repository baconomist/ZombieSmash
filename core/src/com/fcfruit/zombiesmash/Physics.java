package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-07-21.
 */

public class Physics {

    public Game game;

    private Vector2 gravity;

    private Zombie zombie;

    private float ground;

    private Vector2 currentVelocity;

    private float maxSpeed;

    public Physics(Zombie z, Game g){

        game = g;

        gravity = new Vector2(0f, -9.8f);

        zombie = z;

        ground = 50f;

        currentVelocity = new Vector2(0f, 0f);

        maxSpeed = 100f;

    }

    public void update(float delta){

        if(zombie.getPhysicsState()) {

            act(delta);

        }

    }

    private void act(float delta){

        if (zombie.getSprite().getY() > ground) {
            applyGravity(delta);
        }
        else{
            currentVelocity.x = 0f;
            currentVelocity.y = 0f;
        }

    }

    private void applyGravity(float delta){
        Vector2 velocity = getVelocity(delta);

        if(zombie.getSprite().getY() + velocity.y > ground) {

            zombie.getSprite().setPosition(zombie.getSprite().getX() + velocity.x, zombie.getSprite().getY() + velocity.y);

        }
        else{

            zombie.getSprite().setPosition(zombie.getSprite().getX() + velocity.x, zombie.getSprite().getY() + (velocity.y + ground + ground - (zombie.getSprite().getY() + velocity.y + ground)));

        }

        if(velocity.x < maxSpeed) {
            currentVelocity.x = velocity.x;
        }
        if(velocity.y < maxSpeed) {
            currentVelocity.y = velocity.y;
        }

    }

    private Vector2 getVelocity(float delta){
        return new Vector2(currentVelocity.x + (zombie.getMass()*gravity.x) * delta, currentVelocity.y + (zombie.getMass()*gravity.y) * delta);
    }


}
