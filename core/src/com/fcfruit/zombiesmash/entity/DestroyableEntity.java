package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
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
    private AnimatableGraphicsEntity animatableGraphicsEntity;

    private OptimizableEntityInterface optimizableEntity;
    private ContainerEntityInterface containerEntity;
    private DetachableEntityInterface detachableEntity;
    private InteractivePhysicsEntityInterface interactivePhysicsEntity;

    private double destroyTimer;
    private double timeBeforeDestroy = 3000;

    private boolean destroying = false;

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
        if(!this.optimizableEntity.isOptimizationEnabled() || this.interactivePhysicsEntity.getPhysicsBody().isActive())
            this.destroyTimer = System.currentTimeMillis();
        else if(this.shouldDestroy() && !this.destroying)
        {
            this.createSmoke();
            this.destroy();
            this.destroying = true;
        }
    }

    private boolean shouldDestroy()
    {
        if(this.containerEntity != null)
        {
            for (com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface interactiveEntity : this.containerEntity.getInteractiveEntities().values())
            {
                Body b = ((PhysicsEntityInterface)interactiveEntity).getPhysicsBody();
                if(!(!interactiveEntity.isTouching() && b.getPosition().y < 1f
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
            return this.detachableEntity.getState().equals("detached") && !this.interactivePhysicsEntity.isTouching() && b.getPosition().y < 1f
                    && System.currentTimeMillis() - this.destroyTimer >= this.timeBeforeDestroy;
        }
        return !this.interactivePhysicsEntity.isTouching() && this.interactivePhysicsEntity.getPhysicsBody().getPosition().y < 1f
                && System.currentTimeMillis() - this.destroyTimer >= this.timeBeforeDestroy;
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
                    // Instead of destroying body, move it out of screen
                    // Destroying bodies here causes other zombies to lose limbs
                    // because box2d re-uses memory for bodies
                    // Probably safer in terms of crashing anyways
                    ((InteractivePhysicsEntityInterface)interactiveEntityInterface).getPhysicsBody().setTransform(new Vector2(99, 99), 0);
                    ((InteractivePhysicsEntityInterface)interactiveEntityInterface).getPhysicsBody().setActive(false);
                }
            }
            //Environment.physics.destroyBody(this.interactivePhysicsEntity.getPhysicsBody());
        } else if (this.detachableEntity instanceof DrawableEntityInterface)
        {
            Environment.drawableRemoveQueue.add((DrawableEntityInterface) this.interactivePhysicsEntity);
            // Instead of destroying body, move it out of screen
            // Destroying bodies here causes other zombies to lose limbs
            // because box2d re-uses memory for bodies
            // Probably safer in terms of crashing anyways
            this.interactivePhysicsEntity.getPhysicsBody().setTransform(new Vector2(99, 99), 0);
            this.interactivePhysicsEntity.getPhysicsBody().setActive(false);
        }
    }

    private void createSmoke()
    {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("effects/smoke/smoke.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("effects/smoke/smoke.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        //state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

        state.addListener(new AnimationState.AnimationStateAdapter()
        {
            @Override
            public void complete(AnimationState.TrackEntry entry)
            {
                Environment.drawableRemoveQueue.add(animatableGraphicsEntity);
                super.complete(entry);
            }
        });

        this.animatableGraphicsEntity = new AnimatableGraphicsEntity(skeleton, state, atlas);
        this.animatableGraphicsEntity.setAnimation("animation");
        this.animatableGraphicsEntity.setPosition(this.interactivePhysicsEntity.getPhysicsBody().getPosition());
        Environment.drawableBackgroundAddQueue.add(this.animatableGraphicsEntity);
    }

}
