package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.AnimatableGraphicsEntity;

/**
 * Created by Lucas on 2017-11-18.
 */

public class NightLevel extends Level
{

    private AnimatableGraphicsEntity clouds;

    public NightLevel(int level_id)
    {

        super(level_id);

        cameraPositions.put("left", new Vector2(8.9f, 0));
        cameraPositions.put("right", new Vector2(22.6f, 0));
        cameraPositions.put("middle", new Vector2(15.9f, 0));

        this.sprite = new Sprite(Environment.assets.get("maps/night_map/night_map.atlas", TextureAtlas.class).findRegion("night_map"));
        // Move to the left to show only playable map
        this.sprite.setPosition(-956, 0);
        this.sprite.setSize(8000, 8000);

        this.objective = new House();
        this.objective.setPosition(13f, 0.6f);

        TextureAtlas atlas = Environment.assets.get("effects/clouds/clouds.atlas");
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1); // Load the skeleton at 100% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("effects/clouds/clouds.json"));
        Skeleton skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        AnimationState state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(0.3f); // Slow all animations down to 70% speed.

        this.clouds = new AnimatableGraphicsEntity(skeleton, state, atlas);
        this.clouds.setPosition(this.objective.getPosition());
        this.clouds.setAnimation("default");

    }

    @Override
    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        super.draw(batch, skeletonRenderer);
        this.clouds.draw(batch, skeletonRenderer);
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        this.clouds.update(delta);
    }
}
