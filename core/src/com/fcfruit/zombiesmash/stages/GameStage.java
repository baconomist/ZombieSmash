package com.fcfruit.zombiesmash.stages;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.screens.GameScreen;
import com.fcfruit.zombiesmash.zombies.RegularZombie;
import com.fcfruit.zombiesmash.zombies.Zombie;

/**
 * Created by Lucas on 2017-08-05.
 */

public class GameStage extends Stage {

    public Viewport viewport;

    public GameScreen gameScreen;

    private Zombie regZombie;

    private SpriteBatch spriteBatch;

    private SkeletonRenderer skeletonRenderer;

    private ShapeRenderer shapeRenderer;

    private float delta;

    public GameStage(Viewport v, GameScreen gmscrn){
        super(v);

        viewport = v;

        gameScreen = gmscrn;

        regZombie = new RegularZombie();
        regZombie.setPosition(300, 300);

        spriteBatch = new SpriteBatch();

        skeletonRenderer = new SkeletonRenderer();

        shapeRenderer = new ShapeRenderer();

        gameScreen.physics.addBody(regZombie);

    }

    @Override
    public void draw() {
        super.draw();

        delta = Gdx.graphics.getDeltaTime();

        regZombie.update(delta);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.polygon(regZombie.getBody().getParts().get("head").getPolygon().getTransformedVertices());
        shapeRenderer.polygon(regZombie.getBody().getParts().get("left_arm").getPolygon().getTransformedVertices());
        shapeRenderer.polygon(regZombie.getBody().getParts().get("torso").getPolygon().getTransformedVertices());
        shapeRenderer.polygon(regZombie.getBody().getParts().get("right_arm").getPolygon().getTransformedVertices());
        shapeRenderer.polygon(regZombie.getBody().getParts().get("left_leg").getPolygon().getTransformedVertices());
        shapeRenderer.polygon(regZombie.getBody().getParts().get("right_leg").getPolygon().getTransformedVertices());
        shapeRenderer.end();


        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        skeletonRenderer.draw(spriteBatch, regZombie.getBody().getSkeleton());
        spriteBatch.end();

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
