package com.fcfruit.zombiesmash.stages.level_select;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.stages.RubeStage;

public class Page1 extends RubeStage
{
    public Page1(Viewport viewport)
    {
        super(viewport, "ui/level_select/page1/page1.json", "ui/level_select/page1/", false);

        this.findActor("survival_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Gdx.app.log("zaaaa", "ssss");
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }
}
