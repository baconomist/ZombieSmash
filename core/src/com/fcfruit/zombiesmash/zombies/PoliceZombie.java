package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by Lucas on 2017-11-06.
 */

public class PoliceZombie extends Zombie {

    public PoliceZombie(int id) {
        super(id);

        animationSetup();

    }

    @Override
    public void animationSetup() {
        atlas = new TextureAtlas(Gdx.files.internal("zombies/police_zombie/police_zombie.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("zombies/police_zombie/police_zombie.json"));

        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.
        //stateData.setMix("run", "jump", 0.2f);
        //stateData.setMix("jump", "run", 0.2f);

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

        // Queue animations on track 0.
        this.currentAnimation = "walk";
        state.setAnimation(0, currentAnimation, true);

        state.addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if(currentAnimation.equals("attack1")){
                    timesCompleteAttack1++;
                    isAttacking = false;
                }
                else if(currentAnimation.equals("attack2")){
                    timesCompleteAttack1 = 0;
                    isAttacking = false;
                }
                super.complete(entry);
            }
        });


    }

    @Override
    public void constructPhysicsBody(World world){
        RubeSceneLoader loader = new RubeSceneLoader(world);
        RubeScene rubeScene = loader.loadScene(Gdx.files.internal("zombies/police_zombie/police_zombie_rube.json"));

        parts = new HashMap<String, Part>();

        float scaleX = 0;
        float scaleY = 0;

        for(Body b : rubeScene.getBodies()) {

            String bodyName = (String) rubeScene.getCustom(b, "name");
            Gdx.app.log("bodyName", bodyName);
            Sprite sprite = new Sprite(atlas.findRegion(bodyName));

            /*
            * Tomas scales images in spine
            * to access that scale do:
            * ((RegionAttachment)skeleton.findSlot(bodyName).getAttachment()).getScaleX()
            */

            for (RubeImage i : rubeScene.getImages()) {
                if (i.body == b) {
                    sprite.flip(i.flip, false);
                    sprite.setColor(i.color);
                    sprite.setOriginCenter();
                    scaleX = sprite.getWidth();
                    scaleY = sprite.getHeight();
                    sprite.setSize(i.width*Physics.PIXELS_PER_METER, i.height*Physics.PIXELS_PER_METER);
                    sprite.setOriginCenter();
                    scaleX = sprite.getWidth()/scaleX;
                    scaleY = sprite.getHeight()/scaleY;
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



        skeleton.getRootBone().setScale(scaleX + 0.06f, scaleY + 0.06f);

    }

    @Override
    void onObjective(){
        this.attack();
    }

    @Override
    void move(){
        super.move();

        if(this.isAttacking) {
            this.attackTimer = System.currentTimeMillis();
        }
        else{
            this.currentAnimation = "walk";
        }

        if(System.currentTimeMillis() - this.attackTimer > this.timeBeforeAttack){
            this.attack();
        }

    }

    @Override
    void attack(){
        super.attack();

        if(!this.isCrawler) {
            if (timesCompleteAttack1 < 2) {
                this.currentAnimation = "attack1";
            } else {
                this.currentAnimation = "attack2";
            }
        }
        else{
            this.currentAnimation = "crawl_attack";
        }

    }

    @Override
    void crawl(){
        super.crawl();

        this.physicsEnabled = false;
        this.currentAnimation = "crawl";

        setPosition(parts.get("torso").physicsBody.getPosition().x, 0);
    }
    
}
