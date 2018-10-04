package com.fcfruit.zombiesmash.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.screens.MainMenu;

/**
 * Created by Lucas on 2017-12-14.
 */

public class MainMenuStageOld extends Stage {

    MainMenu mainMenu;

    Sprite background;

    SpriteBatch spriteBatch;

    //Json json = new Json();

    boolean touching = false;

    Vector2 pos = new Vector2();

    ImageButton ui = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/main_menu/mute_button.png")))));

    ImageButton play_button;
    ImageButton settings_button;
    ImageButton mute_button;
    ImageButton rate_button;

    public boolean mute = false;

    private JsonReader json;
    private JsonValue data;

    public MainMenuStageOld(Viewport v, MainMenu m){
        super(v);

        mainMenu = m;

        background = new Sprite(new Texture(Gdx.files.internal("ui/main_menu/background.jpg")));
        background.setSize(1920, 1080);

        spriteBatch = new SpriteBatch();

        json = new JsonReader();
        data = json.parse(Gdx.files.internal("ui/main_menu/main_menu.json"));

        play_button = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/main_menu/play_button.png")))));
        play_button.setPosition(data.get("play_button").getFloat("x"), data.get("play_button").getFloat("y"));
        play_button.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Environment.game.setScreen(new com.fcfruit.zombiesmash.screens.LevelSelect());
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        settings_button = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/main_menu/settings_button.png")))));
        settings_button.setPosition(data.get("settings_button").getFloat("x"), data.get("settings_button").getFloat("y"));
        settings_button.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainMenu.show_settings_stage = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        mute_button = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/main_menu/mute_button.png")))), null, new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/main_menu/mute.png")))));
        mute_button.setPosition(data.get("mute_button").getFloat("x"), data.get("mute_button").getFloat("y"));
        mute_button.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mute = !mute;
                if(mute){
                    mute_button.setChecked(false);
                }
                else{
                    mute_button.setChecked(true);
                }

                return super.touchDown(event, x, y, pointer, button);
            }
        });

        rate_button = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/main_menu/rate_button.png")))));
        rate_button.setPosition(data.get("rate_button").getFloat("x"), data.get("rate_button").getFloat("y"));
        rate_button.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return super.touchDown(event, x, y, pointer, button);
            }
        });



        //addActor(screens);
        addActor(play_button);
        addActor(settings_button);
        addActor(mute_button);
        addActor(rate_button);

    }



    @Override
    public void draw() {

        spriteBatch.setProjectionMatrix(getViewport().getCamera().combined);
        spriteBatch.begin();
        background.draw(spriteBatch);
        spriteBatch.end();

       /* pos.x = screens.getX();
        pos.y = screens.getY();*/

        super.draw();
    }

    @Override
    public boolean keyDown(int keyCode) {
        /*json.setOutputType(JsonWriter.OutputType.json);
        Gdx.files.local("ui/main_menu/main_menu.json").writeString(json.prettyPrint(pos), true);
        Gdx.app.debug("write", ""+pos.x + " " + pos.y);*/
        return super.keyDown(keyCode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //screens.setPosition(screenX, getViewport().getScreenHeight() - screenY);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        //screens.setPosition(screenX, getViewport().getScreenHeight() - screenY);
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button);
    }
}
