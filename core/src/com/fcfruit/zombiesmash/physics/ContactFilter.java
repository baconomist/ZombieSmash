package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fcfruit.zombiesmash.effects.Blood;
import com.fcfruit.zombiesmash.zombies.NewZombie;


/**
 * Created by Lucas on 2017-11-01.
 */

public class ContactFilter implements com.badlogic.gdx.physics.box2d.ContactFilter
{
    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB)
    {


        if (fixtureA.getUserData() instanceof NewZombie && fixtureB.getUserData() instanceof NewZombie)
        {


            if (((NewZombie) fixtureA.getUserData()).id == ((NewZombie) fixtureB.getUserData()).id)
            {

                if ((fixtureA.getFilterData().maskBits & fixtureB.getFilterData().categoryBits) != 0 || (fixtureB.getFilterData().maskBits & fixtureA.getFilterData().categoryBits) != 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }

            }
            else
            {
                return false;
            }

        } else if (fixtureA.getUserData() instanceof Blood || fixtureB.getUserData() instanceof Blood)
        {
            return false;
        }

        //Gdx.app.log("baba", ""+fixtureA.getBody().getUserData() + " " + fixtureB.getBody().getUserData());
        // Gdx.app.log("baba", ""+fixtureA.getUserData() + " " + fixtureB.getUserData());
        return true;
        //return !(fixtureA.getUserData().equals("wall") || fixtureB.getUserData().equals("wall")) || (fixtureA.getUserData() instanceof NewZombie && ((NewZombie) fixtureA.getUserData()).isInLevel()) || (fixtureB.getUserData() instanceof NewZombie && ((NewZombie) fixtureB.getUserData()).isInLevel());

    }
}
