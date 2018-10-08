package com.fcfruit.zombiesmash.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.ui.CheckBox;
import com.fcfruit.zombiesmash.ui.MultiImageSlider;

public abstract class OptionsStage extends RubeStage
{
    public OptionsStage(Viewport viewport)
    {
        super(viewport, "ui/options_menu/options_menu.json", "ui/options_menu/", false);

        ((CheckBox) this.findActor("gore_checkbox")).setChecked(Environment.settings.isGoreEnabled());
        ((CheckBox) this.findActor("vibrations_checkbox")).setChecked(Environment.settings.isVibrationsEnabled());
        ((CheckBox) this.findActor("music_checkbox")).setChecked(Environment.settings.isMusicEnabled());
        ((MultiImageSlider) this.findActor("music_volume_slider")).setPercent(Environment.settings.getMusicVolume() * 100);
        ((CheckBox) this.findActor("sfx_checkbox")).setChecked(Environment.settings.isSfxEnabled());
        ((MultiImageSlider) this.findActor("sfx_volume_slider")).setPercent(Environment.settings.getSfxVolume() * 100);


        this.findActor("back_button").addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                onBackButton();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.findActor("music_checkbox").addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                CheckBox volumeCheckBox = (CheckBox) event.getListenerActor();
                Environment.settings.setMusicEnabled(volumeCheckBox.isChecked());
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.findActor("music_volume_slider").addListener(new ClickListener()
        {
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer)
            {
                Environment.settings.setMusicVolume(((MultiImageSlider) event.getListenerActor()).getPercent() / 100);
                super.touchDragged(event, x, y, pointer);
            }
        });

        this.findActor("sfx_checkbox").addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                CheckBox volumeCheckBox = (CheckBox) event.getListenerActor();
                Environment.settings.setSfxEnabled(volumeCheckBox.isChecked());
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.findActor("sfx_volume_slider").addListener(new ClickListener()
        {
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer)
            {
                Environment.settings.setSfxVolume(((MultiImageSlider) event.getListenerActor()).getPercent() / 100);
                super.touchDragged(event, x, y, pointer);
            }
        });

        this.findActor("vibrations_checkbox").addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                CheckBox volumeCheckBox = (CheckBox) event.getListenerActor();
                if (volumeCheckBox.isChecked())
                {
                    Gdx.input.vibrate(500);
                    Environment.settings.setVibrationsEnabled(true);
                } else
                {
                    Environment.settings.setVibrationsEnabled(false);
                }
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.findActor("gore_checkbox").addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                CheckBox goreCheckBox = (CheckBox) event.getListenerActor();
                Environment.settings.setGoreEnabled(goreCheckBox.isChecked());
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

    public abstract void onBackButton();
}
