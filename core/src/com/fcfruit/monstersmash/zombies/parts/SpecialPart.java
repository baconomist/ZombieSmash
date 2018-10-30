package com.fcfruit.monstersmash.zombies.parts;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.BleedablePoint;
import com.fcfruit.monstersmash.entity.DestroyableEntity;
import com.fcfruit.monstersmash.entity.DetachableEntity;
import com.fcfruit.monstersmash.entity.DrawablePhysicsEntity;
import com.fcfruit.monstersmash.entity.InteractivePhysicsEntity;
import com.fcfruit.monstersmash.entity.OptimizableEntity;
import com.fcfruit.monstersmash.entity.interfaces.BleedableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DestroyableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.NameableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.PostLevelDestroyableInterface;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2018-01-07.
 */

public class SpecialPart implements DrawableEntityInterface, OptimizableEntityInterface, InteractivePhysicsEntityInterface,
        DetachableEntityInterface, NameableEntityInterface, PostLevelDestroyableInterface
{
    private String name;

    private ContainerEntityInterface parentContainer;
    private OptimizableEntity optimizableEntity;
    private DestroyableEntity destroyableEntity;
    private DrawablePhysicsEntity drawableEntity;
    private InteractivePhysicsEntity interactivePhysicsEntity;
    private DetachableEntity detachableEntity;

    public SpecialPart(String name, Sprite sprite, Body physicsBody, ArrayList<Joint> joints, ContainerEntityInterface parentContainer)
    {
        this.name = name;

        this.parentContainer = parentContainer;

        this.drawableEntity = new DrawablePhysicsEntity(sprite, physicsBody);

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.drawableEntity.getSize(), 0)));
        size.y = Environment.gameCamera.position.y*2 - size.y;
        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x / 2, size.y / 2);
        this.interactivePhysicsEntity = new InteractivePhysicsEntity(physicsBody, polygon);

        this.optimizableEntity = new OptimizableEntity(this, null, parentContainer);
        this.destroyableEntity = new DestroyableEntity(this, this, this);

        this.detachableEntity = new DetachableEntity(joints, parentContainer, this);

    }


    @Override
    public void draw(SpriteBatch batch)
    {
        this.drawableEntity.draw(batch);
    }

    @Override
    public void update(float delta)
    {
        this.drawableEntity.update(delta);
        this.interactivePhysicsEntity.update(delta);

        this.optimizableEntity.update(delta);
        this.destroyableEntity.update(delta);
        for (InteractiveEntityInterface interactiveEntityInterface : this.parentContainer.getInteractiveEntities().values())
        {
            if (interactiveEntityInterface instanceof InteractivePhysicsEntity)
            {
                if (!((InteractivePhysicsEntity) interactiveEntityInterface).isUsingPowerfulJoint())
                {
                    this.interactivePhysicsEntity.setUsingPowerfulJoint(false);
                    break;
                } else
                {
                    this.interactivePhysicsEntity.setUsingPowerfulJoint(true);
                }
            }
        }
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
        return this.drawableEntity.getPhysicsBody();
    }

    @Override
    public Vector2 getPosition()
    {
        return this.drawableEntity.getPosition();
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.drawableEntity.setPosition(position);
    }

    @Override
    public float getAngle()
    {
        return this.drawableEntity.getAngle();
    }

    @Override
    public void setAngle(float angle)
    {
        this.drawableEntity.setAngle(angle);
    }

    @Override
    public Vector2 getSize()
    {
        return this.drawableEntity.getSize();
    }

    @Override
    public boolean isTouching()
    {
        return interactivePhysicsEntity.isTouching();
    }

    @Override
    public Polygon getPolygon()
    {
        return this.interactivePhysicsEntity.getPolygon();
    }

    @Override
    public void destroy()
    {
        this.destroyableEntity.destroy();
    }

    @Override
    public void force_instant_optimize()
    {
        this.optimizableEntity.force_instant_optimize();
    }

    @Override
    public void enable_optimization()
    {
        this.optimizableEntity.enable_optimization();
    }

    @Override
    public void disable_optimization()
    {
        this.optimizableEntity.disable_optimization();
    }

    @Override
    public boolean isOptimizationEnabled()
    {
        return this.optimizableEntity.isOptimizationEnabled();
    }

    public String getName(){
        return this.name;
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
        return this.drawableEntity.getAlpha();
    }

    @Override
    public void setAlpha(float alpha)
    {
        this.drawableEntity.setAlpha(alpha);
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
    public void detach()
    {
        this.detachableEntity.detach();
        Environment.level.addDrawableEntity(this);
    }

    @Override
    public boolean shouldDetach()
    {
        return this.detachableEntity.shouldDetach();
    }

    @Override
    public ContainerEntityInterface getContainer()
    {
        return this.parentContainer;
    }

    @Override
    public ArrayList<Joint> getJoints()
    {
        return this.detachableEntity.getJoints();
    }

    @Override
    public void setForceForDetach(float force)
    {
        this.detachableEntity.setForceForDetach(force);
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

