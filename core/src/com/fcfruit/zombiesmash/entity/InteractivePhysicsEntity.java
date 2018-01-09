package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by lucas on 2018-01-07.
 */

public class InteractivePhysicsEntity implements InteractiveEntityInterface {

    private boolean isTouching;
    private int pointer;

    private Body physicsBody;
    private Polygon polygon;
    private MouseJoint mouseJoint;
    
    public InteractivePhysicsEntity(Body physicsBody, Polygon polygon){
        this.isTouching = false;
        this.pointer = -1;

        this.physicsBody = physicsBody;
        this.polygon = polygon;
    }
    
    void createMouseJoint(float x, float y){

        /*
        To make dragging faster:

        myObjectBody.applyForceToCenter(new Vector2((float)Math.cos(myMouseDirectionAngle)
        forceYouWantToApply, (float)Math.sin(myMouseDirectionAngle) * forceYouWantToApply));
        */


        MouseJointDef mouseJointDef = new MouseJointDef();
        // Needs 2 bodies, first one not used, so we use an arbitrary body.
        // http://www.binarytides.com/mouse-joint-box2d-javascript/
        mouseJointDef.bodyA = Environment.physics.getGround();
        mouseJointDef.bodyB = this.physicsBody;
        mouseJointDef.collideConnected = true;
        mouseJointDef.target.set(this.physicsBody.getPosition());

        // Makes the joint move body faster
        //mouseJointDef.frequencyHz = 10;

        // Idk what this does, may want to play with it
        // Default is 0.7, I do know that it makes things
        // A lot slower for the mousejoint though when you set it to 10
        //mouseJointDef.dampingRatio = 10;

        // Makes the joint move body faster
        mouseJointDef.dampingRatio = 0.1f;

        // Force applied to body to get to point
        mouseJointDef.maxForce = 10000f * this.physicsBody.getMass();

        // Destroy the current mouseJoint
        if(mouseJoint != null){
            Environment.physics.getWorld().destroyJoint(mouseJoint);
        }
        mouseJoint = (MouseJoint) Environment.physics.getWorld().createJoint(mouseJointDef);
        mouseJoint.setTarget(new Vector2(x, y));

        this.physicsBody.setAwake(true);
    }

    @Override
    public void onTouchDown(float x, float y, int p) {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(x, y, 0)));
        if(polygon.contains(pos.x, Environment.gameCamera.viewportHeight - pos.y)){
            isTouching = true;
            pointer = p;
        }
        if(mouseJoint == null && isTouching) {
            createMouseJoint(x, y);
        }
    }

    @Override
    public void onTouchDragged(float x, float y, int p) {
        if(pointer == p){

        }
    }

    @Override
    public void onTouchUp(float x, float y, int p) {
        if(pointer == p){
            Environment.physics.getWorld().destroyJoint(mouseJoint);
            pointer = -1;
            isTouching = false;
        }
    }
}
