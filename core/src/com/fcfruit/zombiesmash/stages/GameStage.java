package com.fcfruit.zombiesmash.stages;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.level.Level;
import com.fcfruit.zombiesmash.power_ups.PowerUp;
import com.fcfruit.zombiesmash.zombies.Part;
import com.fcfruit.zombiesmash.zombies.Zombie;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-08-05.
 */

public class GameStage extends Stage {

    public Viewport viewport;

    private SpriteBatch spriteBatch;

    private SkeletonRenderer skeletonRenderer;

    BitmapFont text;

    ShapeRenderer shapeRenderer;

    public GameStage(Viewport v){
        super(v);

        viewport = v;

        spriteBatch = new SpriteBatch();

        skeletonRenderer = new SkeletonRenderer();

        text = new BitmapFont(Gdx.files.internal("gui/defaultSkin/default.fnt"));
        shapeRenderer = new ShapeRenderer();
    }


    @Override
    public void draw() {
        super.draw();

        // Viewport.getCamera() != Environment.gameCamera
        spriteBatch.setProjectionMatrix(Environment.gameCamera.combined);

        spriteBatch.begin();
        Environment.level.draw(spriteBatch, skeletonRenderer);
        text.draw(spriteBatch, ""+Environment.level.objective.getHealth(), 100, 100);
        spriteBatch.end();

        shapeRenderer.setProjectionMatrix(Environment.gameCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for(Zombie z : Environment.physics.getZombies()) {
            for(Part p : z.getParts().values()) {
                //shapeRenderer.polygon(p.polygon.getTransformedVertices());
            }
        }
        for(Part p : Environment.physics.getParts()){
            //shapeRenderer.polygon(p.polygon.getTransformedVertices());
        }
        shapeRenderer.end();

        Environment.physics.update(Gdx.graphics.getDeltaTime());

        Environment.level.update();


    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        ArrayList<Part> touchedParts = new ArrayList<Part>();

        Vector3 pos = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));


        for(Zombie z : Environment.physics.getZombies()){
            for(Part p : z.getParts().values()) {
                if (p.polygon.contains(pos.x, pos.y)) {
                    touchedParts.add(p);
                }
            }

        }
        for(Part p : Environment.physics.getParts()){
            if (p.polygon.contains(pos.x, pos.y)) {
                touchedParts.add(p);
            }
        }


        if(touchedParts.size() > 0) {
            touchedParts.get(0).polygonTouched = true;
        }


        Vector3 vector = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
        Environment.physics.touchDown(vector.x, vector.y, pointer);
        Gdx.app.log("touch", ""+Environment.physicsCamera.unproject(new Vector3(0, Gdx.input.getY(), 0)).y);


        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 vector = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
        Environment.physics.touchDragged(vector.x, vector.y, pointer);

        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 vector = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
        Environment.physics.touchUp(vector.x, vector.y, pointer);
        return super.touchUp(screenX, screenY, pointer, button);
    }
}
