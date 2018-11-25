package com.fcfruit.monstersmash.stages;

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
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.physics.PhysicsData;
import com.fcfruit.monstersmash.rube.RubeScene;
import com.fcfruit.monstersmash.rube.loader.RubeSceneLoader;
import com.fcfruit.monstersmash.rube.loader.serializers.utils.RubeImage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.fcfruit.monstersmash.ui.CheckBox;
import com.fcfruit.monstersmash.ui.ImageButton;
import com.fcfruit.monstersmash.ui.MultiImageSlider;
import com.fcfruit.monstersmash.ui.UIPhysicsEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class RubeStage extends Stage
{
    private String rootPath;
    private boolean physicsEnabled;

    private RubeSceneLoader rubeSceneLoader;
    private RubeScene rubeScene;

    // Need to do <Actor, Integer> or else actors with the same draw order will replace
    // one another.
    private HashMap<Actor, Integer> drawOrder;

    public RubeStage(Viewport viewport, String rubeSceneFilePath, String rootPath, boolean physicsEnabled)
    {
        super(viewport);

        this.rootPath = rootPath;
        this.physicsEnabled = physicsEnabled;

        if (physicsEnabled)
            this.rubeSceneLoader = new RubeSceneLoader(Environment.physics.getWorld());
        else
            this.rubeSceneLoader = new RubeSceneLoader();

        this.drawOrder = new HashMap<Actor, Integer>();

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
        boolean hidden;

        this.rubeScene = this.rubeSceneLoader.loadScene(Gdx.files.internal(filePath));
        for (RubeImage image : this.rubeScene.getImages())
        {
            name = (String) rubeScene.getCustom(image, "name");
            button = (rubeScene.getCustom(image, "button") != null ? (Boolean) rubeScene.getCustom(image, "button") : false);
            checkbox = (rubeScene.getCustom(image, "checkbox") != null ? (Boolean) rubeScene.getCustom(image, "checkbox") : false);
            vertical_slider = (rubeScene.getCustom(image, "vertical_slider") != null ? (Boolean) rubeScene.getCustom(image, "vertical_slider") : false);
            horizontal_slider = (rubeScene.getCustom(image, "horizontal_slider") != null ? (Boolean) rubeScene.getCustom(image, "horizontal_slider") : false);
            hidden = (rubeScene.getCustom(image, "hidden") != null ? (Boolean) rubeScene.getCustom(image, "hidden") : false);

            if (name != null)
            {
                if (button)
                    actor = this.createImageButton(image);
                else if (checkbox)
                    actor = this.createCheckBox(image);
                else if (vertical_slider || horizontal_slider)
                    actor = this.createMultiImageSlider(image, horizontal_slider);
                else
                    actor = this.createImage(image);

                if (image.body != null)
                    actor = this.createUIPhysicsEntity(actor, image.body);

                actor.setName((String) this.rubeScene.getCustom(image, "name"));
                this.drawOrder.put(actor, image.renderOrder);
            } else
            {
                Gdx.app.error("RubeStage", "Null Image Name, Creating Image...");
                actor = this.createImage(image);
                actor.setName(image.file.split("/")[image.file.split("/").length - 1]);
                this.drawOrder.put(actor, image.renderOrder);
            }

            actor.setVisible(!hidden);

        }

        if (this.physicsEnabled)
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
        Actor[] drawOrderActorsSorted = new Actor[this.drawOrder.keySet().size()];
        this.drawOrder.keySet().toArray(drawOrderActorsSorted);

        // Sort keys
        Actor temp;

        // Modified Algorithm from https://study.com/academy/lesson/how-to-sort-an-array-in-java.html
        for (int i = 1; i < drawOrderActorsSorted.length; i++)
        {
            for (int x = i; x > 0; x--)
            {
                if (this.drawOrder.get(drawOrderActorsSorted[x]) < this.drawOrder.get(drawOrderActorsSorted[x - 1]))
                {
                    temp = drawOrderActorsSorted[x];
                    drawOrderActorsSorted[x] = drawOrderActorsSorted[x - 1];
                    drawOrderActorsSorted[x - 1] = temp;
                }
            }
        }

        for(Actor actor : drawOrderActorsSorted)
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
        size.y = this.getViewport().getCamera().position.y * 2 - size.y;
        multiImageSlider.setSize(size.x, size.y);

        Vector3 pos = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.center, 0)));
        pos.y = this.getViewport().getCamera().position.y * 2 - pos.y;
        multiImageSlider.setPosition(pos.x - size.x / 2, pos.y - size.y / 2);

        return multiImageSlider;
    }

    private Image createImage(RubeImage image)
    {
        String filename = image.file.split("/")[image.file.split("/").length - 1];

        Image img = new Image(new Texture(Gdx.files.internal(this.rootPath + filename)));

        Vector3 size = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.width, image.height, 0)));
        size.y = this.getViewport().getCamera().position.y * 2 - size.y;
        img.setSize(size.x, size.y);

        Vector3 pos = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.center, 0)));
        pos.y = this.getViewport().getCamera().position.y * 2 - pos.y;
        img.setPosition(pos.x - size.x / 2, pos.y - size.y / 2);

        return img;
    }

    private com.fcfruit.monstersmash.ui.CheckBox createCheckBox(RubeImage image)
    {
        String filename = image.file.split("/")[image.file.split("/").length - 1];
        String checked_image_filename = (String) this.rubeScene.getCustom(image, "checked_image");

        com.fcfruit.monstersmash.ui.CheckBox checkBox = new com.fcfruit.monstersmash.ui.CheckBox(new Sprite(new Texture(Gdx.files.internal(this.rootPath + filename))),
                new Sprite(new Texture(Gdx.files.internal(this.rootPath + checked_image_filename))));

        Vector3 size = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.width, image.height, 0)));
        size.y = this.getViewport().getCamera().position.y * 2 - size.y;
        checkBox.setSize(size.x, size.y);

        Vector3 pos = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.center, 0)));
        pos.y = this.getViewport().getCamera().position.y * 2 - pos.y;
        checkBox.setPosition(pos.x - size.x / 2, pos.y - size.y / 2);

        return checkBox;
    }

    private com.fcfruit.monstersmash.ui.ImageButton createImageButton(RubeImage image)
    {
        String filename = image.file.split("/")[image.file.split("/").length - 1];

        com.fcfruit.monstersmash.ui.ImageButton imageButton = new com.fcfruit.monstersmash.ui.ImageButton(new Sprite(new Texture(Gdx.files.internal(this.rootPath + filename))));

        Vector3 size = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(image.width, image.height, 0)));
        size.y = this.getViewport().getCamera().position.y * 2 - size.y;
        imageButton.setSize(size.x, size.y);

        Vector2 img_pos;
        if (image.body != null)
            img_pos = image.body.getPosition();
        else
            img_pos = image.center;

        Vector3 pos = this.getViewport().getCamera().unproject(Environment.physicsCamera.project(new Vector3(img_pos, 0)));
        pos.y = this.getViewport().getCamera().position.y * 2 - pos.y;
        imageButton.setPosition(pos.x - size.x / 2, pos.y - size.y / 2);

        return imageButton;
    }

    private com.fcfruit.monstersmash.ui.UIPhysicsEntity createUIPhysicsEntity(Actor actor, Body body)
    {
        body.setUserData(new PhysicsData(actor));
        for (Fixture fixture : body.getFixtureList())
        {
            fixture.setUserData(new PhysicsData(actor));
        }

        return new com.fcfruit.monstersmash.ui.UIPhysicsEntity(actor, body);
    }

    public Actor findActor(String name)
    {
        for (Actor actor : this.getActors().toArray())
        {
            if (actor.getName() != null && actor.getName().equals(name))
                return actor;
        }
        return null;
    }

    public RubeScene getRubeScene()
    {
        return this.rubeScene;
    }
}
