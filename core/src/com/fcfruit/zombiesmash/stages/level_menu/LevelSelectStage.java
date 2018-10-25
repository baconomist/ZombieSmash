package com.fcfruit.zombiesmash.stages.level_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.stages.RubeStage;
import com.fcfruit.zombiesmash.ui.CheckBox;
import com.fcfruit.zombiesmash.ui.FontActor;
import com.fcfruit.zombiesmash.ui.ImageButton;

public class LevelSelectStage extends RubeStage
{
    // each font is scaled differently
    private BitmapFont levelButtonBitmapFont;
    private BitmapFont descriptionBitMapFont;
    private BitmapFont difficultyLabelFont;

    private boolean hideDescription = true;
    private Actor descriptionActor;
    private FontActor descriptionText;
    private Actor playButton;

    private int selected_level;
    private String selected_difficulty = "normal";

    private Image[] difficulty_labels;
    private FontActor[] difficulty_fonts;
    private CheckBox[] difficulty_checkboxes;

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
                Environment.currentDifficulty = selected_difficulty;
                Environment.setupGame(selected_level);
                Environment.game.setScreen(Environment.screens.gamescreen);
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.difficultyLabelFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        this.difficultyLabelFont.getData().setScale(1.5f);

        this.difficulty_labels = new Image[3];
        this.difficulty_fonts = new FontActor[3];
        int i = 0;
        FontActor fontActor;
        for (Actor actor : this.getActors().toArray())
        {
            if (actor.getName().contains("label") && !actor.getName().contains("level_select"))
            {
                fontActor = new FontActor(this.difficultyLabelFont);
                fontActor.setText(actor.getName().replace("_label", "").toUpperCase());
                fontActor.setPosition(actor.getX() + 40, actor.getY() + actor.getHeight() / 2 + 40);
                fontActor.setName(actor.getName() + "_font");

                this.difficulty_labels[i] = (Image) actor;
                this.difficulty_fonts[i] = fontActor;
                actor.remove();
                i++;
            }
        }

        this.difficulty_checkboxes = new CheckBox[3];
        i = 0;
        for (Actor actor : this.getActors().toArray())
        {
            if (actor instanceof CheckBox)
            {
                this.difficulty_checkboxes[i] = (CheckBox) actor;
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
        this.descriptionText = new FontActor(this.descriptionBitMapFont);
        this.descriptionText.setPosition(description_bounds.getX() + 20, description_bounds.getY() + description_bounds.getHeight() - 50);

        this.generateLevelButtons();
    }

    private void onDifficultyCheckbox(Actor checkbox)
    {
        for (CheckBox chkbx : this.difficulty_checkboxes)
        {
            if (chkbx != checkbox)
                chkbx.setChecked(false);
        }

        if (!((CheckBox) checkbox).isChecked())
            ((CheckBox) checkbox).setChecked(true); // One checkbox needs to be checked at least, can't uncheck

        this.selected_difficulty = checkbox.getName().replace("_checkbox", "");

        this.updateDescription();
    }

    private void generateLevelButtons()
    {
        Texture levelButtonTexture = new Texture(Gdx.files.internal("ui/level_menu/level_select/box.png"));
        ImageButton imageButton;
        FontActor fontActor;
        Sprite sprite;
        String level_string;
        int level_int;

        float spacing = 10f;

        Image level_list_bounds = (Image) this.findActor("level_list_bounds");
        int columns = (int) (level_list_bounds.getWidth() / (levelButtonTexture.getWidth() + spacing));
        int rows = (int) (level_list_bounds.getHeight() / (levelButtonTexture.getHeight() + spacing));

        int col = 0;
        int row = 1;
        for (FileHandle level_file : Gdx.files.internal("maps/night_map/levels").list())
        {
            level_string = level_file.name().replace(".json", "");
            try
            {
                level_int = Integer.valueOf(level_string);
            } catch (Exception e)
            {
                Gdx.app.error("Error in level button loading.", "The file was: " + level_file.name());
                continue; // Don't create examples.json
            }

            sprite = new Sprite(levelButtonTexture);

            imageButton = new ImageButton(sprite);
            imageButton.setSize(sprite.getWidth(), sprite.getHeight());
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

            fontActor = new FontActor(this.levelButtonBitmapFont);
            fontActor.setText(level_string);
            fontActor.setPosition(imageButton.getX() + imageButton.getWidth() / 2 - 30, imageButton.getY() + imageButton.getHeight() / 2 + 40);

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

            ((CheckBox) this.findActor("normal_checkbox")).setChecked(true); // Default difficulty is normal

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
