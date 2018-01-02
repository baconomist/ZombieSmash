package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.physics.box2d.World;
import com.fcfruit.zombiesmash.zombies.Zombie;

public class SuicideZombie extends Zombie{

    public SuicideZombie(Integer id){
        super(id);

        this.moveAnimation = "run";

        partsToStayAlive.add("head");
        partsToStayAlive.add("torso");
        partsToStayAlive.add("left_arm");
        partsToStayAlive.add("right_arm");
        partsToStayAlive.add("left_leg");
        partsToStayAlive.add("right_leg");

        type = "suicide";

        animationSetup();

    }

    @Override
    public void constructPhysicsBody(World world, boolean flip){

        super.constructPhysicsBody(world, flip);

        parts.get("grenade").isDetachable = false;

    }

    @Override
    void onGetUp(){
        this.currentAnimation = "run";
    }

    @Override
    void attack() {
        super.attack();

        this.currentAnimation = "attack1";

    }

    @Override
    void onDeath(){}

}