package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Config;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.ZombieSmash;
import com.fcfruit.zombiesmash.effects.BleedBlood;
import com.fcfruit.zombiesmash.physics.CollisionListener;
import com.fcfruit.zombiesmash.physics.ContactFilter;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.physics.PhysicsData;
import com.fcfruit.zombiesmash.stages.SettingsStage;
import com.fcfruit.zombiesmash.zombies.Zombie;

/**
 * Created by Lucas on 2017-07-21.
 */

//compile "com.underwaterapps.overlap2druntime:overlap2d-runtime-libgdx:0.1.0"

public class MainMenu implements Screen{

    com.fcfruit.zombiesmash.stages.MainMenuStage stage;

    Viewport viewport;
    OrthographicCamera tempGameCamera;
    OrthographicCamera tempPhysicsCamera;

    Music music;

    public boolean show_settings_stage = false;

    Stage settings;

    private Box2DDebugRenderer box2DDebugRenderer = new Box2DDebugRenderer();


    public MainMenu(){
        viewport = new StretchViewport(ZombieSmash.WIDTH, ZombieSmash.HEIGHT);

        tempGameCamera = new OrthographicCamera(ZombieSmash.WIDTH, ZombieSmash.HEIGHT);
        tempGameCamera.position.set(tempGameCamera.viewportWidth/2, tempGameCamera.viewportHeight/2, 0);
        tempGameCamera.update();

        tempPhysicsCamera = new OrthographicCamera(Physics.WIDTH, Physics.HEIGHT);
        tempPhysicsCamera.position.set(tempPhysicsCamera.viewportWidth/2, tempPhysicsCamera.viewportHeight/2, 0);
        tempPhysicsCamera.update();
        Environment.setupGame(tempGameCamera, tempPhysicsCamera);

        this.tempPhysicsContactFitlerSetup();

        stage = new com.fcfruit.zombiesmash.stages.MainMenuStage(viewport, "ui/main_menu/main_menu.json", "ui/main_menu/", true);

        music = Gdx.audio.newMusic(Gdx.files.internal("audio/theme_song.wav"));

        settings = new SettingsStage(new StretchViewport(ZombieSmash.WIDTH, ZombieSmash.HEIGHT), this);

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
                if(fixtureAData.containsInstanceOf(BleedBlood.class) || fixtureBData.containsInstanceOf(BleedBlood.class))
                {
                    return false;
                }
                // Ground
                else if (fixtureAData.getData().contains("ground", false)
                        || fixtureBData.getData().contains("ground", false))
                {
                    return true;
                }
                else
                {
                    return true;
                }
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        /*if(!stage.mute) {
            //music.play();
        }
        else{
            music.stop();
        }*/

        Gdx.input.setInputProcessor(stage);
        stage.getViewport().apply();
        stage.act();
        stage.draw();

        if(show_settings_stage){
            Gdx.input.setInputProcessor(settings);
            settings.getViewport().apply();
            settings.act();
            settings.draw();
        }

        tempGameCamera.update();
        tempPhysicsCamera.update();
        Environment.physics.update(delta);

        if(Config.DEBUG_PHYSICS)
            this.box2DDebugRenderer.render(Environment.physics.getWorld(), Environment.physicsCamera.combined);
    }

    @Override
    public void resize(int width, int height) {
        // use true here to center the camera
        // that's what you probably want in case of Screens
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
        settings.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
