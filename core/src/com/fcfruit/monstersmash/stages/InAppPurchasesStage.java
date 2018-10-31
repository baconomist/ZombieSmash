package com.fcfruit.monstersmash.stages;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.pay.Offer;
import com.badlogic.gdx.pay.OfferType;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseObserver;
import com.badlogic.gdx.pay.PurchaseSystem;
import com.badlogic.gdx.pay.Transaction;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fcfruit.monstersmash.Environment;
import com.fcfruit.monstersmash.ui.ImageButton;

import java.util.HashMap;

import static com.badlogic.gdx.pay.PurchaseManagerConfig.STORE_NAME_ANDROID_GOOGLE;

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
    private PurchaseManagerConfig purchaseManagerConfig;

    private Image title;
    private com.fcfruit.monstersmash.ui.ImageButton productButton;
    private com.fcfruit.monstersmash.ui.ImageButton backButton;
    private com.fcfruit.monstersmash.ui.ImageButton leftButton;
    private com.fcfruit.monstersmash.ui.ImageButton rightButton;
    private Image overlay;

    private boolean is_transitioning = false;
    private int direction;

    private int current_product;

    public InAppPurchasesStage(Viewport viewport)
    {
        super(viewport, "ui/in_app_purchase_store/in_app_purchase_store.json", "ui/in_app_purchase_store/", false);

        this.setUpPurchaseManager();

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

        this.createProducts();
        this.manageProducts();
        this.resetOverlay();

    }

    private void setUpPurchaseManager()
    {
        this.purchaseManagerConfig = new PurchaseManagerConfig();

        if (Gdx.app.getType() == Application.ApplicationType.Android)
        {
            this.purchaseManagerConfig.addStoreParam(STORE_NAME_ANDROID_GOOGLE, ProductIdentifiers.publicKey);

            this.purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("no_ads"));

            PurchaseSystem.setManager(Environment.purchaseManager);

            Environment.purchaseManager.install(new PurchaseObserver()
            {
                @Override
                public void handleInstall()
                {

                }

                @Override
                public void handleInstallError(Throwable e)
                {

                }

                @Override
                public void handleRestore(Transaction[] transactions)
                {

                }

                @Override
                public void handleRestoreError(Throwable e)
                {

                }

                @Override
                public void handlePurchase(Transaction transaction)
                {

                }

                @Override
                public void handlePurchaseError(Throwable e)
                {

                }

                @Override
                public void handlePurchaseCanceled()
                {

                }
            }, purchaseManagerConfig, true);


            Environment.purchaseManager.purchase("no_ads");

        }

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
    }

    private void onProductClicked()
    {
        this.purchaseProduct(ProductIdentifiers.productToSku.get(this.products[this.current_product].getName()));
    }

    private void purchaseProduct(String sku)
    {
        //Environment.purchaseManager.purchase("no_ads");
        Gdx.app.log("aaaa", "ssss");
    }
}
