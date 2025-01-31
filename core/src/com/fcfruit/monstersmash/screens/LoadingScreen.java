package com.fcfruit.monstersmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.ui.FontActor;

import java.util.Comparator;

public class LoadingScreen implements Screen
{
    private int levelID;

    private StretchViewport viewport;

    private Animation<TextureAtlas.AtlasRegion> animation;
    private float frame_time = 40; // milliseconds

    private Sprite bone;
    private FontActor text;

    private SpriteBatch spriteBatch;
    private float elapsed;

    private boolean gameLoading = false;

    public LoadingScreen()
    {
        viewport = new StretchViewport(1920, 1080);

        this.spriteBatch = new SpriteBatch();

        // This atlas needs multiple images as the GPU on some android devices can't load a 16k image, just turns black instead.
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("ui/loading_screen/loading_screen.atlas"));
        Array<TextureAtlas.AtlasRegion> atlasRegions = textureAtlas.getRegions();
        atlasRegions.sort(new Comparator<TextureAtlas.AtlasRegion>()
        {
            @Override
            public int compare(TextureAtlas.AtlasRegion o1, TextureAtlas.AtlasRegion o2)
            {
                return Integer.valueOf(o1.name) - Integer.valueOf(o2.name);
            }
        });

        animation = new Animation<TextureAtlas.AtlasRegion>(frame_time, atlasRegions.toArray());
        animation.setPlayMode(Animation.PlayMode.LOOP);
        animation.setFrameDuration(frame_time / 1000);

        bone = new Sprite();

        GlyphLayout glyphLayout = new GlyphLayout();
        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        text = new FontActor(bitmapFont);
        text.setText("Loading... Please Wait...");

        glyphLayout.setText(bitmapFont, "Loading... Please Wait...");

        text.setPosition(viewport.getWorldWidth() / 2 - glyphLayout.width / 2, 100);
    }

    public void setLevelID(int id)
    {
        this.levelID = id;
    }

    public void setGameLoading()
    {
        this.gameLoading = true;
    }

    public void setMainMenuLoading()
    {
        this.gameLoading = false;
    }

    @Override
    public void show()
    {
        this.elapsed = 0.0f;
    }

    @Override
    public void render(float delta)
    {
        delta = Math.min(delta, 0.1f);
        this.elapsed += delta;

        if (Environment.assets.update() && elapsed > 2) // When asset loading finished and at least 2 seconds have passed
        {
            if(gameLoading)
            {
                if (Environment.update_setupGameLoading()) // When game/level loading finished
                {
                    if(Environment.mode == Environment.Mode.SURVIVAL)
                        Environment.finishGameLoadingSurvival(levelID);
                    else
                        Environment.finishGameLoadingSandbox();

                    Environment.game.setScreen(Environment.screens.gamescreen);
                    return;
                }
            } else
            {
                Environment.isPaused = false;
                Environment.level = null;
                Environment.physics = null;
                System.gc();
                Environment.screens.mainmenu = new MainMenu();
                Environment.screens.levelmenu = new LevelMenu();
                Environment.game.setScreen(Environment.screens.mainmenu);
            }
        }

        this.spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        bone.setRegion(this.animation.getKeyFrame(this.elapsed));
        bone.setSize(bone.getRegionWidth(), bone.getRegionHeight());
        bone.setPosition(this.viewport.getWorldWidth() / 2 - bone.getWidth() / 2, this.viewport.getWorldHeight() / 2 - bone.getHeight() / 2);

        this.spriteBatch.begin();
        bone.draw(spriteBatch);
        text.draw(spriteBatch, 1);
        this.spriteBatch.end();

    }

    @Override
    public void resize(int width, int height)
    {
        // use true here to center the camera
        // that's what you probably want in case of Screens
        viewport.update(width, height, true);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {
        this.spriteBatch.dispose();
    }
}
