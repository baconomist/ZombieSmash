package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.GifDecoder;

public class SplashScreen implements Screen
{
    private StretchViewport viewport;
    private Animation<TextureRegion> animation;

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

        animation = GifDecoder.loadGIFAnimation(Animation.PlayMode.NORMAL, Gdx.files.internal("ui/splashscreen/splashscreen.gif").read());

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
        this.elapsed += delta;

        if(Environment.assets.update() && this.alpha >= 1.5f) // When loading finished
        {
            Environment.create();
            Environment.game.setScreen(Environment.screens.mainmenu);
            return;
        }

        this.spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        logo.setRegion(this.animation.getKeyFrame(this.elapsed));
        logo.setSize(logo.getRegionWidth(), logo.getRegionHeight());
        logo.setPosition(this.viewport.getWorldWidth()/2 - logo.getRegionWidth()/2, this.viewport.getWorldHeight()/2 - 250);

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
