package com.fcfruit.zombiesmash.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.physics.PhysicsData;

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
        
        this.physicsBody = Environment.physics.createBody(Environment.bleedableBloodData.bodyDef);

        fixture = this.physicsBody.createFixture(Environment.bleedableBloodData.fixtureDef);
        fixture.setUserData(new PhysicsData(this));

        this.physicsBody.setTransform(center, 0);

        Sprite sprite = new Sprite(Environment.assets.get("effects/blood/flowing_blood/"+(new Random().nextInt(13)+1)+".png", Texture.class));
        sprite.setScale(0.5f);
        this.drawablePhysicsEntity = new DrawablePhysicsEntity(sprite, this.physicsBody);

        // Set blood trajectory and scale down speed to half
        // Also negate trajectory cus in BleedablePoint the physics_offset is subtracted from the physics_body_pos
        this.physicsBody.applyLinearImpulse(direction.scl(-0.5f), this.physicsBody.getPosition(), true);

    }

    public void draw(SpriteBatch batch)
    {

        if (this.physicsBody.getPosition().y < 0.1f)
        {
            GroundBlood groundBlood = new GroundBlood();
            groundBlood.setPosition(this.physicsBody.getPosition());
            Environment.drawableBackgroundAddQueue.add(groundBlood);

            this.readyForDestroy = true;
            this.dispose();
        }
        else if(!this.readyForDestroy)
            this.drawablePhysicsEntity.draw(batch);

    }

    @Override
    public void update(float delta)
    {
        if(!this.readyForDestroy)
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
        Environment.drawableRemoveQueue.add(this);
        this.drawablePhysicsEntity.dispose();
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }

}
