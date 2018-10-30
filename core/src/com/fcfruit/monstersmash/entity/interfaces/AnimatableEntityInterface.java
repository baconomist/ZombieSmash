package com.fcfruit.monstersmash.entity.interfaces;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;

/**
 * Created by Lucas on 2018-02-11.
 */

public interface AnimatableEntityInterface extends UpdatableEntityInterface,  DrawableEntityInterface
{
    Skeleton getSkeleton();
    AnimationState getState();
    TextureAtlas getAtlas();

    int timesAnimationCompleted();
    void setAnimation(String animation);
    String getCurrentAnimation();
}
