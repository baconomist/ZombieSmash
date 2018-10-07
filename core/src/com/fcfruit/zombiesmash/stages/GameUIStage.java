package com.fcfruit.zombiesmash.stages;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface;
import com.fcfruit.zombiesmash.ui.ImageButton;
import com.fcfruit.zombiesmash.ui.Message;
import com.fcfruit.zombiesmash.ui.Slider;

/**
 * Created by Lucas on 2017-12-17.
 */

public class GameUIStage extends RubeStage
{

    private GameMenuStage gameMenuStage;
    private boolean show_game_menu = false;

    private ImageButton pow_btn_1;
    private ImageButton pow_btn_2;
    private ImageButton pow_btn_3;
    private ImageButton pow_btn_4;

    private ImageButton[] powerUpButtons;

    private Slider healthBar;

    private ImageButton pause_button;

    private SpriteBatch spriteBatch;
    private SkeletonRenderer skeletonRenderer;

    private Sprite brainCountImage;
    private BitmapFont brainCount;

    private PowerupInterface[] powerups;

    private Message message;

    public GameUIStage(Viewport v)
    {
        super(v, "ui/game_ui/survival/survival_ui.json", "ui/game_ui/survival/", false);

        this.gameMenuStage = new GameMenuStage(v);

        pow_btn_1 = (ImageButton) this.findActor("powerup_button_1");
        pow_btn_2 = (ImageButton) this.findActor("powerup_button_2");
        pow_btn_3 = (ImageButton) this.findActor("powerup_button_3");
        pow_btn_4 = (ImageButton) this.findActor("powerup_button_4");

        addActor(pow_btn_1);
        addActor(pow_btn_2);
        addActor(pow_btn_3);
        addActor(pow_btn_4);

        this.powerUpButtons = new ImageButton[]{pow_btn_1, pow_btn_2, pow_btn_3, pow_btn_4};

        pause_button = (ImageButton) this.findActor("pause_button");
        pause_button.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                showGameMenu();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        addActor(pause_button);

        /*
        * Do this for now.... not good... add "horizontal" and "vertical" slider(s) to rubeStage
        * scrap the slider class, it's not meant for rubestage dynamic loading
        * */
        Sprite overlay = new Sprite(new Texture(Gdx.files.internal("ui/game_ui/survival/health_overlay.png")));
        healthBar = new Slider(new Sprite(new Texture(Gdx.files.internal("ui/game_ui/survival/health.png"))),
                overlay);
        healthBar.setPosition(this.findActor("health_bar").getX(), this.findActor("health_bar").getY());
        healthBar.setSize(this.findActor("health_bar").getWidth(), this.findActor("health_bar").getHeight());
        overlay.setSize(healthBar.getWidth() - 50, healthBar.getHeight() - 50);

        this.findActor("health_bar").remove();
        /*
         * Do this for now.... not good... add "horizontal" and "vertical" slider(s) to rubeStage
         * scrap the slider class, it's not meant for rubestage dynamic loading
         * */

        spriteBatch = new SpriteBatch();
        skeletonRenderer = new SkeletonRenderer();

        brainCountImage = new Sprite(new Texture("brains/brain1.png"));
        brainCountImage.setSize(70, 70);
        brainCountImage.setPosition(getWidth() - brainCountImage.getWidth() * 3, getHeight() - pause_button.getHeight() - brainCountImage.getHeight());

        brainCount = new BitmapFont(Gdx.files.internal("ui/defaultSkin/default.fnt"));
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

    public void showGameMenu()
    {
        Gdx.input.setInputProcessor(this.gameMenuStage);
        this.show_game_menu = true;
        Environment.isPaused = true;
    }

    public void hideGameMenu()
    {
        Gdx.input.setInputProcessor(Environment.screens.gamescreen.getInputMultiplexer());
        this.show_game_menu = false;
        Environment.isPaused = false;
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
                this.message.update(Gdx.graphics.getDeltaTime());
                this.message.draw(spriteBatch, skeletonRenderer);
            }
        }
        spriteBatch.end();

        healthBar.setPercent(Environment.level.objective.getHealth());

        if(show_game_menu)
        {
            this.gameMenuStage.draw();
            this.gameMenuStage.act();
        }
    }
}
