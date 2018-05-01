package com.fcfruit.zombiesmash.release;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.Joint;
import com.fcfruit.zombiesmash.release.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.release.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.release.entity.interfaces.ExplodableEntityInterface;
import com.fcfruit.zombiesmash.release.entity.interfaces.InputCaptureEntityInterface;
import com.fcfruit.zombiesmash.release.entity.interfaces.UpdatableEntityInterface;
import com.fcfruit.zombiesmash.release.level.Level;
import com.fcfruit.zombiesmash.release.physics.Physics;
import com.fcfruit.zombiesmash.release.powerups.PowerupManager;
import com.fcfruit.zombiesmash.release.screens.GameScreen;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-09-18.
 */

public class Environment
{

    // Class to access all important game instances
    // Need to create static methods which catch exceptions which may arise
    // Or else if new instance creation goes wrong it crashes


    public static ZombieSmash game = new ZombieSmash();

    public static AssetManager assets = new AssetManager();

    static
    {
        //assets.load("maps/night_map/night_map.png", Texture.class);
        //assets.load("maps/night_map/night_map_rube.json", Json.class);

        //assets.load("zombies/big_zombie/big_zombie.atlas", TextureAtlas.class);
        //assets.load("zombies/big_zombie/big_zombie.json", Json.class);
        //assets.load("zombies/big_zombie/big_zombie.png", Texture.class);
        //assets.load("zombies/big_zombie/big_zombie_rube.json", Json.class);

        //assets.load("zombies/girl_zombie/girl_zombie.atlas", TextureAtlas.class);
        //assets.load("zombies/girl_zombie/girl_zombie.json", Json.class);
        //assets.load("zombies/girl_zombie/girl_zombie.png", Texture.class);
        //assets.load("zombies/girl_zombie/girl_zombie_rube.json", Json.class);

        //assets.load("zombies/police_zombie/police_zombie.atlas", TextureAtlas.class);
        //assets.load("zombies/police_zombie/police_zombie.json", Json.class);
        //assets.load("zombies/police_zombie/police_zombie.png", Texture.class);
        //assets.load("zombies/police_zombie/police_zombie_rube.json", Json.class);

        //assets.load("zombies/reg_zombie/reg_zombie.atlas", TextureAtlas.class);
        //assets.load("zombies/reg_zombie/reg_zombie.json", Json.class);
        //assets.load("zombies/reg_zombie/reg_zombie.png", Texture.class);
        //assets.load("zombies/reg_zombie/reg_zombie_rube.json", Json.class);

        assets.load("effects/blood/blood.png", Texture.class);

    }

    public static Level level;

    public static GameData gameData;

    public static Settings settings;

    public static GameScreen gameScreen;

    public static OrthographicCamera gameCamera;

    public static PowerupManager powerupManager;

    public static OrthographicCamera physicsCamera;

    public static Physics physics;

    // Add items touched down on touch_down to this list
    // Clear this at the beginning of touch_down
    // Used so interactive items can decide to activate if only they have been touched,
    // Not 3 things at once being touched
    public static ArrayList<InputCaptureEntityInterface> touchedDownItems = new ArrayList<InputCaptureEntityInterface>();

    public static ArrayList<DetachableEntityInterface> detachableEntityDetachQueue = new ArrayList<DetachableEntityInterface>();

    public static ArrayList<Joint> jointDestroyQueue = new ArrayList<Joint>();

    public static ArrayList<ExplodableEntityInterface> explodableEntityQueue = new ArrayList<ExplodableEntityInterface>();

    public static ArrayList<DrawableEntityInterface> drawableRemoveQueue = new ArrayList<DrawableEntityInterface>();

    public static ArrayList<DrawableEntityInterface> drawableAddQueue = new ArrayList<DrawableEntityInterface>();

    public static ArrayList<UpdatableEntityInterface> updatableAddQueue = new ArrayList<UpdatableEntityInterface>();

    public static boolean isMethodInStack(String method_name)
    {
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace())
        {
            if (stackTraceElement.getMethodName().equals(method_name))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean areQuadrilaterallsColliding(Polygon polygon1, Polygon polygon2)
    {
        return (
                (polygon2.getX() < (polygon1.getX() + polygon1.getVertices()[2]) && polygon2.getX() > polygon1.getX())
                        && (polygon2.getY() < (polygon1.getY() + polygon1.getVertices()[5]) && polygon2.getY() > polygon1.getY()))
                ||
                ((polygon1.getX() < (polygon2.getX() + polygon1.getVertices()[2]) && polygon1.getX() > polygon2.getX())
                        && (polygon1.getY() < (polygon2.getY() + polygon1.getVertices()[5]) && polygon1.getY() > polygon2.getY())
                );
    }

    // Can't do this as a static method because camera position setting and update doesnt work.
    // Try creating a game manager class which has an instance and having a method like this in there
    /*public static void onLevelSelect(int lvl)
    {
        gameCamera = new OrthographicCamera(ZombieSmash.WIDTH, ZombieSmash.HEIGHT);
        gameCamera.position.set(gameCamera.viewportWidth/2, gameCamera.viewportHeight/2, 0);
        gameCamera.update();

        physicsCamera = new OrthographicCamera(Physics.WIDTH, Physics.HEIGHT);
        // Camera position/origin is in the middle
        // Not bottom left
        // see see https://github.com/libgdx/libgdx/wiki/Coordinate-systems
        // Also cam.project(worldpos) is x and y from bottom left corner
        // But cam.unproject(screenpos) is x and y from top left corner
        physicsCamera.position.set(physicsCamera.viewportWidth/2, physicsCamera.viewportHeight/2, 0);
        physicsCamera.update();

        physics = new Physics();

        level = new NightLevel(lvl);
        level.create();

        powerupManager = new PowerupManager();
        level.addUpdatableEntity(powerupManager);
        level.addEventListener(powerupManager);
        
        gameScreen = new GameScreen();

        gameCamera.position.x = physicsCamera.position.x * Physics.PIXELS_PER_METER;
        gameCamera.update();
        physics.constructPhysicsBoundaries();

        game.setScreen(gameScreen);
    }*/

}

