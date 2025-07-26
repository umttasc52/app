package com.example.match3game2.util;

import static android.content.Context.MODE_PRIVATE;
import static com.example.match3game2.Config.DELETE_ACCOUNT_URL;
import static com.example.match3game2.Config.EMAIL_ADDRESS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.match3game2.R;
import com.example.match3game2.activities.WelcomeActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Tools {

    /**
     * Start new activity
     */
    public static void goToActivity(Context context, Class<?> activity)
    {
        Intent intent = new Intent(context, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void setAnimationBtn(Context context, Button button, int type)
    {
        Animation myAnim;

        switch (type)
        {
            case 1:
                myAnim = AnimationUtils.loadAnimation(context, R.anim.button_pulse);
                break;
            case 2:
                myAnim = AnimationUtils.loadAnimation(context, R.anim.floating_up);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        button.startAnimation(myAnim);
    }

    public static String getVersionName(Context ctx) {
        try {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return ctx.getString(R.string.version_unknown);
        }
    }

    public static void rateAction(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    public static void shareAction(Activity activity)
    {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.Share_Text));
        activity.startActivity(Intent.createChooser(sharingIntent, "share"));
    }

    public static void contactAction(Activity activity)
    {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_ADDRESS});
        email.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
        email.putExtra(Intent.EXTRA_TEXT, "Text");
        email.setType("message/rfc822");
        activity.startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    public static void copyToClipboard(Context context, String data) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("clipboard", data);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show();
    }

    /**
     * Show Custom Dialog
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public static void customDialog(Context context, int drawable, String title, String message, String btnName, View.OnClickListener onSubmitClickListener)
    {
        Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ImageView dl_logo = dialog.findViewById(R.id.dl_logo);
        TextView dl_title = dialog.findViewById(R.id.dl_title);
        TextView dl_message = dialog.findViewById(R.id.dl_message);

        dl_logo.setImageDrawable(context.getDrawable(drawable));
        dl_title.setText(title);
        dl_message.setText(message);

        Button btnSubmit = dialog.findViewById(R.id.btn_submit);
        btnSubmit.setText(btnName);

        if (onSubmitClickListener != null) {
            btnSubmit.setOnClickListener(v -> {
                onSubmitClickListener.onClick(v); // Perform the custom action
                dialog.dismiss(); // Dismiss the dialog after action
            });
        } else {
            btnSubmit.setOnClickListener(v -> dialog.dismiss()); // Default action
        }

        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    /**
     * Delete User Account
     */
    public static void deleteUserAccount(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("User", MODE_PRIVATE);
        String email = prefs.getString("userEmail", "");

        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        @SuppressLint("SetTextI18n")
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, DELETE_ACCOUNT_URL, response ->
        {

            if (response.contains("Successfully"))
            {
                // Clear all SharedPreferences data
                clearAllSharedPreferences(context);

                // Start WelcomeActivity
                Tools.goToActivity(context, WelcomeActivity.class);

                // Finish the activity
                ((Activity) context).finish();
            }
            else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }

        }, error -> {
            System.out.println(error.toString());
        })
        {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("email", email);
                return MyData;
            }
        };
        MyRequestQueue.add(MyStringRequest);
    }

    /**
     * Clear all SharedPreferences data
     */
    public static void clearAllSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
