package com.fcfruit.monstersmash.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.brains.Brain;
import com.fcfruit.monstersmash.effects.BleedBlood;
import com.fcfruit.monstersmash.entity.ParticleEntity;
import com.fcfruit.monstersmash.entity.interfaces.MultiGroundEntityInterface;
import com.fcfruit.monstersmash.powerups.PowerupCrate;
import com.fcfruit.monstersmash.powerups.explodable.Explodable;
import com.fcfruit.monstersmash.powerups.explodable.Grenade;
import com.fcfruit.monstersmash.powerups.explodable.Molotov;
import com.fcfruit.monstersmash.powerups.rock_powerup.Rock;
import com.fcfruit.monstersmash.powerups.rocket.Rocket;
import com.fcfruit.monstersmash.zombies.Zombie;

import java.util.Random;


/**
 * Created by Lucas on 2017-11-01.
 */

public class ContactFilter implements com.badlogic.gdx.physics.box2d.ContactFilter
{
    private Random random = new Random();

    private PhysicsData fixtureAData;
    private PhysicsData fixtureBData;
    private PhysicsData[] fixtureData = new PhysicsData[2];

    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB)
    {

        fixtureAData = ((PhysicsData) fixtureA.getUserData());
        fixtureBData = ((PhysicsData) fixtureB.getUserData());

        fixtureData[0] = fixtureAData;
        fixtureData[1] = fixtureBData;

        if (fixtureAData.containsInstanceOf(Zombie.class) && fixtureBData.containsInstanceOf(Zombie.class))
        {

            if (((Zombie) fixtureAData.getClassInstance(Zombie.class)).id == ((Zombie) fixtureBData.getClassInstance(Zombie.class)).id)
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
                && !(fixtureA.getBody().getPosition().y < fixtureB.getBody().getPosition().y)
                && !((MultiGroundEntityInterface) fixtureAData.getClassInstance(MultiGroundEntityInterface.class)).isMovingToNewGround())
        {
            return true;
        }
        // Multiground Entity
        else if(fixtureBData.containsInstanceOf(MultiGroundEntityInterface.class) && fixtureAData.getData().contains("ground", false) &&
                ((MultiGroundEntityInterface) fixtureBData.getClassInstance(MultiGroundEntityInterface.class)).getCurrentGround() == Environment.physics.whichGround(fixtureA.getBody())
                && !(fixtureB.getBody().getPosition().y < fixtureA.getBody().getPosition().y)
                && !((MultiGroundEntityInterface) fixtureBData.getClassInstance(MultiGroundEntityInterface.class)).isMovingToNewGround())
        {
            return true;
        }
        // Rock Powerup, Make Rock Collide with ground higher than 0
        else if(fixtureAData.containsInstanceOf(com.fcfruit.monstersmash.powerups.rock_powerup.Rock.class) && fixtureBData.containsInstanceOf(Zombie.class) || fixtureAData.containsInstanceOf(com.fcfruit.monstersmash.powerups.rock_powerup.Rock.class) && (fixtureBData.getData().contains("ground", false) && Environment.physics.whichGround(fixtureB.getBody()) == this.random.nextInt(1) + 1)
                || fixtureBData.containsInstanceOf(com.fcfruit.monstersmash.powerups.rock_powerup.Rock.class) && fixtureAData.containsInstanceOf(Zombie.class) || fixtureBData.containsInstanceOf(com.fcfruit.monstersmash.powerups.rock_powerup.Rock.class) && (fixtureAData.getData().contains("ground", false) && Environment.physics.whichGround(fixtureA.getBody()) == this.random.nextInt(1) + 1))
        {
            return true;
        }
        // Powerup Crate
        else if(fixtureAData.containsInstanceOf(com.fcfruit.monstersmash.powerups.PowerupCrate.class) || fixtureBData.containsInstanceOf(com.fcfruit.monstersmash.powerups.PowerupCrate.class))
        {
            return false;
        }
        // Brain
        else if(fixtureAData.containsInstanceOf(com.fcfruit.monstersmash.brains.Brain.class) || fixtureBData.containsInstanceOf(com.fcfruit.monstersmash.brains.Brain.class))
        {
            return false;
        }
        // Collision BETWEEN Grenades + Molotovs
        else if(fixtureAData.containsInstanceOf(com.fcfruit.monstersmash.powerups.explodable.Explodable.class) && fixtureBData.containsInstanceOf(com.fcfruit.monstersmash.powerups.explodable.Explodable.class))
        {
            return true;
        }
        // Bomb
        else if(fixtureAData.containsInstanceOf(com.fcfruit.monstersmash.powerups.rocket.Rocket.class) || fixtureBData.containsInstanceOf(com.fcfruit.monstersmash.powerups.rocket.Rocket.class))
        {
            return false;
        }
        // ParticleEntity
        else if(fixtureAData.containsInstanceOf(com.fcfruit.monstersmash.entity.ParticleEntity.class) && fixtureB.getBody().getType() != BodyDef.BodyType.StaticBody
                || fixtureBData.containsInstanceOf(com.fcfruit.monstersmash.entity.ParticleEntity.class) && fixtureA.getBody().getType() != BodyDef.BodyType.StaticBody)
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
