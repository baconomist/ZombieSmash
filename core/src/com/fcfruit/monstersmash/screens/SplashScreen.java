package com.fcfruit.monstersmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.fcfruit.monstersmash.Environment;

import java.util.Comparator;

public class SplashScreen implements Screen
{
    private StretchViewport viewport;

    private Animation<TextureAtlas.AtlasRegion> animation;
    private float frame_time = 40; // milliseconds

    private Sprite logo;
    private Sprite background;
    private Sprite text;

    private SpriteBatch spriteBatch;
    private float alpha = 0.5f;
    private float elapsed;

    public SplashScreen()
    {
        viewport = new StretchViewport(1920, 1080);

        this.spriteBatch = new SpriteBatch();

        // This atlas needs multiple images as the GPU on some android devices can't load a 16k image, just turns black instead.
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("ui/splashscreen/logo_anim.atlas"));
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
        animation.setPlayMode(Animation.PlayMode.NORMAL);
        animation.setFrameDuration(frame_time/1000);

        logo = new Sprite();

        text = new Sprite(new Texture(Gdx.files.internal("ui/splashscreen/text.png")));
        text.setAlpha(0);

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.drawPixel(0, 0, 0xffffffff); // Blank background, 1px
        background = new Sprite(new Texture(pix));
        background.setSize(this.viewport.getWorldWidth(), this.viewport.getWorldHeight());
    }

    @Override
    public void show()
    {
    }

    @Override
    public void render(float delta)
    {
        delta = Math.min(delta, 0.1f);
        this.elapsed += delta/2;

        if(Environment.assets.update() && this.alpha >= 1.5f) // When loading finished
        {
            Environment.create();
            Environment.game.setScreen(Environment.screens.mainmenu);
            return;
        }

        this.spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        logo.setRegion(this.animation.getKeyFrame(this.elapsed));
        logo.setSize(logo.getRegionWidth(), logo.getRegionHeight());
        logo.setPosition(this.viewport.getWorldWidth()/2 - logo.getWidth()/2, this.viewport.getWorldHeight()/2 - logo.getHeight()/2);

        text.setPosition(500, 100);
        if(this.alpha < 1f)
            text.setAlpha(this.alpha);

        this.spriteBatch.begin();
            background.draw(spriteBatch);
            logo.draw(spriteBatch);
            text.draw(spriteBatch);
        this.spriteBatch.end();

        this.alpha += delta/10f;
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
