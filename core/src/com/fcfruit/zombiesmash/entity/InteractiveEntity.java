package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by Lucas on 2018-01-06.
 */

public class InteractiveEntity implements DrawableEntityInterface, InteractiveEntityInterface {
    private DrawableEntity drawableEntity;
    
    private boolean isTouching;
    private int pointer;
    private Polygon polygon;
    private MouseJoint mouseJoint;
    

    public InteractiveEntity(DrawableEntity drawableEntity){
        this.drawableEntity = drawableEntity;
        this.isTouching = false;
        this.pointer = -1;

        polygon = new Polygon();
        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize().x, this.getSize().y, 0)));
        polygon.setVertices(new float[]{0, 0, size.x, 0, size.x, Environment.gameCamera.viewportHeight - size.y,
                0, Environment.gameCamera.viewportHeight - size.y});
        polygon.setOrigin(size.x/2, (Environment.gameCamera.viewportHeight - size.y)/2);

    }

    void createMouseJoint(float x, float y){

        /*
        To make dragging faster:

        myObjectBody.applyForceToCenter(new Vector2((float)Math.cos(myMouseDirectionAngle)
        forceYouWantToApply, (float)Math.sin(myMouseDirectionAngle) * forceYouWantToApply));
        */


        MouseJointDef mouseJointDef = new MouseJointDef();
        // Needs 2 bodies, first one not used, so we use an arbitrary body.
        // http://www.binarytides.com/mouse-joint-box2d-javascript/
        mouseJointDef.bodyA = Environment.physics.getGround();
        mouseJointDef.bodyB = this.getPhysicsBody();
        mouseJointDef.collideConnected = true;
        mouseJointDef.target.set(this.drawableEntity.getPosition());

        // Makes the joint move body faster
        //mouseJointDef.frequencyHz = 10;

        // Idk what this does, may want to play with it
        // Default is 0.7, I do know that it makes things
        // A lot slower for the mousejoint though when you set it to 10
        //mouseJointDef.dampingRatio = 10;

        // Makes the joint move body faster
        mouseJointDef.dampingRatio = 0.1f;
        
        // Force applied to body to get to point
        mouseJointDef.maxForce = 10000f * this.getPhysicsBody().getMass();

        // Destroy the current mouseJoint
        if(mouseJoint != null){
            Environment.physics.getWorld().destroyJoint(mouseJoint);
        }
        mouseJoint = (MouseJoint) Environment.physics.getWorld().createJoint(mouseJointDef);
        mouseJoint.setTarget(new Vector2(x, y));

        this.getPhysicsBody().setAwake(true);
    }

    @Override
    public void onTouchDown(float x, float y, int p) {
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(x, y, 0)));
        if(polygon.contains(pos.x, Environment.gameCamera.viewportHeight - pos.y)){
            isTouching = true;
            pointer = p;
        }
        if(mouseJoint == null && isTouching) {
            createMouseJoint(x, y);
        }
    }

    @Override
    public void onTouchDragged(float x, float y, int p) {
        if(pointer == p){

        }
    }

    @Override
    public void onTouchUp(float x, float y, int p) {
        if(pointer == p){
            Environment.physics.getWorld().destroyJoint(mouseJoint);
            pointer = -1;
            isTouching = false;
        }
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
    public Body getPhysicsBody() {
        return this.getPhysicsBody();
    }
}
