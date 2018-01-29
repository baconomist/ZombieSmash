package com.fcfruit.zombiesmash.power_ups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.ContainerEntity;
import com.fcfruit.zombiesmash.entity.DetachableEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.InteractivePhysicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;

/**
 * Created by Lucas on 2018-01-07.
 */

public class Grenade implements DrawableEntityInterface, DetachableEntityInterface, InteractiveEntityInterface, PhysicsEntityInterface
{
    private ContainerEntity containerEntity;

    private DrawablePhysicsEntity drawablePhysicsEntity;
    private DetachableEntity detachableEntity;
    private InteractivePhysicsEntity interactivePhysicsEntity;

    private Joint[] joints;


    public Grenade()
    {

        RubeSceneLoader loader = new RubeSceneLoader(Environment.physics.getWorld());
        RubeScene scene = loader.loadScene(Gdx.files.internal("powerups/grenade/grenade_rube.json"));

        Body physicsBody = scene.getBodies().get(0);
        physicsBody.setUserData(this);

        Joint joint = physicsBody.getJointList().get(0).joint;

        this.drawablePhysicsEntity = new DrawablePhysicsEntity(new Sprite(new Texture(Gdx.files.internal("powerups/grenade/grenade.png"))), physicsBody);
        this.detachableEntity = new DetachableEntity(joint);

    }

    public Grenade(Sprite sprite, Body physicsBody, Joint[] joints, ContainerEntity containerEntity)
    {
        this.drawablePhysicsEntity = new DrawablePhysicsEntity(sprite, physicsBody);
        this.detachableEntity = new DetachableEntity(null, containerEntity);

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.drawablePhysicsEntity.getSize(), 0)));
        size.y = Environment.gameCamera.viewportHeight - size.y;
        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x / 2, size.y / 2);
        this.interactivePhysicsEntity = new InteractivePhysicsEntity(physicsBody, polygon);

        this.joints = joints;
    }

    @Override
    public void detach()
    {
        if (this.joints.length == 0)
        {
            this.detachableEntity.detach();
        } else
        {
            for (Joint joint : this.joints)
            {
                Environment.physics.getWorld().destroyJoint(joint);
            }
        }

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
        this.interactivePhysicsEntity.update(delta);
    }

    @Override
    public void onTouchDown(float x, float y, int p)
    {
        this.interactivePhysicsEntity.onTouchDown(x, y, p);
    }

    @Override
    public void onTouchDragged(float x, float y, int p)
    {
        this.interactivePhysicsEntity.onTouchDragged(x, y, p);
    }

    @Override
    public void onTouchUp(float x, float y, int p)
    {
        this.interactivePhysicsEntity.onTouchUp(x, y, p);
    }

    public Body getPhysicsBody()
    {
        return this.drawablePhysicsEntity.getPhysicsBody();
    }

    @Override
    public void setState(String state)
    {
        this.detachableEntity.setState(state);
    }

    @Override
    public String getState()
    {
        return this.detachableEntity.getState();
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
    public boolean isTouching()
    {
        return this.interactivePhysicsEntity.isTouching();
    }

    @Override
    public Polygon getPolygon()
    {
        return this.interactivePhysicsEntity.getPolygon();
    }

    @Override
    public boolean shouldDetach()
    {
        return this.detachableEntity.shouldDetach();
    }

    @Override
    public ContainerEntity getContainer()
    {
        return this.containerEntity;
    }

    @Override
    public void dispose()
    {

    }

    // Unused
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }

}
