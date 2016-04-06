package ru.gtimoshaz.electro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    MySurfaceView mySurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        mySurfaceView = new MySurfaceView(surfaceView.getContext());

        surfaceView.getHolder().addCallback(mySurfaceView);

        surfaceView.setOnTouchListener(mySurfaceView);

    }

}
