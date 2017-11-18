package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-07-30.
 */

public class Zombie {

    public int id;

    TextureAtlas atlas;
    Skeleton skeleton;
    AnimationState state;

    HashMap<String, Part> parts;

    private MouseJoint getUpMouseJoint = null;

    private float speed = 50;

    // Default direction is left
    private int direction = 0;

    private double timeBeforeAnimate = 5000;

    private double time = 0;

    String currentAnimation;

    boolean physicsEnabled = false;

    boolean hasPowerfulPart = false;

    private boolean isMoving = false;

    private boolean isGettingUp = false;

    private boolean isTouching = false;

    public boolean justTouched = false;


    Zombie(int id) {
        this.id = id;
    }



    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer, float delta){
        for(Slot slot : skeleton.getDrawOrder()){
            if(parts.get(slot.getAttachment().getName()) != null) {
                parts.get(slot.getAttachment().getName()).draw(batch);
            }
        }
        if(!physicsEnabled) {
            skeletonRenderer.draw(batch, skeleton);

        }
        update(delta);
    }

    private void update(float delta){

        updateSkeleton(delta);

        updateParts();

        if(isMoving && !isGettingUp || isTouching){
            time = System.currentTimeMillis();
        }

        if(isTouching){
            if(getUpMouseJoint != null){
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
                getUpMouseJoint = null;
            }
            isGettingUp = false;

            //Make the physics bodies unstuck, sometimes the animation goes into the ground.
            this.setPosition(this.getPosition().x, this.getPosition().y + 0.1f);
            physicsEnabled = true;

        }
        else if(parts.get("head") != null && parts.get("left_leg") != null && parts.get("right_leg") != null) {
            if (physicsEnabled && !isMoving && System.currentTimeMillis() - time >= timeBeforeAnimate) {
                this.getUp();
            } else if (isGettingUp) {
                this.getUp();
            }
        }
        else if(parts.get("head") != null && (parts.get("left_arm") != null || parts.get("right_arm") != null)){
            if (physicsEnabled && !isMoving && System.currentTimeMillis() - time >= timeBeforeAnimate) {
                this.crawl();
            }
        }


        if(!physicsEnabled){
            if(this.direction == 0) {
                skeleton.setPosition(skeleton.getX() + this.speed * Gdx.graphics.getDeltaTime(), skeleton.getY());
            }
            else{
                skeleton.setPosition(skeleton.getX() - this.speed * Gdx.graphics.getDeltaTime(), skeleton.getY());
            }
        }

    }


    private void updateSkeleton(float delta){
        state.update(delta); // Update the animation time.

        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.

        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        for(Slot s : skeleton.getSlots()){
            if(parts.get(s.getData().getName()) == null){
                s.setAttachment(new Attachment("new attachement") {
                    @Override
                    public String getName() {
                        return super.getName();
                    }
                });
            }
        }

        if(!this.currentAnimation.equals(state.getCurrent(0).getAnimation().getName())){
            state.setAnimation(0, currentAnimation, true);
        }

    }

    private void updateParts(){

        // Run update method for each body part
        boolean ispowerfulpart = false;
        boolean istouching = false;
        boolean ismoving = false;
        for(Part p : parts.values()){
            p.update();
            if(p.isPowerfulPart && !ispowerfulpart){
                ispowerfulpart = true;
            }
            if(p.isTouching && !istouching){
                istouching = true;
            }
        }

        // Update body touching state
        hasPowerfulPart = ispowerfulpart;
        justTouched = !isTouching && istouching;
        isTouching = istouching;
        if(parts.get("torso").physicsBody.getLinearVelocity().x > 0.1 || parts.get("torso").physicsBody.getLinearVelocity().y > 0.1 && !ismoving) {
            ismoving = true;
        }
        isMoving = ismoving;

    }


    private void getUp(){

        if (isGettingUp && parts.get("head").physicsBody.getPosition().y >= Environment.gameCamera.viewportHeight - Environment.gameCamera.unproject(new Vector3(0, this.getHeight(new ArrayList<Part>(this.parts.values())) - parts.get("head").sprite.getHeight(), 0)).y){

            isGettingUp = false;
            physicsEnabled = false;
            setPosition(parts.get("torso").physicsBody.getPosition().x, 0);

            if (getUpMouseJoint != null) {
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
                getUpMouseJoint = null;
            }

            // Restart animation
            state.setAnimation(0, currentAnimation, true);

        }

        else if (!isGettingUp){

            MouseJointDef mouseJointDef = new MouseJointDef();

            // Needs 2 bodies, first one not used, so we use an arbitrary body.
            // http://www.binarytides.com/mouse-joint-box2d-javascript/
            mouseJointDef.bodyA = Environment.physics.getGround();
            mouseJointDef.bodyB = parts.get("head").physicsBody;
            mouseJointDef.collideConnected = true;
            mouseJointDef.target.set(parts.get("head").physicsBody.getPosition());

            // The higher the ratio, the slower the movement of body to mousejoint
            mouseJointDef.dampingRatio = 4;

            mouseJointDef.maxForce = 100000f;

            // Destroy the current mouseJoint
            if(getUpMouseJoint != null){
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
            }
            getUpMouseJoint = (MouseJoint) Environment.physics.getWorld().createJoint(mouseJointDef);
            getUpMouseJoint.setTarget(new Vector2(parts.get("torso").physicsBody.getPosition().x, 1.5f));

            isGettingUp = true;

        }

    }

    public void setPosition(float x, float y){
        if(physicsEnabled){
            parts.get("torso").setPosition(x, y);
        }
        else {
            Vector3 pos = Environment.gameCamera.project(new Vector3(x, y, 0));
            skeleton.setPosition(pos.x, pos.y);
            skeleton.updateWorldTransform();
        }




    }

    Vector2 getPosition(){
        if(physicsEnabled){
            return parts.get("torso").physicsBody.getPosition();
        }
        // Else, return animation position
        // Camera doesn't take care of reverse y axis(starting from top)
        Vector3 pos = Environment.gameCamera.unproject(new Vector3(skeleton.getRootBone().getWorldX(), skeleton.getRootBone().getWorldY() + ((RegionAttachment)skeleton.findSlot("torso").getAttachment()).getHeight()/2, 0));
        return new Vector2(pos.x, Environment.gameCamera.viewportHeight - pos.y);
    }


    public Part getPartFromPhysicsBody(Body physicsBody){

        for(Part p : parts.values()){
            if(p.physicsBody.equals(physicsBody)){
                return p;
            }
        }

        return null;

    }

    public Skeleton getSkeleton(){
        return skeleton;
    }

    // Recursion!
    float getHeight(ArrayList<Part> p){
        ArrayList<Part> pCopy = new ArrayList<Part>();
        for(Part prt : p){
            pCopy.add(prt);
        }

        if (p.size() == 0){
            return 0;
        }
        else{
            pCopy.remove(0);
            return getHeight(pCopy) + p.get(0).sprite.getHeight();
        }

    }

    public HashMap<String, Part> getParts(){
        return parts;
    }
    void crawl(){}
    public void constructPhysicsBody(World world){}
    public void animationSetup() {}
    public void destroy(){}



    public void touchDown(float x, float y, int pointer){

        for(String name : parts.keySet()){
            parts.get(name).touchDown(x, y, pointer);
        }

    }

    public void touchDragged(float x, float y, int pointer){

        for(String name : parts.keySet()){
            parts.get(name).touchDragged(x, y, pointer);
        }

    }

    public void touchUp(float x, float y, int pointer){

        for(String name : parts.keySet()){
            parts.get(name).touchUp(x, y, pointer);
        }

    }

}
