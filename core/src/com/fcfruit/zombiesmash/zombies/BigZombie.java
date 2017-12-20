package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-11-21.
 */

public class BigZombie extends Zombie{

    public BigZombie(Integer id) {
        super(id);

        partsToStayAlive.add("head");
        partsToStayAlive.add("torso");
        partsToStayAlive.add("left_arm");
        partsToStayAlive.add("right_arm");
        partsToStayAlive.add("left_leg");
        partsToStayAlive.add("right_leg");

        this.type = "big";

        animationSetup();

    }

    @Override
    public void animationSetup() {

        atlas = new TextureAtlas(Gdx.files.internal("zombies/big_zombie/big_zombie.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("zombies/big_zombie/big_zombie.json"));

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

    @Override
    public void constructPhysicsBody(World world, boolean flip){

        super.constructPhysicsBody(world, flip);

        parts.get("rock").isDetachable = false;

    }

    @Override
    void attack() {
        super.attack();
        if (timesCompleteAttack1 < 2) {
            this.currentAnimation = "attack1";
        } else {
            this.currentAnimation = "attack2";
        }

    }

}



