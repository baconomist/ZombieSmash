package com.fcfruit.zombiesmash.powerups.rock_powerup;


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
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DestroyableEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.physics.PhysicsData;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.Random;

/**
 * Created by Lucas on 2017-12-02.
 */

public class Rock implements com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface, com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface
{

    private com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity drawablePhysicsEntity;
    private Polygon polygon;

    private boolean isFalling;

    private DestroyableEntity destroyableEntity;

    private double timeBeforeDestroy = 1000 + new Random().nextInt(500);
    private double destroyTimer;

    private Array<Zombie> hitZombies = new Array<Zombie>();

    public Rock()
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.active = false;
        bodyDef.position.x = 99;
        bodyDef.position.y = 99;
        bodyDef.gravityScale = 2;

        Shape shape = new CircleShape();
        shape.setRadius(0.3f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        Body body = Environment.physics.createBody(bodyDef);
        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();

        body.setUserData(new PhysicsData(this));
        fixture.setUserData(new PhysicsData(this));

        Sprite sprite = new Sprite(Environment.assets.get("powerups/rock/rock.png", Texture.class));
        sprite.setScale(0.5f);
        sprite.setSize(sprite.getWidth(), sprite.getHeight());// Need to manually update size. Stupid sprite class...
        sprite.setOriginCenter();

        this.drawablePhysicsEntity = new com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity(sprite, body);


        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.position.y*2 - size.y;

        this.polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x / 2, size.y / 2);

        this.destroyableEntity = new DestroyableEntity(this, this);

    }

    public void enable()
    {
        this.getPhysicsBody().setActive(true);
    }

    private boolean isInRange(DrawableEntityInterface drawableEntityInterface)
    {
        return drawableEntityInterface.getPosition().x > this.getPosition().x - 1f
                && drawableEntityInterface.getPosition().x < this.getPosition().x + 1f;
    }

    @Override
    public void update(float delta)
    {
        this.drawablePhysicsEntity.update(delta);

        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getPosition(), 0)));
        pos.y = Environment.gameCamera.position.y*2 - pos.y;
        // Center the this.polygon on physics body
        this.polygon.setPosition(pos.x - (this.polygon.getVertices()[2] / 2), pos.y - (this.polygon.getVertices()[5] / 2));
        this.polygon.setRotation(this.getAngle());

        if(this.isFalling && this.getPosition().y < 1f)
            this.destroyTimer = System.currentTimeMillis();

        this.isFalling = this.getPosition().y > 1f;

        if(!this.isFalling && System.currentTimeMillis() - this.destroyTimer > this.timeBeforeDestroy)
            this.destroyableEntity.destroy();


        for (DrawableEntityInterface drawableEntityInterface : Environment.level.getDrawableEntities())
        {
            if (this.isFalling && drawableEntityInterface instanceof Zombie && this.isInRange(drawableEntityInterface))
            {
                if (!this.hitZombies.contains((Zombie) drawableEntityInterface, false) && Environment.areQuadrilaterallsColliding(((Zombie) drawableEntityInterface).getPolygon(), this.polygon))
                {
                    if (((Zombie) drawableEntityInterface).isAccuratePolygonColliding(this.polygon))
                    {
                        ((Zombie) drawableEntityInterface).stopGetUp();
                        ((Zombie) drawableEntityInterface).enable_physics();
                        hitZombies.add((Zombie) drawableEntityInterface);
                        for (DetachableEntityInterface detachableEntityInterface : ((Zombie) drawableEntityInterface).getDetachableEntities().values())
                        {
                            detachableEntityInterface.setForceForDetach(0.1f);
                            if (detachableEntityInterface instanceof PhysicsEntityInterface)
                                ((PhysicsEntityInterface) detachableEntityInterface).getPhysicsBody().applyLinearImpulse(new Vector2(0, -0.1f), this.getPhysicsBody().getPosition(), true);
                        }
                    }
                }
            }
        }


    }

    @Override
    public void draw(SpriteBatch batch)
    {
        this.drawablePhysicsEntity.draw(batch);
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
