package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.ZombieSmash;
import com.fcfruit.zombiesmash.rube.RubeScene;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-07-21.
 */

//Important!!!!!!!!! Maybe add pixels per meter later on! or ppm! https://gamedev.stackexchange.com/questions/87917/box2d-meters-and-pixels

public class Physics
{

    public static final float WIDTH = 20f;
    public static final float HEIGHT = 11.25f;

    public static final float PIXELS_PER_METER = ZombieSmash.WIDTH / WIDTH;
    public static final float METERS_PER_PIXEL = 1 / PIXELS_PER_METER;

    public static final float STEP_TIME = 1f / 30f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private Lighting lighting;

    private World world;

    private float accumulator = 0;

    private ArrayList<Body> groundBodies;
    private Body roof;
    //private Body wall_1;
    //private Body wall_2;

    public RubeScene scene;

    public Physics()
    {

        this.world = new World(new Vector2(0, -25), true);
        this.world.setContactListener(new CollisionListener());

        this.world.setContactFilter(new ContactFilter());

        this.constructPhysicsBoundaries();

        this.lighting = new Lighting(world);

    }


    public void draw()
    {
        this.lighting.draw();
    }

    public void update(float delta)
    {

        for (com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface detachableEntityInterface : Environment.detachableEntityDetachQueue)
        {
            // Double check waiting for detach I guess...
            if (detachableEntityInterface.getState().equals("waiting_for_detach"))
            {
                detachableEntityInterface.detach();
            }
        }
        Environment.detachableEntityDetachQueue.clear();

        for (com.fcfruit.zombiesmash.entity.interfaces.ExplodableEntityInterface explodableEntityInterface : Environment.explodableEntityQueue)
        {
            explodableEntityInterface.explode();
        }
        Environment.explodableEntityQueue.clear();

        for (Joint joint : Environment.jointDestroyQueue)
        {
            this.destroyJoint(joint);
        }
        Environment.jointDestroyQueue.clear();

        this.lighting.update();

        this.stepWorld(delta);

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

    public void constructPhysicsBoundaries()
    {

        if (this.groundBodies != null)
        {
            for (Body ground : this.groundBodies)
            {
                this.world.destroyBody(ground);
            }
        }
        if (this.roof != null)
        {
            this.world.destroyBody(this.roof);
        }


        BodyDef plane = new BodyDef();
        plane.type = BodyDef.BodyType.StaticBody;

        FixtureDef planeFixture = new FixtureDef();
        planeFixture.friction = 1;

        PolygonShape planeShape = new PolygonShape();
        planeShape.setAsBox(Environment.physicsCamera.viewportWidth * 10, 0);
        planeFixture.shape = planeShape;
        planeFixture.restitution = 0.4f;
        planeFixture.friction = 0.4f;

        this.groundBodies = new ArrayList<Body>();
        for (float i = 0f, c = 0; c < 3; i += 0.3f, c++)
        {
            Body ground = this.world.createBody(plane);
            ground.createFixture(planeFixture);
            ground.getFixtureList().get(0).setUserData(new PhysicsData("ground"));
            ground.setTransform(Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2, i, 0);
            this.groundBodies.add(ground);
        }


        this.roof = this.world.createBody(plane);
        this.roof.createFixture(planeFixture);
        this.roof.getFixtureList().get(0).setUserData(new PhysicsData("ground"));
        this.roof.setTransform(Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2, Environment.physicsCamera.viewportHeight * 2, 0);

        planeShape.dispose();

                /*
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
        */

    }

    public ArrayList<Body> getGroundBodies()
    {
        return groundBodies;
    }

    public int whichGround(Body ground)
    {
        assert this.groundBodies.contains(ground);
        int i = 0;
        for (Body g : this.groundBodies)
        {
            if (g.equals(ground)) return i;
            i++;
        }
        return 0;
    }

    public Body getRoof()
    {
        return roof;
    }

    /*public Body getWall_1()
    {
        return wall_1;
    }

    /public Body getWall_2()
    {
        return wall_2;
    }*/

    /**
     * getWorld() -> returns this.world
     * the world instance returned by this function should not be used to manipulate the world
     * instead use Physics.createBody(), Physics.createJoint(), Physics.destroyBody(), Physics.destroyJoint()
     * **/
    public World getWorld()
    {
        return this.world;
    }

    public void destroyBody(Body body)
    {
        if (Environment.isMethodInStack("stepWorld")) throw new Error("Don't call physics methods in the world time step!");

        //Gdx.app.debug("destroyBody", ""+ Arrays.toString(Thread.currentThread().getStackTrace()));
        //Gdx.app.debug("body", ""+body);

        if (this.get_world_bodies().contains(body, true))
            this.world.destroyBody(body);

    }

    public void destroyJoint(Joint joint)
    {
        if (Environment.isMethodInStack("stepWorld")) throw new Error("Don't call physics methods in the world time step!");

        //Gdx.app.debug("destroyJoint", ""+ Arrays.toString(Thread.currentThread().getStackTrace()));
        //Gdx.app.debug("joint", ""+joint);

        if (this.get_world_joints().contains(joint, true))
            this.world.destroyJoint(joint);
    }

    public Body createBody(BodyDef bodyDef)
    {
        if (Environment.isMethodInStack("stepWorld")) throw new Error("Don't call physics methods in the world time step!");
        return this.world.createBody(bodyDef);
    }

    public Joint createJoint(JointDef jointDef)
    {
        if (Environment.isMethodInStack("stepWorld")) throw new Error("Don't call physics methods in the world time step!");
        return this.world.createJoint(jointDef);
    }

    public Array<Body> get_world_bodies()
    {
        if (Environment.isMethodInStack("stepWorld")) throw new Error("Don't call physics methods in the world time step!");
        Array<Body> world_bodies = new Array<Body>();
        this.world.getBodies(world_bodies);

        return world_bodies;
    }

    public Array<Joint> get_world_joints()
    {
        if (Environment.isMethodInStack("stepWorld")) throw new Error("Don't call physics methods in the world time step!");
        Array<Joint> world_joints = new Array<Joint>();
        this.world.getJoints(world_joints);

        return world_joints;
    }

}
