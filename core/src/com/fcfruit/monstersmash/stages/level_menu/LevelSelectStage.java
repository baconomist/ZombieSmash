package com.fcfruit.monstersmash.stages.level_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.stages.RubeStage;
import com.fcfruit.monstersmash.ui.CheckBox;
import com.fcfruit.monstersmash.ui.FontActor;
import com.fcfruit.monstersmash.ui.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LevelSelectStage extends com.fcfruit.monstersmash.stages.RubeStage
{
    // each font is scaled differently
    private BitmapFont levelButtonBitmapFont;
    private BitmapFont descriptionBitMapFont;
    private BitmapFont difficultyLabelFont;

    private boolean hideDescription = true;
    private Actor descriptionActor;
    private com.fcfruit.monstersmash.ui.FontActor descriptionText;
    private Actor playButton;

    private int selected_level;
    private String selected_difficulty = "normal";

    private Image[] difficulty_labels;
    private com.fcfruit.monstersmash.ui.FontActor[] difficulty_fonts;
    private com.fcfruit.monstersmash.ui.CheckBox[] difficulty_checkboxes;

    public LevelSelectStage(Viewport viewport)
    {
        super(viewport, "ui/level_menu/level_select/level_select.json", "ui/level_menu/level_select/", false);

        this.findActor("back_button").addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Environment.screens.levelmenu.showSeasonSelect();
                hideDescription();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.descriptionActor = this.findActor("description");
        this.descriptionActor.remove();
        this.playButton = this.findActor("play_button");
        this.playButton.remove();

        this.playButton.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Environment.screens.loadingscreen.setLevelID(selected_level);
                Environment.game.setScreen(Environment.screens.loadingscreen);
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.difficultyLabelFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        this.difficultyLabelFont.getData().setScale(1.5f);

        this.difficulty_labels = new Image[3];
        this.difficulty_fonts = new com.fcfruit.monstersmash.ui.FontActor[3];
        int i = 0;
        com.fcfruit.monstersmash.ui.FontActor fontActor;
        for (Actor actor : this.getActors().toArray())
        {
            if (actor.getName().contains("label") && !actor.getName().contains("level_select"))
            {
                fontActor = new com.fcfruit.monstersmash.ui.FontActor(this.difficultyLabelFont);
                fontActor.setText(actor.getName().replace("_label", "").toUpperCase());
                fontActor.setPosition(actor.getX() + 40, actor.getY() + actor.getHeight() / 2 + 40);
                fontActor.setName(actor.getName() + "_font");

                this.difficulty_labels[i] = (Image) actor;
                this.difficulty_fonts[i] = fontActor;
                actor.remove();
                i++;
            }
        }

        this.difficulty_checkboxes = new com.fcfruit.monstersmash.ui.CheckBox[3];
        i = 0;
        for (Actor actor : this.getActors().toArray())
        {
            if (actor instanceof com.fcfruit.monstersmash.ui.CheckBox)
            {
                this.difficulty_checkboxes[i] = (com.fcfruit.monstersmash.ui.CheckBox) actor;
                actor.addListener(new ClickListener()
                {
                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                    {
                        onDifficultyCheckbox(event.getListenerActor());
                        super.touchUp(event, x, y, pointer, button);
                    }
                });
                actor.remove();
                i++;
            }
        }

        this.levelButtonBitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        this.levelButtonBitmapFont.getData().setScale(2);

        this.descriptionBitMapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        this.descriptionBitMapFont.getData().setScale(1.15f);

        Actor description_bounds = this.findActor("description_bounds");
        this.descriptionText = new com.fcfruit.monstersmash.ui.FontActor(this.descriptionBitMapFont);
        this.descriptionText.setPosition(description_bounds.getX() + 20, description_bounds.getY() + description_bounds.getHeight() - 50);

        this.generateLevelButtons();
    }

    private void onDifficultyCheckbox(Actor checkbox)
    {
        for (com.fcfruit.monstersmash.ui.CheckBox chkbx : this.difficulty_checkboxes)
        {
            if (chkbx != checkbox)
                chkbx.setChecked(false);
        }

        if (!((com.fcfruit.monstersmash.ui.CheckBox) checkbox).isChecked())
            ((com.fcfruit.monstersmash.ui.CheckBox) checkbox).setChecked(true); // One checkbox needs to be checked at least, can't uncheck

        this.selected_difficulty = checkbox.getName().replace("_checkbox", "");

        this.updateDescription();
    }

    private void generateLevelButtons()
    {
        Texture levelButtonTexture = new Texture(Gdx.files.internal("ui/level_menu/level_select/box.png"));

        float width = levelButtonTexture.getWidth()*1.15f;
        float height = levelButtonTexture.getHeight();

        ImageButton imageButton;
        FontActor fontActor;
        Sprite sprite;
        String level_string;
        int level_int;

        float spacing = 10f;

        Image level_list_bounds = (Image) this.findActor("level_list_bounds");
        int columns = (int) (level_list_bounds.getWidth() / (width + spacing));
        int rows = (int) (level_list_bounds.getHeight() / (height + spacing));

        int col = 0;
        int row = 1;

        Array<FileHandle> sorted_list = new Array<FileHandle>();
        for(FileHandle fileHandle : Gdx.files.internal("maps/night_map/levels").list())
        {
            if(fileHandle.extension().equals("json"))
                sorted_list.add(fileHandle);
        }
        sorted_list.sort(new Comparator<FileHandle>()
        {
            @Override
            public int compare(FileHandle o1, FileHandle o2)
            {
                StringBuilder str1 = new StringBuilder();
                for(char c : o1.name().toCharArray())
                {
                    if(Character.isDigit(c))
                        str1.append(c);
                }

                StringBuilder str2 = new StringBuilder();
                for(char c : o2.name().toCharArray())
                {
                    if(Character.isDigit(c))
                        str2.append(c);
                }

                if(str1.toString().length() > 0 && str2.toString().length() > 0)
                    return Integer.valueOf(str1.toString()) - Integer.valueOf(str2.toString());
                else
                    return 0;
            }
        });

        GlyphLayout glyphLayout = new GlyphLayout();
        for(FileHandle level_file : sorted_list)
        {
            try
            {
                StringBuilder str = new StringBuilder();
                for(char c : level_file.name().toCharArray())
                {
                    if(Character.isDigit(c))
                        str.append(c);
                }
                level_string = str.toString();
                level_int = Integer.valueOf(level_string);
            } catch (NumberFormatException e){Gdx.app.error("LevelSelectStage", "Level Button Not Created");continue;}

            sprite = new Sprite(levelButtonTexture);

            imageButton = new ImageButton(sprite);
            imageButton.setSize(width, height);
            imageButton.setPosition(level_list_bounds.getX() + imageButton.getWidth() * col, level_list_bounds.getY() + level_list_bounds.getHeight() - imageButton.getHeight() * row);
            imageButton.setPosition(imageButton.getX() + spacing * col + spacing, imageButton.getY() - spacing * row - spacing); // Add spacing between bounds/boxes

            imageButton.addListener(new ClickListener()
            {
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                {
                    showDescription(Integer.valueOf(event.getListenerActor().getName()));
                    super.touchUp(event, x, y, pointer, button);
                }
            });

            fontActor = new com.fcfruit.monstersmash.ui.FontActor(this.levelButtonBitmapFont);
            fontActor.setText(level_string);
            glyphLayout.setText(levelButtonBitmapFont, level_string);
            fontActor.setPosition(imageButton.getX() + imageButton.getWidth() / 2 - 30 - glyphLayout.width/4, imageButton.getY() + imageButton.getHeight() / 2 + 40);

            imageButton.setName(level_string);
            fontActor.setName(level_string);

            this.addActor(imageButton);
            this.addActor(fontActor);

            col += 1;
            if (col == columns)
            {
                col = 0;
                row += 1;
            }
        }
    }

    private void hideDescription()
    {
        this.descriptionActor.remove();
        this.playButton.remove();
        this.descriptionText.remove();

        for (Actor actor : this.difficulty_labels)
            actor.remove();
        for (Actor actor : this.difficulty_fonts)
            actor.remove();
        for (Actor actor : this.difficulty_checkboxes)
            actor.remove();

        this.hideDescription = true;
    }

    private void showDescription(int level_id)
    {
        if (this.hideDescription)
        {
            this.hideDescription = false;
            this.addActor(this.descriptionActor);

            for (Actor actor : this.difficulty_labels)
                this.addActor(actor);
            for (Actor actor : this.difficulty_fonts)
                this.addActor(actor);
            for (Actor actor : this.difficulty_checkboxes)
                this.addActor(actor);

            ((com.fcfruit.monstersmash.ui.CheckBox) this.findActor("normal_checkbox")).setChecked(true); // Default difficulty is normal

            this.addActor(this.playButton);
            this.addActor(this.descriptionText); // Need to draw description overtop of descriptionActor
        }

        this.selected_level = level_id;

        this.updateDescription();
        this.onDifficultyCheckbox(this.findActor("normal_checkbox")); // Reset difficulty on level change
    }

    private void updateDescription()
    {
        this.descriptionText.setText("" + this.selected_level + "\nDifficulty:\n" + selected_difficulty.toUpperCase() + " \n"
                + Environment.difficulty_multipliers.get(selected_difficulty) + "x \nthe brains,\n"
                + Environment.difficulty_multipliers.get(selected_difficulty) + "x\nthe damage.");
    }
}
