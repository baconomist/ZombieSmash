package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;
import com.esotericsoftware.spine.Skin;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.AttachmentType;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.BodyPhysics;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-07-30.
 */

public class Body{

    private BodyPhysics physics;

    public TextureAtlas atlas;
    public Skeleton skeleton;
    public AnimationState state;

    private Polygon headBox;
    private Polygon leftArmBox;
    private Polygon torsoBox;
    private Polygon rightArmBox;
    private Polygon leftLegBox;
    private Polygon rightLegBox;

    private ShapeRenderer shapeRenderer;

    public float mass;

    public boolean isPhysicsEnabled;

    public boolean isGravityEnabled;

    public boolean isRagdollEnabled;

    public boolean isHanging;


    public Body(Zombie z){

        physics = new BodyPhysics(this);

        mass = 40;

        isPhysicsEnabled = true;

        isGravityEnabled = true;

        isRagdollEnabled = true;

        atlas = new TextureAtlas(Gdx.files.internal("zombies/reg_zombie/reg_zombie.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 60% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("zombies/reg_zombie/reg_zombie.json"));

        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).

        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.
        //stateData.setMix("run", "jump", 0.2f);
        //stateData.setMix("jump", "run", 0.2f);



        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(0.7f); // Slow all animations down to 50% speed.

        // Queue animations on track 0.
        state.setAnimation(0, "run", true);

        //Width = height for some limbs.
        //Check atlas file and png.

        headBox = new Polygon();
        headBox.setVertices(new float[]{0, 0, ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth(), 0, ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth(), ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getHeight(), 0, ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getHeight()});
        
        leftArmBox = new Polygon();
        leftArmBox.setVertices(new float[]{0, 0, ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getWidth(), 0, ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getWidth(), ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight(), 0, ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight()});
        leftArmBox.setOrigin(((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getWidth()/2, ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight()/2);

        torsoBox = new Polygon();
        torsoBox.setVertices(new float[]{0, 0, ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getWidth(), 0, ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getWidth(), ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getHeight(), 0, ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getHeight()});
        
        rightArmBox = new Polygon();
        rightArmBox.setVertices(new float[]{0, 0, ((RegionAttachment)skeleton.findSlot("right_arm").getAttachment()).getWidth(), 0, ((RegionAttachment)skeleton.findSlot("right_arm").getAttachment()).getWidth(), ((RegionAttachment)skeleton.findSlot("right_arm").getAttachment()).getHeight(), 0, ((RegionAttachment)skeleton.findSlot("right_arm").getAttachment()).getHeight()});
        rightArmBox.setOrigin(((RegionAttachment)skeleton.findSlot("right_arm").getAttachment()).getWidth()/2, ((RegionAttachment)skeleton.findSlot("right_arm").getAttachment()).getHeight()/2);


        leftLegBox = new Polygon();
        leftLegBox.setVertices(new float[]{0, 0, ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getHeight(), 0, ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getHeight(), ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getWidth(), 0, ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getWidth()});
        leftLegBox.setOrigin(((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getHeight()/2, ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getWidth()/2);

        rightLegBox = new Polygon();
        rightLegBox.setVertices(new float[]{0, 0, ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getHeight(), 0, ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getHeight(), ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getWidth(), 0, ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getWidth()});
        rightLegBox.setOrigin(((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getHeight()/2, ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getWidth()/2);

        shapeRenderer = new ShapeRenderer();

    }

    public void update(float delta){

        state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        physics.update(delta); // Update physics.

        //skeleton.findBone("head").setRotation(-200.1f);
        //skeleton.updateWorldTransform();
        //skeleton.findBone("left_arm").setRotation(-160);

        //Always use this after any transformation change to skeleton:
        skeleton.updateWorldTransform();


        // Make polygons follow skeleton bones.
        // Some extra offsets in the positions.

        headBox.setPosition(skeleton.findBone("head").getWorldX() - ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth()/2, skeleton.findBone("head").getWorldY());
        headBox.setRotation(skeleton.findBone("head").getRotation());

        leftArmBox.setPosition(skeleton.findBone("left_arm").getWorldX(), skeleton.findBone("left_arm").getWorldY() - ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight());
        leftArmBox.setRotation(skeleton.findBone("left_arm").getRotation());

        torsoBox.setPosition(skeleton.findBone("body").getWorldX() - ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getHeight()/3, skeleton.findBone("body").getWorldY());

        rightArmBox.setPosition(skeleton.findBone("right_arm").getWorldX(), skeleton.findBone("right_arm").getWorldY() - ((RegionAttachment)skeleton.findSlot("right_arm").getAttachment()).getHeight());
        rightArmBox.setRotation(skeleton.findBone("right_arm").getRotation());

        leftLegBox.setPosition(skeleton.findBone("left_leg").getWorldX() - ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getHeight()/2, skeleton.findBone("left_leg").getWorldY() - ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getWidth() - ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getWidth()/2.5f);
        leftLegBox.setRotation(skeleton.findBone("left_leg").getRotation());

        rightLegBox.setPosition(skeleton.findBone("right_leg").getWorldX() - ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getHeight()/2, skeleton.findBone("right_leg").getWorldY() - ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getWidth() - ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getWidth()/2.5f);
        rightLegBox.setRotation(skeleton.findBone("right_leg").getRotation());

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.polygon(headBox.getTransformedVertices());
        shapeRenderer.polygon(leftArmBox.getTransformedVertices());
        shapeRenderer.polygon(torsoBox.getTransformedVertices());
        shapeRenderer.polygon(rightArmBox.getTransformedVertices());
        shapeRenderer.polygon(leftLegBox.getTransformedVertices());
        shapeRenderer.polygon(rightLegBox.getTransformedVertices());
        shapeRenderer.end();

    }

    public void hangFromLimb(int limb, float x, float y){
        // Use the hash map limbs for limbs reference!

        //Width = height for some limbs.
        //Check atlas file and png.

        //**************
        //SohCahToa
        //y, x is correct!
        //**************

        Gdx.app.log("aaa", ""+(float)Math.toDegrees(Math.atan2(this.getY() - y, this.getX() - x)));

        // If head
        if(limb == 1){
            this.setPosition(this.getX() + Gdx.input.getDeltaX(), this.getY() - Gdx.input.getDeltaY());
        }
        // If left arm
        else if (limb == 2){
            skeleton.findBone("left_arm").setRotation((float)Math.toDegrees(Math.atan2(skeleton.findBone("left_arm").getWorldY() - y, skeleton.findBone("left_arm").getWorldX() - x)));

            //Apply changes to skeleton
            skeleton.updateWorldTransform();
        }
        // If torso
        else if (limb == 3){
            this.setPosition(this.getX() + Gdx.input.getDeltaX(), this.getY() - Gdx.input.getDeltaY());
        }
        // If right arm
        else if (limb == 4){
            // Do not know why this rotation is messed up, but need to add half circle or math.pi to the radians to rotate clockwise
            // Subtracting is rotationg ccw
            // For reference:
            // https://stackoverflow.com/questions/9970281/java-calculating-the-angle-between-two-points-in-degrees
            skeleton.findBone("right_arm").setRotation((float)Math.toDegrees(Math.PI + Math.atan2(skeleton.findBone("right_arm").getWorldY() - y, skeleton.findBone("right_arm").getWorldX() - x)));

            //Apply changes to skeleton
            skeleton.updateWorldTransform();
        }
        // If left leg
        else if (limb == 5){
            skeleton.findBone("left_leg").setRotation((float)Math.toDegrees(Math.atan2(skeleton.findBone("left_leg").getWorldY() - y, skeleton.findBone("left_leg").getWorldX() - x)));

            //Apply changes to skeleton
            skeleton.updateWorldTransform();
        }
        // If right leg
        else if (limb == 6){
            // Do not know why this rotation is messed up, but need to add half circle or math.pi to the radians to rotate clockwise
            // Subtracting is rotationg ccw
            // For reference:
            // https://stackoverflow.com/questions/9970281/java-calculating-the-angle-between-two-points-in-degrees
            skeleton.findBone("right_leg").setRotation((float)Math.toDegrees(Math.PI + Math.atan2(skeleton.findBone("right_leg").getWorldY() - y, skeleton.findBone("right_leg").getWorldX() - x)));

            //Apply changes to skeleton
            skeleton.updateWorldTransform();
        }



    }


    /*public boolean contains(float x, float y){

        return x > skeleton.findBone("left_arm").getWorldX() && x < skeleton.findBone("right_arm").getWorldX() && y > skeleton.findBone("left_leg").getWorldY() - ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth() && y < skeleton.findBone("head").getWorldY() + ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getHeight();

    }*/

    public int getTouchedLimb(){

        // Probably best method for touch detection, works with rotation, contains() doesn't

        // If statements can't be in order, should be in smallest to largest order for detection purposes

        // Create a rectangle/square that will be used for touch detection
        Polygon touchPolygon = new Polygon(new float[]{0, 0, 1, 0, 1, 1, 0, 1});
        touchPolygon.setPosition(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

        if(Intersector.overlapConvexPolygons(rightArmBox, touchPolygon)){
            return 4;
        }
        else if(Intersector.overlapConvexPolygons(rightLegBox, touchPolygon)){
            return 6;
        }
        else if (Intersector.overlapConvexPolygons(leftArmBox, touchPolygon)) {
            return 2;
        }
        else if(Intersector.overlapConvexPolygons(leftLegBox, touchPolygon)){
            return 5;
        }
        else if(Intersector.overlapConvexPolygons(torsoBox, touchPolygon)){
            return 3;
        }
        else if (Intersector.overlapConvexPolygons(headBox, touchPolygon)){
            return 1;
        }

        return 0;

        /*if(rightArmBox.contains(x, y)){
            return 4;
        }
        else if(rightLegBox.contains(x, y)){
            return 6;
        }
        else if (leftArmBox.contains(x, y)) {
            return 2;
        }
        else if(leftLegBox.contains(x, y)){
            return 5;
        }
        else if(torsoBox.contains(x, y)){
            return 3;
        }
        else if (headBox.contains(x, y)){
            return 1;
        }

        return 0;*/

        /*
        // Check width and height for each limb, width may actually == height.
        // And where is x and y on the attachment? bottom left always????

        // Width = height for some limbs.
        // Check atlas file and png.

        // Left arm, pos is located @ attachement point, so equation is reversed for y
        if(x > skeleton.findBone("left_arm").getWorldX() && x < skeleton.findBone("left_arm").getWorldX() + skeleton.findBone("left_arm").getData().getLength() && y > skeleton.findBone("left_arm").getWorldY() - ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight() && y < skeleton.findBone("left_arm").getWorldY() + ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight()){
            return 2;
        }
        // Right arm, pos is located @ attachement point, so equation is reversed for y
        else if (x > skeleton.findBone("left_arm").getWorldX() && + x < skeleton.findBone("left_arm").getWorldX() + ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getWidth() && y < skeleton.findBone("left_arm").getWorldY() + ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight()/2 && y > skeleton.findBone("left_arm").getWorldY() - ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight()/2){
            return 4;
        }
        // Left leg, pos is located @ attachement point, so equation is reversed for y
        else if(x > skeleton.findBone("left_leg").getWorldX() - ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getHeight()/2 && x < skeleton.findBone("left_leg").getWorldX() + ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getHeight()/2 && y < skeleton.findBone("left_leg").getWorldY() && y > skeleton.findBone("left_leg").getWorldY() - (((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getWidth())){
            return 5;
        }
        // Right leg, pos is located @ attachement point, so equation is reversed for y
        else if(x > skeleton.findBone("right_leg").getWorldX() - ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getHeight()/2 && x < skeleton.findBone("right_leg").getWorldX() + ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getHeight()/2 && y < skeleton.findBone("right_leg").getWorldY() && y > skeleton.findBone("right_leg").getWorldY() - (((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getWidth())){
            return 6;
        }
        // Head
        else if(x > skeleton.findBone("head").getWorldX() - ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth()/2 && x < skeleton.findBone("head").getWorldX() + ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth()/2 && y > skeleton.findBone("head").getWorldY() && y < skeleton.findBone("head").getWorldY() + ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getHeight()){
            return 1;
        }
        // Torso
        else if(x > skeleton.findBone("body").getWorldX() - ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getHeight()/2 && x < skeleton.findBone("body").getWorldX() + ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getHeight()/2 && y > skeleton.findBone("body").getWorldY() && y < skeleton.findBone("body").getWorldY() + ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getWidth()){
            return 3;
        }


        return 0;
        */


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

    public float getWidth(){
        return skeleton.getData().getWidth();
    }

    public float getHeight(){
        return skeleton.getData().getHeight();
    }

}
