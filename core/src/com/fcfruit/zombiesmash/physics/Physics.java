package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.ZombieSmash;
import com.fcfruit.zombiesmash.rube.RubeScene;



import java.util.ArrayList;
import java.util.HashMap;

import static java.util.Collections.min;

/**
 * Created by Lucas on 2017-07-21.
 */

//Important!!!!!!!!! Maybe add pixels per meter later on! or ppm! https://gamedev.stackexchange.com/questions/87917/box2d-meters-and-pixels

public class Physics
{

    public static final float WIDTH = 10;
    public static final float HEIGHT = 5.625f;

    public static final float PIXELS_PER_METER = ZombieSmash.WIDTH / WIDTH;
    public static final float METERS_PER_PIXEL = 1 / PIXELS_PER_METER;

    public static final float STEP_TIME = 1f / 40f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private Lighting lighting;

    public World world;

    private float accumulator = 0;

    private Body ground;
    private Body roof;
    private Body wall_1;
    private Body wall_2;

    public RubeScene scene;

    public Physics()
    {

        this.world = new World(new Vector2(0, -25), true);
        this.world.setContactListener(new CollisionListener());

        this.world.setContactFilter(new ContactFilter());

        this.constructPhysicsBoundries();

        this.lighting = new Lighting(world);

    }


    public void update(float delta)
    {

        this.stepWorld(delta);

        this.lighting.update();

    }

    private void stepWorld(float delta)
    {
        this.accumulator += Math.min(delta, 0.25f);

        if (this.accumulator >= STEP_TIME)
        {
            this.accumulator -= STEP_TIME;
            this.world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }

    public void constructPhysicsBoundries()
    {
        if (this.ground != null)
        {
            this.world.destroyBody(this.ground);
        }
        if (this.roof != null)
        {
            this.world.destroyBody(this.roof);
        }
        if (this.wall_1 != null)
        {
            this.world.destroyBody(this.wall_1);
        }
        if (this.wall_2 != null)
        {
            this.world.destroyBody(this.wall_2);
        }

        BodyDef walls = new BodyDef();
        walls.type = BodyDef.BodyType.StaticBody;

        FixtureDef wallFixture = new FixtureDef();
        wallFixture.friction = 1;

        PolygonShape wallShape = new PolygonShape();
        wallShape.setAsBox(0, Environment.physicsCamera.viewportHeight * 2);
        wallFixture.shape = wallShape;


        this.wall_1 = this.world.createBody(walls);
        this.wall_1.createFixture(wallFixture);
        this.wall_1.getFixtureList().get(0).setUserData("wall");
        this.wall_1.setTransform(Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2, 0, 0);

        this.wall_2 = this.world.createBody(walls);
        this.wall_2.createFixture(wallFixture);
        this.wall_2.getFixtureList().get(0).setUserData("wall");
        this.wall_2.setTransform(Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth / 2, 0, 0);

        wallShape.dispose();


        BodyDef plane = new BodyDef();
        plane.type = BodyDef.BodyType.StaticBody;

        FixtureDef planeFixture = new FixtureDef();
        planeFixture.friction = 1;

        PolygonShape planeShape = new PolygonShape();
        planeShape.setAsBox(Environment.physicsCamera.viewportWidth, 0);
        planeFixture.shape = planeShape;

        this.ground = this.world.createBody(plane);
        this.ground.createFixture(planeFixture);
        this.ground.getFixtureList().get(0).setUserData("ground");
        this.ground.setTransform(Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2, 0, 0);

        this.roof = this.world.createBody(plane);
        this.roof.createFixture(planeFixture);
        this.roof.getFixtureList().get(0).setUserData("roof");
        this.roof.setTransform(Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2, Environment.physicsCamera.viewportHeight * 2, 0);

        planeShape.dispose();

    }


    public World getWorld()
    {
        return world;
    }

    public Body getGround()
    {
        return ground;
    }

    public Body getRoof()
    {
        return roof;
    }

    public Body getWall_1()
    {
        return wall_1;
    }

    public Body getWall_2()
    {
        return wall_2;
    }


}
