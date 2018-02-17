package com.fcfruit.zombiesmash.entity;

import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;

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

    private boolean isOptimizationEnabled;

    public OptimizableEntity(InteractivePhysicsEntityInterface interactivePhysicsEntity, DetachableEntityInterface detachableEntity, ContainerEntityInterface containerEntity)
    {
        this.detachableEntity = detachableEntity;
        this.interactivePhysicsEntity = interactivePhysicsEntity;
        this.containerEntity = containerEntity;

        this.timeBeforeOptimize = 2500;

    }

    public void update(float delta)
    {

        if (this.isOptimizationEnabled())
        {
            if (this.shouldOptimize())
            {
                this.optimize();
            }
        } else
        {
            this.unoptimize();
            this.optimizationTimer = System.currentTimeMillis();
        }

    }

    private boolean shouldOptimize()
    {
        if (this.containerEntity != null)
        {
            for (InteractiveEntityInterface interactiveEntity : this.containerEntity.getInteractiveEntities().values())
            {
                if (interactiveEntity.isTouching())
                {
                    return false;
                }
            }
            return System.currentTimeMillis() - this.optimizationTimer >= this.timeBeforeOptimize;
        } else if(this.detachableEntity != null)
        {
            return this.detachableEntity.getState().equals("detached") && !this.interactivePhysicsEntity.isTouching() && System.currentTimeMillis() - this.optimizationTimer >= this.timeBeforeOptimize;
        }
        return this.interactivePhysicsEntity.isTouching() && System.currentTimeMillis() - this.optimizationTimer >= this.timeBeforeOptimize;
    }


    private void optimize()
    {
        if (this.containerEntity != null)
        {
            for (InteractiveEntityInterface interactiveEntity : this.containerEntity.getInteractiveEntities().values())
            {
                if (interactiveEntity instanceof PhysicsEntityInterface)
                {
                    ((PhysicsEntityInterface) interactiveEntity).getPhysicsBody().setActive(false);
                    ((PhysicsEntityInterface) interactiveEntity).getPhysicsBody().setAwake(false);

                }
            }
        } else
        {
            this.interactivePhysicsEntity.getPhysicsBody().setActive(false);
            this.interactivePhysicsEntity.getPhysicsBody().setAwake(false);
        }
    }

    private void unoptimize()
    {
        if (this.containerEntity != null)
        {
            for (InteractiveEntityInterface interactiveEntity : this.containerEntity.getInteractiveEntities().values())
            {
                if (interactiveEntity instanceof PhysicsEntityInterface)
                {
                    ((PhysicsEntityInterface) interactiveEntity).getPhysicsBody().setActive(true);
                    ((PhysicsEntityInterface) interactiveEntity).getPhysicsBody().setAwake(true);

                }
            }
        } else
        {
            this.interactivePhysicsEntity.getPhysicsBody().setActive(true);
            this.interactivePhysicsEntity.getPhysicsBody().setAwake(true);
        }
    }

    @Override
    public void enable_optimization()
    {
        this.isOptimizationEnabled = true;
    }

    @Override
    public void disable_optimization()
    {
        this.isOptimizationEnabled = false;
    }

    @Override
    public boolean isOptimizationEnabled()
    {
        return this.isOptimizationEnabled;
    }
}
