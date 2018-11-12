package com.fcfruit.monstersmash.stages.game_ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.monstersmash.entity.interfaces.PostLevelDestroyableInterface;
import com.fcfruit.monstersmash.entity.interfaces.PowerupInterface;
import com.fcfruit.monstersmash.entity.interfaces.PreLevelDestroyableInterface;
import com.fcfruit.monstersmash.entity.interfaces.UpdatableEntityInterface;
import com.fcfruit.monstersmash.level.mode.SandboxLevel;
import com.fcfruit.monstersmash.physics.Physics;
import com.fcfruit.monstersmash.powerups.explodable.ExplodablePowerup;
import com.fcfruit.monstersmash.powerups.explodable.GrenadePowerup;
import com.fcfruit.monstersmash.powerups.explodable.MolotovPowerup;
import com.fcfruit.monstersmash.powerups.rock_powerup.RockPowerup;
import com.fcfruit.monstersmash.powerups.rocket.RocketPowerup;
import com.fcfruit.monstersmash.powerups.time.TimePowerup;
import com.fcfruit.monstersmash.ui.ImageButton;
import com.fcfruit.monstersmash.zombies.ArmoredZombie;
import com.fcfruit.monstersmash.zombies.BigZombie;
import com.fcfruit.monstersmash.zombies.Bone_BossZombie;
import com.fcfruit.monstersmash.zombies.CrawlingZombie;
import com.fcfruit.monstersmash.zombies.GrandmaZombie;
import com.fcfruit.monstersmash.zombies.PoliceZombie;
import com.fcfruit.monstersmash.zombies.RegZombie;
import com.fcfruit.monstersmash.zombies.SuicideZombie;
import com.fcfruit.monstersmash.zombies.Zombie;

import java.util.ArrayList;
import java.util.Random;

public class SandboxUIStage extends GameUIStage
{
    private static int MAX_MONSTERS = 31;
    private static float MIN_CAM_POS = 1381f;
    private static float MAX_CAM_POS = 4814f;

    private Texture buttonTexture;
    private TextureRegionDrawable buttonTextureDrawable;

    private MonsterMenu monsterMenu;
    private PowerupMenu powerupMenu;

    private int spawnedEntities = 0;

    private int touchedPowerupButton;
    private double timeSincePowerupButtonPressed;
    private double timeBeforePowerupMenuOpen = 400;
    private boolean shouldOpenPowerupMenu = false;
    private int powerupMenuPointer;

    private ArrayList<Zombie> monsterAddQueue = new ArrayList<Zombie>();
    private ArrayList<Zombie> tempMonsterQueue = new ArrayList<Zombie>();

    private Image cameraMovementButton;
    private Image cameraMovementIndicator;
    private boolean isCameraMoving = false;

    private static ArrayList<MonsterRepresentation> monsterRepresentations = new ArrayList<MonsterRepresentation>();

    static
    {
        monsterRepresentations.add(new MonsterRepresentation(RegZombie.class, "reg_zombie"));
        monsterRepresentations.add(new MonsterRepresentation(CrawlingZombie.class, "crawling_zombie"));
        monsterRepresentations.add(new MonsterRepresentation(PoliceZombie.class, "police_zombie"));
        monsterRepresentations.add(new MonsterRepresentation(SuicideZombie.class, "suicide_zombie"));
        monsterRepresentations.add(new MonsterRepresentation(BigZombie.class, "big_zombie", "biker3/headgreen")); // There are multiple skins for big zombie
        monsterRepresentations.add(new MonsterRepresentation(GrandmaZombie.class, "grandma_zombie"));
        monsterRepresentations.add(new MonsterRepresentation(ArmoredZombie.class, "armored_zombie"));
        monsterRepresentations.add(new MonsterRepresentation(Bone_BossZombie.class, "bone_boss_zombie"));
    }

    private static ArrayList<PowerupRepresentation> powerupRepresentations = new ArrayList<PowerupRepresentation>();

