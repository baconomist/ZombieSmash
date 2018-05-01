package com.fcfruit.zombiesmash.release.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Lucas on 2017-12-17.
 */

public class Slider {

    Sprite background;
    Sprite slider;

    public Slider(Sprite bak, Sprite sld){
        background = bak;
        slider = sld;
    }

    public void setPercent(float percent){
        if(percent > -1 && percent < 101) {
            slider.setSize(slider.getTexture().getWidth() * (percent / 100), slider.getHeight());
        }
    }

    public void draw(Batch batch) {
        background.draw(batch);
        slider.draw(batch);
    }

    public void setPosition(float x, float y){
        background.setPosition(x, y);
        slider.setPosition(x + 16, y + 16);
    }

    public boolean contains(float x, float y){
        return background.getBoundingRectangle().contains(x, y) || slider.getBoundingRectangle().contains(x, y);
    }

    public float getX(){
        return background.getX();
    }
    public float getY(){
        return background.getY();
    }


    public float getWidth(){return background.getWidth();}
    public float getHeight(){return background.getHeight();}

}
