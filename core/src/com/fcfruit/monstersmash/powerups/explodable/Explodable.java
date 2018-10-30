package com.fcfruit.monstersmash.powerups.explodable;

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
import com.fcfruit.monstersmash.entity.DetachableEntity;
import com.fcfruit.monstersmash.entity.DrawablePhysicsEntity;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.ContainerEntity;
import com.fcfruit.monstersmash.entity.DestroyableEntity;
import com.fcfruit.monstersmash.entity.ExplodableEntity;
import com.fcfruit.monstersmash.entity.InteractivePhysicsEntity;
import com.fcfruit.monstersmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.ExplodableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.PostLevelDestroyableInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-01-07.
 */

public class Explodable implements com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface, DetachableEntityInterface, InteractivePhysicsEntityInterface, ExplodableEntityInterface,
        PostLevelDestroyableInterface
{
    private com.fcfruit.monstersmash.entity.ContainerEntity containerEntity;

    private DrawablePhysicsEntity drawablePhysicsEntity;
    private DetachableEntity detachableEntity;
    private com.fcfruit.monstersmash.entity.InteractivePhysicsEntity interactivePhysicsEntity;
    private com.fcfruit.monstersmash.entity.ExplodableEntity explodableEntity;
    private com.fcfruit.monstersmash.entity.DestroyableEntity destroyableEntity;

    public Explodable(Body body, ArrayList<Joint> joints)
    {

        String simpleName = this.getClass().getSimpleName().toLowerCase();
        Sprite sprite = new Sprite(new Texture(Gdx.files.internal("powerups/explodable/"+simpleName+"/"+simpleName+".png")));

        this.drawablePhysicsEntity = new DrawablePhysicsEntity(sprite, body);
        this.detachableEntity = new DetachableEntity(joints);

        this.explodableEntity = new com.fcfruit.monstersmash.entity.ExplodableEntity(this, 20f);

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.drawablePhysicsEntity.getSize(), 0)));
        size.y = Environment.gameCamera.position.y*2 - size.y;

        Polygon polygon = new Polygon(new float[]{0, 0, size.x*4, 0, size.x*4, size.y*4, 0, size.y*4});
        polygon.setOrigin(size.x * 2, size.y * 2);

        this.interactivePhysicsEntity = new com.fcfruit.monstersmash.entity.InteractivePhysicsEntity(body, polygon);
        this.destroyableEntity = new com.fcfruit.monstersmash.entity.DestroyableEntity(this, this);

        this.drawablePhysicsEntity.setAngle(-25);

    }

    public Explodable(Sprite sprite, Body physicsBody, ArrayList<Joint> joints, ContainerEntityInterface containerEntity)
    {
        this.drawablePhysicsEntity = new DrawablePhysicsEntity(sprite, physicsBody);
        this.detachableEntity = new DetachableEntity(joints, containerEntity, this);

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.drawablePhysicsEntity.getSize(), 0)));
        size.y = Environment.gameCamera.position.y*2 - size.y;
        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x / 2, size.y / 2);
        this.interactivePhysicsEntity = new com.fcfruit.monstersmash.entity.InteractivePhysicsEntity(physicsBody, polygon);

        this.explodableEntity = new com.fcfruit.monstersmash.entity.ExplodableEntity(this, 20f);
        this.destroyableEntity = new com.fcfruit.monstersmash.entity.DestroyableEntity(this, this);

        this.drawablePhysicsEntity.setAngle(-25);

    }

    public com.fcfruit.monstersmash.entity.ExplodableEntity getExplodableEntity()
    {
        return explodableEntity;
    }

    @Override
    public void explode()
    {
        this.explodableEntity.explode();

        if(!this.getState().equals("detached"))
        {
            this.setState("waiting_for_detach");
            Environment.detachableEntityDetachQueue.add(this);
        }

        this.getPhysicsBody().setActive(false);
        this.getPhysicsBody().setTransform(99, 99, 0);

        Environment.drawableAddQueue.add(this.explodableEntity);
        Environment.drawableRemoveQueue.add(this);
        this.destroy();
        this.dispose();
    }

    @Override
    public void destroy()
    {
        this.destroyableEntity.destroy();
    }

    @Override
    public boolean shouldExplode()
    {
        return this.explodableEntity.shouldExplode();
    }

    @Override
    public void detach()
    {
        Gdx.app.log("drawableRemove", "this");
        this.detachableEntity.detach();
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        this.drawablePhysicsEntity.draw(batch);
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.explodableEntity.draw(batch, skeletonRenderer);
    }

    @Override
    public void update(float delta)
    {
        this.drawablePhysicsEntity.update(delta);
        this.interactivePhysicsEntity.update(delta);
        this.explodableEntity.update(delta);
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
    public void setForceForDetach(float force)
    {
        this.detachableEntity.setForceForDetach(force);
    }

    @Override
    public com.fcfruit.monstersmash.entity.ContainerEntity getContainer()
    {
        return this.containerEntity;
    }

    @Override
    public void setUsingPowerfulJoint(boolean usingPowerfulJoint)
    {
        this.interactivePhysicsEntity.setUsingPowerfulJoint(usingPowerfulJoint);
    }

    @Override
    public boolean isUsingPowerfulJoint()
    {
        return this.interactivePhysicsEntity.isUsingPowerfulJoint();
    }

    @Override
    public void overrideTouching(boolean touching, float screenX, float screenY, int p)
    {
        this.interactivePhysicsEntity.overrideTouching(touching, screenX, screenY, p);
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

    }

    @Override
    public ArrayList<Joint> getJoints(){return this.detachableEntity.getJoints();}

}
