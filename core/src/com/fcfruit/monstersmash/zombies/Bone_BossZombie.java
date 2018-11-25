package com.fcfruit.monstersmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.monstersmash.entity.BleedablePoint;
import com.fcfruit.monstersmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.monstersmash.zombies.Zombie;
import com.fcfruit.monstersmash.zombies.parts.SpecialPart;
import com.fcfruit.monstersmash.zombies.parts.Torso;

import java.util.ArrayList;

public class Bone_BossZombie extends Zombie
{
    private float health = 100.0f;
    private boolean isAlive = true;

    private AnimatableGraphicsEntity stompSmoke;

    /**
     * Bone Boss Zombie
     * Can only be killed with fire
     * Can be knocked down with grenades and explosives and rocks
     **/

    public Bone_BossZombie(Integer id)
    {
        super(id);

        this.moveAnimation = "walk";
        this.setSpeed(1);
        this.setMoveDistance(1.5f);

        this.detachableEntitiesToStayAlive.add("head");
        this.detachableEntitiesToStayAlive.add("torso_bottom");
        this.detachableEntitiesToStayAlive.add("left_arm_back");
        this.detachableEntitiesToStayAlive.add("left_arm_front");
        this.detachableEntitiesToStayAlive.add("right_arm_back");
        this.detachableEntitiesToStayAlive.add("right_arm_front");
        this.detachableEntitiesToStayAlive.add("leg_back_1");
        this.detachableEntitiesToStayAlive.add("leg_front_1");
        this.detachableEntitiesToStayAlive.add("leg_back_2");
        this.detachableEntitiesToStayAlive.add("leg_front_2");
        this.detachableEntitiesToStayAlive.add("leg_back_3");
        this.detachableEntitiesToStayAlive.add("leg_front_3");
        this.detachableEntitiesToStayAlive.add("leg_back_4");
        this.detachableEntitiesToStayAlive.add("leg_front_4");

        this.currentParts.add("head");
        this.currentParts.add("torso");
        this.currentParts.add("torso_bottom");
        this.currentParts.add("left_arm_back");
        this.currentParts.add("left_arm_front");
        this.currentParts.add("right_arm_back");
        this.currentParts.add("right_arm_front");
        this.currentParts.add("leg_back_1");
        this.currentParts.add("leg_front_1");
        this.currentParts.add("leg_back_2");
        this.currentParts.add("leg_front_2");
        this.currentParts.add("leg_back_3");
        this.currentParts.add("leg_front_3");
        this.currentParts.add("leg_back_4");
        this.currentParts.add("leg_front_4");

    }

    @Override
    public void setup(int direction)
    {
        super.setup(direction);

        TextureAtlas atlas = Environment.assets.get("effects/smoke/smoke.atlas", TextureAtlas.class);
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("effects/smoke/smoke.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).

        this.stompSmoke = new AnimatableGraphicsEntity(skeleton, state, atlas);
        this.stompSmoke.getSkeleton().getRootBone().setScale(2);
        this.stompSmoke.setAnimation("animation");
        this.stompSmoke.setPosition(new Vector2(99, 99)); // move outside of screen
        this.stompSmoke.getState().addListener(new AnimationState.AnimationStateAdapter()
        {
            @Override
            public void complete(AnimationState.TrackEntry entry)
            {
                super.complete(entry);
                stompSmoke.setPosition(new Vector2(99, 99));
            }
        });
    }

    @Override
    protected void isAliveCheck()
    {
        if(this.isAlive && this.health <= 0)
            this.onDeath();
        this.isAlive = this.health > 0;
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        super.draw(batch, skeletonRenderer);
        if(this.isAnimating())
            this.stompSmoke.draw(batch, skeletonRenderer);
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        if(this.isAnimating())
            this.stompSmoke.update(delta);
    }

    @Override
    public void onSpawned()
    {
        Environment.musicManager.addMusic("bone_boss", Environment.assets.get("zombies/bone_boss_zombie/theme.mp3", Music.class), true);
        Environment.musicManager.playMusic("bone_boss");
    }

    @Override
    protected void onAttack1()
    {
        Environment.level.objective.takeDamage(15f);
    }

    @Override
    protected void onAttack2()
    {
        Environment.level.objective.takeDamage(20f);
    }

    private void onAttack3()
    {
        Environment.level.objective.takeDamage(25f);
        this.stompSmoke.setPosition(this.getPosition());
        this.stompSmoke.restartAnimation();
    }

    @Override
    void onAnimationEvent(AnimationState.TrackEntry entry, Event event)
    {
        super.onAnimationEvent(entry, event);
        if (event.getData().getName().equals("move") && this.isAnimating())
        {
            this.stompSmoke.setPosition(this.getPosition());
            this.stompSmoke.restartAnimation();
        } else if (this.getCurrentAnimation().equals("attack3") && event.getData().getName().equals("attack"))
            this.onAttack3();
    }

    @Override
    protected boolean hasRequiredPartsForGetup()
    {
        return this.isAlive(); // Boss zombie will always be able to get up as he doesn't have any detachable parts
    }

    @Override
    public void enable_physics()
    {
        super.enable_physics();
        // Need to check if alive so that onDeath() can be called if not alive
        // and for some reason hit with rocks etc... rather than flames
        this.isAliveCheck();
    }

    @Override
    public boolean isAlive()
    {
        return super.isAlive() && this.isAlive;
    }

    @Override
    protected void onObjectiveOnce()
    {
        this.clearMoveQueue();
        if (this.getDirection() == 0)
            this.moveTo(new Vector2(Environment.level.objective.getPosition().x + 0.1f, Environment.level.objective.getPosition().y));
        else
            this.moveTo(new Vector2(Environment.level.objective.getPosition().x + Environment.level.objective.getWidth() - 0.1f, Environment.level.objective.getPosition().y));
        this.shouldObjectiveOnce = false;
    }

    @Override
    protected void onObjective()
    {
        this.checkDirection();

        if (this.getCurrentAnimation().equals("attack2") && this.timesAnimationCompleted() >= 1)
        {
            this.setAnimation("attack3");
        } else if (this.getCurrentAnimation().equals("attack1") && this.timesAnimationCompleted() >= 2)
        {
            this.setAnimation("attack2");
        } else if (this.getCurrentAnimation().equals(this.moveAnimation) || this.getCurrentAnimation().equals("attack3") && this.timesAnimationCompleted() >= 1)
        {
            this.setAnimation("attack1");
        }
    }

    @Override
    public void onBurned()
    {
        this.bodyFire = null;
        this.health -= 15f;
        this.enable_physics();
        if (this.health <= 0)
        {
            this.bodyFire = null;
            this.enable_physics();
            this.isAlive = false;
            this.onDeath();
        }
    }

    @Override
    public void onTouchDown(float screenX, float screenY, int p)
    {
        // Don't pick up zombie
    }

    @Override
    protected void createPart(Body physicsBody, String bodyName, Sprite sprite, ArrayList<Joint> joints, ContainerEntityInterface containerEntity, Array<BleedablePoint> bleedablePoints)
    {
        if(bodyName.equals("torso"))
        {
            Torso part = new Torso(bodyName, sprite, physicsBody, containerEntity, bleedablePoints);
            this.getDrawableEntities().put(bodyName, part);
            this.getInteractiveEntities().put(bodyName, part);
        }
        else
        {
            SpecialPart part = new SpecialPart(bodyName, sprite, physicsBody, joints, containerEntity);
            this.getDrawableEntities().put(bodyName, part);
            this.getInteractiveEntities().put(bodyName, part);
            this.getDetachableEntities().put(bodyName, part);
        }

    }
}
