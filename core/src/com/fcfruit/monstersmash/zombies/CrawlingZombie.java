package com.fcfruit.monstersmash.zombies;

import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.zombies.Zombie;

public class CrawlingZombie extends Zombie
{
    public CrawlingZombie(Integer id) {
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

        this.setSpeed(2f);

    }

    @Override
    public void setup(int direction)
    {
        super.setup(direction);
        this.getState().setTimeScale(2f);
    }

    @Override
    protected void onAttack1()
    {
        Environment.level.objective.takeDamage(0.25f);
    }

    @Override
    protected void onAttack2()
    {
        Environment.level.objective.takeDamage(0.35f);
    }
}
