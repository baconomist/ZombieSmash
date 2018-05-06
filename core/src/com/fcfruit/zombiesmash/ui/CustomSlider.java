package com.fcfruit.zombiesmash.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by lucas on 2017-12-22.
 */

public class CustomSlider extends Sprite {
    
    
    public CustomSlider(Texture t){
        super(t);
    }


    public void setPercent(float percent){
        this.setSize(this.getTexture().getWidth()*(percent/100), this.getHeight());
    }
    
}
