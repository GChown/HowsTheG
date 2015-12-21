package com.example.gordie.howstheg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        final String HOST = "192.168.1.127";
        final int PORT = 8000;
        otherRating = (RatingBar) findViewById(R.id.otherRatingsBar);
        Connection connection = new Connection(HOST, PORT);
        otherRating.setRating(connection.getRating());

        //TODO: send userRating to server
        RatingBar userRating = (RatingBar) findViewById(R.id.ratingBar);
        //Sets meal text
        DateFormat dateFormat = new SimpleDateFormat("HHmm");
        Calendar cal = Calendar.getInstance();
        int time = Integer.parseInt(dateFormat.format(cal.getTime()));
        //time = 2000; //testing code, remove in release
        String meal = getMeal(time);

        String entering = "It's ";
        if(time < 8000){
            entering = "Caf opens at 8 tomorrow.";
        }
        else if(time > 8000 && time < 1100){
            entering += meal;
        }
        else if(time > 1100 && time < 2200){
            entering += meal;
            if(time > 1800){
                entering += " (closing at 10!)";
            }
        }
        ((TextView) findViewById(R.id.mealDescrip)).setText(entering);
    }
    /**
     * Returns breakfast (hour of day < 1000 (10AM)),
     * lunch (1000 < hour of day < 1500 (3PM)), or dinner (1500 < hour of day)
     */
    //TODO: say time until open or close (~45min)
    String getMeal(int time){
        if(time < 10)
            return "breakfast";
        if(time > 10 && time < 4)
            return "lunch";
        if(time > 4)
            return "dinner";
        else return " ...I'm not actually sure!";

    }
}

