package com.fcfruit.monstersmash.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.ui.FontActor;

import java.util.HashMap;

public class InAppPurchasesStage extends RubeStage
{
    private static class ProductIdentifiers
    {
        public static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQ" +
                "EAlkx9W8KLfKJE9RoU0lA0/pp72GKWIC+KhSA7IQTaOyIt0vgTRsI+oEkrrUVE7xxAOKNic+Zlc7CNy5NFK970GCQ" +
                "qAGEauFsKVZjpk60MURj5bBeeNq34OZgJ9soH1oTvrtgVNp/ZMIGZz9vog+3YRuGhoc4sxX2JAB5Iow6hoJ4NLGzcGyGTF6rCs0" +
                "n05jV+igrpQ3TPhGcNB0entFCEySSgKWgUIwl8DcN6eCysh4YK1toMrBgdlMVfJCYd01OpMIqeT7ZDQNuUSsieBBphcWv+VnPzkGk4Wc/91" +
                "bVsVgwwqadXQtKdG/9c/+AhvMw0nF9+Qs3rEnL4Sc5JFV5QnwIDAQAB";

        public static final String SANDBOX = "sandbox";
        public static final String NO_ADS = "no_ads";

        public static HashMap<String, String> productToSku = new HashMap<String, String>();

        static
        {
            productToSku.put("sandbox", SANDBOX);
            productToSku.put("no_ads", NO_ADS);
        }
    }

    private Image[] products;

    private Image title;
    private com.fcfruit.monstersmash.ui.ImageButton productButton;
    private com.fcfruit.monstersmash.ui.ImageButton backButton;
    private com.fcfruit.monstersmash.ui.ImageButton leftButton;
    private com.fcfruit.monstersmash.ui.ImageButton rightButton;
    private Image overlay;

    private boolean is_transitioning = false;
    private int direction;

    private int current_product;

    private GlyphLayout notificationMessageLayout;
    private FontActor notificationMessage;
    private double timeBeforeNotificationRemove = 2500;
    private double notificationDeltaAccum;

