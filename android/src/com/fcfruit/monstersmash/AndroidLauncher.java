package com.fcfruit.monstersmash;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;


import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

public class AndroidLauncher extends AndroidApplication implements AdActivityInterface, PurchaseActivityInterface, CrashLoggerInterface, PrivacyPolicyInterface
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

    private WebView policyWebView;
    private Button policyBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        Environment.adActivityInterface = this;
        Environment.purchaseActivityInterface = this;
        Environment.crashLoggerInterface = this;
        Environment.game = new MonsterSmash();
        Environment.privacyPolicyInterface = this;

        MobileAds.initialize(this, AD_APP_ID);
        interstitialAd = new InterstitialAd(this);

        if (Config.DEBUG)
            interstitialAd.setAdUnitId(TEST_AD);
        else
            interstitialAd.setAdUnitId(REAL_AD);

        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(adListener);

        this.setupIABPurchase();

        this.setupPrivacyPolicy();

        initialize(Environment.game, config);
    }

    private void setupPrivacyPolicy()
    {
        BufferedReader reader = null;
        StringBuilder html = new StringBuilder();

        try
        {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("privacy_policy/en.html")));
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null)
            {
                //process line
                html.append(mLine);
            }
        } catch (IOException e)
        {
            //log the exception
            Log.e("", e.toString());
            e.printStackTrace();
        } finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                } catch (IOException e)
                {
                    //log the exception
                }
            }
        }

        RelativeLayout.LayoutParams relativeLayout = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // Top Left Apparently
        relativeLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        policyBackButton = new Button(this);
        policyBackButton.setText("Back");
        policyBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getWindowManager().removeView(policyWebView);
            }
        });
        policyWebView = new WebView(this);
        policyWebView.loadData(html.toString(), "text/html; charset=utf-8", "utf-8");
        policyWebView.addView(policyBackButton);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            // This is where you do your work in the UI thread.
            // Your worker tells you in the message what to do.
            getWindowManager().addView(policyWebView, new WindowManager.LayoutParams());
        }
    };
    @Override
    public void showPrivacyPolicy()
    {
        // And this is how you call it from the worker thread:
        Message message = mHandler.obtainMessage();
        message.sendToTarget();
    }

    @Override
    public void crash_log(String tag, String message)
    {
        Crashlytics.log(tag + ": " + message);
    }

    private boolean isBpAvailable()
    {
        return bp.isInitialized() && BillingProcessor.isIabServiceAvailable(this);
    }

    @Override
    public boolean purchase(String item_sku)
    {
        return isBpAvailable() && bp.purchase(this, item_sku); // This will activate the onPurchased() func if purchased
    }

    private void onPurchased(String item_sku, TransactionDetails details)
    {
        Environment.purchaseManager.save_purchase(item_sku);
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
        if (isBpAvailable())
        {
            for (String sku : bp.listOwnedProducts())
            {
                Environment.purchaseManager.save_purchase(sku);
            }
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
                onPurchased(productId, details);
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
                bp.loadOwnedPurchasesFromGoogle(); // Refresh purchase cache of bp
            }

            @Override
            public void onPurchaseHistoryRestored()
            {
                Log.i("ANDOIRD_PURCHASE", "onPurchaseHistoryRestored");
                for (String sku : bp.listOwnedProducts())
                    Log.d("ANDOIRD_PURCHASE", "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d("ANDOIRD_PURCHASE", "Owned Subscription: " + sku);
                restore_purchases();
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
        for (String str : list)
        {
            if (str != null && str.equals("no_ads"))
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
