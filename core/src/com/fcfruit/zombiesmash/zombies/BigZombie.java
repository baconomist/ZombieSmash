package com.fcfruit.zombiesmash.zombies;

/**
 * Created by Lucas on 2017-11-21.
 */

public class BigZombie extends NewZombie
{

    public BigZombie(Integer id)
    {
        super(id);

        this.moveAnimation = "walk";

        this.detachableEntitiesToStayAlive.add("head");
        this.detachableEntitiesToStayAlive.add("left_arm");
        this.detachableEntitiesToStayAlive.add("right_arm");
        this.detachableEntitiesToStayAlive.add("left_leg");
        this.detachableEntitiesToStayAlive.add("right_leg");

    }

}



