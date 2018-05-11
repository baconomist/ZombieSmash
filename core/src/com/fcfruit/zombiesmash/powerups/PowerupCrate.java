package com.fcfruit.zombiesmash.powerups;

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
import com.fcfruit.zombiesmash.entity.interfaces.MultiGroundEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface;


/**
 * Created by Lucas on 2018-03-10.
 */

public class PowerupCrate implements DrawableEntityInterface, InteractiveEntityInterface, PhysicsEntityInterface, MultiGroundEntityInterface
{

    private PowerupInterface powerup;

    private DrawablePhysicsEntity crateDrawable;
    private Sprite powerupUIDrawable;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;

    private int currentGround;

    private boolean isFloatingUp;

    private boolean isOpening;
    private boolean isOpen;

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

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        this.crateDrawable = new DrawablePhysicsEntity(new Sprite(new Texture(Gdx.files.internal("powerups/crate.png"))),
                body);


        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.position.y*2 - size.y;

        this.powerupUIDrawable = powerup.getUIDrawable();
        this.powerupUIDrawable.setSize(size.x, size.y);
        this.powerupUIDrawable.setOriginCenter();

        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x / 2, size.y / 2);
        this.interactiveGraphicsEntity = new InteractiveGraphicsEntity(this.crateDrawable, polygon);

        Environment.powerupManager.addCrate(this);
    }

    private void open()
    {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getPosition().x - this.getSize().x / 2, this.getPosition().y - this.getSize().y / 2, 0)));
        pos.y = Environment.gameCamera.position.y*2 - pos.y;

        this.powerupUIDrawable.setPosition(pos.x, pos.y);

        this.crateDrawable = new DrawablePhysicsEntity(new Sprite(new Texture(Gdx.files.internal("powerups/crate_open.png"))), this.crateDrawable.getPhysicsBody());
        this.isOpening = true;
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
        if (this.isTouching() && !this.isOpening && Environment.powerupManager.has_room_for_powerup())
        {
            this.open();
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
        this.crateDrawable.draw(batch);
        if(this.isOpening)
            this.powerupUIDrawable.draw(batch);
    }

    @Override
    public boolean isTouching()
    {
        return this.interactiveGraphicsEntity.isTouching();
    }

    @Override
    public void update(float delta)
    {

        if (this.isOpening)
        {
            this.getPhysicsBody().setLinearVelocity(0, 0);
            this.setAngle(0);
        } else if (this.isFloatingUp && this.getPosition().y < 2)
        {
            this.getPhysicsBody().setGravityScale(0);
            this.getPhysicsBody().setLinearVelocity(0, 0.3f);
            this.setAngle(this.getAngle() + 1);
        } else if (!this.isFloatingUp)
        {
            this.isFloatingUp = this.getPosition().y < Environment.physics.getGroundBodies().get(this.currentGround).getPosition().y + 0.2f;

            this.getPhysicsBody().setGravityScale(0.01f);
            this.setAngle(this.getAngle() + 1);
        } else
        {
            this.getPhysicsBody().setLinearVelocity(0, 0);
            this.setAngle(this.getAngle() + 1);
        }

        this.crateDrawable.update(delta);

        if (this.isOpening)
        {
            Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.powerupUIDrawable.getX(), this.powerupUIDrawable.getY(), 0)));
            pos.y = Environment.physicsCamera.position.y*2 - pos.y;

            if (pos.y < this.crateDrawable.getPosition().y + 1)
            {
                this.powerupUIDrawable.setPosition(this.powerupUIDrawable.getX(), this.powerupUIDrawable.getY() + 100 * Gdx.graphics.getDeltaTime());
                this.powerupUIDrawable.setRotation(this.powerupUIDrawable.getRotation() + 1);
            } else
            {
                Environment.drawableRemoveQueue.add(this);
                Environment.powerupManager.addPowerup(this.powerup);
                this.isOpening = false;
                this.isOpen = true;
            }
        }

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
    public void dispose()
    {
        this.crateDrawable.dispose();
    }


    // Unsued
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }

    @Override
    public void resetToInitialGround()
    {

    }

    @Override
    public void setInitialGround(int initialGround)
    {

    }

}
