package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import com.fcfruit.zombiesmash.zombies.ZombieBody;

/**
 * Created by Lucas on 2017-07-21.
 */

public class Physics {

    static final float STEP_TIME = 1f / 60f;
    static final int VELOCITY_ITERATIONS = 6;
    static final int POSITION_ITERATIONS = 2;

    private World world;

    private Box2DDebugRenderer debugRenderer;
    private PhysicsShapeCache physicsBodies;

    private float accumulator = 0;
    private Body ground;

    private Body body;


    public Physics(){
        world = new World(new Vector2(0, -120), true);
        //physicsBodies = new PhysicsShapeCache("physics.xml");
    }

    public void update(float delta){
        stepWorld(delta);
    }

    private void stepWorld(float delta) {
        accumulator += Math.min(delta, 0.25f);

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }

    public void addBody(Body b){

    }

    public void removeBody(Body b){

    }

}
