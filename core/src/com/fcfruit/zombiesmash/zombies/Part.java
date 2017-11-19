package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by Lucas on 2017-08-01.
 */

public class Part{

    private String name;

    public Sprite sprite;

    public Zombie body;

    public Body physicsBody;

    public Joint bodyJoint;

    private MouseJoint mouseJoint = null;

    private int pointer;

    public boolean isTouching = false;

    public boolean isPowerfulPart = false;

    String state;

    public Part(String nm, Sprite s, Body b, Joint j, Zombie zbody){
        name = nm;

        sprite = s;

        physicsBody = b;

        bodyJoint = j;

        body = zbody;

        state = "attached";

    }

    public void draw(SpriteBatch batch){
        if(state.equals("attached")) {
            if (body.physicsEnabled) {
                sprite.draw(batch);
            }
        }
        else{
            sprite.draw(batch);
        }
    }

    public void update(){


        if(state.equals("attached") && !body.physicsEnabled) {
            physicsBody.setAwake(false);

            if(name.contains("arm")){

                Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(body.getSkeleton().findBone(name).getWorldX() + sprite.getWidth()/4,
                        body.getSkeleton().findBone(name).getWorldY() - sprite.getHeight()/2, 0)));

                float rot = (float) Math.toRadians(body.getSkeleton().findBone(name).getWorldRotationX());

                physicsBody.setTransform(pos.x, Environment.physicsCamera.viewportHeight - pos.y, rot);

            }

            else if(name.contains("leg")){

                Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(body.getSkeleton().findBone(name).getWorldX(),
                        body.getSkeleton().findBone(name).getWorldY() - sprite.getHeight()/2, 0)));

                // -180 degrees cus the api is messed up
                float rot = (float) Math.toRadians(body.getSkeleton().findBone(name).getWorldRotationX() - 180);

                physicsBody.setTransform(pos.x, Environment.physicsCamera.viewportHeight - pos.y, rot);

            }

            else{

                Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(body.getSkeleton().findBone(name).getWorldX(),
                        body.getSkeleton().findBone(name).getWorldY() + sprite.getHeight()/2, 0)));

                float rot = (float) Math.toRadians(body.getSkeleton().findBone(name).getWorldRotationX());


                physicsBody.setTransform(pos.x, Environment.physicsCamera.viewportHeight - pos.y, rot);

            }



        }

        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(physicsBody.getPosition().x, physicsBody.getPosition().y, 0)));
        sprite.setPosition(pos.x - sprite.getWidth() / 2, Environment.gameCamera.viewportHeight - pos.y - sprite.getHeight()/2);
        sprite.setRotation((float) Math.toDegrees(physicsBody.getAngle()));

    }

    Vector2 hitPoint = new Vector2();
    QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture (Fixture fixture) {

            // if the hit fixture's body is the ground body
            // we ignore it
            if (fixture.getBody().getType() == BodyDef.BodyType.StaticBody) return true;

            // if the hit point is inside the fixture of the body
            // we report it
            if (fixture.testPoint(hitPoint
                    .x, hitPoint
                    .y)) {
                // Simplified if statement
                // If body is fixture body then touch is true, else false
                isTouching = fixture.getBody() == physicsBody;
                return false;
            } else
                return true;
        }
    };

    public void createMouseJoint(float x, float y, int p, boolean isPowerful){

        /*
        To make dragging faster:

        myObjectBody.applyForceToCenter(new Vector2((float)Math.cos(myMouseDirectionAngle)
        forceYouWantToApply, (float)Math.sin(myMouseDirectionAngle) * forceYouWantToApply));
        */


        MouseJointDef mouseJointDef = new MouseJointDef();
        // Needs 2 bodies, first one not used, so we use an arbitrary body.
        // http://www.binarytides.com/mouse-joint-box2d-javascript/
        mouseJointDef.bodyA = Environment.physics.getGround();
        mouseJointDef.bodyB = physicsBody;
        mouseJointDef.collideConnected = true;
        mouseJointDef.target.set(x, y);

        // Makes the joint move body faster
        //mouseJointDef.frequencyHz = 10;

        // Idk what this does, may want to play with it
        // Default is 0.7, I do know that it makes things
        // A lot slower for the mousejoint though when you set it to 10
        //mouseJointDef.dampingRatio = 10;

        // Makes the joint move body faster
        mouseJointDef.dampingRatio = 0.1f;

        pointer = p;
        if(isPowerful) {
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
            if(state.equals("attached") && isTouching) {
                if(!body.hasPowerfulPart) {
                    isPowerfulPart = true;
                    createMouseJoint(x, y, p, isPowerfulPart);
                }
                else{
                    isPowerfulPart = false;
                    createMouseJoint(x, y, p, isPowerfulPart);
                }
            }
            else if (isTouching){
                createMouseJoint(x, y, p, true);
            }
        }

    }

    public void touchDragged(float x, float y, int p){

        if (mouseJoint != null && pointer == p) {
            mouseJoint.setTarget(new Vector2(x, y));
            if(state.equals("attached")){
                if(!body.hasPowerfulPart){
                    mouseJoint.setMaxForce(10000f * physicsBody.getMass());
                }
            }
        }


    }

    public void touchUp(float x, float y, int p){

        if(mouseJoint != null && pointer == p){

            Environment.physics.getWorld().destroyJoint(mouseJoint);
            mouseJoint = null;
            isTouching = false;
            isPowerfulPart = false;

        }


    }


    public void detach(){
        // Don't want to detach if torso or else you have
        // Joint deletion on a joint that doesn't exist

        /*if(name.equals("torso")){
            body.parts.remove(name);
            for(String n : body.parts.keySet()){
                body.parts.get(n).detach();
                body.parts.remove(n);
            }
            bodyJoint = null;
            body.destroy();
            body = null;
        }*/

        Environment.physics.getWorld().destroyJoint(bodyJoint);

        bodyJoint = null;
        body.parts.remove(name);
        body = null;
        Environment.physics.addBody(this);
        setState("detached");

    }

    public String getName(){
        return name;
    }

    public String getState(){return state;}

    public void setPosition(float x, float y){
        physicsBody.setTransform(x, y, physicsBody.getAngle());
    }

    public void setState(String s){
        if(s.equals("waiting_for_detach")){
            state = s;
        }
        else if(s.equals("detached")){
            state = s;
        }
    }

    public void destroy(){

    }

}
