package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Config;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.ZombieSmash;
import com.fcfruit.zombiesmash.effects.BleedBlood;
import com.fcfruit.zombiesmash.physics.ContactFilter;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.physics.PhysicsData;
import com.fcfruit.zombiesmash.stages.InAppPurchasesStage;
import com.fcfruit.zombiesmash.stages.MainMenuStage;
import com.fcfruit.zombiesmash.stages.OptionsStage;
import com.fcfruit.zombiesmash.zombies.Zombie;

/**
 * Created by Lucas on 2017-07-21.
 */

//compile "com.underwaterapps.overlap2druntime:overlap2d-runtime-libgdx:0.1.0"

public class MainMenu implements Screen
{

    private MainMenuStage stage;

    private Viewport viewport;
    private OrthographicCamera tempGameCamera;
    private OrthographicCamera tempPhysicsCamera;

    private Music music;

    private Stage options;
    private boolean show_options_stage = false;

    private InAppPurchasesStage inAppPurchasesStage;
    private boolean show_in_app_purchases_stage = false;

    private Box2DDebugRenderer box2DDebugRenderer = new Box2DDebugRenderer();


    public MainMenu()
    {
        viewport = new StretchViewport(ZombieSmash.WIDTH, ZombieSmash.HEIGHT);

        tempGameCamera = new OrthographicCamera(ZombieSmash.WIDTH, ZombieSmash.HEIGHT);
        tempGameCamera.position.set(tempGameCamera.viewportWidth / 2, tempGameCamera.viewportHeight / 2, 0);
        tempGameCamera.update();

        tempPhysicsCamera = new OrthographicCamera(Physics.WIDTH, Physics.HEIGHT);
        tempPhysicsCamera.position.set(tempPhysicsCamera.viewportWidth / 2, tempPhysicsCamera.viewportHeight / 2, 0);
        tempPhysicsCamera.update();
        Environment.setupGame(tempGameCamera, tempPhysicsCamera);

        this.tempPhysicsContactFitlerSetup();

        stage = new MainMenuStage(viewport, "ui/main_menu/main_menu.json", "ui/main_menu/", true);

        music = Gdx.audio.newMusic(Gdx.files.internal("audio/theme_song.wav"));

        options = new OptionsStage(viewport)
        {
            @Override
            public void onBackButton()
            {
                hideOptionsStage();
            }
        };

        inAppPurchasesStage = new InAppPurchasesStage(viewport);

    }

    private void tempPhysicsContactFitlerSetup()
    {
        Environment.physics.getWorld().setContactFilter(new ContactFilter()
        {
            PhysicsData fixtureAData;
            PhysicsData fixtureBData;

            PhysicsData[] fixtureData = new PhysicsData[2];

            @Override
            public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB)
            {
                fixtureAData = ((PhysicsData) fixtureA.getUserData());
                fixtureBData = ((PhysicsData) fixtureB.getUserData());

                fixtureData[0] = fixtureAData;
                fixtureData[1] = fixtureBData;

                if (fixtureAData.containsInstanceOf(Zombie.class) && fixtureBData.containsInstanceOf(Zombie.class))
                {

                    if (((Zombie) fixtureAData.getClassInstance(Zombie.class)).id == ((Zombie) fixtureBData.getClassInstance(Zombie.class)).id)
                    {

                        if ((fixtureA.getFilterData().maskBits & fixtureB.getFilterData().categoryBits) != 0 || (fixtureB.getFilterData().maskBits & fixtureA.getFilterData().categoryBits) != 0)
                        {
                            return true;
                        } else
                        {
                            return false;
                        }

                    } else
                    {
                        return false;
                    }

                }
                // Blood
                if (fixtureAData.containsInstanceOf(BleedBlood.class) || fixtureBData.containsInstanceOf(BleedBlood.class))
                {
                    return false;
                }
                // Ground
                else if (fixtureAData.getData().contains("ground", false)
                        || fixtureBData.getData().contains("ground", false))
                {
                    return true;
                } else
                {
                    return true;
                }
            }
        });
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta)
    {
        if (Environment.settings.isMusicEnabled())
        {
            music.play();
            music.setVolume(Environment.settings.getMusicVolume());
        } else if(music.isPlaying())
        {
            music.stop();
        }

        stage.getViewport().apply();
        stage.act();
        stage.draw();

        if (show_options_stage)
        {
            options.getViewport().apply();
            options.act();
            options.draw();
        }
        
        if(show_in_app_purchases_stage)
        {
            inAppPurchasesStage.getViewport().apply();
            inAppPurchasesStage.act();
            inAppPurchasesStage.draw();
        }

        tempGameCamera.update();
        tempPhysicsCamera.update();
        Environment.physics.update(delta);

        if (Config.DEBUG_PHYSICS)
            this.box2DDebugRenderer.render(Environment.physics.getWorld(), Environment.physicsCamera.combined);
    }

    public void showOptionsStage()
    {
        this.show_options_stage = true;
        Gdx.input.setInputProcessor(options);
    }

    public void hideOptionsStage()
    {
        this.show_options_stage = false;
        Gdx.input.setInputProcessor(stage);
    }

    public void showInAppPurchasesStage()
    {
        this.show_in_app_purchases_stage = true;
        Gdx.input.setInputProcessor(inAppPurchasesStage);
    }

    public void hideInAppPurchasesStage()
    {
        this.show_in_app_purchases_stage = false;
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height)
    {
        // use true here to center the camera
        // that's what you probably want in case of Screens
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
        options.getViewport().update(width, height, true);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {

    }

}
