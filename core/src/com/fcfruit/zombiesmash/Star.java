package com.fcfruit.zombiesmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by Lucas on 2017-12-18.
 */

public class Star extends Sprite {
    public Body physicsBody;

    double timeBeforeDisappear = 5000;
    double disappearTimer = 0;

    int type;

    public Star(Texture t, float x, float y, int tp){
        super(t);

        this.setSize(70, 70);
        this.setOriginCenter();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 1;


        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.1f, 0.1f);
        fixtureDef.shape = shape;

        physicsBody = Environment.physics.createBody(bodyDef);
        physicsBody.createFixture(fixtureDef);
        physicsBody.getFixtureList().get(0).setUserData("star");
        physicsBody.setLinearDamping(10);

        type = tp;

    }

    @Override
    public void draw(Batch batch) {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(physicsBody.getPosition(), 0)));
        this.setPosition(pos.x, Environment.gameCamera.position.y*2 - pos.y);
        this.rotate(30*Gdx.graphics.getDeltaTime());

        if(disappearTimer == 0){
            disappearTimer = System.currentTimeMillis();
        }
        else if(System.currentTimeMillis() - disappearTimer > timeBeforeDisappear){
            onDisappear();
        }
        pos = Environment.gameCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        if(this.getBoundingRectangle().contains(pos.x, pos.y) && Gdx.input.justTouched()){
            onTouchDown();
        }

        super.draw(batch);

    }

    void onTouchDown(){
        this.physicsBody.setTransform(100, 100, 0);
        Environment.level.starsTouched += 3-type;
    }

    void onDisappear(){
        this.physicsBody.setLinearVelocity(0, 1);
        if(this.physicsBody.getPosition().y > 10){
            this.physicsBody.setActive(false);
            this.physicsBody.setAwake(false);
        }
    }

}
