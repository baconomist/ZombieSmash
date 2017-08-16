package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Polygon;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.BodyPhysics;

/**
 * Created by Lucas on 2017-07-30.
 */

public class Body{

    public Zombie zombie;

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

    private Matrix3 rotationMatrix;

    public float mass;

    public boolean isPhysicsEnabled;

    public boolean isGravityEnabled;

    int x = 0;


    public Body(Zombie z){

        zombie = z;

        physics = new BodyPhysics(this);

        mass = 40;

        isPhysicsEnabled = true;

        isGravityEnabled = true;

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
        headBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());

        leftArmBox = new Polygon();
        leftArmBox.setVertices(new float[]{0, 0, ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getWidth(), 0, ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getWidth(), ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight(), 0, ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight()});
        leftArmBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());

        // height and width flipped because in image height = width
        torsoBox = new Polygon();
        torsoBox.setVertices(new float[]{0, 0, ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getHeight(), 0, ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getHeight(), ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getWidth(), 0, ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getWidth()});
        torsoBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());

        rightArmBox = new Polygon();
        rightArmBox.setVertices(new float[]{0, 0, ((RegionAttachment)skeleton.findSlot("right_arm").getAttachment()).getWidth(), 0, ((RegionAttachment)skeleton.findSlot("right_arm").getAttachment()).getWidth(), ((RegionAttachment)skeleton.findSlot("right_arm").getAttachment()).getHeight(), 0, ((RegionAttachment)skeleton.findSlot("right_arm").getAttachment()).getHeight()});
        rightArmBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());


        leftLegBox = new Polygon();
        leftLegBox.setVertices(new float[]{0, 0, ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getHeight(), 0, ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getHeight(), ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getWidth(), 0, ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getWidth()});
        leftLegBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());

        rightLegBox = new Polygon();
        rightLegBox.setVertices(new float[]{0, 0, ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getHeight(), 0, ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getHeight(), ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getWidth(), 0, ((RegionAttachment)skeleton.findSlot("right_leg").getAttachment()).getWidth()});
        rightLegBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());

        shapeRenderer = new ShapeRenderer();

    }

    public void update(float delta){

        state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        if(isPhysicsEnabled) {

            physics.update(delta); // Update physics.

        }

        //skeleton.findBone("head").setRotation(-200.1f);
        //skeleton.updateWorldTransform();
        //skeleton.findBone("left_arm").setRotation(-160);

        //Always use this after any transformation change to skeleton:
        skeleton.updateWorldTransform();

        if(Gdx.input.isTouched()) {
           this.setRotation(x++);
        }

        this.setPosition(500, 600);

        Gdx.app.log("rot", ""+this.getRotation());

        updateBoxes();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.polygon(headBox.getTransformedVertices());
        shapeRenderer.polygon(leftArmBox.getTransformedVertices());
        shapeRenderer.polygon(torsoBox.getTransformedVertices());
        shapeRenderer.polygon(rightArmBox.getTransformedVertices());
        shapeRenderer.polygon(leftLegBox.getTransformedVertices());
        shapeRenderer.polygon(rightLegBox.getTransformedVertices());
        shapeRenderer.end();

    }

    private void updateBoxes(){
        // Make polygons follow skeleton bones.
        // Some extra offsets in the positions.

        headBox.setPosition(skeleton.findBone("head").getWorldX() + ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth()/2, skeleton.findBone("head").getWorldY());
        headBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());
        headBox.setRotation(skeleton.findBone("head").getWorldRotationX());

        leftArmBox.setPosition(skeleton.findBone("left_arm").getWorldX(), skeleton.findBone("left_arm").getWorldY() - ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight());
        leftArmBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());
        leftArmBox.setRotation(skeleton.findBone("left_arm").getWorldRotationX());

        torsoBox.setPosition(skeleton.findBone("body").getWorldX() + ((RegionAttachment)skeleton.findSlot("body").getAttachment()).getHeight()/3, skeleton.findBone("body").getWorldY());
        torsoBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());
        torsoBox.setRotation(skeleton.findBone("body").getWorldRotationX());

        rightArmBox.setPosition(skeleton.findBone("right_arm").getWorldX(), skeleton.findBone("right_arm").getWorldY() - ((RegionAttachment)skeleton.findSlot("right_arm").getAttachment()).getHeight());
        rightArmBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());
        rightArmBox.setRotation(skeleton.findBone("right_arm").getWorldRotationX());

        leftLegBox.setPosition(skeleton.findBone("left_leg").getWorldX(), skeleton.findBone("left_leg").getWorldY());
        leftLegBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());
        leftLegBox.setRotation(skeleton.findBone("left_leg").getWorldRotationX());

        rightLegBox.setPosition(skeleton.findBone("right_leg").getWorldX(), skeleton.findBone("right_leg").getWorldY() );
        rightLegBox.setOrigin(skeleton.getRootBone().getX(), skeleton.getRootBone().getY());
        rightLegBox.setRotation(skeleton.findBone("right_leg").getWorldRotationX());


    }

    public void hangFromLimb(int limb, float x, float y){

        //Width = height for some limbs.
        //Check atlas file and png.

        //**************
        //SohCahToa
        //y, x is correct!
        //**************

        // If head
        if(limb == 1){
            // Y has to be negative bc deltaY+ is going up
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
            // Subtracting is rotation ccw
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

}
