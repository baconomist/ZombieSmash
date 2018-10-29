package com.fcfruit.zombiesmash.powerups.explodable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.physics.PhysicsData;

import java.util.ArrayList;

public class GrenadePowerup extends ExplodablePowerup
{
    public GrenadePowerup()
    {
        super("powerups/explodable/grenade/grenade_ui.png");
    }

    @Override
    protected void create()
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;

        CircleShape shape = new CircleShape();
        shape.setRadius(0.2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0f;

        Body body = Environment.physics.createBody(bodyDef);
        Fixture fixture = body.createFixture(fixtureDef);

        // Make rope swing
        this.rope = new Rope(5, new Vector2(99, 99));

        ArrayList<Joint> joints = new ArrayList<Joint>();
        joints.add(this.rope.attachToBottom(body));

        this.explodable = new Grenade(body, joints);
        this.explodable.getPhysicsBody().setActive(false);
        this.explodable.setPosition(new Vector2(99, 99));
        this.explodable.getExplodableEntity().explosionRadiusX = this.explodable.getExplodableEntity().explosionRadiusX*(Environment.Prefs.upgrades.getInteger("grenade", 1)/20f);
        this.explodable.getExplodableEntity().explosionRadiusY = this.explodable.getExplodableEntity().explosionRadiusY*(Environment.Prefs.upgrades.getInteger("grenade", 1)/10f);

        // Make explodable easy to detach from rope
        this.explodable.setForceForDetach(20f);

        fixture.setUserData(new PhysicsData(this.explodable));
    }
}
