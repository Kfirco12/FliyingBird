package com.example.kfir.flyingbird;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Coin extends GameObjects{
    //-----variables declarations-----//
    private final int speed;
    //-----instances declarations-----//
    private Bitmap sprite_sheet;
    private ObjectsAnimation animation;
    private RectF rect;

    public Coin(Bitmap res,int x, int y, int w, int h, int numOfFrames)
    {
        //-----variables initialize-----//
        super.width = w;
        super.height = h;
        super.x = x;
        if(y+height > GamePanel.HEIGHT)
            super.y = GamePanel.HEIGHT-height;
        else
            super.y = y;
        this.speed = 10;
        //-----instances initialize-----//
        this.sprite_sheet = res;
        animation = new ObjectsAnimation();
        rect = new RectF();
        Bitmap[] image = new Bitmap[numOfFrames];
        for(int i = 0; i<image.length; i++)
            image[i] = Bitmap.createBitmap(sprite_sheet, 0, i*height, width, height);
        //-----animation's variables initialize-----//
        animation.setDelay(100 - speed);
        animation.setFrames(image);
    }

    //===========================================================================================
    //update the coin's rect.
    private void rectUpdate()
    {
        rect.top = y + 30;
        rect.bottom = rect.top + height - 32;
        rect.left = x +60 + 30;
        rect.right = x + width - 60;
    }

    //===========================================================================================
    //return coin's rect.
    public RectF getRect() {
        return rect;
    }

    //=================================================================================================
    //update object.
    public void update()
    {
        rectUpdate();
        x -= speed;
        animation.update();
    }

    //=================================================================================================
    //draw object.
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }


}
