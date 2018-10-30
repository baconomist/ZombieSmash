package com.fcfruit.monstersmash.zombies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;
import com.fcfruit.monstersmash.entity.BleedablePoint;
import com.fcfruit.monstersmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.monstersmash.zombies.Zombie;
import com.fcfruit.monstersmash.zombies.parts.SpecialPart;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-11-21.
 */

public class BigZombie extends Zombie
{

    public BigZombie(Integer id)
    {
        super(id);

        this.moveAnimation = "walk";
        this.setSpeed(0.5f);
        this.setMoveDistance(3);

        this.detachableEntitiesToStayAlive.add("head");
        this.detachableEntitiesToStayAlive.add("left_armback");
        this.detachableEntitiesToStayAlive.add("left_armfront");
        this.detachableEntitiesToStayAlive.add("right_armback");
        this.detachableEntitiesToStayAlive.add("right_armfront");
        this.detachableEntitiesToStayAlive.add("left_legback");
        this.detachableEntitiesToStayAlive.add("left_legfront");
        this.detachableEntitiesToStayAlive.add("right_legback");
        this.detachableEntitiesToStayAlive.add("right_legfront");
        this.detachableEntitiesToStayAlive.add("bike");

        this.currentParts.add("head");
        this.currentParts.add("torso");
        this.currentParts.add("left_armback");
        this.currentParts.add("left_armfront");
        this.currentParts.add("right_armback");
        this.currentParts.add("right_armfront");
        this.currentParts.add("left_legback");
        this.currentParts.add("left_legfront");
        this.currentParts.add("right_legback");
        this.currentParts.add("right_legfront");
        this.currentParts.add("bike");

    }

    @Override
    protected void animationSetup()
    {
        super.animationSetup();
        this.getSkeleton().setSkin("biker"+((int)Math.round(Math.random()*2) + 1));
    }

    @Override
    public void onTouchDown(float screenX, float screenY, int p)
    {
        // Don't pick up the zombie
    }

    @Override
    protected void createPart(Body physicsBody, String bodyName, Sprite sprite, ArrayList<Joint> joints, ContainerEntityInterface containerEntity, Array<com.fcfruit.monstersmash.entity.BleedablePoint> bleedablePoints)
    {
        if(bodyName.equals("bike"))
        {
            com.fcfruit.monstersmash.zombies.parts.SpecialPart part = new com.fcfruit.monstersmash.zombies.parts.SpecialPart(bodyName, sprite, physicsBody, joints, containerEntity);
            this.getDrawableEntities().put(bodyName, part);
            this.getInteractiveEntities().put(bodyName, part);
            this.getDetachableEntities().put(bodyName, part);
        }
        else
            super.createPart(physicsBody, bodyName, sprite, joints, containerEntity, bleedablePoints);
    }
}



