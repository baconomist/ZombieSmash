package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-11-18.
 */

public class Level
{

    public static HashMap<String, Vector2> positions = new HashMap<String, Vector2>();

    static
    {
        positions.put("left", new Vector2(Environment.physicsCamera.viewportWidth / 2, 0));
        positions.put("right", new Vector2(Environment.physicsCamera.viewportWidth * 1.4f, 0));
        positions.put("middle", new Vector2(10, 0));
    }

    public int level_id;

    JsonReader json;
    JsonValue data;

    Sprite sprite;

    public Objective objective;

    private ArrayList<DrawableEntityInterface> drawableEntities;

    public int starsTouched = 0;

    boolean levelEnd = false;

    ArrayList<Spawner> spawners;

    String currentCameraPosition;

    int currentJsonItem;

    boolean zombiesDead = false;

    boolean isCameraMoving = false;

    public Level(int level_id)
    {
        this.level_id = level_id;
        this.currentJsonItem = 0;
        this.drawableEntities = new ArrayList<DrawableEntityInterface>();
    }


    public void draw(SpriteBatch batch, SkeletonRenderer skeletonRenderer)
    {
        this.sprite.draw(batch);

        for (DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            drawableEntity.draw(batch);
            drawableEntity.draw(batch, skeletonRenderer);
        }

    }

    public void onTouchDown(float x, float y, int pointer)
    {

        Collections.reverse(this.drawableEntities);

        for(DrawableEntityInterface drawableEntity : this.drawableEntities){
            if(drawableEntity instanceof InteractiveEntityInterface){
                ((InteractiveEntityInterface) drawableEntity).onTouchDown(x, y, pointer);
            }
        }

        Collections.reverse(this.drawableEntities);

    }

    public void onTouchDragged(float x, float y, int pointer)
    {
        for (DrawableEntityInterface drawableEntity : drawableEntities)
        {
            if (drawableEntity instanceof InteractiveEntityInterface)
            {
                ((InteractiveEntityInterface) drawableEntity).onTouchDragged(x, y, pointer);
            }
        }
    }

    public void onTouchUp(float x, float y, int pointer)
    {
        for (DrawableEntityInterface drawableEntity : drawableEntities)
        {
            if (drawableEntity instanceof InteractiveEntityInterface)
            {
                ((InteractiveEntityInterface) drawableEntity).onTouchUp(x, y, pointer);
            }
        }
    }

    public void update(float delta)
    {
        // Can't be put in draw bcuz it takes too long
        // When it takes too long in a spritebatch call,
        // it doesn't draw the sprites, only the light
        //Environment.physics.update(Gdx.graphics.getDeltaTime());


        for (DrawableEntityInterface drawableEntity : this.drawableEntities)
        {
            drawableEntity.update(delta);

            if (drawableEntity instanceof DetachableEntityInterface)
            {
                if (((DetachableEntityInterface) drawableEntity).getState().equals("waiting_for_detach"))
                {
                    ((DetachableEntityInterface) drawableEntity).detach();
                }
            }

        }

    }

    public void updateCamera()
    {
        Environment.physicsCamera.update();
        Environment.gameCamera.position.x = Environment.physicsCamera.position.x * 192f;
        Environment.gameCamera.update();
        Environment.physics.constructPhysicsBoundries();
    }

    public void addDrawableEntity(DrawableEntityInterface drawableEntity)
    {
        this.drawableEntities.add(drawableEntity);
    }

    public ArrayList<DrawableEntityInterface> getDrawableEntities()
    {
        return this.drawableEntities;
    }

    public void clear()
    {
        for (DrawableEntityInterface drawableEntity : drawableEntities)
        {
            drawableEntity.dispose();
        }
        drawableEntities = new ArrayList<DrawableEntityInterface>();
    }


}




























