package com.xavierjonesco.keepnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class ocr extends AppCompatActivity implements View.OnClickListener {

    SurfaceView mCameraView;
    ImageView pause_button;
    TextView mTextView;
    CameraSource mCameraSource;
    ImageView send;
    ImageView cancel;
    Button gallery;
    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        final Intent i = getIntent();
        final String activity = i.getStringExtra("parent");



        // button = findViewById(R.id.button);
        mCameraView = findViewById(R.id.surfaceView);
        mTextView = findViewById(R.id.text_view);
        pause_button = findViewById(R.id.pause);
        send = findViewById(R.id.send);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                String current_title = i.getStringExtra("current_title");
                String current_text = i.getStringExtra("current_text");
                if(activity.equals("add")){
                    intent = new Intent(getApplicationContext(),AddNote.class);

                        intent.putExtra("message",current_text);
                        intent.putExtra("title",current_title);
                    }else{
                    intent = new Intent(getApplicationContext(),Edit.class);
                    Long ID = i.getLongExtra("ID",0);
                    intent.putExtra("ID",ID);
                    intent.putExtra("message",current_text);
                    intent.putExtra("title",current_title);
                }
                startActivity(intent);
            }
        });
        mCameraView.setOnClickListener(this);
        startCameraSource();
        // Set Clicks Here
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int min = 0;
                int max = mTextView.getText().length();
                String text = "";
                if(mTextView.isFocused()){
                    final int selStart = mTextView.getSelectionStart();
                    final int selEnd = mTextView.getSelectionEnd();
                    min = Math.max(0, Math.min(selStart, selEnd));
                    max = Math.max(0, Math.max(selStart, selEnd));
                    final CharSequence selectedText = mTextView.getText().subSequence(min,max);
                    text = selectedText.toString();
                }else{
                    text = mTextView.getText().toString();
                }
                Intent intent;
                String current_title = i.getStringExtra("current_title");
                String current_text = i.getStringExtra("current_text");
                if(activity.equals("add")){
                    intent = new Intent(getApplicationContext(),AddNote.class);
                    if(!(text.equals(""))){

                        intent.putExtra("message",current_text +" "+ text);
                        intent.putExtra("title",current_title);
                    }
                }else{
                    intent = new Intent(getApplicationContext(),Edit.class);
                    if(!(text.equals(""))){
                        Long ID = i.getLongExtra("ID",0);
                        intent.putExtra("ID",ID);
                        intent.putExtra("message",current_text +" "+ text);
                        intent.putExtra("title",current_title);
                    }
                }


                startActivity(intent);
            }
        });

    }
    @Override
    public void onRestart(){
        super.onRestart();
        // put your code here...
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    final String capture_text = mTextView.getText().toString();
                    mCameraSource.release();
                    mTextView.setText(capture_text);
                    pause_button.setVisibility(View.VISIBLE);
                }catch(NullPointerException e){

                }

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCameraSource() {

        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(3840, 2160)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(120.0f)
                    .build();
            //16:9 Aspect Ratio needed
            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(ocr.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        Context context = getApplicationContext();
                        PackageManager pm = context.getPackageManager();


                        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                            mCameraSource.start(mCameraView.getHolder());
                            pause_button.setVisibility(View.INVISIBLE);
                        } else {


                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {

                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < items.size(); i++) {
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                mTextView.setText(stringBuilder.toString().replaceAll("[\\n]", " "));
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    final String capture_text = mTextView.getText().toString();
                    mCameraSource.release();
                    mTextView.setText(capture_text);
                    pause_button.setVisibility(View.VISIBLE);
                } catch (NullPointerException e) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(ocr.this,
                                new String[]{Manifest.permission.CAMERA},
                                requestPermissionID);
                        return;
                    }
                    try {
                        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                        mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                                .setFacing(CameraSource.CAMERA_FACING_BACK)
                                .setRequestedPreviewSize(3840, 2160)
                                .setAutoFocusEnabled(true)
                                .setRequestedFps(120.0f)
                                .build();
                        startCameraSource();
                        pause_button.setVisibility(View.INVISIBLE);
                        mCameraSource.start(mCameraView.getHolder());
                        Log.d("Hello","Clicked");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}