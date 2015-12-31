package com.example.gordie.howstheg;

import android.animation.ObjectAnimator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final String HOST = "192.168.1.127";
        final int PORT = 8000;
        String android_id = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        final Connection connection = new Connection(HOST, PORT, android_id);

        RatingBar otherRating = (RatingBar) findViewById(R.id.otherRatingsBar);
        RatingBar userRating = (RatingBar) findViewById(R.id.ratingBar);


        if (savedInstanceState != null) {
            // Restore value of members from saved state
            otherRating.setRating(savedInstanceState.getFloat("otherRating"));
            userRating.setRating(savedInstanceState.getFloat("myRating"));
        } else {
            setRating(otherRating, connection);
        }


        //Send rating on change
        userRating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                connection.sendRating((int) rating);
            }
        });

        setLabels();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putFloat("otherRating", ((RatingBar) findViewById(R.id.otherRatingsBar)).getRating());
        savedInstanceState.putFloat("userRating", ((RatingBar) findViewById(R.id.ratingBar)).getRating());
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        setRating(((RatingBar)findViewById(R.id.ratingBar)), savedInstanceState.getFloat("userRating"));
        setRating(((RatingBar)findViewById(R.id.otherRatingsBar)), savedInstanceState.getFloat("otherRating"));
    }

    private void setRating(RatingBar bar, float rating){
        ObjectAnimator anim = ObjectAnimator.ofFloat(bar, "rating", rating);
        anim.setDuration(1200);
        anim.start();
    }
    private void setRating(RatingBar bar, Connection connection) {
        setRating(bar, connection.getRating());
    }

    //Very broken at the moment.
    private void setLabels(){
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

