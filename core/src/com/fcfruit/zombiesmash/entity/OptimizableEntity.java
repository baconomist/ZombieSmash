package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;

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
    private double timeBeforeOptimize = 500;

    private boolean isOptimizationEnabled;

    // Passive Optimization
    private double passiveOptimizationTimer;
    private double timeBeforePassiveOptimize = 1000;

    private boolean isPassiveOptimizationEnabled;

    public OptimizableEntity(InteractivePhysicsEntityInterface interactivePhysicsEntity, DetachableEntityInterface detachableEntity, ContainerEntityInterface containerEntity)
    {
        this.detachableEntity = detachableEntity;
        this.interactivePhysicsEntity = interactivePhysicsEntity;
        this.containerEntity = containerEntity;
    }

    public void update(float delta)
    {
        if (this.isOptimizationEnabled() && this.shouldOptimize())
        {
            this.optimize();
        }
        else if(this.isOptimizationEnabled() && !this.shouldOptimize())
        {
            this.unoptimize();
        }
        else if(!this.isOptimizationEnabled() && !this.isPassiveOptimizationEnabled)
        {
            this.unoptimize();
            this.optimizationTimer = System.currentTimeMillis();
            this.passiveOptimizationTimer = System.currentTimeMillis();
        }

        if(this.isPassiveOptimizationEnabled && this.shouldPassiveOptimize())
        {
            this.optimize();
        }
    }



    private boolean shouldOptimize()
    {
        if(this.interactivePhysicsEntity != null && this.containerEntity != null)
        {
            for (com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface interactiveEntity : this.containerEntity.getInteractiveEntities().values())
            {
                Body b = this.interactivePhysicsEntity.getPhysicsBody();
                if(!this.interactivePhysicsEntity.isTouching()
                        && b.getLinearVelocity().x < 0.1f && b.getLinearVelocity().y < 0.1f && b.getPosition().y < 0.5f
                        && System.currentTimeMillis() - this.optimizationTimer >= this.timeBeforeOptimize)
                {
                    return true;
                }
            }
            return false;
        }
        else if (this.containerEntity != null)
        {
            for (com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface interactiveEntity : this.containerEntity.getInteractiveEntities().values())
            {
                if (interactiveEntity.isTouching())
                {
                    return false;
                }
            }
            return System.currentTimeMillis() - this.optimizationTimer >= this.timeBeforeOptimize;
        } else if(this.detachableEntity != null)
        {
            Body b = this.interactivePhysicsEntity.getPhysicsBody();
            return this.detachableEntity.getState().equals("detached") && !this.interactivePhysicsEntity.isTouching()
                    && b.getLinearVelocity().x < 0.1f && b.getLinearVelocity().y < 0.1f && b.getPosition().y < 0.5f
                    && System.currentTimeMillis() - this.optimizationTimer >= this.timeBeforeOptimize;
        }
        return this.interactivePhysicsEntity.isTouching() && System.currentTimeMillis() - this.optimizationTimer >= this.timeBeforeOptimize;
    }

    private boolean shouldPassiveOptimize()
    {
        if(this.containerEntity != null)
        {
            for(InteractiveEntityInterface interactiveEntityInterface : this.containerEntity.getInteractiveEntities().values())
            {
                if(interactiveEntityInterface instanceof PhysicsEntityInterface)
                {
                    Body b = ((PhysicsEntityInterface) interactiveEntityInterface).getPhysicsBody();
                    if(!(!interactiveEntityInterface.isTouching() && b.getLinearVelocity().x < 0.01f && b.getLinearVelocity().y < 0.01f && b.getPosition().y < 1f
                        && System.currentTimeMillis() - this.passiveOptimizationTimer >= this.timeBeforePassiveOptimize))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
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

    public void enable_passive_optimization()
    {
        this.isPassiveOptimizationEnabled = true;
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
        this.isPassiveOptimizationEnabled = false;
    }

    @Override
    public boolean isOptimizationEnabled()
    {
        return this.isOptimizationEnabled;
    }
}
