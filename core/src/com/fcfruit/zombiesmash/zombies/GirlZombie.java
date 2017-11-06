package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
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
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;

import java.util.HashMap;

/**
 * Created by Lucas on 2017-11-05.
 */

public class GirlZombie extends Zombie {
    
    public GirlZombie(int id){
        super(id);

        animationSetup();

    }

    @Override
    public void animationSetup() {
        atlas = new TextureAtlas(Gdx.files.internal("zombies/girl_zombie/girl_zombie.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(ANIMSCALE);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("zombies/girl_zombie/girl_zombie.json"));

        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.
        //stateData.setMix("run", "jump", 0.2f);
        //stateData.setMix("jump", "run", 0.2f);

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

        // Queue animations on track 0.
        this.currentAnimation = "run";
        state.setAnimation(0, currentAnimation, true);

        state.addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                super.complete(entry);
            }
        });

    }

    @Override
    public void constructPhysicsBody(World world){
        RubeSceneLoader loader = new RubeSceneLoader(world);
        rubeScene = loader.loadScene(Gdx.files.internal("zombies/girl_zombie/girl_zombie_rube.json"));

        parts = new HashMap<String, Part>();

        for(Body b : rubeScene.getBodies()) {

            String bodyName = (String) rubeScene.getCustom(b, "name");
            Gdx.app.log("bodyName", bodyName);
            Sprite sprite = new Sprite(atlas.findRegion(bodyName));

            for (RubeImage i : rubeScene.getImages()) {
                if (i.body == b) {
                    sprite.flip(i.flip, false);
                    sprite.setColor(i.color);
                    sprite.setOriginCenter();
                    sprite.setSize(ANIMSCALE*sprite.getWidth(), ANIMSCALE*sprite.getHeight());
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
                f.setUserData(this.id);
            }

            parts.put(bodyName, new Part(bodyName, sprite, b, joint, this));

        }
    }

    @Override
    void crawl(){

    }


    
}
