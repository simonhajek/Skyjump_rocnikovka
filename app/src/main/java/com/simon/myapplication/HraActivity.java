package com.simon.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class HraActivity extends AppCompatActivity {
    //zalozeni promennych
    int highestTouched = 0; // aktualni nejvysse dosazeny ostrov
    int getrekord; // pomocna promenna pro porovnani nejvyssich bodu
    int pomocna1 = 0; // pomocna promenna
    double gravity = 150.0; // gravitace
    double timeStep = 0.1 / 3; // time steps za sekundu
    double speedY = 0.0; // počáteční rychlost
    double speedX = 35; // rychlost pohybu do stran
    boolean doleva;
    boolean doprava;

    ConstraintLayout parentLayout;
    ImageView imageView2;
    ImageView imageView1;

    //souradnice ostrovu a postavy pro zjisteni kolize
    int[] location1 = new int[2];
    int[] location2 = new int[2];

    Handler handler = new Handler();

    //AraryList pro 5 ostrovu soucasne
    ArrayList<View> ostrovy = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hra);

        //priprava ukladani highscore
        SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        getrekord = sp.getInt("key", 0);

        //reference z .xml souboru
        TextView tv1 = (TextView) findViewById(R.id.counter);
        ImageView character2 = (ImageView) findViewById(R.id.character2);
        parentLayout = findViewById(R.id.Constraintlayout2);
        imageView1 = findViewById(R.id.character2);
        imageView2 = findViewById(R.id.island2);

        //pridani pocatecniho ostrovu do Arraylistu a prideleni tagu
        ostrovy.add(imageView2);
        ostrovy.get(0).setTag(0);

        ViewTreeObserver vto = parentLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            //generovani ostrovu
            @Override
            public void onGlobalLayout() {
                for (int i = 0; i < 5; i++) {
                    generuj(false);
                }
                //zakaz spawnu druheho ostrova primo nad prvnim
                while (ostrovy.get(1).getX() < ostrovy.get(0).getX() + imageView2.getWidth() && ostrovy.get(1).getX() > ostrovy.get(0).getX() - imageView2.getWidth()) {
                    ostrovy.get(1).setX((float) (Math.random() * (parentLayout.getWidth() - imageView2.getWidth())));
                }
                ViewTreeObserver obs = parentLayout.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }
        });

        //loop opakujici se 30x za sekundu
        Runnable gameLoop = new Runnable() {
            @Override
            public void run() {
                if (highestTouched > 0 && highestTouched % 10 == 0 && highestTouched != pomocna1) {
                    timeStep = timeStep * 3;
                    gravity = gravity / 3;
                    pomocna1 = highestTouched;
                    //pokus o postupne zrychlovani hry (nedodelano)
                }
                boolean generovat = false;
                //detekce smrti
                if (character2.getY() > parentLayout.getHeight()) {
                    System.out.println("zemreli jste");
                    //ulozeni nejlepsiho skore
                    if (highestTouched > getrekord) {
                        getrekord = highestTouched;
                        editor.putInt("key", highestTouched);
                        editor.commit();
                    }
                    //predani hodnot do tridy Death
                    Intent intent = new Intent(HraActivity.this, Death.class);
                    intent.putExtra("ted", highestTouched);
                    intent.putExtra("rekord", getrekord);
                    System.out.println("rekord: " + getrekord + " current: " + highestTouched);

                    startActivity(intent);
                    finish();
                    return;
                }
                //pokud postava pada dolu, kontroluj kolize
                if (speedY > 0) {
                    imageView1.getLocationOnScreen(location1);
                    for (View v :
                            ostrovy) {
                        v.getLocationOnScreen(location2);

                        int left1 = location1[0];
                        int top1 = location1[1];
                        int right1 = left1 + imageView1.getWidth();
                        int bottom1 = top1 + imageView1.getHeight();

                        int left2 = location2[0];
                        int top2 = location2[1];
                        int right2 = left2 + v.getWidth();
                        int bottom2 = top2 + v.getHeight();
                        //pokud postava pristane na ostrov (uprava pixelu kvuli okrajum obrazku), znovu vyskoc a pricti skore pokud postava jeste na ostrove nebyla
                        if (right1 > left2 + 50 && left1 < right2 - 50 && bottom1 - 150 > top2 && top1 < bottom2) {
                            speedY = -100;
                            if ((int) v.getTag() > highestTouched) {
                                highestTouched = (int) v.getTag();
                                generovat = true;
                            } //zmena textu na aktualni skore
                            tv1.setText(String.valueOf(highestTouched));
                        }
                    }
                } // pocitani fyziky postavy
                speedY += gravity * timeStep;
                character2.setY((float) (character2.getY() + speedY));
                handler.postDelayed(this, 100 / 3);

                //ovladani do stran
                if (doleva == true && character2.getX() > 0) {
                    character2.setX((float) (character2.getX() - speedX / 2));
                }
                if (doprava == true && character2.getX() + character2.getWidth() < parentLayout.getWidth()) {
                    character2.setX((float) (character2.getX() + speedX / 2));
                }
                if (character2.getY() < parentLayout.getHeight() / 3) {
                    //posun vsech ostrovu dolu
                    float pomocna2 = (parentLayout.getHeight() / 3) - character2.getY();
                    character2.setY(character2.getY() + pomocna2);
                    for (View b :
                            ostrovy) {
                        b.setY(b.getY() + pomocna2);
                    }
                }
                if (generovat) {
                    generuj(true);
                }
            }
        };
        //prvotni spusteni gameLoop
        handler.postDelayed(gameLoop, 100 / 3);

        //rozpoznani dotyku na parentLayout pro pohyb do stran
        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Rozpoznani toho, jaka polovina obrazovky byla stisknuta
                        float x = event.getX();
                        int width = view.getWidth();

                        if (x < width / 2) {
                            doleva = true;
                        } else {
                            doprava = true;
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        doleva = false;
                        doprava = false;
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    //generovani ostrovu na nahodnou horizontalni souradnici s vertikalnim odstupem 700
    public void generuj(boolean maz) {
        View view = LayoutInflater.from(HraActivity.this).inflate(R.layout.ostrov, null);
        parentLayout.addView(view);

        view.setTag(Integer.parseInt(ostrovy.get(ostrovy.size() - 1).getTag().toString()) + 1);
        view.setX((float) (Math.random() * (parentLayout.getWidth() - imageView2.getWidth())));
        view.setY(ostrovy.get(ostrovy.size() - 1).getY() - 700);
        view.setLayoutParams(new ViewGroup.LayoutParams(imageView2.getWidth(), imageView2.getHeight()));
        ostrovy.add(view);
        //mazani ostrovu
        if (maz) {
            ostrovy.get(0).setVisibility(View.INVISIBLE); //zmizeni ostrova
            ostrovy.remove(0); //odstraneni z ArrayListu ostrovu
        }
    }
}