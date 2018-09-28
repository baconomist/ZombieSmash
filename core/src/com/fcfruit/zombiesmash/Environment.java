package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.Joint;
import com.fcfruit.zombiesmash.brains.BrainPool;
import com.fcfruit.zombiesmash.effects.BleedableBloodPool;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.ExplodableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.UpdatableEntityInterface;
import com.fcfruit.zombiesmash.level.Level;
import com.fcfruit.zombiesmash.level.NightLevel;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.powerups.ParticleEntityPool;
import com.fcfruit.zombiesmash.powerups.PowerupManager;
import com.fcfruit.zombiesmash.powerups.rocket.RocketPool;

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

    public static boolean isPaused = false;

    public static Screens screens;

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

        assets.load("maps/night_map/night_map.jpg", Texture.class);
        assets.load("effects/clouds/clouds.atlas", TextureAtlas.class);

        assets.load("zombies/big_zombie/big_zombie.atlas", TextureAtlas.class);
        assets.load("zombies/police_zombie/police_zombie.atlas", TextureAtlas.class);
        assets.load("zombies/suicide_zombie/suicide_zombie.atlas", TextureAtlas.class);
        assets.load("zombies/reg_zombie/reg_zombie.atlas", TextureAtlas.class);
        assets.load("zombies/armored_zombie/armored_zombie.atlas", TextureAtlas.class);
        assets.load("zombies/crawling_zombie/crawling_zombie.atlas", TextureAtlas.class);


        assets.load("effects/blood/flowing_blood/flowing_blood.atlas", TextureAtlas.class);
        assets.load("effects/blood/ground_blood/ground_blood.atlas", TextureAtlas.class);

        for(int i = 1; i < 4; i++)
        {
            assets.load("brains/brain"+i+".png", Texture.class);
        }
        assets.load("brains/brain_crate.atlas", TextureAtlas.class);

        assets.load("powerups/rocket/rocket.png", Texture.class);
        assets.load("powerups/rock/rock.png", Texture.class);

        assets.load("effects/helicopter/helicopter.atlas", TextureAtlas.class);
        assets.load("powerups/box.atlas", TextureAtlas.class);

        assets.load("effects/smoke/smoke.atlas", TextureAtlas.class);
        assets.load("effects/explosion/explosion.atlas", TextureAtlas.class);

    }

    public static Level level;

    public static GameData gameData;

    public static Settings settings;

    public static OrthographicCamera gameCamera;

    public static PowerupManager powerupManager;

    public static OrthographicCamera physicsCamera;

    public static Physics physics;

    // Pools
    public static BleedableBloodPool bleedableBloodPool;
    public static ParticleEntityPool particleEntityPool;
    public static BrainPool brainPool;
    public static RocketPool rocketPool;

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
    public static ArrayList<DrawableEntityInterface> drawableBackgroundAddQueue = new ArrayList<DrawableEntityInterface>();

    public static ArrayList<UpdatableEntityInterface> updatableAddQueue = new ArrayList<UpdatableEntityInterface>();
    public static ArrayList<UpdatableEntityInterface> updatableRemoveQueue = new ArrayList<UpdatableEntityInterface>();

    public static void setupGame(int levelid){

        load_assets();

        setupPhysicsCamera();
        setupGameCamera();

        physics = new Physics();
        screens.gamescreen.create();
        powerupManager = new PowerupManager();

        // Pools
        bleedableBloodPool = new BleedableBloodPool();
        particleEntityPool = new ParticleEntityPool();
        brainPool = new BrainPool();
        rocketPool = new RocketPool();

        setupLevel(levelid);
    }

    private static void setupGameCamera()
    {
        gameCamera = new OrthographicCamera(ZombieSmash.WIDTH, ZombieSmash.HEIGHT);
        gameCamera.position.set(Environment.gameCamera.viewportWidth/2, Environment.gameCamera.viewportHeight/2, 0);

        gameCamera.update();

    }

    private static void setupPhysicsCamera()
    {
        physicsCamera = new OrthographicCamera(Physics.WIDTH, Physics.HEIGHT);
        // Camera position/origin is in the middle
        // Not bottom left
        // see see https://github.com/libgdx/libgdx/wiki/Coordinate-systems
        // Also cam.project(worldpos) is x and y from bottom left corner
        // But cam.unproject(screenpos) is x and y from top left corner
        physicsCamera.position.set(Environment.physicsCamera.viewportWidth/2, Environment.physicsCamera.viewportHeight/2, 0);

        physicsCamera.update();

    }

    private static void setupLevel(int levelid)
    {
        level = new NightLevel(levelid);
        level.create();
    }


    public static void onResize()
    {
        // If gamestate == game running.....
        try
        {
            gameCamera.position.x = Environment.physicsCamera.position.x * Physics.PIXELS_PER_METER;
            gameCamera.update();
            physics.constructPhysicsBoundaries();
        }
        catch (Exception e){}
    }



    public static void create()
    {
        gameData = new GameData();
        settings = new Settings();

        screens = new Screens();
    }

    public static void load_assets()
    {
        // Load all assets
        assets.finishLoading();
    }


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
        return Intersector.overlapConvexPolygons(polygon1, polygon2);
        /*return (
            (polygon2.getX() < (polygon1.getX() + polygon1.getVertices()[2]) && polygon2.getX() > polygon1.getX())
                    && (polygon2.getY() < (polygon1.getY() + polygon1.getVertices()[5]) && polygon2.getY() > polygon1.getY()))
            ||
            ((polygon1.getX() < (polygon2.getX() + polygon1.getVertices()[2]) && polygon1.getX() > polygon2.getX())
                    && (polygon1.getY() < (polygon2.getY() + polygon1.getVertices()[5]) && polygon1.getY() > polygon2.getY())
            );*/
    }

}

