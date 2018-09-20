package com.fcfruit.zombiesmash.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Config;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.AnimatablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.AnimatableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.MultiGroundEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface;
import com.fcfruit.zombiesmash.physics.PhysicsData;

import java.util.Random;


/**
 * Created by Lucas on 2018-03-10.
 */

public class PowerupCrate implements DrawableEntityInterface, InteractiveEntityInterface, PhysicsEntityInterface, MultiGroundEntityInterface, AnimatableEntityInterface
{

    private PowerupInterface powerup;

    private AnimatablePhysicsEntity animatablePhysicsEntity;
    private Sprite powerupUIDrawable;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;

    private int currentGround;

    private boolean isFloatingUp;

    private boolean isOpening;
    private boolean isOpen;

    private double timeBeforeExpire = 5000;
    private double expiryTimer = System.currentTimeMillis();

    public PowerupCrate(PowerupInterface powerup)
    {

        this.powerup = powerup;

        this.isOpening = false;
        this.isOpen = false;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body body = Environment.physics.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.1f);
        circleShape.setPosition(new Vector2(0, 0));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.friction = 0;
        fixtureDef.density = 0.3f;
        fixtureDef.filter.groupIndex = -1;
        fixtureDef.restitution = 1;

        Fixture fixture = body.createFixture(fixtureDef);

        body.setUserData(new PhysicsData(this));
        fixture.setUserData(new PhysicsData(this));

        TextureAtlas atlas = Environment.assets.get("powerups/box.atlas", TextureAtlas.class);
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("powerups/box.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).

        state.addListener(new AnimationState.AnimationStateAdapter()
        {
            @Override
            public void event(AnimationState.TrackEntry entry, Event event)
            {
                onAnimationEvent(entry, event);
                super.event(entry, event);
            }
        });

        this.animatablePhysicsEntity = new AnimatablePhysicsEntity(skeleton, state, atlas, body);
        this.animatablePhysicsEntity.getSkeleton().getRootBone().setScale(0.5f);

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.position.y*2 - size.y;

        this.powerupUIDrawable = powerup.getUIDrawable();
        this.powerupUIDrawable.setSize(size.x, size.y);
        this.powerupUIDrawable.setOriginCenter();

        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x / 2, size.y / 2);
        this.interactiveGraphicsEntity = new InteractiveGraphicsEntity(this.animatablePhysicsEntity, polygon);

        Environment.powerupManager.addCrate(this);

        if(Config.DEBUG_CRATES)
            Environment.powerupManager.addPowerup(this.powerup);
    }

    private void onAnimationEvent(AnimationState.TrackEntry entry, Event event)
    {
        // Only do once
        if(event.getData().getName().equals("spawn_powerup") && this.timesAnimationCompleted() < 1)
            this.open();
    }

    private void open()
    {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getPosition().x - this.getSize().x / 2, this.getPosition().y - this.getSize().y / 2, 0)));
        pos.y = Environment.gameCamera.position.y*2 - pos.y;

        this.powerupUIDrawable.setPosition(pos.x, pos.y);
        
        // Prevents graphic glitch at position (0, 0)
        this.isOpening = true;

        //this.crateDrawable = new DrawablePhysicsEntity(new Sprite(new Texture(Gdx.files.internal("powerups/crate_open.png"))), this.crateDrawable.getPhysicsBody());
    }

    @Override
    public Body getPhysicsBody()
    {
        return this.animatablePhysicsEntity.getPhysicsBody();
    }

    @Override
    public void onTouchDown(float screenX, float screenY, int pointer)
    {
        // Prevents not being able to touch other crates
        if(!this.isOpening)
            this.interactiveGraphicsEntity.onTouchDown(screenX, screenY, pointer);

        if (this.isTouching() && !this.isOpening && Environment.powerupManager.has_room_for_powerup())
        {
            this.setAlpha(1);
            this.setAnimation("open");
        }
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
    public void draw(SpriteBatch batch)
    {
        if(this.isOpening)
            this.powerupUIDrawable.draw(batch);
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        if(this.timesAnimationCompleted() < 1)
            this.animatablePhysicsEntity.draw(batch, skeletonRenderer);
    }

    @Override
    public boolean isTouching()
    {
        return this.interactiveGraphicsEntity.isTouching();
    }

    @Override
    public void update(float delta)
    {

        if(this.isOpening)
        {
            this.getPhysicsBody().setLinearVelocity(0, 0);
            this.setAngle(0);
        }
        else
        {
            this.setAngle(this.getAngle() + delta*50);
        }

        if(this.getPosition().y >= 3f && this.isFloatingUp)
        {
            this.getPhysicsBody().setLinearDamping(17);
            this.getPhysicsBody().setGravityScale(0);
        }
        else if(!this.isFloatingUp)
            this.isFloatingUp = this.getPosition().y <= Environment.physics.getGroundBodies().get(this.currentGround).getPosition().y + 0.5f;


        if(System.currentTimeMillis() - this.expiryTimer > this.timeBeforeExpire && !this.isOpening)
        {
            this.setAlpha(this.getAlpha()-0.25f*Gdx.graphics.getDeltaTime());
        }
        if(this.getAlpha() == 0)
        {
            this.isOpening = false;
            this.isOpen = true;
            Environment.drawableRemoveQueue.add(this);

            this.getPhysicsBody().setActive(false);
            Environment.physics.destroyBody(this.getPhysicsBody());
        }

        if (this.isOpening)
        {

            Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.powerupUIDrawable.getX(), this.powerupUIDrawable.getY(), 0)));
            pos.y = Environment.physicsCamera.position.y*2 - pos.y;

            if (pos.y < this.animatablePhysicsEntity.getPosition().y + 1)
            {
                this.powerupUIDrawable.setPosition(this.powerupUIDrawable.getX(), this.powerupUIDrawable.getY() + 100 * Gdx.graphics.getDeltaTime());
                this.powerupUIDrawable.setRotation(this.powerupUIDrawable.getRotation() + 1);
            } else
            {
                Environment.drawableRemoveQueue.add(this);
                Environment.powerupManager.addPowerup(this.powerup);

                this.getPhysicsBody().setActive(false);
                Environment.physics.destroyBody(this.getPhysicsBody());

                this.isOpening = false;
                this.isOpen = true;
            }
        }

        this.animatablePhysicsEntity.update(delta);
        this.interactiveGraphicsEntity.update(delta);

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
    public Vector2 getSize()
    {
        return this.animatablePhysicsEntity.getSize();
    }

    @Override
    public void changeToGround(int ground)
    {
        this.currentGround = ground;
    }

    @Override
    public int getInitialGround()
    {
        return this.currentGround;
    }

    @Override
    public int getCurrentGround()
    {
        return this.currentGround;
    }

    @Override
    public boolean isMovingToNewGround()
    {
        return false;
    }

    public boolean isOpening(){return this.isOpening;}

    public boolean isOpen(){return this.isOpen;}

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
    public void dispose()
    {
        this.animatablePhysicsEntity.dispose();
    }


    // Unsued

    @Override
    public void resetToInitialGround()
    {

    }

    @Override
    public void setInitialGround(int initialGround)
    {

    }

}
