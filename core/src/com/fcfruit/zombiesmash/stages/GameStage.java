package com.fcfruit.zombiesmash.stages;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.power_ups.PowerUp;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Lucas on 2017-08-05.
 */

public class GameStage extends Stage
{

    public Viewport viewport;

    private SpriteBatch spriteBatch;

    private SkeletonRenderer skeletonRenderer;

    ShapeRenderer shapeRenderer;

    HashMap<Integer, Integer> lastScreenX;

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

        // Viewport.getCamera() != Environment.gameCamera
        this.spriteBatch.setProjectionMatrix(Environment.gameCamera.combined);

        this.spriteBatch.begin();
        Environment.level.draw(spriteBatch, skeletonRenderer);
        this.spriteBatch.end();

        Environment.physics.update(Gdx.graphics.getDeltaTime());
        Environment.level.update(Gdx.graphics.getDeltaTime());

    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {

        Vector3 pos = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
        pos.y = Environment.physicsCamera.viewportHeight - pos.y;
        Environment.level.onTouchDown(pos.x, pos.y, pointer);

        pos = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));
        lastScreenX.put(pointer, (int) pos.x);

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {

        if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS)
        {
            Vector3 pos = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));

            if (pos.x - lastScreenX.get(pointer) < 220 && pos.x - lastScreenX.get(pointer) > -220)
            {
                pos = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
                pos.y = Environment.physicsCamera.viewportHeight - pos.y;
                Environment.level.onTouchDragged(pos.x, pos.y, pointer);
            } else
            {
                this.touchUp(screenX, screenY, pointer, 0);
            }

            pos = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));
            lastScreenX.put(pointer, (int) pos.x);
        } else
        {
            Vector3 pos = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
            pos.y = Environment.physicsCamera.viewportHeight - pos.y;
            Environment.level.onTouchDragged(pos.x, pos.y, pointer);
        }


        return super.touchDragged(screenX, screenY, pointer);
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {

        Vector3 pos = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
        pos.y = Environment.physicsCamera.viewportHeight - pos.y;
        Environment.level.onTouchUp(pos.x, pos.y, pointer);

        return super.touchUp(screenX, screenY, pointer, button);
    }


}
