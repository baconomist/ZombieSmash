package com.fcfruit.monstersmash.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.screens.MainMenu;

public class GameMenuStage extends RubeStage
{
    /*
    * Fix options stage/gameui stage size,
    * since gameuistage is 1/2 the size of MonsterSmash.WIDTH, the volume bars only have 5 boxes instead of 10
    * */

    private OptionsStage optionsStage;
    private boolean show_options_menu = false;

    public GameMenuStage(Viewport viewport)
    {
        super(viewport, "ui/game_ui/game_menu/game_menu.json", "ui/game_ui/game_menu/", false);

        this.optionsStage = new OptionsStage(viewport)
        {
            @Override
            public void onBackButton()
            {
                hideOptionsMenu();
            }
        };

        this.findActor("resume_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Environment.screens.gamescreen.get_ui_stage().hideGameMenu();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.findActor("options_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                showOptionsMenu();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.findActor("main_menu_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                /*
                * Environment should have a destroy/dispose method!!!!! with System.gc();
                * */
                Environment.isPaused = false;
                Environment.level = null;
                Environment.physics = null;
                System.gc();
                Environment.create();
                Environment.game.setScreen(Environment.screens.mainmenu);
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

    @Override
    public void draw()
    {
        super.draw();

        if(this.show_options_menu)
        {
            this.optionsStage.draw();
            this.optionsStage.act();
        }
    }

    private void showOptionsMenu()
    {
        Gdx.input.setInputProcessor(this.optionsStage);
        this.show_options_menu = true;
    }

    private void hideOptionsMenu()
    {
        Gdx.input.setInputProcessor(this);
        this.show_options_menu = false;
    }
}
