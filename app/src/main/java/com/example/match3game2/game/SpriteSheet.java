package com.example.match3game2.game;

import static com.example.match3game2.game.Constants.*;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;

public class SpriteSheet {
    public Bitmap topBG;
    public Bitmap bottomBG;
    public Bitmap bg_middle;
    public Bitmap candiesBMP;
    public Bitmap candy1;
    public Bitmap candy2;
    public Bitmap candy3;
    public Bitmap candy4;
    public Bitmap candy5;
    public Bitmap candy6;


    public SpriteSheet(Context context)
    {
        AssetManager assetManager = context.getAssets();

        try {
            // Game Top Image
            topBG = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            bottomBG = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            bg_middle = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            // Sadece şeker görsellerini yükleyin
            InputStream inputStream = assetManager.open("candy1.png");
            candiesBMP = BitmapFactory.decodeStream(inputStream);
            candy1 = Bitmap.createBitmap(candiesBMP, 0, 0, candiesBMP.getWidth(), candiesBMP.getHeight());
            candy1 = Bitmap.createScaledBitmap(candy1, cellWidth, cellWidth, false);

            // Game Candies Images
            inputStream = assetManager.open("candy2.png");
            candiesBMP = BitmapFactory.decodeStream(inputStream);
            candy2 = Bitmap.createBitmap(candiesBMP, 0, 0, candiesBMP.getWidth(), candiesBMP.getHeight());
            candy2 = Bitmap.createScaledBitmap(candy2, cellWidth, cellWidth, false);

            // Game Candies Images
            inputStream = assetManager.open("candy3.png");
            candiesBMP = BitmapFactory.decodeStream(inputStream);
            candy3 = Bitmap.createBitmap(candiesBMP, 0, 0, candiesBMP.getWidth(), candiesBMP.getHeight());
            candy3 = Bitmap.createScaledBitmap(candy3, cellWidth, cellWidth, false);

            // Game Candies Images
            inputStream = assetManager.open("candy4.png");
            candiesBMP = BitmapFactory.decodeStream(inputStream);
            candy4 = Bitmap.createBitmap(candiesBMP, 0, 0, candiesBMP.getWidth(), candiesBMP.getHeight());
            candy4 = Bitmap.createScaledBitmap(candy4, cellWidth, cellWidth, false);

            // Game Candies Images
            inputStream = assetManager.open("candy5.png");
            candiesBMP = BitmapFactory.decodeStream(inputStream);
            candy5 = Bitmap.createBitmap(candiesBMP, 0, 0, candiesBMP.getWidth(), candiesBMP.getHeight());
            candy5 = Bitmap.createScaledBitmap(candy5, cellWidth, cellWidth, false);

            // Game Candies Images
            inputStream = assetManager.open("candy6.png");
            candiesBMP = BitmapFactory.decodeStream(inputStream);
            candy6 = Bitmap.createBitmap(candiesBMP, 0, 0, candiesBMP.getWidth(), candiesBMP.getHeight());
            candy6 = Bitmap.createScaledBitmap(candy6, cellWidth, cellWidth, false);


        }catch (Exception e)
        {
            Log.e("spriteSheet", e.getMessage());
        }

    }
}