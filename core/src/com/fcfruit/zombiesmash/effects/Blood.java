package com.fcfruit.zombiesmash.effects;

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

public class Blood
{

    private Body physicsBody;
    private Fixture fixture;

    private Sprite sprite;

    private float offsetX;
    private float offsetY;
    private float rotationOffset;

    public boolean readyForDestroy = false;

    public Blood(float x, float y, float offsetX, float offsetY, float rotationOffset)
    {

        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.rotationOffset = rotationOffset;

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


        float randomForceX = new Random().nextInt(6) + 1;
        float randomForceY = new Random().nextInt(1) + 1;
        if (new Random().nextInt(2) == 1)
        {
            randomForceX = randomForceX * -1;
        }
        this.physicsBody.applyForceToCenter(new Vector2(randomForceX, randomForceY), true);
        this.physicsBody.setTransform(x, y, 0);

        sprite = new Sprite(Environment.assets.get("effects/blood/blood.png", Texture.class));
        sprite.setScale(0.25f);

    }

    public void draw(SpriteBatch batch)
    {

        float x = (float) (offsetX * Math.cos(Math.toRadians(rotationOffset)) - offsetY * Math.sin(Math.toRadians(rotationOffset)));
        float y = (float) (offsetX * Math.sin(Math.toRadians(rotationOffset)) + offsetY * Math.cos(Math.toRadians(rotationOffset)));


        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(
                new Vector3(this.physicsBody.getPosition().x - x, this.physicsBody.getPosition().y - y, 0)));

        sprite.setPosition(pos.x - sprite.getWidth() / 2, Environment.gameCamera.viewportHeight - pos.y - sprite.getHeight() / 2);

        sprite.draw(batch);

        if (this.physicsBody.getPosition().y < 0.1f)
        {
            this.readyForDestroy = true;
        }

    }

    public void destroy()
    {
        Environment.physics.destroyBody(this.physicsBody);
        this.sprite = null;
    }

}
