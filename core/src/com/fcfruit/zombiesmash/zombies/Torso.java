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
import com.fcfruit.zombiesmash.entity.ContainerEntity;
import com.fcfruit.zombiesmash.entity.DetachableParentEntity;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.InteractivePhysicsEntity;
import com.fcfruit.zombiesmash.entity.OptimizableEntity;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityParentInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.NameableEntity;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-02-02.
 */

public class Torso implements DrawableEntityInterface, InteractivePhysicsEntityInterface, DetachableEntityParentInterface, NameableEntity, OptimizableEntityInterface
{
    private String name;
    
    private Body physicsBody;
    private ContainerEntityInterface containerEntity;

    private DrawableEntityInterface drawableEntity;
    private InteractivePhysicsEntity interactivePhysicsEntity;
    private DetachableParentEntity detachableParentEntity;
    private OptimizableEntity optimizableEntity;
    
    
    public Torso(String name, Sprite sprite, Body physicsBody, ContainerEntityInterface containerEntityInterface)
    {
        this.name = name;
        
        this.physicsBody = physicsBody;
        this.containerEntity = containerEntityInterface;
        
        this.drawableEntity = new DrawablePhysicsEntity(sprite, physicsBody);

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.drawableEntity.getSize(), 0)));
        size.y = Environment.gameCamera.viewportHeight - size.y;
        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x / 2, size.y / 2);
        this.interactivePhysicsEntity = new InteractivePhysicsEntity(physicsBody, polygon);
        
        this.detachableParentEntity = new DetachableParentEntity();

        this.optimizableEntity = new OptimizableEntity(this);

        this.getPhysicsBody().setAwake(false);
        this.getPhysicsBody().setActive(false);        
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
    public String getName()
    {
        return this.name;
    }

    @Override
    public Body getPhysicsBody()
    {
        return this.physicsBody;
    }

    @Override
    public void setUsingPowerfulJoint(boolean usingPowerfulJoint)
    {
        this.interactivePhysicsEntity.setUsingPowerfulJoint(usingPowerfulJoint);
    }

    @Override
    public void onTouchDown(float screenX, float screenY, int pointer)
    {
        this.interactivePhysicsEntity.onTouchDown(screenX, screenY, pointer);
    }

    @Override
    public void onTouchDragged(float screenX, float screenY, int pointer)
    {
        this.interactivePhysicsEntity.onTouchDragged(screenX, screenY, pointer);
    }

    @Override
    public void onTouchUp(float screenX, float screenY, int pointer)
    {
        this.interactivePhysicsEntity.onTouchUp(screenX, screenY, pointer);
    }

    @Override
    public boolean isUsingPowerfulJoint()
    {
        return this.interactivePhysicsEntity.isUsingPowerfulJoint();
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        this.drawableEntity.draw(batch);
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
    public void update(float delta)
    {
        this.drawableEntity.update(delta);
        this.interactivePhysicsEntity.update(delta);

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
    public void dispose()
    {
        this.drawableEntity.dispose();
    }

    @Override
    public void removeChild(DetachableEntityInterface detachableEntity)
    {
        this.detachableParentEntity.removeChild(detachableEntity);
    }

    @Override
    public void addChild(DetachableEntityInterface detachableEntity)
    {
        this.detachableParentEntity.addChild(detachableEntity);
    }

    @Override
    public ArrayList<DetachableEntityInterface> getChildren()
    {
        return this.detachableParentEntity.getChildren();
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }

}
