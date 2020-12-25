package com.example.drawapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    private final int timerInterval = 1;
    private int score=0;
    private Sprite playerBird;
    private Sprite enemyBird;
    private int viewHeight;
    private int viewWidth;
    public GameView(Context context) {
        super(context);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.player);
        int w= bitmap.getWidth()/5;
        int h=bitmap.getHeight()/3;
        Rect firstFrame = new Rect(0,0,w,h);
        playerBird=new Sprite(10,0,0,50,firstFrame,bitmap);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j <= 4; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (i == 2 && j == 3) {
                    continue;
                }
                playerBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.enemy);
            w=bitmap.getWidth()/5;
            h=bitmap.getHeight()/3;
            Rect secondFr = new Rect(4*w, 0, 5*w, h);

            enemyBird = new Sprite(2000, 250, -50, 0, secondFr, bitmap);

            for (int k = 0; k < 3; k++) {
                for (int j = 4; j >= 0; j--) {
                    if (k ==0 && j == 4) {
                        continue;
                    }
                    if (k ==2 && j == 1) {
                        continue;
                    }
                    enemyBird.addFrame(new Rect(j*w, k*h, j*w+w, k*w+w));
                }
            }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight=h;
        viewWidth=w;
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint paint=new Paint();
        //настроим цвет заднего фона
        canvas.drawColor(getResources().getColor(R.color.background_color));
        //добавим количество очков на экран
        paint.setTextSize(50.0f);
        paint.setColor(Color.WHITE);
        paint.setSubpixelText(true); // Субпиксельное сглаживание для текста
        paint.setAntiAlias(true);//сглаживание линий
        canvas.drawText("Score: "+score, viewWidth-450,80,paint);
        playerBird.draw(canvas);
        enemyBird.draw(canvas);
        Timer t=new Timer();
        t.start();
    }
    protected void update(){
        playerBird.update(timerInterval);
        enemyBird.update(timerInterval);
        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVy(-playerBird.getVy());
            score--;
        }
        else if (playerBird.getY() < 0) {
            playerBird.setY(0);
            playerBird.setVy(-playerBird.getVy());
            score--;
        }
        if (enemyBird.getX() < - enemyBird.getFrameWidth()) {
            teleportEnemy();
            score +=10;
        }

        if (enemyBird.intersect(playerBird)) {
            teleportEnemy ();
            score -= 40;
        }
        invalidate();
    }
    class Timer extends CountDownTimer {
        public Timer() {
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }

        @Override
        public void onFinish() {
        }
    }
    private void teleportEnemy(){
        enemyBird.setX(viewWidth + Math.random() * 500);
        enemyBird.setY(Math.random() * (viewHeight - enemyBird.getFrameHeight()));
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        int eventAction=event.getAction();
        if(eventAction==MotionEvent.ACTION_DOWN){
            if(event.getY()<playerBird.getBoundingBoxRect().top){
                playerBird.setVy(-100);
                score--;
            }else if(event.getY()>playerBird.getBoundingBoxRect().bottom){
                playerBird.setVy(100);
                score--;
            }
        }
        return true;
    }

}
