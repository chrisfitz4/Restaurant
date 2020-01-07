package com.example.restaurantthreader.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurantthreader.R;
import com.example.restaurantthreader.presenter.Contract;
import com.example.restaurantthreader.presenter.MyPresenter;
import com.example.restaurantthreader.util.Constants;

import javax.xml.transform.Templates;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements Contract.View {

    @BindView(R.id.textview1)
    TextView textView1;
    @BindView(R.id.textview2)
    TextView textView2;
    @BindView(R.id.textview3)
    TextView textView3;
    @BindView(R.id.progress_horizontal1)
    ProgressBar progressBar1;
    @BindView(R.id.progress_horizontal2)
    ProgressBar progressBar2;
    @BindView(R.id.progress_horizontal3)
    ProgressBar progressBar3;

    @BindView(R.id.add_people)
    Button addPerson;
    @BindView(R.id.in_queue_num)
    TextView numberInQueue;
    @BindView(R.id.splash_text)
    TextView splashText;
    Contract.Presenter presenter;
    //Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        presenter = new MyPresenter(this);

//        presenter.registerReceiver();

        updateQueueText(0);

        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addPerson();
                presenter.startThread();
            }
        });

        checkCameraPermission();
    }
    private void checkCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},Constants.REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==Constants.REQUEST_CODE){
            if(permissions[0].equals(Manifest.permission.CAMERA)&&
                    grantResults[0]==(PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Camera permission was granted", Toast.LENGTH_SHORT).show();
            }else if(grantResults[0]!=(PackageManager.PERMISSION_GRANTED)){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
                    checkCameraPermission();
                }else{
                    Toast.makeText(this,"Permission must be granted from phone settings",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void updateProgressBar(int id, int percent) {
        switch (id){
            case 1:
                progressBar1.setProgress(percent);
                return;
            case 2:
                progressBar2.setProgress(percent);
                return;
            case 3:
                progressBar3.setProgress(percent);
        }
    }

    @Override
    public void updateQueueText(int num) {
        String string;
        if(num==1){
            string = "There is one person in the queue";
        }else{
            string = "There are "+num+" people in the queue";
        }
        numberInQueue.setText(string);
    }

    //when a new one is started
    @Override
    public void updateTextViews(int id, String name) {
        switch (id){
            case 1:
                textView1.setText(name);
                return;
            case 2:
                textView2.setText(name);
                return;
            case 3:
                textView3.setText(name);
        }
    }

    @Override
    public void resetProgressBar(int id) {
        switch (id){
            case 1:
                progressBar1.setProgress(0);
                return;
            case 2:
                progressBar2.setProgress(0);
                return;
            case 3:
                progressBar3.setProgress(0);
        }
    }

    //when a task is complete
    @Override
    public void splashTextComplete(int id, String text) {
        splashText.setText("Great! "+text+" finished their task");
        splashText.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSplash() {
        splashText.setVisibility(View.GONE);
    }

    @Override
    public String getIdText(int id) {
        String toReturn;
        switch(id) {
            case 1:
                toReturn = textView1.getText().toString().trim();
                return toReturn;
            case 2:
                toReturn = textView2.getText().toString().trim();
                return toReturn;
            case 3:
                toReturn = textView3.getText().toString().trim();
                return toReturn;
        }
        return "";
    }

    @Override
    public void registerReceiverView(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter(Constants.UPDATE_PROGRESS);
        filter.addAction(Constants.FINISHED_TASK);
        registerReceiver(receiver,filter);
    }


    @Override
    public void unregisterReceiverView(BroadcastReceiver receiver) {
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterReceiver();
    }
}
