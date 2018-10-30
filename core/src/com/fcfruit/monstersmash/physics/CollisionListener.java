package com.fcfruit.monstersmash.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.ExplodableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.monstersmash.physics.PhysicsData;

/**
 * Created by Lucas on 2017-09-19.
 */

public class CollisionListener implements ContactListener
{

    private com.fcfruit.monstersmash.physics.PhysicsData fixtureAData;
    private com.fcfruit.monstersmash.physics.PhysicsData fixtureBData;
    private com.fcfruit.monstersmash.physics.PhysicsData[] fixtureData = new com.fcfruit.monstersmash.physics.PhysicsData[2];

    @Override
    public void beginContact(Contact contact)
    {

        fixtureAData = ((com.fcfruit.monstersmash.physics.PhysicsData) contact.getFixtureA().getUserData());
        fixtureBData = ((com.fcfruit.monstersmash.physics.PhysicsData) contact.getFixtureB().getUserData());

        fixtureData[0] = fixtureAData;
        fixtureData[1] = fixtureBData;

        /*
        * Probably can't detach parts here because this is in the world timestep
        * */

        // Only check the collision if an entity collided with a static body (ie the ground)
        if (contact.getFixtureA().getBody().getType() == BodyDef.BodyType.StaticBody
                || contact.getFixtureB().getBody().getType() == BodyDef.BodyType.StaticBody)
        {
            // Loop through fixtureA and fixtureB to avoid copy-pasting code
            for(PhysicsData physicsData : fixtureData)
            {
                // Explodable Entity
                if(physicsData.containsInstanceOf(ExplodableEntityInterface.class))
                {
                    ExplodableEntityInterface explodableEntityInterface = (ExplodableEntityInterface) physicsData.getClassInstance(ExplodableEntityInterface.class);

                    if (!Environment.explodableEntityQueue.contains(explodableEntityInterface) && explodableEntityInterface.shouldExplode())
                    {
                        Environment.explodableEntityQueue.add(explodableEntityInterface);
                        break;
                    }
                }
                // Detachable Entity
                else if(physicsData.containsInstanceOf(DetachableEntityInterface.class))
                {
                    DetachableEntityInterface detachableEntityInterface = (DetachableEntityInterface) physicsData.getClassInstance(DetachableEntityInterface.class);

                    if (!detachableEntityInterface.getState().equals("detached") && detachableEntityInterface.shouldDetach())
                    {
                        detachableEntityInterface.setState("waiting_for_detach");
                        if (!Environment.detachableEntityDetachQueue.contains(detachableEntityInterface))
                        {
                            Environment.detachableEntityDetachQueue.add(detachableEntityInterface);
                            break;
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
