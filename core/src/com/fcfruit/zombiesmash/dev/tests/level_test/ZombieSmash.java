package com.fcfruit.zombiesmash.dev.tests.level_test;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.fcfruit.zombiesmash.release.GameData;
import com.fcfruit.zombiesmash.release.Settings;


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
