package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.Star;
import com.fcfruit.zombiesmash.level.Level;
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

    ArrayList partsToStayAlive = new ArrayList();

    public int id;

    public String type;

    float scale;

    TextureAtlas atlas;
    Skeleton skeleton;
    AnimationState state;

    HashMap<String, Part> parts = new HashMap<String, Part>();

    private MouseJoint getUpMouseJoint = null;

    private float speed = 200;

    // Default direction is left
    private int direction = 0;


    double timeBeforeAnimate = 5000;
    double timeBeforeAttack = 3000;
    double timeBeforeOptimize = 500;

    double getUpTimer = System.currentTimeMillis();
    double attackTimer = System.currentTimeMillis();
    double optimizationTimer = System.currentTimeMillis();


    String currentAnimation;
    String moveAnimation;


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

    public Polygon polygon;

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

        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getPosition().x, this.getPosition().y, 0)));
        // Center polygon with polygon width/2(polygon.getVerticies[2]/2) and polygon height/2(polygon.getVerticies[5]/2)
        polygon.setPosition(pos.x - polygon.getVertices()[2]/2, Environment.gameCamera.viewportHeight - pos.y - polygon.getVertices()[5]/2);
        polygon.setRotation(this.getRotation());


        // Prevents zombie from being totally lost out of the map
        if(!enteredLevel && this.getPosition().x < -1){
            this.setPosition(Level.positions.get(Environment.level.currentCameraPosition).x - (Environment.physicsCamera.viewportWidth/2 + 1), 0);
            this.speed = 400;
            this.checkDirection();
        }
        else if(!enteredLevel && this.getPosition().y > 21){
            this.setPosition(Level.positions.get(Environment.level.currentCameraPosition).x + Environment.physicsCamera.viewportWidth/2 + 1, 0);
            this.speed = 400;
            this.checkDirection();
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

        else if(parts.get("head") != null && parts.get("left_leg") != null && parts.get("right_leg") != null && this.alive) {
            if (physicsEnabled && !isMoving && System.currentTimeMillis() - getUpTimer >= timeBeforeAnimate) {
                this.getUp();
            } else if (isGettingUp) {
                this.getUp();
            }
        }
        else if(parts.get("head") != null && (parts.get("left_arm") != null || parts.get("right_arm") != null) && this.alive){
            if (physicsEnabled && !isMoving && System.currentTimeMillis() - getUpTimer >= timeBeforeAnimate) {
                this.checkDirection();
                this.crawl();
            }
        }
        if(alive){
            for(Object o : partsToStayAlive){
                if(o instanceof String){
                    if (parts.get((String) o) != null) {
                        alive = true;
                    } else {
                        alive = false;
                        break;
                    }
                }
                else if(o instanceof String[]){
                    for(String s : (String[]) o) {
                        if (parts.get(s) != null) {
                            alive = true;
                            break;
                        } else {
                            alive = false;
                        }
                    }
                }
            }

            if(!alive){
                onDeath();
            }

        }

        if(!physicsEnabled && !this.isGettingUp && !this.isAttacking){
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
        boolean elvl = true;
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
            if(elvl) {
                if (p.physicsBody.getPosition().x > Environment.physics.getWall_1().getPosition().x + 0.5f
                        && p.physicsBody.getPosition().x < Environment.physics.getWall_2().getPosition().x - 0.5f) {
                    elvl = true;
                } else {
                    elvl = false;
                }
            }
        }

        // Update body touching state
        hasPowerfulPart = ispowerfulpart;
        justTouched = !isTouching && istouching;
        isTouching = istouching;
        isMoving = parts.get("torso").isMoving;
        isOnGround = parts.get("torso").isOnGround;
        isAtObjective = isatobjective;
        if(!enteredLevel && elvl){
            this.onEnteredLevel();
        }
        enteredLevel = elvl;

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
            getUpMouseJoint.setTarget(new Vector2(parts.get("torso").physicsBody.getPosition().x, Environment.physicsCamera.viewportHeight - Environment.physicsCamera.unproject((Environment.gameCamera.project(new Vector3(0, this.getHeight(), 0)))).y));

            isGettingUp = true;

        }


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
        return parts.get("torso").physicsBody.getPosition();
    }

    float getRotation(){
        return (float)Math.toDegrees(parts.get("torso").physicsBody.getAngle());
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

        return total;
    }

    float getDistanceToObjective(){
        //this is broken
        if(this.direction == 0) {
            return Math.abs(Environment.level.objective.getPosition().x + ((Environment.level.objective.getWidth() / 2) * 3/4) - this.getPosition().x);
        }
        return Math.abs(Environment.level.objective.getPosition().x + ((Environment.level.objective.getWidth() / 2) * 6/4) - this.getPosition().x);
    }

    public HashMap<String, Part> getParts(){
        return parts;
    }
    void crawl(){
        this.isCrawler = true;
    }
    void onObjective(){
        this.attack();
    }
    void attack(){
        // Only attack if inside the level
        this.isAttacking = this.enteredLevel;
    }
    void move(){

        this.currentAnimation = this.moveAnimation;

        if (isAtObjective && this.randomObjectiveX == 0) {
            this.checkDirection();
            if(this.direction == 0) {
                Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getDistanceToObjective(), 0, 0)));
                this.randomObjectiveX = skeleton.getRootBone().getWorldX() + new Random().nextInt((int)pos.x);
            }
            else{
                Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getDistanceToObjective(), 0, 0)));
                this.randomObjectiveX = skeleton.getRootBone().getWorldX() - new Random().nextInt((int)pos.x);
            }
        }
        if (!isAtObjective && !isAttacking) {
            if (this.direction == 0) {
                skeleton.setPosition(skeleton.getX() + this.speed * Gdx.graphics.getDeltaTime(), skeleton.getY());
            } else {
                skeleton.setPosition(skeleton.getX() - this.speed * Gdx.graphics.getDeltaTime(), skeleton.getY());
            }
            this.randomObjectiveX = 0;
        } else if (skeleton.getRootBone().getWorldX() < this.randomObjectiveX && this.direction == 0 || skeleton.getRootBone().getWorldX() > this.randomObjectiveX && this.direction == 1) {
            if (this.direction == 0) {
                skeleton.setPosition(skeleton.getX() + this.speed * Gdx.graphics.getDeltaTime(), skeleton.getY());
            } else {
                skeleton.setPosition(skeleton.getX() - this.speed * Gdx.graphics.getDeltaTime(), skeleton.getY());
            }
        }
        else {
            this.onObjective();
        }


    }
    void onGetUp(){this.checkDirection();}

    void checkDirection(){
        int previous_direction = this.direction;
        if(this.getPosition().x < Environment.level.objective.getPosition().x + Environment.level.objective.getWidth()/4){
            this.direction = 0;
        }
        else if(this.getPosition().x < Environment.level.objective.getPosition().x + Environment.level.objective.getWidth()/2){
            this.direction = 1;
        }
        else if(this.getPosition().x < Environment.level.objective.getPosition().x + Environment.level.objective.getWidth()/2 + Environment.level.objective.getWidth()/4){
            this.direction = 0;
        }
        else{
            this.direction = 1;
        }
        if(previous_direction != this.direction){
            onDirectionChange();
        }
    }
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


        polygon = new Polygon(new float[]{0, 0, this.getWidth(), 0, this.getWidth(), this.getHeight(), 0, this.getHeight()});
        polygon.setOrigin(this.getWidth()/2, this.getHeight()/2);

    }
    void animationSetup() {
        atlas = new TextureAtlas(Gdx.files.internal("zombies/"+this.type+"_zombie/"+this.type+"_zombie.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("zombies/"+this.type+"_zombie/"+this.type+"_zombie.json"));

        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.
        //stateData.setMix("run", "jump", 0.2f);
        //stateData.setMix("jump", "run", 0.2f);

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

        // Queue animations on track 0.
        this.currentAnimation = this.moveAnimation;
        state.setAnimation(0, currentAnimation, true);

        state.addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                super.complete(entry);
            }
        });

        state.addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if(alive) {
                    if (currentAnimation.equals("attack1")) {
                        timesCompleteAttack1++;
                    } else if (currentAnimation.equals("attack2")) {
                        timesCompleteAttack1 = 0;
                    }
                    if (currentAnimation.contains("attack")) {
                        isAttacking = false;
                        Environment.level.objective.onHit();
                    }
                    super.complete(entry);
                }
            }
        });

    }

    void onEnteredLevel(){
        this.speed = this.getSpeed();
    }

    float getSpeed(){
        return 200;
    }

    void onDeath(){
        Random rand = new Random();
        int ammount_points = rand.nextInt(2);
        String s = "";
        for(int i = 0; i <= ammount_points; i++){
            int type = rand.nextInt(10);
            if(type <= 2) {
                type = 0;
                s = "stars/gold_star.png";
            }
            if(type > 2 && type <= 5){
                type = 1;
                s = "stars/silver_star.png";
            }
            if(type > 5){
                type = 2;
                s = "stars/bronze_star.png";
            }
            Environment.physics.addBody(new Star(new Texture(Gdx.files.internal(s)), this.getPosition().x + i, 1.5f, type));
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
