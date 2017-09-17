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
import java.util.HashMap;

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

    private HashMap<Integer, MouseJoint>mouseJoints;

    private Body touchedBody = null;

    public RubeScene scene;

    public Physics(){

        parts = new ArrayList<Part>();
        zombies = new ArrayList<Zombie>();

        mouseJoints = new HashMap<Integer, MouseJoint>();

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
        shape.setAsBox(Gdx.graphics.getWidth()/PPM*2, 0);
        fixtureDef.shape = shape;

        ground = world.createBody(bodyDef);
        ground.createFixture(fixtureDef);
        ground.setTransform(0, 0, 0);

        shape.dispose();
    }

    Vector2 hitPoint = new Vector2();
    QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture (Fixture fixture) {
            // if the hit fixture's body is the ground body
            // we ignore it
            if (fixture.getBody() == ground) return true;

            // if the hit point is inside the fixture of the body
            // we report it
            if (fixture.testPoint(hitPoint
                    .x, hitPoint
                    .y)) {
                touchedBody = fixture.getBody();
                return false;
            } else
                return true;
        }
    };

    public void createMouseJoint(float x, float y, int pointer){
        MouseJointDef mouseJointDef = new MouseJointDef();
        // Needs 2 bodies, first one not used, so we use an arbitrary body.
        // http://www.binarytides.com/mouse-joint-box2d-javascript/
        mouseJointDef.bodyA = ground;
        mouseJointDef.bodyB = touchedBody;
        mouseJointDef.collideConnected = true;
        mouseJointDef.target.set(hitPoint.x, hitPoint.y);
        if(pointer < 1) {
            // Force applied to body to get to point
            mouseJointDef.maxForce = 10000f * touchedBody.getMass();
        }
        else{
            // Force applied to body to get to point
            mouseJointDef.maxForce = 100f * touchedBody.getMass();
        }
        mouseJoints.put(pointer, (MouseJoint) world.createJoint(mouseJointDef));

        touchedBody.setAwake(true);
    }

    public void touchDown(float x, float y, int pointer){

        hitPoint.set(x, y);
        touchedBody = null;
        world.QueryAABB(callback, hitPoint.x - 0.1f, hitPoint.y - 0.1f, hitPoint.x + 0.1f, hitPoint.y + 0.1f);
        if (touchedBody != null) {
            createMouseJoint(x, y, pointer);
        }

    }

    public void touchDragged(float x, float y, int pointer){
        if (mouseJoints.get(pointer) != null) {
            mouseJoints.get(pointer).setTarget(new Vector2(x, y));
        }
        else{
            hitPoint.set(x, y);
            touchedBody = null;
            world.QueryAABB(callback, hitPoint.x - 0.1f, hitPoint.y - 0.1f, hitPoint.x + 0.1f, hitPoint.y + 0.1f);
            if(touchedBody != null) {
                createMouseJoint(x, y, pointer);
            }
        }
    }

    public void touchUp(float x, float y, int pointer){

        // Destroy mouseJoint at a pointer

        if(mouseJoints.get(pointer) != null) {
            world.destroyJoint(mouseJoints.get(pointer));
            mouseJoints.remove(pointer);
        }

        Gdx.app.log("mouse", ""+mouseJoints.get(pointer));


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

}
