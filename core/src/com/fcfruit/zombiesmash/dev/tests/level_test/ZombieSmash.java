package com.fcfruit.zombiesmash.dev.tests.level_test;

public class ZombieSmash extends com.fcfruit.zombiesmash.release.ZombieSmash
{

    //Logger logger = new Logger()

    @Override
    public void create()
    {
        super.create();
        Environment.create();
        this.setScreen(Environment.screens.gamescreen);
    }

}
