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
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.DrawablePhysicsEntity;
import com.fcfruit.zombiesmash.entity.InteractiveGraphicsEntity;
import com.fcfruit.zombiesmash.entity.InteractivePhysicsEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
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

public class NewZombie implements DrawableEntityInterface, InteractiveEntityInterface
{

    // Animations
    String moveAnimation;

    // Identifiers
    private int id;

    // Composition
    private AnimatableGraphicsEntity animatableGraphicsEntity;
    private InteractiveGraphicsEntity interactiveGraphicsEntity;

    // Name, Item
    private HashMap<String, DrawableEntityInterface> drawableEntities;
    private HashMap<String, InteractiveEntityInterface> interactiveEntities;
    private HashMap<String, DetachableEntityInterface> detachableEntities;


    // Zombie Specific Fields
    private boolean isAnimating;
    private int timesCompleteAttack1;
    private int direction;
    private int speed;
    ArrayList partsToStayAlive;

    // Get up Fields
    private double getUpTimer;
    private double timeBeforeGetup;
    private boolean isGettingUp;
    private MouseJoint getUpMouseJoint;

    // Optimization
    private double optimizationTimer;
    private double timeBeforeOptimize;
    private boolean optimizationEnabled;


    public NewZombie(int id)
    {
        this.id = id;

        this.animationSetup();
        this.interactiveEntitySetup();

        this.isAnimating = false;
        this.timesCompleteAttack1 = 0;
        this.speed = 200;
        this.partsToStayAlive = new ArrayList();

        this.timeBeforeGetup = 5000;
        this.isGettingUp = false;

        this.timeBeforeOptimize = 2500;

    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.animatableGraphicsEntity.draw(batch, skeletonRenderer);

        if (isTouching() || !this.isAnimating)
        {
            for (Slot slot : this.animatableGraphicsEntity.getSkeleton().getDrawOrder())
            {
                if (drawableEntities.get(slot.getAttachment().getName()) != null)
                {
                    drawableEntities.get(slot.getAttachment().getName()).draw(batch);
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
        this.updateEntities(delta);
        if (this.isAlive())
        {

            this.handleGetup();
            // May cause problems if running constantly in loop, idk
            this.checkDirection();

            if (this.isTouching())
            {
                this.onTouching();
            }

            if (this.isAnimating)
            {
                this.onAnimate();
            }

            if (this.isTouching() || this.isGettingUp)
            {
                this.optimizationEnabled = false;
                optimizationTimer = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - optimizationTimer >= timeBeforeOptimize)
            {
                this.optimizationEnabled = true;
            }

        }

    }

    public void updateEntities(float delta){
        for(DrawableEntityInterface drawableEntityInterface : drawableEntities.values()){
            drawableEntityInterface.update(delta);
        }
        for(InteractiveEntityInterface interactiveEntityInterface : interactiveEntities.values()){
            interactiveEntityInterface.update(delta);
            if(optimizationEnabled && interactiveEntityInterface instanceof InteractivePhysicsEntity){
                ((InteractivePhysicsEntity) interactiveEntityInterface).optimize();
            }
        }
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


    public void constructPhysicsBody(World world, boolean flip)
    {
        RubeSceneLoader loader = new RubeSceneLoader(world);
        RubeScene rubeScene;
        if (flip)
        {
            rubeScene = loader.loadScene(Gdx.files.internal("zombies/" + this.getClass().getName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getName().replace("Zombie", "").toLowerCase() + "_zombie_flip_rube.json"));
        } else
        {
            rubeScene = loader.loadScene(Gdx.files.internal("zombies/" + this.getClass().getName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getName().replace("Zombie", "").toLowerCase() + "_zombie_rube.json"));
        }

        this.drawableEntities = new HashMap<String, DrawableEntityInterface>();
        this.interactiveEntities = new HashMap<String, InteractiveEntityInterface>();
        this.detachableEntities = new HashMap<String, DetachableEntityInterface>();

        float scale = 0;

        for (Body b : rubeScene.getBodies())
        {

            if ((Boolean) rubeScene.getCustom(b, "isPart"))
            {

                String bodyName = (String) rubeScene.getCustom(b, "name");
                Sprite sprite = new Sprite(this.animatableGraphicsEntity.getAtlas().findRegion(bodyName));

                for (RubeImage i : rubeScene.getImages())
                {
                    if (i.body == b)
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
                    if (j.getBodyA() == b || j.getBodyB() == b)
                    {
                        joint = j;
                        break;
                    }
                }

                for (Fixture f : b.getFixtureList())
                {
                    // Makes different zombies not collide with each other
                    f.setUserData(this);
                }

                drawableEntities.put(bodyName, new NewPart(bodyName, sprite, b, joint));
            }

        }

        this.animatableGraphicsEntity.getSkeleton().getRootBone().setScale(scale);

        this.animatableGraphicsEntity.update(Gdx.graphics.getDeltaTime()); // Update the animation getUpTimer.


    }

    private boolean isInLevel()
    {
        boolean isInLevel = false;
        for (DrawableEntityInterface i : drawableEntities.values())
        {
            if (i.getPosition().x > Environment.physics.getWall_1().getPosition().x + 0.5f
                    && i.getPosition().x < Environment.physics.getWall_2().getPosition().x - 0.5f)
            {
                isInLevel = true;
            } else
            {
                isInLevel = false;
            }
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
        constructPhysicsBody(Environment.physics.getWorld(), this.direction == 1);
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
        for (InteractiveEntityInterface i : this.interactiveEntities.values())
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
        if (!this.isGettingUp && this.hasRequiredPartsForGetup() && System.currentTimeMillis() - getUpTimer >= timeBeforeGetup)
        {
            MouseJointDef mouseJointDef = new MouseJointDef();
            // Needs 2 bodies, first one not used, so we use an arbitrary body.
            // http://www.binarytides.com/mouse-joint-box2d-javascript/
            mouseJointDef.bodyA = Environment.physics.getGround();
            mouseJointDef.bodyB = ((DrawablePhysicsEntity) this.drawableEntities.get("head")).getPhysicsBody();
            mouseJointDef.collideConnected = true;
            mouseJointDef.target.set(drawableEntities.get("head").getPosition());
            // The higher the ratio, the slower the movement of body to mousejoint
            mouseJointDef.dampingRatio = 7;
            mouseJointDef.maxForce = 100000f;
            // Destroy the current mouseJoint
            if (getUpMouseJoint != null)
            {
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
            }
            getUpMouseJoint = (MouseJoint) Environment.physics.getWorld().createJoint(mouseJointDef);
            getUpMouseJoint.setTarget(new Vector2(drawableEntities.get("torso").getPosition().x, this.animatableGraphicsEntity.getSize().x));

            this.isGettingUp = true;
        }

        // -0.2f to give it wiggle room to detect get up
        if (this.isGettingUp && this.drawableEntities.get("head").getPosition().y >= this.getSize().y - 0.2f)
        {
            this.isGettingUp = false;
            this.isAnimating = true;
            this.setPosition(new Vector2(this.drawableEntities.get("torso").getPosition().x, 0));
            if (getUpMouseJoint != null)
            {
                Environment.physics.getWorld().destroyJoint(getUpMouseJoint);
                getUpMouseJoint = null;
            }
            // Restart animation
            this.animatableGraphicsEntity.restartAnimation();
        }

    }

    private boolean isAlive()
    {
        boolean isAlive = false;
        for (Object o : partsToStayAlive)
        {
            if (o instanceof String)
            {
                if (detachableEntities.get(o) != null)
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
                    if (detachableEntities.get(s) != null)
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

    private void onTouching()
    {
        if (this.getUpMouseJoint != null)
        {
            Environment.physics.getWorld().destroyJoint(this.getUpMouseJoint);
            this.getUpMouseJoint = null;
        }
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

        this.animatableGraphicsEntity.update(Gdx.graphics.getDeltaTime());
        this.getUpTimer = System.currentTimeMillis();
    }

    private void detachAnimationLimbs()
    {
        // Do not draw detached parts in animation
        for (Slot s : this.animatableGraphicsEntity.getSkeleton().getSlots())
        {
            // If the part is detached...
            if (this.drawableEntities.get(s.getData().getName()) == null)
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

    private void move(final Vector2 moveByPos)
    {
        final Vector2 initialPos = this.getPosition();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                boolean finishedMove = false;
                while (!finishedMove)
                {
                    if (initialPos.x < initialPos.x + moveByPos.x)
                    {
                        setDirection(0);
                        animatableGraphicsEntity.setPosition(new Vector2(getPosition().x + speed / 100 * Gdx.graphics.getDeltaTime(), getPosition().y));
                    }
                    if (initialPos.x > initialPos.x + moveByPos.x)
                    {
                        setDirection(1);
                        animatableGraphicsEntity.setPosition(new Vector2(getPosition().x + speed / 100 * Gdx.graphics.getDeltaTime(), getPosition().y));
                    }
                    if (initialPos.y < initialPos.y + moveByPos.y)
                    {
                        animatableGraphicsEntity.setPosition(new Vector2(getPosition().x, getPosition().y + speed / 100 * Gdx.graphics.getDeltaTime()));
                    }
                    if (initialPos.y > initialPos.y + moveByPos.y)
                    {
                        animatableGraphicsEntity.setPosition(new Vector2(getPosition().x, getPosition().y + speed / 100 * Gdx.graphics.getDeltaTime()));
                    }
                    finishedMove = initialPos.x == initialPos.x + moveByPos.x && initialPos.y == initialPos.y + moveByPos.y;
                }
            }
        });

    }

    private void moveTo(final Vector2 moveToPos)
    {
        final Vector2 initialPos = this.getPosition();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                boolean finishedMove = false;
                while (!finishedMove)
                {
                    if (initialPos.x < moveToPos.x)
                    {
                        setDirection(0);
                        animatableGraphicsEntity.setPosition(new Vector2(getPosition().x + speed / 100 * Gdx.graphics.getDeltaTime(), getPosition().y));
                    }
                    if (initialPos.x > moveToPos.x)
                    {
                        setDirection(1);
                        animatableGraphicsEntity.setPosition(new Vector2(getPosition().x + speed / 100 * Gdx.graphics.getDeltaTime(), getPosition().y));
                    }
                    if (initialPos.y < moveToPos.y)
                    {
                        animatableGraphicsEntity.setPosition(new Vector2(getPosition().x, getPosition().y + speed / 100 * Gdx.graphics.getDeltaTime()));
                    }
                    if (initialPos.y > moveToPos.y)
                    {
                        animatableGraphicsEntity.setPosition(new Vector2(getPosition().x, getPosition().y + speed / 100 * Gdx.graphics.getDeltaTime()));
                    }
                    finishedMove = initialPos.x == moveToPos.x && initialPos.y == moveToPos.y;
                }
            }
        });
    }

    private void onObjectiveOnce()
    {
        if (this.direction == 0)
        {
            this.move(new Vector2(new Random().nextInt((int) this.getDistanceToObjective()), 0));
        } else
        {
            this.move(new Vector2(-new Random().nextInt((int) this.getDistanceToObjective()), 0));
        }
    }

    private void onObjective()
    {
        if (this.timesCompleteAttack1 < 2)
        {
            this.animatableGraphicsEntity.setAnimation("attack1");
        } else
        {
            this.animatableGraphicsEntity.setAnimation("attack2");
        }
    }

    private void onAttack1Complete()
    {
        Environment.level.objective.takeDamage(0.5f);
        this.timesCompleteAttack1++;
    }

    private void onAttack2Complete()
    {
        Environment.level.objective.takeDamage(1f);
        this.timesCompleteAttack1 = 0;
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
        return interactiveEntities.get("head") != null && interactiveEntities.get("left_leg") != null
                && interactiveEntities.get("right_leg") != null;
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
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("zombies/" + this.getClass().getName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getName().replace("Zombie", "").toLowerCase() + "_zombie.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("zombies/" + this.getClass().getName().replace("Zombie", "").toLowerCase() + "_zombie/" + this.getClass().getName().replace("Zombie", "").toLowerCase() + "_zombie.json"));

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

    }

    private void onAnimationComplete(AnimationState.TrackEntry entry)
    {
        if (entry.getAnimation().getName().equals("attack"))
        {
            this.onAttack1Complete();
        } else if (entry.getAnimation().getName().equals("attack2"))
        {
            this.onAttack2Complete();
        } else
        {
            this.move(new Vector2(0.1f, 0.1f));
        }
    }

    private void onDeath()
    {
    }


    @Override
    public Vector2 getPosition()
    {
        return this.animatableGraphicsEntity.getPosition();
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.animatableGraphicsEntity.setPosition(position);
        this.drawableEntities.get("torso").setPosition(position);
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
        return this.interactiveGraphicsEntity.isTouching();
    }

    public Polygon getPolygon()
    {
        return this.interactiveGraphicsEntity.getPolygon();
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch)
    {

    }

}