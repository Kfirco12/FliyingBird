package com.example.kfir.flyingbird;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Explosion extends GameObjects {

    //-----variables declarations-----//
    private int row;
    private Bitmap spritesheet;
    //-----instances declarations-----//
    private ObjectsAnimation animation;

    //ctor.
    public Explosion(Bitmap res, int x, int y, int w, int h, int numFrames)
    {
        //-----variables  initialize-----//
        super.x = x;
        super.y = y;
        super.width = w;
        super.height = h;
        spritesheet = res;
        //-----instances initialize-----//
        Bitmap[] image = new Bitmap[numFrames];
        animation = new ObjectsAnimation();
        for(int i = 0; i<image.length;i++)
        {
            if(i%5==0 && i>0)row++;
            image[i] = Bitmap.createBitmap(spritesheet,(i-(5*row))*width, row*height, width, height);
        }
        //-----animation's variables initialize-----//
        animation.setFrames(image);
        animation.setDelay(10);

    }

    //=================================================================================================
    //update object.
    public void update()
    {
        //update the animation only once.
        if(!animation.getPlayedOnce())
            animation.update();

    }

    //=================================================================================================
    //draw object.
    public void draw(Canvas canvas)
    {
        //draw the animation only once.
        if(!animation.getPlayedOnce())
            canvas.drawBitmap(animation.getImage(), x, y, null);
    }
}

