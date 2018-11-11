package com.fcfruit.monstersmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.monstersmash.MonsterSmash;
import com.fcfruit.monstersmash.stages.level_menu.LevelSelectStage;
import com.fcfruit.monstersmash.stages.level_menu.ModeSelectStage;
import com.fcfruit.monstersmash.stages.level_menu.SeasonSelectStage;


/**
 * Created by Lucas on 2017-07-22.
 */

public class LevelMenu implements Screen
{

    private Viewport viewport;

    private Stage currentStage;
    private ModeSelectStage modeSelectStage;
    private SeasonSelectStage seasonSelectStage;
    private LevelSelectStage levelSelectStage;

    public LevelMenu()
    {
        this.viewport = new StretchViewport(MonsterSmash.WIDTH, MonsterSmash.HEIGHT);

        this.modeSelectStage = new ModeSelectStage(this.viewport);
        this.seasonSelectStage = new SeasonSelectStage(this.viewport);
        this.levelSelectStage = new LevelSelectStage(this.viewport);

        this.setCurrentStage(this.seasonSelectStage);
    }

    public void showModeSelect()
    {
        this.setCurrentStage(this.modeSelectStage);
    }

    public void showSeasonSelect()
    {
        this.setCurrentStage(this.seasonSelectStage);
    }

    public void showLevelSelect()
    {
        this.setCurrentStage(this.levelSelectStage);
    }

    private void setCurrentStage(Stage stage)
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
