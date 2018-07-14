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
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.PointAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.BleedablePoint;
import com.fcfruit.zombiesmash.entity.ContainerEntity;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.MovableEntity;
import com.fcfruit.zombiesmash.entity.MultiGroundEntity;
import com.fcfruit.zombiesmash.entity.OptimizableEntity;
import com.fcfruit.zombiesmash.entity.interfaces.AnimatableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.MovableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.MultiGroundEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;
import com.fcfruit.zombiesmash.zombies.parts.Part;
import com.fcfruit.zombiesmash.zombies.parts.Torso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Lucas on 2017-07-30.
 */

public class Zombie implements DrawableEntityInterface, InteractiveEntityInterface,
        ContainerEntityInterface, OptimizableEntityInterface,
        AnimatableEntityInterface, MovableEntityInterface, MultiGroundEntityInterface
{

    /**
     * Animation
     **/
    String moveAnimation;

    /**
     * Identifier
     **/
    public int id;

    /**
     * Composition
     **/
    private AnimatableGraphicsEntity animatableGraphicsEntity;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;
    private OptimizableEntity optimizableEntity;
    private MovableEntity movableEntity;
    private MultiGroundEntity multiGroundEntity;
    public ContainerEntity containerEntity;

    /**
     * Zombie Specific Fields
     **/
    private boolean shouldObjectiveOnce;
    private int direction;
    private int speed;
    ArrayList detachableEntitiesToStayAlive;
    ArrayList<String> currentParts;
    private float animScale = 0;

    /**
     * Getup fields (need to make getupable entity)
     **/
    private double getUpTimer;
    private double timeBeforeGetup;
    private boolean isGettingUp;
    private MouseJoint getUpMouseJoint;

    /**
     * Zombie
     **/
    public Zombie(int id)
    {
        this.id = id;

        this.speed = 1;

        this.movableEntity = new MovableEntity(this);
        this.multiGroundEntity = new MultiGroundEntity(this, this);
        this.setSpeed(this.speed);
        this.containerEntity = new ContainerEntity();
        this.optimizableEntity = new OptimizableEntity(null, null, this);

        this.shouldObjectiveOnce = true;
        this.detachableEntitiesToStayAlive = new ArrayList();
        this.currentParts = new ArrayList<String>();

        this.timeBeforeGetup = 5000;
        this.isGettingUp = false;

        this.enable_optimization();

    }

    /**
     * Init
     **/
    public void setup()
    {
        // Need to have separate function here because reflection does not work in constructor
        this.animationSetup();
        this.constructBody();
        this.interactiveEntitySetup();
    }

    private void calc_anim_scale(RubeScene rubeScene)
    {

        float height = 0;
        for (RubeImage i : rubeScene.getImages())
        {
            height += i.height * Physics.PIXELS_PER_METER;
        }

        float height2 = 0;
        for (Slot slot : this.animatableGraphicsEntity.getSkeleton().getSlots())
        {
            if (slot.getAttachment() instanceof RegionAttachment)
                height2 += ((RegionAttachment) slot.getAttachment()).getHeight();
        }

        this.animScale = height / height2;


        Gdx.app.log("calc_anim_scale", ""+this.animScale);
    }

    public void constructBody()
    {
        boolean flip = this.direction == 1;
        World world = Environment.physics.getWorld();

        com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader loader = new com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader(world);
        RubeScene rubeScene;
        if (flip)
        {
            rubeScene = loader.loadScene(Gdx.files.internal("zombies/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie_flip_rube.json"));
        } else
        {
            rubeScene = loader.loadScene(Gdx.files.internal("zombies/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie_rube.json"));
        }

        // Remove old entities from world
        if (this.getDrawableEntities() != null)
        {
            for (DrawableEntityInterface drawableEntity : this.getDrawableEntities().values())
            {
                drawableEntity.dispose();
                if (drawableEntity instanceof PhysicsEntityInterface)
                {
                    Environment.physics.destroyBody(((PhysicsEntityInterface) drawableEntity).getPhysicsBody());
                }
            }
        }

        this.setDrawableEntities(new HashMap<String, DrawableEntityInterface>());
        this.setInteractiveEntities(new HashMap<String, InteractiveEntityInterface>());
        this.setDetachableEntities(new HashMap<String, DetachableEntityInterface>());

        // Only set animScale once because later on when you loop through the slots of the anim
        // the scale is inaccurate because you remove certain slots when parts detach
        if(this.animScale == 0)
        {
            this.calc_anim_scale(rubeScene);
        }

        // Update the animation
        this.animatableGraphicsEntity.getSkeleton().setFlipX(flip);
        this.animatableGraphicsEntity.getSkeleton().getRootBone().setScale(this.animScale);
        this.animatableGraphicsEntity.update(Gdx.graphics.getDeltaTime());

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
                        sprite.setSize(i.width * Physics.PIXELS_PER_METER, i.height * Physics.PIXELS_PER_METER);
                        sprite.setOriginCenter();
                    }

                }

                ArrayList<Joint> joints = new ArrayList<Joint>();
                for (Joint joint : rubeScene.getJoints())
                {
                    // Only detachableEntity/Child gets the joints
                    if (joint.getBodyB() == body)
                    {
                        joints.add(joint);
                    }
                }

                for (Fixture fixture : body.getFixtureList())
                {
                    // Makes different zombies not collide with each other
                    fixture.setUserData(this);
                }

                Array<Attachment> attachments = new Array<Attachment>();
                this.animatableGraphicsEntity.getSkeleton().getData().getDefaultSkin().findAttachmentsForSlot(this.animatableGraphicsEntity.getSkeleton().findSlot(bodyName).getData().getIndex(), attachments);


                Array<Attachment> blood_pos_attachments = new Array<Attachment>();
                for(int i = 0; i < attachments.size; i++)
                {
                    if(attachments.get(i).getName().contains("blood_pos"))
                        blood_pos_attachments.add(attachments.get(i));
                }

                BleedablePoint[] bleedablePoints = new BleedablePoint[blood_pos_attachments.size];

                for (int i = 0; i < blood_pos_attachments.size; i++)
                {
                    Attachment attachment = blood_pos_attachments.get(i);

                    // Match Body Pos And Rotation To Spine For BloodPoint Calculations
                    Vector2 vec = ((PointAttachment) this.animatableGraphicsEntity.getSkeleton().getAttachment(bodyName, "physics_pos")).computeWorldPosition(this.animatableGraphicsEntity.getSkeleton().findBone(bodyName), new Vector2(0, 0));
                    Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(vec.x, vec.y, 0)));
                    pos.y = Environment.physicsCamera.position.y*2 - pos.y;
                    body.setTransform(new Vector2(pos.x, pos.y), 0);
                    if(this.animatableGraphicsEntity.getSkeleton().getFlipX())
                        body.setTransform(body.getPosition(), (float)Math.toRadians(this.animatableGraphicsEntity.getSkeleton().findBone(bodyName).getWorldRotationX() + 180 - ((RegionAttachment) this.animatableGraphicsEntity.getSkeleton().findSlot(bodyName).getAttachment()).getRotation()));
                    else
                        body.setTransform(body.getPosition(), (float)Math.toRadians(this.animatableGraphicsEntity.getSkeleton().findBone(bodyName).getWorldRotationX() + ((RegionAttachment)this.animatableGraphicsEntity.getSkeleton().findSlot(bodyName).getAttachment()).getRotation()));

                    // Create Bleed Point
                    bleedablePoints[i] = new BleedablePoint((PointAttachment) this.animatableGraphicsEntity.getSkeleton().getAttachment(bodyName, "physics_pos"),
                            ((PointAttachment) attachment), this.animatableGraphicsEntity.getSkeleton().findBone(bodyName), body);

                }

                // If we still have the part
                // If not, we destroy the body loaded into the world by rube
                // Prevents invisible bodies for when parts are detached from the zombie
                if (this.currentParts.contains(bodyName))
                    this.createPart(body, bodyName, sprite, joints, this, bleedablePoints);
                else
                    Environment.physics.destroyBody(body);

            }
        }

    }

    /**
     * Zombie subclasses should override this method
     **/
    protected void createPart(Body physicsBody, String bodyName, Sprite sprite, ArrayList<Joint> joints, ContainerEntityInterface containerEntity, BleedablePoint[] bleedablePoints)
    {
        physicsBody.setUserData(bodyName);
        // If child
        if (joints.size() > 0)
        {
            Part part = new Part(bodyName, sprite, physicsBody, joints, containerEntity, bleedablePoints[0]);
            this.getDrawableEntities().put(bodyName, part);
            this.getInteractiveEntities().put(bodyName, part);
            this.getDetachableEntities().put(bodyName, part);
        }
        // If parent
        else if (bodyName.equals("torso"))
        {
            Torso torso = new Torso(bodyName, sprite, physicsBody, containerEntity, bleedablePoints);
            this.getDrawableEntities().put(bodyName, torso);
            this.getInteractiveEntities().put(bodyName, torso);
        }
    }

    private void interactiveEntitySetup()
    {
        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.position.y*2 - size.y;
        Polygon polygon = new Polygon(new float[]{0, 0, size.x*2, 0, size.x*2, size.y*1.5f, 0, size.y*1.5f});
        polygon.setOrigin(size.x, 0);
        this.interactiveGraphicsEntity = new InteractiveGraphicsEntity(this.animatableGraphicsEntity, polygon);
    }

    private void animationSetup()
    {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("zombies/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("zombies/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        //state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

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

    /**
     * Booleans
     **/
    public boolean isInLevel()
    {
        /*boolean isInLevel = false;
        for (DrawableEntityInterface i : this.getDrawableEntities().values())
        {
            isInLevel = i.getPosition().x > Environment.physics.getWall_1().getPosition().x + 0.1f
                    && i.getPosition().x < Environment.physics.getWall_2().getPosition().x - 0.1f;
        }

        return isInLevel;*/

        boolean isInLevel = false;
        for (DrawableEntityInterface i : this.getDrawableEntities().values())
        {
            isInLevel = i.getPosition().x > Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2
                    && i.getPosition().x < Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth / 2;
        }
        return isInLevel;

    }

    private boolean isAtObjective()
    {
        boolean isAtObjective = false;
        for (InteractiveEntityInterface i : this.getInteractiveEntities().values())
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

        return isAtObjective && this.isInLevel();

    }

    private boolean isAttacking()
    {
        return this.animatableGraphicsEntity.getCurrentAnimation().contains("attack");
    }

    public boolean isAlive()
    {

        boolean isAlive = true;
        for (Object o : this.detachableEntitiesToStayAlive)
        {
            if (o instanceof String)
            {
                if (this.getDetachableEntities().get(o) != null)
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
                    if (this.getDetachableEntities().get(s) != null)
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
        return !this.isTouching() && this.isOptimizationEnabled();
    }

    private boolean hasRequiredPartsForGetup()
    {
        return this.getDrawableEntities().get("head") != null && this.getDrawableEntities().get("left_leg") != null
                && this.getDrawableEntities().get("right_leg") != null;
    }

    /**
     * Misc.
     **/
    public void updateEntities(float delta)
    {
        for (DrawableEntityInterface drawableEntityInterface : this.getDrawableEntities().values())
        {
            drawableEntityInterface.update(delta);
        }
        for (InteractiveEntityInterface interactiveEntityInterface : this.getInteractiveEntities().values())
        {
            interactiveEntityInterface.update(delta);
        }
    }

    private void checkDirection()
    {

        int previous_direction = this.direction;

        if (this.getPosition().x < Environment.level.objective.getPosition().x + Environment.level.objective.getWidth() / 4)
        {
            this.direction = 0;
        } else if (this.getPosition().x < Environment.level.objective.getPosition().x + Environment.level.objective.getWidth() / 2)
        {
            if (Environment.level.getCurrentCameraPosition().equals("right"))
            {
                this.direction = 0;
            } else this.direction = 1;
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

    private void handleGetup()
    {
        if (!this.isAnimating() && !this.isGettingUp && this.hasRequiredPartsForGetup() && System.currentTimeMillis() - getUpTimer >= timeBeforeGetup)
        {

            MouseJointDef mouseJointDef = new MouseJointDef();
            // Needs 2 bodies, first one not used, so we use an arbitrary body.
            // http://www.binarytides.com/mouse-joint-box2d-javascript/
            mouseJointDef.bodyA = Environment.physics.getGroundBodies().get(0);
            mouseJointDef.bodyB = ((Part) this.getInteractiveEntities().get("head")).getPhysicsBody();
            mouseJointDef.collideConnected = true;
            mouseJointDef.target.set(this.getDrawableEntities().get("head").getPosition());
            // The higher the ratio, the slower the movement of body to mousejoint
            mouseJointDef.dampingRatio = 7;
            mouseJointDef.maxForce = 100000f;
            // Destroy the current mouseJoint
            if (getUpMouseJoint != null)
            {
                Environment.physics.destroyJoint(getUpMouseJoint);
            }
            getUpMouseJoint = (MouseJoint) Environment.physics.createJoint(mouseJointDef);
            getUpMouseJoint.setTarget(new Vector2(this.getDrawableEntities().get("torso").getPosition().x,
                    this.animatableGraphicsEntity.getSize().y + Environment.physics.getGroundBodies().get(this.getInitialGround()).getPosition().y));

            this.isGettingUp = true;

            this.onGetupStart();

        }

        // -0.3f to give it wiggle room to detect get up
        if (this.isGettingUp && this.getDrawableEntities().get("head").getPosition().y >= this.getSize().y - 0.3f + Environment.physics.getGroundBodies().get(this.getInitialGround()).getPosition().y)
        {
            this.onGetupEnd();
        }

    }

    /*private boolean isGettingUp(){
         return this.isPhysicsEnabled && System.currentTimeMillis() - this.getUpTimer > this.timeBeforeGetup;
     }*/

    public void stopGetUp()
    {

        if (this.getUpMouseJoint != null && !Environment.jointDestroyQueue.contains(this.getUpMouseJoint))
        {
            Environment.jointDestroyQueue.add(this.getUpMouseJoint);
        }
        this.getUpTimer = System.currentTimeMillis();
        this.isGettingUp = false;

    }

    private void syncEntitiesToAnimation()
    {
        for (String key : this.getDrawableEntities().keySet())
        {

            Vector2 vec = ((PointAttachment) this.animatableGraphicsEntity.getSkeleton().getAttachment(key, "physics_pos")).computeWorldPosition(this.animatableGraphicsEntity.getSkeleton().findBone(key), new Vector2(0, 0));

            Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(vec.x, vec.y, 0)));
            pos.y = Environment.physicsCamera.position.y*2 - pos.y;
            this.getDrawableEntities().get(key).setPosition(new Vector2(pos.x, pos.y));
            if(this.animatableGraphicsEntity.getSkeleton().getFlipX())
                this.getDrawableEntities().get(key).setAngle(this.animatableGraphicsEntity.getSkeleton().findBone(key).getWorldRotationX() + 180 - ((RegionAttachment) this.animatableGraphicsEntity.getSkeleton().findSlot(key).getAttachment()).getRotation());
            else
                this.getDrawableEntities().get(key).setAngle(this.animatableGraphicsEntity.getSkeleton().findBone(key).getWorldRotationX() + ((RegionAttachment)this.animatableGraphicsEntity.getSkeleton().findSlot(key).getAttachment()).getRotation());
        }
    }

    private void detachAnimationLimbs()
    {
        // Do not draw detached parts in animation
        for (Slot s : this.animatableGraphicsEntity.getSkeleton().getSlots())
        {
            // If the part is detached...
            if (!s.getData().getName().equals("bounding_box") && this.getDrawableEntities().get(s.getData().getName()) == null)
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

    // May need to be changed to some more general system in the future(using points in data files or something)
    private float getDistanceToObjective()
    {
        if (Environment.level.getCurrentCameraPosition().equals("left"))
        {
            return Math.abs(Environment.level.objective.getPosition().x + ((Environment.level.objective.getWidth() / 2) * 3 / 4) - this.getPosition().x);
        }
        else if(Environment.level.getCurrentCameraPosition().equals("middle") && this.direction == 0)
        {
            return Math.abs(Environment.level.objective.getPosition().x + ((Environment.level.objective.getWidth() / 2) * 3 / 4) - this.getPosition().x);
        }
        return Math.abs(Environment.level.objective.getPosition().x + ((Environment.level.objective.getWidth() / 2) * 6 / 4) - this.getPosition().x);

    }

    /**
     * Events
     **/
    private void onAnimationComplete(AnimationState.TrackEntry entry)
    {
        if (entry.getAnimation().getName().equals("attack1"))
        {
            this.onAttack1Complete();
        } else if (entry.getAnimation().getName().equals("attack2"))
        {
            this.onAttack2Complete();
        }

        if (entry.getAnimation().getName().equals(this.moveAnimation) && !this.isAtObjective())
        {
            if (this.direction == 0)
            {
                this.moveBy(new Vector2(1f, 0));
            } else
            {
                this.moveBy(new Vector2(-1f, 0));
            }
        }

    }

    private void onDeath()
    {
        this.getPolygon().setPosition(9999, 9999);
    }

    private void onObjectiveOnce()
    {

        float move = new Random().nextFloat() * (int) this.getDistanceToObjective();

        this.clearMoveQueue();

        if (this.direction == 0)
        {
            this.moveBy(new Vector2(move, 0));
        } else
        {
            this.moveBy(new Vector2(-move, 0));
        }
        this.changeToGround(new Random().nextInt(1) + 1);

        this.shouldObjectiveOnce = false;
    }

    protected void onObjective()
    {
        if (this.timesAnimationCompleted() >= 2 && this.getCurrentAnimation().contains("attack"))
        {
            this.setAnimation("attack2");
        } else if (this.timesAnimationCompleted() >= 1)
        {
            this.setAnimation("attack1");
        }
    }

    private void onGetupStart()
    {
        this.disable_optimization();
    }

    private void onGetupEnd()
    {
        this.setAnimation(this.moveAnimation);
        this.shouldObjectiveOnce = true;

        this.isGettingUp = false;
        this.enable_optimization();

        if (getUpMouseJoint != null)
        {
            Environment.physics.destroyJoint(getUpMouseJoint);
            getUpMouseJoint = null;
        }

        this.animatableGraphicsEntity.setPosition(new Vector2(this.getDrawableEntities().get("torso").getPosition().x, Environment.physics.getGroundBodies().get(this.getInitialGround()).getPosition().y));

        // Restart animation
        this.animatableGraphicsEntity.restartAnimation();

        this.checkDirection();

    }

    private void onDirectionChange()
    {
        this.constructBody();
    }

    private void onAnimate()
    {
        this.detachAnimationLimbs();

        if (this.isAtObjective() && this.shouldObjectiveOnce)
        {
            this.onObjectiveOnce();
        } else if (this.isAtObjective() && !this.isMoving())
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

    private void onPhysicsEnabled()
    {
        this.resetToInitialGround();
        this.clearMoveQueue();
    }

    private void onTouching()
    {
        if (this.getUpMouseJoint != null)
        {
            Environment.physics.destroyJoint(this.getUpMouseJoint);
            this.getUpMouseJoint = null;
        }
        this.clearMoveQueue();
        this.disable_optimization();
        this.isGettingUp = false;
        this.getUpTimer = System.currentTimeMillis();
    }

    protected void onAttack1Complete()
    {
        Environment.level.objective.takeDamage(0.5f);
    }

    protected void onAttack2Complete()
    {
        Environment.level.objective.takeDamage(1f);
    }


    /**
     * DrawableEntity
     **/
    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {

        if (this.isAnimating())
        {
            this.animatableGraphicsEntity.draw(batch, skeletonRenderer);
        } else
        {
            for (Slot slot : this.animatableGraphicsEntity.getSkeleton().getDrawOrder())
            {
                if (this.getDrawableEntities().get(slot.getAttachment().getName()) != null)
                {
                    this.getDrawableEntities().get(slot.getAttachment().getName()).draw(batch);
                    this.getDrawableEntities().get(slot.getAttachment().getName()).draw(batch, skeletonRenderer);
                }
            }
        }

    }

    @Override
    public void update(float delta)
    {
        this.updateEntities(delta);
        if (this.isAlive())
        {
            this.interactiveGraphicsEntity.update(delta);

            if (this.isTouching())
            {
                this.onTouching();
            }

            if (this.isAnimating())
            {
                this.onAnimate();
                this.movableEntity.update(delta);
                this.multiGroundEntity.update(delta);
            } else
            {
                this.onPhysicsEnabled();
                this.animatableGraphicsEntity.setPosition(new Vector2(this.getPosition().x, this.getPosition().y - this.getSize().y/2));
                this.animatableGraphicsEntity.update(delta);
            }

            this.optimizableEntity.update(delta);

            this.handleGetup();
        }

    }

    @Override
    public Vector2 getPosition()
    {
        if (!this.isAnimating())
        {
            return this.getDrawableEntities().get("torso").getPosition();
        } else
        {
            return this.animatableGraphicsEntity.getPosition();
        }
    }

    @Override
    public void setPosition(Vector2 position)
    {
        if (!this.isAnimating())
        {
            this.getDrawableEntities().get("torso").setPosition(position);
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


    /**
     * InteractiveEntity
     **/
    @Override
    public void onTouchDown(float screenX, float screenY, int p)
    {
        boolean touching = false;
        for (InteractiveEntityInterface interactiveEntity : this.getInteractiveEntities().values())
        {
            interactiveEntity.onTouchDown(screenX, screenY, p);
            if (interactiveEntity.isTouching())
            {
                touching = true;
            }
        }
        this.interactiveGraphicsEntity.onTouchDown(screenX, screenY, p);

        if (this.isTouching() && !touching && this.isAlive() && this.isInLevel())
        {
            ((InteractivePhysicsEntityInterface) this.getInteractiveEntities().get("torso")).overrideTouching(true, screenX, screenY, p);
        }
    }

    @Override
    public void onTouchDragged(float screenX, float screenY, int p)
    {
        for (InteractiveEntityInterface interactiveEntity : this.getInteractiveEntities().values())
        {
            interactiveEntity.onTouchDragged(screenX, screenY, p);
        }
        this.interactiveGraphicsEntity.onTouchDragged(screenX, screenY, p);
    }

    @Override
    public void onTouchUp(float screenX, float screenY, int p)
    {
        for (InteractiveEntityInterface interactiveEntity : this.getInteractiveEntities().values())
        {
            interactiveEntity.onTouchUp(screenX, screenY, p);
        }
        this.interactiveGraphicsEntity.onTouchUp(screenX, screenY, p);
    }

    @Override
    public boolean isTouching()
    {
        for (InteractiveEntityInterface interactiveEntity : this.getInteractiveEntities().values())
        {
            if (interactiveEntity.isTouching())
            {
                return true;
            }
        }
        return this.interactiveGraphicsEntity.isTouching();
    }

    @Override
    public Polygon getPolygon()
    {
        return this.interactiveGraphicsEntity.getPolygon();
    }


    /**
     * OptimizableEntity
     **/
    @Override
    public void enable_optimization()
    {
        this.optimizableEntity.enable_optimization();
    }

    @Override
    public void disable_optimization()
    {
        this.onPhysicsEnabled();
        this.optimizableEntity.disable_optimization();
    }

    @Override
    public boolean isOptimizationEnabled()
    {
        return this.optimizableEntity.isOptimizationEnabled();
    }

    /**
     * ContainerEntity
     **/
    @Override
    public void detach(DetachableEntityInterface detachableEntityInterface)
    {
        for(String name : this.getDetachableEntities().keySet())
        {
            if(this.getDetachableEntities().get(name).equals(detachableEntityInterface))
                this.currentParts.remove(name);
        }
        this.containerEntity.detach(detachableEntityInterface);
    }

    @Override
    public HashMap<String, DrawableEntityInterface> getDrawableEntities()
    {
        return this.containerEntity.getDrawableEntities();
    }

    @Override
    public HashMap<String, InteractiveEntityInterface> getInteractiveEntities()
    {
        return this.containerEntity.getInteractiveEntities();
    }

    @Override
    public HashMap<String, DetachableEntityInterface> getDetachableEntities()
    {
        return this.containerEntity.getDetachableEntities();
    }

    @Override
    public void setDrawableEntities(HashMap<String, DrawableEntityInterface> drawableEntities)
    {
        this.containerEntity.setDrawableEntities(drawableEntities);
    }

    @Override
    public void setInteractiveEntities(HashMap<String, InteractiveEntityInterface> interactiveEntities)
    {
        this.containerEntity.setInteractiveEntities(interactiveEntities);
    }

    @Override
    public void setDetachableEntities(HashMap<String, DetachableEntityInterface> detachableEntities)
    {
        this.containerEntity.setDetachableEntities(detachableEntities);
    }

    @Override
    public void moveBy(Vector2 moveBy)
    {
        this.movableEntity.moveBy(moveBy);
    }

    @Override
    public void moveTo(Vector2 moveTo)
    {
        this.movableEntity.moveTo(moveTo);
    }

    @Override
    public boolean isMoving()
    {
        return this.movableEntity.isMoving();
    }

    @Override
    public void setSpeed(float speed)
    {
        this.movableEntity.setSpeed(speed);
    }

    @Override
    public void clearMoveQueue()
    {
        this.movableEntity.clearMoveQueue();
    }

    @Override
    public void setAnimation(String animation)
    {
        this.animatableGraphicsEntity.setAnimation(animation);
    }

    @Override
    public String getCurrentAnimation()
    {
        return this.animatableGraphicsEntity.getCurrentAnimation();
    }

    @Override
    public int timesAnimationCompleted()
    {
        return this.animatableGraphicsEntity.timesAnimationCompleted();
    }

    @Override
    public void changeToGround(int ground)
    {
        this.multiGroundEntity.changeToGround(ground);
    }

    @Override
    public int getCurrentGround()
    {
        return this.multiGroundEntity.getCurrentGround();
    }

    @Override
    public void resetToInitialGround()
    {
        this.multiGroundEntity.resetToInitialGround();
    }

    @Override
    public int getInitialGround()
    {
        return this.multiGroundEntity.getInitialGround();
    }

    @Override
    public void setInitialGround(int initialGround)
    {
        this.multiGroundEntity.setInitialGround(initialGround);
    }

    @Override
    public boolean isMovingToNewGround()
    {
        return this.multiGroundEntity.isMovingToNewGround();
    }

    @Override
    public float getAlpha()
    {
        return this.animatableGraphicsEntity.getAlpha();
    }

    @Override
    public void setAlpha(float alpha)
    {
        this.animatableGraphicsEntity.setAlpha(alpha);
        for(DrawableEntityInterface drawableEntityInterface : this.containerEntity.getDrawableEntities().values())
        {
            drawableEntityInterface.setAlpha(alpha);
        }
    }

    @Override
    public void dispose()
    {

    }

    // Unused
    @Override
    public void draw(SpriteBatch batch)
    {

    }

}