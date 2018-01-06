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

public class GameStage extends Stage {

    public Viewport viewport;

    private SpriteBatch spriteBatch;

    private SkeletonRenderer skeletonRenderer;

    ShapeRenderer shapeRenderer;

    HashMap<Integer, Integer> lastScreenX;

    public GameStage(Viewport v){
        super(v);

        viewport = v;

        spriteBatch = new SpriteBatch();

        skeletonRenderer = new SkeletonRenderer();

        shapeRenderer = new ShapeRenderer();

        lastScreenX = new HashMap<Integer, Integer>();
        lastScreenX.put(0, 0);
    }


    @Override
    public void draw() {
        super.draw();

        // Viewport.getCamera() != Environment.gameCamera
        spriteBatch.setProjectionMatrix(Environment.gameCamera.combined);

        spriteBatch.begin();
        Environment.level.draw(spriteBatch, skeletonRenderer);
        spriteBatch.end();

        shapeRenderer.setProjectionMatrix(Environment.gameCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for(Zombie z : Environment.level.zombies) {
            //shapeRenderer.polygon(z.polygon.getTransformedVertices());
        }
        for(Part p : Environment.physics.getParts()){
            //shapeRenderer.polygon(p.polygon.getTransformedVertices());
        }
        //shapeRenderer.polygon(Environment.level.objective.polygon.getTransformedVertices());
        for(PowerUp pow : Environment.physics.getPowerUps()){
            //shapeRenderer.polygon(pow.polygon.getTransformedVertices());
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

        ArrayList<Zombie> touchedZombies = new ArrayList<Zombie>();
        ArrayList<Part> touchedParts = new ArrayList<Part>();

        Vector3 pos = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));

        Collections.reverse(Environment.level.zombies);
        Collections.reverse(Environment.level.parts);

        for (Zombie z : Environment.level.zombies) {
            if (z.polygon.contains(pos.x, pos.y)) {
                boolean partTouched = false;
                for (Part p : z.getParts().values()) {
                    if (p.polygon.contains(pos.x, pos.y)) {
                        touchedParts.add(p);
                        partTouched = true;
                    }
                }
                if (!partTouched) {
                    touchedZombies.add(z);
                }
            }

        }
        for (Part p : Environment.physics.getParts()) {
            if (p.polygon.contains(pos.x, pos.y)) {
                touchedParts.add(p);
            }
        }


        if (touchedParts.size() > 0) {
            touchedParts.get(0).polygonTouched = true;
            //touchedParts.get(0).polygonTouched = true;

        } else if (touchedZombies.size() > 0) {
            touchedZombies.get(0).getParts().get("torso").polygonTouched = true;
            //touchedZombies.get(0).getParts().get("torso").polygonTouched = true;
        }


        Collections.reverse(Environment.level.zombies);
        Collections.reverse(Environment.level.parts);

        pos = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
        Environment.physics.touchDown(pos.x, pos.y, pointer);

        Gdx.app.log("touchDown", ""+screenX);

        pos = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));
        lastScreenX.put(pointer, (int)pos.x);

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if(Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS) {
            Vector3 pos = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));

            if (pos.x - lastScreenX.get(pointer) < 220 && pos.x - lastScreenX.get(pointer) > -220) {
                pos = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
                Environment.physics.touchDragged(pos.x, pos.y, pointer);
                Gdx.app.log("touchDragged", "dragged " + pos.x);
            } else {
                Gdx.app.log("touchDragged", "" + (pos.x - lastScreenX.get(pointer)));
                touchUp(screenX, screenY, pointer, 0);
            }

            pos = Environment.gameCamera.unproject(new Vector3(screenX, screenY, 0));
            lastScreenX.put(pointer, (int) pos.x);
        }
        else{
            Vector3 pos = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
            Environment.physics.touchDragged(pos.x, pos.y, pointer);
        }


        return super.touchDragged(screenX, screenY, pointer);
    }



    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        Vector3 vector = Environment.physicsCamera.unproject(new Vector3(screenX, screenY, 0));
        Environment.physics.touchUp(vector.x, vector.y, pointer);

        Gdx.app.log("touchUp", ""+screenX);

        return super.touchUp(screenX, screenY, pointer, button);
    }

    Object getFirstMatch(ArrayList one, ArrayList two){

        for(Object o : one){
            for(Object obj : two){
                if(o.equals(obj)){
                    return o;
                }
            }
        }

        return null;

    }


}
