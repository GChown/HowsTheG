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
    //TODO: make this actually connect to a server
    Socket client;
    DataInputStream din;
    DataOutputStream dout;
    int gotRating;
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
        new fetchRating().execute(host, "" + port);
            System.out.println("Connecting to " + client);
            gotRating = 0;
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
    public int getRating(){
        return gotRating;
    }

    /**
     * Gets number of people who have voted
     * @return Number of people who have voted
     */
    public int getNumVotes(){

        return 10;
    }


    class fetchRating extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... params){
            try{
                client = new Socket(host, port);
                dout = new DataOutputStream(client.getOutputStream());
                din = new DataInputStream(client.getInputStream());

                byte[] info = new byte[2];
                //Send byte 0, get array of bytes (2):
                //rating, numVoters
                dout.writeByte(0);
                din.read(info);
                for(byte b : info){
                    System.out.print(b + ", ");
                }


            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
