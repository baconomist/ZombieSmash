package com.fcfruit.zombiesmash.stages;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.rube.RubeDefaults;
import com.fcfruit.zombiesmash.ui.HealthBar;

/**
 * Created by Lucas on 2017-12-17.
 */

public class GameUIStage extends Stage {

    ImageButton pow_btn_1;
    ImageButton pow_btn_2;
    ImageButton pow_btn_3;
    ImageButton pow_btn_4;

    HealthBar healthBar;

    ImageButton pause_button;

    SpriteBatch spriteBatch;

    public GameUIStage(Viewport v){
        super(v);

        pow_btn_1 = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/game_ui/power_up_box.png")))));
        pow_btn_1.setPosition(0, getHeight() - pow_btn_1.getHeight());

        pow_btn_2 = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/game_ui/power_up_box.png")))));
        pow_btn_2.setPosition(pow_btn_1.getWidth(), getHeight() - pow_btn_2.getHeight());

        pow_btn_3 = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/game_ui/power_up_box.png")))));
        pow_btn_3.setPosition(pow_btn_1.getWidth()*2, getHeight() - pow_btn_3.getHeight());

        pow_btn_4 = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/game_ui/power_up_box.png")))));
        pow_btn_4.setPosition(pow_btn_1.getWidth()*3, getHeight() - pow_btn_4.getHeight());

        addActor(pow_btn_1);
        addActor(pow_btn_2);
        addActor(pow_btn_3);
        addActor(pow_btn_4);

        pause_button = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/game_ui/pause_button.png")))));
        pause_button.setPosition(getWidth() - pause_button.getWidth(), getHeight() - pause_button.getHeight());

        addActor(pause_button);

        healthBar = new HealthBar(new Sprite(new Texture(Gdx.files.internal("gui/game_ui/health_box.png"))),
                new Sprite(new Texture(Gdx.files.internal("gui/game_ui/health_overlay.png"))));
        healthBar.setPosition(getWidth() - healthBar.getWidth() - pause_button.getWidth(), getHeight() - healthBar.getHeight());

        spriteBatch = new SpriteBatch();

    }

    @Override
    public void draw() {
        super.draw();
        spriteBatch.setProjectionMatrix(getCamera().combined);
        spriteBatch.begin();
        healthBar.draw(spriteBatch);
        spriteBatch.end();
        healthBar.setPercent(Environment.level.objective.getHealth());
    }
}
