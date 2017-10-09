package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.Physics;
import com.fcfruit.zombiesmash.rube.RubeDefaults;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.*;
import com.fcfruit.zombiesmash.screens.GameScreen;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lucas on 2017-07-30.
 */

public class ZombieBody{
    private static float ANIMSCALE;

    private Zombie zombie;

    private RubeScene rubeScene;

    private TextureAtlas atlas;
    private Skeleton skeleton;
    private AnimationState state;

    public HashMap<String, Part> parts;

    private MouseJoint getUpMouseJoint = null;

    private double timeBeforeAnimate = 500;

    private double time = 0;

    public boolean physicsEnabled = false;

    public boolean hasPowerfulPart = false;

    public boolean isMoving = false;
    
    public boolean isAnimating = true;

    public boolean isGettingUp = false;

    public boolean isTouching = false;

    public ZombieBody(Zombie z){

        zombie = z;

        animationSetup();

    }

    private void animationSetup(){
        atlas = new TextureAtlas(Gdx.files.internal("zombies/reg_zombie/reg_zombie.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("zombies/reg_zombie/reg_zombie.json"));

        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.
        //stateData.setMix("run", "jump", 0.2f);
        //stateData.setMix("jump", "run", 0.2f);

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

        // Queue animations on track 0.
        state.setAnimation(0, "run", true);

    }

    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer, float delta){
        for(Slot slot : skeleton.getDrawOrder()){
            if(parts.get(slot.getAttachment().getName()) != null) {
                parts.get(slot.getAttachment().getName()).draw(batch);
            }
        }
        skeletonRenderer.draw(batch, skeleton);
        update(delta);
    }

    private void update(float delta){

        state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.

        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        //parts.get("head").setRotation(-200.1f);
        //skeleton.updateWorldTransform();
        //parts.get("left_am").setRotation(-160);

        //Always use this after any transformation change to skeleton:
        //skeleton.updateWorldTransform();

        updateParts();

        if(isTouching){
            time = System.currentTimeMillis();
            if(getUpMouseJoint != null){
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
                getUpMouseJoint = null;
            }
            isAnimating = false;
            isGettingUp = false;
            physicsEnabled = true;
        }
        else if(!isAnimating && !isMoving && System.currentTimeMillis() - time >= timeBeforeAnimate){
            getUp();
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
            if(p.physicsBody.getLinearVelocity().x > 0.1 || p.physicsBody.getLinearVelocity().y > 0.1 && !ismoving) {
                ismoving = true;
            }
        }

        // Update body touching state
        hasPowerfulPart = ispowerfulpart;
        isTouching = istouching;
        isMoving = ismoving;

    }

    public void constructPhysicsBody(World world){
        RubeSceneLoader loader = new RubeSceneLoader(world);
        rubeScene = loader.loadScene(Gdx.files.internal("zombies/reg_zombie/reg_zombie_rube.json"));

        parts = new HashMap<String, Part>();

        for(Body b : rubeScene.getBodies()) {

            String bodyName = (String) rubeScene.getCustom(b, "name");

                Sprite sprite = new Sprite(atlas.findRegion(bodyName));

                for (RubeImage i : rubeScene.getImages()) {
                    if (i.body == b) {
                        sprite.flip(i.flip, false);
                        sprite.setColor(i.color);
                        sprite.setOrigin(i.center.x, i.center.y);
                        float width = sprite.getWidth();
                        sprite.setSize(i.width * Physics.PPM, i.height * Physics.PPM);
                        ANIMSCALE = sprite.getWidth()/width;
                    }

                }

                Joint joint = null;
                for (Joint j : rubeScene.getJoints()) {
                    if (j.getBodyA() == b || j.getBodyB() == b) {
                        joint = j;
                        break;
                    }
                }

                parts.put(bodyName, new Part(bodyName, sprite, b, joint, this));

        }
        skeleton.getRootBone().setScale(ANIMSCALE);
    }

    private void getUp(){

        if(isGettingUp && parts.get("head").physicsBody.getPosition().y >= 1.15){
            isAnimating = true;
            isGettingUp = false;
            Vector3 pos = Environment.gameCamera.project(new Vector3(parts.get("torso").physicsBody.getPosition(), 0));
            Gdx.app.log("posy", ""+pos.y);
            //skeleton.setPosition(pos.x, Environment.gameCamera.viewportHeight*Physics.PPM - pos.y);
            //skeleton.updateWorldTransform();
            physicsEnabled = false;
            if(getUpMouseJoint != null){
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
                getUpMouseJoint = null;
            }
        }

        else if(!isGettingUp) {
            
            MouseJointDef mouseJointDef = new MouseJointDef();
            
            // Needs 2 bodies, first one not used, so we use an arbitrary body.
            // http://www.binarytides.com/mouse-joint-box2d-javascript/
            mouseJointDef.bodyA = Environment.physics.getGround();
            mouseJointDef.bodyB = parts.get("head").physicsBody;
            mouseJointDef.collideConnected = true;
            mouseJointDef.target.set(parts.get("head").physicsBody.getPosition());

            // Makes the joint move body slower
            mouseJointDef.dampingRatio = 20;

            mouseJointDef.maxForce = 100000f;

            // Destroy the current mouseJoint
            if(getUpMouseJoint != null){
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
            }
            getUpMouseJoint = (MouseJoint) Environment.physics.getWorld().createJoint(mouseJointDef);
            getUpMouseJoint.setTarget(new Vector2(parts.get("left_leg").physicsBody.getPosition().x, 1.5f));

            isGettingUp = true;

            Gdx.app.log("joint", "jjj"+getUpMouseJoint);
        }
        if(isGettingUp) {
            for (Part p : parts.values()) {
                p.physicsBody.setAwake(true);
            }
        }

    }



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

    public Vector2 getPosition(){
        if(physicsEnabled){
            return parts.get("torso").physicsBody.getPosition();
        }
        // Else, return animation position
        // Camera doesn't take care of reverse y axis(starting from top)
        Vector3 pos = Environment.gameCamera.unproject(new Vector3(skeleton.getRootBone().getWorldX(), skeleton.getRootBone().getWorldY() + ((RegionAttachment)skeleton.findSlot("torso").getAttachment()).getHeight()/2, 0));
        return new Vector2(pos.x, Environment.gameCamera.viewportHeight - pos.y);
    }

    public Array<Body> getPhysicsBodies(){
        return rubeScene.getBodies();
    }

    public Skeleton getSkeleton(){
        return skeleton;
    }

    public float getMass(){
        float mass = 0;
        for(Part p : parts.values()){
            mass = mass + p.physicsBody.getMass();
        }
        return mass;
    }

    public Part getPartFromPhysicsBody(Body physicsBody){

        for(Part p : parts.values()){
            if(p.physicsBody.equals(physicsBody)){
                return p;
            }
        }

        return null;

    }

    public HashMap<String, Part> getParts(){
        return parts;
    }

    public void destroy(){

    }


}
