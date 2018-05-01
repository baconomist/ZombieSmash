package com.fcfruit.zombiesmash.release.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Lucas on 2017-12-17.
 */

public class CustomImageButton extends Sprite {

    BitmapFont font = new BitmapFont(Gdx.files.internal("gui/defaultSkin/default.fnt"));

    String text;

    Object userData;

    public Texture imageDown;

    public CustomImageButton(Texture t, String s){
        super(t);

        text = s;



    }

    public void draw(SpriteBatch batch){
        super.draw(batch);
        font.draw(batch, text, this.getX() + this.getWidth()/2, this.getY() + this.getHeight()/2);
    }

    public void setUserData(Object object){
        userData = object;
    }

    public Object getUserData(){
        return userData;
    }

    public void touchDown(){
        if(imageDown!=null){
            this.setTexture(imageDown);
        }
    }



}
