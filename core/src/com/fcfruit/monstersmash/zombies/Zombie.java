package com.fcfruit.monstersmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
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
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.PointAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.effects.BodyFire;
import com.fcfruit.monstersmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.monstersmash.entity.BleedablePoint;
import com.fcfruit.monstersmash.entity.ContainerEntity;
import com.fcfruit.monstersmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.monstersmash.entity.MovableEntity;
import com.fcfruit.monstersmash.entity.MultiGroundEntity;
import com.fcfruit.monstersmash.entity.OptimizableEntity;
import com.fcfruit.monstersmash.entity.interfaces.AnimatableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.BurnableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.MovableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.MultiGroundEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.PhysicsEntityInterface;
import com.fcfruit.monstersmash.physics.Physics;
import com.fcfruit.monstersmash.physics.PhysicsData;
import com.fcfruit.monstersmash.rube.RubeScene;
import com.fcfruit.monstersmash.rube.loader.serializers.utils.RubeImage;
import com.fcfruit.monstersmash.zombies.parts.Part;
import com.fcfruit.monstersmash.zombies.parts.Torso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Lucas on 2017-07-30.
 */

public class Zombie implements DrawableEntityInterface, InteractiveEntityInterface,
        ContainerEntityInterface, OptimizableEntityInterface,
        AnimatableEntityInterface, MovableEntityInterface, MultiGroundEntityInterface,
        com.fcfruit.monstersmash.entity.interfaces.BurnableEntityInterface
{

    /**
     * Animation
     **/
    String moveAnimation;
    private Array<String> drawOrder;

    /**
     * Identifier
     **/
    public int id;

    /**
     * Composition
     **/
    private com.fcfruit.monstersmash.entity.AnimatableGraphicsEntity animatableGraphicsEntity;
    private com.fcfruit.monstersmash.entity.InteractiveGraphicsEntity interactiveGraphicsEntity;
    private com.fcfruit.monstersmash.entity.OptimizableEntity optimizableEntity;
    private com.fcfruit.monstersmash.entity.MovableEntity movableEntity;
    private com.fcfruit.monstersmash.entity.MultiGroundEntity multiGroundEntity;
    public com.fcfruit.monstersmash.entity.ContainerEntity containerEntity;
    protected com.fcfruit.monstersmash.effects.BodyFire bodyFire;

    /**
     * Zombie Specific Fields
     **/
    private HashMap<String, Array<com.fcfruit.monstersmash.entity.BleedablePoint>> bleedablePoints;
    protected boolean shouldObjectiveOnce;
    private int direction;
    private float speed;
    private float moveDistance;
    ArrayList detachableEntitiesToStayAlive;
    ArrayList<String> currentParts;
    private float animScale = 0;
    private boolean onDeathFired = false;

    /**
     * Getup fields (need to make getupable entity)
     **/
    private double getUpTimer;
    protected double timeBeforeGetup;
    private double maxGetupTimer;
    private double maxGetupTime;
    private boolean isGettingUp;
    private MouseJoint getUpMouseJoint;

    /**
     * Status fields, for optimization
     **/
    private boolean isAlive;
    private boolean isTouching;
    private boolean isAnimating;

    /**
     * Zombie
     **/
    public Zombie(int id)
    {

        // Identifier
        this.id = id;

        // Composition
        this.movableEntity = new com.fcfruit.monstersmash.entity.MovableEntity(this);
        this.multiGroundEntity = new com.fcfruit.monstersmash.entity.MultiGroundEntity(this, this);
        this.containerEntity = new com.fcfruit.monstersmash.entity.ContainerEntity();
        this.optimizableEntity = new com.fcfruit.monstersmash.entity.OptimizableEntity(null, null, this);

        // Zombie Specific Fields
        this.shouldObjectiveOnce = true;
        this.speed = 1;
        this.moveDistance = 0.5f;
        this.detachableEntitiesToStayAlive = new ArrayList();
        this.currentParts = new ArrayList<String>();

        // Getup Fields
        this.timeBeforeGetup = 5000;
        this.maxGetupTime = 3000;
        this.isGettingUp = false;

        // Status fields
        this.isAlive = true; // Zombie is alive by default :)
        this.isAnimating = true; // Zombie starts in an animating state
        this.isTouching = false;

        // Optimize zombie
        this.enable_optimization();

        // Set Zombie Speed While Moving/Animating
        this.setSpeed(this.speed);

    }

    /**
     * Init
     **/
    public void setup(int direction)
    {
        this.direction = direction;

        // Need to have separate function here because reflection does not work in constructor
        this.animationSetup();
        this.constructBody();
        this.animationListenerSetup(); // Has to be done after construct body to stop crashing
        this.interactiveEntitySetup();
        this.drawOrderSetup();
        this.optimizableEntity.setHeight(this.getSize().y);

        this.returnEntitiesToOptimizedLocation();
    }

    private void calc_anim_scale(RubeScene rubeScene)
    {

        float height = 0;
        for (com.fcfruit.monstersmash.rube.loader.serializers.utils.RubeImage i : rubeScene.getImages())
        {
            height += i.height * com.fcfruit.monstersmash.physics.Physics.PIXELS_PER_METER;
        }

        float height2 = 0;
        for (Slot slot : this.animatableGraphicsEntity.getSkeleton().getSlots())
        {
            if (slot.getAttachment() instanceof RegionAttachment)
                height2 += ((RegionAttachment) slot.getAttachment()).getHeight();
        }

        this.animScale = height / height2;
    }

    private HashMap<String, Array<com.fcfruit.monstersmash.entity.BleedablePoint>> create_bleedable_points(RubeScene rubeScene)
    {
        HashMap<String, Array<com.fcfruit.monstersmash.entity.BleedablePoint>> bleedable_points = new HashMap<String, Array<com.fcfruit.monstersmash.entity.BleedablePoint>>();

        for (Body body : rubeScene.getBodies())
        {
            if ((Boolean) rubeScene.getCustom(body, "isPart"))
            {
                String bodyName = (String) rubeScene.getCustom(body, "name");

                Array<Attachment> attachments = new Array<Attachment>();
                this.animatableGraphicsEntity.getSkeleton().getData().getDefaultSkin().findAttachmentsForSlot(this.animatableGraphicsEntity.getSkeleton().findSlot(bodyName).getData().getIndex(), attachments);

                Array<Attachment> blood_pos_attachments = new Array<Attachment>();
                for (int i = 0; i < attachments.size; i++)
                {
                    if (attachments.get(i).getName().contains("blood_pos"))
                        blood_pos_attachments.add(attachments.get(i));
                }

                Array<com.fcfruit.monstersmash.entity.BleedablePoint> bleedablePoints = new Array<com.fcfruit.monstersmash.entity.BleedablePoint>();

                for (int i = 0; i < blood_pos_attachments.size; i++)
                {
                    // If part is detached...
                    if (this.animatableGraphicsEntity.getSkeleton().findSlot(bodyName).getAttachment() == null)
                        continue;

                    Attachment attachment = blood_pos_attachments.get(i);

                    // Match Body Pos And Rotation To Spine For BloodPoint Calculations
                    Vector2 vec = ((PointAttachment) this.animatableGraphicsEntity.getSkeleton().getAttachment(bodyName, "physics_pos")).computeWorldPosition(this.animatableGraphicsEntity.getSkeleton().findBone(bodyName), new Vector2(0, 0));
                    Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(vec.x, vec.y, 0)));
                    pos.y = Environment.physicsCamera.position.y * 2 - pos.y;
                    body.setTransform(new Vector2(pos.x, pos.y), 0);
                    if (this.animatableGraphicsEntity.getSkeleton().getFlipX())
                        body.setTransform(body.getPosition(), (float) Math.toRadians(this.animatableGraphicsEntity.getSkeleton().findBone(bodyName).getWorldRotationX() + 180 - ((RegionAttachment) this.animatableGraphicsEntity.getSkeleton().findSlot(bodyName).getAttachment()).getRotation()));
                    else
                        body.setTransform(body.getPosition(), (float) Math.toRadians(this.animatableGraphicsEntity.getSkeleton().findBone(bodyName).getWorldRotationX() + ((RegionAttachment) this.animatableGraphicsEntity.getSkeleton().findSlot(bodyName).getAttachment()).getRotation()));

                    // Create Bleed Point
                    bleedablePoints.add(new com.fcfruit.monstersmash.entity.BleedablePoint((PointAttachment) this.animatableGraphicsEntity.getSkeleton().getAttachment(bodyName, "physics_pos"),
                            ((PointAttachment) attachment), this.animatableGraphicsEntity.getSkeleton().findBone(bodyName), body, this.animScale));


                }

                bleedable_points.put(bodyName, bleedablePoints);

            }
        }

        return bleedable_points;
    }

    public void constructBody()
    {
        boolean flip = this.direction == 1;
        World world = Environment.physics.getWorld();

        com.fcfruit.monstersmash.rube.loader.RubeSceneLoader loader = new com.fcfruit.monstersmash.rube.loader.RubeSceneLoader(world);
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
        if (this.animScale == 0)
        {
            this.calc_anim_scale(rubeScene);
        }

        // Update the animation
        this.animatableGraphicsEntity.getSkeleton().setFlipX(flip);
        this.animatableGraphicsEntity.getSkeleton().getRootBone().setScale(this.animScale);
        this.animatableGraphicsEntity.update(Gdx.graphics.getDeltaTime()); // May cause crash @warning, need this for blood to work properly

        this.bleedablePoints = this.create_bleedable_points(rubeScene);
        for (String part : bleedablePoints.keySet())
        {
            if (bleedablePoints.get(part).size > 1)
            {
                for (com.fcfruit.monstersmash.entity.BleedablePoint bleedablePoint : bleedablePoints.get(part))
                {
                    /*
                     * TODO:
                     * TODO:
                     * TODO:
                     *   - Make this work for parts which may have more than 1 bleed position but are still children
                     * */
                    if (bleedablePoints.get(bleedablePoint.blood_pos_name.replace("blood_pos_", "")) != null)
                    {
                        if (bleedablePoints.get(bleedablePoint.blood_pos_name.replace("blood_pos_", "")).size > 0)
                            bleedablePoints.get(bleedablePoint.blood_pos_name.replace("blood_pos_", "")).get(0).setParent(bleedablePoint);
                    }
                    //else
                    //bleedablePoint.enable_body_blood();
                }
            }
        }

        for (Body body : rubeScene.getBodies())
        {
            // Disable body(for faster loading, before optimizableEntity kicks in.)
            body.setActive(false);
            body.setAwake(false);
            // Move body out of screen
            body.setTransform(99, 99, body.getAngle());

            if ((Boolean) rubeScene.getCustom(body, "isPart") && this.currentParts.contains((String) rubeScene.getCustom(body, "name")))
            {
                String bodyName = (String) rubeScene.getCustom(body, "name");
                Sprite sprite = new Sprite(((RegionAttachment) this.animatableGraphicsEntity.getSkeleton().findSlot(bodyName).getAttachment()).getRegion());

                for (com.fcfruit.monstersmash.rube.loader.serializers.utils.RubeImage i : rubeScene.getImages())
                {
                    if (i.body == body)
                    {
                        sprite.flip(flip, false);
                        sprite.setColor(i.color);
                        sprite.setOriginCenter();
                        sprite.setSize(i.width * com.fcfruit.monstersmash.physics.Physics.PIXELS_PER_METER, i.height * com.fcfruit.monstersmash.physics.Physics.PIXELS_PER_METER);
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
                    fixture.setUserData(new com.fcfruit.monstersmash.physics.PhysicsData(this));
                }


                // If we still have the part
                // If not, we destroy the body loaded into the world by rube
                // Prevents invisible bodies for when parts are detached from the zombie
                if (this.currentParts.contains(bodyName))
                    this.createPart(body, bodyName, sprite, joints, this, bleedablePoints.get(bodyName));
                else
                    Environment.physics.destroyBody(body);

            } else
            {
                Environment.physics.destroyBody(body);
            }
        }

    }

    /**
     * Zombie subclasses should override this method
     **/
    protected void createPart(Body physicsBody, String bodyName, Sprite sprite, ArrayList<Joint> joints, ContainerEntityInterface containerEntity, Array<com.fcfruit.monstersmash.entity.BleedablePoint> bleedablePoints)
    {
        physicsBody.setUserData(new com.fcfruit.monstersmash.physics.PhysicsData(this));
        // If child
        if (joints.size() > 0)
        {
            if (bleedablePoints.size < 1)
                Gdx.app.error("Zombie Creation", "Add Bleed Points to Animation In The Form Of 'blood_pos' As Attachment Name! Part: " + bodyName);
            com.fcfruit.monstersmash.zombies.parts.Part part = new com.fcfruit.monstersmash.zombies.parts.Part(bodyName, sprite, physicsBody, joints, containerEntity, bleedablePoints.get(0));
            this.getDrawableEntities().put(bodyName, part);
            this.getInteractiveEntities().put(bodyName, part);
            this.getDetachableEntities().put(bodyName, part);
        }
        // If parent
        else if (bodyName.equals("torso"))
        {
            com.fcfruit.monstersmash.zombies.parts.Torso torso = new com.fcfruit.monstersmash.zombies.parts.Torso(bodyName, sprite, physicsBody, containerEntity, bleedablePoints);
            this.getDrawableEntities().put(bodyName, torso);
            this.getInteractiveEntities().put(bodyName, torso);
        }
    }

    private void interactiveEntitySetup()
    {
        Vector3 size = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getSize(), 0)));
        size.y = Environment.gameCamera.position.y * 2 - size.y;
        Polygon polygon = new Polygon(new float[]{0, 0, size.x * 2, 0, size.x * 2, size.y * 1.5f, 0, size.y * 1.5f});
        polygon.setOrigin(size.x, 0);
        this.interactiveGraphicsEntity = new com.fcfruit.monstersmash.entity.InteractiveGraphicsEntity(this.animatableGraphicsEntity, polygon);
    }

    protected void animationSetup()
    {
        TextureAtlas atlas = Environment.assets.get("zombies/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie.atlas", TextureAtlas.class);
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("zombies/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getSimpleName().replace("Zombie", "").toLowerCase() + "_zombie.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        //state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

        this.animatableGraphicsEntity = new com.fcfruit.monstersmash.entity.AnimatableGraphicsEntity(skeleton, state, atlas);
        this.animatableGraphicsEntity.setAnimation(this.moveAnimation);
    }

    private void animationListenerSetup()
    {
        this.getState().addListener(new AnimationState.AnimationStateAdapter()
        {
            @Override
            public void interrupt(AnimationState.TrackEntry entry)
            {
                onAnimationInterrupt(entry);
                super.interrupt(entry);
            }

            @Override
            public void event(AnimationState.TrackEntry entry, Event event)
            {
                onAnimationEvent(entry, event);
                super.event(entry, event);
            }

            @Override
            public void complete(AnimationState.TrackEntry entry)
            {
                onAnimationComplete(entry);
                super.complete(entry);
            }
        });
    }

    private void drawOrderSetup()
    {
        this.drawOrder = new Array<String>();
        for (Slot slot : this.animatableGraphicsEntity.getSkeleton().getDrawOrder())
        {
            if (slot.getAttachment() != null && this.getDrawableEntities().get(slot.toString()) != null)
            {
                this.drawOrder.add(slot.toString());
            }
        }
    }

    /**
     * Booleans( Status )
     **/
    private boolean isGettingUp()
    {
        return this.isGettingUp;
    }

    private boolean isInPlayableRange()
    {
        return this.getPosition().x > Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2 - 4f - this.getSize().x
                && this.getPosition().x < Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth / 2 + 4f + this.getSize().x;
    }

    public boolean isInLevel()
    {
        /*boolean isInLevel = false;
        for (DrawableEntityInterface i : this.getDrawableEntities().values())
        {
            isInLevel = i.getPosition().x > Environment.physics.getWall_1().getPosition().x + 0.1f
                    && i.getPosition().x < Environment.physics.getWall_2().getPosition().x - 0.1f;
        }

        return isInLevel;*/

        /*boolean isInLevel = false;
        for (DrawableEntityInterface i : this.getDrawableEntities().values())
        {
            isInLevel = i.getPosition().x > Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2
                    && i.getPosition().x < Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth / 2
                    && i.getPosition().y > Environment.physicsCamera.position.y - Environment.physicsCamera.viewportHeight / 2
                    && i.getPosition().y < Environment.physicsCamera.position.y + Environment.physicsCamera.viewportHeight / 2;

            if(isInLevel)
                break;
        }
        return isInLevel;*/


        DrawableEntityInterface i;
        if (!this.isAnimating())
            i = this.getDrawableEntities().get("torso");
        else
            i = this.animatableGraphicsEntity;

        return i != null && i.getPosition().x > Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2 - this.getSize().x
                && i.getPosition().x < Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth / 2 + this.getSize().x
                && i.getPosition().y > Environment.physicsCamera.position.y - Environment.physicsCamera.viewportHeight / 2 - this.getSize().y
                && i.getPosition().y < Environment.physicsCamera.position.y + Environment.physicsCamera.viewportHeight / 2 + this.getSize().y;

    }

    protected boolean isAtObjective()
    {
       /* boolean isAtObjective = false;
        for (InteractiveEntityInterface i : this.getInteractiveEntities().values())
        {
            if (Environment.level.objective.polygon.contains(i.getPolygon().getX() + i.getPolygon().getVertices()[2]/2, i.getPolygon().getY() + i.getPolygon().getVertices()[5]/2))
            {
                isAtObjective = true;
                break;
            } else
            {
                isAtObjective = false;
            }
        }

        return isAtObjective && this.isInLevel();*/

        // x + half polygon width, objective y + half objective height because we don't care about y axis
        //return Environment.level.objective.polygon.contains(this.getPolygon().getX() + this.getPolygon().getVertices()[3]/2, Environment.level.objective.polygon.getY() + Environment.level.objective.polygon.getVertices()[5]/2f);
        //return Environment.areQuadrilaterallsColliding(Environment.level.objective.polygon, this.getPolygon());
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(this.getPosition(), 0)));
        pos.y = Environment.gameCamera.position.y * 2 - pos.y;
        return Environment.level.objective.polygon.contains(pos.x, Environment.level.objective.polygon.getY() + 100);

    }

    private boolean isAttacking()
    {
        return this.animatableGraphicsEntity.getCurrentAnimation().contains("attack");
    }

    public boolean isAlive()
    {
        return this.isAlive;
    }

    protected void isAliveCheck()
    {

        boolean isAlive = true;
        for (Object o : this.detachableEntitiesToStayAlive)
        {
            if (o instanceof String)
            {
                if (this.getDetachableEntities().get(o) != null && this.getDetachableEntities().get(o).getState().equals("attached"))
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
                    if (this.getDetachableEntities().get(s) != null && this.getDetachableEntities().get(s).getState().equals("attached"))
                    {
                        isAlive = true;
                        break;
                    } else
                    {
                        isAlive = false;
                    }
                }
            }

            if (!isAlive)
                break;
        }

        if (this.isAlive && !isAlive)
        {
            this.onDeath();
        }
        this.isAlive = isAlive && this.isAlive;
    }

    protected boolean isAnimating()
    {
        //return !this.isTouching() && this.isOptimizationEnabled();
        return this.isAnimating;
    }

    protected boolean hasRequiredPartsForGetup()
    {
        return this.getDrawableEntities().get("head") != null
                && ((this.getDrawableEntities().get("left_leg") != null && this.getDrawableEntities().get("right_leg") != null)
                || (this.getDrawableEntities().get("left_legfront") != null && this.getDrawableEntities().get("right_legfront") != null));
    }

    /**
     * Misc.
     **/


    /**
     * @expensive operation
     **/
    public boolean isAccuratePolygonColliding(Polygon polygon)
    {
        // Without an animation check the zombies slide with physics enabled, not what we want
        if (this.isAnimating())
        {
            this.syncEntitiesToAnimation();
            this.updateEntities(Gdx.graphics.getDeltaTime());
        }

        for (InteractiveEntityInterface interactiveEntityInterface : this.getInteractiveEntities().values())
        {
            if (Environment.areQuadrilaterallsColliding(interactiveEntityInterface.getPolygon(), polygon))
            {
                if (this.isAnimating())
                    this.returnEntitiesToOptimizedLocation();

                return true;
            }
        }

        if (this.isAnimating())
            this.returnEntitiesToOptimizedLocation();

        return false;
    }

    public void updateEntities(float delta)
    {
        for (DrawableEntityInterface drawableEntityInterface : this.getDrawableEntities().values())
        {
            drawableEntityInterface.update(delta);
        }
        for (InteractiveEntityInterface interactiveEntityInterface : this.getInteractiveEntities().values())
        {
            if (!this.getDrawableEntities().values().contains(interactiveEntityInterface))
                interactiveEntityInterface.update(delta);
        }
    }

    protected void checkDirection()
    {

        int previous_direction = this.direction;

        /*if (this.getPosition().x < Environment.level.objective.getPosition().x + Environment.level.objective.getWidth() / 4)
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
        }*/

        if (this.getPosition().x < Environment.level.objective.getPosition().x + Environment.level.objective.getWidth() / 2)
            this.direction = 0;
        else
            this.direction = 1;

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
        this.isAliveCheck();

        if (this.isInLevel())
        {
            if (this.getPosition().y > this.getSize().y)
                this.getUpTimer = System.currentTimeMillis();

            if (!this.isGettingUp && this.hasRequiredPartsForGetup() && System.currentTimeMillis() - getUpTimer >= timeBeforeGetup)
            {

                MouseJointDef mouseJointDef = new MouseJointDef();
                // Needs 2 bodies, first one not used, so we use an arbitrary body.
                // http://www.binarytides.com/mouse-joint-box2d-javascript/
                mouseJointDef.bodyA = Environment.physics.getGroundBodies().get(0);
                mouseJointDef.bodyB = ((PhysicsEntityInterface) this.getInteractiveEntities().get("head")).getPhysicsBody();
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

                this.maxGetupTimer = System.currentTimeMillis();

                this.onGetupStart();

            } else if (!this.hasRequiredPartsForGetup() && this.isAlive() && System.currentTimeMillis() - getUpTimer >= timeBeforeGetup)
            {
                this.onGetupEnd();
                this.startCrawl();
            }

            // -size.getY()/6f to give it wiggle room to detect get up
            if (this.isGettingUp() && this.getDrawableEntities().get("head").getPosition().y >= this.getSize().y - this.getSize().y / 6f + Environment.physics.getGroundBodies().get(this.getInitialGround()).getPosition().y)
            {
                this.onGetupEnd();
            } else if (this.isGettingUp() && System.currentTimeMillis() - maxGetupTimer >= maxGetupTime)
            {
                this.onGetupEnd();
            }

            if (this.isGettingUp() && !this.hasRequiredPartsForGetup() || !this.isAlive())
            {
                this.stopGetUp();
            }
        } else
        {
            if (this.hasRequiredPartsForGetup() && System.currentTimeMillis() - getUpTimer >= timeBeforeGetup)
            {
                this.onGetupEnd();
            } else if (this.isAlive() && System.currentTimeMillis() - getUpTimer >= timeBeforeGetup)
            {
                this.onGetupEnd();
                this.startCrawl();
            }
        }


    }

    private void startCrawl()
    {
        this.moveAnimation = "crawl";
        try
        {
            this.animatableGraphicsEntity.setAnimation("crawl");
        } catch (Exception e)
        {
            Gdx.app.error("startCrawl", "Error occurred while attempting to startCrawl()! Zombie may not have a 'crawl' animation!");
        }
    }

    public void stopGetUp()
    {

        if (this.getUpMouseJoint != null && !Environment.jointDestroyQueue.contains(this.getUpMouseJoint))
        {
            Environment.jointDestroyQueue.add(this.getUpMouseJoint);
        }
        this.getUpTimer = System.currentTimeMillis();
        this.isGettingUp = false;

    }

    private void returnToPlayableRange()
    {
        float x = (float) new Random().nextInt(100) / 100;
        float y = Environment.physics.getGroundBodies().get(this.getCurrentGround()).getPosition().y;
        if (this.animatableGraphicsEntity.getPosition().x < Environment.physicsCamera.position.x)
            this.setPosition(new Vector2(Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2 - 1f - x - this.getSize().x/2, y));
        else if (this.animatableGraphicsEntity.getPosition().x > Environment.physicsCamera.position.x)
            this.setPosition(new Vector2(Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth / 2 + 1f + x + this.getSize().x/2, y));
    }

    /**
     * @expensive operation
     **/
    protected void syncEntitiesToAnimation()
    {
        for (String key : this.getDrawableEntities().keySet())
        {
            Vector2 vec = ((PointAttachment) this.animatableGraphicsEntity.getSkeleton().getAttachment(key, "physics_pos")).computeWorldPosition(this.animatableGraphicsEntity.getSkeleton().findBone(key), new Vector2(0, 0));

            Vector3 pos = Environment.physicsCamera.unproject(Environment.gameCamera.project(new Vector3(vec.x, vec.y, 0)));
            pos.y = Environment.physicsCamera.position.y * 2 - pos.y;
            this.getDrawableEntities().get(key).setPosition(new Vector2(pos.x, pos.y));
            if (this.animatableGraphicsEntity.getSkeleton().getFlipX())
                this.getDrawableEntities().get(key).setAngle(this.animatableGraphicsEntity.getSkeleton().findBone(key).getWorldRotationX() + 180 - ((RegionAttachment) this.animatableGraphicsEntity.getSkeleton().findSlot(key).getAttachment()).getRotation());
            else
                this.getDrawableEntities().get(key).setAngle(this.animatableGraphicsEntity.getSkeleton().findBone(key).getWorldRotationX() + ((RegionAttachment) this.animatableGraphicsEntity.getSkeleton().findSlot(key).getAttachment()).getRotation());
        }
    }

    /**
     * Position physicsBody out of screen
     * to prevent invisible zombie hit detection
     */
    private void returnEntitiesToOptimizedLocation()
    {
        for (DrawableEntityInterface drawableEntityInterface : this.getDrawableEntities().values())
        {
            drawableEntityInterface.setPosition(new Vector2(99, 99));
        }
        this.updateEntities(Gdx.graphics.getDeltaTime());
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
                s.setAttachment(null);
            }
        }
    }

    // May need to be changed to some more general system in the future(using points in data files or something)
    private float getDistanceToObjective()
    {
        /*if (Environment.level.getCurrentCameraPosition().equals("left"))
        {
            return Math.abs(Environment.level.objective.getPosition().x + ((Environment.level.objective.getWidth() / 2) * 3 / 4) - this.getPosition().x);
        }
        else if(Environment.level.getCurrentCameraPosition().equals("middle") && this.direction == 0)
        {
            return Math.abs(Environment.level.objective.getPosition().x + ((Environment.level.objective.getWidth() / 2) * 3 / 4) - this.getPosition().x);
        }
        return Math.abs(Environment.level.objective.getPosition().x + ((Environment.level.objective.getWidth() / 2) * 6 / 4) - this.getPosition().x);*/

        if (this.direction == 0)
            return Math.abs(Environment.level.objective.getPosition().x + Environment.level.objective.getWidth() - this.getPosition().x);
        else
            return Math.abs(Environment.level.objective.getPosition().x - this.getPosition().x);
    }

    /**
     * Events
     **/


    public void onSpawned()
    {
    }

    @Override
    public void onBurned()
    {
        this.bodyFire = null;
        this.enable_physics();
        this.isAlive = false;
        this.onDeath();
    }

    private void onAnimationInterrupt(AnimationState.TrackEntry entry)
    {
        /* Stop zombie from moving if the animation is changed to
           something other than move animation( ie attack anim.) */
        if (!this.getCurrentAnimation().equals(this.moveAnimation) && this.getCurrentAnimation().contains("attack"))
        {
            this.clearMoveQueue();
        }
    }

    void onAnimationEvent(AnimationState.TrackEntry entry, Event event)
    {
        if (this.isAnimating() && event.getData().getName().equals("move") && !this.isAtObjective())
        {
            this.checkDirection();
            this.moveBy((this.direction == 0 ? new Vector2(this.moveDistance * this.speed, 0) : new Vector2(-this.moveDistance * this.speed, 0)));
        }

        if (entry.getAnimation().getName().equals("attack1"))
        {
            this.onAttack1();
        } else if (entry.getAnimation().getName().equals("attack2"))
        {
            this.onAttack2();
        } else if (entry.getAnimation().getName().equals("crawl_attack"))
        {
            this.onCrawlAttack();
        }
    }

    void onAnimationComplete(AnimationState.TrackEntry entry)
    {


    }

    protected void onDeath()
    {
        if (!this.onDeathFired)
        {
            Gdx.app.debug("Zombie", "onDeath()");

            // Enable optimization for torso so it's destroyableEntity can destroy it.
            ((OptimizableEntityInterface) this.getDrawableEntities().get("torso")).enable_optimization();

            // No point in spawning brains outside of level
            if (this.isInLevel())
            {
                for (int i = 0; i < new Random().nextInt(4) + 1; i++)
                {
                    int rand = new Random().nextInt(100) + 1;
                    int value;

                    // Most probably to get regular brain, least probable to get "gold" brain.
                    if (rand >= 50)
                        value = 1;
                    else if (rand >= 20)
                        value = 2;
                    else
                        value = 3;

                    if (Environment.brainPool.hasAvailableBrain(value))
                        Environment.drawableAddQueue.add(Environment.brainPool.getBrain(value, this.getPosition(), new Vector2((float) Math.random() * (new Random().nextBoolean() ? 1 : -1), 2f)));
                }
            }
        } else
        {
            Gdx.app.log("Zombie", "onDeath() did not fire. It can't be called more than once.");
        }

        this.onDeathFired = true;

    }

    protected void onObjectiveOnce()
    {
        this.clearMoveQueue();
        this.checkDirection();

        float move;

        if (this.direction == 0)
        {
            move = (Environment.level.objective.getPosition().x - Environment.level.objective.getAttackZonePosition(0).x)
                    + Environment.level.objective.getAttackZoneSize(0).x;
        } else
        {
            move = Environment.level.objective.getAttackZoneSize(1).x;
        }
        move = move - this.getSize().x / 2;
        move = (float) Math.random() * move;

        // Then move zombie to a spot on the objective
        if (this.direction == 0)
        {
            this.moveBy(new Vector2(move, 0));
        } else
        {
            this.moveBy(new Vector2(-move, 0));
        }
        this.changeToGround(2); // Change to ground first, looks better

        this.shouldObjectiveOnce = false;
    }

    protected void onObjective()
    {
        this.checkDirection();
        if (this.getCurrentAnimation().contains("crawl"))
        {
            this.setAnimation("crawl_attack");
        } else if (this.timesAnimationCompleted() >= 2 && this.getCurrentAnimation().contains("attack"))
        {
            this.setAnimation("attack2");
        } else if (this.timesAnimationCompleted() >= 1)
        {
            this.setAnimation("attack1");
        }
    }

    protected void onGetupStart()
    {
        this.disable_optimization();
    }

    protected void onGetupEnd()
    {
        // Sync animation limbs with physics limbs
        this.detachAnimationLimbs();

        // Status
        this.shouldObjectiveOnce = true;
        this.isGettingUp = false;
        this.isAnimating = true;

        if (getUpMouseJoint != null)
        {
            // Destroy physics mouseJoint
            Environment.physics.destroyJoint(getUpMouseJoint);
            getUpMouseJoint = null;
        }

        this.setAnimation(this.moveAnimation);// Set animation to move animation

        // Set animation to physics position before animating
        this.animatableGraphicsEntity.setPosition(new Vector2(this.getDrawableEntities().get("torso").getPosition().x,
                Environment.physics.getGroundBodies().get(this.getInitialGround()).getPosition().y));
        // Restart animation
        this.animatableGraphicsEntity.restartAnimation();
        // Update animation so that it changes position before drawing
        this.animatableGraphicsEntity.update(Gdx.graphics.getDeltaTime());

        // Switch zombie direction if needed
        this.checkDirection();

        if (!this.isInLevel())
            this.returnToPlayableRange();

        // Position physicsBody out of screen
        this.returnEntitiesToOptimizedLocation();

        // Enable optimization instantly, without optimizationTimer
        this.force_instant_optimize();

    }

    private void onDirectionChange()
    {
        this.constructBody();
    }

    private void onAnimate()
    {
        if (this.isAtObjective() && this.shouldObjectiveOnce)
        {
            this.onObjectiveOnce();
        } else if (this.isAtObjective() && !this.isMoving() && !this.isMovingToNewGround())
        {
            this.onObjective();
        }

        this.animatableGraphicsEntity.update(Gdx.graphics.getDeltaTime());

        this.getUpTimer = System.currentTimeMillis();
    }

    private void onPhysicsEnabled()
    {
        this.resetToInitialGround();
        this.clearMoveQueue();
    }

    public void enable_physics()
    {
        // To prevent zombie moving if multiple calls to enable_physics are made
        if (this.isAnimating())
        {
            this.syncEntitiesToAnimation();
            this.updateEntities(Gdx.graphics.getDeltaTime());

            // Move zombie to front of screen, better for gameplay
            Environment.drawableRemoveQueue.add(this);

            /*
            drawableAddQueue.add(this) needs to be in if statement
            because otherwise items like Grenades -> ParticleEntity can cause
            zombie to add itself to level so many times that the zombie is
            updated more than once per frame and is super fast
            */
            Environment.drawableAddQueue.add(this);
            /* *********************************************************** */
        }

        this.disable_optimization();

        this.isAnimating = false;
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

    protected void onAttack1()
    {
        Environment.level.objective.takeDamage(0.5f);
    }

    protected void onAttack2()
    {
        Environment.level.objective.takeDamage(1f);
    }

    protected void onCrawlAttack()
    {
        Environment.level.objective.takeDamage(0.2f);
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
            for (String part : this.bleedablePoints.keySet())
            {
                for (com.fcfruit.monstersmash.entity.BleedablePoint bleedablePoint : this.bleedablePoints.get(part))
                {
                    bleedablePoint.draw(batch);
                }
            }
        } else
        {
            for (String name : this.drawOrder)
            {
                try
                {
                    this.getDrawableEntities().get(name).draw(batch);
                    this.getDrawableEntities().get(name).draw(batch, skeletonRenderer);
                } catch (NullPointerException e)
                {
                }
            }
            /*for (Slot slot : this.animatableGraphicsEntity.getSkeleton().getDrawOrder())
            {
                if (slot.getAttachment() != null && this.getDrawableEntities().get(slot.getAttachment().getName()) != null)
                {
                    this.getDrawableEntities().get(slot.getAttachment().getName()).draw(batch);
                    this.getDrawableEntities().get(slot.getAttachment().getName()).draw(batch, skeletonRenderer);
                }
            }*/
        }
        if (this.bodyFire != null)
            this.bodyFire.draw(batch, skeletonRenderer);
    }

    @Override
    public void update(float delta)
    {
        if (this.isInPlayableRange() || Environment.mode == Environment.Mode.SANDBOX)
        {
            if (!this.isAnimating() && this.isInLevel())
            {
                // Has to be outside of isAlive() because when zombie dies, torso
                // stops updating and freezes otherwise
                this.updateEntities(delta);
                if (!this.isGettingUp)
                    this.enable_optimization();

            }
            this.optimizableEntity.update(delta);

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
                    this.animatableGraphicsEntity.setPosition(new Vector2(this.getPosition().x, this.getPosition().y - this.getSize().y / 2));
                    this.animatableGraphicsEntity.update(delta);
                    this.handleGetup();
                }
            }

            if (this.bodyFire != null)
                this.bodyFire.update(delta);

        } else
        {
            if (!this.isAnimating())
                this.handleGetup();
            this.force_instant_optimize();
            if (!this.isAlive())
            {
                this.returnEntitiesToOptimizedLocation();
                Environment.drawableRemoveQueue.add(this);
            }
            this.returnToPlayableRange();
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

        /*Vector3 pos = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));
        if (this.isAnimating() && Environment.touchedDownItems.size() < 1 && this.getPolygon().contains(pos.x, pos.y))
        {
            this.enable_physics();
        }*/

        boolean touching = false;
        for (InteractiveEntityInterface interactiveEntity : this.getInteractiveEntities().values())
        {
            interactiveEntity.onTouchDown(screenX, screenY, p);
            if (interactiveEntity.isTouching())
            {
                touching = true;
            }
        }

        // Don't enable big zombie polygon when physics enabled
        if (this.isAnimating())
            this.interactiveGraphicsEntity.onTouchDown(screenX, screenY, p);

        if (this.interactiveGraphicsEntity.isTouching() && !touching && this.isAlive() && this.isInLevel())
        {
            this.enable_physics();
            ((InteractivePhysicsEntityInterface) this.getInteractiveEntities().get("torso")).overrideTouching(true, screenX, screenY, p);
        }

        this.isTouching = touching || this.interactiveGraphicsEntity.isTouching();

        if (this.isTouching())
            this.isAnimating = false;
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
        boolean touching = false;
        for (InteractiveEntityInterface interactiveEntity : this.getInteractiveEntities().values())
        {
            interactiveEntity.onTouchUp(screenX, screenY, p);

            if (interactiveEntity.isTouching())
                touching = true;
        }
        this.interactiveGraphicsEntity.onTouchUp(screenX, screenY, p);

        this.isTouching = touching || this.interactiveGraphicsEntity.isTouching();
    }

    @Override
    public boolean isTouching()
    {
        /*for (InteractiveEntityInterface interactiveEntity : this.getInteractiveEntities().values())
        {
            if (interactiveEntity.isTouching())
            {
                return true;
            }
        }
        return this.interactiveGraphicsEntity.isTouching();*/
        return this.isTouching;
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
    public void force_instant_optimize()
    {
        this.optimizableEntity.force_instant_optimize();
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
        for (String name : this.getDetachableEntities().keySet())
        {
            if (this.getDetachableEntities().get(name).equals(detachableEntityInterface))
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
        this.speed = speed;
    }

    public void setMoveDistance(float moveDistance)
    {
        this.moveDistance = moveDistance;
    }

    @Override
    public void clearMoveQueue()
    {
        this.movableEntity.clearMoveQueue();
    }

    @Override
    public Skeleton getSkeleton()
    {
        return this.animatableGraphicsEntity.getSkeleton();
    }

    @Override
    public AnimationState getState()
    {
        return this.animatableGraphicsEntity.getState();
    }

    @Override
    public TextureAtlas getAtlas()
    {
        return this.animatableGraphicsEntity.getAtlas();
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
        for (DrawableEntityInterface drawableEntityInterface : this.containerEntity.getDrawableEntities().values())
        {
            drawableEntityInterface.setAlpha(alpha);
        }
    }

    @Override
    public void attach_fire(com.fcfruit.monstersmash.effects.BodyFire fire)
    {
        fire.setScale(this.animatableGraphicsEntity.getSize().x / fire.getSize().x, this.animatableGraphicsEntity.getSize().y / fire.getSize().y);
        this.bodyFire = fire;
    }

    @Override
    public com.fcfruit.monstersmash.effects.BodyFire getBodyFire()
    {
        return this.bodyFire;
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