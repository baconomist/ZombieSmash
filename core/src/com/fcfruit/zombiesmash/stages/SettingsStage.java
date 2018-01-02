package com.fcfruit.zombiesmash.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Settings;
import com.fcfruit.zombiesmash.screens.MainMenu;
import com.fcfruit.zombiesmash.ui.Slider;

/**
 * Created by Lucas on 2017-12-17.
 */

public class SettingsStage extends Stage {

    MainMenu mainMenu;

    Sprite background;

    ImageButton gore_on;
    ImageButton gore_off;

    Sprite selector;

    ImageButton backButton;

    SpriteBatch spriteBatch;

    Slider volume_slider;

    JsonReader json;
    JsonValue data;

    boolean sliderTouched = false;

    public SettingsStage(Viewport v, MainMenu m){
        super(v);
        mainMenu = m;

        json = new JsonReader();
        data = json.parse(Gdx.files.internal("gui/settings/settings.json"));

        background = new Sprite(new Texture(Gdx.files.internal("gui/settings/background.png")));


        gore_on = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/settings/gore_on.png")))));
        gore_on.setPosition(data.get("gore_on").getFloat("x"), data.get("gore_on").getFloat("y"));
        gore_on.addListener(new ClickListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                selector.setPosition(gore_on.getX() + gore_on.getWidth()/2 - selector.getWidth()/2, gore_on.getY() + gore_on.getHeight());
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        gore_off = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/settings/gore_off.png")))));
        gore_off.setPosition(data.get("gore_off").getFloat("x"), data.get("gore_off").getFloat("y"));
        gore_off.addListener(new ClickListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                selector.setPosition(gore_off.getX() + gore_off.getWidth()/2 - selector.getWidth()/2, gore_off.getY() + gore_off.getHeight());
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        selector = new Sprite(new Texture(Gdx.files.internal("gui/settings/selector.png")));
        selector.setPosition(gore_on.getX() + gore_on.getWidth()/2 - selector.getWidth()/2, gore_on.getY() + gore_on.getHeight());

        backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gui/settings/back_button.png")))));
        backButton.setPosition(data.get("back_button").getFloat("x"), data.get("back_button").getFloat("y"));
        backButton.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainMenu.show_settings_stage = false;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        volume_slider = new Slider(new Sprite(new Texture(Gdx.files.internal("gui/settings/volume.png"))), new Sprite(new Texture(Gdx.files.internal("gui/settings/volume.png"))));
        volume_slider.setPosition(data.get("volume_slider").getFloat("x"), data.get("volume_slider").getFloat("y"));

        spriteBatch = new SpriteBatch();

        addActor(gore_on);
        addActor(gore_off);
        addActor(backButton);

    }

    @Override
    public void draw(){
        spriteBatch.setProjectionMatrix(getCamera().combined);
        spriteBatch.begin();
        background.draw(spriteBatch);
        selector.draw(spriteBatch);
        volume_slider.draw(spriteBatch);
        spriteBatch.end();
        super.draw();
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 pos = getCamera().unproject(new Vector3(screenX, screenY, 0));
        if(volume_slider.contains(pos.x, pos.y)){
            volume_slider.setPercent(((pos.x - volume_slider.getX() )/ volume_slider.getWidth())*100);
            sliderTouched = true;
        }

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 pos = getCamera().unproject(new Vector3(screenX, screenY, 0));
        if(sliderTouched) {
            volume_slider.setPercent(((pos.x - volume_slider.getX()) / volume_slider.getWidth()) * 100);
        }
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        sliderTouched = false;
        return super.touchUp(screenX, screenY, pointer, button);
    }
}
