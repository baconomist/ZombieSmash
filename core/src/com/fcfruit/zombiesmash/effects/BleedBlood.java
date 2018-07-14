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
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;

import java.util.Random;

/**
 * Created by Lucas on 2018-01-03.
 */

public class BleedBlood implements DrawableEntityInterface
{

    private DrawablePhysicsEntity drawablePhysicsEntity;
    private Body physicsBody;
    private Fixture fixture;

    public boolean readyForDestroy = false;

    public BleedBlood(Vector2 center, Vector2 direction)
    {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        this.physicsBody = Environment.physics.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.1f);
        circleShape.setPosition(new Vector2(0, 0));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.friction = 0;
        fixtureDef.density = 0.3f;

        fixture = this.physicsBody.createFixture(fixtureDef);
        fixture.setUserData(this);

        this.physicsBody.setTransform(center, 0);

        this.drawablePhysicsEntity = new DrawablePhysicsEntity(new Sprite(Environment.assets.get("effects/blood/flowing_blood/"+(new Random().nextInt(13)+1)+".png", Texture.class)), this.physicsBody);
        Environment.drawableAddQueue.add(this.drawablePhysicsEntity);

        // Set blood trajectory and scale down speed to half
        // Also negate trajectory cus in BleedablePoint the physics_offset is subtracted from the physics_body_pos
        this.physicsBody.applyLinearImpulse(direction.scl(-0.5f), this.physicsBody.getPosition(), true);

    }

    public void draw(SpriteBatch batch)
    {
        this.drawablePhysicsEntity.draw(batch);

        if (this.physicsBody.getPosition().y < 0.1f)
        {
            GroundBlood groundBlood = new GroundBlood();
            groundBlood.setPosition(this.physicsBody.getPosition());
            Environment.groundBloodAddQueue.add(groundBlood);

            this.readyForDestroy = true;
        }

    }

    @Override
    public void update(float delta)
    {
        this.drawablePhysicsEntity.update(delta);
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

    @Override
    public void dispose()
    {
        Environment.physics.destroyBody(this.physicsBody);
        this.drawablePhysicsEntity.dispose();
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }

}
