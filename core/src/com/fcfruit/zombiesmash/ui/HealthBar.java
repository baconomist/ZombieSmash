package com.fcfruit.zombiesmash.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Lucas on 2017-12-17.
 */

public class HealthBar {

    Sprite healthBar;
    Sprite overlay;

    public HealthBar(Sprite bar, Sprite baroverlay){
        healthBar = bar;
        overlay = baroverlay;
    }

    public void setPercent(float percent){
        if(percent > -1 || percent < 101) {
            overlay.setSize(overlay.getTexture().getWidth() * (percent / 100), overlay.getHeight());
        }
    }

    public void draw(Batch batch) {
        healthBar.draw(batch);
        overlay.draw(batch);
    }

    public void setPosition(float x, float y){
        healthBar.setPosition(x, y);
        overlay.setPosition(x + 16, y + 16);
    }


    public float getWidth(){return healthBar.getWidth();}
    public float getHeight(){return healthBar.getHeight();}

}
