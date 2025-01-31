package com.fcfruit.monstersmash.zombies;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.zombies.Zombie;

import java.util.Random;

/**
 * Created by Lucas on 2017-11-06.
 */

public class PoliceZombie extends Zombie
{

    private double timeBeforeAttack = 2500 + new Random().nextDouble()*2500d;
    private double attackTimer = System.currentTimeMillis();

    public PoliceZombie(Integer id)
    {
        super(id);

        this.moveAnimation = "walk";

        this.detachableEntitiesToStayAlive.add("head");
        this.detachableEntitiesToStayAlive.add(new String[]{"left_arm", "right_arm"});


        this.currentParts.add("head");
        this.currentParts.add("left_arm");
        this.currentParts.add("torso");
        this.currentParts.add("right_arm");
        this.currentParts.add("left_leg");
        this.currentParts.add("right_leg");

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
            if (entry.getAnimation().getName().equals("walk"))
            {
                this.setAnimation("attack1");
            } else if(this.getCurrentAnimation().equals("crawl")) // animation could be 'attack2'
            {
                this.setAnimation("crawl_attack");
            }
            this.attackTimer = System.currentTimeMillis();
        }
        else if(!this.isAtObjective() && this.timesAnimationCompleted() >= 1)
        {
            this.setAnimation(this.moveAnimation);
        }

        if(!this.isInShootingRange() || this.getCurrentAnimation().equals(this.moveAnimation) && this.timesAnimationCompleted() < 1)
            this.attackTimer = System.currentTimeMillis();
    }

    @Override
    void onAnimationComplete(AnimationState.TrackEntry entry)
    {
        super.onAnimationComplete(entry);
        if(entry.getAnimation().getName().equals("attack1") || entry.getAnimation().getName().equals("crawl_attack"))
        {
            if (!this.isAtObjective())
            {
                this.attackTimer = System.currentTimeMillis();
                this.setAnimation(this.moveAnimation);
            }
        }
    }

}