    static
    {
        powerupRepresentations.add(new PowerupRepresentation(GrenadePowerup.class, "explodable", "grenade"));
        powerupRepresentations.add(new PowerupRepresentation(RockPowerup.class, "rock"));
        powerupRepresentations.add(new PowerupRepresentation(TimePowerup.class, "time"));
        powerupRepresentations.add(new PowerupRepresentation(MolotovPowerup.class, "explodable", "molotov"));
        powerupRepresentations.add(new PowerupRepresentation(RocketPowerup.class, "rocket"));
    }

    public SandboxUIStage(Viewport v)
    {
        super(v, "ui/game_ui/sandbox/sandbox_ui.json", "ui/game_ui/sandbox/", false);

        this.buttonTexture = new Texture("ui/game_ui/sandbox/box.png");
        this.buttonTextureDrawable = new TextureRegionDrawable(new TextureRegion(this.buttonTexture));

        this.monsterMenu = new MonsterMenu();
        this.powerupMenu = new PowerupMenu();

        this.monsterMenu.setVisible(false);
        this.powerupMenu.setVisible(false);

        findActor("monster_button").addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                monsterMenu.setVisible(!monsterMenu.isVisible());
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.cameraMovementButton = (Image) findActor("camera_movement_button");
        this.cameraMovementButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                isCameraMoving = !isCameraMoving;
                ((SandboxLevel) Environment.level).setCameraMoving(isCameraMoving);
                cameraMovementIndicator.setVisible(isCameraMoving);
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.cameraMovementIndicator = (Image) findActor("camera_image");

        for (ImageButton powerUpButton : powerUpButtons)
        {
            powerUpButton.addListener(new ClickListener()
            {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
                {
                    touchedPowerupButton = (Integer) event.getListenerActor().getUserObject();
                    timeSincePowerupButtonPressed = System.currentTimeMillis();
                    powerupMenuPointer = pointer;
                    shouldOpenPowerupMenu = true;

                    return super.touchDown(event, x, y, pointer, button);
                }
            });
        }

        this.addActor(monsterMenu);
        this.addActor(powerupMenu);

    }

    private static class MonsterRepresentation extends Image
    {
        public Class monsterClass;
        public String thumbnail_path;
        public String thumbnail_region_name = "head";

        public MonsterRepresentation(Class monsterClass, String monster_name)
        {
            this.monsterClass = monsterClass;
            this.thumbnail_path = "zombies/" + monster_name + "/" + monster_name + ".atlas";
        }

        public MonsterRepresentation(Class monsterClass, String monster_name, String thumbnail_region_name)
        {
            this.monsterClass = monsterClass;
            this.thumbnail_path = "zombies/" + monster_name + "/" + monster_name + ".atlas";
            this.thumbnail_region_name = thumbnail_region_name;
        }
    }

    private static class PowerupRepresentation extends Image
    {
        public Class powerupClass;
        public String thumbnail_path;

        public PowerupRepresentation(Class powerupClass, String powerup_name)
        {
            this.powerupClass = powerupClass;
            this.thumbnail_path = "powerups/" + powerup_name + "/" + powerup_name + ".png";
        }

        public PowerupRepresentation(Class powerupClass, String powerup_ext, String powerup_name)
        {
            this.powerupClass = powerupClass;
            this.thumbnail_path = "powerups/" + powerup_ext + "/" + powerup_name + "/" + powerup_name + ".png";
        }
    }

    private class MonsterMenu extends Group
    {
        private Image menuBounds;

        private Table table;

        public MonsterMenu()
        {
            super();
            this.menuBounds = (Image) SandboxUIStage.this.findActor("monster_menu_bounds");

            this.create_table();

            this.setPosition(menuBounds.getX(), menuBounds.getY());
        }

