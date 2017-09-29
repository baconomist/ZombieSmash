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

    private Zombie zombie;

    private RubeScene rubeScene;

    private TextureAtlas atlas;
    private Skeleton skeleton;
    private AnimationState state;

    public HashMap<String, Part> parts;


    public boolean physicsEnabled;

    public boolean isTouching = false;

    public ZombieBody(Zombie z){

        zombie = z;

        physicsEnabled = true;

        animationSetup();

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

        //getUp();

        state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.

        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        //parts.get("head").setRotation(-200.1f);
        //skeleton.updateWorldTransform();
        //parts.get("left_am").setRotation(-160);

        //Always use this after any transformation change to skeleton:
        //skeleton.updateWorldTransform();

        updateParts();

    }

    private void updateParts(){

        HashMap<String, Part> copy = new HashMap<String, Part>();
        for (Map.Entry<String, Part> entry : parts.entrySet())
        {
            copy.put(entry.getKey(), entry.getValue());
        }

        // Run update method for each body part
        for(Part p : copy.values()){
            p.update();
        }
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
                        sprite.setSize(i.width * Physics.PPM, i.height * Physics.PPM);
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
        this.setPosition(1, 1);
        updateParts();
    }

    private void getUp(){
        for(Part p : parts.values()){

            if(p.getName() != "head" || !(p.getName().contains("arm"))) {

                p.physicsBody.setTransform(p.physicsBody.getPosition(), (float)Math.toRadians(0));

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
        skeleton.setPosition(x, y);
        if(parts.get("torso") != null){
            parts.get("torso").setPosition(x,
                    y);
        }
        else{
            this.destroy();
        }
    }

    public void setRotation(float degrees){
        skeleton.getRootBone().setRotation(degrees);
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
