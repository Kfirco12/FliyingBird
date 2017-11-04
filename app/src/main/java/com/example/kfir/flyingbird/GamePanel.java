package com.example.kfir.flyingbird;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends SurfaceView implements Runnable {
    //-----final declarations-----//
    public static int WIDTH;
    public static int HEIGHT;
    public static long SECOND = 1000;
    public static final int GAMESPEED = -5;
    private final long FPS = 30;    //tracks the game frame rate(frame per second).
    //-----variables declarations-----//
    private double averageFPS;
    private boolean playing, collision;    //flag for knowing if playing or not.
    private long enemies_start_time, coins_start_time;
    private long current_frame_time; //help to calculate the fps.
    private int lives, score;
    //-----instances declarations-----//
    private Canvas canvas;  //screen.
    private SurfaceHolder holder;
    private Paint paint;
    private Random rand = new Random();
    private Thread game_thread;  //the main thread of the game.
    private Background bg;
    private Player player;
    private Explosion explosion;
    private ArrayList<Enemy> enemies; //preferred on vector because ArrayList in non-synchronized.
    private ArrayList<Coin> coins;

    //ctor.
    public GamePanel(Context context, int w, int h) {
        super(context);
        //-----variables  initialize-----//
        this.enemies_start_time = System.nanoTime();
        this.coins_start_time = System.nanoTime();
        this.lives = 3;
        this.score = 0;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        WIDTH = metrics.widthPixels;
        HEIGHT = metrics.heightPixels;
        //-----instances initialize-----//
            this.canvas = null;
            this.holder = getHolder();
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background));
        this.player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.player), 350, 196, 8);
        this.enemies = new ArrayList<Enemy>();
        this.coins = new ArrayList<Coin>();
    }

    //=====================================================================================
    //game thread. implement of runnable.
    public void run() {
        long start_time;
        long time_millis;
        long wait_time;
        long total_time = 0;
        int frame_count = 0;
        long target_time = SECOND / FPS;  //how long the game loop run.

        while (playing) {
            start_time = System.nanoTime();
            //try locking the canvas for pixel editing.
            try {
                // Lock the canvas ready to draw
                canvas = holder.lockCanvas();
                //one thread at a time use the surface holder.
                synchronized (holder) {
                    update();
                    draw();
                }

            } catch (Exception e) {
            }
            //how long it tooks to update and draw the game(devides by 1 milion milliseconds).
            time_millis = (System.nanoTime() - start_time) / SECOND ^ 2;
            //how long we wait between game loops.
            wait_time = target_time - time_millis;

            try {
                game_thread.sleep(wait_time);
            } catch (Exception e) {
            } finally {
                if (canvas != null)
                    try {
                        holder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

            total_time = System.nanoTime() - start_time;
            frame_count++;
            //every decided FPS.
            if (frame_count == FPS) {
                averageFPS = SECOND / ((total_time / frame_count) / SECOND ^ 2);
                frame_count = 0;
                target_time = 0;
                System.out.println(averageFPS);//checking.
            }
        }
    }

    //=====================================================================================
    //game update on run time.
    public void update() {
        if (player.isPlaying()) {
            bg.update();
            player.update();

            //the elapsed time from last enemy start until now in nanoseconds.
            long enemies_elapsed_time = (System.nanoTime() - enemies_start_time) / 1000000;
            long coins_elapsed_time = (System.nanoTime() - coins_start_time) / 1000000;

            //--------------------player----------------------------
            if(Player.crashed)
            {
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(),
                        R.drawable.explosion), player.getX(), player.getY() - 30, 100, 100, 25);
                explosion.update();
                lives = 0;
            }

            //--------------------enemies---------------------------
            //change how often the enemies reappear on screen.
            if (enemies_elapsed_time > (2000 - player.getDifficult() / 4))
                addEnemy();

            //update enemies and check for collision.
            for (int i = 0; i < enemies.size(); i++) {
                enemies.get(i).update();

                //if there is an intersect with an Enemy, create explosion and remove the enemy.
                if (RectF.intersects(enemies.get(i).getRect(), player.getRect())) {
                    explosion = new Explosion(BitmapFactory.decodeResource(getResources(),
                            R.drawable.explosion), enemies.get(i).getX(), enemies.get(i).getY() - 30, 100, 100, 25);
                    enemies.remove(i);
                    //three attempts to play.
                    collision = true; //for checking in 'update' and 'draw' that explotion is not null.
                    lives--;

                }
                if (collision)
                    explosion.update();

                if(lives == 0)
                {
                    player.setPlaying(false);
                    newGame();
                }

                if (enemies.get(i).getX() < -1000)
                    enemies.remove(i);
            }

            //--------------------coins----------------------------
            if (coins_elapsed_time > 1000)
                addCoin();
            for(int i =0; i<coins.size(); i++)
            {
                coins.get(i).update();

                if(RectF.intersects(coins.get(i).getRect(), player.getRect()))
                {
                    coins.remove(i);
                    score++;
                }

                if(coins.get(i).getX() < -1000)
                    coins.remove(i);
            }
        }
    }

    //=====================================================================================
    //helping method to add enemies to enemies Vector.
    private void addEnemy() {
        //first missile always goes down the middle
        if (enemies.isEmpty())
            enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(), R.drawable.bat), WIDTH + 10,
                    HEIGHT / 2, 290, 163, player.getDifficult(), 8));

        //add enemies randomly(max 15 on the screen).
        else if(enemies.size() < 15)
            enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(), R.drawable.bat), WIDTH + 10,
                    (int) ((rand.nextDouble() * ((HEIGHT)) + 15)), 290, 163, player.getDifficult(), 8));

        //zero the enemies start time.
        enemies_start_time = System.nanoTime();

    }

    //=====================================================================================
    //helping method to add coins to coins Vector.
    private void addCoin()
    {
        //add coins randomly(max 20 on the screen).
        if(coins.size() < 20)
            coins.add(new Coin(BitmapFactory.decodeResource(getResources(), R.drawable.coin), WIDTH + 10,
                    (int) ((rand.nextDouble() * ((HEIGHT)) + 15)), 100, 100, 30));
        //zero the coins start time.
        coins_start_time = System.nanoTime();
    }

    //=====================================================================================
    //helping method to start a new game.
    private void newGame()
    {
        //clear all the game objects except the player.
        enemies.clear();
        coins.clear();
        //reset player variables, score and lives.
        player.setY(HEIGHT/4);
        player.setDifficult(0);
        player.resetYAcc(); //reset acceleration.
        Player.crashed = false;
        score = 0;
        lives = 3;

    }

    //=====================================================================================
    //drawing the elements.
    public void draw() {
        super.draw(canvas);
        // Make sure our drawing surface is valid or we crash
        if (holder.getSurface().isValid()) {
            if (canvas != null) {
                bg.draw(canvas);    //draw background.
                player.draw(canvas);    //draw player.
                for (Enemy e : enemies)  //draw enemies from enemies ArrayList.
                    e.draw(canvas);
                for(Coin c : coins)     //draw coins from coins ArrayList.
                    c.draw(canvas);
                if (collision)  //draw collisions if happened.
                    explosion.draw(canvas);

                // Draw the HUD
                paint.setColor(Color.WHITE);
                paint.setTextSize(60);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                canvas.drawText("LIVES: " + lives + "    " + "SCORE: " + score,10,50,paint);
            }
        }
    }

    //=====================================================================================
    //resume implement of the activity 'onResume'.
    public void resume() {
        //start 'run' and start the game thread.
        this.game_thread = new Thread(this);
        playing = true;
        game_thread.start();
    }

    //=====================================================================================
    //pause implement of the activity 'onPause'.
    public void pause() {
        //stop 'run' and kill the thread.
        playing = false;
        try {
            game_thread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    //=====================================================================================
    //touch listener implement.
    public boolean onTouchEvent(MotionEvent event) {
        //if the screen is pushed.
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!player.isPlaying())
                player.setPlaying(true);
            player.setUp(true);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //player.setPlaying(false);
            player.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }
}

