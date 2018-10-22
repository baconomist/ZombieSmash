package com.fcfruit.zombiesmash.stages.level_menu;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.stages.RubeStage;

public class LevelSelectStage extends RubeStage
{
    public LevelSelectStage(Viewport viewport)
    {
        super(viewport, "ui/level_menu/level_select/level_select.json", "ui/level_menu/level_select/", false);
    }
}
