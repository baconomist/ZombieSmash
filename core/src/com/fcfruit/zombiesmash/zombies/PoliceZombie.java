package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.AnimationState;

import java.util.Random;

/**
 * Created by Lucas on 2017-11-06.
 */

public class PoliceZombie extends Zombie
{

    private double timeBeforeAttack = 3500 + new Random().nextInt(2500);
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

    @Override
    public void onAnimationComplete(AnimationState.TrackEntry entry)
    {
        super.onAnimationComplete(entry);
        if (!this.isAtObjective() && this.isInLevel() && !this.isMovingToNewGround() && System.currentTimeMillis() - this.attackTimer >= this.timeBeforeAttack)
        {
            if (this.getCurrentAnimation().equals("walk"))
            {
                this.setAnimation("attack1");
            } else if(this.getCurrentAnimation().equals("crawl")) // animation could be 'attack2'
            {
                this.setAnimation("crawl_attack");
            }
            this.attackTimer = System.currentTimeMillis();
        }
        else
        {
            this.setAnimation(this.moveAnimation);
        }
    }

    @Override
    protected void onAttack1Complete()
    {
        super.onAttack1Complete();
        if (!this.isAtObjective())
        {
            this.setAnimation(this.moveAnimation);
        }
    }

    @Override
    protected void onCrawlAttackComplete()
    {
        super.onCrawlAttackComplete();
        if (!this.isAtObjective())
        {
            this.setAnimation(this.moveAnimation);
        }
    }
}
