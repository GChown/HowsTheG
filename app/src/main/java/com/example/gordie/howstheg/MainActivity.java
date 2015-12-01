package com.example.gordie.howstheg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String meal = getMeal();
        //Sets text to getMeal
        //getActionBar().setTitle("It's " + meal + ".");
        ((TextView)findViewById(R.id.mealNamer)).setText("It's " + meal + ".");
        ((TextView)findViewById(R.id.ratingSummary)).setText("10 other people have given " + meal);
        //Update little tracker beside stars
        ((RatingBar)findViewById(R.id.ratingBar)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((TextView)findViewById(R.id.rateFraction)).setText("(" + (int)((RatingBar) findViewById(R.id.ratingBar)).getRating() + "/5)");
                return false;
            }
        });
    }

    /**
     * Returns breakfast (hour of day < 10),
     * lunch (10 < hour of day < 3), or dinner (3 < hour of day)
     */
    String getMeal(){
        DateFormat dateFormat = new SimpleDateFormat("HH");
        Calendar cal = Calendar.getInstance();
        int time = Integer.parseInt(dateFormat.format(cal.getTime()));
        if(time < 10)
            return "breakfast";
        if(time > 10 && time < 3)
            return "lunch";
        if(time > 3)
            return "dinner";
        else return " ...I'm not actually sure!";
    }
}
