package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.Physics;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-07-30.
 */

public class ZombieBody{

    public static final float SCALE = 0.6f;

    private Zombie zombie;

    private TextureAtlas atlas;
    private Skeleton skeleton;
    private AnimationState state;

    private Polygon headBox;
    private Polygon leftArmBox;
    private Polygon torsoBox;
    private Polygon rightArmBox;
    private Polygon leftLegBox;
    private Polygon rightLegBox;

    private HashMap<String, Part> parts;

    // OFFSETS
    private float HEAD_OFFSET_X;
    private float LEFTARM_OFFSET_Y;
    private float TORSO_OFFSET_X;
    private float RIGHTARM_OFFSET_Y;

    public float mass;

    public boolean isPhysicsEnabled;

    public ZombieBody(Zombie z){

        zombie = z;

        mass = 40;

        isPhysicsEnabled = true;

        animationSetup();

        parts = new HashMap<String, Part>();
        parts.put("head", new Part("head", this));
        parts.put("left_arm", new Part("left_arm", this));
        parts.put("torso", new Part("torso", this));
        parts.put("right_arm", new Part("right_arm", this));
        parts.put("left_leg", new Part("left_leg", this));
        parts.put("right_leg", new Part("right_leg", this));


        //Width = height for some limbs.
        //Check atlas file and png.

        headBox = new Polygon();
        headBox.setVertices(new float[]{0, 0, parts.get("head").getWidth(), 0, parts.get("head").getWidth(), parts.get("head").getHeight(), 0, parts.get("head").getHeight()});
        headBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());


