package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.DetachableEntity;
import com.fcfruit.zombiesmash.entity.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.DrawableEntity;
import com.fcfruit.zombiesmash.entity.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;

/**
 * Created by Lucas on 2018-01-07.
 */

public class NewPart implements DrawableEntityInterface, DetachableEntityInterface, InteractiveEntityInterface {
    private DrawableEntity drawableEntity;
    private DetachableEntity detachableEntity;

    private Joint joint;


    public NewPart(Sprite sprite, Body physicsBody, Joint joint){
        this.drawableEntity = new DrawableEntity(sprite, physicsBody);
        this.detachableEntity = new DetachableEntity(joint);

        this.joint = joint;
    }

    @Override
    public void detach() {
        this.detachableEntity.detach();
    }

    @Override
    public void setState(String state) {
        this.detachableEntity.setState(state);
    }

    @Override
    public String getState() {
        return this.detachableEntity.getState();
    }

    @Override
    public void draw(SpriteBatch batch) {
        this.drawableEntity.draw(batch);
    }

    @Override
    public void update(float delta) {
        this.drawableEntity.update(delta);
    }

    @Override
    public Vector2 getPosition() {
        return this.drawableEntity.getPosition();
    }

    @Override
    public void setPosition(Vector2 position) {
        this.drawableEntity.setPosition(position);
    }

    @Override
    public float getAngle() {
        return this.drawableEntity.getAngle();
    }

    @Override
    public void setAngle(float angle) {
        this.drawableEntity.setAngle(angle);
    }

    @Override
    public Vector2 getSize() {
        return this.drawableEntity.getSize();
    }

    @Override
    public void onTouchDown() {

    }

    @Override
    public void onTouchDragged() {

    }

    @Override
    public void onTouchUp() {

    }
}