        private void create_table()
        {
            this.table = new Table();
            this.table.setSize(menuBounds.getWidth(), menuBounds.getHeight());

            int rows = 4;
            int columns = 4;
            int i = 0;
            for (int r = 0; r < rows; r++)
            {
                for (int c = 0; c < columns; c++)
                {
                    Image button_image = new Image(buttonTexture);
                    button_image.setSize(menuBounds.getWidth() / (button_image.getWidth() * columns) * button_image.getWidth(), menuBounds.getHeight() / (button_image.getHeight() * rows) * button_image.getHeight());
                    button_image.setName("button_image");

                    Group menu_button = new Group();
                    menu_button.setSize(button_image.getWidth(), button_image.getHeight());
                    menu_button.addActor(button_image);

                    if (i < monsterRepresentations.size())
                    {
                        Image monster_image = new Image(Environment.assets.get(monsterRepresentations.get(i).thumbnail_path, TextureAtlas.class).findRegion(monsterRepresentations.get(i).thumbnail_region_name));
                        monster_image.setName("monster_image");

                        monster_image.setSize(menu_button.getWidth() / 1.35f, menu_button.getHeight() / 1.35f);

                        monster_image.setOrigin(monster_image.getWidth() / 2, monster_image.getHeight() / 2);
                        monster_image.setRotation(90);

                        monster_image.setPosition(button_image.getWidth() / 2 - monster_image.getWidth() / 2, button_image.getHeight() / 2 - monster_image.getHeight() / 2);

                        menu_button.addActor(monster_image);

                        menu_button.setUserObject(monsterRepresentations.get(i));

                        menu_button.addListener(new ClickListener(){
                            @Override
                            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
                            {
                                Actor actor = ((Group)event.getListenerActor()).findActor("monster_image");
                                actor.setSize(actor.getWidth()/2, actor.getHeight()/2);
                                actor.setPosition(actor.getX() - actor.getWidth()/2, actor.getY() + actor.getHeight()/2); // Center image
                                return super.touchDown(event, x, y, pointer, button);
                            }

                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                            {
                                Actor actor = ((Group)event.getListenerActor()).findActor("monster_image");
                                actor.setPosition(actor.getX() + actor.getWidth()/2, actor.getY() - actor.getHeight()/2); // Center image
                                actor.setSize(actor.getWidth()*2, actor.getHeight()*2);
                                super.touchUp(event, x, y, pointer, button);
                            }
                        });

                    }

                    menu_button.addListener(new ClickListener()
                    {
                        @Override
                        public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                        {
                            if (event.getListenerActor().getUserObject() != null)
                                loadMonster(((MonsterRepresentation) event.getListenerActor().getUserObject()).monsterClass);
                            super.touchUp(event, x, y, pointer, button);
                        }
                    });

                    this.table.add(menu_button);
                    i++;
                }
                this.table.row();
            }

            this.addActor(table);
        }
    }

    private class PowerupMenu extends Group
    {
        private Image menuBounds;

        private Table table;

        public PowerupMenu()
        {
            super();
            this.menuBounds = (Image) SandboxUIStage.this.findActor("powerup_menu_bounds");

            this.create_table();

            this.setPosition(menuBounds.getX(), menuBounds.getY());
        }

