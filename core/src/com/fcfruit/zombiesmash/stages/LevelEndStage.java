package com.fcfruit.zombiesmash.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.screens.MainMenu;
import com.fcfruit.zombiesmash.ui.FontActor;
import com.fcfruit.zombiesmash.ui.ImageButton;

import java.util.ArrayList;

public class LevelEndStage extends RubeStage
{
    private boolean constructed = false;

    private static ArrayList<String> randomGameOverTips;
    static
    {
        randomGameOverTips = new ArrayList<String>();
        randomGameOverTips.add("Zombies ate your brains.\n(Tip: power-ups do stuff)");
        randomGameOverTips.add("Zombies ate your brains.\n(Tip: don't let them eat your \nbrains)");
        randomGameOverTips.add("Zombies ate your brains.\n(Tip: don't suck)");
        randomGameOverTips.add("Zombies ate your brains.\n(Tip: throw them)");
    }

    private static ArrayList<String> randomGameWinText;
    static
    {
        randomGameWinText = new ArrayList<String>();
        randomGameWinText.add("Wow! Look at you! You did it!\nYou won! Proud of yourself?\nHere are some brains...");
        randomGameWinText.add("Congrats.\nThat's all I have to say this time.");
        randomGameWinText.add("Super Cool! Super Awesome!\nCan't think of anything else\nto tell you...");
    }

    private ImageButton mainMenuButton;
    private Image endStatusBounds;
    private Image levelInfoBounds;
    private Image levelRewardBounds;
    private ImageButton continueButton;

    private FontActor endStatus;
    private double endStatusFlickerTimer;
    private double timeBetweenFlicker = 500;
    private double timeBeforeFlicker;

    private FontActor levelInfo;
    private FontActor levelRewards1;
    private FontActor levelRewards2;
    private FontActor continueButtonText;

    private Skeleton brainAnimSkeleton;
    private AnimationState brainAnimState;
    private SpriteBatch spriteBatch;
    private SkeletonRenderer skeletonRenderer;

    public LevelEndStage(Viewport viewport)
    {
        super(viewport, "ui/level_end/level_end.json", "ui/level_end/", false);

        this.mainMenuButton = (ImageButton) this.findActor("main_menu_button");
        mainMenuButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {

                /*
                 * Environment should have a destroy/dispose method!!!!! with System.gc();
                 * */
                Environment.isPaused = false;
                Environment.level = null;
                Environment.physics = null;
                System.gc();
                Environment.screens.mainmenu = new MainMenu();
                Environment.game.setScreen(Environment.screens.mainmenu);
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.endStatusBounds = (Image) this.findActor("end_status_bounds");
        this.levelInfoBounds = (Image) this.findActor("level_info_bounds");
        this.levelRewardBounds = (Image) this.findActor("level_reward_bounds");

        this.continueButton = (ImageButton) this.findActor("continue_button");
        this.continueButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                onContinue();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.spriteBatch = new SpriteBatch();
        this.skeletonRenderer = new SkeletonRenderer();
    }

    @Override
    public void draw()
    {
        super.draw();

        if(!(Environment.level.objective.getHealth() <= 0))
        {
            this.spriteBatch.setProjectionMatrix(this.getViewport().getCamera().combined);
            this.spriteBatch.begin();
            this.skeletonRenderer.draw(this.spriteBatch, this.brainAnimSkeleton);
            this.spriteBatch.end();
        }
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);

        if(!(Environment.level.objective.getHealth() <= 0))
        {
            this.brainAnimState.update(delta); // Update the animation getUpTimer.

            this.brainAnimState.apply(brainAnimSkeleton); // Poses skeleton using current animations. This sets the bones' local SRT.

            this.brainAnimSkeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.
        }
    }

    @Override
    public void act()
    {
        super.act();
        if(System.currentTimeMillis() - this.endStatusFlickerTimer >= this.timeBeforeFlicker)
        {
            this.endStatus.remove();
            this.endStatusFlickerTimer = System.currentTimeMillis();
            this.timeBeforeFlicker = Math.random()*1000 + 500;
        }
        else if(System.currentTimeMillis() - this.endStatusFlickerTimer >= this.timeBetweenFlicker)
        {
            for(Actor actor : this.getActors().toArray()) if(actor == this.endStatus) return;
            this.addActor(this.endStatus);
        }
    }

    public void onLevelEnd()
    {
        if(!this.constructed)
        {
            this.constructEndStatus();
            this.constructLevelInfo();
            this.constructContinueButton();
            this.endStatusFlickerTimer = System.currentTimeMillis();
            this.timeBeforeFlicker = Math.random() * 1000 + 1000;
        }
        this.constructed = true;
    }

