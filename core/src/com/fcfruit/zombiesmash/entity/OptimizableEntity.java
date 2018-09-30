package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.NameableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;

/**
 * Created by Lucas on 2018-01-25.
 */

public class OptimizableEntity implements com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface
{
    private InteractivePhysicsEntityInterface interactivePhysicsEntity;
    private DetachableEntityInterface detachableEntity;
    private ContainerEntityInterface containerEntity;

    // Optimization
    private double optimizationTimer;
    private double timeBeforeOptimize = 1250;

    private boolean isOptimizationEnabled;

    public OptimizableEntity(InteractivePhysicsEntityInterface interactivePhysicsEntity, DetachableEntityInterface detachableEntity, ContainerEntityInterface containerEntity)
    {
        this.detachableEntity = detachableEntity;
        this.interactivePhysicsEntity = interactivePhysicsEntity;
        this.containerEntity = containerEntity;
    }

    public void update(float delta)
    {
        if(this.isTouching())
        {
            this.unoptimize();
            this.optimizationTimer = System.currentTimeMillis();
        }
        else if (this.isOptimizationEnabled() && this.shouldOptimize() && System.currentTimeMillis() - this.optimizationTimer >= this.timeBeforeOptimize)
        {
            this.optimize();
        }
        else if((this.isOptimizationEnabled() && !this.shouldOptimize()) || !this.isOptimizationEnabled)
        {
            this.optimizationTimer = System.currentTimeMillis();
        }
    }

    private boolean isTouching()
    {
        if(this.containerEntity != null)
        {
            for(InteractiveEntityInterface interactiveEntityInterface : this.containerEntity.getInteractiveEntities().values())
            {
                if(interactiveEntityInterface instanceof PhysicsEntityInterface)
                {
                    Body b = ((PhysicsEntityInterface) interactiveEntityInterface).getPhysicsBody();
                    if(interactiveEntityInterface.isTouching())
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        return this.interactivePhysicsEntity.isTouching();
    }

    private boolean shouldOptimize()
    {
        if(this.containerEntity != null)
        {
            for(InteractiveEntityInterface interactiveEntityInterface : this.containerEntity.getInteractiveEntities().values())
            {
                if(interactiveEntityInterface instanceof PhysicsEntityInterface)
                {
                    Body b = ((PhysicsEntityInterface) interactiveEntityInterface).getPhysicsBody();
                    if(!(!interactiveEntityInterface.isTouching() && b.getLinearVelocity().x < 1f && b.getLinearVelocity().y < 1f && b.getPosition().y < 2f))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        else if(this.detachableEntity != null)
        {
            Body b = this.interactivePhysicsEntity.getPhysicsBody();
            return this.detachableEntity.getState().equals("detached") && !this.interactivePhysicsEntity.isTouching()
                    && b.getLinearVelocity().x < 1f && b.getLinearVelocity().y < 1f && b.getPosition().y < 1f;
        }
        return !this.interactivePhysicsEntity.isTouching();
    }

    private void optimize()
    {
        if (this.containerEntity != null)
        {
            for (com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface interactiveEntity : this.containerEntity.getInteractiveEntities().values())
            {
                if (interactiveEntity instanceof com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface)
                {
                    ((com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface) interactiveEntity).getPhysicsBody().setActive(false);
                    ((com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface) interactiveEntity).getPhysicsBody().setAwake(false);
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
            for (com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface interactiveEntity : this.containerEntity.getInteractiveEntities().values())
            {
                if (interactiveEntity instanceof com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface)
                {
                    ((com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface) interactiveEntity).getPhysicsBody().setActive(true);
                    ((com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface) interactiveEntity).getPhysicsBody().setAwake(true);
                }
            }
        } else
        {
            this.interactivePhysicsEntity.getPhysicsBody().setActive(true);
            this.interactivePhysicsEntity.getPhysicsBody().setAwake(true);
        }
    }

    @Override
    public void force_instant_optimize()
    {
        this.enable_optimization();
        this.optimize();
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
        this.optimizationTimer = System.currentTimeMillis();
        this.unoptimize();
    }

    @Override
    public boolean isOptimizationEnabled()
    {
        return this.isOptimizationEnabled;
    }
}
