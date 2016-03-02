package com.gordiechown.howstheg;

import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by gordie on 06/02/16.
 */
public class VoteFragment extends Fragment{
    private String meal;
    RatingBar otherRating;
    RatingBar userRating;
    TextView mealDescrip;
    TextView ratingSummary;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.vote_fragment, container, false);
        otherRating = (RatingBar) view.findViewById(R.id.otherRatingsBar);
        userRating = (RatingBar) view.findViewById(R.id.myRating);
        mealDescrip = (TextView) view.findViewById(R.id.mealDescrip);
        ratingSummary = (TextView) view.findViewById(R.id.ratingSummary);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            otherRating.setRating(savedInstanceState.getFloat("otherRating"));
        }

        //Send rating on change
        userRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ((MainActivity) getActivity()).getConnection().sendRating((int) rating);
            }
        });

        meal = getMealTime();
        updateUIThread();
        mealDescrip.setText(meal);

        return view;
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

    /**
     * Every three seconds update the otherRating bar and
     * numVotes labels from server
     */
    private void updateUIThread() {
        ratingSummary.setText(getString(R.string.fetching));
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                boolean isConnected = ((MainActivity) getActivity()).getConnection().isConnected();
                System.out.println("Running UI thread, connected: " + isConnected);
                if (isConnected) {
                    setRating(otherRating, ((MainActivity) getActivity()).getConnection().getRating());
                    int numVotes = ((MainActivity)getActivity()).getConnection().getNumVotes();
                    ratingSummary.setText(
                            getResources().getQuantityString(R.plurals.othervote, numVotes, numVotes) + getMealName());
                } else {
                    setRating(otherRating, 0);
                    ratingSummary.setText(getString(R.string.noconnection));
                }
                h.postDelayed(this, 3000);
            }
        }, 3000);
    }

    /**
     * Asks how the meal is - 'How's breakfast/lunch/dinner', or
     * says closing time if it's after 8:00PM
     * @return String asking how the meal is
     */
    String getMealTime() {
        DateFormat dateFormat = new SimpleDateFormat("HHmm");
        Calendar cal = Calendar.getInstance();
        int time = Integer.parseInt(dateFormat.format(cal.getTime()));
        //time = 900; // testing code, remove in release
        int hour = time / 100;
        if (hour < 8 || hour >= 22) {
            return getString(R.string.notmealtime);
        } else if (hour >= 8 && hour < 11) {
            return getString(R.string.howsbreakfast);
        } else if(hour >= 11 && hour < 16){
            return getString(R.string.howslunch);
        } else if (hour >= 16 && hour <= 22) {
            if (hour >= 18) {
                return getString(R.string.howsdin);
            }
            return getString(R.string.howsdin);
        }
        return null;
    }
    /**
     * Returns lowercase string value of meal.
     * @return meal name
     */
    String getMealName(){
        DateFormat dateFormat = new SimpleDateFormat("HHmm");
        Calendar cal = Calendar.getInstance();
        int time = Integer.parseInt(dateFormat.format(cal.getTime()));
        //time = 900; // testing code, remove in release
        int hour = time / 100;
        if (hour >= 8 && hour < 11) {
            return getString(R.string.breakfast);
        } else if(hour >= 11 && hour < 16){
            return getString(R.string.lunch);
        } else if (hour >= 16 && hour <= 24) {
            return getString(R.string.dinner);
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putFloat("otherRating", otherRating.getRating());
        super.onSaveInstanceState(savedInstanceState);
    }
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        setRating(otherRating, savedInstanceState.getFloat("otherRating"));
    }
}
