package com.fcfruit.monstersmash.stages.game_ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.interfaces.PowerupInterface;
import com.fcfruit.monstersmash.powerups.explodable.ExplodablePowerup;
import com.fcfruit.monstersmash.stages.GameMenuStage;
import com.fcfruit.monstersmash.stages.LevelEndStage;
import com.fcfruit.monstersmash.stages.RubeStage;
import com.fcfruit.monstersmash.ui.HealthOverlay;
import com.fcfruit.monstersmash.ui.ImageButton;
import com.fcfruit.monstersmash.ui.Message;

/**
 * Created by Lucas on 2017-12-17.
 */

public class GameUIStage extends RubeStage
{

    private GameMenuStage gameMenuStage;
    private boolean show_game_menu = false;
    private LevelEndStage levelEndStage;
    private boolean show_level_end = false;

    private ImageButton pow_btn_1;
    private ImageButton pow_btn_2;
    private ImageButton pow_btn_3;
    private ImageButton pow_btn_4;

    protected PowerupInterface[] powerups;
    protected ImageButton[] powerUpButtons;

    protected ImageButton pause_button;

    private SpriteBatch spriteBatch;
    private SkeletonRenderer skeletonRenderer;

    private Message message;

    public GameUIStage(Viewport v, String rubeSceneFilePath, String rootPath, boolean physicsEnabled)
    {
        super(v, rubeSceneFilePath, rootPath, physicsEnabled);

        this.gameMenuStage = new GameMenuStage(v);
        this.levelEndStage = new LevelEndStage(v);

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

        spriteBatch = new SpriteBatch();
        skeletonRenderer = new SkeletonRenderer();

        this.powerups = new PowerupInterface[4];

        for (int i = 0; i < this.powerUpButtons.length; i++)
        {
            final int finalI = i;
            this.powerUpButtons[i].setUserObject(i);
            this.powerUpButtons[i].addListener(new ClickListener()
            {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
                {
                    if (powerups[finalI] != null)
                    {
                        if(!Environment.level.isCameraMoving() || !(powerups[finalI] instanceof com.fcfruit.monstersmash.powerups.explodable.ExplodablePowerup))
                        {
                            powerups[finalI].activate();
                            Environment.level.addUpdatableEntity((powerups[finalI]));
                            remove_powerup(powerups[finalI]);
                        }
                        else
                        {
                            Message message = new Message();
                            message.setContent("You can't activate this type\n" +
                                    "of power-up while during" +
                                    "\nthis time." +
                                    "\n\tPlease wait...");
                            setMessage(message);
                        }
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
        if(this.message != null)
            this.message.dispose();
        this.message = message;
        this.message.display();
    }

    public void removeMessage(Message message)
    {
        this.message = null;
    }

    public void showGameMenu()
    {
        Environment.musicManager.pauseMusic();
        Gdx.input.setInputProcessor(this.gameMenuStage);
        this.show_game_menu = true;
        Environment.isPaused = true;
    }

    public void hideGameMenu()
    {
        Environment.musicManager.resumeMusic();
        Gdx.input.setInputProcessor(Environment.screens.gamescreen.getInputMultiplexer());
        this.show_game_menu = false;
        Environment.isPaused = false;
    }

    public void onLevelEnd()
    {
        this.levelEndStage.onLevelEnd();
        Gdx.input.setInputProcessor(this.levelEndStage);
        this.show_level_end = true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        if(this.message != null)
            this.message.onTouchDown(screenX, screenY, pointer);
        return super.touchDown(screenX, screenY, pointer, button);
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

            if(this.message != null)
            {
                this.message.update(Gdx.graphics.getDeltaTime());
                this.message.draw(spriteBatch, skeletonRenderer);
            }
        }
        spriteBatch.end();

        if(show_game_menu)
        {
            this.gameMenuStage.draw();
            this.gameMenuStage.act();
        }

        if(show_level_end)
        {
            this.levelEndStage.draw();
            this.levelEndStage.act();
        }
    }
}
