package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.DetachableEntity;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.InteractivePhysicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-07-30.
 */

public class NewZombie implements DrawableEntityInterface, InteractiveEntityInterface
{

    private int id;
    private String type;

    private AnimatableGraphicsEntity animatableGraphicsEntity;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;

    // Name, Item
    private HashMap<String, DrawableEntityInterface> drawableEntities;
    private HashMap<String, InteractiveEntityInterface> interactiveEntities;
    private HashMap<String, DetachableEntityInterface> detachableEntities;

    public NewZombie(int id, AnimatableGraphicsEntity animatableGraphicsEntity, InteractiveGraphicsEntity interactiveGraphicsEntity){
        this.id = id;

        this.animatableGraphicsEntity = animatableGraphicsEntity;
        this.interactiveGraphicsEntity = interactiveGraphicsEntity;

    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer) {
        this.animatableGraphicsEntity.draw(batch, skeletonRenderer);

       /*for (Slot slot : this.animatableGraphicsEntity.getSkeleton().getDrawOrder()) {
            if (parts.get(slot.getAttachment().getName()) != null) {
                parts.get(slot.getAttachment().getName()).draw(batch);
            }
        }
        if (!physicsEnabled) {
            skeletonRenderer.draw(batch, skeleton);
        }*/

    }

    @Override
    public void update(float delta) {
        this.animatableGraphicsEntity.update(delta);
    }


    @Override
    public void onTouchDown(float x, float y, int p)
    {
        this.interactiveGraphicsEntity.onTouchDown(x, y, p);
    }

    @Override
    public void onTouchDragged(float x, float y, int p)
    {
        this.interactiveGraphicsEntity.onTouchDragged(x, y, p);
    }

    @Override
    public void onTouchUp(float x, float y, int p)
    {
        this.interactiveGraphicsEntity.onTouchUp(x, y, p);
    }


    public void constructPhysicsBody(World world, boolean flip){
        RubeSceneLoader loader = new RubeSceneLoader(world);
        RubeScene rubeScene;
        if(flip){
            rubeScene = loader.loadScene(Gdx.files.internal("zombies/"+this.type+"_zombie/"+this.type+"_zombie_flip_rube.json"));
        }
        else {
            rubeScene = loader.loadScene(Gdx.files.internal("zombies/"+this.type+"_zombie/"+this.type+"_zombie_rube.json"));
        }

        this.drawableEntities = new HashMap<String, DrawableEntityInterface>();
        this.interactiveEntities = new HashMap<String, InteractiveEntityInterface>();
        this.detachableEntities = new HashMap<String, DetachableEntityInterface>();

        float scale = 0;

        for(Body b : rubeScene.getBodies()) {

            if((Boolean) rubeScene.getCustom(b, "isPart")) {

                String bodyName = (String) rubeScene.getCustom(b, "name");
                Sprite sprite = new Sprite(this.animatableGraphicsEntity.getAtlas().findRegion(bodyName));

                for (RubeImage i : rubeScene.getImages()) {
                    if (i.body == b) {
                        sprite.flip(flip, false);
                        sprite.setColor(i.color);
                        sprite.setOriginCenter();
                        scale = sprite.getWidth();
                        sprite.setSize(i.width * Physics.PIXELS_PER_METER, i.height * Physics.PIXELS_PER_METER);
                        scale = sprite.getWidth()/scale;
                        sprite.setOriginCenter();
                    }

                }

                Joint joint = null;
                for(Joint j : rubeScene.getJoints()){
                    if(j.getBodyA() == b || j.getBodyB() == b){
                        joint = j;
                        break;
                    }
                }

                for (Fixture f : b.getFixtureList()) {
                    // Makes different zombies not collide with each other
                    f.setUserData(this);
                }

                drawableEntities.put(bodyName, new NewPart(bodyName, sprite, b, joint));
            }

        }

        this.animatableGraphicsEntity.getSkeleton().getRootBone().setScale(scale);

        this.animatableGraphicsEntity.update(Gdx.graphics.getDeltaTime()); // Update the animation getUpTimer.

        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.viewportHeight - size.y;

        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x/2, size.y/2);
        this.interactiveGraphicsEntity = new InteractiveGraphicsEntity(this.animatableGraphicsEntity, polygon);


    }

    @Override
    public Vector2 getPosition() {
        return this.animatableGraphicsEntity.getPosition();
    }

    @Override
    public void setPosition(Vector2 position) {
        this.animatableGraphicsEntity.setPosition(position);
    }

    @Override
    public float getAngle() {
        return this.animatableGraphicsEntity.getAngle();
    }

    @Override
    public void setAngle(float angle) {
        this.animatableGraphicsEntity.setAngle(angle);
    }

    @Override
    public Vector2 getSize() {
        return this.animatableGraphicsEntity.getSize();
    }

    @Override
    public boolean isTouching()
    {
        return this.interactiveGraphicsEntity.isTouching();
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch) {

    }

}