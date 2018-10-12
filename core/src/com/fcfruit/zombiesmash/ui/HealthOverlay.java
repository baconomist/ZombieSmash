package com.fcfruit.zombiesmash.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class HealthOverlay
{
    private Sprite slider_image;
    private Vector2 initial_size;

    private float percent;

    public HealthOverlay(Sprite slider_image)
    {
        this.slider_image = slider_image;
        this.initial_size = new Vector2(slider_image.getWidth(), slider_image.getHeight());

        this.percent = 100;
    }

    public void setPercent(float percent)
    {
        if(percent < 0)
            percent = 0;
        else if(percent > 100)
            percent = 100;

        this.percent = percent;
        percent = percent/100;

        this.slider_image.setSize(this.initial_size.x*percent, this.slider_image.getHeight());
    }

    public void draw(SpriteBatch batch)
    {
        this.slider_image.draw(batch);
    }

    public float getPercent()
    {
        return this.percent;
    }

    public void setPosition(float x, float y)
    {
        this.slider_image.setPosition(x, y);
    }


}
