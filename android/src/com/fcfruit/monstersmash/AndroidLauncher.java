package com.fcfruit.monstersmash;

import android.app.ActivityManager;
import android.content.Context;
import android.os.*;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.pay.android.googleplay.AndroidGooglePlayPurchaseManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class AndroidLauncher extends AndroidApplication implements AdActivityInterface
{

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

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        Environment.purchaseManager = new AndroidGooglePlayPurchaseManager(this, 0);
        Environment.game = new MonsterSmash();
        Environment.adActivityInterface = this;

        MobileAds.initialize(this, AD_APP_ID);
        interstitialAd = new InterstitialAd(this);
        if(Config.DEBUG)
            interstitialAd.setAdUnitId(TEST_AD);
        else
            interstitialAd.setAdUnitId(REAL_AD);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(adListener);

        initialize(Environment.game, config);

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
