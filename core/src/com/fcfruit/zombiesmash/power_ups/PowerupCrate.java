package com.fcfruit.zombiesmash.power_ups;

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
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PowerUpEntityInterface;


/**
 * Created by Lucas on 2018-03-10.
 */

public class PowerupCrate implements DrawableEntityInterface, InteractiveEntityInterface, PhysicsEntityInterface
{

    private DrawablePhysicsEntity crateDrawable;
    private DrawableGraphicsEntity powerupDrawable;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;

    public PowerupCrate(PowerUpEntityInterface powerUpEntity)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body body = Environment.physics.getWorld().createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.1f);
        circleShape.setPosition(new Vector2(0, 0));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.friction = 0;
        fixtureDef.density = 0.3f;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        this.crateDrawable = new DrawablePhysicsEntity(new Sprite(new Texture(Gdx.files.internal("powerups/crate.png"))),
                body);

        this.powerupDrawable = powerUpEntity.get_ui_image();

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.crateDrawable.getSize(), 0)));
        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});

        this.interactiveGraphicsEntity = new InteractiveGraphicsEntity(this.crateDrawable, polygon);


    }

    @Override
    public Body getPhysicsBody()
    {
        return this.crateDrawable.getPhysicsBody();
    }

    @Override
    public void onTouchDown(float screenX, float screenY, int pointer)
    {
        this.interactiveGraphicsEntity.onTouchDown(screenX, screenY, pointer);
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
        this.crateDrawable.draw(batch);
        //this.powerupDrawable.draw(batch);
    }

    @Override
    public boolean isTouching()
    {
        return this.interactiveGraphicsEntity.isTouching();
    }

    @Override
    public void update(float delta)
    {
        this.crateDrawable.update(delta);

        //this.powerupDrawable.setPosition(new Vector2(this.crateDrawable.getPosition().x + 0.1f, this.crateDrawable.getPosition().y + 0.1f));
        //this.powerupDrawable.update(delta);

        this.interactiveGraphicsEntity.update(delta);
    }

    @Override
    public Polygon getPolygon()
    {
        return this.interactiveGraphicsEntity.getPolygon();
    }

    @Override
    public Vector2 getPosition()
    {
        return this.crateDrawable.getPosition();
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.crateDrawable.setPosition(position);
    }

    @Override
    public float getAngle()
    {
        return this.crateDrawable.getAngle();
    }

    @Override
    public void setAngle(float angle)
    {
        this.crateDrawable.setAngle(angle);
    }

    @Override
    public Vector2 getSize()
    {
        return this.crateDrawable.getSize();
    }

    @Override
    public void dispose()
    {
        this.crateDrawable.dispose();
        this.powerupDrawable.dispose();
    }

    // Unsued
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }

}
