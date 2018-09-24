package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;
import com.fcfruit.zombiesmash.entity.BleedablePoint;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.zombies.parts.BodyRock;

import java.util.ArrayList;

public class ArmoredZombie extends Zombie
{
    public ArmoredZombie(Integer id)
    {
        super(id);

        this.moveAnimation = "run";

        this.detachableEntitiesToStayAlive.add("head");
        this.detachableEntitiesToStayAlive.add("left_arm_back");
        this.detachableEntitiesToStayAlive.add("left_arm_front");
        this.detachableEntitiesToStayAlive.add("right_arm_back");
        this.detachableEntitiesToStayAlive.add("right_arm_front");
        this.detachableEntitiesToStayAlive.add("left_leg");
        this.detachableEntitiesToStayAlive.add("right_leg");

        this.currentParts.add("head");
        this.currentParts.add("left_arm_front");
        this.currentParts.add("left_arm_back");
        this.currentParts.add("torso");
        this.currentParts.add("right_arm_front");
        this.currentParts.add("right_arm_back");
        this.currentParts.add("left_leg");
        this.currentParts.add("right_leg");
        this.currentParts.add("gun");
    }

    @Override
    public void onTouchDown(float screenX, float screenY, int p)
    {
        // Don't pick zombie up
    }

    @Override
    protected void createPart(Body physicsBody, String bodyName, Sprite sprite, ArrayList<Joint> joints, ContainerEntityInterface containerEntity, Array<BleedablePoint> bleedablePoints)
    {
        Gdx.app.log("aaa", ""+bodyName);
        if(bodyName.equals("gun"))
        {
            BodyRock part = new BodyRock(bodyName, sprite, physicsBody, joints, containerEntity);
            this.getDrawableEntities().put(bodyName, part);
            this.getInteractiveEntities().put(bodyName, part);
            this.getDetachableEntities().put(bodyName, part);
        }
        else
            super.createPart(physicsBody, bodyName, sprite, joints, containerEntity, bleedablePoints);
    }
}
