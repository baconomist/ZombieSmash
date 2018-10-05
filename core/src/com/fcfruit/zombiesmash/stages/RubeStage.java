package com.fcfruit.zombiesmash.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.physics.PhysicsData;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.fcfruit.zombiesmash.ui.ImageButton;
import com.fcfruit.zombiesmash.ui.UIPhysicsEntity;

import java.util.ArrayList;
import java.util.Collections;

public class RubeStage extends Stage
{
    private String rootPath;

    private RubeSceneLoader rubeSceneLoader;
    private RubeScene rubeScene;

    private ArrayList<Actor> drawOrder;

    public RubeStage(Viewport viewport, String rubeSceneFilePath, String rootPath, boolean physics)
    {
        super(viewport);

        this.rootPath = rootPath;

        if(physics)
            this.rubeSceneLoader = new RubeSceneLoader(Environment.physics.getWorld());
        else
            this.rubeSceneLoader = new RubeSceneLoader();

        this.drawOrder = new ArrayList<Actor>();

        this.loadScene(rubeSceneFilePath);
    }

    private void loadScene(String filePath)
    {
        String name;
        Actor actor = null;

        this.rubeScene = this.rubeSceneLoader.loadScene(Gdx.files.internal(filePath));
        for(RubeImage image : this.rubeScene.getImages())
        {
            name = (String) rubeScene.getCustom(image, "name");
            if(name != null)
            {
                if (name.contains("button"))
                    actor = this.createImageButton(image);
                else
                    actor = this.createImage(image);

                if(image.body != null)
                    actor = this.createUIPhysicsEntity(actor, image.body);

                actor.setName((String) this.rubeScene.getCustom(image, "name"));
                drawOrder.add(image.renderOrder, actor);
            }
            else
                Gdx.app.error("RubeStage", "Null Image Name");
        }

        for(Body body : this.rubeScene.getBodies())
        {
            if(body.getUserData() == null)
                body.setUserData(new PhysicsData(this));
            for(Fixture fixture : body.getFixtureList())
                if(fixture.getUserData() == null)
                    fixture.setUserData(new PhysicsData(this));
        }

        this.manageDrawOrder();

    }

    private void manageDrawOrder()
    {
        for(Actor actor : this.drawOrder)
        {
            this.addActor(actor);
        }
    }

    private Image createImage(RubeImage image)
    {
        Image img = new Image(new Texture(Gdx.files.internal(this.rootPath + ((String) this.rubeScene.getCustom(image, "name")) + ".png")));
        img.setSize(image.width*Physics.PIXELS_PER_METER, image.height*Physics.PIXELS_PER_METER);

        Vector3 pos = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.center, 0)));
        pos.y = this.getViewport().getCamera().position.y*2 - pos.y;

        img.setPosition(pos.x-image.width*Physics.PIXELS_PER_METER/2, pos.y-image.height*Physics.PIXELS_PER_METER/2);

        return img;
    }

    private ImageButton createImageButton(RubeImage image)
    {
        String name = (String) this.rubeScene.getCustom(image, "name");
        ImageButton imageButton = new ImageButton(new Sprite(new Texture(Gdx.files.internal(this.rootPath + name + ".png"))));
        imageButton.setSize(image.width*Physics.PIXELS_PER_METER, image.height*Physics.PIXELS_PER_METER);

        Vector2 img_pos;
        if(image.body != null)
            img_pos = image.body.getPosition();
        else
            img_pos = image.center;

        Vector3 pos = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(img_pos, 0)));
        pos.y = this.getViewport().getCamera().position.y*2 - pos.y;
        imageButton.setPosition(pos.x-image.width*Physics.PIXELS_PER_METER/2, pos.y-image.height*Physics.PIXELS_PER_METER/2);

        return imageButton;
    }

    private UIPhysicsEntity createUIPhysicsEntity(Actor actor, Body body)
    {
        body.setUserData(new PhysicsData(actor));
        for(Fixture fixture : body.getFixtureList())
        {
            fixture.setUserData(new PhysicsData(actor));
        }

        return new UIPhysicsEntity(actor, body);
    }

    public Actor findActor(String name)
    {
        for(Actor actor : this.getActors())
        {
            if(actor.getName().equals(name))
                return actor;
        }
        return null;
    }

    public RubeScene getRubeScene()
    {
        return this.rubeScene;
    }
}
