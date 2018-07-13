package com.fcfruit.zombiesmash.zombies;

/**
 * Created by Lucas on 2017-07-21.
 */

public class RegularZombie extends Zombie
{

    public RegularZombie(Integer id) {
        super(id);

        this.moveAnimation = "run";

        this.detachableEntitiesToStayAlive.add("head");
        this.detachableEntitiesToStayAlive.add(new String[]{"left_arm", "right_arm"});


        this.currentParts.add("head");
        this.currentParts.add("left_arm");
        this.currentParts.add("torso");
        this.currentParts.add("right_arm");
        this.currentParts.add("left_leg");
        this.currentParts.add("right_leg");

    }

}
