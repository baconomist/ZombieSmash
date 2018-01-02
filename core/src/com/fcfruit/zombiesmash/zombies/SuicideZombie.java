package com.fcfruit.zombiesmash.zombies;

import com.fcfruit.zombiesmash.zombies.Zombie;

public class SuicideZombie extends Zombie{

    public SuicideZombie(int id){
        super(id);

        this.moveAnimation = "run";

        partsToStayAlive.add("head");
        partsToStayAlive.add("torso");
        partsToStayAlive.add("left_arm");
        partsToStayAlive.add("right_arm");
        partsToStayAlive.add("left_leg");
        partsToStayAlive.add("right_leg");

        type = "reg";

        animationSetup();

    }

    @Override
    void onGetUp(){
        this.currentAnimation = "run";
    }

    @Override
    void crawl(){
        super.crawl();
        this.physicsEnabled = false;
        this.currentAnimation = "crawl";

        setPosition(parts.get("torso").physicsBody.getPosition().x, 0);
    }

    @Override
    void attack() {
        super.attack();

        this.currentAnimation = "attack1";

    }

    @Override
    void onDeath(){}

}