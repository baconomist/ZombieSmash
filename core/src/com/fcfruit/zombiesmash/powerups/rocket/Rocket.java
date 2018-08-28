package com.fcfruit.zombiesmash.powerups.rocket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.ExplodableEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.ExplodableEntityInterface;
import com.fcfruit.zombiesmash.physics.PhysicsData;

public class Rocket implements DrawableEntityInterface, ExplodableEntityInterface
{
    private DrawablePhysicsEntity drawablePhysicsEntity;
    private ExplodableEntity explodableEntity;

    public boolean enabled = false;

    public Rocket()
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.angle = (float) Math.toRadians(-125);
        bodyDef.active = false;
        bodyDef.position.x = 99;
        bodyDef.position.y = 99;

        Shape shape = new CircleShape();
        shape.setRadius(0.1f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        Body body = Environment.physics.createBody(bodyDef);
        Fixture fixture = body.createFixture(fixtureDef);

        body.setUserData(new PhysicsData(this));
        fixture.setUserData(new PhysicsData(this));

        Sprite sprite = new Sprite(new Texture(Gdx.files.internal("powerups/rocket/rocket.png")));
        sprite.setSize(sprite.getWidth()/2, sprite.getHeight()/2);
        sprite.setOriginCenter();

        this.drawablePhysicsEntity = new DrawablePhysicsEntity(sprite, body);

        this.explodableEntity = new ExplodableEntity(this, 60f);

        this.explodableEntity.getState().addListener(new AnimationState.AnimationStateAdapter()
        {
            @Override
            public void complete(AnimationState.TrackEntry entry)
            {
                onExplodeableEntityAnimationComplete();
                super.complete(entry);
            }
        });
    }

    private void onExplodeableEntityAnimationComplete()
    {
        Environment.rocketPool.returnRocket(this);
    }

    public void enable()
    {
        this.getPhysicsBody().setTransform(this.getPosition(), this.getPhysicsBody().getAngle());
        this.getPhysicsBody().setActive(true);

        this.explodableEntity.exploded = false;
        this.explodableEntity.isAnimating = false;

        this.enabled = true;
    }

    public void disable()
    {
        this.getPhysicsBody().setLinearVelocity(0, 0);
        this.getPhysicsBody().setActive(false);
        this.getPhysicsBody().setTransform(99, 99, this.getPhysicsBody().getAngle());

        this.enabled = false;
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        this.drawablePhysicsEntity.draw(batch);
    }

    @Override
    public void update(float delta)
    {
        this.drawablePhysicsEntity.update(delta);
    }

    @Override
    public void explode()
    {
        this.explodableEntity.explode();
        Environment.drawableAddQueue.add(this.explodableEntity);
        Environment.drawableRemoveQueue.add(this);
    }

    @Override
    public boolean shouldExplode()
    {
        return this.explodableEntity.shouldExplode();
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
    public Body getPhysicsBody()
    {
        return this.drawablePhysicsEntity.getPhysicsBody();
    }

    @Override
    public void dispose()
    {
        this.drawablePhysicsEntity.dispose();
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }
}
