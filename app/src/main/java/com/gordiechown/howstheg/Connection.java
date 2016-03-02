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
        gotRating = 0;
        numVotes = 0;
        try {
            new connect().execute(host, "" + port, android_id);
            if(client != null && client.isConnected())
            gotRating = new fetchRating().execute().get();
            else
                new connect().execute(host, "" + port, android_id);
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
           if(client != null && client.isConnected())
               gotRating = new fetchRating().execute().get();
        }catch(Exception e){
            e.printStackTrace();
        }
        return gotRating;
    }

    /**
     * Returns whether or not the client is connected to the server
     * @return true if client is connected, false if it isn't
     */
    public boolean isConnected(){
        if(client != null)
        return client.isConnected();
        else return false;
    }

    /**
     * Gets number of people who have voted
     * @return Number of people who have voted
     */
    public int getNumVotes(){
        try {
            if (client != null && client.isConnected())
                new fetchRating().execute();
        }catch(Exception e){
            e.printStackTrace();
        }
        return numVotes;
    }


    public class connect extends AsyncTask<String, Void, Void>{
        protected Void doInBackground(String... params){
            try{
                //System.out.println("Connecting to server");
                client = new Socket(params[0], Integer.parseInt(params[1]));
                dout = new DataOutputStream(client.getOutputStream());
                din = new DataInputStream(client.getInputStream());
                    //System.out.println("Connected to server");
                    //System.out.println("Sending device ID " + params[2]);
                    dout.writeUTF(params[2]);
            }catch(IOException e){
                System.out.println("Couldn't connect to server");
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
                    if(isConnected()) {
                        dout.writeInt(1);
                        gotRating = din.readFloat();
                        numVotes = din.readInt();
                        return gotRating;
                    }
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public class sendRating extends AsyncTask<Integer, Void, Void>{
        protected Void doInBackground(Integer... params){
            try{
                if(isConnected()) {
                    dout.writeInt(0);
                    dout.writeInt(params[0]);
                    //System.out.println("Sent " + params[0]);
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}