        private void create_table()
        {
            this.table = new Table();
            this.table.setSize(menuBounds.getWidth(), menuBounds.getHeight());

            int rows = 4;
            int columns = 4;
            int i = 0;
            for (int r = 0; r < rows; r++)
            {
                for (int c = 0; c < columns; c++)
                {
                    Image button_image = new Image(buttonTexture);
                    button_image.setSize(menuBounds.getWidth() / (button_image.getWidth() * columns) * button_image.getWidth(), menuBounds.getHeight() / (button_image.getHeight() * rows) * button_image.getHeight());
                    button_image.setName("button_image");

                    Group menu_button = new Group();
                    menu_button.setSize(button_image.getWidth(), button_image.getHeight());
                    menu_button.addActor(button_image);

                    if (i < powerupRepresentations.size())
                    {
                        Image powerup_image = new Image(new Texture(powerupRepresentations.get(i).thumbnail_path));
                        powerup_image.setName("powerup_image");

                        float offset = 100;
                        float scale;
                        if ((menu_button.getWidth() - offset) / powerup_image.getWidth() < (menu_button.getHeight() - offset) / powerup_image.getHeight())
                            scale = (menu_button.getWidth() - offset) / powerup_image.getWidth();
                        else
                            scale = (menu_button.getHeight() - offset) / powerup_image.getHeight(); // If still too big, scale with height instead
                        powerup_image.setSize(powerup_image.getWidth() * scale, powerup_image.getHeight() * scale); // Size doesn't update when using setScale for some reason

                        powerup_image.setPosition(button_image.getWidth() / 2 - powerup_image.getWidth() / 2, button_image.getHeight() / 2 - powerup_image.getHeight() / 2);

                        menu_button.addActor(powerup_image);
                        menu_button.setUserObject(powerupRepresentations.get(i));

                        menu_button.addListener(new ClickListener(){
                            @Override
                            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
                            {
                                Actor actor = ((Group)event.getListenerActor()).findActor("powerup_image");
                                actor.setSize(actor.getWidth()/2, actor.getHeight()/2);
                                actor.setPosition(actor.getX() + actor.getWidth()/2, actor.getY() + actor.getHeight()/2); // Center image
                                return super.touchDown(event, x, y, pointer, button);
                            }

                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                            {
                                Actor actor = ((Group)event.getListenerActor()).findActor("powerup_image");
                                actor.setPosition(actor.getX() - actor.getWidth()/2, actor.getY() - actor.getHeight()/2); // Center image
                                actor.setSize(actor.getWidth()*2, actor.getHeight()*2);
                                super.touchUp(event, x, y, pointer, button);
                            }
                        });
                    }

                    menu_button.addListener(new ClickListener()
                    {
                        @Override
                        public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                        {
                            if (event.getListenerActor().getUserObject() != null)
                                addPowerup(((PowerupRepresentation) event.getListenerActor().getUserObject()).powerupClass);
                            super.touchUp(event, x, y, pointer, button);
                        }
                    });

                    this.table.add(menu_button);
                    i++;
                }
                this.table.row();
            }

            this.addActor(table);
        }
    }

    private void moveCamera()
    {
        float delta = -Gdx.input.getDeltaX();
        delta = delta*2;

        if(Environment.gameCamera.position.x + delta >= MIN_CAM_POS
                && Environment.gameCamera.position.x + delta <= MAX_CAM_POS)
        {
            Environment.gameCamera.position.x += delta;
            Environment.gameCamera.update();
            Environment.physicsCamera.position.x += delta / Physics.PIXELS_PER_METER;
            Environment.physicsCamera.update();
        }

        this.clearExplodableHangingPowerups();
    }

