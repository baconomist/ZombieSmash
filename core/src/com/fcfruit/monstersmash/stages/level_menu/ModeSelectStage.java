package com.fcfruit.monstersmash.stages.level_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.stages.RubeStage;

public class ModeSelectStage extends com.fcfruit.monstersmash.stages.RubeStage
{
    public ModeSelectStage(Viewport viewport)
    {
        super(viewport, "ui/level_menu/mode_select/mode_select.json", "ui/level_menu/mode_select/", false);

        this.findActor("back_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Environment.screens.levelmenu.showSeasonSelect();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.findActor("sandbox_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Environment.mode = Environment.Mode.SANDBOX;
                Environment.screens.loadingscreen.setGameLoading();
                Environment.game.setScreen(Environment.screens.loadingscreen);
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.findActor("survival_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Environment.mode = Environment.Mode.SURVIVAL;
                Environment.screens.levelmenu.showLevelSelect();
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

}
