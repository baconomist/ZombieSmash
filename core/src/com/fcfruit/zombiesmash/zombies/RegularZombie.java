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
 * Created by Lucas on 2017-07-21.
 */

public class RegularZombie extends Zombie {

    public RegularZombie(Integer id) {
        super(id);

        this.moveAnimation = "run";

        partsToStayAlive.add("head");
        partsToStayAlive.add("torso");
        partsToStayAlive.add(new String[]{"left_arm", "right_arm"});

        type = "reg";

        animationSetup();

    }


    @Override
    void onGetUp(){
        this.currentAnimation = "run";
    }

    @Override
    void crawl(){
        super.crawl();
        this.physicsEnabled = false;
        this.currentAnimation = "crawl";

        setPosition(parts.get("torso").physicsBody.getPosition().x, 0);
    }

    @Override
    void attack() {
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

}