    private void clearExplodableHangingPowerups()
    {
        for(DrawableEntityInterface drawableEntityInterface : Environment.level.getDrawableEntities())
        {
            if(drawableEntityInterface instanceof ExplodablePowerup)
                ((ExplodablePowerup) drawableEntityInterface).destroy();
        }
        for(UpdatableEntityInterface updatableEntityInterface : Environment.level.getUpdatableEntities())
        {
            if(updatableEntityInterface instanceof ExplodablePowerup)
                ((ExplodablePowerup) updatableEntityInterface).destroy();
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        if(isCameraMoving)
            this.moveCamera();
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        boolean result = super.touchUp(screenX, screenY, pointer, button);
        if (!result && !isCameraMoving)
        {
            // If nothing touched, hide menus
            this.monsterMenu.setVisible(false);
            this.powerupMenu.setVisible(false);
        }
        return result;
    }

    private void addPowerup(Class powerupClass)
    {
        try
        {
            PowerupInterface tempPowerup;
            tempPowerup = (PowerupInterface) powerupClass.getDeclaredConstructor().newInstance();

            if(this.powerups[touchedPowerupButton] != null)
            {
                int i = 0;
                for(PowerupInterface powerupInterface : this.powerups)
                {
                    if(powerupInterface == null)
                    {
                        this.powerups[i] = tempPowerup;
                        this.powerups[i].getUIDrawable().setSize(this.powerUpButtons[touchedPowerupButton].getWidth(), this.powerUpButtons[touchedPowerupButton].getHeight());
                        return;
                    }
                    i++;
                }
            }

            this.powerups[touchedPowerupButton] = tempPowerup;
            this.powerups[touchedPowerupButton].getUIDrawable().setSize(this.powerUpButtons[touchedPowerupButton].getWidth(), this.powerUpButtons[touchedPowerupButton].getHeight());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadMonster(Class monsterClass)
    {
        try
        {
            int direction;
            if (Environment.gameCamera.position.x <= (MIN_CAM_POS + MAX_CAM_POS)/3) // 3 positions, "left", "middle", "right" => /3
            {
                direction = 0;
            } else if (Environment.gameCamera.position.x >= (MIN_CAM_POS + MAX_CAM_POS)*2/3)
            {
                direction = 1;
            } else
            {
                direction = (int) Math.round(Math.random());
            }

            Zombie tempZombie;
            tempZombie = (Zombie) monsterClass.getDeclaredConstructor(Integer.class).newInstance((this.spawnedEntities + 1));
            tempZombie.setup(direction);

            Vector2 position;
            position = (direction == 0 ? new Vector2(Environment.physicsCamera.position.x - Environment.physicsCamera.viewportWidth / 2 - 2, 0) : new Vector2(Environment.physicsCamera.position.x + Environment.physicsCamera.viewportWidth / 2 + 2, 0));

            float offset = (tempZombie.getDirection() == 0 ? -tempZombie.getSize().x / 2 : tempZombie.getSize().x / 2);
            position.x = position.x + offset;

            tempZombie.setPosition(position);

            // Prevents graphic glitch at position (0, 0)
            tempZombie.update(Gdx.graphics.getDeltaTime());

            tempZombie.setInitialGround(Math.round(new Random().nextFloat()));

            this.monsterAddQueue.add(tempZombie);

            Gdx.app.debug("Spawner", "Added Zombie");

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void spawnMonster(Zombie zombie)
    {
        Environment.drawableAddQueue.add(zombie);
        // Apply Slow Motion
        if (Environment.powerupManager.isSlowMotionEnabled)
            zombie.getState().setTimeScale(zombie.getState().getTimeScale() / TimePowerup.timeFactor);
        zombie.onSpawned();
    }

    private int getAliveMonsters()
    {
        int i = 0;
        for (DrawableEntityInterface drawableEntityInterface : Environment.level.getDrawableEntities())
        {
            if (drawableEntityInterface instanceof Zombie && ((Zombie) drawableEntityInterface).isAlive())
                i++;
        }
        return i;
    }

    @Override
    public void draw()
    {
        super.draw();

        int i = 0;

        for (Zombie zombie : this.monsterAddQueue)
        {
            if (getAliveMonsters() + i < MAX_MONSTERS)
            {
               spawnMonster(zombie);
            }
            else
                this.tempMonsterQueue.add(zombie);
            i++;
        }
        this.monsterAddQueue.clear();
        this.monsterAddQueue.addAll(this.tempMonsterQueue);
        this.tempMonsterQueue.clear();
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);

        if (System.currentTimeMillis() - timeSincePowerupButtonPressed >= timeBeforePowerupMenuOpen
                && this.shouldOpenPowerupMenu && Gdx.input.isTouched(powerupMenuPointer)) // If finger held down for 400ms
        {
            powerupMenu.setVisible(true);
            this.shouldOpenPowerupMenu = false;
        } else if(!Gdx.input.isTouched(powerupMenuPointer)) // If at any point, user stops touching powerup buttons
            this.shouldOpenPowerupMenu = false;
    }
}
