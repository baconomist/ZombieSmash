package com.fcfruit.zombiesmash.dev.tests.level_test;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.fcfruit.zombiesmash.release.Environment;
import com.fcfruit.zombiesmash.release.GameData;
import com.fcfruit.zombiesmash.release.Settings;
import com.fcfruit.zombiesmash.release.level.NightLevel;
import com.fcfruit.zombiesmash.release.physics.Physics;
import com.fcfruit.zombiesmash.release.powerups.PowerupManager;
import com.fcfruit.zombiesmash.release.screens.GameScreen;
import com.fcfruit.zombiesmash.release.screens.MainMenu;


public class ZombieSmash extends com.fcfruit.zombiesmash.release.ZombieSmash
{

    @Override
    public void create()
    {
        /*
		called after all game default game functions
		are initialized
		 */
        Environment.gameData = new GameData();
        Environment.settings = new Settings();

        // Load all assets
        Environment.assets.finishLoading();

        if (Gdx.app.getType() == Application.ApplicationType.Android)
        {
            Gdx.input.setCatchBackKey(true);
        }

        this.setupLevelTest();
        this.setScreen(Environment.gameScreen);
    }

    void setupLevelTest()
    {

            Environment.gameCamera = new OrthographicCamera(com.fcfruit.zombiesmash.release.ZombieSmash.WIDTH, com.fcfruit.zombiesmash.release.ZombieSmash.HEIGHT);
            Environment.gameCamera.position.set(Environment.gameCamera.viewportWidth/2, Environment.gameCamera.viewportHeight/2, 0);
            Environment.gameCamera.update();

            Environment.physicsCamera = new OrthographicCamera(Physics.WIDTH, Physics.HEIGHT);
            // Camera position/origin is in the middle
            // Not bottom left
            // see see https://github.com/libgdx/libgdx/wiki/Coordinate-systems
            // Also cam.project(worldpos) is x and y from bottom left corner
            // But cam.unproject(screenpos) is x and y from top left corner
            Environment.physicsCamera.position.set(Environment.physicsCamera.viewportWidth/2, Environment.physicsCamera.viewportHeight/2, 0);
            Environment.physicsCamera.update();

            Environment.physics = new Physics();

            Environment.level = new NightLevel(2);
            Environment.level.create();

            Environment.powerupManager = new PowerupManager();
            Environment.level.addUpdatableEntity(Environment.powerupManager);
            Environment.level.addEventListener(Environment.powerupManager);

            Environment.gameScreen = new GameScreen();

            Environment.gameCamera.position.x = Environment.physicsCamera.position.x * Physics.PIXELS_PER_METER;
            Environment.gameCamera.update();
            Environment.physics.constructPhysicsBoundaries();

    }

}
