package com.fcfruit.zombiesmash.zombies;

import com.fcfruit.zombiesmash.Environment;

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

    }

    @Override
    protected void onAttack1Complete()
    {
        Environment.level.objective.takeDamage(3f);
    }

    @Override
    protected void onObjective()
    {
        this.setAnimation("attack1");
    }
}