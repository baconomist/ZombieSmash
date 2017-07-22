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

    private HashMap bodies;

    private float ground = 100;

    public Physics(Game g){

        game = g;

        gravity = new Vector2(0.1f, 5f);

        bodies = new HashMap();
    }

    public void update(float delta){
        for(Object o : bodies.keySet()){
            if(((Zombie) o).getY() > 100){
                act((Zombie) o, delta);
            }
        }
    }

    public void addBody(Zombie z){
        bodies.put(z, z);
    }

    public void removeBody(Zombie z){
        bodies.remove(z);
    }

    private void act(Zombie z, float delta){
        Vector2 velocity = getVelocity(z);
        z.setPosition(z.getX() + (z.getX() * delta * velocity.x), z.getY() + (z.getY() * delta * velocity.y));
    }

    private Vector2 getVelocity(Zombie z){
        return new Vector2(z.getX()*gravity.x, z.getY()*gravity.y);
    }


}
