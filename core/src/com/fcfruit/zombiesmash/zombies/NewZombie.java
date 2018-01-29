package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.esotericsoftware.spine.attachments.PointAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.esotericsoftware.spine.attachments.VertexAttachment;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.ContainerEntity;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.InteractivePhysicsEntity;
import com.fcfruit.zombiesmash.entity.MovableEntity;
import com.fcfruit.zombiesmash.entity.OptimizableEntity;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Lucas on 2017-07-30.
 */

public class NewZombie implements DrawableEntityInterface, InteractiveEntityInterface, ContainerEntityInterface, OptimizableEntityInterface
{

    // Animations
    String moveAnimation;

    // Identifiers
    public int id;

    // Composition
    private AnimatableGraphicsEntity animatableGraphicsEntity;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;
    private OptimizableEntity optimizableEntity;
    private MovableEntity movableEntity;
    public ContainerEntity containerEntity;

    // Zombie Specific Fields
    private boolean isPhysicsEnabled;
    private int direction;
    private int speed;
    ArrayList partsToStayAlive;
    HashMap<String, Class> specialParts;

    // Get up Fields
    private double getUpTimer;
    private double timeBeforeGetup;
    private boolean isGettingUp;
    private MouseJoint getUpMouseJoint;

    public NewZombie(int id)
    {
        this.id = id;

        this.speed = 1;

        this.movableEntity = new MovableEntity(this);
        this.movableEntity.setSpeed(this.speed);
        this.containerEntity = new ContainerEntity();
        this.optimizableEntity = new OptimizableEntity(this.containerEntity);

        this.isPhysicsEnabled = false;
        this.partsToStayAlive = new ArrayList();

        this.timeBeforeGetup = 5000;
        this.isGettingUp = false;

    }

    public void setup()
    {
        // Need to have seperate function here because reflection does not work in constructor
        this.animationSetup();
        this.constructPhysicsBodies();
        this.interactiveEntitySetup();
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

        if (this.isTouching() || !this.isAnimating())
        {
            for (Slot slot : this.animatableGraphicsEntity.getSkeleton().getDrawOrder())
            {
                if (this.containerEntity.drawableEntities.get(slot.getAttachment().getName()) != null)
                {
                    this.containerEntity.drawableEntities.get(slot.getAttachment().getName()).draw(batch);
                }
            }
        } else
        {
            this.animatableGraphicsEntity.draw(batch, skeletonRenderer);
        }

    }

    @Override
    public void update(float delta)
    {
        if (this.isAlive())
        {
            this.updateEntities(delta);
            this.interactiveGraphicsEntity.update(delta);

            if (this.isTouching())
            {
                this.onTouching();
            }

            if (this.isAnimating())
            {
                this.onAnimate();
                this.movableEntity.update(delta);
            }

            if (this.isGettingUp)
            {
                this.onGetup();
            }

            this.optimizableEntity.update(delta);

            this.handleGetup();

        }

    }

    public void updateEntities(float delta)
    {
        for (DrawableEntityInterface drawableEntityInterface : this.containerEntity.drawableEntities.values())
        {
            drawableEntityInterface.update(delta);
        }
        for (InteractiveEntityInterface interactiveEntityInterface : this.containerEntity.interactiveEntities.values())
        {
            interactiveEntityInterface.update(delta);
        }
    }


    @Override
    public void onTouchDown(float screenX, float screenY, int p)
    {
        for (InteractiveEntityInterface interactiveEntity : this.containerEntity.interactiveEntities.values())
        {
            interactiveEntity.onTouchDown(screenX, screenY, p);
        }
        this.interactiveGraphicsEntity.onTouchDown(screenX, screenY, p);
    }

    @Override
    public void onTouchDragged(float screenX, float screenY, int p)
    {
        for (InteractiveEntityInterface interactiveEntity : this.containerEntity.interactiveEntities.values())
        {
            interactiveEntity.onTouchDragged(screenX, screenY, p);
        }
        this.interactiveGraphicsEntity.onTouchDragged(screenX, screenY, p);
    }

