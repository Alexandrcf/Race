package com.bignerdranch.android.race;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RaceGameActivity extends AppCompatActivity {

    private ImageView buttonLeft, buttonRight, hp1, hp2, hp3, car1;
    private int hp = 3;
    private ConstraintLayout constraintLayout;
    private Timer timerCarOpponent, timerLighting;
    private boolean checkLaunch = true;
    private ArrayList<ImageView> arrayListCarXY = new ArrayList<>();
    private ArrayList<ImageView> arrayListLightingXY = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_game);

        initView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (checkLaunch){
            timerAnimationsCarOpponent();
            timerAnimationsLighting();
            animationBackground();
            checkLaunch = false;
        }
    }

    private void initView() {
        buttonLeft = (ImageView) findViewById(R.id.button_left);
        buttonRight = (ImageView) findViewById(R.id.button_right);
        car1 = (ImageView) findViewById(R.id.car_1);
        hp1 = (ImageView) findViewById(R.id.hp_1);
        hp2 = (ImageView) findViewById(R.id.hp_2);
        hp3 = (ImageView) findViewById(R.id.hp_3);

        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (car1.getX() - car1.getWidth() / 2 >= 0) {
                    car1.animate().translationXBy(-50f).setDuration(85);
                }
            }
        });

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (car1.getX() <= constraintLayout.getWidth() - car1.getWidth()) {
                    car1.animate().translationXBy(50f).setDuration(85);
                }
            }
        });
    }


    private void animationBackground(){
        final ImageView background1 = (ImageView) findViewById(R.id.background_1);
        final ImageView background2 = (ImageView) findViewById(R.id.background_2);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float height = background1.getHeight();
                final float translationY = height * progress;
                background1.setTranslationY(translationY);
                background2.setTranslationY(translationY - height);
            }
        });
        animator.start();
    }

    private void animationBackgroundFaster(){
        final ImageView background1 = (ImageView) findViewById(R.id.background_1);
        final ImageView background2 = (ImageView) findViewById(R.id.background_2);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float height = background1.getHeight();
                final float translationY = height * progress;
                background1.setTranslationY(translationY);
                background2.setTranslationY(translationY - height);
            }
        });
        animator.start();
    }

    private void timerAnimationsCarOpponent() {
        timerCarOpponent = new Timer();
        timerCarOpponent.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView carOpponent = new ImageView(RaceGameActivity.this);
                        carOpponent.setImageResource(R.drawable.car_2);

                        Random random = new Random();
                        int x = random.nextInt(constraintLayout.getWidth());

                        arrayListCarXY.add(carOpponent);

                        carOpponent.setLayoutParams(new ConstraintLayout.LayoutParams(800, 400));
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) carOpponent.getLayoutParams();
                        layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        carOpponent.setLayoutParams(layoutParams);
                        carOpponent.setX(x);

                        carOpponent.setTranslationY(-400);

                        carOpponent.animate().setInterpolator(new LinearInterpolator()).
                                translationYBy(constraintLayout.getHeight() + 400).setDuration(5000).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float x1Car = car1.getX();
                                float x2Car = car1.getX() + car1.getWidth();
                                float y1Car = car1.getY();
                                float y2Car = car1.getY() + car1.getHeight();

                                for (int i = 0; i < arrayListCarXY.size(); i++) {
                                    ImageView currentOpponent = arrayListCarXY.get(i);

                                    float x1Opponent = currentOpponent.getX();
                                    float x2Opponent = currentOpponent.getX() + currentOpponent.getWidth();
                                    float y1Opponent = currentOpponent.getY();
                                    float y2Opponent = currentOpponent.getY() + currentOpponent.getHeight();

                                    if ((x1Car >= x1Opponent && x1Car <= x2Opponent || x2Car >= x1Opponent && x2Car <= x2Opponent) &&
                                            (y1Car >= y1Opponent && y1Car <= y2Opponent || y2Car >= y1Opponent && y2Car <= y2Opponent)) {
                                        hp--;
                                        checkHp();
                                        arrayListCarXY.remove(currentOpponent);
                                        constraintLayout.removeView(currentOpponent);
                                    }
                                }

                            }
                        });
                        constraintLayout.addView(carOpponent);
                    }
                });
            }
        }, 1000, 3000);
    }

    private void timerAnimationsLighting() {
        timerLighting = new Timer();
        timerLighting.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView viewLighting = new ImageView(RaceGameActivity.this);
                        viewLighting.setImageResource(R.drawable.lightning);

                        Random random = new Random();
                        int x = random.nextInt(constraintLayout.getWidth());

                        arrayListLightingXY.add(viewLighting);

                        viewLighting.setLayoutParams(new ConstraintLayout.LayoutParams(800, 400));
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) viewLighting.getLayoutParams();
                        layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        viewLighting.setLayoutParams(layoutParams);
                        viewLighting.setX(x);

                        viewLighting.setTranslationY(-400);

                        viewLighting.animate().setInterpolator(new LinearInterpolator()).
                                translationYBy(constraintLayout.getHeight() + 400).setDuration(5000).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float x1Car = car1.getX();
                                float x2Car = car1.getX() + car1.getWidth();
                                float y1Car = car1.getY();
                                float y2Car = car1.getY() + car1.getHeight();

                                for (int i = 0; i < arrayListLightingXY.size(); i++) {
                                    ImageView currentLighting = arrayListLightingXY.get(i);

                                    float x1Lighting = currentLighting.getX();
                                    float x2Lighting = currentLighting.getX() + currentLighting.getWidth();
                                    float y1Lighting = currentLighting.getY();
                                    float y2Lighting = currentLighting.getY() + currentLighting.getHeight();

                                    if ((x1Car >= x1Lighting && x1Car <= x2Lighting || x2Car >= x1Lighting && x2Car <= x2Lighting) &&
                                            (y1Car >= y1Lighting && y1Car <= y2Lighting || y2Car >= y1Lighting && y2Car <= y2Lighting)) {
                                        timer();
                                        arrayListLightingXY.remove(currentLighting);
                                        constraintLayout.removeView(currentLighting);
                                    }
                                }

                            }
                        });
                        constraintLayout.addView(viewLighting);
                    }
                });
            }
        }, 1000, 10000);
    }

    private void timer() {
        animationBackgroundFaster();
        CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millis) {
            }

            @Override
            public void onFinish() {
                animationBackground();
            }
        }.start();
    }

    private void checkHp(){
        switch (hp){
            case 0:
                Intent intent = new Intent(getApplication(), EndGameActivity.class);
                startActivity(intent);
                finish();
                break;
            case 1:
                hp1.setVisibility(View.VISIBLE);
                hp2.setVisibility(View.INVISIBLE);
                hp3.setVisibility(View.INVISIBLE);
                break;
            case 2:
                hp1.setVisibility(View.VISIBLE);
                hp2.setVisibility(View.VISIBLE);
                hp3.setVisibility(View.INVISIBLE);
                break;
        }
    }
}