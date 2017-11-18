package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fcfruit.zombiesmash.level.House;
import com.fcfruit.zombiesmash.level.Objective;
import com.fcfruit.zombiesmash.zombies.Zombie;

/**
 * Created by Lucas on 2017-11-01.
 */

public class ContactFilter implements com.badlogic.gdx.physics.box2d.ContactFilter {
    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {

        if(fixtureA.getUserData() instanceof Objective && fixtureB.getUserData() instanceof Zombie){
            ((Zombie)fixtureB.getUserData()).isAtObjective = true;
            return false;
        }
        else if(fixtureB.getUserData() instanceof Objective && fixtureA.getUserData() instanceof Zombie){
            ((Zombie)fixtureA.getUserData()).isAtObjective = true;
            return false;
        }

        if (fixtureA.getUserData() != null && fixtureA.getUserData() instanceof Zombie && fixtureB.getUserData() != null && fixtureB.getUserData() instanceof Zombie) {


            if (((Zombie) fixtureA.getUserData()).id == ((Zombie) fixtureB.getUserData()).id) {

                if ((fixtureA.getFilterData().maskBits & fixtureB.getFilterData().categoryBits) != 0 || (fixtureB.getFilterData().maskBits & fixtureA.getFilterData().categoryBits) != 0) {
                    return true;
                } else {
                    return false;
                }

            }


        }


        if (fixtureA.getBody().getType() == BodyDef.BodyType.StaticBody || fixtureB.getBody().getType() == BodyDef.BodyType.StaticBody) {

            if (fixtureA.getUserData() != null && !((Zombie) fixtureA.getUserData()).justTouched || fixtureB.getUserData() != null && !((Zombie) fixtureB.getUserData()).justTouched) {
                return true;
            } else {
                return true;
            }
        } else {
            return false;
        }


    }
}