    @Override
    public void onTouchUp(float screenX, float screenY, int p)
    {
        for (InteractiveEntityInterface interactiveEntity : this.containerEntity.interactiveEntities.values())
        {
            interactiveEntity.onTouchUp(screenX, screenY, p);
        }
        this.interactiveGraphicsEntity.onTouchUp(screenX, screenY, p);
    }


    public void constructPhysicsBodies()
    {
        boolean flip = this.direction == 1;
        Gdx.app.log("flip", ""+flip);
        World world = Environment.physics.getWorld();

        RubeSceneLoader loader = new RubeSceneLoader(world);
        RubeScene rubeScene;
        if (flip)
        {
            rubeScene = loader.loadScene(Gdx.files.internal("zombies/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie_flip_rube.json"));
        } else
        {
            rubeScene = loader.loadScene(Gdx.files.internal("zombies/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie_rube.json"));
        }

        this.containerEntity.drawableEntities = new HashMap<String, DrawableEntityInterface>();
        this.containerEntity.interactiveEntities = new HashMap<String, InteractiveEntityInterface>();
        this.containerEntity.detachableEntities = new HashMap<String, DetachableEntityInterface>();

        float scale = 0;

        for (Body body : rubeScene.getBodies())
        {

            if ((Boolean) rubeScene.getCustom(body, "isPart"))
            {

                String bodyName = (String) rubeScene.getCustom(body, "name");
                Sprite sprite = new Sprite(this.animatableGraphicsEntity.getAtlas().findRegion(bodyName));

                for (RubeImage i : rubeScene.getImages())
                {
                    if (i.body == body)
                    {
                        sprite.flip(flip, false);
                        sprite.setColor(i.color);
                        sprite.setOriginCenter();
                        scale = sprite.getWidth();
                        sprite.setSize(i.width * Physics.PIXELS_PER_METER, i.height * Physics.PIXELS_PER_METER);
                        scale = sprite.getWidth() / scale;
                        sprite.setOriginCenter();
                    }

                }

                Joint joint = null;
                for (Joint j : rubeScene.getJoints())
                {
                    if (j.getBodyA() == body || j.getBodyB() == body)
                    {
                        joint = j;
                        break;
                    }
                }

                for (Fixture fixture : body.getFixtureList())
                {
                    // Makes different zombies not collide with each other
                    fixture.setUserData(this);
                }

                boolean specialPart = false;
                /*for(String name : specialParts.keySet()){
                    if(bodyName.equals(name)){
                        try
                        {
                            specialParts.get(name).getDeclaredConstructor(Integer.class).newInstance();
                            specialPart = true;
                            break;
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }*/
                if (!specialPart)
                {
                    body.setUserData("a");
                    NewPart part = new NewPart(bodyName, sprite, body, joint, this.containerEntity);
                    this.containerEntity.drawableEntities.put(bodyName, part);
                    this.containerEntity.interactiveEntities.put(bodyName, part);
                    this.containerEntity.detachableEntities.put(bodyName, part);

                }
            }

        }

        this.animatableGraphicsEntity.getSkeleton().getRootBone().setScale(scale);

        this.animatableGraphicsEntity.update(Gdx.graphics.getDeltaTime()); // Update the animation getUpTimer.

    }

    public boolean isInLevel()
    {
        boolean isInLevel = false;
        for (DrawableEntityInterface i : this.containerEntity.drawableEntities.values())
        {
            isInLevel = i.getPosition().x > Environment.physics.getWall_1().getPosition().x + 0.5f
                    && i.getPosition().x < Environment.physics.getWall_2().getPosition().x - 0.5f;
        }

        return isInLevel;

    }

    private void checkDirection()
    {
        int previous_direction = this.direction;
        if (this.getPosition().x < Environment.level.objective.getPosition().x + Environment.level.objective.getWidth() / 4)
        {
            this.direction = 0;
        } else if (this.getPosition().x < Environment.level.objective.getPosition().x + Environment.level.objective.getWidth() / 2)
        {
            this.direction = 1;
        } else if (this.getPosition().x < Environment.level.objective.getPosition().x + Environment.level.objective.getWidth() / 2 + Environment.level.objective.getWidth() / 4)
        {
            this.direction = 0;
        } else
        {
            this.direction = 1;
        }
        if (previous_direction != this.direction)
        {
            onDirectionChange();
        }
    }

