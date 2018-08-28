package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.attachments.PointAttachment;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.effects.BleedBlood;
import com.fcfruit.zombiesmash.entity.interfaces.BleedableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Lucas on 2018-02-02.
 */

public class BleedablePoint implements BleedableEntityInterface
{
    public String blood_pos_name;

    private float rotOffset;
    private float physics_h;
    private Vector2 physics_offset;
    private Vector2 complete_physics_pos;
    private Body physicsBody;

    //private DrawableGraphicsEntity bodypartBlood;
    private float blood_pos_rot_offset;

    private boolean isBleeding;

    private double bleedTime = 5000;
    private double bleedAccumilator = 0d;
    private double timeBeforeBlood;
    private double bloodAccumilator = 0d;

    private boolean initUpdate = true;

    /**
     * Child code
     **/
    private BleedablePoint parentBleedablePoint;

    public void setParent(BleedablePoint parent)
    {
        this.parentBleedablePoint = parent;
    }

    /**
     * Parent constructor
     **/
    public BleedablePoint(PointAttachment physics_pos, PointAttachment blood_pos, Bone bone, Body physicsBody, float animScale)
    {
        this.create(physics_pos, blood_pos, bone, physicsBody, animScale);
    }

    /**
     * General create method as to not copy and paste code
     **/
    private void create(PointAttachment physics_pos, PointAttachment blood_pos, Bone bone, Body physicsBody, float animScale)
    {
        this.blood_pos_name = blood_pos.getName();

        this.physicsBody = physicsBody;

        Vector2 spine_pos = physics_pos.computeWorldPosition(bone, new Vector2(0, 0)).sub(blood_pos.computeWorldPosition(bone, new Vector2(0, 0)));
        float x = spine_pos.x;
        float y = spine_pos.y;

        Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(x, y, 0)));
        pos.y = Environment.physicsCamera.position.y * 2 - pos.y;

        this.physics_h = (float) Math.sqrt(pos.x * pos.x + pos.y * pos.y);

        this.isBleeding = false;

        //this.rotOffset = this.physicsBody.getAngle() - (float)Math.atan2(y, x);
        this.rotOffset = 0;
        this.calc_phys_pos();
        float angle = (float) Math.atan2(this.physics_offset.y, this.physics_offset.x);
        this.rotOffset = angle - (float) Math.atan2(y, x);


        //this.bodypartBlood = new DrawableGraphicsEntity(new Sprite(new Texture(Gdx.files.internal("effects/blood/blood.png"))));
        //this.bodypartBlood.getSprite().setScale(animScale);

        this.blood_pos_rot_offset = (float) Math.toDegrees(this.physicsBody.getAngle()) - blood_pos.computeWorldRotation(bone); // - 180; // -180 because pointAttachments have a different "0 degrees" than everything else

    }

    /**
     * Calculate the position for blood to spawn at relative to the physicsBody
     **/
    private void calc_phys_pos()
    {
        this.physics_offset = new Vector2(this.physics_h * (float) Math.cos(this.physicsBody.getAngle() - rotOffset), this.physics_h * (float) Math.sin(this.physicsBody.getAngle() - rotOffset));
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        //if(this.isBleeding)
        //this.bodypartBlood.draw(batch);
    }

    @Override
    public void update(float delta)
    {
        if (this.isBleeding)
        {
            this.calc_phys_pos();

            this.complete_physics_pos = this.physicsBody.getPosition();
            this.complete_physics_pos.sub(this.physics_offset);

            this.updateBlood(delta);
        } else
        {
            this.bloodAccumilator = 0d;
        }
    }

    /**
     * Create blood accordingly
     **/
    private void updateBlood(float delta)
    {
        //this.bodypartBlood.setPosition(this.complete_physics_pos.sub(this.bodypartBlood.getSize().scl(0.5f)));
        //this.bodypartBlood.setAngle((float) Math.toDegrees(this.physicsBody.getAngle()) + this.blood_pos_rot_offset);

        if (initUpdate)
        {
            this.bleedAccumilator = 0d;
            this.bloodAccumilator = 0d;
            this.initUpdate = false;
        }

        this.bleedAccumilator += Math.min(delta, 0.25f);
        this.bloodAccumilator += Math.min(delta, 0.25f);

        this.timeBeforeBlood = 200 + this.bleedAccumilator*this.bleedAccumilator / 10000;

        if (this.bleedAccumilator*1000 < this.bleedTime && this.bloodAccumilator*1000 > this.timeBeforeBlood)
        {

            Environment.drawableBackgroundAddQueue.add(Environment.bleedableBloodPool.getBlood(this.complete_physics_pos, this.physics_offset));

            this.bloodAccumilator = 0d;
        } else if (this.bleedAccumilator*1000 > this.bleedTime)
            this.isBleeding = false;
    }

    /**
     * Used when you don't want blood particles but instead blood on the body
     **/
    /*public void enable_body_blood()
    {
        this.initUpdate = false;
        this.enable_bleeding();
    }*/
    @Override
    public void enable_bleeding()
    {
        this.isBleeding = true;
        if (this.parentBleedablePoint != null)
            this.parentBleedablePoint.enable_bleeding();
    }

    @Override
    public void disable_bleeding()
    {
        this.isBleeding = false;
        if (this.parentBleedablePoint != null)
            this.parentBleedablePoint.disable_bleeding();
    }

}
