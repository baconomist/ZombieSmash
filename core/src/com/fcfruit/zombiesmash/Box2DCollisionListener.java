package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.Zombie;

/**
 * Created by Lucas on 2017-09-19.
 */

public class Box2DCollisionListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {

        // Probably can't detach parts here because this is in the world timestep

        if(contact.getFixtureA().getBody().getType() == BodyDef.BodyType.StaticBody
                || contact.getFixtureB().getBody().getType() == BodyDef.BodyType.StaticBody) {

            Part part = null;

            for (Zombie z : Environment.physics.zombies) {
                part = z.getPartFromPhysicsBody(contact.getFixtureA().getBody());
                if (part == null) {
                    part = z.getPartFromPhysicsBody(contact.getFixtureB().getBody());
                }

                if (part != null) {
                    break;
                }

            }

            if (part != null && !part.getName().equals("torso")) {

                // Recode body so either when torso is detached, it becomes a part, or the body is always the torso
                // If name is torso, torso body joint is probably null, and you call a world mehod bodyjoint.getreactionforce
                // Which triggers a box2d exception fatal error 11
                
                if (part.bodyJoint.getReactionForce(1f / Physics.STEP_TIME).x > 10 || part.bodyJoint.getReactionForce(1f / Physics.STEP_TIME).x > 10
                        || part.bodyJoint.getReactionForce(1f / Physics.STEP_TIME).y > 10 || part.bodyJoint.getReactionForce(1f / Physics.STEP_TIME).y > 10) {

                    part.setState("waiting_for_detach");

                }
                

            }

        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }



}
