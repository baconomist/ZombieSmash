package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Lucas on 2017-07-21.
 */

public class Physics {

    public Game game;

    private Vector2 gravity;

    public Physics(Game g){
        game = g;

        gravity = new Vector2(0.1f, 5f);

    }

    public void update(float delta){

    }

}
