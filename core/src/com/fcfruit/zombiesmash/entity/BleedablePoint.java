package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.attachments.PointAttachment;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.effects.BleedBlood;
import com.fcfruit.zombiesmash.entity.interfaces.BleedableEntityInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-02-02.
 */

public class BleedablePoint implements BleedableEntityInterface
{
    private float rotOffset;
    private float physics_h;
    private Vector2 physics_offset;
    private Body physicsBody;

    private boolean isBleeding;

    private ArrayList<BleedBlood> blood;
    private double bleedTime = 5000;
    private double bleedTimer;
    private double timeBeforeBlood;
    private double bloodTimer;

    private boolean initUpdate = true;

    DrawableGraphicsEntity drawableGraphicsEntity = new DrawableGraphicsEntity(new Sprite(new Texture(Gdx.files.internal("badlogic.jpg"))));

    public BleedablePoint(PointAttachment physics_pos, PointAttachment blood_pos, Bone bone, Body physicsBody)
    {
        this.physicsBody = physicsBody;

        Vector2 spine_pos = physics_pos.computeWorldPosition(bone, new Vector2(0, 0)).sub(blood_pos.computeWorldPosition(bone, new Vector2(0, 0)));
        float x = spine_pos.x;
        float y = spine_pos.y;

        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(x, y, 0)));
        pos.y = Environment.physicsCamera.position.y*2 - pos.y;

        this.physics_h = (float)Math.sqrt(pos.x*pos.x + pos.y*pos.y);

        this.blood = new ArrayList<BleedBlood>();
        this.isBleeding = false;

        //this.rotOffset = this.physicsBody.getAngle() - (float)Math.atan2(y, x);
        this.rotOffset = 0;
        this.calc_phys_pos();
        float angle = (float)Math.atan2(this.physics_offset.y, this.physics_offset.x);
        this.rotOffset = angle - (float)Math.atan2(y, x);

        drawableGraphicsEntity.getSprite().setSize(10, 10);
        Environment.drawableAddQueue.add(this.drawableGraphicsEntity);

    }

    private void calc_phys_pos()
    {
        this.physics_offset = new Vector2(this.physics_h*(float)Math.cos(this.physicsBody.getAngle() - rotOffset), this.physics_h*(float)Math.sin(this.physicsBody.getAngle() - rotOffset));
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        for (BleedBlood blood : this.blood)
        {
            blood.draw(batch);
        }
    }

    @Override
    public void update(float delta)
    {
        this.calc_phys_pos();

        Vector2 complete_physics_pos = this.physicsBody.getPosition().sub(this.physics_offset);
        this.drawableGraphicsEntity.setPosition(complete_physics_pos);

        ArrayList<BleedBlood> copy = new ArrayList<BleedBlood>();
        for (BleedBlood blood : this.blood)
        {
            copy.add(blood);
        }
        for (BleedBlood blood : copy)
        {
            if (blood.readyForDestroy)
            {
                blood.destroy();
                this.blood.remove(blood);
            }
        }

        this.updateBlood();
    }

    private void updateBlood()
    {
        if (this.isBleeding)
        {
            if(initUpdate)
            {
                this.bleedTimer = System.currentTimeMillis();
                this.bloodTimer = System.currentTimeMillis();
                this.initUpdate = false;
            }

            this.timeBeforeBlood = 200+(System.currentTimeMillis() - this.bleedTimer)*(System.currentTimeMillis() - this.bleedTimer)/10000;

            if (System.currentTimeMillis() - this.bleedTimer < this.bleedTime && System.currentTimeMillis() - this.bloodTimer > this.timeBeforeBlood)
            {

                //this.blood.add(new BleedBlood(this.physicsBody.getPosition().x, this.physicsBody.getPosition().y, this.physicsBody.getPosition().x, this.physicsBody.getPosition().y, this.rotation));

                this.bloodTimer = System.currentTimeMillis();
            }
        } else
        {
            this.bloodTimer = System.currentTimeMillis();
        }
    }

    @Override
    public void enable_bleeding()
    {
        this.isBleeding = true;
        Environment.drawableAddQueue.add(this.drawableGraphicsEntity);
    }

    @Override
    public void disable_bleeding()
    {
        this.isBleeding = false;
    }
}
