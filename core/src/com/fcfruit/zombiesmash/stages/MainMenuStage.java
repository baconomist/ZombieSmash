package com.fcfruit.zombiesmash.stages;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.AnimatableGraphicsEntity;
import com.fcfruit.zombiesmash.entity.OptimizableEntity;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.InteractiveEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.OptimizableEntityInterface;
import com.fcfruit.zombiesmash.physics.PhysicsData;
import com.fcfruit.zombiesmash.zombies.MenuRegZombie;
import com.fcfruit.zombiesmash.zombies.RegZombie;

import org.apache.tools.ant.taskdefs.Java;


/**
 * Created by Lucas on 2017-12-14.
 */

public class MainMenuStage extends RubeStage
{

    private MenuRegZombie regZombie;
    private SpriteBatch spriteBatch;
    private SkeletonRenderer skeletonRenderer;

    private Array<DrawableEntityInterface> drawableEntities;

    public MainMenuStage(Viewport viewport, String rubeSceneFilePath, String rootPath, boolean physics)
    {
        super(viewport, rubeSceneFilePath, rootPath, physics);

        this.regZombie = new MenuRegZombie();
        this.regZombie.setup();
        this.regZombie.setAnimation("idle");

        String name;
        for(Body body : this.getRubeScene().getBodies())
        {
            name = (String) this.getRubeScene().getCustom(body, "name");
            if(name.equals("zombie_pos"))
                this.regZombie.setPosition(body.getPosition());
            else if(name.equals("zombie_ground"))
                body.setUserData(new PhysicsData("ground"));
        }

        this.spriteBatch = new SpriteBatch();
        this.skeletonRenderer = new SkeletonRenderer();

        this.drawableEntities = new Array<DrawableEntityInterface>();

        this.findActor("play_button").addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Environment.setupGame(2);
                Environment.game.setScreen(Environment.screens.gamescreen);
                System.gc();
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

    @Override
    public void draw()
    {
        super.draw();
        spriteBatch.setProjectionMatrix(this.getViewport().getCamera().combined);
        spriteBatch.begin();
            this.regZombie.draw(spriteBatch, skeletonRenderer);
            for(DrawableEntityInterface drawableEntityInterface : this.drawableEntities)
            {
                drawableEntityInterface.draw(spriteBatch);
                drawableEntityInterface.draw(spriteBatch, skeletonRenderer);
            }
        spriteBatch.end();
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        this.regZombie.update(delta);

        for(DrawableEntityInterface drawableEntityInterface : Environment.drawableBackgroundAddQueue)
        {
            this.drawableEntities.add(drawableEntityInterface);
        }
        Environment.drawableBackgroundAddQueue.clear();

        for(DrawableEntityInterface drawableEntityInterface : Environment.drawableAddQueue)
        {
            this.drawableEntities.add(drawableEntityInterface);
        }
        Environment.drawableAddQueue.clear();

        for(DrawableEntityInterface drawableEntityInterface : Environment.drawableRemoveQueue)
        {
            this.drawableEntities.removeValue(drawableEntityInterface, true);
        }
        Environment.drawableRemoveQueue.clear();

        for(DrawableEntityInterface drawableEntityInterface : this.drawableEntities)
        {
            drawableEntityInterface.update(delta);
            if(drawableEntityInterface instanceof OptimizableEntityInterface)
                // Disable optimization for parts to prevent destroyableEntity from destroying detached zombie parts
                ((OptimizableEntityInterface) drawableEntityInterface).disable_optimization();
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        Environment.touchedDownItems.clear();

        this.regZombie.onTouchDown(screenX, screenY, pointer);
        for(DrawableEntityInterface drawableEntityInterface : this.drawableEntities)
        {
            if(drawableEntityInterface instanceof InteractiveEntityInterface)
                ((InteractiveEntityInterface) drawableEntityInterface).onTouchDown(screenX, screenY, pointer);
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        this.regZombie.onTouchDragged(screenX, screenY, pointer);
        for(DrawableEntityInterface drawableEntityInterface : this.drawableEntities)
        {
            if(drawableEntityInterface instanceof InteractiveEntityInterface)
                ((InteractiveEntityInterface) drawableEntityInterface).onTouchDragged(screenX, screenY, pointer);
        }
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        this.regZombie.onTouchUp(screenX, screenY, pointer);
        for(DrawableEntityInterface drawableEntityInterface : this.drawableEntities)
        {
            if(drawableEntityInterface instanceof InteractiveEntityInterface)
                ((InteractiveEntityInterface) drawableEntityInterface).onTouchUp(screenX, screenY, pointer);
        }
        return super.touchUp(screenX, screenY, pointer, button);
    }
}
