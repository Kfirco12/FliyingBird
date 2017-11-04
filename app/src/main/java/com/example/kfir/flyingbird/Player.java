package com.example.kfir.flyingbird;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Player extends GameObjects{
    //-----variables declarations-----//
    public static boolean crashed;
    private long start_time; //for calculate the score.
    private int difficult;
    private double y_accel; //acceleration up and down.
    //-----instances declarations-----//
    private Bitmap spritesheet;
    private boolean playing, up;
    private ObjectsAnimation animation;
    private RectF rect;

    //ctor.
    public Player(Bitmap res, int w, int h, int numOfFrames )
    {
        //-----variables  initialize-----//
        super.width = w;
        super.height = h;
        super.x = 100;
        super.y = GamePanel.HEIGHT/4;
        this.playing = false;
        this.difficult = 0;
        this.crashed = false;
        //-----instances initialize-----//
        rect = new RectF();
        animation = new ObjectsAnimation();
        Bitmap[] image = new Bitmap[numOfFrames];
        spritesheet = res;
        for(int i = 0; i<image.length; i++)
            image[i] = Bitmap.createBitmap(spritesheet, i*width,0,width,height);
        //-----animation's variables initialize-----//
        animation.setDelay(10);
        animation.setFrames(image);
        start_time = System.nanoTime();
    }
    //==========================================================================================
    //return the distance that the player passed.(the game will be difficult as much as the distance increased).
    public int getDifficult() {
        return difficult;
    }

    //==========================================================================================
    //set the wanted difficult.
    public void setDifficult(int difficult) {
        this.difficult = difficult;
    }

    //==========================================================================================
    //return if the game is currently playing.
    public boolean isPlaying() {
        return playing;
    }

    //==========================================================================================
    //set if the game currently playing or not.
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    //==========================================================================================
    //set if the player movement is up or down.
    public void setUp(boolean up)
    {
        this.up = up;
    }

    //==========================================================================================
    //reser acceleration.
    public void resetYAcc()
    {
        this.y_accel = 0;
    }

    //==========================================================================================
    //update the player's rect.
    private void rectUpdate()
    {
        //the left and right of the rect are always the same for the player.
        rect.top = y + 20 + 30;
        rect.bottom = rect.top + height - 32 - 30;
        rect.left = x +60 + 10;
        rect.right = x + width - 60 -10;
    }

    //==========================================================================================
    //return enemy's rect.
    public RectF getRect() {
        return rect;
    }

    //==========================================================================================
    //update object.
    public void update() {
        animation.update();
        long elapsed = (System.nanoTime() - start_time) / GamePanel.SECOND ^ 2;
        if (elapsed > 100) {
            difficult++;
            start_time = System.nanoTime();
        }

        if (up)
            dy = (int) (y_accel -= 0.8);

        else
            dy = (int) (y_accel += 0.8);

        if (dy > 8)
            dy = 8;
        else if (dy < -8)
            dy = -8;

        //update player position.
        rectUpdate();

        //collision with screen boundaries.
        if(y + height < -10 || y+height > GamePanel.HEIGHT)
            crashed = true;
        else
            y+=dy*2;

    }

    //==========================================================================================
    //draw object.
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(), x, y, null);
    }
}
