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

import java.util.HashMap;
import java.util.Random;

/**
 * Created by Lucas on 2017-11-06.
 */

public class PoliceZombie extends Zombie {


    public PoliceZombie(Integer id) {
        super(id);

        this.moveAnimation = "walk";

        partsToStayAlive.add("head");
        partsToStayAlive.add("torso");
        partsToStayAlive.add(new String[]{"left_arm", "right_arm"});

        this.type = "police";

        animationSetup();

    }



    @Override
    void move(){
        super.move();

        if(this.isAttacking) {
            this.attackTimer = System.currentTimeMillis();
        }
        else if(!this.isCrawler){
            this.currentAnimation = "walk";
        }
        else{
            this.currentAnimation = "crawl";
        }

        if(this.enteredLevel && System.currentTimeMillis() - this.attackTimer > this.timeBeforeAttack){
            this.attack();
            this.attackTimer = System.currentTimeMillis();
        }



    }

    @Override
    void attack(){
        super.attack();

        //If police zombie has an arm, he can still attack while walking
        if(parts.get("left_arm") != null || parts.get("right_arm") != null) {
            //If zombie is a crawler, play crawler animation
            // else, do regular attack anims
            if (!this.isCrawler) {
                if (timesCompleteAttack1 < 2) {
                    this.currentAnimation = "attack1";
                } else {
                    this.currentAnimation = "attack2";
                }
            } else {
                this.currentAnimation = "crawl_attack";
            }

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
