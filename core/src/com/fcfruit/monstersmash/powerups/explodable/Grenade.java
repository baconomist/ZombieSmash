package com.fcfruit.monstersmash.powerups.explodable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.ContainerEntity;
import com.fcfruit.monstersmash.entity.DestroyableEntity;
import com.fcfruit.monstersmash.entity.InteractivePhysicsEntity;
import com.fcfruit.monstersmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.ExplodableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.InteractivePhysicsEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.PostLevelDestroyableInterface;
import com.fcfruit.monstersmash.powerups.explodable.Explodable;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-01-07.
 */

public class Grenade extends Explodable
{
    public Grenade(Body body, ArrayList<Joint> joints)
    {
        super(body, joints);
    }

    public Grenade(Sprite sprite, Body physicsBody, ArrayList<Joint> joints, ContainerEntityInterface containerEntity)
    {
        super(sprite, physicsBody, joints, containerEntity);
    }
}
