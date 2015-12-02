package com.example.gordie.howstheg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    RatingBar otherRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        otherRating = (RatingBar) findViewById(R.id.otherRatingsBar);
        setScore();
        RatingBar userRating = (RatingBar) findViewById(R.id.ratingBar);
        String meal = getMeal();
        ((TextView) findViewById(R.id.mealDescrip)).setText("It's " + meal + ".");
        ((TextView)findViewById(R.id.ratingSummary)).setText("10 other people have given " + meal);
        int score = (int) userRating.getRating();

    }
    //TODO: insert code to get rating from server
    void setScore(){
        otherRating.setRating(1f);
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
