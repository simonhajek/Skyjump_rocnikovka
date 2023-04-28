package com.simon.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button hratbtn = (Button) findViewById(R.id.hratbutton);
        hratbtn.setOnClickListener(new View.OnClickListener() {

            //zapnutí aktivity po stisknutí tlačítka
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(StartActivity.this, HraActivity.class);
                startActivity(myIntent);
            }
        });

        Button reset = (Button) findViewById(R.id.resetskore);
        reset.setOnClickListener(new View.OnClickListener() {

            //reset highscore po stisknuti tlacitka
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("key", 0);
                editor.commit();
            }
        });


    }


}