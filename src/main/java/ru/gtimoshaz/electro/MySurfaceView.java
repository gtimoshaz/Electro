package ru.gtimoshaz.electro;

import android.app.Notification;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by george on 06.04.16.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    SurfaceHolder surfaceHolder;
    Path path;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MySurfaceView(Context context) {
        super(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawRGB(255, 255, 255);
        surfaceHolder.unlockCanvasAndPost(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.WHITE);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            path = new Path();
            path.moveTo(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            path.lineTo(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            path.lineTo(event.getX(), event.getY());
        }

        if (path != null) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawPath(path, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

        return true;
    }
}
