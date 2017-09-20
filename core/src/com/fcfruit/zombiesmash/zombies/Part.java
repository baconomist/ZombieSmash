package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.Physics;
import com.fcfruit.zombiesmash.screens.GameScreen;

/**
 * Created by Lucas on 2017-08-01.
 */

public class Part{

    private String name;

    public Sprite sprite;

    private ZombieBody body;

    public Body physicsBody;

    private Joint bodyJoint;


    private MouseJoint mouseJoint = null;

    private int pointer;

    private boolean touched = false;

    private boolean isPowerfulPart = false;


    public boolean isAttached;

    public Part(String nm, Sprite s, Body b, Joint j, ZombieBody zbody){
        name = nm;

        sprite = s;

        physicsBody = b;

        bodyJoint = j;

        body = zbody;

        isAttached = true;

    }

    public void draw(SpriteBatch batch){
        sprite.draw(batch);
    }

    public void update(){

        Vector3 pos = Environment.gameCamera.project(new Vector3(physicsBody.getPosition().x, physicsBody.getPosition().y, 0));
        sprite.setPosition(pos.x - sprite.getWidth() / 2, pos.y - sprite.getHeight() / 2);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        sprite.setRotation((float) Math.toDegrees(physicsBody.getAngle()));

    }

    Vector2 hitPoint = new Vector2();
    QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture (Fixture fixture) {
            // if the hit fixture's body is the ground body
            // we ignore it
            if (fixture.getBody() == Environment.physics.getGround()) return true;

            // if the hit point is inside the fixture of the body
            // we report it
            if (fixture.testPoint(hitPoint
                    .x, hitPoint
                    .y)) {
                // Simplified if statement
                // If body is fuxture body then touch is true, else false
                touched = fixture.getBody() == physicsBody;
                return false;
            } else
                return true;
        }
    };

    public void createMouseJoint(float x, float y, int p, boolean isTouching){
        MouseJointDef mouseJointDef = new MouseJointDef();
        // Needs 2 bodies, first one not used, so we use an arbitrary body.
        // http://www.binarytides.com/mouse-joint-box2d-javascript/
        mouseJointDef.bodyA = Environment.physics.getGround();
        mouseJointDef.bodyB = physicsBody;
        mouseJointDef.collideConnected = true;
        mouseJointDef.target.set(x, y);
        pointer = p;
        if(isTouching) {
            // Force applied to body to get to point
            mouseJointDef.maxForce = 10000f * physicsBody.getMass();
        }
        else {
            // Force applied to body to get to point
            // Set to less if already touching so you can only rotate other limbs
            mouseJointDef.maxForce = 100f * physicsBody.getMass();
        }
        // Destroy the current mouseJoint
        if(mouseJoint != null){
            Environment.physics.getWorld().destroyJoint(mouseJoint);
        }
        mouseJoint = (MouseJoint) Environment.physics.getWorld().createJoint(mouseJointDef);

        physicsBody.setAwake(true);
    }

    public void touchDown(float x, float y, int p){
        if(mouseJoint == null) {
            hitPoint.set(x, y);
            Environment.physics.getWorld().QueryAABB(callback, hitPoint.x - 0.1f, hitPoint.y - 0.1f, hitPoint.x + 0.1f, hitPoint.y + 0.1f);
            if(isAttached && touched) {
                if(!body.isTouching) {
                    isPowerfulPart = true;
                    createMouseJoint(x, y, p, isPowerfulPart);
                    body.isTouching = true;
                }
                else{
                    isPowerfulPart = false;
                    createMouseJoint(x, y, p, isPowerfulPart);
                }
            }
            else if (touched){
                createMouseJoint(x, y, p, true);
            }
        }

    }

    public void touchDragged(float x, float y, int p){
        if(mouseJoint != null && pointer == p){
            mouseJoint.setTarget(new Vector2(x, y));
        }
    }

    public void touchUp(float x, float y, int p){
        if(mouseJoint != null && pointer == p){
            Environment.physics.getWorld().destroyJoint(mouseJoint);
            mouseJoint = null;
            touched = false;
            if(isPowerfulPart){
                body.isTouching = false;
            }
        }

    }


    public void detach(){
        // Don't want to detach if torso or else you have
        // Joint deletion on a joint that doesn't exist
        if(isAttached && !name.equals("torso")) {
            Environment.physics.getWorld().destroyJoint(bodyJoint);
            bodyJoint = null;
            body.parts.remove(name);
            body = null;
            Environment.physics.addBody(this);
            isAttached = false;
        }
    }

    public String getName(){
        return name;
    }

    public void setPosition(float x, float y){
        physicsBody.setTransform(x, y, physicsBody.getAngle());
    }

}
