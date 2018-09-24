package com.fcfruit.zombiesmash.zombies.parts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.BleedablePoint;
import com.fcfruit.zombiesmash.entity.DestroyableEntity;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.InteractivePhysicsEntity;
import com.fcfruit.zombiesmash.entity.OptimizableEntity;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DestroyableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.NameableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PostLevelDestroyableInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-01-07.
 */

public class Torso implements DrawableEntityInterface, OptimizableEntityInterface, InteractivePhysicsEntityInterface, NameableEntityInterface, PostLevelDestroyableInterface
{
    private String name;

    private ContainerEntityInterface parentContainer;
    private OptimizableEntity optimizableEntity;
    private DestroyableEntity destroyableEntity;
    private DrawablePhysicsEntity drawableEntity;
    private InteractivePhysicsEntity interactivePhysicsEntity;

    private Array<BleedablePoint> bleedablePoints;

    public Torso(String name, Sprite sprite, Body physicsBody, ContainerEntityInterface parentContainer, Array<BleedablePoint> bleedablePoints)
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
        this.destroyableEntity = new DestroyableEntity(this.parentContainer, this, this);

        this.bleedablePoints = bleedablePoints;
    }


    @Override
    public void draw(SpriteBatch batch)
    {
        this.drawableEntity.draw(batch);

        for(BleedablePoint bleedablePoint : this.bleedablePoints)
        {
            bleedablePoint.draw(batch);
        }
    }

    @Override
    public void update(float delta)
    {
        this.drawableEntity.update(delta);
        this.interactivePhysicsEntity.update(delta);

        for(BleedablePoint bleedablePoint : this.bleedablePoints)
        {
            bleedablePoint.update(delta);
        }

        this.optimizableEntity.update(delta);
        this.destroyableEntity.update(delta);
    }


    @Override
    public void onTouchDown(float x, float y, int p)
    {
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

        /*
        * TODO:
        * - Fix this if need be because this can cause bugs
        * where even if something isn't attached to torso
        * the torso will disable optimization.
        * */
        for(InteractiveEntityInterface interactiveEntityInterface : this.parentContainer.getInteractiveEntities().values())
        {
            if(interactiveEntityInterface.isTouching())
            {
                this.disable_optimization();
            }
        }

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
    public void dispose()
    {

    }

    // Unused
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

    }
}

