package ru.gtimoshaz.electro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by george on 06.04.16.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, AdapterView.OnItemSelectedListener {

    private SurfaceHolder surfaceHolder;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_red = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_rectInResistor = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_rectInCapacitor = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint_rectBounds = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float MERGE_DIST = 144;
    private final int INSTRUMENT_DRAW = 0;
    private final int INSTRUMENT_MOVE = 1;
    private final int INSTRUMENT_DELETE = 2;
    private float resistance = 220;

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.whattodo)
            touch_action = i;
        else {
            switch (i) {
                case 0:
                    resistance = 0;
                    break;
                case 1:
                    resistance = 220;
                    break;
                case 2:
                    resistance = 1000;
                    break;
                case 3:
                    resistance = 10000;
                    break;
                case 4: resistance = -1e-7f;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        touch_action = -1;
    }

    private class ElectroComponent {
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
        // Resistance of resistor if > 0 and capacity of capacitor if < 0
        // Cable if = 0
        float R;
        String name;

        PointF from, to;

        public ElectroComponent(PointF from, PointF to, float r, String name) {
            this.from = from;
            this.to = to;
            this.R = r;
            this.name = name;
        }

    }
    private ArrayList<PointF> vertexes = new ArrayList<>();
    private ArrayList<ElectroComponent> electroComponents = new ArrayList<>();
    private float down_x, down_y, last_x, last_y;
    private boolean draw_last_resistor;
    private PointF toMove = null;
    private int touch_action = 0;

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

        paint_rectInCapacitor.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_rectInCapacitor.setStrokeWidth(30.3f);
        paint_rectInCapacitor.setColor(Color.WHITE);

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

        Iterator<ElectroComponent> iterator = electroComponents.iterator();
        ElectroComponent electroComponent;

        while (iterator.hasNext()) {
            Path rectInResistor = new Path();
            Path rectInternal   = new Path();

            electroComponent = iterator.next();
            canvas.drawLine(electroComponent.x1(), electroComponent.y1(), electroComponent.x2(), electroComponent.y2(), paint);

            if (electroComponent.R != 0) {
                if (electroComponent.R > 0) {
                    rectInResistor.moveTo(electroComponent.x1() + (electroComponent.x2() - electroComponent.x1()) / 3, electroComponent.y1() + (electroComponent.y2() - electroComponent.y1()) / 3);
                    rectInResistor.lineTo(electroComponent.x2() - (electroComponent.x2() - electroComponent.x1()) / 3, electroComponent.y2() - (electroComponent.y2() - electroComponent.y1()) / 3);

                    rectInternal.moveTo(electroComponent.x1() + (electroComponent.x2() - electroComponent.x1()) / 3.1f, electroComponent.y1() + (electroComponent.y2() - electroComponent.y1()) / 3.1f);
                    rectInternal.lineTo(electroComponent.x2() - (electroComponent.x2() - electroComponent.x1()) / 3.1f, electroComponent.y2() - (electroComponent.y2() - electroComponent.y1()) / 3.1f);

                    canvas.drawPath(rectInternal, paint_rectBounds);
                    canvas.drawPath(rectInResistor, paint_rectInResistor);
                }
                else {
                    rectInResistor.moveTo(electroComponent.x1() + (electroComponent.x2() - electroComponent.x1()) / 2.1f, electroComponent.y1() + (electroComponent.y2() - electroComponent.y1()) / 2.1f);
                    rectInResistor.lineTo(electroComponent.x2() - (electroComponent.x2() - electroComponent.x1()) / 2.1f, electroComponent.y2() - (electroComponent.y2() - electroComponent.y1()) / 2.1f);

                    rectInternal.moveTo(electroComponent.x1() + (electroComponent.x2() - electroComponent.x1()) / 2.2f, electroComponent.y1() + (electroComponent.y2() - electroComponent.y1()) / 2.2f);
                    rectInternal.lineTo(electroComponent.x2() - (electroComponent.x2() - electroComponent.x1()) / 2.2f, electroComponent.y2() - (electroComponent.y2() - electroComponent.y1()) / 2.2f);

                    canvas.drawPath(rectInternal, paint_rectBounds);
                    canvas.drawPath(rectInResistor, paint_rectInCapacitor);
                }

            }
            if (electroComponent.R > 0)
                canvas.drawText(electroComponent.name + " - " + electroComponent.R + " Ω", (electroComponent.x1() + electroComponent.x2()) / 2 - 30, (electroComponent.y1() + electroComponent.y2()) / 2 + 60, paint);

            if (electroComponent.R < 0)
                canvas.drawText(electroComponent.name + " - " + electroComponent.R + " F", (electroComponent.x1() + electroComponent.x2()) / 2 - 30, (electroComponent.y1() + electroComponent.y2()) / 2 + 60, paint);

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

    private void addElectroComponent(float x1, float y1, float x2, float y2, float resist) {

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

        if (resist > 0)
            electroComponents.add(new ElectroComponent(p1, p2, resist, "R" + electroComponents.size()));

        if (resist < 0)
            electroComponents.add(new ElectroComponent(p1, p2, resist, "C" + electroComponents.size()));

        if (resist == 0)
            electroComponents.add(new ElectroComponent(p1, p2, resist, "N" + electroComponents.size()));

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

    private void findAndRemoveUnusedVertexes() {
        ArrayList<PointF> toDelete = new ArrayList<>();
        for (PointF pf :
                vertexes) {
            boolean used = false;
            for (ElectroComponent r :
                    electroComponents) {
                if (pf == r.from || pf == r.to)  {
                    used = true;
                    break;
                }
            }
            if (!used)
                toDelete.add(pf);
        }
        for (PointF pf :
                toDelete) {
            vertexes.remove(pf);
        }
    }

    private void downAction() {
        PointF pf = findNearest(down_x, down_y);
        switch (touch_action) {
            case INSTRUMENT_DRAW:
                break;

            case INSTRUMENT_MOVE:
                if (pf != null && dist_sqr(pf.x, pf.y, down_x, down_y) < 4 * MERGE_DIST) toMove = pf;
                break;

            case INSTRUMENT_DELETE:
                if (pf != null && dist_sqr(pf.x, pf.y, down_x, down_y) < 2 * MERGE_DIST) {
                    ArrayList<ElectroComponent> toDelete = new ArrayList<>();
                    for (ElectroComponent r :
                            electroComponents) {
                        if (r.from == pf || r.to == pf) toDelete.add(r);
                    }
                    for (ElectroComponent r :
                            toDelete) {
                        electroComponents.remove(r);
                    }
                    findAndRemoveUnusedVertexes();
                }
                break;
        }
    }

    private void moveAction() {
        switch (touch_action) {
            case INSTRUMENT_DRAW:
                draw_last_resistor = true;
                break;

            case INSTRUMENT_MOVE:
                if (toMove != null)
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
                    addElectroComponent(down_x, down_y, last_x, last_y, resistance);
                draw_last_resistor = false;
                break;

            case INSTRUMENT_MOVE:
                // Trying to find the closest point and merge with it
                float d = MERGE_DIST * 2;
                PointF pointF = null;
                if (toMove != null)
                    for (PointF pf : vertexes) {
                        if (pf == toMove) continue;
                        if (dist_sqr(pf, toMove) < d) {
                            pointF = pf;
                            d = dist_sqr(pf, toMove);
                        }
                    }

                if (pointF != null)
                    for (ElectroComponent r : electroComponents) {
                        if (toMove == r.to)   r.to   = pointF;
                        if (toMove == r.from) r.from = pointF;
                    }
                toMove = null;
                findAndRemoveUnusedVertexes();
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
            last_x = event.getX();
            last_y = event.getY();
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
