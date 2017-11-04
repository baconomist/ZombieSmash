package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fcfruit.zombiesmash.zombies.Zombie;

/**
 * Created by Lucas on 2017-11-01.
 */

public class ContactFilter implements com.badlogic.gdx.physics.box2d.ContactFilter {
    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {

        if(fixtureA.getUserData() == fixtureB.getUserData()) {

            if ((fixtureA.getFilterData().maskBits & fixtureB.getFilterData().categoryBits) != 0 || (fixtureB.getFilterData().maskBits & fixtureA.getFilterData().categoryBits) != 0) {
                return true;
            } else {
                return false;
            }

        }

        if(fixtureA.getBody().getType() == BodyDef.BodyType.StaticBody || fixtureB.getBody().getType() == BodyDef.BodyType.StaticBody){
            return true;
        }
        else{
            return false;
        }

    }
}
