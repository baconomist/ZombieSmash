package com.fcfruit.zombiesmash.stages;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.Config;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.powerups.time.TimePowerup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-08-05.
 */

public class GameStage extends Stage
{

    public Viewport viewport;

    private SpriteBatch spriteBatch;

    private SkeletonRenderer skeletonRenderer;

    HashMap<Integer, Integer> lastScreenX;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public GameStage(Viewport viewport)
    {
        super(viewport);

        this.viewport = viewport;

        this.spriteBatch = new SpriteBatch();

        this.skeletonRenderer = new SkeletonRenderer();

        this.shapeRenderer = new ShapeRenderer();

        this.lastScreenX = new HashMap<Integer, Integer>();
        this.lastScreenX.put(0, 0);

    }


    @Override
    public void draw()
    {
        super.draw();
        
        float delta = Gdx.graphics.getDeltaTime();

        if(Environment.powerupManager.isSlowMotionEnabled)
            delta = delta/TimePowerup.timeFactor;

        if(!Environment.isPaused)
            Environment.level.update(delta);
        // Viewport.getCamera() != Environment.gameCamera
        this.spriteBatch.setProjectionMatrix(Environment.gameCamera.combined);
        this.spriteBatch.begin();
        Environment.level.draw(spriteBatch, skeletonRenderer);
        this.spriteBatch.end();

        if(!Environment.isPaused)
            Environment.physics.update(delta);

        if(Config.DEBUG_POLYGONS)
        {
            this.shapeRenderer.setProjectionMatrix(Environment.gameCamera.combined);
            this.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (DrawableEntityInterface drawableEntity : Environment.level.getDrawableEntities())
            {
                if (drawableEntity instanceof InteractiveEntityInterface)
                {
                    this.shapeRenderer.polygon(((InteractiveEntityInterface) drawableEntity).getPolygon().getTransformedVertices());
                }
                if (drawableEntity instanceof ContainerEntityInterface)
                {
                    for (InteractiveEntityInterface InteractiveEntityInterface : ((ContainerEntityInterface) drawableEntity).getInteractiveEntities().values())
                    {
                        this.shapeRenderer.polygon(InteractiveEntityInterface.getPolygon().getTransformedVertices());
                    }
                }
            }
            this.shapeRenderer.polygon(Environment.level.objective.polygon.getTransformedVertices());
            for(int i = 0; i < Environment.level.objective.attack_zones.length; i++)
                this.shapeRenderer.polygon(Environment.level.objective.attack_zones[i].getTransformedVertices());
            this.shapeRenderer.end();
        }

    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        Environment.touchedDownItems = new ArrayList<com.fcfruit.zombiesmash.entity.interfaces.InputCaptureEntityInterface>();

        Environment.level.onTouchDown(screenX, screenY, pointer);

        lastScreenX.put(pointer, screenX);

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {

        if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS)
        {

            if (screenX - lastScreenX.get(pointer) < 220 && screenX - lastScreenX.get(pointer) > -220)
            {
                Environment.level.onTouchDragged(screenX, screenY, pointer);
            } else
            {
                this.touchUp(screenX, screenY, pointer, 0);
            }

            lastScreenX.put(pointer, screenX);
        } else
        {
            Environment.level.onTouchDragged(screenX, screenY, pointer);
        }


        return super.touchDragged(screenX, screenY, pointer);
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {

        Environment.level.onTouchUp(screenX, screenY, pointer);

        return super.touchUp(screenX, screenY, pointer, button);
    }


}
