package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.Zombie;


import java.util.ArrayList;
import java.util.HashMap;

import box2dLight.DirectionalLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import static java.util.Collections.min;

/**
 * Created by Lucas on 2017-07-21.
 */

//Important!!!!!!!!! Maybe add pixels per meter later on! or ppm! https://gamedev.stackexchange.com/questions/87917/box2d-meters-and-pixels

public class Physics {

    public static final float PIXELS_PER_METER = 192;
    public static final float METERS_PER_PIXEL = 1/192;

    static final float STEP_TIME = 1f / 60f;
    static final int VELOCITY_ITERATIONS = 6;
    static final int POSITION_ITERATIONS = 2;

    ArrayList<Part> parts;
    ArrayList<Zombie> zombies;

    Lighting lighting;

    public World world;

    private float accumulator = 0;

    private Body ground;
    private Body roof;
    private Body wall_1;
    private Body wall_2;

    public RubeScene scene;

    public Physics(){

        parts = new ArrayList<Part>();
        zombies = new ArrayList<Zombie>();

        world = new World(new Vector2(0, -25), true);
        world.setContactListener(new CollisionListener());

        world.setContactFilter(new ContactFilter());

        constructPhysicsBoundries();

        lighting = new Lighting(world);



    }


    public void update(float delta){

        updateParts();
        updateZombies();
        stepWorld(delta);

        lighting.update();

    }

    private void stepWorld(float delta) {
        accumulator += Math.min(delta, 0.25f);

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }

    public void constructPhysicsBoundries(){
        if(ground != null){
            world.destroyBody(ground);
        }
        if(roof != null){
            world.destroyBody(roof);
        }
        if(wall_1 != null){
            world.destroyBody(wall_1);
        }
        if(wall_2 != null){
            world.destroyBody(wall_2);
        }

        BodyDef walls = new BodyDef();
        walls.type = BodyDef.BodyType.StaticBody;

        FixtureDef wallFixture = new FixtureDef();
        wallFixture.friction = 1;

        PolygonShape wallShape = new PolygonShape();
        wallShape.setAsBox(0, Environment.gameCamera.viewportHeight*2);
        wallFixture.shape = wallShape;



        wall_1 = world.createBody(walls);
        wall_1.createFixture(wallFixture);
        wall_1.setTransform(0, 0, 0);

        wall_2 = world.createBody(walls);
        wall_2.createFixture(wallFixture);
        wall_2.setTransform(Environment.gameCamera.viewportWidth, 0, 0);

        wallShape.dispose();



        BodyDef plane = new BodyDef();
        plane.type = BodyDef.BodyType.StaticBody;

        FixtureDef planeFixture = new FixtureDef();
        planeFixture.friction = 1;

        PolygonShape planeShape = new PolygonShape();
        planeShape.setAsBox(Environment.gameCamera.viewportWidth, 0);
        planeFixture.shape = planeShape;



        ground = world.createBody(plane);
        ground.createFixture(planeFixture);
        ground.setTransform(0, 0, 0);

        roof = world.createBody(plane);
        roof.createFixture(planeFixture);
        roof.setTransform(0, Environment.gameCamera.viewportHeight*2, 0);

        planeShape.dispose();
        
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

    private void updateParts(){
        for(Part p : parts){
            p.update();
        }
    }

    private void updateZombies(){

        for(Zombie z : zombies){
            HashMap<String, Part> copy = new HashMap<String, Part>();
            for(HashMap.Entry<String, Part> e: z.getParts().entrySet()){
                copy.put(e.getKey(), e.getValue());
            }
            for(Part p : copy.values()){
                if(p.getState().equals("waiting_for_detach")) {

                    p.detach();

                }
            }
        }
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
        for(Zombie z : zombies){
            z.destroy();
        }
        for(Part p : parts){
            p.destroy();
        }
    }

    public World getWorld(){
        return world;
    }

    public Body getGround(){
        return ground;
    }

    public ArrayList<Part> getParts() {
        return parts;
    }

    public ArrayList<Zombie> getZombies() {
        return zombies;
    }

}
