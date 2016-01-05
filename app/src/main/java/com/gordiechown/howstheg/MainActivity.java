package com.gordiechown.howstheg;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {
    private Connection connection;
    private String meal;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String HOST = "192.168.1.127";
        final int PORT = 8000;
        String android_id = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        connection = new Connection(HOST, PORT, android_id);

        RatingBar otherRating = (RatingBar) findViewById(R.id.otherRatingsBar);
        RatingBar userRating = (RatingBar) findViewById(R.id.ratingBar);

        setLabels();
        updateUIThread();

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            otherRating.setRating(savedInstanceState.getFloat("otherRating"));
            userRating.setRating(savedInstanceState.getFloat("myRating"));
        }

        LayerDrawable stars = (LayerDrawable) otherRating.getProgressDrawable();
        stars.getDrawable(1).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        stars = (LayerDrawable) userRating.getProgressDrawable();
        stars.getDrawable(1).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

        //Send rating on change
        userRating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                connection.sendRating((int) rating);
            }
        });
        mAdView = (AdView) findViewById(R.id.adView);
        //ADD TESTING DEVICE OR FACE THE CONSEQUENCES
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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
        setRating(((RatingBar) findViewById(R.id.ratingBar)), savedInstanceState.getFloat("userRating"));
        setRating(((RatingBar) findViewById(R.id.otherRatingsBar)), savedInstanceState.getFloat("otherRating"));
    }

    /**
     * Sets bar's rating
     * @param bar RatingBar to be changed
     * @param rating rating to set the bar
     */
    private void setRating(RatingBar bar, float rating){
        ObjectAnimator anim = ObjectAnimator.ofFloat(bar, "rating", rating);
        anim.setDuration(1200);
        anim.start();
    }

    /*
    * Every second (1000ms) update the otherRating bar and
    * numVotes from server
     */
    private void updateUIThread(){
        if(connection.isConnected()){
            final Handler h = new Handler();
            h.postDelayed(new Runnable() {
                public void run() {
                    if(connection.isConnected()) {
                        setRating(((RatingBar) findViewById(R.id.otherRatingsBar)), connection.getRating());
                        ((TextView) findViewById(R.id.ratingSummary)).setText(connection.getNumVotes() + " people have given " + meal);
                        h.postDelayed(this, 3000);
                    }
                }
            }, 3000);
        }else{
            setRating(((RatingBar) findViewById(R.id.otherRatingsBar)), 0);
            ((TextView) findViewById(R.id.ratingSummary)).setText("Couldn't connect to server.");
        }
    }

    //Very broken at the moment.
    private void setLabels(){
        //Sets meal text
        DateFormat dateFormat = new SimpleDateFormat("HHmm");
        Calendar cal = Calendar.getInstance();
        int time = Integer.parseInt(dateFormat.format(cal.getTime()));
        //time = 900; // testing code, remove in release
        int hour = time / 100;
        meal = getMeal(hour);
        String entering = "It's ";
        if(hour < 8 || hour >= 22){
            entering = "Caf opens at 8 AM.";
        }
        else if(hour >= 8 && hour < 11){
            entering += meal;
        }
        else if(hour >= 11 && hour < 22){
            entering += meal;
            if(hour >= 18){
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
        if(time > 10 && time < 14)
            return "lunch";
        if(time > 14)
            return "dinner";
        else return "ERROR TIME!";
    }
}