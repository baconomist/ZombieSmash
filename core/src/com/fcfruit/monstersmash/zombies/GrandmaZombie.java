package com.fcfruit.monstersmash.zombies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.fcfruit.monstersmash.entity.BleedablePoint;
import com.fcfruit.monstersmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.monstersmash.zombies.parts.SpecialPart;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-11-21.
 */

public class GrandmaZombie extends Zombie
{

    private boolean downed = false;
    private boolean end_run_completed = false;

    public GrandmaZombie(Integer id)
    {
        super(id);

        this.moveAnimation = "walk";
        this.setSpeed(0.5f);
        this.setMoveDistance(4f);

        this.detachableEntitiesToStayAlive.add("head");
        this.detachableEntitiesToStayAlive.add("left_armback");
        this.detachableEntitiesToStayAlive.add("left_armfront");
        this.detachableEntitiesToStayAlive.add("right_armback");
        this.detachableEntitiesToStayAlive.add("right_armfront");
        this.detachableEntitiesToStayAlive.add("left_legback");
        this.detachableEntitiesToStayAlive.add("left_legfront");
        this.detachableEntitiesToStayAlive.add("right_legback");
        this.detachableEntitiesToStayAlive.add("right_legfront");
        this.detachableEntitiesToStayAlive.add("rollingpin");

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
        this.currentParts.add("rollingpin");

    }

    @Override
    public void setup(int direction)
    {
        super.setup(direction);
        if(!downed)
        {
            for (DetachableEntityInterface detachableEntityInterface : this.getDetachableEntities().values())
            {
                detachableEntityInterface.setForceForDetach(999999); // Disable Detaching first time
            }
        }
    }

    @Override
    protected void onGetupEnd()
    {
        super.onGetupEnd();
        this.moveAnimation = "start_run";
        this.setAnimation(moveAnimation);
        for (DetachableEntityInterface detachableEntityInterface : this.getDetachableEntities().values())
        {
            detachableEntityInterface.setForceForDetach(100); // Enable detaching
        }
        this.downed = true;
    }

    @Override
    protected void onObjective()
    {
        if(this.downed && this.end_run_completed) // Only attack after run ended
            super.onObjective();
        else if(this.downed)
        {
            this.setAnimation("end_run");
            this.getState().getCurrent(0).setLoop(false); // Prevents reseting to first frame and seeing "red" glitch
        }
    }

    @Override
    void onAnimationComplete(AnimationState.TrackEntry entry)
    {
        super.onAnimationComplete(entry);
        if(entry.getAnimation().getName().equals("start_run"))
        {
            this.setAnimation("run");
            this.setSpeed(4);
            this.setMoveDistance(8);
            this.end_run_completed = false;
        }
        else if(entry.getAnimation().getName().equals("end_run"))
        {
            this.end_run_completed = true;
        }
    }

    @Override
    protected void createPart(Body physicsBody, String bodyName, Sprite sprite, ArrayList<Joint> joints, com.fcfruit.monstersmash.entity.interfaces.ContainerEntityInterface containerEntity, Array<com.fcfruit.monstersmash.entity.BleedablePoint> bleedablePoints)
    {
        if(bodyName.equals("rollingpin"))
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



