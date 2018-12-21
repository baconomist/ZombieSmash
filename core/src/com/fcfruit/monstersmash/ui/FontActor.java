package com.fcfruit.monstersmash.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class FontActor extends Actor
{
    private BitmapFont bitmapFont;
    private String text = "";

    private GlyphLayout glyphLayout;

    public FontActor(BitmapFont bitmapFont)
    {
        this.bitmapFont = bitmapFont;
        this.glyphLayout = new GlyphLayout();
    }

    public void setText(String text)
    {
        this.text = text;
        this.glyphLayout.setText(bitmapFont, text);
        this.setScale(1, 1);
        this.setSize(glyphLayout.width, glyphLayout.height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);
        if(getScaleX() != this.bitmapFont.getScaleX() || getScaleY() != this.bitmapFont.getScaleY())
        {
            this.bitmapFont.getData().setScale(getScaleX(), getScaleY());
        }

        this.bitmapFont.draw(batch, text, this.getX(), this.getY());
    }

    public BitmapFont getBitmapFont()
    {
        return bitmapFont;
    }
}
