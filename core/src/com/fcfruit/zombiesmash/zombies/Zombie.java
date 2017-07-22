package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.fcfruit.zombiesmash.GifDecoder;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-07-21.
 */

public class Zombie extends Sprite{

    private Game game;

    private Animation<TextureRegion> defaultAnimation;

    private Animation<TextureRegion> touchedAnimation;

    private Animation<TextureRegion> currentAnimation;

    private float previousTouchX = 0;
    private float previousTouchY = 0;

    private float elapsed;

    private boolean isTouched;

    public Zombie(Texture t, Animation<TextureRegion> anim, Game g) {
        super(t);

        game = g;

        defaultAnimation = anim;

        currentAnimation = defaultAnimation;

        elapsed = 0;

        isTouched = false;

    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        float delta = Gdx.graphics.getDeltaTime();
        animate(delta, batch);
        update(delta);
    }

    public void update(float delta){

        if(isDraging()){
            this.setPosition(Gdx.input.getX(), (Gdx.graphics.getHeight() - Gdx.input.getY()));
        }

        if(this.getBoundingRectangle().contains(previousTouchX, previousTouchY)){
            isTouched = true;
        }
        else if(!isDraging()){
            isTouched = false;
        }

        previousTouchX = Gdx.input.getX();
        previousTouchY = Gdx.input.getY();

    }

    public void animate(float delta, Batch batch){

        if(isTouched){
           currentAnimation = touchedAnimation;
        }

        elapsed += delta;
        batch.draw(currentAnimation.getKeyFrame(elapsed), this.getX(), this.getY());

    }

    private void onTouch(){

    }

    public void setTouchedAnimation(Animation anim){
        touchedAnimation = anim;
    }

    public void setCurrentAnimation(Animation anim){
        currentAnimation = anim;
    }

    private boolean isDraging(){
        if(previousTouchX - Gdx.input.getX() >= 100 ||  previousTouchX - Gdx.input.getX() <= -100){
            return true;
        }
        if(previousTouchY - Gdx.input.getY() >= 100 ||  previousTouchY - Gdx.input.getY() <= -100){
            return true;
        }
        return false;
    }

}
