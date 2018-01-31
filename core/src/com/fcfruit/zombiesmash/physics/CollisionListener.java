package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.zombies.NewPart;
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


            for (DrawableEntityInterface drawableEntityInterface : Environment.level.getDrawableEntities()) {
                if(drawableEntityInterface instanceof ContainerEntityInterface)
                {
                    for(DetachableEntityInterface detachableEntityInterface : ((ContainerEntityInterface) drawableEntityInterface).getDetachableEntities().values())
                    {
                        if(!detachableEntityInterface.getState().equals("detached") && detachableEntityInterface.shouldDetach())
                        {
                            detachableEntityInterface.setState("waiting_for_detach");
                            if(!Environment.detachableEntityDetachQueue.contains(detachableEntityInterface))
                            {
                                Environment.detachableEntityDetachQueue.add(detachableEntityInterface);
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
