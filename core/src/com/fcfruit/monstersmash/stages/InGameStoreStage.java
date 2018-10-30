package com.fcfruit.monstersmash.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.stages.RubeStage;
import com.fcfruit.monstersmash.ui.FontActor;
import com.fcfruit.monstersmash.ui.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;

public class InGameStoreStage extends RubeStage
{
    private static String[] powerups;

    static
    {
        powerups = new String[]{"rock", "rocket", "time", "explodable/grenade", "explodable/molotov"};
    }

    private static HashMap<Integer, Integer> upgradePrices = new HashMap<Integer, Integer>();

    static
    {
        // to upgrade to 2/4 it costs 1000 brains
        upgradePrices.put(2, 1000);
        upgradePrices.put(3, 2000);
        upgradePrices.put(4, 3500);
    }

    private Preferences brainPrefs = Environment.Prefs.brains;
    private Preferences upgradePrefs = Environment.Prefs.upgrades;

    private FontActor userBrainCount;
    private double errorTime = 1000d;
    private double userBrainCountErrorTimer = 0.0d;

    private ImageButton backButton;
    private Image listBounds;
    private Image priceBounds;
    private Image storeGuyBounds;
    private Image productBounds;
    private Image productDisplayBounds;

    private ImageButton downButton;
    private ImageButton upButton;

    private ImageButton upgradeButton;

    private Image upgradeStatusBounds;
    private Image[] upgradeStatusGears;

    private SpriteBatch spriteBatch;
    private SkeletonRenderer skeletonRenderer;
    private Skeleton guySkeleton;
    private AnimationState guyState;

    private ProductLabel dispalyedProductLabel;
    private Image productOnDisplay;
    private Image brainPriceImage;
    private GlyphLayout displayPriceGlyphLayout;
    private FontActor displayPrice;

    public InGameStoreStage(Viewport viewport)
    {
        super(viewport, "ui/store/store.json", "ui/store/", false);

        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        bitmapFont.getData().setScale(2);

        String text = "You have: " + brainPrefs.getInteger("userBrainCount", 0) + " Brains";

        this.userBrainCount = new FontActor(bitmapFont);
        this.userBrainCount.setText(text);
        GlyphLayout brainCountLayout = new GlyphLayout();
        brainCountLayout.setText(bitmapFont, text);

        this.userBrainCount.setPosition(this.findActor("user_brain_count_bounds").getX(),
                this.findActor("user_brain_count_bounds").getY() + brainCountLayout.height);
        this.addActor(this.userBrainCount);

        this.backButton = (ImageButton) this.findActor("back_button");
        this.listBounds = (Image) this.findActor("list_bounds");
        this.priceBounds = (Image) this.findActor("price_bounds");
        this.storeGuyBounds = (Image) this.findActor("store_guy_bounds");
        this.productBounds = (Image) this.findActor("product_bounds");
        this.productDisplayBounds = (Image) this.findActor("product_display_bounds");

        this.downButton = (ImageButton) this.findActor("down_button");
        this.upButton = (ImageButton) this.findActor("up_button");

        this.upgradeButton = (ImageButton) this.findActor("upgrade_button");
        this.upgradeButton.setImageDown(new Sprite(new Texture("ui/store/button_down.png")));
        this.upgradeButton.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                onUpgradeButtonClicked();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.upgradeStatusBounds = (Image) this.findActor("upgrade_bounds");

        this.upgradeStatusGears = new Image[4];
        for(int i = 0; i < upgradeStatusGears.length; i++)
        {
            this.upgradeStatusGears[i] = new Image(new Texture(Gdx.files.internal("ui/store/gear.png")));
            this.upgradeStatusGears[i].setPosition(this.upgradeStatusBounds.getX() + this.upgradeStatusGears[i].getWidth()*i, this.upgradeStatusBounds.getY());
            this.upgradeStatusGears[i].setVisible(false);
            this.addActor(this.upgradeStatusGears[i]);
        }

        this.brainPriceImage = new Image(new Texture(Gdx.files.internal("ui/store/brain.png")));
        this.brainPriceImage.setSize(this.brainPriceImage.getWidth() * 0.5f, this.brainPriceImage.getHeight() * 0.5f);
        this.brainPriceImage.setPosition(priceBounds.getX() + 10,
                priceBounds.getY() + priceBounds.getHeight() / 2 - brainPriceImage.getHeight() / 2);
        this.brainPriceImage.setVisible(false);
        this.addActor(this.brainPriceImage);

        this.backButton.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                hideDisplayedItems();
                Environment.screens.mainmenu.hideInGameStorePage();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.spriteBatch = new SpriteBatch();
        this.skeletonRenderer = new SkeletonRenderer();
        this.addGuy();

        this.createList();
    }

