package com.fcfruit.monstersmash.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.screens.MainMenu;
import com.fcfruit.monstersmash.ui.FontActor;
import com.fcfruit.monstersmash.ui.ImageButton;

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

    private com.fcfruit.monstersmash.ui.ImageButton mainMenuButton;
    private Image endStatusBounds;
    private Image levelInfoBounds;
    private Image levelRewardBounds;
    private Image brainCounterBounds;
    private com.fcfruit.monstersmash.ui.ImageButton continueButton;

    private com.fcfruit.monstersmash.ui.FontActor endStatus;
    private double endStatusFlickerTimer;
    private double timeBetweenFlicker = 500;

    private com.fcfruit.monstersmash.ui.FontActor levelInfo;
    private com.fcfruit.monstersmash.ui.FontActor levelRewards;
    private com.fcfruit.monstersmash.ui.FontActor continueButtonText;

    private Skeleton[] brainAnimSkeletons;
    private ArrayList<Skeleton> brainAnimSkeletonsToDraw;
    private AnimationState[] brainAnimStates;
    private ArrayList<AnimationState> brainAnimStatesToUpdate;
    private ArrayList<AnimationState> brainAnimStateAddQueue; // Need queue since listeners are called while looping through <brainAnimStatesToUpdate>

    private SpriteBatch spriteBatch;
    private SkeletonRenderer skeletonRenderer;

    private int brainIncrementSpeedFactor = 8;
    private int brainIncrement = this.brainIncrementSpeedFactor; // Need this as the brain increment needs to be divisible by the speed evenly
    private double timeBetweenBrainIncrement = 50;
    private double brainIncrementTimer;

    public LevelEndStage(Viewport viewport)
    {
        super(viewport, "ui/level_end/level_end.json", "ui/level_end/", false);

        this.mainMenuButton = (com.fcfruit.monstersmash.ui.ImageButton) this.findActor("main_menu_button");
        mainMenuButton.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {

                Environment.musicManager.stopAllMusic();
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
        this.brainCounterBounds = (Image) this.findActor("brain_counter_bounds");

        this.continueButton = (com.fcfruit.monstersmash.ui.ImageButton) this.findActor("continue_button");
        this.continueButton.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                onContinue();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.brainAnimSkeletonsToDraw = new ArrayList<Skeleton>();
        this.brainAnimStatesToUpdate = new ArrayList<AnimationState>();
        this.brainAnimStateAddQueue = new ArrayList<AnimationState>();

        this.spriteBatch = new SpriteBatch();
        this.skeletonRenderer = new SkeletonRenderer();
    }

    @Override
    public void draw()
    {
        super.draw();

        if (!(Environment.level.objective.getHealth() <= 0))
        {
            this.spriteBatch.setProjectionMatrix(this.getViewport().getCamera().combined);
            this.spriteBatch.begin();
            for (Skeleton skeleton : this.brainAnimSkeletonsToDraw)
                this.skeletonRenderer.draw(this.spriteBatch, skeleton);
            this.spriteBatch.end();
        }
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);

        if (!(Environment.level.objective.getHealth() <= 0))
        {
            this.brainAnimStatesToUpdate.addAll(this.brainAnimStateAddQueue);
            this.brainAnimStateAddQueue.clear();

            int i = 0;
            for (AnimationState animationState : this.brainAnimStatesToUpdate)
            {
                animationState.update(delta); // Update the animation getUpTimer.

                animationState.apply(this.brainAnimSkeletonsToDraw.get(i)); // Poses skeleton using current animations. This sets the bones' local SRT.

                this.brainAnimSkeletonsToDraw.get(i).updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.
                i++;
            }

            if (this.brainIncrement < Environment.level.brainCounter * Environment.difficulty_multipliers.get(Environment.currentDifficulty)
                    && System.currentTimeMillis() - this.brainIncrementTimer >= this.timeBetweenBrainIncrement)
            {
                this.brainIncrement += this.brainIncrement / this.brainIncrementSpeedFactor;
                this.levelRewards.setText("Brains: " + brainIncrement);
                this.timeBetweenBrainIncrement -= ((double) this.brainIncrement);
                this.brainIncrementTimer = System.currentTimeMillis();
            }

            if (this.brainIncrement > Environment.level.brainCounter*Environment.difficulty_multipliers.get(Environment.currentDifficulty))
            {
                this.brainIncrement -= this.brainIncrement - Environment.level.brainCounter*Environment.difficulty_multipliers.get(Environment.currentDifficulty);
                this.levelRewards.setText("Brains: " + brainIncrement);
            }
        }
    }

    @Override
    public void act()
    {
        super.act();

        if (System.currentTimeMillis() - this.endStatusFlickerTimer >= this.timeBetweenFlicker * 2)
        {
            for (Actor actor : this.getActors().toArray()) if (actor == this.endStatus) return;
            this.addActor(this.endStatus);
            this.endStatusFlickerTimer = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - this.endStatusFlickerTimer >= this.timeBetweenFlicker)
        {
            this.endStatus.remove();
        }
    }

    public void onLevelEnd()
    {
        if (!this.constructed)
        {
            this.constructEndStatus();
            this.constructLevelInfo();
            this.constructContinueButton();
            this.endStatusFlickerTimer = System.currentTimeMillis();
            this.brainIncrementTimer = System.currentTimeMillis();
        }
        this.constructed = true;
    }

    private void constructEndStatus()
    {

        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        bitmapFont.getData().setScale(4);

        String text;
        GlyphLayout layout = new GlyphLayout();

        this.endStatus = new com.fcfruit.monstersmash.ui.FontActor(bitmapFont);

        if (Environment.level.objective.getHealth() <= 0)
            text = "Game Over";
        else
            text = "Level Complete";

        endStatus.setText(text);
        layout.setText(bitmapFont, text);
        this.endStatus.setPosition(this.endStatusBounds.getX() + this.endStatusBounds.getWidth() / 2 - layout.width / 2, this.endStatusBounds.getY() + layout.height);

        this.addActor(endStatus);
    }

    private void constructLevelInfo()
    {
        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        bitmapFont.getData().setScale(2.5f);

        this.levelInfo = new com.fcfruit.monstersmash.ui.FontActor(bitmapFont);
        this.levelInfo.setPosition(this.levelInfoBounds.getX(), this.levelInfoBounds.getY() + this.levelInfoBounds.getHeight() - 100);

        if (Environment.level.objective.getHealth() <= 0)
        {
            this.levelInfo.setText(randomGameOverTips.get((int) Math.round(Math.random() * 3)));
            this.addActor(this.levelInfo);
            return;
        } else
        {
            this.levelInfo.setText(randomGameWinText.get((int) Math.round(Math.random() * 2)));
            this.addActor(this.levelInfo);
        }

        GlyphLayout levelRewards1Layout = new GlyphLayout();
        this.levelRewards = new com.fcfruit.monstersmash.ui.FontActor(bitmapFont);
        this.levelRewards.setText("Brains: 0");
        levelRewards1Layout.setText(bitmapFont, "Brains: " + this.brainIncrement + " x");

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("ui/level_end/brain_reward.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("ui/level_end/brain_reward.json"));
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        int len = 1;
        if (Environment.currentDifficulty.equals("normal"))
            len = 2;
        else if (Environment.currentDifficulty.equals("hard"))
            len = 3;

        String[] dif_list = new String[]{"easy", "normal", "hard"};

        this.brainAnimSkeletons = new Skeleton[len];
        this.brainAnimStates = new AnimationState[len];
        for (int i = 0; i < len; i++)
        {
            this.brainAnimSkeletons[i] = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).

            this.brainAnimSkeletons[i].setSkin(dif_list[i]); // Set skin

            this.brainAnimStates[i] = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
            this.brainAnimStates[i].setAnimation(0, "bounce", false);
            this.brainAnimStates[i].setTimeScale(0.75f);
            this.brainAnimStates[i].addListener(new AnimationState.AnimationStateAdapter()
            {
                @Override
                public void complete(AnimationState.TrackEntry entry)
                {
                    super.complete(entry);
                    addNextBrainAnimation();
                }
            });

            this.brainAnimSkeletons[i].getRootBone().setScale(this.levelRewardBounds.getHeight() / this.brainAnimSkeletons[i].getData().getHeight());
        }

        this.brainAnimSkeletonsToDraw.add(this.brainAnimSkeletons[0]);
        this.brainAnimStatesToUpdate.add(this.brainAnimStates[0]);

        for (int i = 0; i < len; i++)
        {
            this.brainAnimSkeletons[i].setPosition(50 + this.levelRewardBounds.getX() + this.brainAnimSkeletons[i].getData().getWidth() * this.brainAnimSkeletons[i].getRootBone().getScaleX() * i,
                    this.levelRewardBounds.getY());
        }

        this.levelRewards.setPosition(this.brainCounterBounds.getX(),
                this.brainCounterBounds.getY() + this.brainCounterBounds.getHeight()/1.5f);

        this.addActor(this.levelRewards);
    }

    private void addNextBrainAnimation()
    {
        if (this.brainAnimSkeletonsToDraw.size() == this.brainAnimSkeletons.length)
            return;

        this.brainAnimSkeletonsToDraw.add(this.brainAnimSkeletons[this.brainAnimSkeletonsToDraw.size()]);
        this.brainAnimStateAddQueue.add(this.brainAnimStates[this.brainAnimStatesToUpdate.size()]);
    }

    private void constructContinueButton()
    {
        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        bitmapFont.getData().setScale(2.5f);

        GlyphLayout glyphLayout = new GlyphLayout();

        this.continueButtonText = new com.fcfruit.monstersmash.ui.FontActor(bitmapFont);

        if (Environment.level.objective.getHealth() <= 0)
        {
            this.continueButtonText.setText("Retry");
            glyphLayout.setText(bitmapFont, "Retry");
        } else
        {
            this.continueButtonText.setText("Next Level");
            glyphLayout.setText(bitmapFont, "Next level");
        }

        this.continueButtonText.setPosition(this.continueButton.getX() + this.continueButton.getWidth() / 2 - glyphLayout.width / 2,
                this.continueButton.getY() + this.continueButton.getHeight() - glyphLayout.height / 2);

        this.addActor(this.continueButtonText);

    }

    private void onContinue()
    {

        if (Environment.level.objective.getHealth() <= 0)
        {
            // Restart level
            Environment.setupGame(Environment.level.level_id);
            Environment.game.setScreen(Environment.screens.gamescreen);
        } else
        {
            // Return to level select
            Environment.musicManager.stopAllMusic();
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
