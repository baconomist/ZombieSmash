package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-07-21.
 */

public class Zombie extends Sprite {

    private ArrayList animation;

    private int animationLength;

    private float animationSpeed;

    private float count;

    public Zombie(Texture t, ArrayList anim, float animSpeed) {
        super(t);
        animation = anim;
        animationLength = animation.size();
        animationSpeed = animSpeed;
        count = 0;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        animate(Gdx.graphics.getDeltaTime());
    }

    public void animate(float delta){
        if(count == animationLength){
            count = 0;
        }
        this.setTexture((Texture)animation.get((int)count));
        count += 1*animationSpeed;
    }

}
