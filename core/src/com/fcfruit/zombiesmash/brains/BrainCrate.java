package com.fcfruit.zombiesmash.brains;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.AnimatablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.AnimatableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.physics.PhysicsData;

import java.util.Random;

public class BrainCrate implements AnimatableEntityInterface, InteractiveEntityInterface
{

    private AnimatablePhysicsEntity animatablePhysicsEntity;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;

    private boolean isFalling = true;
    private boolean isOpening = false;

    public BrainCrate(float posx)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.x = posx;
        bodyDef.position.y = 3f;
        bodyDef.gravityScale = 0.5f;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.1f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;


        TextureAtlas atlas = Environment.assets.get("brains/brain_crate.atlas", TextureAtlas.class);
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("brains/brain_crate.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).

        state.addListener(new AnimationState.AnimationStateAdapter()
        {
            @Override
            public void complete(AnimationState.TrackEntry entry)
            {
                onAnimationComplete(entry);
                super.complete(entry);
            }
        });


        this.animatablePhysicsEntity = new AnimatablePhysicsEntity(skeleton, state, atlas, Environment.physics.createBody(bodyDef));
        this.animatablePhysicsEntity.getSkeleton().getRootBone().setScale(0.5f);
        this.animatablePhysicsEntity.getPhysicsBody().setUserData(new PhysicsData(this));
        this.animatablePhysicsEntity.getPhysicsBody().createFixture(fixtureDef).setUserData(new PhysicsData(this));
        circleShape.dispose();

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.position.y*2 - size.y;

        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x /2, size.y / 2);
        this.interactiveGraphicsEntity = new InteractiveGraphicsEntity(this.animatablePhysicsEntity, polygon);

    }

    private void onAnimationComplete(AnimationState.TrackEntry entry)
    {
        if(this.timesAnimationCompleted() < 1)
        {
            this.explodeBrains();
            this.animatablePhysicsEntity.getState().clearTracks();
        }
    }

    private void explodeBrains()
    {
        int brain_num = new Random().nextInt(10) + 5;
        for(int i = 0; i < brain_num; i++)
        {
            double angle = Math.toRadians(((180 / brain_num) * i) - 90d);
            Vector2 rayDir = new Vector2((float) Math.sin(angle), (float) Math.cos(angle));
            Environment.drawableAddQueue.add(Environment.brainPool.getBrain(new Random().nextInt(3) + 1, this.getPosition(), new Vector2(rayDir.scl(2f))));
        }
        Environment.drawableRemoveQueue.add(this);
    }

    private void open()
    {
        this.animatablePhysicsEntity.setAnimation("open");
        this.isOpening = true;
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.animatablePhysicsEntity.draw(batch, skeletonRenderer);
    }

    @Override
    public void update(float delta)
    {
        this.animatablePhysicsEntity.update(delta);
        this.interactiveGraphicsEntity.update(delta);

        if(!this.isFalling && !this.isOpening && this.getPosition().y < 3f)
        {
            this.animatablePhysicsEntity.getPhysicsBody().setActive(false);
            this.setPosition(new Vector2(this.getPosition().x, this.getPosition().y + delta*2));
        }
        else
        {
            this.isFalling = this.getPosition().y > 1f;
        }

        if(!this.isOpening)
            this.setAngle(this.getAngle() + delta*50);
        else
            this.setAngle(0);

    }


    @Override
    public void onTouchDown(float screenX, float screenY, int pointer)
    {
        this.interactiveGraphicsEntity.onTouchDown(screenX, screenY, pointer);

        if(this.isTouching())
            this.open();
    }

    @Override
    public void onTouchDragged(float screenX, float screenY, int pointer)
    {
        this.interactiveGraphicsEntity.onTouchDragged(screenX, screenY, pointer);
    }

    @Override
    public void onTouchUp(float screenX, float screenY, int pointer)
    {
        this.interactiveGraphicsEntity.onTouchUp(screenX, screenY, pointer);
    }

    @Override
    public Skeleton getSkeleton()
    {
        return this.animatablePhysicsEntity.getSkeleton();
    }

    @Override
    public AnimationState getState()
    {
        return this.animatablePhysicsEntity.getState();
    }

    @Override
    public TextureAtlas getAtlas()
    {
        return this.animatablePhysicsEntity.getAtlas();
    }

    @Override
    public int timesAnimationCompleted()
    {
        return this.animatablePhysicsEntity.timesAnimationCompleted();
    }

    @Override
    public void setAnimation(String animation)
    {
        this.animatablePhysicsEntity.setAnimation(animation);
    }

    @Override
    public String getCurrentAnimation()
    {
        return this.animatablePhysicsEntity.getCurrentAnimation();
    }

    @Override
    public boolean isTouching()
    {
        return this.interactiveGraphicsEntity.isTouching();
    }

    @Override
    public Polygon getPolygon()
    {
        return this.interactiveGraphicsEntity.getPolygon();
    }


    @Override
    public Vector2 getPosition()
    {
        return this.animatablePhysicsEntity.getPosition();
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.animatablePhysicsEntity.setPosition(position);
    }

    @Override
    public float getAngle()
    {
        return this.animatablePhysicsEntity.getAngle();
    }

    @Override
    public void setAngle(float angle)
    {
        this.animatablePhysicsEntity.setAngle(angle);
    }

    @Override
    public float getAlpha()
    {
        return this.animatablePhysicsEntity.getAlpha();
    }

    @Override
    public void setAlpha(float alpha)
    {
        this.animatablePhysicsEntity.setAlpha(alpha);
    }

    @Override
    public Vector2 getSize()
    {
        return this.animatablePhysicsEntity.getSize();
    }

    @Override
    public void dispose()
    {

    }

    // Unused

    @Override
    public void draw(SpriteBatch batch)
    {

    }

}