        leftArmBox = new Polygon();
        leftArmBox.setVertices(new float[]{0, 0, parts.get("left_arm").getWidth(), 0, parts.get("left_arm").getWidth(), parts.get("left_arm").getHeight(), 0, parts.get("left_arm").getHeight()});
        leftArmBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());

        torsoBox = new Polygon();
        torsoBox.setVertices(new float[]{0, 0, parts.get("torso").getHeight(), 0, parts.get("torso").getHeight(), parts.get("torso").getWidth(), 0, parts.get("torso").getWidth()});
        torsoBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());

        rightArmBox = new Polygon();
        rightArmBox.setVertices(new float[]{0, 0, parts.get("right_arm").getWidth(), 0, parts.get("right_arm").getWidth(), parts.get("right_arm").getHeight(), 0, parts.get("right_arm").getHeight()});
        rightArmBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());

        leftLegBox = new Polygon();
        leftLegBox.setVertices(new float[]{0, 0, parts.get("left_leg").getHeight(), 0, parts.get("left_leg").getHeight(), parts.get("left_leg").getWidth(), 0, parts.get("left_leg").getWidth()});
        leftLegBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());

        rightLegBox = new Polygon();
        rightLegBox.setVertices(new float[]{0, 0, parts.get("right_leg").getHeight(), 0, parts.get("right_leg").getHeight(), parts.get("right_leg").getWidth(), 0, parts.get("right_leg").getWidth()});
        rightLegBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());

        parts.get("head").setPolygon(headBox);
        parts.get("left_arm").setPolygon(leftArmBox);
        parts.get("torso").setPolygon(torsoBox);
        parts.get("right_arm").setPolygon(rightArmBox);
        parts.get("left_leg").setPolygon(leftLegBox);
        parts.get("right_leg").setPolygon(rightLegBox);

        // OFFSETS
        // Negatives are there because I am assuming that you
        // add the offsets not subtract, for consistency.
        HEAD_OFFSET_X = ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth()/2;
        LEFTARM_OFFSET_Y = (parts.get("left_arm").getHeight())*-1;
        TORSO_OFFSET_X = parts.get("torso").getHeight()/3;
        RIGHTARM_OFFSET_Y = (parts.get("right_arm").getHeight())*-1;

    }

    private void animationSetup(){
        atlas = new TextureAtlas(Gdx.files.internal("zombies/reg_zombie/reg_zombie.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(0.6f); // Load the skeleton at 60% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("zombies/reg_zombie/reg_zombie.json"));

        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).

        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.
        //stateData.setMix("run", "jump", 0.2f);
        //stateData.setMix("jump", "run", 0.2f);

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(0.7f); // Slow all animations down to 50% speed.

        // Queue animations on track 0.
        state.setAnimation(0, "run", true);
    }

    public void update(float delta){

        //state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        //parts.get("head").setRotation(-200.1f);
        //skeleton.updateWorldTransform();
        //parts.get("left_am").setRotation(-160);

        //Always use this after any transformation change to skeleton:
        skeleton.updateWorldTransform();

        updateParts();

    }

    private void updateParts(){

        for(String partName : parts.keySet()){
            parts.get(partName).update();
        }

        /*
        // Make polygons follow skeleton bones.
        // Some extra offsets in the positions.

        headBox.setPosition(parts.get("head").getWorldX() + HEADOFFSETX, parts.get("head").getWorldY());
        headBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());
        headBox.setRotation(parts.get("head").getWorldRotationX());

        leftArmBox.setPosition(parts.get("left_arm").getWorldX(), parts.get("left_arm").getWorldY() - LEFTARMOFFSETY);
        leftArmBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());
        leftArmBox.setRotation(parts.get("left_arm").getWorldRotationX());

        torsoBox.setPosition(parts.get("torso").getWorldX() + TORSOOFFSETX, parts.get("torso").getWorldY());
        torsoBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());
        torsoBox.setRotation(parts.get("torso").getWorldRotationX());

        rightArmBox.setPosition(parts.get("right_arm").getWorldX(), parts.get("right_arm").getWorldY() - RIGHTARMOFFSETY);
        rightArmBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());
        rightArmBox.setRotation(parts.get("right_arm").getWorldRotationX());

        leftLegBox.setPosition(parts.get("left_leg").getWorldX(), parts.get("left_leg").getWorldY());
        leftLegBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());
        leftLegBox.setRotation(parts.get("left_leg").getWorldRotationX());

        rightLegBox.setPosition(parts.get("right_leg").getWorldX(), parts.get("right_leg").getWorldY());
        rightLegBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());
        rightLegBox.setRotation(parts.get("right_leg").getWorldRotationX());
        */

    }

    public void hangFromLimb(String limb, float x, float y){

        //Width = height for some limbs.
        //Check atlas file and png.

        //**************
        //SohCahToa
        //y, x is correct!
        //**************

        // If head
        if(limb.equals("head")){
            // Y has to be negative bc deltaY+ is going up
            this.setPosition(this.getX() + Gdx.input.getDeltaX(), this.getY() - Gdx.input.getDeltaY());
        }
        // If left arm
        else if (limb.equals("left_arm")){
            parts.get("left_arm").setRotation((float)Math.toDegrees(Math.atan2(parts.get("left_arm").getWorldY() - y, parts.get("left_arm").getWorldX() - x)));

            //Apply changes to skeleton
            skeleton.updateWorldTransform();
        }
        // If torso
        else if (limb.equals("torso")){
            this.setPosition(this.getX() + Gdx.input.getDeltaX(), this.getY() - Gdx.input.getDeltaY());
        }
        // If right arm
        else if (limb.equals("right_arm")){
            // Do not know why this rotation is messed up, but need to add half circle or math.pi to the radians to rotate clockwise
            // Subtracting is rotation ccw
            // For reference:
            // https://stackoverflow.com/questions/9970281/java-calculating-the-angle-between-two-points-in-degrees
            parts.get("right_arm").setRotation((float)Math.toDegrees(Math.PI + Math.atan2(parts.get("right_arm").getWorldY() - y, parts.get("right_arm").getWorldX() - x)));

            //Apply changes to skeleton
            skeleton.updateWorldTransform();
        }
        // If left leg
        else if (limb.equals("left_leg")){
            parts.get("left_leg").setRotation((float)Math.toDegrees(Math.atan2(parts.get("left_leg").getWorldY() - y, parts.get("left_leg").getWorldX() - x)));

            //Apply changes to skeleton
            skeleton.updateWorldTransform();
        }
        // If right leg
        else if (limb.equals("right_leg")){
            // Do not know why this rotation is messed up, but need to add half circle or math.pi to the radians to rotate clockwise
            // Subtracting is rotationg ccw
            // For reference:
            // https://stackoverflow.com/questions/9970281/java-calculating-the-angle-between-two-points-in-degrees
            parts.get("right_leg").setRotation((float)Math.toDegrees(Math.PI + Math.atan2(parts.get("right_leg").getWorldY() - y, parts.get("right_leg").getWorldX() - x)));

            //Apply changes to skeleton
            skeleton.updateWorldTransform();
        }

    }


    /*public boolean contains(float x, float y){

        return x > parts.get("left_arm").getWorldX() && x < parts.get("right_arm").getWorldX() && y > parts.get("left_leg").getWorldY() - ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth() && y < parts.get("head").getWorldY() + ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getHeight();

    }*/

    public String getTouchedLimb(){

        // Probably best method for touch detection, works with rotation, contains() doesn't

        // If statements can't be in order, should be in smallest to largest order for detection purposes

        // Create a rectangle/square that will be used for touch detection
        Polygon touchPolygon = new Polygon(new float[]{0, 0, 1, 0, 1, 1, 0, 1});
        touchPolygon.setPosition(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

        if(Intersector.overlapConvexPolygons(rightArmBox, touchPolygon)){
            return "right_arm";
        }
        else if(Intersector.overlapConvexPolygons(rightLegBox, touchPolygon)){
            return "right_leg";
        }
        else if (Intersector.overlapConvexPolygons(leftArmBox, touchPolygon)) {
            return "left_arm";
        }
        else if(Intersector.overlapConvexPolygons(leftLegBox, touchPolygon)){
            return "left_leg";
        }
        else if(Intersector.overlapConvexPolygons(torsoBox, touchPolygon)){
            return "torso";
        }
        else if (Intersector.overlapConvexPolygons(headBox, touchPolygon)){
            return "head";
        }

        return "none";

    }

    public void constructPhysicsBodies(PhysicsShapeCache shapeCache, World world){
        for(String partName : parts.keySet()){
            //Bind physics body to part, maybe a part.setPhysicsBody() method***************************************************fdsgdbjkhagfiqbhfiklhghfkbfkahjb flkiabnfaskfl
        }
    }

    public Body createBody(String name, float x, float y, float rotation, PhysicsShapeCache shapeCache, World world) {
        Body body = shapeCache.createBody(name, world, SCALE, SCALE);
        body.setTransform(x, y, rotation);

        return body;
    }

    public float[] getOffset(String partName){
        if(partName.equals("head")){
            return new float[]{HEAD_OFFSET_X, 0};
        }
        else if(partName.equals("left_arm")){
            return new float[]{0, LEFTARM_OFFSET_Y};
        }
        else if(partName.equals("torso")){
            return new float[]{TORSO_OFFSET_X, 0};
        }
        else if(partName.equals("right_arm")){
            return new float[]{0, RIGHTARM_OFFSET_Y};
        }
        return new float[]{0, 0};
    }

    public Skeleton getSkeleton(){
        return skeleton;
    }

    public float getX(){
        return skeleton.getX();
    }

    public float getY(){
        return skeleton.getY();

    }

    public void setPosition(float x, float y){
        skeleton.setPosition(x, y);

        //Apply changes to skeleton
        skeleton.updateWorldTransform();
    }

    public void setRotation(float degrees){
        skeleton.getRootBone().setRotation(degrees);
    }

    public float getRotation(){
        return skeleton.getRootBone().getWorldRotationX();
    }

    public float getWidth(){
        return skeleton.getData().getWidth();
    }

    public float getHeight(){
        return skeleton.getData().getHeight();
    }

    public HashMap<String, Part> getParts(){
        return parts;
    }

}
