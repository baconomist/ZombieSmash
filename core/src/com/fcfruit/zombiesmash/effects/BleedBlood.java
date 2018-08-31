package com.fcfruit.zombiesmash.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.physics.PhysicsData;

import java.util.Random;

/**
 * Created by Lucas on 2018-01-03.
 */

public class BleedBlood implements DrawableEntityInterface, PhysicsEntityInterface
{

    public boolean enabled = false;

    private DrawablePhysicsEntity drawablePhysicsEntity;
    private Body physicsBody;
    private Fixture fixture;

    private GroundBlood groundBlood;

    public BleedBlood()
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.1f);
        circleShape.setPosition(new Vector2(0, 0));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.friction = 0;
        fixtureDef.density = 0.3f;
        
        this.physicsBody = Environment.physics.createBody(bodyDef);

        fixture = this.physicsBody.createFixture(fixtureDef);
        fixture.setUserData(new PhysicsData(this));

        Sprite sprite = new Sprite(Environment.assets.get("effects/blood/flowing_blood/flowing_blood.atlas", TextureAtlas.class).findRegion(""+(new Random().nextInt(13)+1)));
        sprite.setScale(0.5f);
        this.drawablePhysicsEntity = new DrawablePhysicsEntity(sprite, this.physicsBody);

        circleShape.dispose();
        circleShape = null;
        fixtureDef = null;
        bodyDef = null;

        this.groundBlood = new GroundBlood();

        // Blood is disabled by default
        this.disable();

    }

    public void enable(Vector2 center, Vector2 direction)
    {
        this.physicsBody.setTransform(center, 0);
        this.physicsBody.setActive(true);

        // Set blood trajectory and scale down speed to half
        // Also negate trajectory cus in BleedablePoint the physics_offset is subtracted from the physics_body_pos
        this.physicsBody.applyLinearImpulse(direction.scl(-0.5f), this.physicsBody.getPosition(), true);

        this.enabled = true;
    }

    public void disable()
    {
        this.physicsBody.setActive(false);
        this.physicsBody.setLinearVelocity(0, 0);
        this.physicsBody.setTransform(99, 99, 0);

        this.enabled = false;
    }

    private boolean isInLevel()
    {
        DrawableEntityInterface i = this;

        return i.getPosition().x > Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2 - i.getSize().x
                && i.getPosition().x < Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth / 2 + i.getSize().x;
    }

    public void draw(SpriteBatch batch)
    {
        if (this.getPosition().y < 0.1f && this.isInLevel())
        {
            this.groundBlood.enable();
            this.groundBlood.setPosition(this.getPosition());
            Environment.drawableBackgroundAddQueue.add(this.groundBlood);

            Environment.bleedableBloodPool.returnBlood(this);
            Environment.drawableRemoveQueue.add(this);
        }
        else if(!this.isInLevel())
        {
            Environment.bleedableBloodPool.returnBlood(this);
            Environment.drawableRemoveQueue.add(this);
        }
        else if(this.enabled)
            this.drawablePhysicsEntity.draw(batch);

    }

    @Override
    public void update(float delta)
    {
        if(this.enabled)
            this.drawablePhysicsEntity.update(delta);
    }

    @Override
    public Body getPhysicsBody()
    {
        return this.physicsBody;
    }

    @Override
    public Vector2 getPosition()
    {
        return this.drawablePhysicsEntity.getPosition();
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.drawablePhysicsEntity.setPosition(position);
    }

    @Override
    public float getAngle()
    {
        return this.drawablePhysicsEntity.getAngle();
    }

    @Override
    public void setAngle(float angle)
    {
        this.drawablePhysicsEntity.setAngle(angle);
    }

    @Override
    public float getAlpha()
    {
        return this.drawablePhysicsEntity.getAlpha();
    }

    @Override
    public void setAlpha(float alpha)
    {
        this.drawablePhysicsEntity.setAlpha(alpha);
    }

    @Override
    public Vector2 getSize()
    {
        return this.drawablePhysicsEntity.getSize();
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }

    @Override
    public void dispose()
    {

    }

}
