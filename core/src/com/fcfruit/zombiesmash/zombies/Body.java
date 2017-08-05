package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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

    public HashMap limbs;

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

        limbs = new HashMap();

        limbs.put(1, skeleton.findSlot("head"));
        limbs.put(2, skeleton.findSlot("left_arm"));
        limbs.put(3, skeleton.findSlot("torso"));
        limbs.put(4, skeleton.findSlot("right_arm"));
        limbs.put(5, skeleton.findSlot("left_leg"));
        limbs.put(6, skeleton.findSlot("right_leg"));

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

    }

    public void hangFromLimb(int limb, float x, float y){
        // Use the hash map limbs for limbs reference!

        // If head
        if(limb == 1){
            this.setPosition(x - ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth(), y - skeleton.getData().getHeight() + ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getHeight()/2);
        }
        // If left arm
        else if (limb == 2){

        }
        // If torso
        else if (limb == 3){

            //this.setPosition(x - ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getWidth(), y - ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight());

            skeleton.findBone("left_arm").setRotation((float)Math.toDegrees(Math.atan2(this.getY() - y, this.getX() - x)));
            Gdx.app.log("rot", ""+skeleton.findBone("left_arm").getRotation());

            //Apply changes to skeleton
            skeleton.updateWorldTransform();

        }
        // If right arm
        else if (limb == 4){

        }
        // If left leg
        else if (limb == 5){

        }
        // If right leg
        else if (limb == 6){

        }



    }


    public boolean contains(float x, float y){

        return x > skeleton.findBone("left_leg").getWorldX() && x < skeleton.findBone("right_leg").getWorldX() && y > skeleton.findBone("left_leg").getWorldY() && y < skeleton.findBone("head").getWorldY() + ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getHeight();

    }

    public int getTouchedLimb(float x, float y){
        // Check width and height for each limb, width may actually == height.
        // And where is x and y on the attachment? bottom left always????

        // If head is touched, head bone is in middle of head
        if(x > skeleton.findBone("head").getWorldX() - ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth()/2 && x < skeleton.findBone("head").getWorldX() + ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth()/2 && y > skeleton.findBone("head").getWorldY() && y < skeleton.findBone("head").getWorldY() + ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getHeight()){
            return 1;
        }
        // Left arm
        else if(x > skeleton.findBone("left_arm").getWorldX() && x < skeleton.findBone("left_arm").getWorldX() + ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getWidth() && y > skeleton.findBone("left_arm").getWorldY() && y < skeleton.findBone("left_arm").getWorldY() + ((RegionAttachment)skeleton.findSlot("left_arm").getAttachment()).getHeight()){
            return 2;
        }
        // Torso
        else if(true){
            return 3;
        }
        // Right arm
        else if (true){
            return 4;
        }
        // Left leg
        else if(x > skeleton.findBone("left_leg").getWorldX() && x < skeleton.findBone("left_leg").getWorldX() + ((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getWidth() && y > skeleton.findBone("left_leg").getWorldY() && y < skeleton.findBone("left_leg").getWorldY() + (((RegionAttachment)skeleton.findSlot("left_leg").getAttachment()).getHeight())){
            return 5;
        }
        // Right leg
        else if(true){
            return 6;
        }

        return 0;
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
