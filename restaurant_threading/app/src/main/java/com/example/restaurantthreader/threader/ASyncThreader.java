package com.example.restaurantthreader.threader;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.restaurantthreader.util.Constants;

public class ASyncThreader extends AsyncTask<Integer,Integer,Void> {

    private int id;
    private Context context;

    public ASyncThreader(int id, Context context) {
        this.id = id;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Integer... time) {
        Log.d("TAG_X", "doInBackground: ");
        final int FINAL_TIME = time[0];
        new Thread(){
            public synchronized void start(){
                try {
                    Log.d("TAG_X", "doInBackground: try catch success");
                    long sleepTime = FINAL_TIME/100;
                    for(int i = 0;i<100;i++) {
                        Thread.sleep(sleepTime);
                        publishProgress(i);
                    }
                } catch (InterruptedException e) {
                    Log.e("TAG_X", "doInBackground: "+e.getMessage());
                }
            }
        }.start();

        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        //Log.d("TAG_X", "onProgressUpdate: ");
        super.onProgressUpdate(values);
        Intent intent = new Intent();
        intent.setAction(Constants.UPDATE_PROGRESS);
        intent.putExtra(Constants.ID_KEY,id);
        intent.putExtra(Constants.UPDATE_PROGRESS,values[0]);
        context.sendBroadcast(intent);
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Intent intent = new Intent();
        intent.setAction(Constants.FINISHED_TASK);
        intent.putExtra(Constants.FINISHED_TASK,id);
        context.sendBroadcast(intent);
    }
}