    private void addGuy()
    {
        float scale = 2;

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("ui/store/store_guy/store_guy.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(scale);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("ui/store/store_guy/store_guy.json"));
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        this.guySkeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone cameraPositions, slot attachments, etc).
        this.guyState = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        this.guyState.setAnimation(0, "idle", true);
        this.guyState.setTimeScale(0.5f);

        this.guySkeleton.setPosition(this.storeGuyBounds.getX() + this.guySkeleton.getData().getWidth() * scale / 2, this.storeGuyBounds.getY());
    }

    private class ProductLabel extends Group
    {
        public String productName;
        public int currentLevel = 2;
        public Image background;
        public Image image;
        public FontActor fontActor;

        public ProductLabel(final String productName, Image background, Image image, FontActor upgradeAmount)
        {
            this.productName = productName;
            this.background = background;
            this.image = image;
            this.fontActor = upgradeAmount;

            this.setWidth(background.getWidth());
            this.setHeight(background.getHeight());

            this.addActor(background);
            this.addActor(this.image);
            this.addActor(this.fontActor);

            this.addListener(new ClickListener()
            {
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                {
                    displayProduct(productName, getInstance());
                    super.touchUp(event, x, y, pointer, button);
                }
            });
        }

        private ProductLabel getInstance()
        {
            return this;
        }
    }

    private void displayProduct(String productName, ProductLabel productLabel)
    {
        if (this.productOnDisplay != null)
            this.productOnDisplay.remove();

        this.dispalyedProductLabel = productLabel;
        this.productOnDisplay = new Image(productLabel.image.getDrawable());
        this.productOnDisplay.setScale(this.productDisplayBounds.getHeight() / this.productOnDisplay.getHeight());
        this.productOnDisplay.setPosition(this.productDisplayBounds.getX(), this.productDisplayBounds.getY());

        if (this.displayPrice == null)
        {
            BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
            bitmapFont.getData().setScale(2);
            this.displayPriceGlyphLayout = new GlyphLayout();
            this.displayPrice = new FontActor(bitmapFont);
            this.addActor(displayPrice);
        }


        Integer price = upgradePrices.get(productLabel.currentLevel + 1);
        String text = "" + price;
        if (price == null)
        {
            text = "Max";
            this.brainPriceImage.setVisible(false);
        } else
            this.brainPriceImage.setVisible(true);

        this.displayPriceGlyphLayout.setText(this.displayPrice.getBitmapFont(), text);
        this.displayPrice.setText(text);

        this.displayPrice.setPosition(brainPriceImage.getX() + brainPriceImage.getWidth() + 10,
                brainPriceImage.getY() + displayPriceGlyphLayout.height);

        this.setUpgradeStatus(this.dispalyedProductLabel);

        this.addActor(this.productOnDisplay);
    }