    private void constructEndStatus()
    {

        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        bitmapFont.getData().setScale(4);

        String text;
        GlyphLayout layout = new GlyphLayout();

        this.endStatus = new FontActor(bitmapFont);

        if(Environment.level.objective.getHealth() <= 0)
            text = "Game Over";
        else
            text = "Level Complete";

        endStatus.setText(text);
        layout.setText(bitmapFont, text);
        this.endStatus.setPosition(this.endStatusBounds.getX() + this.endStatusBounds.getWidth()/2 - layout.width/2, this.endStatusBounds.getY() + layout.height);

        this.addActor(endStatus);
    }

    private void constructLevelInfo()
    {
        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        bitmapFont.getData().setScale(2.5f);

        this.levelInfo = new FontActor(bitmapFont);
        this.levelInfo.setPosition(this.levelInfoBounds.getX(), this.levelInfoBounds.getY() + this.levelInfoBounds.getHeight() - 100);

        if(Environment.level.objective.getHealth() <= 0)
        {
            this.levelInfo.setText(randomGameOverTips.get((int) Math.round(Math.random() * 3)));
            this.addActor(this.levelInfo);
            return;
        }
        else
        {
            this.levelInfo.setText(randomGameWinText.get((int) Math.round(Math.random() * 2)));
            this.addActor(this.levelInfo);
        }

        GlyphLayout levelRewards1Layout = new GlyphLayout();
        this.levelRewards1 = new FontActor(bitmapFont);
        this.levelRewards1.setText("Brains: " + Environment.level.brainCounter + " x");
        levelRewards1Layout.setText(bitmapFont, "Brains: " + Environment.level.brainCounter + " x");

        GlyphLayout levelRewards2Layout = new GlyphLayout();
        this.levelRewards2 = new FontActor(bitmapFont);
        this.levelRewards2.setPosition(this.levelRewardBounds.getX(), this.levelRewardBounds.getY() + this.levelRewardBounds.getHeight());
        this.levelRewards2.setText("= " + Environment.level.brainCounter * Environment.difficulty_multipliers.get(Environment.currentDifficulty));
        levelRewards2Layout.setText(bitmapFont, "= " + Environment.level.brainCounter * Environment.difficulty_multipliers.get(Environment.currentDifficulty));

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("ui/level_end/brain_reward.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("ui/level_end/brain_reward.json"));
        this.brainAnimSkeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        this.brainAnimSkeleton.setSkin(Environment.currentDifficulty); // Set skin

        this.brainAnimState = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        this.brainAnimState.setAnimation(0, "bounce", false);
        this.brainAnimState.setTimeScale(0.75f);

        this.brainAnimSkeleton.getRootBone().setScale(this.levelRewardBounds.getHeight()/this.brainAnimSkeleton.getData().getHeight());

        this.levelRewards1.setPosition(this.levelRewardBounds.getX(), this.levelRewardBounds.getY() + this.levelRewardBounds.getHeight());
        this.brainAnimSkeleton.setPosition(this.levelRewards1.getX()
                + levelRewards1Layout.width + this.brainAnimSkeleton.getData().getWidth()/2*this.brainAnimSkeleton.getRootBone().getScaleX(), this.levelRewards1.getY() - 200);
        this.levelRewards2.setPosition(this.brainAnimSkeleton.getX() + levelRewards2Layout.width/2, this.levelRewards1.getY());

        this.addActor(this.levelRewards1);
        this.addActor(this.levelRewards2);
    }

    private void constructContinueButton()
    {
        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        bitmapFont.getData().setScale(2.5f);

        GlyphLayout glyphLayout = new GlyphLayout();

        this.continueButtonText = new FontActor(bitmapFont);

        if(Environment.level.objective.getHealth() <= 0)
        {
            this.continueButtonText.setText("Retry");
            glyphLayout.setText(bitmapFont, "Retry");
        }
        else
        {
            this.continueButtonText.setText("Next Level");
            glyphLayout.setText(bitmapFont, "Next level");
        }

        this.continueButtonText.setPosition(this.continueButton.getX() + this.continueButton.getWidth()/2 - glyphLayout.width/2,
                this.continueButton.getY() + this.continueButton.getHeight() - glyphLayout.height/2);

        this.addActor(this.continueButtonText);

    }

    private void onContinue()
    {

        if(Environment.level.objective.getHealth() <= 0)
        {
            // Restart level
            Environment.setupGame(Environment.level.level_id);
            Environment.game.setScreen(Environment.screens.gamescreen);
        }
        else
        {
            // Return to main menu
            /*
             * Environment should have a destroy/dispose method!!!!! with System.gc();
             * */
            Environment.isPaused = false;
            Environment.level = null;
            Environment.physics = null;
            System.gc();
            Environment.screens.mainmenu = new MainMenu();
            Environment.screens.levelmenu.showLevelSelect();
            Environment.game.setScreen(Environment.screens.levelmenu);
        }

    }

}
