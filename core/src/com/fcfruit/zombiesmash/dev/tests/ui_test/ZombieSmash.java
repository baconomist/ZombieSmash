package com.fcfruit.zombiesmash.dev.tests.ui_test;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;


public class ZombieSmash extends com.fcfruit.zombiesmash.release.ZombieSmash
{

    @Override
    public void create()
    {
        /*
		called after all game default game functions
		are initialized
		 */

        Environment.create();

        if (Gdx.app.getType() == Application.ApplicationType.Android)
        {
            Gdx.input.setCatchBackKey(true);
        }

        this.setScreen(Environment.screens.mainmenu);
    }

}
