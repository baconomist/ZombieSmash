package com.fcfruit.zombiesmash.powerups.grenade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-03-22.
 */

public class GrenadePowerup implements PowerupInterface
{

    private Sprite ui_image;

    private Rope rope;
    private Grenade grenade;

    private boolean isActive;

    public GrenadePowerup()
    {
        this.ui_image = new Sprite(new Texture(Gdx.files.internal("powerups/grenade/grenade_ui.png")));

        this.isActive = false;
    }

    @Override
    public void update(float delta)
    {
        if(grenade.shouldDetach()){
            grenade.detach();
        }
    }

    @Override
    public void activate()
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        CircleShape shape = new CircleShape();
        shape.setRadius(0.2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;

        Body body = Environment.physics.createBody(bodyDef);
        Fixture fixture = body.createFixture(fixtureDef);

        Vector3 pos = Environment.powerupManager.getGrenadeSpawnPosition(this);

        Vector3 size = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.getUIDrawable().getWidth(), this.getUIDrawable().getHeight(), 0)));
        size.y = Environment.physicsCamera.viewportHeight - size.y;

        // Make rope swing
        this.rope = new Rope(5, new Vector2(pos.x, pos.y + 1));

        ArrayList<Joint> joints = new ArrayList<Joint>();
        joints.add(this.rope.attachToBottom(body));

        this.grenade = new Grenade(body, joints);
        this.grenade.setPosition(new Vector2(pos.x, pos.y));

        // Make grenade easy to detach from rope
        this.grenade.setForceForDetach(20f);

        fixture.setUserData(this.grenade);

        Environment.drawableAddQueue.add(this.grenade);
        Environment.updatableAddQueue.add(this);

        // Stick rope to top
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(new Vector2(pos.x + size.x/2, pos.y));
        this.rope.attachToTop(Environment.physics.createBody(bodyDef));

        this.isActive = true;
    }

    @Override
    public boolean hasCompleted()
    {
        return false;
    }

    @Override
    public boolean isActive()
    {
        return this.isActive;
    }

    @Override
    public Sprite getUIDrawable()
    {
        return this.ui_image;
    }
}
