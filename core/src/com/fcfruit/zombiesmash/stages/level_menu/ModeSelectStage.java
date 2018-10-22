package com.fcfruit.zombiesmash.stages.level_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.stages.RubeStage;

public class ModeSelectStage extends RubeStage
{
    private LevelSelectStage levelSelectStage;

    public ModeSelectStage(Viewport viewport)
    {
        super(viewport, "ui/level_menu/mode_select/mode_select.json", "ui/level_menu/mode_select/", false);

        this.levelSelectStage = new LevelSelectStage(viewport);

        this.findActor("back_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Environment.game.setScreen(Environment.screens.mainmenu);
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.findActor("survival_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Environment.game.setScreen(Environment.screens.mainmenu);
                super.touchUp(event, x, y, pointer, button);
            }
        });
        
        this.setDebugAll(true);
    }

}
