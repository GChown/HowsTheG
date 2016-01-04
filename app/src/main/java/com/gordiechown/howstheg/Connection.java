package com.gordiechown.howstheg;

import android.os.AsyncTask;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by gordie on 08/12/15.
 * Used to receive and send information to server.
 */
public class Connection {
    private Socket client;
    private DataInputStream din;
    private DataOutputStream dout;
    private float gotRating;
    private String host;
    private String android_id;
    private int port;
    private boolean isConnected;
    private int numVotes;
    /**
     * Creates connection to host
     * @param host IP of host to connect to
     * @param port Port of host to connect to
     */
    public Connection(String host, int port, String android_id){
        this.host = host;
        this.port = port;
        this.android_id = android_id;
        isConnected = false;
        gotRating = 0;
        numVotes = 0;
        try {
            new connect().execute(host, "" + port, android_id);
            gotRating = new fetchRating().execute().get();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Sends score to be added to total
     * @param score Number of stars (out of five) to be sent
     */
    public void sendRating(int score){
        if(score < -1 || score > 5){
            throw new IllegalArgumentException("Score must be 0 < x < 5!");
        }else{
            if(score == 0) score = 1;
            new sendRating().execute(score);
        }
    }

    /**
     * Gets the average score from server
     * @return Score everybody else voted on
     */
    public float getRating(){
        try {
            gotRating = new fetchRating().execute().get();
        }catch(Exception e){
            e.printStackTrace();
        }
        return gotRating;
    }

    /**
     * Gets number of people who have voted
     * @return Number of people who have voted
     */
    public int getNumVotes(){
        new fetchRating().execute();
        return numVotes;
    }

    /**
     * Tells if the socket is connected to the server
     * @return true if connected to server
     */
    public boolean isConnected(){
        return isConnected;
    }

    public class connect extends AsyncTask<String, Void, Void>{
        protected Void doInBackground(String... params){
            try{
                System.out.println("Connecting to server");
                client = new Socket(params[0], Integer.parseInt(params[1]));
                dout = new DataOutputStream(client.getOutputStream());
                din = new DataInputStream(client.getInputStream());
                dout.writeUTF(params[2]);
                System.out.println("Sending device ID " + params[2]);
                if(client.isConnected()) isConnected = true;
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Connects to server, gets user rating and numVotes
     */
    public class fetchRating extends AsyncTask<Void, Void, Float> {

        protected Float doInBackground(Void... params){
            try{
                if(client != null && dout != null && din != null) {
                    dout.writeInt(1);
                    gotRating = din.readFloat();
                    numVotes = din.readInt();
                    System.out.println("Got rating " + gotRating + ", numVotes " + numVotes);
                    return gotRating;
                }
            }catch(IOException e){
                isConnected = false;
                e.printStackTrace();
            }
            return null;
        }
    }

    public class sendRating extends AsyncTask<Integer, Void, Void>{
        protected Void doInBackground(Integer... params){
            try{
                if(client != null && dout != null && din != null) {
                    dout.writeInt(0);
                    dout.writeInt(params[0]);
                    System.out.println("Sent " + params[0]);
                }
            }catch(IOException e){
                isConnected = false;
                e.printStackTrace();
            }
            return null;
        }
    }
}