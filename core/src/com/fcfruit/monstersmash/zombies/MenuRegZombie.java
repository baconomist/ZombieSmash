package com.fcfruit.monstersmash.zombies;

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
import com.fcfruit.monstersmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractivePhysicsEntityInterface;
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

public class MenuRegZombie implements DrawableEntityInterface, AnimatableEntityInterface,
        InteractiveEntityInterface, OptimizableEntityInterface, ContainerEntityInterface
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
    private AnimatableGraphicsEntity animatableGraphicsEntity;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;
    private OptimizableEntity optimizableEntity;
    public ContainerEntity containerEntity;

    /**
     * Zombie Specific Fields
     **/
    private HashMap<String, Array<BleedablePoint>> bleedablePoints;
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
     * Status fields, for optimization
     * **/
    private boolean isAlive;
    private boolean isTouching;
    private boolean isAnimating;

    public MenuRegZombie()
    {
        // Identifier
        this.id = 0;

        // Composition
        this.containerEntity = new ContainerEntity();
        this.optimizableEntity = new OptimizableEntity(null, null, this);

        // Zombie Specific Fields
        this.detachableEntitiesToStayAlive = new ArrayList();
        this.currentParts = new ArrayList<String>();

        // Getup Fields
        this.timeBeforeGetup = 5000;
        this.isGettingUp = false;

        // Status fields
        this.isAlive = true; // Zombie is alive by default :)
        this.isAnimating = true; // Zombie starts in an animating state
        this.isTouching = false;

        // Optimize zombie
        this.enable_optimization();

        this.detachableEntitiesToStayAlive.add("head");
        this.detachableEntitiesToStayAlive.add(new String[]{"left_arm", "right_arm"});
        this.detachableEntitiesToStayAlive.add("left_leg");
        this.detachableEntitiesToStayAlive.add("right_leg");


        this.currentParts.add("head");
        this.currentParts.add("left_arm");
        this.currentParts.add("torso");
        this.currentParts.add("right_arm");
        this.currentParts.add("left_leg");
        this.currentParts.add("right_leg");
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
        this.drawOrderSetup();

        this.returnEntitiesToOptimizedLocation();
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
    }

    private HashMap<String, Array<BleedablePoint>> create_bleedable_points(RubeScene rubeScene)
    {
        HashMap<String, Array<BleedablePoint>> bleedable_points = new HashMap<String, Array<BleedablePoint>>();

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

                Array<BleedablePoint> bleedablePoints = new Array<BleedablePoint>();

                for (int i = 0; i < blood_pos_attachments.size; i++)
                {
                    // If part is detached...
                    if(this.animatableGraphicsEntity.getSkeleton().findSlot(bodyName).getAttachment() == null)
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
                    bleedablePoints.add(new BleedablePoint((PointAttachment) this.animatableGraphicsEntity.getSkeleton().getAttachment(bodyName, "physics_pos"),
                            ((PointAttachment) attachment), this.animatableGraphicsEntity.getSkeleton().findBone(bodyName), body, this.animScale));


                }

                bleedable_points.put(bodyName, bleedablePoints);

            }
        }

        return bleedable_points;
    }

    public void constructBody()
    {
        boolean flip=true;
        World world = Environment.physics.getWorld();

        com.fcfruit.monstersmash.rube.loader.RubeSceneLoader loader = new com.fcfruit.monstersmash.rube.loader.RubeSceneLoader(world);
        RubeScene rubeScene;
        rubeScene = loader.loadScene(Gdx.files.internal("zombies/reg_zombie/menu_reg_zombie_flip_rube.json"));
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
        this.animatableGraphicsEntity.update(Gdx.graphics.getDeltaTime()); // May cause crash @warning, need this for blood to work properly

        this.bleedablePoints = this.create_bleedable_points(rubeScene);
        for(String part : bleedablePoints.keySet())
        {
            if(bleedablePoints.get(part).size > 1)
            {
                for(BleedablePoint bleedablePoint : bleedablePoints.get(part))
                {
                    /*
                     * TODO:
                     * TODO:
                     * TODO:
                     *   - Make this work for parts which may have more than 1 bleed position but are still children
                     * */
                    if(bleedablePoints.get(bleedablePoint.blood_pos_name.replace("blood_pos_", "")) != null)
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
                    fixture.setUserData(new PhysicsData(this));
                }



                // If we still have the part
                // If not, we destroy the body loaded into the world by rube
                // Prevents invisible bodies for when parts are detached from the zombie
                if (this.currentParts.contains(bodyName))
                    this.createPart(body, bodyName, sprite, joints, this, bleedablePoints.get(bodyName));
                else
                    Environment.physics.destroyBody(body);

            }
            else
            {
                Environment.physics.destroyBody(body);
            }
        }

    }

    /**
     * Zombie subclasses should override this method
     **/
    protected void createPart(Body physicsBody, String bodyName, Sprite sprite, ArrayList<Joint> joints, ContainerEntityInterface containerEntity, Array<BleedablePoint> bleedablePoints)
    {
        physicsBody.setUserData(new PhysicsData(this));
        // If child
        if (joints.size() > 0)
        {
            if(bleedablePoints.size < 1)
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
        size.y = Environment.gameCamera.position.y*2 - size.y;
        Polygon polygon = new Polygon(new float[]{0, 0, size.x*2, 0, size.x*2, size.y*1.5f, 0, size.y*1.5f});
        polygon.setOrigin(size.x, 0);
        this.interactiveGraphicsEntity = new InteractiveGraphicsEntity(this.animatableGraphicsEntity, polygon);
    }

    private void animationSetup()
    {
        TextureAtlas atlas = Environment.assets.get("zombies/reg_zombie/reg_zombie.atlas", TextureAtlas.class);
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("zombies/reg_zombie/reg_zombie.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        //state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

        this.animatableGraphicsEntity = new AnimatableGraphicsEntity(skeleton, state, atlas);
    }

    private void drawOrderSetup()
    {
        this.drawOrder = new Array<String>();
        for(Slot slot : this.animatableGraphicsEntity.getSkeleton().getDrawOrder())
        {
            if (slot.getAttachment() != null && this.getDrawableEntities().get(slot.getAttachment().getName()) != null)
            {
                this.drawOrder.add(slot.getAttachment().getName());
            }
        }
    }

    private void returnEntitiesToOptimizedLocation()
    {
        for(DrawableEntityInterface drawableEntityInterface : this.getDrawableEntities().values())
        {
            drawableEntityInterface.setPosition(new Vector2(99, 99));
        }
        this.updateEntities(Gdx.graphics.getDeltaTime());
    }

    public void updateEntities(float delta)
    {
        for (DrawableEntityInterface drawableEntityInterface : this.getDrawableEntities().values())
        {
            drawableEntityInterface.update(delta);
        }
        for (InteractiveEntityInterface interactiveEntityInterface : this.getInteractiveEntities().values())
        {
            if(!this.getDrawableEntities().values().contains(interactiveEntityInterface))
                interactiveEntityInterface.update(delta);
        }
    }

    /**
     * Booleans
     * **/
    private boolean isGettingUp()
    {
        return this.isGettingUp;
    }

    public boolean isAlive()
    {
        return this.isAlive;
    }

    private void isAliveCheck()
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

            if(!isAlive)
                break;
        }

        this.isAlive = isAlive && this.isAlive;
    }

    private boolean isAnimating()
    {
        //return !this.isTouching() && this.isOptimizationEnabled();
        return this.isAnimating;
    }

    private boolean hasRequiredPartsForGetup()
    {
        return this.getDrawableEntities().get("head") != null && this.getDrawableEntities().get("left_leg") != null
                && this.getDrawableEntities().get("right_leg") != null;
    }

    private void handleGetup()
    {
        this.isAliveCheck();

        if (this.getPosition().y > this.getSize().y)
            this.getUpTimer = System.currentTimeMillis();

        if (!this.isGettingUp && this.hasRequiredPartsForGetup() && System.currentTimeMillis() - getUpTimer >= timeBeforeGetup)
        {

            MouseJointDef mouseJointDef = new MouseJointDef();
            // Needs 2 bodies, first one not used, so we use an arbitrary body.
            // http://www.binarytides.com/mouse-joint-box2d-javascript/
            mouseJointDef.bodyA = Environment.physics.getGroundBodies().get(0);
            mouseJointDef.bodyB = ((com.fcfruit.monstersmash.zombies.parts.Part) this.getInteractiveEntities().get("head")).getPhysicsBody();
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
                    this.animatableGraphicsEntity.getSize().y));

            this.isGettingUp = true;

            this.onGetupStart();

        }

        // -0.3f to give it wiggle room to detect get up
        if (this.isGettingUp() && this.getDrawableEntities().get("head").getPosition().y >= this.getSize().y - 0.3f)
        {
            this.onGetupEnd();
        }

        if(this.isGettingUp() && !this.hasRequiredPartsForGetup() || !this.isAlive())
        {
            this.stopGetUp();
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

    private void onGetupStart()
    {
        this.disable_optimization();
    }

    private void onGetupEnd()
    {
        // Sync animation limbs with physics limbs
        this.detachAnimationLimbs();

        // Status
        this.isGettingUp = false;
        this.isAnimating = true;

        if (getUpMouseJoint != null)
        {
            // Destroy physics mouseJoint
            Environment.physics.destroyJoint(getUpMouseJoint);
            getUpMouseJoint = null;
        }

        // Set animation to physics position before animating
        this.animatableGraphicsEntity.setPosition(new Vector2(this.getDrawableEntities().get("torso").getPosition().x, 0.3f));
        // Restart animation
        this.animatableGraphicsEntity.restartAnimation();
        // Update animation so that it changes position before drawing
        this.animatableGraphicsEntity.update(Gdx.graphics.getDeltaTime());

        // Position physicsBody out of screen
        this.returnEntitiesToOptimizedLocation();

        // Enable optimization instantly, without optimizationTimer
        this.force_instant_optimize();

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

    protected void syncEntitiesToAnimation()
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

    private void onAnimate()
    {
        this.animatableGraphicsEntity.update(Gdx.graphics.getDeltaTime());

        this.getUpTimer = System.currentTimeMillis();
    }

    public void enable_physics()
    {
        // To prevent zombie moving if multiple calls to enable_physics are made
        if(this.isAnimating())
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
        this.disable_optimization();
        this.isGettingUp = false;
        this.getUpTimer = System.currentTimeMillis();
    }

    @Override
    public void draw(SpriteBatch batch)
    {

    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        if (this.isAnimating())
        {
            this.animatableGraphicsEntity.draw(batch, skeletonRenderer);
            for(String part : this.bleedablePoints.keySet())
            {
                for (BleedablePoint bleedablePoint : this.bleedablePoints.get(part))
                {
                    bleedablePoint.draw(batch);
                }
            }
        } else
        {
            for(String name : this.drawOrder)
            {
                try
                {
                    this.getDrawableEntities().get(name).draw(batch);
                    this.getDrawableEntities().get(name).draw(batch, skeletonRenderer);
                }
                catch (NullPointerException e){}
            }
        }
    }

    @Override
    public void update(float delta)
    {
        if (!this.isAnimating())
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
            } else
            {
                this.animatableGraphicsEntity.setPosition(new Vector2(this.getPosition().x, this.getPosition().y - this.getSize().y / 2));
                this.animatableGraphicsEntity.update(delta);
                this.handleGetup();
            }
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
        if(this.isAnimating())
            this.interactiveGraphicsEntity.onTouchDown(screenX, screenY, p);

        if (this.interactiveGraphicsEntity.isTouching() && !touching && this.isAlive())
        {
            this.enable_physics();
            ((InteractivePhysicsEntityInterface) this.getInteractiveEntities().get("torso")).overrideTouching(true, screenX, screenY, p);
        }

        this.isTouching = touching || this.interactiveGraphicsEntity.isTouching();

        if(this.isTouching())
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

            if(interactiveEntity.isTouching())
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
}
