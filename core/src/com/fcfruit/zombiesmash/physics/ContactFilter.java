package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.effects.Blood;
import com.fcfruit.zombiesmash.entity.ParticleEntity;
import com.fcfruit.zombiesmash.entity.interfaces.MultiGroundEntityInterface;
import com.fcfruit.zombiesmash.powerups.PowerupCrate;
import com.fcfruit.zombiesmash.powerups.grenade.Grenade;
import com.fcfruit.zombiesmash.powerups.rock_powerup.Rock;
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
                } else
                {
                    return false;
                }

            } else
            {
                return false;
            }

        } else if (fixtureA.getUserData() instanceof Blood || fixtureB.getUserData() instanceof Blood)
        {
            return false;
        }

        if ((fixtureA.getUserData().equals("ground") && Environment.physics.whichGround(fixtureA.getBody()) == 0)
                || (fixtureB.getUserData().equals("ground") && Environment.physics.whichGround(fixtureB.getBody()) == 0))
        {
            return true;
        } else if ((fixtureA.getUserData() instanceof MultiGroundEntityInterface && fixtureB.getUserData().equals("ground") &&
                ((MultiGroundEntityInterface) fixtureA.getUserData()).getCurrentGround() == Environment.physics.whichGround(fixtureB.getBody()) &&
                !((MultiGroundEntityInterface) fixtureA.getUserData()).isMovingToNewGround()))
        {
            return true;
        } else if ((fixtureB.getUserData() instanceof MultiGroundEntityInterface && fixtureA.getUserData().equals("ground")
                && ((MultiGroundEntityInterface) fixtureB.getUserData()).getCurrentGround() == Environment.physics.whichGround(fixtureA.getBody())
                && !((MultiGroundEntityInterface) fixtureB.getUserData()).isMovingToNewGround()))
        {
            return true;
        } else if (fixtureA.getUserData() instanceof Rock && fixtureB.getUserData() instanceof NewZombie
                || fixtureB.getUserData() instanceof Rock && fixtureA.getUserData() instanceof NewZombie)
        {
            return true;
        }
        else if (fixtureA.getUserData() instanceof PowerupCrate || fixtureB.getUserData() instanceof PowerupCrate)
        {
            return false;
        } else if (fixtureA.getUserData() instanceof Grenade && fixtureB.getUserData() instanceof Grenade)
        {
            return true;
        } else if (fixtureA.getUserData() instanceof ParticleEntity && fixtureB.getBody().getType() != BodyDef.BodyType.StaticBody
                || fixtureB.getUserData() instanceof ParticleEntity && fixtureA.getBody().getType() != BodyDef.BodyType.StaticBody)
        {
            return true;
        }

        return false;

        /*return (fixtureA.getUserData().equals("ground") && Environment.physics.whichGround(fixtureA.getBody()) == 0)
                || (fixtureB.getUserData().equals("ground") && Environment.physics.whichGround(fixtureB.getBody()) == 0)
                || (
                (fixtureA.getUserData() instanceof MultiGroundEntityInterface && fixtureB.getUserData().equals("ground") && ((MultiGroundEntityInterface) fixtureA.getUserData()).getCurrentGround() == Environment.physics.whichGround(fixtureB.getBody()) && !((MultiGroundEntityInterface) fixtureA.getUserData()).isMovingToNewGround())
                        || (fixtureB.getUserData() instanceof MultiGroundEntityInterface && fixtureA.getUserData().equals("ground") && ((MultiGroundEntityInterface) fixtureB.getUserData()).getCurrentGround() == Environment.physics.whichGround(fixtureA.getBody()) && !((MultiGroundEntityInterface) fixtureB.getUserData()).isMovingToNewGround())
        );*/

        /*
        Gdx.app.debug("baba", ""+fixtureA.getBody().getUserData() + " " + fixtureB.getBody().getUserData());
        Gdx.app.debug("baba", ""+fixtureA.getUserData() + " " + fixtureB.getUserData());
        return true;
        return !(fixtureA.getUserData().equals("wall") || fixtureB.getUserData().equals("wall")) || (fixtureA.getUserData() instanceof NewZombie && ((NewZombie) fixtureA.getUserData()).isInLevel()) || (fixtureB.getUserData() instanceof NewZombie && ((NewZombie) fixtureB.getUserData()).isInLevel());
        */
    }
}
