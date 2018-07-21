package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Lucas on 2017-11-06.
 */

public class PoliceZombie extends Zombie
{

    private double timeBeforeAttack = 5000;
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
    public void moveBy(Vector2 moveBy)
    {
        if (this.isInLevel() && System.currentTimeMillis() - this.attackTimer >= this.timeBeforeAttack)
        {
            if (this.getCurrentAnimation().equals("walk"))
            {
                this.setAnimation("attack1");
            } else
            {
                this.setAnimation("crawl_attack");
            }
            this.attackTimer = System.currentTimeMillis();
        } else
        {
            super.moveBy(moveBy);
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
}
