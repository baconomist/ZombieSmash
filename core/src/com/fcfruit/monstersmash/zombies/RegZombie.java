package com.fcfruit.monstersmash.zombies;

import com.fcfruit.monstersmash.zombies.Zombie;

/**
 * Created by Lucas on 2017-07-21.
 */

public class RegZombie extends Zombie
{

    public RegZombie(Integer id) {
        super(id);

        this.moveAnimation = "walk";

        this.detachableEntitiesToStayAlive.add("head");
        this.detachableEntitiesToStayAlive.add(new String[]{"left_arm", "right_arm"});
        this.detachableEntitiesToStayAlive.add("left_leg");
        this.detachableEntitiesToStayAlive.add("right_leg");


        this.currentParts.add("head");
        this.currentParts.add("left_arm");
        this.currentParts.add("torso");
        this.currentParts.add("right_arm");
        this.currentParts.add("left_leg");
        this.currentParts.add("right_leg");

        this.setSpeed(1);

    }

    @Override
    public void setup(int direction)
    {
        super.setup(direction);
        this.getState().setTimeScale(2f);
    }
}
