package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.powerups.grenade.Grenade;

import java.util.ArrayList;

public class SuicideZombie extends NewZombie
{

    public SuicideZombie(Integer id)
    {
        super(id);

        this.moveAnimation = "run";

        this.detachableEntitiesToStayAlive.add("head");
        this.detachableEntitiesToStayAlive.add("left_arm");
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

    }

    @Override
    protected void onAttack1Complete()
    {
        Environment.level.objective.takeDamage(3f);
        this.resetToInitialGround();
        this.disable_optimization();
        ((Grenade)this.containerEntity.getDrawableEntities().get("grenade")).explode();
    }

    @Override
    protected void onObjective()
    {
        if(!this.isMoving())
        {
            Gdx.app.log("changing", "anm " + this.isMoving());
            this.setAnimation("attack1");
        }
    }

    @Override
    protected void createPart(Body physicsBody, String bodyName, Sprite sprite, ArrayList<Joint> joints, ContainerEntityInterface containerEntity)
    {
        super.createPart(physicsBody, bodyName, sprite, joints, containerEntity);

        if (bodyName.equals("grenade"))
        {
            Grenade grenade = new Grenade(sprite, physicsBody, joints, containerEntity);
            this.getDrawableEntities().put("grenade", grenade);
            this.getInteractiveEntities().put("grenade", grenade);
            this.getDetachableEntities().put("grenade", grenade);
        }

    }
}