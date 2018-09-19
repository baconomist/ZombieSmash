package com.fcfruit.zombiesmash.brains;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.physics.PhysicsData;

public class Brain implements DrawableEntityInterface, InteractiveEntityInterface
{
    public int value;

    private DrawablePhysicsEntity drawablePhysicsEntity;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;

    private double destroyTimer;
    private double timeBeforeDestroy = 5000;

    public boolean enabled = false;

    public Brain(int value)
    {
        // Value 1-3
        this.value = value;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.gravityScale = 0; // No gravity effect
        bodyDef.active = false;
        bodyDef.position.x = 99;
        bodyDef.position.y = 99;
        bodyDef.linearDamping = 0.5f; // Drag, slows down body over time

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.1f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;

        Body body = Environment.physics.createBody(bodyDef);
        Fixture fixture = body.createFixture(fixtureDef);

        body.setUserData(new PhysicsData(this));
        fixture.setUserData(new PhysicsData(this));

        Sprite sprite = new Sprite(Environment.assets.get("brains/brain"+this.value+".png", Texture.class));
        sprite.setSize(sprite.getWidth()/2, sprite.getHeight()/2);

        sprite.setOriginCenter();

        this.drawablePhysicsEntity = new DrawablePhysicsEntity(sprite, body);

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.position.y*2 - size.y;

        Polygon polygon = new Polygon(new float[]{0, 0, size.x*2, 0, size.x*2, size.y*2, 0, size.y*2});
        polygon.setOrigin(size.x, size.y);
        this.interactiveGraphicsEntity = new InteractiveGraphicsEntity(this.drawablePhysicsEntity, polygon);
    }

    public void enable(Vector2 position, Vector2 velocity)
    {
        this.drawablePhysicsEntity.setPosition(position);
        this.drawablePhysicsEntity.getPhysicsBody().setActive(true);
        this.drawablePhysicsEntity.getPhysicsBody().setLinearVelocity(velocity);
        this.destroyTimer = System.currentTimeMillis();
        this.setAlpha(1);
        this.enabled = true;
    }

    public void disable()
    {
        this.drawablePhysicsEntity.getPhysicsBody().setActive(false);
        this.drawablePhysicsEntity.getPhysicsBody().setLinearVelocity(0, 0);
        this.drawablePhysicsEntity.setPosition(new Vector2(99, 99));
        this.drawablePhysicsEntity.update(Gdx.graphics.getDeltaTime());
        this.enabled = false;
    }

    private void onTouched()
    {
        Environment.level.brainCounter += this.value;
        this.disable();
        Environment.brainPool.returnBrain(this);
        Environment.drawableRemoveQueue.add(this);
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
        this.interactiveGraphicsEntity.update(delta);

        if(this.getAlpha() <= 0)
        {
            this.disable();
            Environment.brainPool.returnBrain(this);
            Environment.drawableRemoveQueue.add(this);
        }

        if(System.currentTimeMillis() - this.timeBeforeDestroy >= this.destroyTimer)
            this.setAlpha(this.getAlpha() - delta/4);

        this.setAngle(this.getAngle() + delta*20);

    }

    @Override
    public void onTouchDown(float screenX, float screenY, int pointer)
    {
        this.interactiveGraphicsEntity.onTouchDown(screenX, screenY, pointer);

        if(this.isTouching())
            this.onTouched();
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
        this.drawablePhysicsEntity.dispose();
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

    // Unused
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }

}
