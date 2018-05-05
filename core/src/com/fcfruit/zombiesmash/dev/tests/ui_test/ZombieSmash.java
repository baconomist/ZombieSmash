package com.fcfruit.zombiesmash.dev.tests.ui_test;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;


public class ZombieSmash extends com.fcfruit.zombiesmash.release.ZombieSmash
{

    @Override
    public void create()
    {
        super.create();
        Environment.create();
        this.setScreen(Environment.screens.mainmenu);
    }

}