    private void onUpgradeButtonClicked()
    {
        if (this.dispalyedProductLabel != null && this.dispalyedProductLabel.currentLevel + 1 <= 4)
        {
            int requested_upgrade_level = this.dispalyedProductLabel.currentLevel + 1;
            boolean hasEnoughMoney = this.brainPrefs.getInteger("userBrainCount", 0) - upgradePrices.get(requested_upgrade_level) >= 0;

            if (hasEnoughMoney)
            {
                if (requested_upgrade_level > this.dispalyedProductLabel.currentLevel)
                    this.dispalyedProductLabel.currentLevel = requested_upgrade_level;

                this.setUpgradeStatus(this.dispalyedProductLabel);
                this.displayProduct(this.dispalyedProductLabel.productName, this.dispalyedProductLabel);

                this.brainPrefs.putInteger("userBrainCount", brainPrefs.getInteger("userBrainCount", 0) - upgradePrices.get(requested_upgrade_level));
                this.brainPrefs.flush();
                this.upgradePrefs.putInteger(this.dispalyedProductLabel.productName, this.dispalyedProductLabel.currentLevel);
                this.upgradePrefs.flush();
            } else
            {
                this.userBrainCount.setText("You Don't Have Enough Brains\nFor That!");
                this.userBrainCountErrorTimer = System.currentTimeMillis();
            }
        } else if (this.dispalyedProductLabel == null)
        {
            this.userBrainCount.setText("Select an Item to Upgrade.");
            this.userBrainCountErrorTimer = System.currentTimeMillis();
        } else if (this.dispalyedProductLabel.currentLevel + 1 <= 4)
        {
            this.userBrainCount.setText("Max Upgrade Level Reached \nfor This Item.");
            this.userBrainCountErrorTimer = System.currentTimeMillis();
        }
    }

    private void setUpgradeStatus(ProductLabel productLabel)
    {
        for(int i = 0; i < this.upgradeStatusGears.length; i++)
        {
            this.upgradeStatusGears[i].setVisible(false);
        }
        for(int i = 0; i < productLabel.currentLevel; i++)
        {
            this.upgradeStatusGears[i].setVisible(true);
        }
    }

    private void hideDisplayedItems()
    {
    }

    private void createList()
    {
        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        ArrayList<ProductLabel> powerupLabels = new ArrayList<ProductLabel>();
        int i = 0;
        for (String folderPath : powerups)
        {
            String powerupName = folderPath.split("/")[folderPath.split("/").length - 1].replace("/", "");
            ProductLabel powerupLabel = this.createProductLabel(powerupName, folderPath, bitmapFont);

            if (i == 0)
                powerupLabel.setPosition(this.listBounds.getX(), (this.listBounds.getY() + this.listBounds.getHeight()) - (powerupLabel.getHeight() * (i + 1)));
            else
                powerupLabel.setPosition(this.listBounds.getX(), (this.listBounds.getY() + this.listBounds.getHeight()) - (powerupLabels.get(i - 1).getHeight() * (i + 1)));

            this.addActor(powerupLabel);
            powerupLabels.add(powerupLabel);

            i++;
        }
    }

    private ProductLabel createProductLabel(String productName, String path, BitmapFont bitmapFont)
    {
        FileHandle full_path = Gdx.files.internal("powerups/" + path + "/" + productName + ".png");

        Image background = new Image(new Texture("ui/store/box.png"));
        background.setSize(this.productBounds.getWidth(), this.productBounds.getHeight());

        Image image = new Image(new Texture(full_path));
        image.setPosition(background.getX() + 50, background.getY() + 50);
        image.setScale((background.getHeight() - 100) / image.getHeight());

        FontActor fontActor = new FontActor(bitmapFont);
        fontActor.setText("1/4");

        GlyphLayout layout = new GlyphLayout();
        layout.setText(bitmapFont, "1/4");

        fontActor.setPosition(background.getX() + background.getWidth() - layout.width - 100, image.getY() + layout.height);

        ProductLabel productLabel = new ProductLabel(productName, background, image, fontActor);
        productLabel.currentLevel = upgradePrefs.getInteger(productName, 1);

        return productLabel;
    }

    @Override
    public void draw()
    {
        this.spriteBatch.setProjectionMatrix(this.getViewport().getCamera().combined);

        this.spriteBatch.begin();
        for (Actor actor : this.getActors().toArray())
        {
            if (actor.getName() != null && actor.getName().equals("overlay"))
                this.skeletonRenderer.draw(spriteBatch, guySkeleton);
            if (actor.isVisible())
                actor.draw(spriteBatch, 1);
        }
        this.spriteBatch.end();
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);

        this.guyState.update(delta); // Update the animation getUpTimer.

        this.guyState.apply(guySkeleton); // Poses skeleton using current animations. This sets the bones' local SRT.

        this.guySkeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        if (System.currentTimeMillis() - this.userBrainCountErrorTimer >= this.errorTime)
        {
            this.userBrainCount.setText("You have: " + brainPrefs.getInteger("userBrainCount", 0) + " Brains");
        }
    }
}
