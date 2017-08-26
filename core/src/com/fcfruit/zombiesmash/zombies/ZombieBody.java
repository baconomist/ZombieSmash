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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lucas on 2017-07-30.
 */

public class ZombieBody{

    public static final float SCALE = 0.6f;

    private static final String[][] jointInfo = {new String[]{"head", "torso"}, new String[]{"left_arm", "torso"},
            new String[]{"right_arm", "torso"}, new String[]{"left_leg", "torso"}, new String[]{"right_leg", "torso"}};

    //https://stackoverflow.com/questions/16839182/can-a-java-array-be-used-as-a-hashmap-key
    private HashMap<List<String>, float[]> jointOffsets;

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
    private HashMap<String, float[]> POLY_OFFSETS;
    private HashMap<String, float[]> PHYS_OFFSETS;
    private HashMap<String, Float> PHYS_ROT_OFFSETS;

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

        //Joint offsets
        //https://stackoverflow.com/questions/16839182/can-a-java-array-be-used-as-a-hashmap-key
        jointOffsets = new HashMap<List<String>, float[]>();
        jointOffsets.put(Collections.unmodifiableList(Arrays.asList("head", "torso")), new float[]{0, parts.get("torso").getHeight()});


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
        POLY_OFFSETS = new HashMap<String, float[]>();
        POLY_OFFSETS.put("head", new float[]{((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth()/2, 0});
        POLY_OFFSETS.put("left_arm", new float[]{0, (parts.get("left_arm").getHeight())*-1});
        POLY_OFFSETS.put("torso", new float[]{parts.get("torso").getHeight()/3, 0});
        POLY_OFFSETS.put("left_arm", new float[]{0, (parts.get("right_arm").getHeight())*-1});

        //Physics
        PHYS_OFFSETS = new HashMap<String, float[]>();
        PHYS_OFFSETS.put("head", new float[]{-10, -10});
        //PHYS_OFFSETS.put("left_arm", POLY_OFFSETS.get("left_arm"));
        PHYS_OFFSETS.put("torso", new float[]{POLY_OFFSETS.get("torso")[0]*-1, PHYS_OFFSETS.get("head")[1]});
        PHYS_OFFSETS.put("right_arm", POLY_OFFSETS.get("right_arm"));
        PHYS_OFFSETS.put("left_leg", new float[]{parts.get("left_leg").getWidth()/2, parts.get("left_leg").getHeight()/10});
        PHYS_OFFSETS.put("right_leg", new float[]{parts.get("right_leg").getWidth()/2, parts.get("left_leg").getHeight()/10});

        // Cus rotation is ccw and I am adding offsets
        PHYS_ROT_OFFSETS = new HashMap<String, Float>();
        PHYS_ROT_OFFSETS.put("head", -50f);
        PHYS_ROT_OFFSETS.put("torso", -90f);
        PHYS_ROT_OFFSETS.put("left_leg", -90f);
        PHYS_ROT_OFFSETS.put("right_leg", -90f);

    }

    private void animationSetup(){
        atlas = new TextureAtlas(Gdx.files.internal("zombies/reg_zombie/reg_zombie.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(SCALE); // Load the skeleton at 60% the size it was in Spine.
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

    public void update(float delta){

        state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

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
        // Run update method for each body part
        for(String partName : parts.keySet()){
            parts.get(partName).update();
        }

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
        // If right arm
        else if (limb.equals("right_arm")){
            parts.get("right_arm").setRotation((float)Math.toDegrees(Math.atan2(parts.get("right_arm").getWorldY() - y, parts.get("right_arm").getWorldX() - x)));

            //Apply changes to skeleton
            skeleton.updateWorldTransform();
        }
        // If torso
        else if (limb.equals("torso")){
            this.setPosition(this.getX() + Gdx.input.getDeltaX(), this.getY() - Gdx.input.getDeltaY());
        }
        // If left arm
        else if (limb.equals("left_arm")){
            // Do not know why this rotation is messed up, but need to add half circle or math.pi to the radians to rotate clockwise
            // Subtracting is rotation ccw
            // For reference:
            // https://stackoverflow.com/questions/9970281/java-calculating-the-angle-between-two-points-in-degrees
            parts.get("left_arm").setRotation((float)Math.toDegrees(Math.PI + Math.atan2(parts.get("left_arm").getWorldY() - y, parts.get("left_arm").getWorldX() - x)));

            //Apply changes to skeleton
            skeleton.updateWorldTransform();
        }
        // If right leg
        else if (limb.equals("right_leg")){
            parts.get("right_leg").setRotation((float)Math.toDegrees(Math.atan2(parts.get("right_leg").getWorldY() - y, parts.get("right_leg").getWorldX() - x)));

            //Apply changes to skeleton
            skeleton.updateWorldTransform();
        }
        // If left leg
        else if (limb.equals("left_leg")){
            // Do not know why this rotation is messed up, but need to add half circle or math.pi to the radians to rotate clockwise
            // Subtracting is rotationg ccw
            // For reference:
            // https://stackoverflow.com/questions/9970281/java-calculating-the-angle-between-two-points-in-degrees
            parts.get("left_leg").setRotation((float)Math.toDegrees(Math.PI + Math.atan2(parts.get("left_leg").getWorldY() - y, parts.get("left_leg").getWorldX() - x)));

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
        Body b;
        for(String partName : parts.keySet()){
            //Bind physics body to part
            b = createBody(partName, parts.get(partName).getWorldX(), parts.get(partName).getWorldY(), (float)Math.toRadians(parts.get(partName).getWorldRotationX()), shapeCache, world);
            parts.get(partName).setPhysicsBody(b);
        }
        // After all physics bodies have been constructed, create joints.
        for(String[] i: jointInfo){
            parts.get(i[0]).createJoint(parts.get(i[1]).getPhysicsBody(), jointOffsets.get(Arrays.asList(i)), world);
            break;
        }

    }

    public Body createBody(String name, float x, float y, float rotation, PhysicsShapeCache shapeCache, World world) {
        Body body = shapeCache.createBody(name, world, SCALE, SCALE);
        body.setTransform(x, y, rotation);

        return body;
    }


    public void setPosition(float x, float y){
        skeleton.setPosition(x, y);

        //Apply changes to skeleton
        skeleton.updateWorldTransform();
    }

    public void setRotation(float degrees){
        skeleton.getRootBone().setRotation(degrees);
    }


    public float[] getPolyOffset(String partName){
        if(POLY_OFFSETS.get(partName) != null) {
            return POLY_OFFSETS.get(partName);
        }
        return new float[]{0, 0};


    }

    public float[] getPhysicsOffset(String partName){
        if(PHYS_OFFSETS.get(partName) != null) {
            return PHYS_OFFSETS.get(partName);
        }
        return new float[]{0, 0};

    }

    public float getRotationOffset(String partName){
        if(PHYS_ROT_OFFSETS.get(partName) != null){
            return PHYS_ROT_OFFSETS.get(partName);
        }
        return 0f;

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
