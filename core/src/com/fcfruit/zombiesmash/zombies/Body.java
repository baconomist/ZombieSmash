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
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;
import com.esotericsoftware.spine.Skin;
import com.fcfruit.zombiesmash.BodyPhysics;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-07-30.
 */

public class Body{

    private BodyPhysics physics;

    public TextureAtlas atlas;
    public Skeleton skeleton;
    public AnimationState state;

    public float mass;

    public boolean isPhysicsEnabled;

    public boolean isGravityEnabled;

    public boolean isRagdollEnabled;

    public Body(Zombie z){

        physics = new BodyPhysics(this);

        mass = 40;

        isPhysicsEnabled = true;

        isGravityEnabled = true;

        isRagdollEnabled = true;

        atlas = new TextureAtlas(Gdx.files.internal("spineboy.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(0.6f); // Load the skeleton at 60% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("spineboy.json"));

        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).

        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.
        stateData.setMix("run", "jump", 0.2f);
        stateData.setMix("jump", "run", 0.2f);

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(0.5f); // Slow all animations down to 50% speed.

        // Queue animations on track 0.
        state.setAnimation(0, "run", true);
        state.addAnimation(0, "jump", false, 2); // Jump after 2 seconds.
        state.addAnimation(0, "run", true, 0); // Run after the jump.

    }

    public void update(float delta){

        state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        physics.update(delta); // Update physics.

        Gdx.app.log("update", ""+skeleton.getY());


    }


    public boolean contains(float x, float y){

        return x < skeleton.findBone("head").getWorldX() + 100 && x > skeleton.findBone("head").getWorldX() - 100 && y < skeleton.findBone("head").getWorldY() + 100 && y > skeleton.findBone("head").getWorldY() - 100;

    }

    public float getX(){
        return skeleton.getX();
    }

    public float getY(){
        return skeleton.getY();

    }

    public void setPosition(float x, float y){
        skeleton.setPosition(x, y);
    }

    public float getWidth(){
        return skeleton.getX();
    }

    public float getHeight(){
        return skeleton.getY();
    }

}
