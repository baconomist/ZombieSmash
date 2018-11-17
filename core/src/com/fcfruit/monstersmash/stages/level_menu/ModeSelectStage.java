package com.fcfruit.monstersmash.stages.level_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.ui.FontActor;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

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
                onSandboxModeClicked();
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

        this.sandboxModeButtonSetup();
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        if(Environment.purchaseManager.is_cache_purchased("sandbox") && findActor("lock") != null)
        {
            findActor("lock").remove();
        }
    }

    private void sandboxModeButtonSetup()
    {
        if(!Environment.purchaseManager.is_cache_purchased("sandbox"))
        {
            Image lock_bounds = (Image) this.findActor("lock_bounds");

            Image lock = new Image(new Texture(Gdx.files.internal("ui/level_menu/mode_select/lock.png")));
            lock.setName("lock");
            lock.setSize(lock_bounds.getWidth(), lock_bounds.getHeight());
            lock.setPosition(lock_bounds.getX(), lock_bounds.getY());

            this.addActor(lock);
        }
    }

    private void onSandboxModeClicked()
    {
        if(!Environment.purchaseManager.is_cache_purchased("sandbox"))
        {
            Environment.game.setScreen(Environment.screens.mainmenu);
            Environment.screens.mainmenu.showInAppPurchasesStage();
            Environment.screens.mainmenu.inAppPurchasesStage.setCurrentProduct("sandbox");
        }
        else
        {
            Environment.mode = Environment.Mode.SANDBOX;
            Environment.screens.loadingscreen.setGameLoading();
            Environment.game.setScreen(Environment.screens.loadingscreen);
        }
    }

}
