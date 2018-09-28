package com.fcfruit.zombiesmash.stages;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface;
import com.fcfruit.zombiesmash.ui.Message;
import com.fcfruit.zombiesmash.ui.Slider;

/**
 * Created by Lucas on 2017-12-17.
 */

public class GameUIStage extends Stage
{

    ImageButton pow_btn_1;
    ImageButton pow_btn_2;
    ImageButton pow_btn_3;
    ImageButton pow_btn_4;

    ImageButton[] powerUpButtons;

    Slider healthBar;

    ImageButton pause_button;

    SpriteBatch spriteBatch;

    Sprite brainCountImage;
    BitmapFont brainCount;

    PowerupInterface[] powerups;

    private Message message;

    public GameUIStage(Viewport v)
    {
        super(v);

        pow_btn_1 = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/game_ui/power_up_box.png")))));
        pow_btn_1.setPosition(0, getHeight() - pow_btn_1.getHeight());

        pow_btn_2 = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/game_ui/power_up_box.png")))));
        pow_btn_2.setPosition(pow_btn_1.getWidth(), getHeight() - pow_btn_2.getHeight());

        pow_btn_3 = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/game_ui/power_up_box.png")))));
        pow_btn_3.setPosition(pow_btn_1.getWidth() * 2, getHeight() - pow_btn_3.getHeight());

        pow_btn_4 = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/game_ui/power_up_box.png")))));
        pow_btn_4.setPosition(pow_btn_1.getWidth() * 3, getHeight() - pow_btn_4.getHeight());

        addActor(pow_btn_1);
        addActor(pow_btn_2);
        addActor(pow_btn_3);
        addActor(pow_btn_4);

        this.powerUpButtons = new ImageButton[]{pow_btn_1, pow_btn_2, pow_btn_3, pow_btn_4};

        pause_button = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/game_ui/pause_button.png")))));
        pause_button.setPosition(getWidth() - pause_button.getWidth(), getHeight() - pause_button.getHeight());

        addActor(pause_button);

        healthBar = new Slider(new Sprite(new Texture(Gdx.files.internal("gui/game_ui/health_box.png"))),
                new Sprite(new Texture(Gdx.files.internal("gui/game_ui/health_overlay.png"))));
        healthBar.setPosition(getWidth() - healthBar.getWidth() - pause_button.getWidth(), getHeight() - healthBar.getHeight());

        spriteBatch = new SpriteBatch();

        brainCountImage = new Sprite(new Texture("brains/brain1.png"));
        brainCountImage.setSize(70, 70);
        brainCountImage.setPosition(getWidth() - brainCountImage.getWidth() * 3, getHeight() - pause_button.getHeight() - brainCountImage.getHeight());

        brainCount = new BitmapFont(Gdx.files.internal("gui/defaultSkin/default.fnt"));
        brainCount.getData().setScale(2);

        this.powerups = new PowerupInterface[4];

        for (int i = 0; i < this.powerUpButtons.length; i++)
        {
            final int finalI = i;
            this.powerUpButtons[i].addListener(new ClickListener()
            {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
                {
                    if (powerups[finalI] != null)
                    {
                        powerups[finalI].activate();
                        Environment.level.addUpdatableEntity((powerups[finalI]));
                        remove_powerup(powerups[finalI]);
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
        }


    }

    public void add_powerup(PowerupInterface powerUpEntity)
    {
        for (int i = 0; i < this.powerups.length; i++)
        {
            if (this.powerups[i] == null)
            {
                this.powerups[i] = powerUpEntity;
                this.powerups[i].getUIDrawable().setSize(this.powerUpButtons[i].getWidth(), this.powerUpButtons[i].getHeight());
                break;
            }
        }
    }

    public void remove_powerup(PowerupInterface powerUpEntity)
    {
        for (int i = 0; i < this.powerups.length; i++)
        {
            if (this.powerups[i] == powerUpEntity)
            {
                this.powerups[i] = null;
                break;
            }
        }
    }

    public void setMessage(Message message)
    {
        this.message = message;
        this.message.display();
    }

    public void removeMessage(Message message)
    {
        this.message = null;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        if(this.message != null)
            this.message.onTouchUp(screenX, screenY, pointer);
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public void draw()
    {
        super.draw();

        spriteBatch.setProjectionMatrix(getCamera().combined);

        spriteBatch.begin();
        {
            for (int i = 0; i < this.powerups.length; i++)
            {
                if (this.powerups[i] != null)
                {
                    this.powerups[i].getUIDrawable().setPosition(this.powerUpButtons[i].getX() - this.powerUpButtons[i].getWidth() / 2 + this.powerups[i].getUIDrawable().getWidth() / 2, this.powerUpButtons[i].getY() - this.powerUpButtons[i].getHeight() / 2 + this.powerups[i].getUIDrawable().getHeight() / 2);
                    this.powerups[i].getUIDrawable().setRotation(0);
                    this.powerups[i].getUIDrawable().draw(spriteBatch);
                }
            }
            healthBar.draw(spriteBatch);
            brainCountImage.draw(spriteBatch);
            brainCount.draw(spriteBatch, "" + Environment.level.brainCounter, brainCountImage.getX() + brainCountImage.getWidth(), brainCountImage.getY() + brainCountImage.getHeight() / 2);

            if(this.message != null)
            {
                this.message.draw(spriteBatch);
            }
        }
        spriteBatch.end();

        healthBar.setPercent(Environment.level.objective.getHealth());
    }
}
