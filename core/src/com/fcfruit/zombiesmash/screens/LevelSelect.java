package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.ZombieSmash;
import com.fcfruit.zombiesmash.stages.level_select.Page1;


/**
 * Created by Lucas on 2017-07-22.
 */

public class LevelSelect implements Screen
{

    private Viewport viewport;

    private Stage currentStage;
    private Page1 page1;

    public LevelSelect()
    {
        this.viewport = new StretchViewport(ZombieSmash.WIDTH, ZombieSmash.HEIGHT);
        this.page1 = new Page1(this.viewport);

        this.currentStage = this.page1;

        Gdx.input.setInputProcessor(this.currentStage);
    }

    public void showPage2()
    {

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
