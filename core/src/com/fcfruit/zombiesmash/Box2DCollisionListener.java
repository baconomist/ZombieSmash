package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.Zombie;

/**
 * Created by Lucas on 2017-09-19.
 */

public class Box2DCollisionListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        for(Zombie z : Environment.physics.zombies){

            Part part = z.getPartFromPhysicsBody(contact.getFixtureA().getBody());
            if(part == null){
                part = z.getPartFromPhysicsBody(contact.getFixtureB().getBody());
            }

            if(part != null && contact.getFixtureB().getBody().getType() == BodyDef.BodyType.StaticBody
                    || contact.getFixtureA().getBody().getType() == BodyDef.BodyType.StaticBody){

                /*if(part.isAttached && part.bodyJoint.getReactionForce(1f/Environment.physics.STEP_TIME).x > 10 ||
                        part.bodyJoint.getReactionForce(1f/Environment.physics.STEP_TIME).y > 10){
                    part.detach();
                }*/

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
