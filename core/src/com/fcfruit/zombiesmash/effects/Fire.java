package com.fcfruit.zombiesmash.effects;

import com.badlogic.gdx.Gdx;
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
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.AnimatablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.BurnableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.physics.PhysicsData;
import com.fcfruit.zombiesmash.zombies.Zombie;

public class Fire implements DrawableEntityInterface, PhysicsEntityInterface, InteractiveEntityInterface
{

    public boolean enabled = false;

    private AnimatablePhysicsEntity animatablePhysicsEntity;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;
    private BodyFire bodyFire;
    private Body physicsBody;
    private Fixture fixture;

    private double destroyTimer;
    private double timeBeforeDestroy = 5000d;

    public Fire()
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.active = false;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.1f);
        circleShape.setPosition(new Vector2(0, 0));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.friction = 1f;
        fixtureDef.density = 0.3f;

        this.physicsBody = Environment.physics.createBody(bodyDef);

        fixture = this.physicsBody.createFixture(fixtureDef);
        fixture.setUserData(new PhysicsData(this));

        TextureAtlas atlas = Environment.assets.get("effects/fire/fire.atlas");
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(0.5f); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("effects/fire/fire.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).

        this.animatablePhysicsEntity = new AnimatablePhysicsEntity(skeleton, state, atlas, this.physicsBody);
        this.animatablePhysicsEntity.setAnimation("single_flame");


        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.position.y*2 - size.y;
        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x/2, size.y/2);
        this.interactiveGraphicsEntity = new InteractiveGraphicsEntity(this.animatablePhysicsEntity, polygon);

        circleShape.dispose();
        circleShape = null;
        fixtureDef = null;
        bodyDef = null;

        this.disable();

    }

    public void enable(Vector2 pos, Vector2 vel)
    {
        this.physicsBody.setTransform(pos, 0);
        this.physicsBody.setActive(true);

        this.physicsBody.applyLinearImpulse(vel, this.physicsBody.getPosition(), true);

        this.destroyTimer = System.currentTimeMillis();

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

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        if (this.enabled)
            this.animatablePhysicsEntity.draw(batch, skeletonRenderer);

    }

    @Override
    public void update(float delta)
    {
        if (System.currentTimeMillis() - this.destroyTimer >= this.timeBeforeDestroy)
        {
            Environment.firePool.returnFire(this);
            Environment.drawableRemoveQueue.add(this);
        } else if (!this.isInLevel())
        {
            Environment.firePool.returnFire(this);
            Environment.drawableRemoveQueue.add(this);
        }

        if (this.enabled)
        {
            this.animatablePhysicsEntity.update(delta);
            this.interactiveGraphicsEntity.update(delta);

            Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getPosition(), 0)));
            pos.y = Environment.gameCamera.position.y*2 - pos.y;

            for(DrawableEntityInterface drawableEntityInterface : Environment.level.getDrawableEntities())
            {
                if(drawableEntityInterface instanceof Zombie)
                {
                    if(Environment.areQuadrilaterallsColliding(((Zombie) drawableEntityInterface).getPolygon(), this.interactiveGraphicsEntity.getPolygon()) && ((Zombie) drawableEntityInterface).getBodyFire() == null)
                    {
                        Environment.firePool.attachFireToBurnable((BurnableEntityInterface) drawableEntityInterface);
                    }
                }
            }
        }
    }

    @Override
    public boolean isTouching()
    {
        return false;
    }

    @Override
    public Polygon getPolygon()
    {
        return this.interactiveGraphicsEntity.getPolygon();
    }

    @Override
    public void onTouchDown(float screenX, float screenY, int pointer)
    {

    }

    @Override
    public void onTouchDragged(float screenX, float screenY, int pointer)
    {

    }

    @Override
    public void onTouchUp(float screenX, float screenY, int pointer)
    {

    }

    @Override
    public Body getPhysicsBody()
    {
        return this.physicsBody;
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
    public Vector2 getSize()
    {
        return this.animatablePhysicsEntity.getSize();
    }

    // Unused


    @Override
    public void draw(SpriteBatch batch)
    {

    }

    @Override
    public void dispose()
    {

    }
}
