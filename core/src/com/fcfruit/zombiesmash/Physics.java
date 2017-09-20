package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import com.fcfruit.zombiesmash.rube.RubeDefaults;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;
import com.fcfruit.zombiesmash.screens.GameScreen;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.Zombie;
import com.fcfruit.zombiesmash.zombies.ZombieBody;


import net.java.games.input.Mouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static java.util.Collections.min;

/**
 * Created by Lucas on 2017-07-21.
 */

//Important!!!!!!!!! Maybe add pixels per meter later on! or ppm! https://gamedev.stackexchange.com/questions/87917/box2d-meters-and-pixels

public class Physics {

    public static final float PPM = 192;

    static final float STEP_TIME = 1f / 60f;
    static final int VELOCITY_ITERATIONS = 6;
    static final int POSITION_ITERATIONS = 2;
    
    private ArrayList<Part> parts;
    private ArrayList<Zombie> zombies;

    private World world;

    private float accumulator = 0;

    private Body ground;


    public RubeScene scene;

    public Physics(){

        parts = new ArrayList<Part>();
        zombies = new ArrayList<Zombie>();

        world = new World(new Vector2(0, -25), true);


        createGround();
    }


    public void update(float delta){
        //if(Gdx.input.isTouched()) {
            updateZombies();
            updateParts();
            stepWorld(delta);
        //}
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
        shape.setAsBox(Environment.gameCamera.viewportWidth*2, 0);
        fixtureDef.shape = shape;

        ground = world.createBody(bodyDef);
        ground.createFixture(fixtureDef);
        ground.setTransform(0, 0, 0);

        shape.dispose();
    }


    public void touchDown(float x, float y, int pointer){

        for(Zombie z : zombies){
            z.touchDown(x, y, pointer);
        }

        for(Part p : parts){
            p.touchDown(x, y, pointer);
        }

    }

    public void touchDragged(float x, float y, int pointer){

        for(Zombie z : zombies){
            z.touchDragged(x, y, pointer);
        }

        for(Part p : parts){
            p.touchDragged(x, y, pointer);
        }

    }

    public void touchUp(float x, float y, int pointer){

        for(Zombie z : zombies){
            z.touchUp(x, y, pointer);
        }

        for(Part p : parts){
            p.touchUp(x, y, pointer);
        }

    }

    private void updateZombies(){

    }

    private void updateParts(){

    }

    public void addBody(Object o){

        if(o instanceof Zombie){
            ((Zombie)o).constructPhysicsBody(world);
            zombies.add((Zombie) o);
        }
        else if (o instanceof Part){
            parts.add((Part)o);
        }
    }

    public void clearBodies(){
        zombies = null;
        parts = null;
    }

    public World getWorld(){
        return world;
    }

    public Body getGround(){
        return ground;
    }

}
