package com.fcfruit.zombiesmash.dev.tests.level_test;

import com.badlogic.gdx.Gdx;

public class ZombieSmash extends com.fcfruit.zombiesmash.release.ZombieSmash
{

    //Logger logger = new Logger()

    @Override
    public void create()
    {
        super.create();
        Environment.create();
        Environment.setupGame(2);
        this.setScreen(Environment.screens.gamescreen);
    }

}
