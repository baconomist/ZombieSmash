package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.BleedablePoint;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.zombies.parts.SpecialPart;

import java.util.ArrayList;

public class Bone_BossZombie extends Zombie
{
    private float health = 100.0f;
    private boolean isAlive = true;

    /**
     * Bone Boss Zombie
     * Can only be killed with fire
     * Can be knocked down with grenades and explosives and rocks
     * **/

    public Bone_BossZombie(Integer id)
    {
        super(id);

        this.moveAnimation = "walk";
        this.setSpeed(1);
        this.setMoveDistance(3f);

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
    public void onSpawned()
    {
        Environment.musicManager.addMusic("bone_boss", Environment.assets.get("zombies/bone_boss_zombie/theme.mp3", Music.class));
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
        Environment.level.objective.takeDamage(25f);
    }

    @Override
    protected boolean hasRequiredPartsForGetup()
    {
        return this.isAlive(); // Boss zombie will always be able to get up as he doesn't have any detachable parts
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
        if(this.getDirection() == 0)
            this.moveTo(new Vector2(Environment.level.objective.getPosition().x + 0.1f, Environment.level.objective.getPosition().y));
        else
            this.moveTo(new Vector2(Environment.level.objective.getPosition().x + Environment.level.objective.getWidth() - 0.1f, Environment.level.objective.getPosition().y));
        this.shouldObjectiveOnce = false;
    }

    @Override
    public void onBurned()
    {
        this.bodyFire = null;
        this.health -= 15f;
        this.enable_physics();
        if(this.health <= 0)
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
       /* if(bodyName.equals("bike"))
        {*/
            SpecialPart part = new SpecialPart(bodyName, sprite, physicsBody, joints, containerEntity);
            this.getDrawableEntities().put(bodyName, part);
            this.getInteractiveEntities().put(bodyName, part);
            this.getDetachableEntities().put(bodyName, part);
        /*}
        else
            super.createPart(physicsBody, bodyName, sprite, joints, containerEntity, bleedablePoints);*/
    }
}
