package com.fcfruit.monstersmash.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.fcfruit.monstersmash.Environment;

public class MultiImageSlider extends Actor
{
    private boolean horizontal;

    private Sprite background;
    private Sprite image;

    private float percent;

    public MultiImageSlider(Sprite slider_background, Sprite slider_image, boolean horizontal)
    {
        super();

        this.horizontal = horizontal;

        this.background = slider_background;
        this.image = slider_image;

        this.addListener(new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                onTouchDown(x, y, pointer);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer)
            {
                onTouchDragged(x, y, pointer);
                super.touchDragged(event, x, y, pointer);
            }
        });
    }

    public void setPercent(float percent)
    {
        if(percent < 0)
            percent = 0;
        else if(percent > 100)
            percent = 100;

        this.percent = percent;
        percent = percent / 100;

        TextureRegion textureRegion;
        if(!this.horizontal)
        {
            textureRegion = new TextureRegion(this.image.getTexture());
            textureRegion.setRegionHeight((int) (this.background.getHeight() * percent));
            textureRegion.flip(false, true);

            this.image.setSize(this.background.getWidth(), this.background.getHeight() * percent);
        }
        else
        {
            textureRegion = new TextureRegion(this.image.getTexture());
            textureRegion.setRegionWidth((int) (this.background.getWidth() * percent));
            textureRegion.flip(false, true);

            this.image.setSize(this.background.getWidth() * percent, this.background.getHeight());
        }
        this.image.setRegion(textureRegion);
    }

    public float getPercent()
    {
        return this.percent;
    }

    private void onTouchDown(float x, float y, int pointer)
    {
        this.setPercent((y - this.background.getY()) / this.getHeight() * 100);
    }

    private void onTouchDragged(float x, float y, int pointer)
    {
        this.setPercent((y - this.background.getY()) / this.getHeight() * 100);
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);
        this.background.draw(batch);
        this.image.draw(batch);
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        this.background.setPosition(x, y);
        this.image.setPosition(x, y);
    }

    @Override
    public void setSize(float width, float height)
    {
        super.setSize(width, height);
        this.background.setSize(width, height);
        this.image.setSize(width, this.image.getHeight());
    }

    @Override
    public void setRotation(float degrees)
    {
        super.setRotation(degrees);
    }
}
