package com.fcfruit.monstersmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DestroyableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.NameableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.PhysicsEntityInterface;

import java.util.Random;

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

    private DrawableEntityInterface drawableEntity;
    private PhysicsEntityInterface physicsEntity;

    private double destroyTimer;
    private double timeBeforeDestroy = 2500 + new Random().nextInt(500);

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

    public DestroyableEntity(DrawableEntityInterface drawableEntityInterface, PhysicsEntityInterface physicsEntityInterface)
    {
        this.drawableEntity = drawableEntityInterface;
        this.physicsEntity = physicsEntityInterface;
    }

    @Override
    public void update(float delta)
    {
        if(!this.optimizableEntity.isOptimizationEnabled() || this.interactivePhysicsEntity.getPhysicsBody().isActive())
            this.destroyTimer = System.currentTimeMillis();
        else if(this.shouldDestroy() && !this.destroying)
        {
            this.destroy();
        }
    }

    private boolean shouldDestroy()
    {
        if(this.containerEntity != null)
        {
            for (com.fcfruit.monstersmash.entity.interfaces.InteractiveEntityInterface interactiveEntity : this.containerEntity.getInteractiveEntities().values())
            {
                if(interactiveEntity.isTouching())
                {
                    return false;
                }
            }
            if(System.currentTimeMillis() - this.destroyTimer >= this.timeBeforeDestroy)
                return true;
        }
        else if(this.detachableEntity != null)
        {
            return this.detachableEntity.getState().equals("detached") && !this.interactivePhysicsEntity.isTouching() && System.currentTimeMillis() - this.destroyTimer >= this.timeBeforeDestroy;
        }
        return !this.interactivePhysicsEntity.isTouching() && System.currentTimeMillis() - this.destroyTimer >= this.timeBeforeDestroy;
    }
    
    private boolean isInLevel(Body physicsBody)
    {
        return physicsBody.getPosition().x > Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2 - 2f
                && physicsBody.getPosition().x < Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth / 2 + 2f
                && physicsBody.getPosition().y > Environment.physicsCamera.position.y - Environment.physicsCamera.viewportHeight / 2 - 2f
                && physicsBody.getPosition().y < Environment.physicsCamera.position.y + Environment.physicsCamera.viewportHeight / 2 + 2f;
    }

    @Override
    public void destroy()
    {
        // Only play smoke animation if in level, otherwise its pointless
        if(interactivePhysicsEntity != null && this.isInLevel(this.interactivePhysicsEntity.getPhysicsBody()))
            this.createSmoke();
        else if(this.drawableEntity != null && drawableEntity instanceof PhysicsEntityInterface && this.isInLevel(((PhysicsEntityInterface) drawableEntity).getPhysicsBody()))
            this.createSmoke();
        else if(this.containerEntity != null)
        {
            for(InteractiveEntityInterface interactiveEntityInterface : this.containerEntity.getInteractiveEntities().values())
            {
                if(interactiveEntityInterface instanceof PhysicsEntityInterface && this.isInLevel(((PhysicsEntityInterface)interactiveEntityInterface).getPhysicsBody()))
                {
                    this.createSmoke();
                    break;
                }
            }
        }

        this.destroying = true;

        if (this.containerEntity != null && this.containerEntity instanceof DrawableEntityInterface)
        {
            Environment.drawableRemoveQueue.add((DrawableEntityInterface) this.containerEntity);
            for (InteractiveEntityInterface interactiveEntityInterface : this.containerEntity.getInteractiveEntities().values())
            {
                if(interactiveEntityInterface instanceof DestroyableEntityInterface && interactiveEntityInterface != this.interactivePhysicsEntity)
                {
                    ((DestroyableEntityInterface) interactiveEntityInterface).destroy();
                }
                else if (interactiveEntityInterface instanceof PhysicsEntityInterface)
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
        } else if(this.drawableEntity != null)
        {
            Environment.drawableRemoveQueue.add(this.drawableEntity);
            // Instead of destroying body, move it out of screen
            // Destroying bodies here causes other zombies to lose limbs
            // because box2d re-uses memory for bodies
            // Probably safer in terms of crashing anyways
            this.physicsEntity.getPhysicsBody().setTransform(new Vector2(99, 99), 0);
            this.physicsEntity.getPhysicsBody().setActive(false);
        }
    }

    private void createSmoke()
    {
        TextureAtlas atlas = Environment.assets.get("effects/smoke/smoke.atlas", TextureAtlas.class);
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

        if(this.drawableEntity != null)
            this.animatableGraphicsEntity.setPosition(this.physicsEntity.getPhysicsBody().getPosition());
        else if(this.interactivePhysicsEntity != null)
            this.animatableGraphicsEntity.setPosition(this.interactivePhysicsEntity.getPhysicsBody().getPosition());
        else if(this.containerEntity != null)
            this.animatableGraphicsEntity.setPosition(((DrawableEntityInterface) this.containerEntity.getDrawableEntities().values().toArray()[0]).getPosition()); // Pick one item out of the container to spawn the smoke on

        Environment.drawableBackgroundAddQueue.add(this.animatableGraphicsEntity);
    }

}
