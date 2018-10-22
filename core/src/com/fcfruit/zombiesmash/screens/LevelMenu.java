package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.ZombieSmash;
import com.fcfruit.zombiesmash.stages.level_menu.ModeSelectStage;


/**
 * Created by Lucas on 2017-07-22.
 */

public class LevelMenu implements Screen
{

    private Viewport viewport;

    private Stage currentStage;
    private ModeSelectStage modeSelectStage;

    public LevelMenu()
    {
        this.viewport = new StretchViewport(ZombieSmash.WIDTH, ZombieSmash.HEIGHT);
        this.modeSelectStage = new ModeSelectStage(this.viewport);

        this.setCurrentStage(this.modeSelectStage);
    }

    public void setCurrentStage(Stage stage)
    {
        this.currentStage = stage;
        Gdx.input.setInputProcessor(this.currentStage);
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(this.currentStage);
    }

    @Override
    public void render(float delta)
    {
        Gdx.input.setInputProcessor(this.currentStage);
        this.currentStage.act();
        this.currentStage.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        // use true here to center the camera
        // that's what you probably want in case of Screens
        viewport.update(width, height, true);
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
