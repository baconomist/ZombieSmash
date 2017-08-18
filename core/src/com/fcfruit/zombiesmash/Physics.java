package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import com.fcfruit.zombiesmash.screens.GameScreen;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.ZombieBody;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-07-21.
 */

public class Physics {

    static final float SCALE = 0.05f;
    static final float STEP_TIME = 1f / 60f;
    static final int VELOCITY_ITERATIONS = 6;
    static final int POSITION_ITERATIONS = 2;

    private GameScreen gameScreen;

    private ArrayList<ZombieBody> zombieBodies;
    private ArrayList<Part> parts;

    private World world;

    private PhysicsShapeCache physicsBodies;

    private float accumulator = 0;
    private Body ground;

    public Physics(GameScreen gmscrn){
        gameScreen = gmscrn;

        world = new World(new Vector2(0, -120), true);
        physicsBodies = new PhysicsShapeCache("physics.xml");

        createGround();
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

    private void createGround() {
        if (ground != null) world.destroyBody(ground);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 1;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Gdx.graphics.getWidth()*2, 1);
        fixtureDef.shape = shape;

        ground = world.createBody(bodyDef);
        ground.createFixture(fixtureDef);
        ground.setTransform(0, 0, 0);

        shape.dispose();
    }

    private Body createBody(String name, float x, float y, float rotation) {
        Body body = physicsBodies.createBody(name, world, SCALE, SCALE);
        body.setTransform(x, y, rotation);

        return body;
    }

    public void addBody(Object o){

        if(o instanceof ZombieBody){
            if(zombieBodies == null){
                zombieBodies = new ArrayList<ZombieBody>();
            }
            zombieBodies.add((ZombieBody)o);
        }
        else if (o instanceof Part){
            if(parts == null){
                parts = new ArrayList<Part>();
            }
            parts.add((Part)o);
        }
    }

    public void clearBodies(){
        zombieBodies = null;
        parts = null;
    }

}
