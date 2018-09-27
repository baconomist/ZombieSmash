package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.BleedablePoint;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.zombies.parts.BodyRock;

import java.util.ArrayList;
import java.util.Random;

public class ArmoredZombie extends Zombie
{

    private double timeBeforeAttack = 500 + new Random().nextDouble()*2000d;
    private double attackTimer = System.currentTimeMillis();

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

        this.setSpeed(3);
    }

    private boolean isInShootingRange()
    {
        return this.getPosition().x >= Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth/2 + 1f
                && this.getPosition().x <= Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth/2 - 1f;
    }

    @Override
    public void onAnimationEvent(AnimationState.TrackEntry entry, Event event)
    {
        super.onAnimationEvent(entry, event);

        if (!this.isAtObjective() && this.isInLevel() && this.isInShootingRange() && !this.isMovingToNewGround() && System.currentTimeMillis() - this.attackTimer >= this.timeBeforeAttack)
        {
            if (entry.getAnimation().getName().equals("run"))
            {
                this.setAnimation("attack1");
            }
            this.attackTimer = System.currentTimeMillis();
        }
        else if(!this.isAtObjective())
        {
            this.setAnimation(this.moveAnimation);
        }

        if(!this.isInShootingRange() || this.getCurrentAnimation().equals(this.moveAnimation) && this.timesAnimationCompleted() < 1)
            this.attackTimer = System.currentTimeMillis();
    }

    @Override
    protected void onAttack1Complete()
    {
        Environment.level.objective.takeDamage(10f);
        if (!this.isAtObjective())
        {
            this.attackTimer = System.currentTimeMillis();
            this.setAnimation(this.moveAnimation);
        }
    }

    @Override
    protected void onAttack2Complete()
    {
        Environment.level.objective.takeDamage(20f);
    }

    @Override
    public void onTouchDown(float screenX, float screenY, int p)
    {
        // Don't pick zombie up
    }

    @Override
    protected void createPart(Body physicsBody, String bodyName, Sprite sprite, ArrayList<Joint> joints, ContainerEntityInterface containerEntity, Array<BleedablePoint> bleedablePoints)
    {
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
