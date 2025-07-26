
package com.example.match3game2.activities;

import static com.example.match3game2.Config.WEBSITE_HOME_URL;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.match3game2.R;
import com.example.match3game2.util.Constants;
import com.example.match3game2.util.Tools;

public class AboutActivity extends AppCompatActivity {

    TextView appVersionTxt;
    LinearLayout contactBtn, rateBtn, shareBtn, privacyBtn, websiteBtn, deleteAccountBtn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;
        setContentView(R.layout.activity_about);

        setToolbar();
        initViews();

        // App Version Name
        appVersionTxt.setText(getString(R.string.app_version) + " " + Tools.getVersionName(this));

        contactBtn.setOnClickListener(v ->
        {
            Tools.contactAction(this);
        });

        rateBtn.setOnClickListener(v ->
        {
            Tools.rateAction(this);
        });

        shareBtn.setOnClickListener(v ->
        {
            Tools.shareAction(this);
        });

        privacyBtn.setOnClickListener(v ->
        {
            Tools.goToActivity(this, PrivacyActivity.class);
        });

        websiteBtn.setOnClickListener(v ->
        {
            Intent website = new Intent(Intent.ACTION_VIEW);
            website.setData(Uri.parse(WEBSITE_HOME_URL));
            startActivity(website);
        });

        deleteAccountBtn.setOnClickListener(v ->
        {
            Tools.customDialog(this, R.drawable.trash, getString(R.string.delete_account),
                    getString(R.string.delete_your_account), getString(R.string.delete), v2->
                    {
                        Tools.deleteUserAccount(this);
                    });
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.toolbar_about);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void initViews()
    {
        appVersionTxt = findViewById(R.id.appVersionTxt);
        contactBtn = findViewById(R.id.contactBtn);
        rateBtn = findViewById(R.id.rateBtn);
        shareBtn = findViewById(R.id.shareBtn);
        privacyBtn = findViewById(R.id.privacyBtn);
        websiteBtn = findViewById(R.id.websiteBtn);
        deleteAccountBtn = findViewById(R.id.deleteAccountBtn);
    }
}