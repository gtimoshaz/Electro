package ru.gtimoshaz.electro;

import android.app.Notification;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by george on 06.04.16.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, AdapterView.OnItemSelectedListener {

    private SurfaceHolder surfaceHolder;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_red = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_rectInResistor = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_rectBounds = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float MERGE_DIST = 144;
    private final int INSTRUMENT_DRAW = 0;
    private final int INSTRUMENT_MOVE = 1;
    private final int INSTRUMENT_DELETE = 2;

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        touch_action = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        touch_action = -1;
    }

    private class Resistor {
        float x1() {
            return from.x;
        };
        float y1() {
            return from.y;
        };
        float x2() {
            return to.x;
        };
        float y2() {
            return to.y;
        };
        float R;
        String name;

        PointF from, to;

        public Resistor(PointF from, PointF to, float r, String name) {
            this.from = from;
            this.to = to;
            this.R = r;
            this.name = name;
        }

    }
    private ArrayList<PointF> vertexes = new ArrayList<>();
    private ArrayList<Resistor> resistors = new ArrayList<>();
    private float down_x, down_y, last_x, last_y;
    private boolean draw_last_resistor;
    private PointF toMove = null;
    private int touch_action = -1;

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
        paint.setTextSize(30);

        paint_red.setStyle(Paint.Style.STROKE);
        paint_red.setStrokeWidth(3);
        paint_red.setColor(Color.RED);

        paint_rectInResistor.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_rectInResistor.setStrokeWidth(24);
        paint_rectInResistor.setColor(Color.WHITE);

        paint_rectBounds.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_rectBounds.setStrokeWidth(30);
        paint_rectBounds.setColor(Color.BLACK);
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

        while (iterator.hasNext()) {
            Path rectInResistor = new Path();
            Path rectInternal   = new Path();

            resistor = iterator.next();
            canvas.drawLine(resistor.x1(), resistor.y1(), resistor.x2(), resistor.y2(), paint);
            rectInResistor.moveTo(resistor.x1() + (resistor.x2() - resistor.x1()) / 3, resistor.y1() + (resistor.y2() - resistor.y1()) / 3);
            rectInResistor.lineTo(resistor.x2() - (resistor.x2() - resistor.x1()) / 3, resistor.y2() - (resistor.y2() - resistor.y1()) / 3);

            rectInternal.moveTo(resistor.x1() + (resistor.x2() - resistor.x1()) / 3.1f, resistor.y1() + (resistor.y2() - resistor.y1()) / 3.1f);
            rectInternal.lineTo(resistor.x2() - (resistor.x2() - resistor.x1()) / 3.1f, resistor.y2() - (resistor.y2() - resistor.y1()) / 3.1f);

            canvas.drawPath(rectInternal, paint_rectBounds);
            canvas.drawPath(rectInResistor, paint_rectInResistor);

            canvas.drawText(resistor.name + " - " + resistor.R + " Ω", (resistor.x1() + resistor.x2()) / 2 - 30, (resistor.y1() + resistor.y2()) / 2 + 60, paint);

        }

        for (PointF pf :
                vertexes) {
            canvas.drawCircle(pf.x, pf.y, (float) Math.sqrt(MERGE_DIST), paint_rectBounds);
        }

        if (draw_last_resistor) canvas.drawLine(down_x, down_y, last_x, last_y, paint_red);

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private static float sqr(float x) {
        return x * x;
    }

    private static float dist_sqr(PointF p1, PointF p2) {
        return sqr(p2.x - p1.x) + sqr(p2.y - p1.y);
    }

    private static float dist_sqr(float x1, float y1, float x2, float y2) {
        return sqr(x2 - x1) + sqr(y2 - y1);
    }


    private static float dist(PointF p1, PointF p2) {
        return (float) Math.sqrt(sqr(p2.x - p1.x) + sqr(p2.y - p1.y));
    }

    private void addResistor(float x1, float y1, float x2, float y2, float R) {

        PointF p1, p2;

        float dist, dist_min_1 = MERGE_DIST, dist_min_2 = MERGE_DIST;
        PointF nearest1 = null, nearest2 = null;

        for (PointF vertex : vertexes) {
            dist = sqr(vertex.x - x1) + sqr(vertex.y - y1);//, sqr(current.x - x2) + sqr(current.y - y2));
            if (dist < dist_min_1) {
                dist_min_1 = dist;
                nearest1 = vertex;
            }

            dist = sqr(vertex.x - x2) + sqr(vertex.y - y2);
            if (dist < dist_min_2) {
                dist_min_2 = dist;
                nearest2 = vertex;
            }

        }

        if (nearest1 != null) p1 = nearest1;
        else {
            p1 = new PointF(x1, y1);
            vertexes.add(p1);
        }
        if (nearest2 != null) p2 = nearest2;
        else {
            p2 = new PointF(x2, y2);
            vertexes.add(p2);
        }

        resistors.add(new Resistor(p1, p2, 220, "R" + resistors.size()));

    }

    private PointF findNearest(float x, float y) {

        PointF c = new PointF(x, y);
        PointF nearest = null;

        float dist = Float.POSITIVE_INFINITY;

        for (PointF pf : vertexes) {
            if (dist_sqr(pf, c) < dist) {
                nearest = pf;
                dist = dist_sqr(pf, c);
            }
        }
        return nearest;
    }

    private void downAction() {
        switch (touch_action) {
            case INSTRUMENT_DRAW:
                break;

            case INSTRUMENT_MOVE:
                toMove = findNearest(down_x, down_y);
                break;

            case INSTRUMENT_DELETE:
                break;
        }
    }

    private void moveAction() {
        switch (touch_action) {
            case INSTRUMENT_DRAW:
                draw_last_resistor = true;
                break;

            case INSTRUMENT_MOVE:
                toMove.set(last_x, last_y);
                break;

            case INSTRUMENT_DELETE:
                break;
        }
    }
    private void upAction() {
        switch (touch_action) {
            case INSTRUMENT_DRAW:
                if (sqr(last_x - down_x) + sqr(last_y - down_y) > 1600)
                    addResistor(down_x, down_y, last_x, last_y, 220);
                draw_last_resistor = false;
                break;

            case INSTRUMENT_MOVE:
                float d = MERGE_DIST;
                PointF pointF = null;
                for (PointF pf : vertexes) {
                    if (pf == toMove) continue;
                    if (dist_sqr(pf, toMove) < d) {
                        pointF = pf;
                        d = dist_sqr(pf, toMove);
                    }
                }

                if (pointF != null)
                for (Resistor r : resistors) {
                    if (toMove == r.to)   r.to   = pointF;
                    if (toMove == r.from) r.from = pointF;
                }
                toMove = null;
                break;

            case INSTRUMENT_DELETE:
                break;
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            down_x = event.getX();
            down_y = event.getY();
            downAction();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            last_x = event.getX();
            last_y = event.getY();
            moveAction();

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            last_x = event.getX();
            last_y = event.getY();
            upAction();
        }

        redraw();

        return true;
    }

}
