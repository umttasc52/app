package com.example.match3game2.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdk;
import com.example.match3game2.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.concurrent.TimeUnit;

public class AdsManager implements MaxAdListener, MaxRewardedAdListener {
    static InterstitialAd mInterstitialAd;
    RewardedAd mRewardedAd;
    MaxRewardedAd rewardedAd;
    static MaxInterstitialAd maxInterstitialAd;
    int retryAttempt;
    Activity activity;

    public AdsManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * Show Banner Ads from Admob
     */
    public void showBannerAdmobAds() {
        try {
            MobileAds.initialize(activity, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    // Başlatma tamamlandı, banner reklamı yüklemeyi dene
                    loadBannerAd();
                }
            });
        } catch (Exception e) {
            // Herhangi bir hata olursa loglayıp devam et
            Log.e("AdsManager", "Error initializing ads: " + e.getMessage());
        }
    }

    /**
     * Load banner ad with error handling
     */
    private void loadBannerAd() {
        try {
            AdView mAdView = activity.findViewById(R.id.adView);
            if (mAdView != null) {
                AdRequest adRequest = new AdRequest.Builder().build();

                // Yükleme hatası durumunda callback ekleyelim
                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        // Hata loglanır ama uygulama çalışmaya devam eder
                        Log.i("AdsManager", "Banner ad failed to load: " + loadAdError.getCode());
                    }
                });

                // Reklamı yükleyelim
                mAdView.loadAd(adRequest);
            }
        } catch (Exception e) {
            Log.e("AdsManager", "Error loading banner ad: " + e.getMessage());
        }
    }

    /**
     * Load Interstitial Ads for Admob
     */
    public void setInterAds() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(activity, activity.getString(R.string.admob_interstitial_id), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            Log.i("AdsManager", "Interstitial ad loaded");

                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when fullscreen content is dismissed.
                                    Log.d("AdsManager", "Interstitial ad was dismissed.");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    // Called when fullscreen content failed to show.
                                    Log.d("AdsManager", "Interstitial ad failed to show.");
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    mInterstitialAd = null;
                                    Log.d("AdsManager", "Interstitial ad was shown.");
                                }
                            });
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.i("AdsManager", "Interstitial ad failed to load: " + loadAdError.getMessage());
                            mInterstitialAd = null;
                        }
                    });
        } catch (Exception e) {
            Log.e("AdsManager", "Error loading interstitial ad: " + e.getMessage());
            mInterstitialAd = null;
        }
    }

    /**
     * Show Interstitial Ads for Admob
     */
    public void showInterstitial() {
        try {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(activity);
                // Yeni bir interstitial yükle
                setInterAds();
            } else {
                Log.d("AdsManager", "The interstitial ad wasn't ready yet.");
                // Eğer hazır değilse, yeni bir tane yükle
                setInterAds();
            }
        } catch (Exception e) {
            Log.e("AdsManager", "Error showing interstitial ad: " + e.getMessage());
        }
    }

    /**
     * Load Rewarded Ads for Admob
     */
    public void setRewardedAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();

            RewardedAd.load(activity, activity.getString(R.string.admob_reward_id),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d("AdsManager", "Rewarded ad failed to load: " + loadAdError.getMessage());
                            mRewardedAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;
                            Log.d("AdsManager", "Rewarded ad loaded successfully.");

                            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdShowedFullScreenContent() {
                                    // Called when ad is shown.
                                    Log.d("AdsManager", "Rewarded ad was shown.");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    // Called when ad fails to show.
                                    Log.d("AdsManager", "Rewarded ad failed to show.");
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when ad is dismissed.
                                    // Set the ad reference to null so you don't show the ad a second time.
                                    Log.d("AdsManager", "Rewarded ad was dismissed.");
                                    mRewardedAd = null;
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            Log.e("AdsManager", "Error loading rewarded ad: " + e.getMessage());
            mRewardedAd = null;
        }
    }

    /**
     * Show Rewarded Ads for Admob
     */
    public void showRewardsAs() {
        try {
            if (mRewardedAd != null) {
                mRewardedAd.show(activity, rewardItem -> {
                    // Handle the reward.
                    Log.d("AdsManager", "The user earned the reward.");
                    // Yeni bir rewarded ad yükle
                    setRewardedAd();
                });
            } else {
                Log.d("AdsManager", "The rewarded ad wasn't ready yet.");
                // Eğer hazır değilse, yeni bir tane yükle
                setRewardedAd();
            }
        } catch (Exception e) {
            Log.e("AdsManager", "Error showing rewarded ad: " + e.getMessage());
        }
    }

    // Applovin Ads - MaxAds -----------------------------------------------------
    /**
     * Initialize Applovin Ads
     */
    public void initApplovinAds() {
        try {
            AppLovinSdk.getInstance(activity).setMediationProvider("max");
            AppLovinSdk.initializeSdk(activity, configuration -> {
                // AppLovin SDK is initialized, start loading ads
                showMaxBannerAd();
            });
        } catch (Exception e) {
            Log.e("AdsManager", "Error initializing AppLovin ads: " + e.getMessage());
        }
    }

    /**
     * Show Applovin Banner Ads
     */
    public void showMaxBannerAd() {
        try {
            MaxAdView maxBannerAdView = activity.findViewById(R.id.MaxAdView);
            if (maxBannerAdView != null) {
                maxBannerAdView.loadAd();
            }
        } catch (Exception e) {
            Log.e("AdsManager", "Error showing MaxBanner ad: " + e.getMessage());
        }
    }

    /**
     * Load Applovin Interstitial Ads
     */
    public void loadMaxInterstitialAd() {
        try {
            maxInterstitialAd = new MaxInterstitialAd(activity.getString(R.string.appLovin_interstitial_id), activity);
            maxInterstitialAd.setListener(this);
            maxInterstitialAd.loadAd();
        } catch (Exception e) {
            Log.e("AdsManager", "Error loading MaxInterstitial ad: " + e.getMessage());
            maxInterstitialAd = null;
        }
    }

    /**
     * Show Applovin Interstitial Ads
     */
    public void showMaxInterstitialAd() {
        try {
            if (maxInterstitialAd != null && maxInterstitialAd.isReady()) {
                maxInterstitialAd.showAd();
            } else {
                Log.d("AdsManager", "The MaxInterstitial ad wasn't ready yet.");
                // Hazır değilse, yeni bir tane yükle
                loadMaxInterstitialAd();
            }
        } catch (Exception e) {
            Log.e("AdsManager", "Error showing MaxInterstitial ad: " + e.getMessage());
        }
    }

    /**
     * Load Rewards Ads for Applovin
     */
    public void loadMaxRewardsAd() {
        try {
            rewardedAd = MaxRewardedAd.getInstance(activity.getString(R.string.appLovin_rewards_id), activity);
            rewardedAd.setListener(this);
            rewardedAd.loadAd();
        } catch (Exception e) {
            Log.e("AdsManager", "Error loading MaxRewards ad: " + e.getMessage());
            rewardedAd = null;
        }
    }

    /**
     * Show Applovin Rewarded Video Ads
     */
    public void showMaxVideoAds() {
        try {
            if (rewardedAd != null && rewardedAd.isReady()) {
                rewardedAd.showAd();
            } else {
                Log.d("AdsManager", "The MaxRewarded ad wasn't ready yet.");
                // Hazır değilse, yeni bir tane yükle
                loadMaxRewardsAd();
            }
        } catch (Exception e) {
            Log.e("AdsManager", "Error showing MaxVideo ad: " + e.getMessage());
        }
    }

    // MaxAdListener Interface Methods
    @Override
    public void onAdLoaded(@NonNull final MaxAd maxAd) {
        // Ad yükleme başarılı, yeniden deneme sayacını sıfırla
        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(@NonNull final String adUnitId, @NonNull final MaxError error) {
        try {
            // Yeniden deneme sayacını artır
            retryAttempt++;
            long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

            new Handler().postDelayed(() -> {
                try {
                    if (maxInterstitialAd != null) {
                        maxInterstitialAd.loadAd();
                    }
                } catch (Exception e) {
                    Log.e("AdsManager", "Error in retry load: " + e.getMessage());
                }
            }, delayMillis);
        } catch (Exception e) {
            Log.e("AdsManager", "Error in onAdLoadFailed: " + e.getMessage());
        }
    }

    @Override
    public void onAdDisplayFailed(@NonNull final MaxAd maxAd, @NonNull final MaxError error) {
        try {
            // Reklamı gösterme başarısız oldu, yeni reklamlar yükle
            Log.d("AdsManager", "MaxAd display failed: " + error.getMessage());

            if (maxInterstitialAd != null) {
                maxInterstitialAd.loadAd();
            }

            if (rewardedAd != null) {
                rewardedAd.loadAd();
            }
        } catch (Exception e) {
            Log.e("AdsManager", "Error in onAdDisplayFailed: " + e.getMessage());
        }
    }

    @Override
    public void onAdDisplayed(@NonNull final MaxAd maxAd) {
        // Reklam gösterildi, gerekirse burada ek işlemler yapabilirsiniz
        Log.d("AdsManager", "MaxAd displayed successfully");
    }

    @Override
    public void onAdClicked(@NonNull final MaxAd maxAd) {
        // Reklama tıklandı, gerekirse burada ek işlemler yapabilirsiniz
        Log.d("AdsManager", "MaxAd was clicked");
    }

    @Override
    public void onAdHidden(@NonNull final MaxAd maxAd) {
        try {
            // Reklam kapatıldı, yeni reklamlar yükle
            Log.d("AdsManager", "MaxAd was hidden");

            if (maxInterstitialAd != null) {
                maxInterstitialAd.loadAd();
            }

            if (rewardedAd != null) {
                rewardedAd.loadAd();
            }
        } catch (Exception e) {
            Log.e("AdsManager", "Error in onAdHidden: " + e.getMessage());
        }
    }

    @Override
    public void onUserRewarded(@NonNull final MaxAd maxAd, @NonNull final MaxReward maxReward) {
        // Kullanıcı ödüllendirildi
        Log.d("AdsManager", "User was rewarded with " + maxReward.getAmount() + " " + maxReward.getLabel());
        // Burada kullanıcıya ödül verebilirsiniz
    }


}