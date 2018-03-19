package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.BleedableEntity;
import com.fcfruit.zombiesmash.entity.DetachableEntity;
import com.fcfruit.zombiesmash.entity.OptimizableEntity;
import com.fcfruit.zombiesmash.entity.interfaces.BleedableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.InteractivePhysicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.NameableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-01-07.
 */

public class NewPart implements DrawableEntityInterface, DetachableEntityInterface, OptimizableEntityInterface, InteractivePhysicsEntityInterface, BleedableEntityInterface, NameableEntityInterface
{
    private String name;

    private ContainerEntityInterface containerEntity;
    private OptimizableEntity optimizableEntity;
    private DrawablePhysicsEntity drawableEntity;
    private DetachableEntity detachableEntity;
    private InteractivePhysicsEntity interactivePhysicsEntity;
    private BleedableEntityInterface bleedableEntity;

    public NewPart(String name, Sprite sprite, Body physicsBody, ArrayList<Joint> joints, ContainerEntityInterface containerEntity)
    {
        this.name = name;

        this.containerEntity = containerEntity;

        this.drawableEntity = new DrawablePhysicsEntity(sprite, physicsBody);
        this.detachableEntity = new DetachableEntity(joints, containerEntity, this);

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.drawableEntity.getSize(), 0)));
        size.y = Environment.gameCamera.viewportHeight - size.y;
        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x / 2, size.y / 2);
        this.interactivePhysicsEntity = new InteractivePhysicsEntity(physicsBody, polygon);

        this.optimizableEntity = new OptimizableEntity(this, this, null);

        this.bleedableEntity = new BleedableEntity(this);

    }

    @Override
    public void detach()
    {
        this.detachableEntity.detach();

        /*maybe set joint state to waiting for detach when detaching
            joint user data probably gets instantly deleted when you call joint.destroy
            so no checking if joint has been destroyed with userdata*/
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        this.drawableEntity.draw(batch);
        this.bleedableEntity.draw(batch);
    }

    @Override
    public void update(float delta)
    {
        this.drawableEntity.update(delta);
        this.interactivePhysicsEntity.update(delta);
        this.bleedableEntity.update(delta);

        this.optimizableEntity.update(delta);
        for (InteractiveEntityInterface interactiveEntityInterface : this.containerEntity.getInteractiveEntities().values())
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
    public boolean shouldDetach()
    {
        return detachableEntity.shouldDetach();
    }

    @Override
    public ContainerEntityInterface getContainer()
    {
        return this.containerEntity;
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

    public ArrayList<Joint> getJoints(){return this.detachableEntity.getJoints();}

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
    public void dispose()
    {

    }

    // Unused
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }
}

