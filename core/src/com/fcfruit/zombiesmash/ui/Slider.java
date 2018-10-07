package com.fcfruit.zombiesmash.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Lucas on 2017-12-17.
 */

public class Slider
{

    Sprite background;
    Sprite slider;

    public Slider(Sprite bak, Sprite sld)
    {
        background = bak;
        slider = sld;

        this.setPercent(100);
    }

    public void setPercent(float percent)
    {
        if (percent > -1 && percent < 101)
        {
            slider.setSize(background.getWidth() * (percent / 100), background.getHeight() - 30);
        } else if (percent <= 0)
        {
            slider.setSize(0, slider.getHeight());
        }
    }

    public void draw(Batch batch)
    {
        background.draw(batch);
        slider.draw(batch);
    }

    public void setPosition(float x, float y)
    {
        background.setPosition(x, y);
        slider.setPosition(x + 18, y + 18);
    }

    public boolean contains(float x, float y)
    {
        return background.getBoundingRectangle().contains(x, y) || slider.getBoundingRectangle().contains(x, y);
    }

    public void setSize(float width, float height)
    {
        this.background.setSize(width, height);
        this.slider.setSize(width, this.slider.getHeight());
    }

    public float getX()
    {
        return background.getX();
    }

    public float getY()
    {
        return background.getY();
    }


    public float getWidth()
    {
        return background.getWidth();
    }

    public float getHeight()
    {
        return background.getHeight();
    }

}
