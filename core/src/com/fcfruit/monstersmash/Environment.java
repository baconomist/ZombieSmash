package com.fcfruit.monstersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.pay.PurchaseManager;
import com.badlogic.gdx.physics.box2d.Joint;
import com.fcfruit.monstersmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.ExplodableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InputCaptureEntityInterface;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-09-18.
 */

public class Environment
{

    // Class to access all important game instances
    // Need to create static methods which catch exceptions which may arise
    // Or else if new instance creation goes wrong it crashes

    public static MonsterSmash game;

    public static PurchaseManager purchaseManager;

    public static MusicManager musicManager;

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

        assets.load("maps/night_map/night_map.atlas", TextureAtlas.class);
        assets.load("effects/clouds/clouds.atlas", TextureAtlas.class);

        assets.load("zombies/grandma_zombie/grandma_zombie.atlas", TextureAtlas.class);
        assets.load("zombies/big_zombie/big_zombie.atlas", TextureAtlas.class);
        assets.load("zombies/police_zombie/police_zombie.atlas", TextureAtlas.class);
        assets.load("zombies/suicide_zombie/suicide_zombie.atlas", TextureAtlas.class);
        assets.load("zombies/reg_zombie/reg_zombie.atlas", TextureAtlas.class);
        assets.load("zombies/armored_zombie/armored_zombie.atlas", TextureAtlas.class);
        assets.load("zombies/crawling_zombie/crawling_zombie.atlas", TextureAtlas.class);
        assets.load("zombies/bone_boss_zombie/bone_boss_zombie.atlas", TextureAtlas.class);

        assets.load("zombies/bone_boss_zombie/theme.mp3", Music.class);

        assets.load("effects/blood/flowing_blood/flowing_blood.atlas", TextureAtlas.class);
        assets.load("effects/blood/ground_blood/ground_blood.atlas", TextureAtlas.class);

        for (int i = 1; i < 4; i++)
        {
            assets.load("brains/brain" + i + ".png", Texture.class);
        }
        assets.load("brains/brain_crate.atlas", TextureAtlas.class);

        assets.load("powerups/rocket/rocket.png", Texture.class);
        assets.load("powerups/rock/rock.png", Texture.class);

        assets.load("effects/helicopter/helicopter.atlas", TextureAtlas.class);
        assets.load("powerups/box.atlas", TextureAtlas.class);

        assets.load("effects/smoke/smoke.atlas", TextureAtlas.class);
        assets.load("effects/explosion/explosion.atlas", TextureAtlas.class);
        assets.load("effects/fire/fire.atlas", TextureAtlas.class);

