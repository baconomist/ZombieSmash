package com.fcfruit.zombiesmash.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class FontActor extends Actor
{
    private BitmapFont bitmapFont;
    private String text = "";

    public FontActor(BitmapFont bitmapFont)
    {
        this.bitmapFont = bitmapFont;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);
        this.bitmapFont.draw(batch, text, this.getX(), this.getY());
    }

    public BitmapFont getBitmapFont()
    {
        return bitmapFont;
    }
}
