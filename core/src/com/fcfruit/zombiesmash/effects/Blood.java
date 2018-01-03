package com.fcfruit.zombiesmash.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Joint;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.Zombie;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.ENTITYDatatypeValidator;

/**
 * Created by Lucas on 2018-01-03.
 */

public class Blood {

    TextureAtlas atlas;
    Skeleton skeleton;
    AnimationState state;

    String currentAnimation;

    Part part;

    public Blood(Part p){

        part = p;

        atlas = new TextureAtlas(Gdx.files.internal("effects/blood/blood.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(0.5f); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("effects/blood/blood.json"));

        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.
        //stateData.setMix("run", "jump", 0.2f);
        //stateData.setMix("jump", "run", 0.2f);

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

        // Queue animations on track 0.
        this.currentAnimation = "bleed";
        state.setAnimation(0, currentAnimation, true);

    }

    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer, float delta){
        float scaleFactor = (float)Math.tan(Math.toRadians(part.sprite.getRotation() + 180));
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(part.physicsBody.getPosition(), 0)));
        skeleton.setPosition(pos.x, (Environment.gameCamera.viewportHeight - pos.y - part.sprite.getHeight()/2)*scaleFactor);
        skeleton.getRootBone().setRotation(part.sprite.getRotation() + 180);

        state.update(delta); // Update the animation getUpTimer.

        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.

        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        skeletonRenderer.draw(batch, skeleton);
    }

}
