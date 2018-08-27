package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.brains.Brain;
import com.fcfruit.zombiesmash.effects.BleedBlood;
import com.fcfruit.zombiesmash.entity.ParticleEntity;
import com.fcfruit.zombiesmash.entity.interfaces.MultiGroundEntityInterface;
import com.fcfruit.zombiesmash.powerups.PowerupCrate;
import com.fcfruit.zombiesmash.powerups.grenade.Grenade;
import com.fcfruit.zombiesmash.powerups.rock_powerup.Rock;
import com.fcfruit.zombiesmash.powerups.rocket.Rocket;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.Random;


/**
 * Created by Lucas on 2017-11-01.
 */

public class ContactFilter implements com.badlogic.gdx.physics.box2d.ContactFilter
{
    private Random random = new Random();

    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB)
    {

        PhysicsData fixtureAData = ((PhysicsData) fixtureA.getUserData());
        PhysicsData fixtureBData = ((PhysicsData) fixtureB.getUserData());

        PhysicsData[] fixtureData = {fixtureAData, fixtureBData};

        if (fixtureA.getUserData() instanceof Zombie && fixtureB.getUserData() instanceof Zombie)
        {

            if (((Zombie) fixtureA.getUserData()).id == ((Zombie) fixtureB.getUserData()).id)
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

        }
        // Blood
        if(fixtureAData.containsInstanceOf(BleedBlood.class) || fixtureBData.containsInstanceOf(BleedBlood.class))
        {
            return false;
        }
        // Ground
        else if ((fixtureAData.getData().contains("ground", false) && Environment.physics.whichGround(fixtureA.getBody()) == 0)
                || (fixtureBData.getData().contains("ground", false) && Environment.physics.whichGround(fixtureB.getBody()) == 0))
        {
            return true;
        }
        // Multiground Entity
        else if(fixtureAData.containsInstanceOf(MultiGroundEntityInterface.class) && fixtureBData.getData().contains("ground", false) &&
                ((MultiGroundEntityInterface) fixtureAData.getClassInstance(MultiGroundEntityInterface.class)).getCurrentGround() == Environment.physics.whichGround(fixtureB.getBody())
                && !((MultiGroundEntityInterface) fixtureAData.getClassInstance(MultiGroundEntityInterface.class)).isMovingToNewGround())
        {
            return true;
        }
        // Multiground Entity
        else if(fixtureBData.containsInstanceOf(MultiGroundEntityInterface.class) && fixtureAData.getData().contains("ground", false) &&
                ((MultiGroundEntityInterface) fixtureBData.getClassInstance(MultiGroundEntityInterface.class)).getCurrentGround() == Environment.physics.whichGround(fixtureA.getBody())
                && !((MultiGroundEntityInterface) fixtureBData.getClassInstance(MultiGroundEntityInterface.class)).isMovingToNewGround())
        {
            return true;
        }
        // Rock Powerup, Make Rock Collide with ground higher than 0
        else if(fixtureAData.containsInstanceOf(Rock.class) && fixtureBData.containsInstanceOf(Zombie.class) || fixtureAData.containsInstanceOf(Rock.class) && (fixtureBData.getData().contains("ground", false) && Environment.physics.whichGround(fixtureB.getBody()) == this.random.nextInt(1) + 1)
                || fixtureBData.containsInstanceOf(Rock.class) && fixtureAData.containsInstanceOf(Zombie.class) || fixtureBData.containsInstanceOf(Rock.class) && (fixtureAData.getData().contains("ground", false) && Environment.physics.whichGround(fixtureA.getBody()) == this.random.nextInt(1) + 1))
        {
            return true;
        }
        // Powerup Crate
        else if(fixtureAData.containsInstanceOf(PowerupCrate.class) || fixtureBData.containsInstanceOf(PowerupCrate.class))
        {
            return false;
        }
        // Brain
        else if(fixtureAData.containsInstanceOf(Brain.class) || fixtureBData.containsInstanceOf(Brain.class))
        {
            return false;
        }
        // Collision BETWEEN Grenades
        else if(fixtureAData.containsInstanceOf(Grenade.class) && fixtureBData.containsInstanceOf(Grenade.class))
        {
            return true;
        }
        // Bomb
        else if(fixtureAData.containsInstanceOf(Rocket.class) || fixtureBData.containsInstanceOf(Rocket.class))
        {
            return false;
        }
        // ParticleEntity
        else if(fixtureAData.containsInstanceOf(ParticleEntity.class) && fixtureB.getBody().getType() != BodyDef.BodyType.StaticBody
                || fixtureBData.containsInstanceOf(ParticleEntity.class) && fixtureA.getBody().getType() != BodyDef.BodyType.StaticBody)
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
        return !(fixtureA.getUserData().equals("wall") || fixtureB.getUserData().equals("wall")) || (fixtureA.getUserData() instanceof Zombie && ((Zombie) fixtureA.getUserData()).isInLevel()) || (fixtureB.getUserData() instanceof Zombie && ((Zombie) fixtureB.getUserData()).isInLevel());
        */
    }
}
