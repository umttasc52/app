package com.example.match3game2.activities;

import static com.example.match3game2.Config.ADS_NETWORK;
import static com.example.match3game2.Config.USER_ACHIEVEMENTS_URL;
import static com.example.match3game2.Config.USER_PROFILE;
import static com.example.match3game2.game.Constants.cellWidth;
import static com.example.match3game2.game.Constants.drawX;
import static com.example.match3game2.game.Constants.drawY;
import static com.example.match3game2.game.Constants.screenHeight;
import static com.example.match3game2.game.Constants.screenWidth;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.match3game2.MainActivity;
import com.example.match3game2.R;
import com.example.match3game2.game.GameView;
import com.example.match3game2.game.Levels;
import com.example.match3game2.util.AdsManager;
import com.example.match3game2.util.Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static TextView scoreTxt, moveTxt, levelTxt, gameLevelTxt, gameLevelRequireTxt;
    public static String email2;
    public String email, password;
    ProgressDialog pDialog;
    SharedPreferences prefs;
    public static int levelScore, levelMoves;
    AdsManager adsManager;

    // Lightning effect
    private View lightningEffect;
    private Handler lightningHandler = new Handler(Looper.getMainLooper());
    private Random random = new Random();

    // Level container animation
    private View levelContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Ekran boyutları ve çizim koordinatlarını ayarla
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        cellWidth = screenWidth / 9;
        drawX = (float) (screenWidth - cellWidth * 9) / 2;
        drawY = cellWidth * 3;

        // Layout'u yükle
        setContentView(R.layout.activity_game);

        // Görünümleri başlat
        initViews();
        setupLightningEffect();
        applyStatsPulseAnimation();

        // Kullanıcı verilerini al
        prefs = getSharedPreferences("User", Context.MODE_PRIVATE);
        email = prefs.getString("userEmail", "");
        email2 = prefs.getString("userEmail", "");
        password = prefs.getString("userPassword", "");

        // Reklamları yükle
        adsManager = new AdsManager(this);
        if (ADS_NETWORK.equals("admob")) {
            adsManager.showBannerAdmobAds();
        } else if (ADS_NETWORK.equals("applovin")) {
            adsManager.initApplovinAds();
        }

        // Oyun alanını ayarla
        FrameLayout root_layout = findViewById(R.id.root_layout);


        // GameView oluştur
        GameView gameView = new GameView(this);

        // SurfaceView ayarları - şeffaflık için
        gameView.setZOrderOnTop(true);
        gameView.getHolder().setFormat(android.graphics.PixelFormat.TRANSPARENT);

        // GameView'ı layout'a ekle
        root_layout.addView(gameView);
        gameView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // İlerleme barını göster ve seviye bilgisini yükle
        openProgressBar();
        getGameLevel();
    }
    protected void initViews() {
        scoreTxt = findViewById(R.id.scoreTxt);
        moveTxt = findViewById(R.id.moveTxt);
        levelTxt = findViewById(R.id.levelTxt);
        lightningEffect = findViewById(R.id.lightning_effect);

        // Find level container for animation
        if (levelTxt != null && levelTxt.getParent() instanceof View) {
            levelContainer = (View) levelTxt.getParent();
        }
    }

    // Setup lightning effect similar to MainActivity
    private void setupLightningEffect() {
        if (lightningEffect == null) return;

        lightningHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && !isDestroyed()) {
                    if (random.nextInt(100) < 20) { // Less frequent than main screen
                        showLightningEffect();
                    }

                    // Schedule next lightning
                    int nextDelay = 8000 + random.nextInt(12000);
                    lightningHandler.postDelayed(this, nextDelay);
                }
            }
        }, 5000); // First check after 5 seconds
    }

    // Show quick lightning flash effect
    private void showLightningEffect() {
        if (lightningEffect == null) return;

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(lightningEffect, "alpha", 0f, 0.5f);
        alphaAnimator.setDuration(50);
        alphaAnimator.start();

        lightningHandler.postDelayed(() -> {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(lightningEffect, "alpha", 0.5f, 0f);
            fadeOut.setDuration(150);
            fadeOut.start();

            if (random.nextBoolean()) {
                lightningHandler.postDelayed(() -> {
                    ObjectAnimator secondFlash = ObjectAnimator.ofFloat(lightningEffect, "alpha", 0f, 0.2f);
                    secondFlash.setDuration(30);
                    secondFlash.start();

                    lightningHandler.postDelayed(() -> {
                        ObjectAnimator secondFade = ObjectAnimator.ofFloat(lightningEffect, "alpha", 0.2f, 0f);
                        secondFade.setDuration(100);
                        secondFade.start();
                    }, 30);
                }, 200);
            }
        }, 50);
    }

    // Apply subtle pulse animation to level display to draw attention
    private void applyStatsPulseAnimation() {
        if (levelContainer == null) return;

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(levelContainer, "scaleX", 1.0f, 1.05f);
        scaleX.setDuration(1500);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(levelContainer, "scaleY", 1.0f, 1.05f);
        scaleY.setDuration(1500);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatMode(ObjectAnimator.REVERSE);

        scaleX.start();
        scaleY.start();
    }

    @SuppressLint("SetTextI18n")
    private void showDialogGameLevel(String level) {
        Dialog dialog = new Dialog(this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_game_level);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        gameLevelTxt = dialog.findViewById(R.id.gameLevelTxt);
        gameLevelRequireTxt = dialog.findViewById(R.id.gameLevelRequireTxt);

        gameLevelTxt.setText("Level: " + level); // in dialog

        dialog.findViewById(R.id.bt_ok).setOnClickListener(v -> {
            dialog.dismiss();
            showLightningEffect(); // Add lightning effect when closing dialog
        });

        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void getGameLevel() {
        RequestQueue myRequestQueue = Volley.newRequestQueue(this);
        StringRequest myStringRequest = new StringRequest(Request.Method.POST, USER_PROFILE, response -> {
            Pattern pattern = Pattern.compile("L1(.*?)L2");
            Matcher matcher = pattern.matcher(response);

            if (matcher.find()) {
                levelTxt.setText(matcher.group(1)); // Level in game board

                showDialogGameLevel(matcher.group(1)); // Level in dialog

                Levels.setGameLevelDetails(getApplicationContext(), Integer.parseInt(matcher.group(1))); // set game level details

                checkAchievements(Integer.parseInt(matcher.group(1))); // check achievements

                closeProgressBar(); // close progress bar

                // Toast.makeText(GameActivity.this, "r:" + levelScore + " - " + levelMoves, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(getApplicationContext(), "error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("email", email);
                MyData.put("password", password);
                return MyData;
            }
        };

        myRequestQueue.add(myStringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Tools.goToActivity(this, MainActivity.class);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Prevent memory leaks
        lightningHandler.removeCallbacksAndMessages(null);
    }

    private void checkAchievements(int gameLevel) {
        gameLevel = gameLevel - 1;
        if (gameLevel >= 5) {
            sendRequest("1");
        }
        if (gameLevel >= 25) {
            sendRequest("2");
        }
        if (gameLevel >= 50) {
            sendRequest("3");
        }
    }

    public void sendRequest(String achID) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, USER_ACHIEVEMENTS_URL, response -> Log.e("SetAchievementsResponse", response), error -> {
            //This code is executed if there is an error.
            Toast.makeText(getApplicationContext(), "error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            //closeProgressBar();
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("email", email);
                MyData.put("action", "Set");
                MyData.put("achID", achID);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }

    protected void openProgressBar() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
    }

    protected void closeProgressBar() {
        pDialog.dismiss();
    }
}