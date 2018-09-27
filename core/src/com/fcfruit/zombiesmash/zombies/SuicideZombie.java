package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.BleedablePoint;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.physics.PhysicsData;
import com.fcfruit.zombiesmash.powerups.grenade.Grenade;

import java.util.ArrayList;

public class SuicideZombie extends Zombie
{

    public SuicideZombie(Integer id)
    {
        super(id);

        this.moveAnimation = "run";

        this.detachableEntitiesToStayAlive.add("head");
        this.detachableEntitiesToStayAlive.add("left_arm");
        this.detachableEntitiesToStayAlive.add("grenade");
        this.detachableEntitiesToStayAlive.add("right_arm");
        this.detachableEntitiesToStayAlive.add("left_leg");
        this.detachableEntitiesToStayAlive.add("right_leg");


        this.currentParts.add("head");
        this.currentParts.add("left_arm");
        this.currentParts.add("torso");
        this.currentParts.add("right_arm");
        this.currentParts.add("left_leg");
        this.currentParts.add("right_leg");
        this.currentParts.add("grenade");

        this.setSpeed(4);

    }

    @Override
    protected void onAttack1()
    {
        if(this.containerEntity.getDrawableEntities().get("grenade") != null)
        {
            Environment.level.objective.takeDamage(3f);
            this.resetToInitialGround();
            this.enable_physics(); // Need to sync grenade to zombie pos -> enable_physics()
            Grenade grenade = (Grenade) this.containerEntity.getDrawableEntities().get("grenade");
            grenade.setPosition(new Vector2(grenade.getPosition().x, this.getPosition().y));
            Environment.explodableEntityQueue.add(grenade);
            grenade.setState("waiting_for_detach");
            Environment.detachableEntityDetachQueue.add(grenade);
        }
    }

    @Override
    protected void onObjective()
    {
        if(!this.isMoving())
        {
            this.setAnimation("attack1");
        }
    }

    @Override
    protected void createPart(Body physicsBody, String bodyName, Sprite sprite, ArrayList<Joint> joints, ContainerEntityInterface containerEntity, Array<BleedablePoint> bleedablePoints)
    {
        if (bodyName.equals("grenade"))
        {
            Grenade grenade = new Grenade(sprite, physicsBody, joints, containerEntity);

            PhysicsData physicsData = new PhysicsData(this);
            physicsData.add_data(grenade);
            physicsBody.setUserData(physicsData);
            for(Fixture fixture : physicsBody.getFixtureList())
            {
                fixture.setUserData(physicsData);
            }

            this.getDrawableEntities().put("grenade", grenade);
            this.getInteractiveEntities().put("grenade", grenade);
            this.getDetachableEntities().put("grenade", grenade);
        }
        else
        {
            super.createPart(physicsBody, bodyName, sprite, joints, containerEntity, bleedablePoints);
        }
    }
}