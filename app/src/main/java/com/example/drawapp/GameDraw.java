package com.example.drawapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class GameDraw extends SurfaceView implements SurfaceHolder.Callback{
    private boolean createdF=false;
    private Sprite playerBird;
    private Sprite enemyBird;
    private Sprite bonusBird;
    private Timer t=new Timer();
    private DrawThread thread=new DrawThread(getHolder());
    private int viewWidth;
    private int viewHeight;
    private int levelNumber=1;
    private int points = 0;
    private boolean isLastLvl=false;

    private final int timerInterval = 30;

    public GameDraw(Context context) {
        super(context);
        getHolder().addCallback(this);

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        int w = b.getWidth()/5;
        int h = b.getHeight()/3;
        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 100, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i ==0 && j == 0) {
                    continue;
                }
                if (i ==2 && j == 3) {
                    continue;
                }
                playerBird.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
            }
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        w = b.getWidth()/5;
        h = b.getHeight()/3;
        firstFrame = new Rect(4*w, 0, 5*w, h);

        enemyBird = new Sprite(2000, 250, -300, 0, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {

                if (i ==0 && j == 4) {
                    continue;
                }

                if (i ==2 && j == 0) {
                    continue;
                }

                enemyBird.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
            }
        }
//        timerControl();
    }
    public GameDraw (Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        int w = b.getWidth() / 5;
        int h = b.getHeight() / 3;
        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 100, firstFrame, b);
//добавление кадров для нашей птицы
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (i == 2 && j == 3) {
                    continue;
                }
                playerBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        w = b.getWidth() / 5;
        h = b.getHeight() / 3;
        firstFrame = new Rect(4 * w, 0, 5 * w, h);

        enemyBird = new Sprite(2000, 250, -300, 0, firstFrame, b);
//добавление кадров для нашей и вражеской птички
        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {

                if (i == 0 && j == 4) {
                    continue;
                }

                if (i == 2 && j == 0) {
                    continue;
                }
                enemyBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }
      Bitmap a = BitmapFactory.decodeResource(getResources(), R.drawable.bonus_enemy);
        bonusBird=new Sprite(2000,250,-300,0,firstFrame,a);
        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {

                if (i == 0 && j == 4) {
                    continue;
                }

                if (i == 2 && j == 0) {
                    continue;
                }
                bonusBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }
        //ненужная фигня, которую надо исправить
        if (!createdF) {
            createdF=true;
            timerControl(false);
            Log.d("TIMER_LOG","timer is on in constructor");
        }
    }
    //попытка добавления включения и остановки треда(исправить)
    public void enableThread(){
        thread.start();
    }
    public void disableThread(){
        thread.requestStop();
        thread.interrupt();
        thread=null;
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        enableThread();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
       thread.requestStop();
        boolean retry=true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                //
            }
        }
    }


    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

//метод апдейта кадров
    protected void update () {
        playerBird.update(timerInterval);
        enemyBird.update(timerInterval);
        bonusBird.update(timerInterval);
        //проверка на последний уровень(можно переделать по проверке на очки, а не по уровню), ускорение вражеских птицы
        if(isLastLvl){
//            enemyBirdLastLvl.setVx(-500);
        }
        //если наша птица задевает края экрана, то минус очки
        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVy(-playerBird.getVy());
            points--;
        }
        else if (playerBird.getY() < 0) {
            playerBird.setY(0);
            playerBird.setVy(-playerBird.getVy());
            points--;
        }

        if (enemyBird.getX() < - enemyBird.getFrameWidth()) {
            teleportEnemy(enemyBird);
            points +=10;
        }
    //проверка касания вражеской птицы
        if (enemyBird.intersect(playerBird)) {
            teleportEnemy(enemyBird);
            points -= 40;
        }
        //проверка на столкновение с птицей бонусной(если не успели тапнуть)
        if(bonusBird.intersect(playerBird)){
            points=points+20;
            teleportEnemy(bonusBird);
        }
        //переход на новый лвл
        if(points>40&&levelNumber==1){
            levelNumber++;
            points=0;
            isLastLvl=true;
        }


    }
//действия по касанию
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN)  {
            //перемещение птицы вниз
            if (event.getY() < playerBird.getBoundingBoxRect().top) {
                playerBird.setVy(-100);
//                Toast.makeText(getContext(), event.getX()+"x and bird x"+enemyBird.getX()+"frw"+enemyBird.getFrameWidth(), Toast.LENGTH_SHORT).show();
            }
            //перемещение птицы вверх
            else if (event.getY() > (playerBird.getBoundingBoxRect().bottom)) {
                playerBird.setVy(100);
//                Toast.makeText(getContext(), event.getX()+"x and bird botx"+enemyBird.getX()+"frw"+enemyBird.getFrameWidth(), Toast.LENGTH_SHORT).show();
            }
            //убийство птичек по нажатию
           if(event.getX()>bonusBird.getX()&&event.getY()>bonusBird.getY()
                    &&event.getX()<bonusBird.getFrameWidth()+bonusBird.getX()
                    &&event.getY()<bonusBird.getFrameHeight()+bonusBird.getY()){
                Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
            }

        }
        return true;
    }

    //перемещение вражеской птицы в разные точки, чтоб не вылетала из одной
    private void teleportEnemy (Sprite bird) {
        bird.setX(viewWidth + Math.random() * 500);
        bird.setY(Math.random() * (viewHeight - enemyBird.getFrameHeight()));
    }

    public void timerControl(boolean pauseFlag){
        Timer timer = new Timer();
        if(pauseFlag) {
            Log.d("TIMER_LOG","timer is off");
            timer.cancel();
            timer=null;
        }else{
            Log.d("TIMER_LOG","timer is started");
            timer.start();
        }
    }

//оббычный таймер обратного отсчета
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
    //рисование поля птиц и прочего
    private void drawingBirds(Canvas canvas){
        canvas.drawARGB(250, 127, 199, 255);
        playerBird.draw(canvas);
        enemyBird.draw(canvas);
        bonusBird.draw(canvas);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(55.0f);
        p.setColor(Color.WHITE);
        canvas.drawText("Score: "+points, viewWidth - 300, 70, p);
        canvas.drawText("Lvl: " + levelNumber, viewWidth -500, 70, p);
    }
    //удаление тапнутой птицы
    private void deleteTappedBird(Canvas canvas){

    }
    //класс унаследованный от потока
    protected class DrawThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private Canvas canvas;
        private volatile boolean running=true;
        public DrawThread(SurfaceHolder surfaceHolder){
            this.surfaceHolder=surfaceHolder;
        }
        public Canvas returnCanvas(){
            return canvas;
        }
        public void requestStop(){
            running=false;
        }
        @Override
        public void run() {
            while(running){
               canvas = surfaceHolder.lockCanvas();
                if(canvas!=null){
                    try {
                   drawingBirds(canvas);

                    }finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

}

