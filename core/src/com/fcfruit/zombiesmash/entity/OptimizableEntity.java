package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.fcfruit.zombiesmash.entity.InteractivePhysicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.physics.Physics;

/**
 * Created by Lucas on 2018-01-25.
 */

public class OptimizableEntity implements OptimizableEntityInterface
{
    private InteractivePhysicsEntityInterface interactivePhysicsEntity;
    private DetachableEntityInterface detachableEntity;
    private ContainerEntityInterface containerEntity;

    // Optimization
    private double optimizationTimer;
    private double timeBeforeOptimize;

    public OptimizableEntity(InteractivePhysicsEntityInterface interactivePhysicsEntity, DetachableEntityInterface detachableEntity, ContainerEntityInterface containerEntity)
    {
        this.detachableEntity = detachableEntity;
        this.interactivePhysicsEntity = interactivePhysicsEntity;
        this.containerEntity = containerEntity;

        this.timeBeforeOptimize = 2500;
    }

    public void update(float delta)
    {

        if (this.containerEntity != null)
        {
            for (InteractiveEntityInterface interactiveEntityInterface : this.containerEntity.getInteractiveEntities().values())
            {
                if (interactiveEntityInterface.isTouching())
                {
                    this.disable_optimization();
                    optimizationTimer = System.currentTimeMillis();
                    break;
                } else if (System.currentTimeMillis() - optimizationTimer >= timeBeforeOptimize)
                {
                    this.enable_optimization();
                }
            }
        }
        else if(this.detachableEntity != null){
            if (!this.detachableEntity.getState().equals("detached"))
            {
                optimizationTimer = System.currentTimeMillis();
            }
            else
            {
                if (this.interactivePhysicsEntity.isTouching())
                {
                    this.disable_optimization();
                    optimizationTimer = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - optimizationTimer >= timeBeforeOptimize)
                {
                    this.enable_optimization();
                }
            }
        }
        else
        {
            if (this.interactivePhysicsEntity.isTouching())
            {
                this.disable_optimization();
                optimizationTimer = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - optimizationTimer >= timeBeforeOptimize)
            {
                this.enable_optimization();
            }
        }

    }

    @Override
    public void enable_optimization()
    {
        if (this.containerEntity != null)
        {
            for (InteractiveEntityInterface interactiveEntityInterface : this.containerEntity.getInteractiveEntities().values())
            {
                if (interactiveEntityInterface instanceof PhysicsEntityInterface)
                {
                    ((PhysicsEntityInterface) interactiveEntityInterface).getPhysicsBody().setActive(false);
                    ((PhysicsEntityInterface) interactiveEntityInterface).getPhysicsBody().setAwake(false);

                }
            }
        } else
        {
            this.interactivePhysicsEntity.getPhysicsBody().setActive(false);
            this.interactivePhysicsEntity.getPhysicsBody().setAwake(false);
        }
    }

    @Override
    public void disable_optimization()
    {
        if (this.containerEntity != null)
        {
            for (InteractiveEntityInterface interactiveEntityInterface : this.containerEntity.getInteractiveEntities().values())
            {
                if (interactiveEntityInterface instanceof PhysicsEntityInterface)
                {
                    ((PhysicsEntityInterface) interactiveEntityInterface).getPhysicsBody().setActive(true);
                    ((PhysicsEntityInterface) interactiveEntityInterface).getPhysicsBody().setAwake(true);

                }
            }
        } else
        {
            this.interactivePhysicsEntity.getPhysicsBody().setActive(true);
            this.interactivePhysicsEntity.getPhysicsBody().setAwake(true);
        }
        optimizationTimer = System.currentTimeMillis();
    }
}
