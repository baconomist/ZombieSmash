package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-07-30.
 */

public class Body{

    public Zombie zombie;

    public Sprite head;
    public Sprite torso;
    public Sprite leftArm;
    public Sprite rightArm;
    public Sprite leftFoot;
    public Sprite rightFoot;

    public ArrayList parts;

    public ArrayList body;

    private Vector2 pos;

    private float rotation;

    public float mass;

    public Body(ArrayList prts, Zombie z){
        //head = (Part)prts.get(0);
        //torso = (Part)prts.get(1);
        //leftArm = (Part)prts.get(2);
        //rightArm = (Part)prts.get(3);
        //leftFoot = (Part)prts.get(4);
        //rightFoot = (Part)prts.get(5);

        parts = new ArrayList();
        parts.add(head);
        parts.add(torso);
        parts.add(leftArm);
        parts.add(rightArm);
        parts.add(leftFoot);
        parts.add(rightFoot);

        body = new ArrayList();
        body.add(head);
        body.add(torso);
        body.add(leftArm);
        body.add(rightArm);
        body.add(leftFoot);
        body.add(rightFoot);

        pos = new Vector2(0, 0);

        rotation = 0;

        mass = 68;

        construct();

    }

    private void construct(){
        leftFoot.setPosition(pos.x, pos.y);
        rightFoot.setPosition(leftFoot.getWidth(), leftFoot.getY());
        torso.setPosition(leftFoot.getX() + leftFoot.getWidth(), leftFoot.getHeight());
        head.setPosition(torso.getX() + torso.getWidth()/2, torso.getY() + torso.getHeight());
        leftArm.setPosition(torso.getX() - leftArm.getWidth(), torso.getY());
        rightArm.setPosition(torso.getX() + rightArm.getWidth(), torso.getY());
    }

    private void rotate(){
        //https://academo.org/demos/rotation-about-point/
        //https://www.mathsisfun.com/sine-cosine-tangent.html

        torso.setOrigin(head.getX() - torso.getX() ,head.getY() - torso.getY());
        torso.setRotation(rotation);

        leftArm.setOrigin(head.getX() - torso.getX() ,head.getY() - torso.getY());
        leftArm.setRotation(rotation);

        rightArm.setOrigin(head.getX() - torso.getX() ,head.getY() - torso.getY());
        rightArm.setRotation(rotation);

        leftFoot.setOrigin(head.getX() - torso.getX() ,head.getY() - torso.getY());
        leftFoot.setRotation(rotation);

        rightFoot.setOrigin(head.getX() - torso.getX() ,head.getY() - torso.getY());
        rightFoot.setRotation(rotation);

        //torso.setPosition((float)(x*(Math.cos(rotation) - y*Math.sin(rotation))), (float)(y*(Math.cos(rotation) + x*Math.sin(rotation))));

        head.rotate(rotation);

    }

    public void updateGravity(float delta){
        //((Part)body.get(0)).update(delta);
        construct();
    }

    public void setPosition(float x, float y){
        pos.x = x;
        pos.y = y;
        construct();
    }

    public void setRotation(float rot){
        rotation = rot;
        rotate();
    }

    public Vector2 getPosition(){
        return pos;
    }    public float getRotation(){
        return rotation;
    }

    public float getX(){
        return leftFoot.getX();
    }
    public float getY(){
        return leftFoot.getY();
    }

    public float getWidth(){
        return leftArm.getWidth() + torso.getWidth() + rightArm.getWidth();
    }

    public float getHeight(){
        return leftFoot.getHeight() + torso.getHeight() + head.getHeight();
    }

    public void removePart(int part){
        body.remove(part);
    }

    public void addPart(int part){
        body.add(parts.get(part));
    }

    public void draw(Batch batch){
        batch.draw(leftFoot, leftFoot.getX(), leftFoot.getY());
        batch.draw(rightFoot, rightFoot.getX(), rightFoot.getY());
        batch.draw(leftArm, leftArm.getX(), leftArm.getY());
        batch.draw(rightArm, rightArm.getX(), rightArm.getY());
        batch.draw(torso, torso.getX(), torso.getY());
        batch.draw(head, head.getX(), head.getY());
    }



}
