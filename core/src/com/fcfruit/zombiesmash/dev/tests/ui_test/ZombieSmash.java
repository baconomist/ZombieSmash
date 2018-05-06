package com.fcfruit.zombiesmash.dev.tests.ui_test;

public class ZombieSmash extends com.fcfruit.zombiesmash.release.ZombieSmash
{

    @Override
    public void create()
    {
        super.create();
        this.setScreen(Environment.screens.mainmenu);
    }

}
