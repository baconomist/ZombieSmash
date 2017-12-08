package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.fcfruit.zombiesmash.level.Level;
import com.fcfruit.zombiesmash.level.Objective;
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

    public static AssetManager assets = new AssetManager();
    static{
        assets.load("maps/night_map/night_map.png", Texture.class);
        //assets.load("maps/night_map/night_map_rube.json", Json.class);
        
        assets.load("zombies/big_zombie/big_zombie.atlas", TextureAtlas.class);
        //assets.load("zombies/big_zombie/big_zombie.json", Json.class);
        assets.load("zombies/big_zombie/big_zombie.png", Texture.class);
        //assets.load("zombies/big_zombie/big_zombie_rube.json", Json.class);

        assets.load("zombies/girl_zombie/girl_zombie.atlas", TextureAtlas.class);
        //assets.load("zombies/girl_zombie/girl_zombie.json", Json.class);
        assets.load("zombies/girl_zombie/girl_zombie.png", Texture.class);
        //assets.load("zombies/girl_zombie/girl_zombie_rube.json", Json.class);

        assets.load("zombies/police_zombie/police_zombie.atlas", TextureAtlas.class);
        //assets.load("zombies/police_zombie/police_zombie.json", Json.class);
        assets.load("zombies/police_zombie/police_zombie.png", Texture.class);
        //assets.load("zombies/police_zombie/police_zombie_rube.json", Json.class);

        assets.load("zombies/reg_zombie/reg_zombie.atlas", TextureAtlas.class);
        //assets.load("zombies/reg_zombie/reg_zombie.json", Json.class);
        assets.load("zombies/reg_zombie/reg_zombie.png", Texture.class);
        //assets.load("zombies/reg_zombie/reg_zombie_rube.json", Json.class);

        
    }

    public static Level level;

    public static GameData gameData;

    public static Settings settings;

    public static GameScreen gameScreen;

    public static OrthographicCamera gameCamera;

    public static OrthographicCamera physicsCamera;

    public static Physics physics;

    }