    public InAppPurchasesStage(Viewport viewport)
    {
        super(viewport, "ui/in_app_purchase_store/in_app_purchase_store.json", "ui/in_app_purchase_store/", false);

        //this.setUpPurchaseManager();

        this.title = (Image) this.findActor("title");

        this.productButton = (com.fcfruit.monstersmash.ui.ImageButton) this.findActor("product_button");
        this.productButton.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                onProductClicked();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.backButton = (com.fcfruit.monstersmash.ui.ImageButton) this.findActor("back_button");
        this.backButton.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Environment.screens.mainmenu.hideInAppPurchasesStage();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.leftButton = (com.fcfruit.monstersmash.ui.ImageButton) this.findActor("left_button");
        this.rightButton = (com.fcfruit.monstersmash.ui.ImageButton) this.findActor("right_button");

        this.leftButton.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                onLeftClicked();
                super.touchUp(event, x, y, pointer, button);
            }
        });


        this.rightButton.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                onRightClicked();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.overlay = (Image) this.findActor("overlay");

        this.notificationMessageLayout = new GlyphLayout();
        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("ui/font/font.fnt"));
        bitmapFont.getData().setScale(2);
        this.notificationMessage = new FontActor(bitmapFont);

        this.createProducts();
        this.manageProducts();
        //this.resetOverlay(); // for sliding animation, causes non-responsive product buttons

    }

    private void createProducts()
    {
        /** *2 this for animations!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! **/
        this.products = new Image[Gdx.files.internal("ui/in_app_purchase_store/products").list().length]; // *2 to have a list of products twice the size for smooth transitions

        int i = 0;
        for (FileHandle fileHandle : Gdx.files.internal("ui/in_app_purchase_store/products").list())
        {
            this.products[i] = new Image(new Texture(fileHandle));
            this.products[i].setPosition(this.productButton.getX() - this.productButton.getWidth() - i * this.products[i].getWidth() - 10, this.productButton.getY()); // hide products
            this.products[i].setSize(this.productButton.getWidth(), this.productButton.getHeight());

            this.products[i].setName(fileHandle.nameWithoutExtension());

            this.products[i].addListener(new ClickListener(){
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                {
                    onProductClicked();
                    super.touchUp(event, x, y, pointer, button);
                }
            });

            this.addActor(this.products[i]);
            i++;
        }

        // Create copy to ensure smooth transition of products, 4 products in reality
        /*for(FileHandle fileHandle : Gdx.files.internal("ui/in_app_purchase_store/products").list())
        {
            this.products[i] = new Image(new Texture(fileHandle));
            this.products[i].setPosition(this.productButton.getX() - this.productButton.getWidth() - i*this.products[i].getWidth() - 10, this.productButton.getY()); // hide products
            this.products[i].setSize(this.productButton.getWidth(), this.productButton.getHeight());

            this.addActor(this.products[i]);
            i++;
        }*/

    }

    // Show non-purchased products first
    private void manageProducts()
    {
        this.current_product = 0;
        this.products[current_product].setPosition(this.productButton.getX(), this.productButton.getY());

        int i = current_product + 1;
        for (Image product : this.products)
        {
            if (product != this.products[current_product])
                product.setPosition(this.products[current_product].getX() - product.getWidth() * i, product.getY());
            i++;
        }
    }

    private void resetOverlay()
    {
        this.title.remove();

        this.overlay.remove();
        this.addActor(overlay); // put overlay back on top of products

        this.leftButton.remove();
        this.rightButton.remove();
        this.addActor(this.leftButton);
        this.addActor(this.rightButton);

        this.backButton.remove();
        this.addActor(this.backButton);

        this.addActor(title);
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        //this.manageTransitionAnimation(delta);

        this.notificationDeltaAccum += delta;

        if(this.notificationDeltaAccum >= this.timeBeforeNotificationRemove/1000 && this.notificationMessage.hasParent())
        {
            for (Image product : this.products) this.addActor(product);
            this.notificationMessage.remove();
        }
    }

    private void manageTransitionAnimation(float dt)
    {
        if (this.is_transitioning)
        {
            for (int i = 0; i < this.products.length; i++)
            {
                Image product = this.products[i];

                product.setPosition(product.getX() + 1000 * dt * direction, product.getY());

                int index;
                if (i == 0)
                    index = this.products.length - 1;
                else
                    index = i - 1;

                if (this.direction > 0 && product.getX() > this.productButton.getX() + this.productButton.getWidth())
                {
                    product.setPosition(this.products[index].getX() - product.getWidth() + 30, product.getY());
                } else if (this.direction < 0 && product.getX() + product.getWidth() < this.productButton.getX())
                {
                    product.setPosition(this.products[index].getX() + product.getWidth() - 30, product.getY());
                }
            }
        }
    }

    private void onLeftClicked()
    {
        this.direction = -1;

        // Remove old product from screen
        this.products[current_product].setPosition(-9999, -9999);

        if (this.current_product - 1 < 0)
            this.current_product = this.products.length - 1;
        else
            this.current_product -= 1;

        // non-anim code
        this.products[current_product].setPosition(this.productButton.getX(), this.productButton.getY());

        this.is_transitioning = true;

        this.onSwitchProduct();
    }

    private void onRightClicked()
    {
        this.direction = 1;

        // Remove old product from screen
        this.products[current_product].setPosition(-9999, -9999);

        if (this.current_product + 1 > this.products.length - 1)
            this.current_product = 0;
        else
            this.current_product += 1;

        // non-anim code
        this.products[current_product].setPosition(this.productButton.getX(), this.productButton.getY());

        this.is_transitioning = true;

        this.onSwitchProduct();
    }

    private void onSwitchProduct()
    {
        for(Image product : this.products) if(!product.hasParent()) this.addActor(product);
    }

    private void onProductClicked()
    {
        String sku = ProductIdentifiers.productToSku.get(this.products[this.current_product].getName());
        String[] list = new Json().fromJson(String[].class, Environment.Prefs.purchases.getString("purchased_items"));
        for (String str : list)
        {
            if (str != null && str.equals(sku))
            {
                Gdx.app.debug("InAppPurchaseStage", "The product \"" + sku + "\" is already purchased");
                showAlreadyPurchasedNotification();
                return; // Don't purchase if already purchased
            }
        }

        if(!this.purchaseProduct(sku)) // If purchase failed
            showPurchaseErrorNotification();
    }

    private void showAlreadyPurchasedNotification()
    {
        String msg = "This product is already purchased.";
        this.notificationMessage.setText(msg);
        this.notificationMessageLayout.setText(notificationMessage.getBitmapFont(), msg);
        this.notificationMessage.setPosition(getViewport().getWorldWidth()/2 - notificationMessageLayout.width/2, getViewport().getWorldHeight()/2 + notificationMessageLayout.height);
        this.addActor(notificationMessage);

        for (Image product : this.products) product.remove();

        this.notificationDeltaAccum = 0;
    }

    private void showPurchaseErrorNotification()
    {
        String msg = "An error occured while purchasing the product. \n Please try again later.\nIf the problem persists contact us \nat fcfruitstudios@gmail.com";
        this.notificationMessage.setText(msg);
        this.notificationMessageLayout.setText(notificationMessage.getBitmapFont(), msg);
        this.notificationMessage.setPosition(getViewport().getWorldWidth()/2 - notificationMessageLayout.width/2, getViewport().getWorldHeight()/2 + notificationMessageLayout.height);
        this.addActor(notificationMessage);

        for (Image product : this.products) product.remove();

        this.notificationDeltaAccum = 0;
    }

    private boolean purchaseProduct(String sku)
    {
        Gdx.app.debug("InAppPurchasesStage", "Attempting to purchase item: \"" + sku + "\"");
        return Environment.purchaseActivityInterface.purchase(sku);
    }
}
