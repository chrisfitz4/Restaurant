package com.example.restaurantthreader.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;

public interface Contract {

    interface Presenter{

        void startThread();

        void updateProgress(int id, int percent);

        void onTaskComplete(int id);

        void addPerson();

        void registerReceiver();

        void unregisterReceiver();

    }


    interface View{

        void updateProgressBar(int id, int percent);

        void updateTextViews(int id, String name);

        void updateQueueText(int num);

        void splashTextComplete(int id, String text);

        void hideSplash();

        void resetProgressBar(int id);

        String getIdText(int id);

        Context getApplicationContext();

        void registerReceiverView(BroadcastReceiver receiver);

        void unregisterReceiverView(BroadcastReceiver receiver);
    }


}
