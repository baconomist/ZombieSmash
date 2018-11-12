package com.fcfruit.monstersmash.powerups.explodable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.interfaces.PreLevelDestroyableInterface;

/**
 * Created by Lucas on 2018-03-22.
 */

public class ExplodablePowerup implements com.fcfruit.monstersmash.entity.interfaces.PowerupInterface, PreLevelDestroyableInterface
{

    private Sprite ui_image;

    public Rope rope;
    public Explodable explodable;

    private double destroyTimer;
    private double timeBeforeDestroy = 2500;

    private boolean isActive;

    private boolean isGrenadeDetached;

    public ExplodablePowerup(String uiImagePath)
    {
        this.ui_image = new Sprite(new Texture(Gdx.files.internal(uiImagePath)));

        this.isActive = false;
        this.isGrenadeDetached = false;

        this.create();

    }

    protected void create()
    {

    }

    @Override
    public void update(float delta)
    {
        if(explodable.shouldDetach()){
            explodable.detach();
            this.isGrenadeDetached = true;
            this.destroyTimer = System.currentTimeMillis();
        }
        if(this.isGrenadeDetached && System.currentTimeMillis() - this.destroyTimer > this.timeBeforeDestroy)
        {
            this.rope.destroy();
        }
    }

    @Override
    public void destroy()
    {
        this.rope.destroy();
        this.explodable.destroy();
    }

    @Override
    public void activate()
    {
        Vector3 pos = Environment.powerupManager.getExplodablePowerupSpawnPos(this);

        Vector3 size = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.getUIDrawable().getWidth(), this.getUIDrawable().getHeight(), 0)));
        size.y = Environment.physicsCamera.position.y*2 - size.y;

        this.explodable.setPosition(new Vector2(pos.x, pos.y));
        this.explodable.getPhysicsBody().setActive(true);

        // Stick rope to top
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(new Vector2(pos.x + size.x/2, pos.y));

        this.rope.activate();
        this.rope.setPosition(new Vector2(pos.x, pos.y + 1));
        this.rope.attachToTop(Environment.physics.createBody(bodyDef));

        Environment.drawableAddQueue.add(this.explodable);
        Environment.updatableAddQueue.add(this);

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

    public boolean isGrenadeDetached(){return this.isGrenadeDetached;}

    @Override
    public Sprite getUIDrawable()
    {
        return this.ui_image;
    }
}
