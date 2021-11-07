package com.example.switchofmadness;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class SwitchOfMadness extends View {

    private static final boolean DEBUG_MODE = false;
    private static final int LEVEL = 5, LINE_THICK = 30, PADDING = 100;

    private Point startPoint, endPoint;
    private ArrayList<Point> grid;
    private ArrayList<Point> pathPoints;
    int ex, ey;
    Path path;

    public SwitchOfMadness(Context context) { super(context); initialize();}
    public SwitchOfMadness(Context context, @Nullable AttributeSet attrs) { super(context, attrs); initialize();}
    public SwitchOfMadness(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); initialize();}

    public void initialize(){
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.createGrid();
        generatePath();
        ex = startPoint.x;
        ey = startPoint.y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ex = (int) event.getX();
        ey = (int) event.getY();
        invalidate();
        return true;
    }

    public void resetPath(){
        generatePath();
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(DEBUG_MODE)
            drawBackground(canvas);

        drawPath(canvas);

        if(DEBUG_MODE)
            drawPoints(canvas);
            drawUserPoint(canvas);
            drawWarning(canvas);
    }

    private void createGrid() {
        this.grid = new ArrayList<>();
        this.startPoint = new Point(getPaddingX(), getPaddingY()+getPaddingHeight()/2);
        this.endPoint = new Point(getPaddingX()+getPaddingWidth(), getPaddingY()+getPaddingHeight()/2);
        if(LEVEL==1){
            this.grid.add(new Point(getPaddingX()+getPaddingWidth()/2, getPaddingY() + getPaddingHeight()/2));
        }else{
            int wUnit = this.getPaddingWidth() / (LEVEL+1), hUnit = this.getPaddingHeight()/(LEVEL-1);
            for (int j = 0; j < LEVEL; j++) {
                for (int i = 0; i < LEVEL; i++) {
                    this.grid.add(new Point(getPaddingX() + wUnit + i * wUnit, getPaddingY() + j * hUnit));
                }
            }
        }
    }
    private void generatePath() {
        Random random = new Random();
        pathPoints = new ArrayList<>();
        pathPoints.add(startPoint);
        for(int i=0; i<LEVEL; i++){
            Point randomPoint = grid.get(random.nextInt(grid.size()));
            pathPoints.add(new Point(grid.get(i).x, randomPoint.y));
        }
        pathPoints.add(endPoint);
        path = getCurvePath(pathPoints);
    }

    private Path getCurvePath(ArrayList<Point> pathPoints) {
        ArrayList<Point> conPoint1 = new ArrayList<>();
        ArrayList<Point> conPoint2 = new ArrayList<>();
        for (int i=1; i<pathPoints.size(); i++) {
            conPoint1.add(new Point((pathPoints.get(i).x + pathPoints.get(i-1).x) / 2, pathPoints.get(i-1).y));
            conPoint2.add(new Point((pathPoints.get(i).x + pathPoints.get(i-1).x) / 2, pathPoints.get(i).y));
        }

        Path path = new Path();
        path.moveTo(pathPoints.get(0).x, pathPoints.get(0).y);
        for (int i=1; i<pathPoints.size(); i++) {
            path.cubicTo(
                    conPoint1.get(i-1).x, conPoint1.get(i-1).y, conPoint2.get(i-1).x, conPoint2.get(i-1).y,
                    pathPoints.get(i).x, pathPoints.get(i).y
            );
        }
        return path;
    }

    private void drawPoints(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.getContext().getResources().getColor(R.color.black, this.getContext().getTheme()));
        paint.setStrokeWidth(LINE_THICK);

        for(Point point : grid) canvas.drawPoint(point.x, point.y, paint);
        canvas.drawPoint(startPoint.x, startPoint.y, paint);
        canvas.drawPoint(endPoint.x, endPoint.y, paint);
    }

    private void drawBackground(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.getContext().getResources().getColor(R.color.gray, this.getContext().getTheme()));
        canvas.drawRect(new Rect(0,0, this.getWidth(), this.getHeight()), paint);
    }
    private void drawPath(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.getContext().getResources().getColor(R.color.teal_200, this.getContext().getTheme()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(LINE_THICK);
        canvas.drawPath(path, paint);
    }
    private void drawUserPoint(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.getContext().getResources().getColor(R.color.red, this.getContext().getTheme()));
        paint.setStrokeWidth(LINE_THICK);
        int[] arr = new int[]{(int) this.getX(), (int) this.getY()};
        this.getLocationOnScreen(arr);
        canvas.drawPoint(ex,ey, paint);
    }
    private void drawWarning(Canvas canvas) {
        if(isOffThePath()){
            Paint paint = new Paint();
            paint.setColor(this.getContext().getResources().getColor(R.color.warning_red, this.getContext().getTheme()));
            canvas.drawRect(new Rect(0,0, this.getWidth(), this.getHeight()), paint);
        }
    }

    private boolean isOffThePath() {
        return false;
//        float minDistance = Integer.MAX_VALUE;
//        for(int i=0; i<pathPoints.size()-1; i++){
//            Point p1 = pathPoints.get(i);
//            Point p2 = pathPoints.get(i+1);
//            float distance = pDistance(ex, ey, p1.x, p1.y, p2.x, p2.y);
//            if(distance<minDistance) minDistance = distance;
//        }
//        return minDistance < 10;
    }

    private static float pDistance(float x, float y, float x1, float y1, float x2, float y2) {
        // A - the standalone point (x, y)
        // B - start point of the line segment (x1, y1)
        // C - end point of the line segment (x2, y2)
        // D - the crossing point between line from A to BC

        float AB = distBetween(x, y, x1, y1);
        float BC = distBetween(x1, y1, x2, y2);
        float AC = distBetween(x, y, x2, y2);

        // Heron's formula
        float s = (AB + BC + AC) / 2;
        float area = (float) Math.sqrt(s * (s - AB) * (s - BC) * (s - AC));

        // but also area == (BC * AD) / 2
        // BC * AD == 2 * area
        // AD == (2 * area) / BC
        // TODO: check if BC == 0
        float AD = (2 * area) / BC;
        return AD;
    }

    private static float distBetween(float x, float y, float x1, float y1) {
        float xx = x1 - x;
        float yy = y1 - y;

        return (float) Math.sqrt(xx * xx + yy * yy);
    }

    private int getPaddingWidth(){ return this.getWidth()-PADDING*2; }
    private int getPaddingHeight(){ return this.getHeight()-PADDING*2; }
    private int getPaddingX(){ return PADDING; }
    private int getPaddingY(){ return PADDING; }
}
