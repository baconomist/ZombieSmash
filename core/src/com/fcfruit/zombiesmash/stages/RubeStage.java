package com.fcfruit.zombiesmash.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.physics.PhysicsData;
import com.fcfruit.zombiesmash.rube.RubeScene;
import com.fcfruit.zombiesmash.rube.loader.RubeSceneLoader;
import com.fcfruit.zombiesmash.rube.loader.serializers.utils.RubeImage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.fcfruit.zombiesmash.ui.CheckBox;
import com.fcfruit.zombiesmash.ui.ImageButton;
import com.fcfruit.zombiesmash.ui.MultiImageSlider;
import com.fcfruit.zombiesmash.ui.UIPhysicsEntity;

import java.util.ArrayList;

public class RubeStage extends Stage
{
    private String rootPath;
    private boolean physicsEnabled;

    private RubeSceneLoader rubeSceneLoader;
    private RubeScene rubeScene;

    private ArrayList<Actor> drawOrder;

    public RubeStage(Viewport viewport, String rubeSceneFilePath, String rootPath, boolean physicsEnabled)
    {
        super(viewport);

        this.rootPath = rootPath;
        this.physicsEnabled = physicsEnabled;

        if(physicsEnabled)
            this.rubeSceneLoader = new RubeSceneLoader(Environment.physics.getWorld());
        else
            this.rubeSceneLoader = new RubeSceneLoader();

        this.drawOrder = new ArrayList<Actor>();

        this.loadScene(rubeSceneFilePath);
    }

    private void loadScene(String filePath)
    {
        String name;
        Actor actor;
        boolean button;
        boolean checkbox;
        boolean vertical_slider;
        boolean horizontal_slider;

        this.rubeScene = this.rubeSceneLoader.loadScene(Gdx.files.internal(filePath));
        for(RubeImage image : this.rubeScene.getImages())
        {
            name = (String) rubeScene.getCustom(image, "name");
            button = (rubeScene.getCustom(image, "button") != null ? (Boolean) rubeScene.getCustom(image, "button") : false);
            checkbox = (rubeScene.getCustom(image, "checkbox") != null ? (Boolean) rubeScene.getCustom(image, "checkbox") : false);
            vertical_slider = (rubeScene.getCustom(image, "vertical_slider") != null ? (Boolean) rubeScene.getCustom(image, "vertical_slider") : false);
            horizontal_slider = (rubeScene.getCustom(image, "horizontal_slider") != null ? (Boolean) rubeScene.getCustom(image, "horizontal_slider") : false);

            if(name != null)
            {
                if (button)
                    actor = this.createImageButton(image);
                else if (checkbox)
                    actor = this.createCheckBox(image);
                else if(vertical_slider || horizontal_slider)
                    actor = this.createMultiImageSlider(image, horizontal_slider);
                else
                    actor = this.createImage(image);

                if(image.body != null)
                    actor = this.createUIPhysicsEntity(actor, image.body);

                actor.setName((String) this.rubeScene.getCustom(image, "name"));
                drawOrder.add(image.renderOrder, actor);
            }
            else
            {
                Gdx.app.error("RubeStage", "Null Image Name, Creating Image...");
                actor = this.createImage(image);
                actor.setName(image.file.split("/")[image.file.split("/").length - 1]);
                drawOrder.add(image.renderOrder, actor);
            }
        }

        if(this.physicsEnabled)
        {
            for (Body body : this.rubeScene.getBodies())
            {
                if (body.getUserData() == null)
                    body.setUserData(new PhysicsData(this));
                for (Fixture fixture : body.getFixtureList())
                    if (fixture.getUserData() == null)
                        fixture.setUserData(new PhysicsData(this));
            }
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

    private MultiImageSlider createMultiImageSlider(RubeImage image, boolean horizontal)
    {
        String filename = image.file.split("/")[image.file.split("/").length - 1];

        MultiImageSlider multiImageSlider = new MultiImageSlider(new Sprite(new Texture(this.rootPath + filename)),
                new Sprite(new Texture(this.rootPath + rubeScene.getCustom(image, "slider_image"))), horizontal);

        Vector3 size = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.width, image.height, 0)));
        size.y = this.getViewport().getCamera().position.y*2 - size.y;
        multiImageSlider.setSize(size.x, size.y);

        Vector3 pos = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.center, 0)));
        pos.y = this.getViewport().getCamera().position.y*2 - pos.y;
        multiImageSlider.setPosition(pos.x-size.x/2, pos.y-size.y/2);

        return  multiImageSlider;
    }

    private Image createImage(RubeImage image)
    {
        String filename = image.file.split("/")[image.file.split("/").length - 1];

        Image img = new Image(new Texture(Gdx.files.internal(this.rootPath + filename)));

        Vector3 size = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.width, image.height, 0)));
        size.y = this.getViewport().getCamera().position.y*2 - size.y;
        img.setSize(size.x, size.y);

        Vector3 pos = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.center, 0)));
        pos.y = this.getViewport().getCamera().position.y*2 - pos.y;
        img.setPosition(pos.x-size.x/2, pos.y-size.y/2);

        return img;
    }

    private CheckBox createCheckBox(RubeImage image)
    {
        String filename = image.file.split("/")[image.file.split("/").length - 1];
        String checked_image_filename = (String) this.rubeScene.getCustom(image, "checked_image");

        CheckBox checkBox = new CheckBox(new Sprite(new Texture(Gdx.files.internal(this.rootPath + filename))),
                new Sprite(new Texture(Gdx.files.internal(this.rootPath + checked_image_filename))));

        Vector3 size = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.width, image.height, 0)));
        size.y = this.getViewport().getCamera().position.y*2 - size.y;
        checkBox.setSize(size.x, size.y);

        Vector3 pos = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.center, 0)));
        pos.y = this.getViewport().getCamera().position.y*2 - pos.y;
        checkBox.setPosition(pos.x-size.x/2, pos.y-size.y/2);

        return checkBox;
    }

    private ImageButton createImageButton(RubeImage image)
    {
        String filename = image.file.split("/")[image.file.split("/").length - 1];

        ImageButton imageButton = new ImageButton(new Sprite(new Texture(Gdx.files.internal(this.rootPath + filename))));

        Vector3 size = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.width, image.height, 0)));
        size.y = this.getViewport().getCamera().position.y*2 - size.y;
        imageButton.setSize(size.x, size.y);

        Vector2 img_pos;
        if(image.body != null)
            img_pos = image.body.getPosition();
        else
            img_pos = image.center;

        Vector3 pos = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(img_pos, 0)));
        pos.y = this.getViewport().getCamera().position.y*2 - pos.y;
        imageButton.setPosition(pos.x-size.x/2, pos.y-size.y/2);

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
