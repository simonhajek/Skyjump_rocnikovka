package com.simon.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Death extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_death);

        String ted = String.valueOf(getIntent().getExtras().getInt("ted"));
        String rekord = String.valueOf(getIntent().getExtras().getInt("rekord"));

        TextView score = findViewById(R.id.score);
        Button again = findViewById(R.id.playagain);

        score.setText("Nejlepší skóre:" + rekord + "\nSkóre:" + ted);

        //restart hry po stisknutí tlačítka
        again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Death.this, HraActivity.class);
                startActivity(myIntent);
            }
        });
    }
}