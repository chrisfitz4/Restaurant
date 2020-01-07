package com.example.restaurantthreader.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.restaurantthreader.threader.ASyncThreader;
import com.example.restaurantthreader.util.Constants;

import java.util.ArrayList;
import java.util.Random;

public class MyPresenter implements Contract.Presenter, Handler.Callback {

    private Contract.View view;
    private ArrayList<String> names;
    private ArrayList<Integer> available;
    private Handler handler;
    Thread timerThread;


    public MyPresenter(Contract.View view) {
        this.view = view;
        this.names = new ArrayList<>();
        available = new ArrayList<>();
        available.add(1);
        available.add(2);
        available.add(3);
        handler = new Handler(this);
        IntentFilter filter = new IntentFilter(Constants.UPDATE_PROGRESS);
        filter.addAction(Constants.FINISHED_TASK);
        view.getApplicationContext().registerReceiver(receiver,filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d("TAG_X", "onReceive: "+intent.getAction());
            if(intent.getAction().equals(Constants.FINISHED_TASK)){
                onTaskComplete(intent.getIntExtra(Constants.FINISHED_TASK,-1));
            }else if(intent.getAction().equals(Constants.UPDATE_PROGRESS)){
                updateProgress(intent.getIntExtra(Constants.ID_KEY,-1),
                        intent.getIntExtra(Constants.UPDATE_PROGRESS,-1));
            }
        }
    };

    public void registerReceiver(){
        view.registerReceiverView(receiver);
    }

    @Override
    public void unregisterReceiver() {
        view.unregisterReceiverView(receiver);
    }

    @Override
    public void startThread() {
        Log.d("TAG_X", "startThread: ");
        if(available.size()>0&&names.size()>0){
            int thread = available.remove(0);
            Random random = new Random();
            int timeLength = random.nextInt(Constants.MAX_TIME_LENGTH)+Constants.ADD_TIME_LENGTH;
            new ASyncThreader(thread,view.getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,timeLength*1000);
            view.updateTextViews(thread,names.remove(0));
            view.resetProgressBar(thread);
            view.updateQueueText(names.size());
        }
    }


    @Override
    public void updateProgress(int id, int percent) {
        view.updateProgressBar(id, percent);
    }

    private void resetTimerThread(){
        if(timerThread==null){
            setThread();
        }else{
            timerThread.interrupt();
            setThread();
        }
        timerThread.start();
    }

    private void setThread() {
        timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean hide = true;
                try {
                    Thread.sleep(Constants.TIMER);
                } catch (InterruptedException e) {
                    hide = false;
                    e.printStackTrace();
                }
                final boolean HIDE = hide;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG_X", "run3: ");
                        startThread();
                        if(HIDE) {
                            view.hideSplash();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onTaskComplete(int id) {
        resetTimerThread();
        view.splashTextComplete(id,view.getIdText(id));
        available.add(id);
//        final Thread thread = new Thread(){
//            @Override
//            public void run() {
//                Log.d("TAG_X", "run: ");
//                super.run();
//                try {
//                    Log.d("TAG_X", "run2: ");
//                    sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        };
//        thread.start();

    }

    @Override
    public void addPerson() {
        Random random = new Random();
        int name = random.nextInt(Constants.NAMES.length);
        names.add(Constants.NAMES[name]);
        view.updateQueueText(names.size());

    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return true;
    }
}
