package com.fcfruit.monstersmash.stages.game_ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.ui.FontActor;
import com.fcfruit.monstersmash.ui.HealthOverlay;

public class SurvivalUIStage extends GameUIStage
{

    private HealthOverlay healthBar;

    private Image brainCountbackground;
    private Image brainCountImage;
    private FontActor brainCount;

    public SurvivalUIStage(Viewport v)
    {
        super(v, "ui/game_ui/survival/survival_ui.json", "ui/game_ui/survival/", false);

        healthBar = new HealthOverlay((Image) this.findActor("health_overlay"));

        brainCountbackground = new Image(new Texture("ui/game_ui/survival/retro_box.png"));
        brainCountbackground.setSize(pause_button.getWidth()*1.25f, 180);
        brainCountbackground.setPosition(pause_button.getX() - (brainCountbackground.getWidth() - pause_button.getWidth()), this.findActor("pause_button").getY() - brainCountbackground.getHeight());

        brainCountImage = new Image(new Texture("brains/brain1.png"));
        brainCountImage.setSize(140, 140);
        brainCountImage.setPosition(brainCountbackground.getX() + 20, brainCountbackground.getY() + 20);

        brainCount = new FontActor(new BitmapFont(Gdx.files.internal("ui/font/font.fnt")));
        brainCount.getBitmapFont().getData().setScale(2);

        brainCount.setPosition(brainCountImage.getX() + brainCountImage.getWidth() + 10, brainCountImage.getY() + brainCountImage.getHeight() / 2 + 20);

        this.addActor(brainCountbackground);
        this.addActor(brainCountImage);
        this.addActor(brainCount);
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        this.brainCount.setText("" + Environment.level.getBrainCount());
        this.healthBar.setPercent(Environment.level.objective.getHealth());
    }
}