    private void onDirectionChange()
    {
        this.animatableGraphicsEntity.getSkeleton().setFlipX(this.direction == 1);
        this.constructPhysicsBodies();
    }

    public void setDirection(int direction)
    {
        if (this.direction != direction)
        {
            this.direction = direction;
            onDirectionChange();
        }
    }

    public int getDirection()
    {
        return this.direction;
    }

    private boolean isAtObjective()
    {
        boolean isAtObjective = false;
        for (InteractiveEntityInterface i : this.containerEntity.interactiveEntities.values())
        {
            if (Environment.level.objective.polygon.contains(i.getPolygon().getX(), i.getPolygon().getY()))
            {
                isAtObjective = true;
            } else
            {
                isAtObjective = false;
                break;
            }
        }

        return isAtObjective;

    }

    private boolean isJustAtObjective()
    {
        return !this.isAttacking() && this.isAtObjective();
    }

    private boolean isAttacking()
    {
        return this.animatableGraphicsEntity.getAnimation().contains("attack");
    }

    private void handleGetup()
    {
        if (!this.isAnimating() && !this.isGettingUp && this.hasRequiredPartsForGetup() && System.currentTimeMillis() - getUpTimer >= timeBeforeGetup)
        {

            this.disable_optimization();

            MouseJointDef mouseJointDef = new MouseJointDef();
            // Needs 2 bodies, first one not used, so we use an arbitrary body.
            // http://www.binarytides.com/mouse-joint-box2d-javascript/
            mouseJointDef.bodyA = Environment.physics.getGround();
            mouseJointDef.bodyB = ((NewPart) this.containerEntity.interactiveEntities.get("head")).getPhysicsBody();
            mouseJointDef.collideConnected = true;
            mouseJointDef.target.set(this.containerEntity.drawableEntities.get("head").getPosition());
            // The higher the ratio, the slower the movement of body to mousejoint
            mouseJointDef.dampingRatio = 7;
            mouseJointDef.maxForce = 100000f;
            // Destroy the current mouseJoint
            if (getUpMouseJoint != null)
            {
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
            }
            getUpMouseJoint = (MouseJoint) Environment.physics.getWorld().createJoint(mouseJointDef);
            getUpMouseJoint.setTarget(new Vector2(this.containerEntity.drawableEntities.get("torso").getPosition().x, this.animatableGraphicsEntity.getSize().y));

            this.isGettingUp = true;
        }

        // -0.2f to give it wiggle room to detect get up
        if (this.isGettingUp && this.containerEntity.drawableEntities.get("head").getPosition().y >= this.getSize().y - 0.3f)
        {
            this.isGettingUp = false;
            this.isPhysicsEnabled = false;

            this.setPosition(new Vector2(this.containerEntity.drawableEntities.get("torso").getPosition().x, 0));
            if (getUpMouseJoint != null)
            {
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
                getUpMouseJoint = null;
            }

            // Restart animation
            this.animatableGraphicsEntity.restartAnimation();
        }

    }

    private void onGetup()
    {
        this.disable_optimization();
    }

    private boolean isAlive()
    {

        boolean isAlive = true;
        for (Object o : partsToStayAlive)
        {
            if (o instanceof String)
            {
                if (this.containerEntity.detachableEntities.get(o) != null)
                {
                    isAlive = true;
                } else
                {
                    isAlive = false;
                    break;
                }
            } else if (o instanceof String[])
            {
                for (String s : (String[]) o)
                {
                    if (this.containerEntity.detachableEntities.get(s) != null)
                    {
                        isAlive = true;
                        break;
                    } else
                    {
                        isAlive = false;
                    }
                }
            }
        }

        if (!isAlive)
        {
            this.onDeath();
        }

        return isAlive;
    }

    private boolean isAnimating()
    {
        return !this.isTouching() && !this.isPhysicsEnabled;
    }


    private void onTouching()
    {
        if (this.getUpMouseJoint != null)
        {
            Environment.physics.getWorld().destroyJoint(this.getUpMouseJoint);
            this.getUpMouseJoint = null;
        }
        this.movableEntity.clear();
        this.isPhysicsEnabled = true;
        this.isGettingUp = false;
        this.getUpTimer = System.currentTimeMillis();
    }

