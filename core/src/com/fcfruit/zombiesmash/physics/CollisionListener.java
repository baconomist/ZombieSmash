package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.ExplodableEntityInterface;

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


            for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity : Environment.level.getDrawableEntities())
            {
                if (drawableEntity instanceof ExplodableEntityInterface)
                {
                    if (contact.getFixtureA().getBody().equals(((ExplodableEntityInterface) drawableEntity).getPhysicsBody())
                            || contact.getFixtureB().getBody().equals(((ExplodableEntityInterface) drawableEntity).getPhysicsBody()))
                    {
                        if (!Environment.explodableEntityQueue.contains(drawableEntity) && ((ExplodableEntityInterface) drawableEntity).shouldExplode())
                        {
                            Environment.explodableEntityQueue.add(((ExplodableEntityInterface) drawableEntity));
                        }
                    }
                }

                if (drawableEntity instanceof com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface)
                {

                    for (com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface drawableEntity1 : ((com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface) drawableEntity).getDrawableEntities().values())
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

                    for (DetachableEntityInterface detachableEntity : ((com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface) drawableEntity).getDetachableEntities().values())
                    {
                        if(contact.getFixtureA().getBody().equals(((com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface)detachableEntity).getPhysicsBody()) || contact.getFixtureB().getBody().equals(((com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface)detachableEntity).getPhysicsBody()))
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
