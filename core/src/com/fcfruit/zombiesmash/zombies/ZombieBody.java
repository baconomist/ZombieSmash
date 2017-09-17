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
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.Physics;
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

/**
 * Created by Lucas on 2017-07-30.
 */

public class ZombieBody{

    private Zombie zombie;

    private RubeScene rubeScene;

    private HashMap<Body, Sprite> rubeSprites;

    private TextureAtlas atlas;
    private Skeleton skeleton;
    private AnimationState state;

    private HashMap<String, Part> parts;

    public boolean isPhysicsEnabled;

    public OrthographicCamera camera;

    public ZombieBody(Zombie z){

        zombie = z;

        rubeSprites = new HashMap<Body, Sprite>();

        isPhysicsEnabled = true;

        animationSetup();

        parts = new HashMap<String, Part>();
        parts.put("head", new Part("head", this));
        parts.put("left_arm", new Part("left_arm", this));
        parts.put("torso", new Part("torso", this));
        parts.put("right_arm", new Part("right_arm", this));
        parts.put("left_leg", new Part("left_leg", this));
        parts.put("right_leg", new Part("right_leg", this));


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

    public void draw(SpriteBatch batch, float delta){
        for(Body b : parts){
            batch.begin();
            rubeSprites.get(b).draw(batch);
            batch.end();
        }
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
        skeleton.updateWorldTransform();

        updateRubeImages();

        updateParts();

    }

    private void updateRubeImages(){
        for(Body b : rubeSprites.keySet()){
            Sprite sprite = rubeSprites.get(b);

            for(RubeImage i : rubeScene.getImages()){
                if(i.body == b){
                    sprite.flip(i.flip, false);
                    sprite.setColor(i.color);
                    sprite.setOrigin(i.center.x, i.center.y);
                    sprite.setSize(i.width*Physics.PPM, i.height*Physics.PPM);

                }

            }


            Vector3 pos = camera.project(new Vector3(b.getPosition().x, b.getPosition().y, 0));
            sprite.setPosition(pos.x - sprite.getWidth()/2, pos.y - sprite.getHeight()/2);
            sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
            sprite.setRotation((float) Math.toDegrees(b.getAngle()));




        }
    }

    private void updateParts(){
        // Run update method for each body part
        for(String partName : parts.keySet()){
            parts.get(partName).update();
        }
    }

    public void constructPhysicsBody(World world){
        RubeSceneLoader loader = new RubeSceneLoader(world);
        rubeScene = loader.loadScene(Gdx.files.internal("zombies/reg_zombie/reg_zombie_rube.json"));

        for(Body b : rubeScene.getBodies()){

            /*Sprite tempSprite = new Sprite(atlas.findRegion((String) rubeScene.getCustom(b, "name")));

            if(atlas.findRegion((String) rubeScene.getCustom(b, "name")).rotate){
                tempSprite.setRotation(tempSprite.getRotation() - 90);
            }

            Sprite sprite = new Sprite(tempSprite.getTexture());*/

            Sprite sprite = new Sprite(atlas.findRegion((String) rubeScene.getCustom(b, "name")));

            rubeSprites.put(b, sprite);

        }

        updateRubeImages();
    }


    /*public boolean contains(float x, float y){

        return x > parts.get("left_arm").getWorldX() && x < parts.get("right_arm").getWorldX() && y > parts.get("left_leg").getWorldY() - ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth() && y < parts.get("head").getWorldY() + ((RegionAttachment)skeleton.findSlot("head").getAttachment()).getHeight();

    }*/




    public void setPosition(float x, float y){
        skeleton.setPosition(x, y);

        //Apply changes to skeleton
        skeleton.updateWorldTransform();
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
