package com.example.kfir.flyingbird;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Background {
    private int x, y, dx; //initialized to zero.
    private Bitmap image, old;
    private int screen_width = GamePanel.WIDTH;
    private int screen_height = GamePanel.HEIGHT;

    //=========================================================================================
    //constructor.
    public Background(Bitmap img) {
        //scale the background to screen size.
        this.image = Bitmap.createScaledBitmap(img,screen_width,screen_height,false);
        this.dx = GamePanel.GAMESPEED;
    }

    //=========================================================================================
    //scroll the background.
    public void update() {
        x += dx;
        if (x < -GamePanel.WIDTH)
            x = 0;
    }

    //=========================================================================================
    //draw background.
    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
        if (x < 0)
            canvas.drawBitmap(image, x + GamePanel.WIDTH, y, new Paint(Paint.ANTI_ALIAS_FLAG));
    }

}