    private void onAnimate()
    {
        this.detachAnimationLimbs();

        if (this.isJustAtObjective())
        {
            this.onObjectiveOnce();
        } else if (this.isAtObjective())
        {
            this.onObjective();
        }

        if (!this.isTouching())
        {
            this.syncEntitiesToAnimation();
        }

        this.animatableGraphicsEntity.update(Gdx.graphics.getDeltaTime());

        this.getUpTimer = System.currentTimeMillis();
    }

    private void syncEntitiesToAnimation()
    {
        for (String key : this.containerEntity.drawableEntities.keySet())
        {
            //float y = ((float) Math.sin(this.animatableGraphicsEntity.getSkeleton().findBone(key).getWorldRotationX())*(((RegionAttachment)this.animatableGraphicsEntity.getSkeleton().findSlot(key).getAttachment()).getHeight()/2*this.animatableGraphicsEntity.getSkeleton().getRootBone().getWorldScaleY()));
            //float x = ((float) Math.cos(this.animatableGraphicsEntity.getSkeleton().findBone(key).getWorldRotationX()));
            if(key.equals("head")){
                float x = this.animatableGraphicsEntity.getSkeleton().findBone("head").getWorldX();
                // X and Y are inverted for parent/local cords
                //That's why im using getX instead of getY for the pointattachment
                float y  = ((PointAttachment)this.animatableGraphicsEntity.getSkeleton().getAttachment("head", "pos")).getX()*this.animatableGraphicsEntity.getSkeleton().getRootBone().getWorldScaleY() + this.animatableGraphicsEntity.getSkeleton().findBone("head").getWorldY();

                Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(x, y, 0)));
                pos.y = Environment.physicsCamera.viewportHeight - pos.y;
                this.containerEntity.drawableEntities.get(key).setPosition(new Vector2(pos.x, pos.y));
                this.containerEntity.drawableEntities.get(key).setAngle(this.animatableGraphicsEntity.getSkeleton().findBone(key).getWorldRotationX());

                Gdx.app.log("fafafa", ""+y + " " +((PointAttachment)this.animatableGraphicsEntity.getSkeleton().getAttachment("head", "pos")).getY() + " " + this.animatableGraphicsEntity.getSkeleton().findBone("head").getWorldY());
            }
            else
            {
                Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(this.animatableGraphicsEntity.getSkeleton().findBone(key).getWorldX(),
                        this.animatableGraphicsEntity.getSkeleton().findBone(key).getWorldY(), 0)));
                pos.y = Environment.physicsCamera.viewportHeight - pos.y;
                this.containerEntity.drawableEntities.get(key).setPosition(new Vector2(pos.x, pos.y));
                this.containerEntity.drawableEntities.get(key).setAngle(this.animatableGraphicsEntity.getSkeleton().findBone(key).getWorldRotationX());
            }
        }
    }

    private void detachAnimationLimbs()
    {
        // Do not draw detached parts in animation
        for (Slot s : this.animatableGraphicsEntity.getSkeleton().getSlots())
        {
            // If the part is detached...
            if (!s.getData().getName().equals("bounding_box") && this.containerEntity.drawableEntities.get(s.getData().getName()) == null)
            {
                // Replace current attachment with a new empty one
                s.setAttachment(new Attachment(" ")
                {
                    @Override
                    public String getName()
                    {
                        return super.getName();
                    }
                });
            }
        }
    }

    private void onObjectiveOnce()
    {
        if (this.direction == 0)
        {
            this.movableEntity.moveBy(new Vector2(new Random().nextInt((int) this.getDistanceToObjective()), 0));
        } else
        {
            this.movableEntity.moveBy(new Vector2(-new Random().nextInt((int) this.getDistanceToObjective()), 0));
        }
    }

    private void onObjective()
    {
        if (this.animatableGraphicsEntity.timesAnimationCompleted() > 2)
        {
            this.animatableGraphicsEntity.setAnimation("attack2");
        } else
        {
            this.animatableGraphicsEntity.setAnimation("attack1");
        }
    }

    private void onAttack1Complete()
    {
        Environment.level.objective.takeDamage(0.5f);
    }

    private void onAttack2Complete()
    {
        Environment.level.objective.takeDamage(1f);
    }

    private float getDistanceToObjective()
    {
        if (this.direction == 0)
        {
            return Math.abs(Environment.level.objective.getPosition().x + ((Environment.level.objective.getWidth() / 2) * 3 / 4) - this.getPosition().x);
        }
        return Math.abs(Environment.level.objective.getPosition().x + ((Environment.level.objective.getWidth() / 2) * 6 / 4) - this.getPosition().x);
    }

    private boolean hasRequiredPartsForGetup()
    {
        return this.containerEntity.interactiveEntities.get("head") != null && this.containerEntity.interactiveEntities.get("left_leg") != null
                && this.containerEntity.interactiveEntities.get("right_leg") != null;
    }

    private void interactiveEntitySetup()
    {
        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.viewportHeight - size.y;
        Polygon polygon = new Polygon(new float[]{0, 0, size.x, 0, size.x, size.y, 0, size.y});
        polygon.setOrigin(size.x / 2, size.y / 2);
        this.interactiveGraphicsEntity = new InteractiveGraphicsEntity(this.animatableGraphicsEntity, polygon);
    }

    private void animationSetup()
    {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("zombies/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("zombies/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

        state.addListener(new AnimationState.AnimationStateAdapter()
        {
            @Override
            public void complete(AnimationState.TrackEntry entry)
            {
                onAnimationComplete(entry);
                super.complete(entry);
            }
        });

        this.animatableGraphicsEntity = new AnimatableGraphicsEntity(skeleton, state, atlas);
        this.animatableGraphicsEntity.setAnimation(this.moveAnimation);
    }


    private void onAnimationComplete(AnimationState.TrackEntry entry)
    {
        if (entry.getAnimation().getName().equals("attack"))
        {
            this.onAttack1Complete();
        } else if (entry.getAnimation().getName().equals("attack2"))
        {
            this.onAttack2Complete();
        }

        if (entry.getAnimation().getName().equals(this.moveAnimation))
        {
            if (this.direction == 0)
            {
                this.movableEntity.moveBy(new Vector2(1f, 0));
            } else
            {
                this.movableEntity.moveBy(new Vector2(-1f, 0));
            }
        }

    }

    private void onDeath()
    {
    }

    @Override
    public void dispose()
    {

    }

    @Override
    public Vector2 getPosition()
    {
        if (this.isPhysicsEnabled)
        {
            return this.containerEntity.drawableEntities.get("torso").getPosition();
        } else
        {
            return this.animatableGraphicsEntity.getPosition();
        }
    }

    @Override
    public void setPosition(Vector2 position)
    {
        if (this.isPhysicsEnabled)
        {
            this.containerEntity.drawableEntities.get("torso").setPosition(position);
        } else
        {
            this.animatableGraphicsEntity.setPosition(position);
        }
    }

    @Override
    public float getAngle()
    {
        return this.animatableGraphicsEntity.getAngle();
    }

    @Override
    public void setAngle(float angle)
    {
        this.animatableGraphicsEntity.setAngle(angle);
    }

    @Override
    public Vector2 getSize()
    {
        return this.animatableGraphicsEntity.getSize();
    }

    @Override
    public boolean isTouching()
    {
        for (InteractiveEntityInterface interactiveEntity : this.containerEntity.interactiveEntities.values())
        {
            if (interactiveEntity.isTouching())
            {
                return true;
            }
        }
        return this.interactiveGraphicsEntity.isTouching();
    }

    public Polygon getPolygon()
    {
        return this.interactiveGraphicsEntity.getPolygon();
    }

    @Override
    public void detach(DetachableEntityInterface detachableEntityInterface)
    {
        this.containerEntity.detach(detachableEntityInterface);
    }

    @Override
    public void enable_optimization()
    {
        this.optimizableEntity.enable_optimization();
    }

    @Override
    public void disable_optimization()
    {
        this.optimizableEntity.disable_optimization();
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch)
    {

    }

}