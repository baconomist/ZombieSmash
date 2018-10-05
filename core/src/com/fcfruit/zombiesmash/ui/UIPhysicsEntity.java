package com.fcfruit.zombiesmash.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.fcfruit.zombiesmash.Environment;

public class UIPhysicsEntity extends Group
{
    private Actor actor;
    private Body physicsBody;

    private Vector3 pos;

    public UIPhysicsEntity(Actor actor, Body physicsBody)
    {
        super();
        this.actor = actor;
        this.addActor(actor);

        this.physicsBody = physicsBody;
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);
        this.actor.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);

        pos = this.getStage().getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(this.physicsBody.getPosition(), 0)));
        pos.y = this.getStage().getViewport().getCamera().position.y*2-pos.y;
        this.actor.setPosition(pos.x - this.actor.getWidth()/2, pos.y - this.actor.getHeight()/2);
        this.actor.setRotation((float)Math.toDegrees(this.physicsBody.getAngle()));
    }

}
