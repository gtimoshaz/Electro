package ru.gtimoshaz.electro;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by george on 06.04.16.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public MySurfaceView(Context context) {
        super(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawRGB(255, 255, 255);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
