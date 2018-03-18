package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.effects.Blood;
import com.fcfruit.zombiesmash.entity.BleedableEntity;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.ExplosionEntityParticle;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.ExplodableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.zombies.NewPart;
import com.fcfruit.zombiesmash.zombies.NewZombie;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.Zombie;

/**
 * Created by Lucas on 2017-09-19.
 */

public class CollisionListener implements ContactListener
{

    @Override
    public void beginContact(Contact contact)
    {

        // Probably can't detach parts here because this is in the world timestep
        if (contact.getFixtureA().getBody().getType() == BodyDef.BodyType.StaticBody
                || contact.getFixtureB().getBody().getType() == BodyDef.BodyType.StaticBody)
        {
            /*
            if (contact.getFixtureA().getUserData() instanceof Blood)
            {
                ((Blood) contact.getFixtureA().getUserData()).readyForDestroy = true;
            } else if (contact.getFixtureB().getUserData() instanceof Blood)
            {
                ((Blood) contact.getFixtureB().getUserData()).readyForDestroy = true;
            }*/


            for (DrawableEntityInterface drawableEntity : Environment.level.getDrawableEntities())
            {
                if (drawableEntity instanceof ContainerEntityInterface)
                {

                    for (DrawableEntityInterface drawableEntity1 : ((ContainerEntityInterface) drawableEntity).getDrawableEntities().values())
                    {
                        if (drawableEntity1 instanceof ExplodableEntityInterface)
                        {
                            if (contact.getFixtureA().getBody().equals(((ExplodableEntityInterface) drawableEntity1).getPhysicsBody())
                                    || contact.getFixtureB().getBody().equals(((ExplodableEntityInterface) drawableEntity1).getPhysicsBody()))
                            {
                                if (!Environment.explodableEntityQueue.contains(drawableEntity1) && ((ExplodableEntityInterface) drawableEntity1).shouldExplode())
                                {
                                    Environment.explodableEntityQueue.add(((ExplodableEntityInterface) drawableEntity1));
                                }
                            }
                        }
                    }

                    for (DetachableEntityInterface detachableEntity : ((ContainerEntityInterface) drawableEntity).getDetachableEntities().values())
                    {
                        if(contact.getFixtureA().getBody().equals(((PhysicsEntityInterface)detachableEntity).getPhysicsBody()) || contact.getFixtureB().getBody().equals(((PhysicsEntityInterface)detachableEntity).getPhysicsBody()))
                        {
                            if (!detachableEntity.getState().equals("detached") && detachableEntity.shouldDetach())
                            {
                                detachableEntity.setState("waiting_for_detach");
                                if (!Environment.detachableEntityDetachQueue.contains(detachableEntity))
                                {
                                    Environment.detachableEntityDetachQueue.add(detachableEntity);
                                }
                            }
                        }

                    }
                }

            }

        }

    }

    @Override
    public void endContact(Contact contact)
    {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold)
    {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse)
    {

    }


}
