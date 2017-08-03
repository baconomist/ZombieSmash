package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.spine.Skeleton;
import com.fcfruit.zombiesmash.BodyPhysics;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-07-21.
 */

public class Zombie extends Actor{

    private Game game;

    public Body body;

    public float mass;

    public int touch;

    public boolean isTouching;

    public boolean isPhysicsEnabled;

    public Zombie(ArrayList parts, Game g) {
        super();

        game = g;

        body = new Body(this);

        mass = 4;

        isTouching = false;

        isPhysicsEnabled = true;

        Gdx.app.log("width", "" + body.getWidth());

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float delta = Gdx.graphics.getDeltaTime();

        update(delta);

    }

    public void update(float delta){

        if(body.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()) && Gdx.input.isTouched()) {
            isTouching = true;
        } else if (!isDraging()){
            isTouching = false;
        }

        if(!isTouching){
            body.isGravityEnabled = true;
        }
        else{
            body.isGravityEnabled = false;
        }

        if(isTouching) {
            this.setPosition(Gdx.input.getX() + 10, (Gdx.graphics.getHeight() - Gdx.input.getY() - 100));
        }

        body.update(delta);

    }

    private boolean isDraging(){

        if(Gdx.input.getDeltaX() != 0f || Gdx.input.getDeltaY() != 0f) {

            return Gdx.input.getDeltaX() <= 50 && Gdx.input.getDeltaX() >= -50 && Gdx.input.getDeltaY() <= 50 && Gdx.input.getDeltaY() >= -50;

        }

        return false;

    }

    public void setPosition(float x, float y){
        super.setPosition(x, y);
        body.setPosition(x, y);
    }

}








