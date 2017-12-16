package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Lucas on 2017-07-30.
 */

public class Zombie {

    public int id;

    public String type;

    float scale;

    TextureAtlas atlas;
    Skeleton skeleton;
    AnimationState state;

    HashMap<String, Part> parts = new HashMap<String, Part>();

    private MouseJoint getUpMouseJoint = null;

    private float speed = 50;

    // Default direction is left
    private int direction = 0;


    double timeBeforeAnimate = 5000;
    double timeBeforeAttack = 3000;
    double timeBeforeOptimize = 500;

    double getUpTimer = System.currentTimeMillis();
    double attackTimer = System.currentTimeMillis();
    double optimizationTimer = System.currentTimeMillis();


    String currentAnimation;


    public boolean physicsEnabled = false;

    boolean hasPowerfulPart = false;

    private boolean isMoving = false;

    private boolean isGettingUp = false;

    public boolean isTouching = false;

    public boolean justTouched = false;

    public boolean isAtObjective = false;

    public boolean alive = true;

    public boolean enteredLevel = false;

    boolean isOnGround = false;

    boolean isAttacking = false;

    boolean isCrawler = false;

    int timesCompleteAttack1 = 0;

    float randomObjectiveX = 0;

    public boolean optimize = false;

    Zombie(int id) {
        this.id = id;
    }



    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer, float delta){
        for (Slot slot : skeleton.getDrawOrder()) {
            if (parts.get(slot.getAttachment().getName()) != null) {
                parts.get(slot.getAttachment().getName()).draw(batch);
            }
        }
        if (!physicsEnabled) {
            skeletonRenderer.draw(batch, skeleton);
        }
        update(delta);
    }

    private void update(float delta){

        updateSkeleton(delta);

        updateParts();

        if(this.getPosition().x > Environment.physics.getWall_1().getPosition().x + 0.5f
                && this.getPosition().x < Environment.physics.getWall_2().getPosition().x - 0.5f){
            enteredLevel = true;
            Gdx.app.log("entered", "a");
        }

        if(isMoving && !isGettingUp || isTouching){
            getUpTimer = System.currentTimeMillis();
        }

        if(justTouched){
            //Make the physics bodies unstuck, sometimes the animation goes into the ground.
            this.setPosition(this.getPosition().x, this.getPosition().y + 0.1f);
            // Un-freezes the zombie when holding click
            for(Part p : parts.values()){
                p.physicsBody.setAwake(true);
            }
        }

        if(isTouching){
            if(getUpMouseJoint != null){
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
                getUpMouseJoint = null;
            }
            isGettingUp = false;

            physicsEnabled = true;
            isAtObjective = false;

        }

        else if(parts.get("head") != null && parts.get("left_leg") != null && parts.get("right_leg") != null) {
            if (physicsEnabled && !isMoving && System.currentTimeMillis() - getUpTimer >= timeBeforeAnimate) {
                this.getUp();
            } else if (isGettingUp) {
                this.getUp();
            }
        }
        else if(parts.get("head") != null && (parts.get("left_arm") != null || parts.get("right_arm") != null)){
            if (physicsEnabled && !isMoving && System.currentTimeMillis() - getUpTimer >= timeBeforeAnimate) {
                this.crawl();
            }
        }
        else{
            this.alive = false;
        }



        if(!physicsEnabled){
            this.move();
        }


        if(isTouching || isMoving || !isOnGround || isGettingUp){
            optimize = false;
            optimizationTimer = System.currentTimeMillis();
        }
        else if(System.currentTimeMillis() - optimizationTimer >= timeBeforeOptimize){
            optimize = true;
        }
    }


    private void updateSkeleton(float delta){
        state.update(delta); // Update the animation getUpTimer.

        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.

        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        for(Slot s : skeleton.getSlots()){
            if(parts.get(s.getData().getName()) == null){
                s.setAttachment(new Attachment("new attachement") {
                    @Override
                    public String getName() {
                        return super.getName();
                    }
                });
            }
        }

        if(!this.currentAnimation.equals(state.getCurrent(0).getAnimation().getName())){
            state.setAnimation(0, currentAnimation, true);
        }



    }

    private void updateParts(){

        // Run update method for each body part
        boolean ispowerfulpart = false;
        boolean istouching = false;
        boolean isatobjective = false;
        for(Part p : parts.values()){
            p.update();
            if(p.isPowerfulPart && !ispowerfulpart){
                ispowerfulpart = true;
            }
            if(p.isTouching && !istouching){
                istouching = true;
            }
            if(!isatobjective && Environment.level.objective.polygon.contains(p.polygon.getX(), p.polygon.getY())){
                isatobjective = true;
            }
        }

        // Update body touching state
        hasPowerfulPart = ispowerfulpart;
        justTouched = !isTouching && istouching;
        isTouching = istouching;
        isMoving = parts.get("torso").isMoving;
        isOnGround = parts.get("torso").isOnGround;
        isAtObjective = isatobjective;

    }


    private void getUp(){

        // -0.2f to give it wiggle room to detect get up
        if (isGettingUp && parts.get("head").physicsBody.getPosition().y >= (Environment.physicsCamera.viewportHeight - Environment.physicsCamera.unproject((Environment.gameCamera.project(new Vector3(0, this.getHeight(), 0)))).y) - 0.2f){

            isGettingUp = false;
            physicsEnabled = false;
            setPosition(parts.get("torso").physicsBody.getPosition().x, 0);

            if (getUpMouseJoint != null) {
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
                getUpMouseJoint = null;
            }

            this.onGetUp();

            // Restart animation
            state.setAnimation(0, currentAnimation, true);

        }

        else if (!isGettingUp){

            MouseJointDef mouseJointDef = new MouseJointDef();

            // Needs 2 bodies, first one not used, so we use an arbitrary body.
            // http://www.binarytides.com/mouse-joint-box2d-javascript/
            mouseJointDef.bodyA = Environment.physics.getGround();
            mouseJointDef.bodyB = parts.get("head").physicsBody;
            mouseJointDef.collideConnected = true;
            mouseJointDef.target.set(parts.get("head").physicsBody.getPosition());

            // The higher the ratio, the slower the movement of body to mousejoint
            mouseJointDef.dampingRatio = 7;

            mouseJointDef.maxForce = 100000f;

            // Destroy the current mouseJoint
            if(getUpMouseJoint != null){
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
            }
            getUpMouseJoint = (MouseJoint) Environment.physics.getWorld().createJoint(mouseJointDef);
            Gdx.app.log("a", "" + (Environment.physicsCamera.viewportHeight - Environment.physicsCamera.unproject((Environment.gameCamera.project(new Vector3(0, this.getHeight(), 0)))).y));
            getUpMouseJoint.setTarget(new Vector2(parts.get("torso").physicsBody.getPosition().x, Environment.physicsCamera.viewportHeight - Environment.physicsCamera.unproject((Environment.gameCamera.project(new Vector3(0, this.getHeight(), 0)))).y));

            isGettingUp = true;

        }

        Gdx.app.log("h", ""+parts.get("head").physicsBody.getTransform().getPosition().y);

    }

    public void setPosition(float x, float y){
        if(physicsEnabled){
            parts.get("torso").setPosition(x, y);
        }
        else {
            Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(x, y, 0)));
            skeleton.setPosition(pos.x, Environment.gameCamera.viewportHeight - pos.y);
            skeleton.updateWorldTransform();
        }




    }

    Vector2 getPosition(){
        if(physicsEnabled){
            return parts.get("torso").physicsBody.getPosition();
        }
        // Else, return animation position
        // Camera doesn't take care of reverse y axis(starting from top)
        Vector3 pos = Environment.physicsCamera.unproject(new Vector3(skeleton.getX(), skeleton.getY(), 0));
        return new Vector2(pos.x, Environment.physicsCamera.viewportHeight - pos.y);
    }


    public Part getPartFromPhysicsBody(Body physicsBody){

        for(Part p : parts.values()){
            if(p.physicsBody.equals(physicsBody)){
                return p;
            }
        }

        return null;

    }

    public Skeleton getSkeleton(){
        return skeleton;
    }

    // Recursion!
    /*float getHeight(ArrayList<Part> p){
        ArrayList<Part> pCopy = new ArrayList<Part>();
        for(Part prt : p){
            pCopy.add(prt);
        }

        if (p.size() == 0){
            return 0;
        }
        else{
            pCopy.remove(0);
            return getHeight(pCopy) + p.get(0).sprite.getHeight();
        }

    }*/

    float getHeight(){
        float total = 0;

        total += parts.get("head").sprite.getHeight();
        total += parts.get("torso").sprite.getHeight();
        total += parts.get("left_leg").sprite.getHeight();

        return total;
    }

    float getWidth(){
        float total = 0;

        total += parts.get("left_arm").sprite.getWidth();
        total += parts.get("torso").sprite.getWidth();
        total += parts.get("right_arm").sprite.getWidth();

        return total;
    }

    public HashMap<String, Part> getParts(){
        return parts;
    }
    void crawl(){
        this.isCrawler = true;
    }
    void onObjective(){this.attack();}
    void attack(){
        this.isAttacking = true;
    }
    void move(){
        if(!isAttacking) {
            if (isAtObjective && this.randomObjectiveX == 0) {
                if(this.direction == 0) {
                    this.randomObjectiveX = skeleton.getRootBone().getWorldX() + new Random().nextInt(300);
                }
                else{
                    this.randomObjectiveX = skeleton.getRootBone().getWorldX() - new Random().nextInt(200);
                }
            }
            if (!isAtObjective && !isAttacking) {
                if (this.direction == 0) {
                    skeleton.setPosition(skeleton.getX() + this.speed * Gdx.graphics.getDeltaTime(), skeleton.getY());
                } else {
                    skeleton.setPosition(skeleton.getX() - this.speed * Gdx.graphics.getDeltaTime(), skeleton.getY());
                }
                this.randomObjectiveX = 0;
            } else if (skeleton.getRootBone().getWorldX() < this.randomObjectiveX) {
                if (this.direction == 0) {
                    skeleton.setPosition(skeleton.getX() + this.speed * Gdx.graphics.getDeltaTime(), skeleton.getY());
                } else {
                    skeleton.setPosition(skeleton.getX() - this.speed * Gdx.graphics.getDeltaTime(), skeleton.getY());
                }
            } else {
                this.onObjective();
            }
        }

    }
    void onGetUp(){}

    private void onDirectionChange(){
        if(this.direction == 1){
            skeleton.setFlipX(true);
        }
        else{
            skeleton.setFlipX(false);
        }

        for (Part p : parts.values()) {
            p.setState("waiting_for_destroy");
        }

        if(this.direction == 1){
            constructPhysicsBody(Environment.physics.getWorld(), true);
        }
        else{
            constructPhysicsBody(Environment.physics.getWorld(), false);
        }
    }

    public void constructPhysicsBody(World world, boolean flip){
        RubeSceneLoader loader = new RubeSceneLoader(world);
        RubeScene rubeScene;
        if(flip){
            rubeScene = loader.loadScene(Gdx.files.internal("zombies/"+type+"_zombie/"+type+"_zombie_flip_rube.json"));
        }
        else {
            rubeScene = loader.loadScene(Gdx.files.internal("zombies/"+type+"_zombie/"+type+"_zombie_rube.json"));
        }

        parts = new HashMap<String, Part>();

        for(Body b : rubeScene.getBodies()) {

            String bodyName = (String) rubeScene.getCustom(b, "name");
            Gdx.app.log("bodyName", bodyName);
            Sprite sprite = new Sprite(atlas.findRegion(bodyName));

            for (RubeImage i : rubeScene.getImages()) {
                if (i.body == b) {
                    sprite.flip(flip, false);
                    sprite.setColor(i.color);
                    sprite.setOriginCenter();
                    sprite.setSize(i.width*Physics.PIXELS_PER_METER, i.height*Physics.PIXELS_PER_METER);
                    sprite.setOriginCenter();
                }

            }

            Joint joint = null;
            for (Joint j : rubeScene.getJoints()) {
                if (j.getBodyA() == b || j.getBodyB() == b) {
                    joint = j;
                    break;
                }
            }


            for (Fixture f : b.getFixtureList()) {
                // Makes different zombies not collide with each other
                f.setUserData(this);
            }

            parts.put(bodyName, new Part(bodyName, sprite, b, joint, this));

        }

        skeleton.getRootBone().setScale(parts.get("head").sprite.getWidth()/((RegionAttachment)skeleton.findSlot("head").getAttachment()).getWidth(), parts.get("head").sprite.getHeight()/((RegionAttachment)skeleton.findSlot("head").getAttachment()).getHeight());

        state.update(Gdx.graphics.getDeltaTime()); // Update the animation getUpTimer.

        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.

        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        parts.get("torso").isDetachable = false;

    }
    public void animationSetup() {}
    public void setDirection(int i){
        if(this.direction != i){
            this.direction = i;
            onDirectionChange();
        }
    }
    public int getDirection(){
        if(direction == 0){
            return 0;
        }
        else{
            return 1;
        }
    }

    public void touchDown(float x, float y, int pointer) {
        for (String name : parts.keySet()) {
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

    public void destroy(){}

}
