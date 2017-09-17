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

    static String[] drawOrder = new String[]{"head", "torso", "right_arm", "left_arm", "right_leg", "left_leg"};

    private Zombie zombie;
    
    private Physics physics;

    private RubeScene rubeScene;

    private TextureAtlas atlas;
    private Skeleton skeleton;
    private AnimationState state;

    public HashMap<String, Part> parts;

    public boolean isPhysicsEnabled;

    public OrthographicCamera camera;
    

    private HashMap<Integer, MouseJoint>mouseJoints;

    private Body touchedPart = null;

    private int mainPointer;

    public ZombieBody(Zombie z, Physics p){

        zombie = z;
        
        physics = p;

        isPhysicsEnabled = true;

        mouseJoints = new HashMap<Integer, MouseJoint>();

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

    public void draw(SpriteBatch batch, float delta){
        for(String name : drawOrder){
            parts.get(name).draw(batch);
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
        for(String partName : parts.keySet()){
            Part part = parts.get(partName);
            Sprite sprite = parts.get(partName).sprite;

            for(RubeImage i : rubeScene.getImages()){
                if(i.body == part.physicsBody){
                    sprite.flip(i.flip, false);
                    sprite.setColor(i.color);
                    sprite.setOrigin(i.center.x, i.center.y);
                    sprite.setSize(i.width*Physics.PPM, i.height*Physics.PPM);

                }

            }


            Vector3 pos = camera.project(new Vector3(part.physicsBody.getPosition().x, part.physicsBody.getPosition().y, 0));
            sprite.setPosition(pos.x - sprite.getWidth()/2, pos.y - sprite.getHeight()/2);
            sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
            sprite.setRotation((float) Math.toDegrees(part.physicsBody.getAngle()));

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

        parts = new HashMap<String, Part>();

        for(Body b : rubeScene.getBodies()){

            String bodyName = (String) rubeScene.getCustom(b, "name");

            Sprite sprite = new Sprite(atlas.findRegion(bodyName));

            parts.put(bodyName, new Part(bodyName, sprite, b, this));

        }

        updateRubeImages();
    }
    
    
    Vector2 hitPoint = new Vector2();
    QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture (Fixture fixture) {
            // if the hit fixture's body is the ground body
            // we ignore it
            if (fixture.getBody() == physics.getGround()) return true;
            
            // if the hit point is inside the fixture of the body
            // we report it
            if (fixture.testPoint(hitPoint
                    .x, hitPoint
                    .y)) {
                touchedPart = fixture.getBody();
                return false;
            } else
                return true;
        }
    };

    public void createMouseJoint(float x, float y, int pointer, boolean mainTouch){
        MouseJointDef mouseJointDef = new MouseJointDef();
        // Needs 2 bodies, first one not used, so we use an arbitrary body.
        // http://www.binarytides.com/mouse-joint-box2d-javascript/
        mouseJointDef.bodyA = physics.getGround();
        mouseJointDef.bodyB = touchedPart;
        mouseJointDef.collideConnected = true;
        mouseJointDef.target.set(x, y);
        if(mainTouch) {
            mainPointer = pointer;
            // Force applied to body to get to point
            mouseJointDef.maxForce = 10000f * touchedPart.getMass();
        }
        else{
            // Force applied to body to get to point
            // Set to less if already touching so you can only rotate other limbs
            mouseJointDef.maxForce = 100f * touchedPart.getMass();
        }
        if(mouseJoints.get(pointer) != null){
            physics.getWorld().destroyJoint(mouseJoints.get(pointer));
            mouseJoints.remove(pointer);
        }
        mouseJoints.put(pointer, (MouseJoint) physics.getWorld().createJoint(mouseJointDef));

        touchedPart.setAwake(true);
    }

    public void touchDown(float x, float y, int pointer){

        hitPoint.set(x, y);
        touchedPart = null;
        physics.getWorld().QueryAABB(callback, hitPoint.x - 0.1f, hitPoint.y - 0.1f, hitPoint.x + 0.1f, hitPoint.y + 0.1f);
        if (touchedPart != null) {
            if(mouseJoints.size() < 1) {
                createMouseJoint(x, y, pointer, true);
            }
            else{
                createMouseJoint(x, y, pointer, false);
            }
        }

    }

    public void touchDragged(float x, float y, int pointer){
        if (mouseJoints.get(pointer) != null) {
            mouseJoints.get(pointer).setTarget(new Vector2(x, y));
        }
        else{
            hitPoint.set(x, y);
            touchedPart = null;
            physics.getWorld().QueryAABB(callback, hitPoint.x - 0.1f, hitPoint.y - 0.1f, hitPoint.x + 0.1f, hitPoint.y + 0.1f);
            if(touchedPart != null) {
                if(mouseJoints.size() < 1) {
                    createMouseJoint(x, y, pointer, true);
                }
                else{
                    createMouseJoint(x, y, pointer, false);
                }
            }
        }
    }

    public void touchUp(float x, float y, int pointer){

        // Destroy mouseJoint at a pointer

        if(mouseJoints.get(pointer) != null) {
            physics.getWorld().destroyJoint(mouseJoints.get(pointer));
            mouseJoints.remove(pointer);

            if(pointer == mainPointer) {
                // Assuming mouseJoints.keySet() doesn't list in arbitrary order
                for (int p : mouseJoints.keySet()) {
                    // Get next pointer in order
                    touchedPart = mouseJoints.get(p).getBodyB();
                    mainPointer = p;
                    mouseJoints.get(p).setMaxForce(10000f * touchedPart.getMass());
                    break;
                }
            }

        }

    }




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

}
