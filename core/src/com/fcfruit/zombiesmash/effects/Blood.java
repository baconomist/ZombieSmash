package com.fcfruit.zombiesmash.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.fcfruit.zombiesmash.Environment;

import java.util.Random;

/**
 * Created by Lucas on 2018-01-03.
 */

public class Blood {
/*
    TextureAtlas atlas;
    Skeleton skeleton;
    AnimationState state;

    String currentAnimation;

    Part part;

    float offsetX;
    float offsetY;
    float rotationOffset;

    public Blood(Part p, float offsetx, float offsety, float rotoffset){
        part = p;
        offsetX = offsetx;
        offsetY = offsety;
        rotationOffset = rotoffset;

        atlas = new TextureAtlas(Gdx.files.internal("effects/blood/blood.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(0.3f); // Load the skeleton at 100% the size it was in Spine.
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

        //float x = (float)(part.sprite.getWidth()/2 * Math.cos(Math.toRadians(part.sprite.getRotation() + 180)) - part.sprite.getHeight()/2 * Math.sin(Math.toRadians(part.sprite.getRotation() + 180)));
        //float y = (float)(part.sprite.getWidth()/2 * Math.sin(Math.toRadians(part.sprite.getRotation() + 180)) + part.sprite.getHeight()/2 * Math.cos(Math.toRadians(part.sprite.getRotation() + 180)));
        float x = (float)(offsetX * Math.cos(Math.toRadians(part.sprite.getRotation() + rotationOffset)) - offsetY * Math.sin(Math.toRadians(part.sprite.getRotation() + rotationOffset)));
        float y = (float)(offsetX * Math.sin(Math.toRadians(part.sprite.getRotation() + rotationOffset)) + offsetY * Math.cos(Math.toRadians(part.sprite.getRotation() + rotationOffset)));

        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(part.physicsBody.getPosition().x + x, part.physicsBody.getPosition().y + y, 0)));
        skeleton.setPosition(pos.x, Environment.gameCamera.viewportHeight - pos.y);
        skeleton.getRootBone().setRotation(part.sprite.getRotation() + rotationOffset);

        state.update(delta); // Update the animation getUpTimer.

        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.

        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        skeletonRenderer.draw(batch, skeleton);
    }
*/

    private Body physicsBody;
    private Fixture fixture;

    private Sprite sprite;

    private float offsetX;
    private float offsetY;
    private float rotationOffset;

    public Blood(float x, float y, float offsetX, float offsetY, float rotationOffset){

        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.rotationOffset = rotationOffset;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBody = Environment.physics.getWorld().createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.1f);
        circleShape.setPosition(new Vector2(0, 0));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.friction = 0;
        fixtureDef.density = 0.3f;

        fixture = physicsBody.createFixture(fixtureDef);
        fixture.setUserData(this);


        float randomForceX = new Random().nextInt(6) + 1;
        float randomForceY = new Random().nextInt(1) + 1;
        if(new Random().nextInt(2) == 1){
            randomForceX = randomForceX * -1;
        }
        physicsBody.applyForceToCenter(new Vector2(randomForceX, randomForceY), true);
        physicsBody.setTransform(x, y, 0);

        sprite = new Sprite(new Texture(Gdx.files.internal("effects/blood/blood.png")));
        sprite.setScale(0.25f);

    }

    public void draw(SpriteBatch batch){
        float x = (float)(offsetX * Math.cos(Math.toRadians(rotationOffset)) - offsetY * Math.sin(Math.toRadians(rotationOffset)));
        float y = (float)(offsetX * Math.sin(Math.toRadians(rotationOffset)) + offsetY * Math.cos(Math.toRadians(rotationOffset)));


        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(
                new Vector3(physicsBody.getPosition().x - x, physicsBody.getPosition().y - y, 0)));

        sprite.setPosition(pos.x - sprite.getWidth()/2, Environment.gameCamera.viewportHeight - pos.y - sprite.getHeight()/2);
        if(physicsBody.getPosition().y > 0.2f){
            sprite.draw(batch);
        }
    }

}
