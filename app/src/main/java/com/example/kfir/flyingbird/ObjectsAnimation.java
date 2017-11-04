package com.example.kfir.flyingbird;

import android.graphics.Bitmap;

public class ObjectsAnimation {

    private Bitmap[] frames;
    private int current_frame;
    private long start_time;
    private long delay;
    private boolean played_once;

    //=========================================================================================
    //set the sprite sheet frames.
    public void setFrames(Bitmap[] frames) {
        this.frames = frames;
        current_frame = 0;
        start_time = System.nanoTime();
    }

    //=========================================================================================
    //determine the start time of the sprite sheet frames running.
    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    //=========================================================================================
    //set delay between every frame in the sprite sheet.
    public void setDelay(long delay) {
        this.delay = delay;
    }

    //=========================================================================================
    //run through the bitmap frames.
    public void update() {
        //which frame image we are going to use.
        long elapsed = (System.nanoTime() - start_time) / GamePanel.SECOND ^ 2;
        if (elapsed > delay) {
            current_frame++;
            start_time = System.nanoTime();
        }
        if (current_frame == frames.length) {
            current_frame = 0;
            played_once = true;
        }
    }

    //=========================================================================================
    //get the sprite sheet.
    public Bitmap getImage() {
        return frames[current_frame];
    }

    //=========================================================================================
    //which frame in the sprite sheet.
    public int getCurrent_frame() {
        return current_frame;
    }

    //=========================================================================================
    //check if the animation finished once(to prevent loop like explosion or to play it again like other objects).
    public boolean getPlayedOnce() {
        return played_once;
    }
}

