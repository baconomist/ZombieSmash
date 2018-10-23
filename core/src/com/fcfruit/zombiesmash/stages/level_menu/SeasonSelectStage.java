package com.fcfruit.zombiesmash.stages.level_menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.stages.RubeStage;

public class SeasonSelectStage extends RubeStage
{
    public SeasonSelectStage(Viewport viewport)
    {
        super(viewport, "ui/level_menu/season_select/season_select.json", "ui/level_menu/season_select/", false);

        this.findActor("back_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Environment.screens.levelmenu.showModeSelect();
                super.touchUp(event, x, y, pointer, button);
            }
        });


        this.findActor("play_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Environment.screens.levelmenu.showLevelSelect();
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }
}