        assets.load("ui/game_ui/message_box/message_guy/message_guy.atlas", TextureAtlas.class);
    }

    public static String currentDifficulty = "normal";
    public static HashMap<String, Float> difficulty_multipliers;
    static
    {
        difficulty_multipliers = new HashMap<String, Float>();
        difficulty_multipliers.put("easy", 0.75f);
        difficulty_multipliers.put("normal", 1f);
        difficulty_multipliers.put("hard", 1.5f);
    }

    public static com.fcfruit.monstersmash.level.Level level;

    public static GameData gameData;

    public static Settings settings;

    public static OrthographicCamera gameCamera;

    public static com.fcfruit.monstersmash.powerups.PowerupManager powerupManager;

    public static OrthographicCamera physicsCamera;

    public static com.fcfruit.monstersmash.physics.Physics physics;

    // Pools
    public static com.fcfruit.monstersmash.powerups.FirePool firePool;
    public static com.fcfruit.monstersmash.effects.BleedableBloodPool bleedableBloodPool;
    public static com.fcfruit.monstersmash.powerups.ParticleEntityPool particleEntityPool;
    public static com.fcfruit.monstersmash.brains.BrainPool brainPool;
    public static com.fcfruit.monstersmash.powerups.rocket.RocketPool rocketPool;

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

    public static ArrayList<com.fcfruit.monstersmash.entity.interfaces.UpdatableEntityInterface> updatableAddQueue = new ArrayList<com.fcfruit.monstersmash.entity.interfaces.UpdatableEntityInterface>();
    public static ArrayList<com.fcfruit.monstersmash.entity.interfaces.UpdatableEntityInterface> updatableRemoveQueue = new ArrayList<com.fcfruit.monstersmash.entity.interfaces.UpdatableEntityInterface>();

    public static ArrayList<com.fcfruit.monstersmash.entity.interfaces.event.LevelEventListener> levelEventListenerAddQueue = new ArrayList<com.fcfruit.monstersmash.entity.interfaces.event.LevelEventListener>();
    public static ArrayList<com.fcfruit.monstersmash.entity.interfaces.event.LevelEventListener> levelEventListenerRemoveQueue = new ArrayList<com.fcfruit.monstersmash.entity.interfaces.event.LevelEventListener>();

    public static void update()
    {
        musicManager.update();
    }

    public static void setupGame(int levelid)
    {
        Environment.isPaused = false;

        load_assets();

        setupPhysicsCamera();
        setupGameCamera();

        physics = new com.fcfruit.monstersmash.physics.Physics();
        screens.gamescreen.create();
        powerupManager = new com.fcfruit.monstersmash.powerups.PowerupManager();

        // Pools
        firePool = new com.fcfruit.monstersmash.powerups.FirePool();
        if(Environment.settings.isGoreEnabled())
            bleedableBloodPool = new com.fcfruit.monstersmash.effects.BleedableBloodPool();
        particleEntityPool = new com.fcfruit.monstersmash.powerups.ParticleEntityPool();
        brainPool = new com.fcfruit.monstersmash.brains.BrainPool();
        rocketPool = new com.fcfruit.monstersmash.powerups.rocket.RocketPool();

        setupLevel(levelid);
    }

    public static void setupGame(OrthographicCamera gameCam, OrthographicCamera physicsCam)
    {
        load_assets();

        gameCamera = gameCam;
        physicsCamera = physicsCam;

        physics = new com.fcfruit.monstersmash.physics.Physics();

        // Pools
        bleedableBloodPool = new com.fcfruit.monstersmash.effects.BleedableBloodPool();
    }

    private static void setupGameCamera()
    {
        gameCamera = new OrthographicCamera(MonsterSmash.WIDTH, MonsterSmash.HEIGHT);
        gameCamera.position.set(Environment.gameCamera.viewportWidth / 2, Environment.gameCamera.viewportHeight / 2, 0);

        gameCamera.update();
    }

    private static void setupPhysicsCamera()
    {
        physicsCamera = new OrthographicCamera(com.fcfruit.monstersmash.physics.Physics.WIDTH, com.fcfruit.monstersmash.physics.Physics.HEIGHT);
        // Camera position/origin is in the middle
        // Not bottom left
        // see see https://github.com/libgdx/libgdx/wiki/Coordinate-systems
        // Also cam.project(worldpos) is x and y from bottom left corner
        // But cam.unproject(screenpos) is x and y from top left corner
        physicsCamera.position.set(Environment.physicsCamera.viewportWidth / 2, Environment.physicsCamera.viewportHeight / 2, 0);

        physicsCamera.update();
    }

    private static void setupLevel(int levelid)
    {
        level = new com.fcfruit.monstersmash.level.NightLevel(levelid);
        level.create();
    }


    public static void onResize()
    {
        // If gamestate == game running.....
        try
        {
            gameCamera.position.x = Environment.physicsCamera.position.x * com.fcfruit.monstersmash.physics.Physics.PIXELS_PER_METER;
            gameCamera.update();
            physics.constructPhysicsBoundaries();
        } catch (Exception e)
        {
        }
    }


    public static class Prefs
    {
        public static Preferences settings;
        public static Preferences upgrades;
        public static Preferences brains;

        public static void clear()
        {
            settings.clear();
            upgrades.clear();
            brains.clear();
        }
    }

    public static void create()
    {
        Prefs.upgrades = Gdx.app.getPreferences("upgrades");
        Prefs.brains = Gdx.app.getPreferences("brains");
        Prefs.settings = Gdx.app.getPreferences("settings");

        musicManager = new MusicManager();

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

