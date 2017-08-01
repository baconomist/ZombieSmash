package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.fcfruit.zombiesmash.Physics;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-07-21.
 */

public class Zombie extends Actor{

    private Game game;

    private Physics physics;

    private Sprite sprite;

    private Rectangle bounds;

    private Vector2 previousTouch;

    private float elapsed;

    private float mass;

    public boolean isTouching;

    public boolean isPhysicsEnabled;

    public Zombie(ArrayList parts, Game g) {
        super();

        game = g;

        physics = new Physics(this);

        sprite = new Sprite(new Texture(Gdx.files.internal("zombies/default/DefaultZombie.png")));

        bounds = new Rectangle();
        bounds.setSize(sprite.getWidth()*2, sprite.getHeight()*2);
        bounds.setPosition(sprite.getX() - sprite.getWidth()/2, sprite.getY() - sprite.getHeight()/2);

        previousTouch = new Vector2(0, 0);

        elapsed = 0;
        //a
        ///asdfasdf

        mass = 4;

        isTouching = false;

        isPhysicsEnabled = true;

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float delta = Gdx.graphics.getDeltaTime();

        animate(batch, delta);

        update(delta);

    }

    private void update(float delta){

            bounds.setPosition(sprite.getX() - sprite.getWidth() / 2, sprite.getY() - sprite.getHeight() / 2);

            if (isTouching) {
                sprite.setPosition(Gdx.input.getX() - sprite.getWidth() / 2, (Gdx.graphics.getHeight() - Gdx.input.getY()) - sprite.getHeight());
            }

            if (bounds.contains(Gdx.input.getX(), (Gdx.graphics.getHeight() - Gdx.input.getY())) && Gdx.input.isTouched()) {
                isTouching = true;
            } else if (!isDraging()) {
                isTouching = false;
            }

            if(!isTouching){
                physics.update(delta);
            }

        previousTouch.x = Gdx.input.getX();
        previousTouch.y = Gdx.input.getY();

    }

    private void animate(Batch batch, float delta){

        sprite.draw(batch);

    }

    private boolean isDraging(){

        if(Gdx.input.getDeltaX() != 0f || Gdx.input.getDeltaY() != 0f) {

            return Gdx.input.getDeltaX() <= 50 && Gdx.input.getDeltaX() >= -50 && Gdx.input.getDeltaY() <= 50 && Gdx.input.getDeltaY() >= -50;

        }

        return false;

    }

    public Sprite getSprite(){
        return sprite;
    }

    public Vector2 getPreviousTouch(){
        return previousTouch;
    }

    public float getMass(){
        return mass;
    }

}








