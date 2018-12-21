package com.fcfruit.monstersmash.stages.level_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.ui.FontActor;

import java.util.ArrayList;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

public class ModeSelectStage extends com.fcfruit.monstersmash.stages.RubeStage
{

    private ScaleToAction popup_action;
    private ScaleToAction popdown_action;

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

        // If either actor clicked below, trigger onPurchaseButtonClicked()
        ((Group) this.findActor("buy_popup")).findActor("purchase_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                onPurchaseButtonClicked();
                super.touchUp(event, x, y, pointer, button);
            }
        });
        ((Group) this.findActor("buy_popup")).findActor("purchase_text").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                onPurchaseButtonClicked();
                super.touchUp(event, x, y, pointer, button);
            }
        });

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

    private Group createBuyPopup()
    {
        Group group = new Group();
        group.setName("buy_popup");

        Image background = new Image(new Texture(Gdx.files.internal("ui/level_menu/mode_select/buy_popup_back.png")));
        background.setName("buy_popup");
        group.addActor(background);

        FontActor promoText = new FontActor(new BitmapFont(Gdx.files.internal("ui/font/font.fnt")));
        promoText.setText("In order to play sandbox you have to buy it for $1.99(CAD).");
        promoText.setPosition(30, background.getHeight() - 30);
        background.setWidth(promoText.getWidth() + 60);
        group.addActor(promoText);

        FontActor purchase_text = new FontActor(promoText.getBitmapFont());
        purchase_text.setName("purchase_text");
        purchase_text.setText("Buy $1.99(CAD)");

        Image purchase_button = new Image(new Texture(Gdx.files.internal("ui/level_menu/mode_select/buy_popup_back.png")));
        purchase_button.setName("purchase_button");
        purchase_button.setSize(purchase_text.getWidth() + 40, purchase_text.getHeight() + 40);
        purchase_button.setPosition(background.getWidth()/2 - purchase_button.getWidth()/2, 50);

        purchase_text.setPosition(purchase_button.getX() + 20, purchase_button.getY() + purchase_button.getHeight() - 20);

        group.addActor(purchase_button);
        group.addActor(purchase_text);

        group.setSize(background.getWidth(), background.getHeight());

        return group;
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

            Group buy_popup = this.createBuyPopup();
            buy_popup.setPosition(getViewport().getWorldWidth()/2 - buy_popup.getWidth()/2, getViewport().getWorldHeight()/2 - buy_popup.getHeight()/2);
            buy_popup.setScale(0);
            buy_popup.setVisible(false);
            buy_popup.setOrigin(Align.center);

            this.popup_action = new ScaleToAction();
            this.popup_action.setScale(1);
            this.popup_action.setDuration(0.1f);

            this.popdown_action = new ScaleToAction();
            this.popdown_action.setScale(0);
            this.popdown_action.setDuration(0.1f);

            this.addActor(lock);
            this.addActor(buy_popup);
        }
    }

    // RECURSIVE
    public ArrayList<Actor> getActorParents(Actor actor, ArrayList<Actor> tempList)
    {
        if(!actor.hasParent())
            return tempList;

        tempList.add(actor.getParent());
        return tempList;
    }

    public boolean actorHasParent(String parent_name, Actor actor)
    {
        for(Actor parent_actor : getActorParents(actor, new ArrayList<Actor>()))
        {
            if(parent_actor.getName() != null && parent_actor.getName().equals(parent_name))
                return true;
        }
        return false;
    }

    public boolean actorHasParent(Actor parent, Actor actor)
    {
        for(Actor parent_actor : getActorParents(actor, new ArrayList<Actor>()))
        {
            if(parent_actor.equals(parent))
                return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        Vector3 stage_pos = getViewport().unproject(new Vector3(screenX, screenY, 0));
        Actor hit = this.hit(stage_pos.x, stage_pos.y, true);

        return (!this.findActor("buy_popup").isVisible() || actorHasParent("buy_popup", hit)) && super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        Vector3 stage_pos = getViewport().unproject(new Vector3(screenX, screenY, 0));
        Actor hit = this.hit(stage_pos.x, stage_pos.y, true);

        if((hit.getParent() == null && !hit.getName().equals("buy_popup"))
                || !actorHasParent("buy_popup", hit))
        {
            this.popdown_action.restart();
            this.findActor("buy_popup").addAction(popdown_action);
        }
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public void act()
    {
        if(this.findActor("buy_popup").getScaleX() == 0 && this.findActor("buy_popup").getActions().size < 1)
            this.findActor("buy_popup").setVisible(false);
        super.act();
    }

    private void onSandboxModeClicked()
    {
        if(!Environment.purchaseManager.is_cache_purchased("sandbox"))
        {
            this.popup_action.restart();
            this.findActor("buy_popup").setScale(0.25f);
            this.findActor("buy_popup").addAction(popup_action);
            this.findActor("buy_popup").setVisible(true);
        }
        else
        {
            Environment.mode = Environment.Mode.SANDBOX;
            Environment.screens.loadingscreen.setGameLoading();
            Environment.game.setScreen(Environment.screens.loadingscreen);
        }
    }

    private void onPurchaseButtonClicked()
    {
        this.popup_action.restart();
        this.findActor("buy_popup").setVisible(false);

        Environment.game.setScreen(Environment.screens.mainmenu);
        Environment.screens.mainmenu.showInAppPurchasesStage();
        Environment.screens.mainmenu.inAppPurchasesStage.setCurrentProduct("sandbox");
    }

}
