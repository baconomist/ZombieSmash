package com.fcfruit.zombiesmash.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.ZombieSmash;

public class SplashScreen implements Screen
{
    private StretchViewport viewport;

    Sprite sprite;
    SpriteBatch spriteBatch;
    float alpha = 0.5f;

    public SplashScreen()
    {
        viewport = new StretchViewport(1920, 1080);

        this.sprite = new Sprite(new Texture(Gdx.files.internal("ui/splashscreen.png")));
        this.sprite.setAlpha(alpha);

        this.spriteBatch = new SpriteBatch();
    }

    @Override
    public void show()
    {
    }

    @Override
    public void render(float delta)
    {
        this.spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        this.spriteBatch.begin();
        this.sprite.draw(this.spriteBatch);
        this.spriteBatch.end();
        this.alpha += delta/10f;

        if(this.alpha >= 1.5f)
            Environment.game.setScreen(Environment.screens.mainmenu);

        if(this.alpha <= 1f)
            this.sprite.setAlpha(this.alpha);
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

    }
}
