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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by george on 06.04.16.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private SurfaceHolder surfaceHolder;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_red = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_rectInResistor = new Paint(Paint.ANTI_ALIAS_FLAG);
    private class Resistor {
        float x1;
        float y1;
        float x2;
        float y2;
        float R;
        String name;

        public Resistor(float x1, float y1, float x2, float y2, float r, String name) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            R = r;
            this.name = name;
        }
    }
    private ArrayList<Resistor> resistors = new ArrayList<>();
    private float down_x, down_y, last_x, last_y;

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
        paint.setColor(Color.BLACK);

        paint_red.setStyle(Paint.Style.STROKE);
        paint_red.setStrokeWidth(3);
        paint_red.setColor(Color.RED);

        paint_rectInResistor.setStyle(Paint.Style.FILL);
        paint_rectInResistor.setStrokeWidth(3);
        paint_rectInResistor.setColor(Color.WHITE);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private void redraw() {
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawRGB(255, 255, 255);

        Iterator<Resistor> iterator = resistors.iterator();
        Resistor resistor;
        Path rectInResistor = new Path();

        while (iterator.hasNext()) {
            resistor = iterator.next();
            canvas.drawLine(resistor.x1, resistor.y1, resistor.x2, resistor.y2, paint);
            rectInResistor.moveTo(resistor.x1 + (resistor.x2 - resistor.x1) / 3, resistor.y1 + (resistor.y2 - resistor.y1) / 3);
            rectInResistor.lineTo(resistor.x2 - (resistor.x2 - resistor.x1) / 3, resistor.y2 - (resistor.y2 - resistor.y1) / 3);
            canvas.drawPath(rectInResistor, paint_red);
        }


        canvas.drawLine(down_x, down_y, last_x, last_y, paint_red);

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        boolean multi = event.getPointerCount() > 1;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            down_x = event.getX();
            down_y = event.getY();

            //for (int i = 0; i < resistors.length; i++) {

            //}

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            last_x = event.getX();
            last_y = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            if ((last_x - down_x) * (last_x - down_x) + (last_y - down_y) * (last_y - down_y) > 400)
                resistors.add(new Resistor(down_x, down_y, last_x, last_y, 220, "R" + resistors.size()));
        }

        redraw();

        return true;
    }
}
