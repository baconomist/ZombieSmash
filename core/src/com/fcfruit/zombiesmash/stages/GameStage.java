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

    }

    @Override
    public void draw() {
        super.draw();

        delta = Gdx.graphics.getDeltaTime();

        regZombie.update(delta);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.polygon(regZombie.body.headBox.getTransformedVertices());
        shapeRenderer.polygon(regZombie.body.leftArmBox.getTransformedVertices());
        shapeRenderer.polygon(regZombie.body.torsoBox.getTransformedVertices());
        shapeRenderer.polygon(regZombie.body.rightArmBox.getTransformedVertices());
        shapeRenderer.polygon(regZombie.body.leftLegBox.getTransformedVertices());
        shapeRenderer.polygon(regZombie.body.rightLegBox.getTransformedVertices());
        shapeRenderer.end();


        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        skeletonRenderer.draw(spriteBatch, regZombie.body.skeleton);
        spriteBatch.end();

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
