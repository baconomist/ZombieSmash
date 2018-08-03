package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DestroyableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.NameableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;

/**
 * Created by Lucas on 2018-08-02.
 */

public class DestroyableEntity implements DestroyableEntityInterface
{
    private OptimizableEntityInterface optimizableEntity;
    private ContainerEntityInterface containerEntity;
    private DetachableEntityInterface detachableEntity;
    private InteractivePhysicsEntityInterface interactivePhysicsEntity;

    private double destroyTimer;
    private double timeBeforeDestroy = 3000;

    public DestroyableEntity(ContainerEntityInterface containerEntity, OptimizableEntityInterface optimizableEntity)
    {
        this.containerEntity = containerEntity;
        this.optimizableEntity = optimizableEntity;
    }

    public DestroyableEntity(ContainerEntityInterface containerEntity, InteractivePhysicsEntityInterface interactiveEntity, OptimizableEntityInterface optimizableEntity)
    {
        this.containerEntity = containerEntity;
        this.interactivePhysicsEntity = interactiveEntity;
        this.optimizableEntity = optimizableEntity;
    }

    public DestroyableEntity(DetachableEntityInterface detachableEntity, InteractivePhysicsEntityInterface interactiveEntity, OptimizableEntityInterface optimizableEntity)
    {
        this.detachableEntity = detachableEntity;
        this.interactivePhysicsEntity = interactiveEntity;
        this.optimizableEntity = optimizableEntity;
    }

    @Override
    public void update(float delta)
    {
        if(!this.optimizableEntity.isOptimizationEnabled())
            this.destroyTimer = System.currentTimeMillis();
        else if(this.shouldDestroy())
            this.destroy();
    }

    private boolean shouldDestroy()
    {
        if(this.containerEntity != null)
        {
            for (com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface interactiveEntity : this.containerEntity.getInteractiveEntities().values())
            {
                Body b = ((PhysicsEntityInterface)interactiveEntity).getPhysicsBody();
                if(!(!interactiveEntity.isTouching()
                        && b.getLinearVelocity().x < 0.05f && b.getLinearVelocity().y < 0.05f && b.getPosition().y < 0.5f
                        && System.currentTimeMillis() - this.destroyTimer >= this.timeBeforeDestroy))
                {
                    return false;
                }
            }
            return true;
        }
        else if(this.detachableEntity != null)
        {
            Body b = this.interactivePhysicsEntity.getPhysicsBody();
            return this.detachableEntity.getState().equals("detached") && !this.interactivePhysicsEntity.isTouching()
                    && b.getLinearVelocity().x < 0.05f && b.getLinearVelocity().y < 0.05f && b.getPosition().y < 0.5f
                    && System.currentTimeMillis() - this.destroyTimer >= this.timeBeforeDestroy;
        }
        return this.interactivePhysicsEntity.isTouching() && System.currentTimeMillis() - this.destroyTimer >= this.timeBeforeDestroy;
    }

    @Override
    public void destroy()
    {
        if (this.containerEntity != null && this.containerEntity instanceof DrawableEntityInterface)
        {
            Environment.drawableRemoveQueue.add((DrawableEntityInterface) this.containerEntity);
            for (InteractiveEntityInterface interactiveEntityInterface : this.containerEntity.getInteractiveEntities().values())
            {
                if (interactiveEntityInterface instanceof PhysicsEntityInterface)
                {
                    Gdx.app.log("Destroy", ""+((NameableEntityInterface) interactiveEntityInterface).getName());
                    Environment.physics.destroyBody(((PhysicsEntityInterface) interactiveEntityInterface).getPhysicsBody());
                }
            }
        } else if (this.detachableEntity instanceof DrawableEntityInterface)
        {
            Environment.drawableRemoveQueue.add((DrawableEntityInterface) this.interactivePhysicsEntity);
            Gdx.app.log("Destroy", ""+((NameableEntityInterface) detachableEntity).getName());
            Environment.physics.destroyBody(this.interactivePhysicsEntity.getPhysicsBody());
        }
    }
}
