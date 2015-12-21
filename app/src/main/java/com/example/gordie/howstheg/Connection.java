package com.example.gordie.howstheg;

import android.os.AsyncTask;
import android.widget.RatingBar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by gordie on 08/12/15.
 * Used to receive and send information to server - at time of writing not yet implemented.
 */
public class Connection {
    Socket client;
    DataInputStream din;
    DataOutputStream dout;
    private float gotRating;
    String host;
    int port;
    /**
     * Creates connection to host
     * @param host IP of host to connect to
     * @param port Port of host to connect to
     */
    public Connection(String host, int port){
        this.host = host;
        this.port = port;
        gotRating = 0;
        try {
            gotRating = new fetchRating().execute(host, "" + port).get();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Sends score to be added to total
     * @param score Number of stars (out of five) to be sent
     */
    public void sendRating(int score){
        if(score < 0 || score > 5){
            throw new IllegalArgumentException("Score must be 0 < x < 5!");
        }
    }

    /**
     * Gets the average score from server
     * @return Score everybody else voted on
     */
    public float getRating(){
        return gotRating;
    }
    public void setRating(RatingBar bar) {
        try{
            bar.setRating(new fetchRating().execute().get());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Gets number of people who have voted
     * @return Number of people who have voted
     */
    public int getNumVotes(){

        return 10;
    }


    public class fetchRating extends AsyncTask<String, Void, Float> {
        protected Float doInBackground(String... params){
            System.out.println("Running in background");
            float returning = 1;
            try{
                client = new Socket(host, port);
                dout = new DataOutputStream(client.getOutputStream());
                din = new DataInputStream(client.getInputStream());
                dout.writeInt(0);
                byte[] recv = new byte[2];
                din.read(recv);
                returning = recv[0];
                gotRating = (int) returning;
            }catch(IOException e){
                e.printStackTrace();
            }
            return returning;
        }
    }
}
