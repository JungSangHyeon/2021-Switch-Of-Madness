package com.example.switchofmadness;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class SwitchOfMadness extends View {

    private static boolean DEBUG_MODE = false;
    private static final int LEVEL = 10, LINE_THICK = 30, PADDING = 100;

    private PointF startPoint, endPoint;
    private ArrayList<PointF> grid;
    private ArrayList<PointF> pathPoints;
    float ex, ey;
    Path path;
    float[] pathApproximatePoints;

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
        ex = event.getX();
        ey = event.getY();
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
            drawOkArea(canvas);
            drawUserPoint(canvas);
            drawWarning(canvas);
    }

    private void createGrid() {
        this.grid = new ArrayList<>();
        this.startPoint = new PointF(getPaddingX(), getPaddingY()+getPaddingHeight()/2);
        this.endPoint = new PointF(getPaddingX()+getPaddingWidth(), getPaddingY()+getPaddingHeight()/2);
        if(LEVEL==1){
            this.grid.add(new PointF(getPaddingX()+getPaddingWidth()/2, getPaddingY() + getPaddingHeight()/2));
        }else{
            int wUnit = this.getPaddingWidth() / (LEVEL+1), hUnit = this.getPaddingHeight()/(LEVEL-1);
            for (int j = 0; j < LEVEL; j++) {
                for (int i = 0; i < LEVEL; i++) {
                    this.grid.add(new PointF(getPaddingX() + wUnit + i * wUnit, getPaddingY() + j * hUnit));
                }
            }
        }
    }
    private void generatePath() {
        Random random = new Random();
        pathPoints = new ArrayList<>();
        pathPoints.add(startPoint);
        for(int i=0; i<LEVEL; i++){
            PointF randomPoint = grid.get(random.nextInt(grid.size()));
            pathPoints.add(new PointF(grid.get(i).x, randomPoint.y));
        }
        pathPoints.add(endPoint);
        path = getCurvePath(pathPoints);
        pathApproximatePoints = path.approximate(0.05f);
    }

    private Path getCurvePath(ArrayList<PointF> pathPoints) {
        ArrayList<PointF> conPoint1 = new ArrayList<>();
        ArrayList<PointF> conPoint2 = new ArrayList<>();
        for (int i=1; i<pathPoints.size(); i++) {
            conPoint1.add(new PointF((pathPoints.get(i).x + pathPoints.get(i-1).x) / 2, pathPoints.get(i-1).y));
            conPoint2.add(new PointF((pathPoints.get(i).x + pathPoints.get(i-1).x) / 2, pathPoints.get(i).y));
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

        for(PointF point : grid) canvas.drawPoint(point.x, point.y, paint);
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
        paint.setColor(this.getContext().getResources().getColor(R.color.purple_700, this.getContext().getTheme()));
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

    private void drawOkArea(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.getContext().getResources().getColor(R.color.ok_area, this.getContext().getTheme()));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(80);

        for(int i=1; i<pathApproximatePoints.length; i+=2){
            canvas.drawPoint(pathApproximatePoints[i-1], pathApproximatePoints[i], paint);
        }
    }

    private boolean isOffThePath() {
        for(int i=1; i<pathApproximatePoints.length; i+=2){
            double d = Math.sqrt(Math.pow(ex - pathApproximatePoints[i-1], 2) + Math.pow(ey-pathApproximatePoints[i], 2));
            if(d<40) return false;
        }
        return true;
    }

    private int getPaddingWidth(){ return this.getWidth()-PADDING*2; }
    private int getPaddingHeight(){ return this.getHeight()-PADDING*2; }
    private int getPaddingX(){ return PADDING; }
    private int getPaddingY(){ return PADDING; }

    public void changeTest() {
        DEBUG_MODE = !DEBUG_MODE;
        invalidate();
    }
}
