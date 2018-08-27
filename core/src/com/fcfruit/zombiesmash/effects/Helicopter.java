package com.fcfruit.zombiesmash.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.DrawableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.MovableEntity;
import com.fcfruit.zombiesmash.entity.interfaces.AnimatableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.MovableEntityInterface;
import com.fcfruit.zombiesmash.level.Spawner;
import com.fcfruit.zombiesmash.powerups.PowerupCrate;
import com.fcfruit.zombiesmash.powerups.grenade.GrenadePowerup;
import com.fcfruit.zombiesmash.powerups.gun_powerup.PistolPowerup;
import com.fcfruit.zombiesmash.powerups.gun_powerup.RiflePowerup;
import com.fcfruit.zombiesmash.powerups.rock_powerup.RockPowerup;
import com.fcfruit.zombiesmash.powerups.time.TimePowerup;

import java.util.HashMap;
import java.util.Random;

public class Helicopter implements AnimatableEntityInterface, MovableEntityInterface
{
    private static HashMap<String, Class> entityType = new HashMap<String, Class>();
    static
    {
        entityType.put("rifle", RiflePowerup.class);
        entityType.put("rock", RockPowerup.class);
        entityType.put("pistol", PistolPowerup.class);
        entityType.put("grenade", GrenadePowerup.class);
        entityType.put("time", TimePowerup.class);
    }

    private JsonValue data;
    private double powerup_spawn_delay;
    private double powerup_init_delay;
    private int powerupsToSpawn;
    private int spawnedPowerups;

    private double powerupInitDelayTimer;
    private double powerupSpawnTimer;

    private AnimatableGraphicsEntity animatableGraphicsEntity;
    private MovableEntity movableEntity;

    private boolean isDroppingCrates = false;

    public Helicopter(JsonValue data)
    {
        this.data = data;
        this.powerup_spawn_delay = this.data.getDouble("powerup_spawn_delay")*1000;
        this.powerup_init_delay = this.data.getDouble("powerup_init_delay")*1000;
        this.powerupsToSpawn = this.data.get("powerups").size;

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("effects/helicopter/helicopter.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("effects/helicopter/helicopter.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        //state.setTimeScale(0.7f); // Slow all animations down to 70% speed.

        state.addListener(new AnimationState.AnimationStateAdapter()
        {

        });

        this.animatableGraphicsEntity = new AnimatableGraphicsEntity(skeleton, state, atlas);
        this.animatableGraphicsEntity.setAnimation("default");

        this.movableEntity = new MovableEntity(this.animatableGraphicsEntity);

        this.moveTo(new Vector2(-20, 8));
        this.setSpeed(10);
    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.animatableGraphicsEntity.draw(batch, skeletonRenderer);
    }

    @Override
    public void update(float delta)
    {
        this.animatableGraphicsEntity.update(delta);
        this.movableEntity.update(delta);

        if(!this.isInLevel())
        {
            this.powerupInitDelayTimer = System.currentTimeMillis();
            this.powerupSpawnTimer = System.currentTimeMillis();
        }
        else if(System.currentTimeMillis() - this.powerupInitDelayTimer >= this.powerup_init_delay)
        {
            if(System.currentTimeMillis() - this.powerupSpawnTimer >= this.powerup_spawn_delay && this.spawnedPowerups < this.powerupsToSpawn)
            {
                this.clearMoveQueue();
                this.spawnCrate(this.data.get("powerups").get(this.spawnedPowerups).asString());
                this.powerupSpawnTimer = System.currentTimeMillis();
            }
            else if(this.spawnedPowerups >= this.powerupsToSpawn)
                this.moveTo(new Vector2(-20, 8));
        }

    }

    private void spawnCrate(String powerup)
    {
        try
        {
            com.fcfruit.zombiesmash.powerups.PowerupCrate tempCrate;
            com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface tempPowerup;
            tempPowerup = (com.fcfruit.zombiesmash.entity.interfaces.PowerupInterface) entityType.get(powerup).getDeclaredConstructor().newInstance();
            tempCrate = new com.fcfruit.zombiesmash.powerups.PowerupCrate(tempPowerup);

            tempCrate.setPosition(new Vector2(this.getPosition().x + this.getSize().x/2, this.getPosition().y));
            tempCrate.changeToGround(1);

            Environment.drawableAddQueue.add(tempCrate);

            this.spawnedPowerups += 1;

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Gdx.app.debug("Spawner", "Added Crate");
    }

    private boolean isInLevel()
    {
        Helicopter i = this;

        return i.getPosition().x > Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2 - 0.5f
                && i.getPosition().x < Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth / 2 + 0.5f
                && i.getPosition().y > Environment.physicsCamera.position.y - Environment.physicsCamera.viewportHeight / 2 - 0.5f
                && i.getPosition().y < Environment.physicsCamera.position.y + Environment.physicsCamera.viewportHeight / 2 + 0.5f;
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
    public void clearMoveQueue()
    {
        this.movableEntity.clearMoveQueue();
    }

    @Override
    public void setSpeed(float speed)
    {
        this.movableEntity.setSpeed(speed);
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
    public float getAlpha()
    {
        return this.animatableGraphicsEntity.getAlpha();
    }

    @Override
    public void setAlpha(float alpha)
    {
        this.animatableGraphicsEntity.setAlpha(alpha);
    }

    @Override
    public Vector2 getSize()
    {
        return this.animatableGraphicsEntity.getSize();
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
    public int timesAnimationCompleted()
    {
        return this.animatableGraphicsEntity.timesAnimationCompleted();
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
    public void dispose()
    {
        this.animatableGraphicsEntity.dispose();
    }

    // Unused
    @Override
    public void draw(SpriteBatch batch)
    {

    }

}
