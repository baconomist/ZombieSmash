package com.fcfruit.zombiesmash;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.screens.GameScreen;

/**
 * Created by Lucas on 2017-09-18.
 */

public class Environment {

    // Class to access all important game instances
    // Need to create static methods which catch exceptions which may arise
    // Or else if new instance creation goes wrong it crashes


    public static ZombieSmash game = new ZombieSmash();

    public static GameData gameData;

    public static Settings settings;

    public static GameScreen gameScreen;

    public static OrthographicCamera gameCamera;

    public static OrthographicCamera physicsCamera;

    public static Physics physics;

    }

