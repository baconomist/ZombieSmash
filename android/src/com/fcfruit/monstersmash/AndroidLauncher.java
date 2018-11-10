package com.fcfruit.monstersmash;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

public class AndroidLauncher extends AndroidApplication implements AdActivityInterface, PurchaseActivityInterface
{
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlkx9W8KLfKJE9RoU0lA0/pp72GKWIC+KhSA7IQTaOyIt0vgTRsI+oEkrrUVE7xxAOKNic+Zlc7CNy5NFK970GCQqAGEauFsKVZjpk60MURj5bBeeNq34OZgJ9soH1oTvrtgVNp/ZMIGZz9vog+3YRuGhoc4sxX2JAB5Iow6hoJ4NLGzcGyGTF6rCs0n05jV+igrpQ3TPhGcNB0entFCEySSgKWgUIwl8DcN6eCysh4YK1toMrBgdlMVfJCYd01OpMIqeT7ZDQNuUSsieBBphcWv+VnPzkGk4Wc/91bVsVgwwqadXQtKdG/9c/+AhvMw0nF9+Qs3rEnL4Sc5JFV5QnwIDAQAB"; // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID = null;
    private BillingProcessor bp;

    private InterstitialAd interstitialAd;
    private static String AD_APP_ID = "ca-app-pub-4220591610119162~6944345471";
    private static String TEST_AD = "ca-app-pub-3940256099942544/1033173712";
    private static String REAL_AD = "ca-app-pub-4220591610119162/1356853166";

    private AdListener adListener = new AdListener()
    {
        @Override
        public void onAdLoaded()
        {
            super.onAdLoaded();
        }

        @Override
        public void onAdClosed()
        {
            super.onAdClosed();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        Environment.adActivityInterface = this;
        Environment.purchaseActivityInterface = this;
        Environment.game = new MonsterSmash();

        MobileAds.initialize(this, AD_APP_ID);
        interstitialAd = new InterstitialAd(this);

        if (Config.DEBUG)
            interstitialAd.setAdUnitId(TEST_AD);
        else
            interstitialAd.setAdUnitId(REAL_AD);

        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(adListener);

        this.setupIABPurchase();

        initialize(Environment.game, config);

    }

    private boolean isBpAvailable()
    {
        return bp.isInitialized() && BillingProcessor.isIabServiceAvailable(this);
    }

    @Override
    public boolean purchase(String item_sku)
    {

        if (isBpAvailable() && bp.purchase(this, item_sku))
        {
            String[] purchased_items_old = json.fromJson(String[].class, Environment.Prefs.purchases.getString("purchased_items"));
            String[] purchased_items = new String[purchased_items_old.length + 1];

            // Copy old list into new one
            for(int i = 0; i < purchased_items_old.length; i++)
            {
                purchased_items[i] = purchased_items_old[i];
            }

            purchased_items[purchased_items.length - 1] = item_sku;

            Environment.Prefs.purchases.putString("purchased_items", json.toJson(purchased_items));
            Environment.Prefs.purchases.flush();
        }

        return false;
    }

    @Override
    public boolean is_purchased(String item_sku)
    {
        return isBpAvailable() && bp.isPurchased(item_sku);
    }

    @Override
    public boolean consume_purchase(String sku)
    {
        return isBpAvailable() && bp.consumePurchase(sku);
    }

    @Override
    public boolean restore_purchases()
    {
        if(isBpAvailable())
        {
            String[] purchased_items = new String[bp.listOwnedProducts().size()];

            int i = 0;
            for (String sku : bp.listOwnedProducts())
            {
                purchased_items[i] = sku;
                i++;
            }

            Environment.Prefs.purchases.putString("purchased_items", json.toJson(purchased_items));
            Environment.Prefs.purchases.flush();

            return true;
        }
        return false;
    }

    private void setupIABPurchase()
    {
        if (!BillingProcessor.isIabServiceAvailable(this))
        {
            Log.i("AndroidLauncher", "In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = BillingProcessor.newBillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler()
        {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details)
            {
                Log.i("onProductPurchased: ", productId);
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error)
            {
                Log.i("ANDOIRD_PURCHASE", "onBillingError: " + Integer.toString(errorCode));
            }

            @Override
            public void onBillingInitialized()
            {
                Log.i("ANDOIRD_PURCHASE", "onBillingInitialized");
            }

            @Override
            public void onPurchaseHistoryRestored()
            {
                Log.i("ANDOIRD_PURCHASE", "onPurchaseHistoryRestored");
                for (String sku : bp.listOwnedProducts())
                    Log.d("ANDOIRD_PURCHASE", "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d("ANDOIRD_PURCHASE", "Owned Subscription: " + sku);
            }
        });

        bp.initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy()
    {
        if (bp != null)
        {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // Might crash as Environment.settings might not yet be initialized
        try
        {
            // Disable recent apps button
            if (!Environment.settings.isRecentAppsButtonEnabled())
            {
                ActivityManager activityManager = (ActivityManager) getApplicationContext()
                        .getSystemService(Context.ACTIVITY_SERVICE);
                // If apk version matches required for this function call
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                {
                    activityManager.moveTaskToFront(getTaskId(), 0);
                }
            }
        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void showAds()
    {
        String[] list = json.fromJson(String[].class, Environment.Prefs.purchases.getString("purchased_items"));
        for(String str : list)
        {
            if(str != null && str.equals("no_ads"))
                return; // Don't show ads if no_ads is purchased
        }

        try
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (interstitialAd.isLoaded())
                        interstitialAd.show();
                    else
                        interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        } catch (Exception e)
        {
        }

    }
}
