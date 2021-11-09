package com.example.switchofmadness;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
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
import androidx.core.graphics.PathParser;

import java.util.ArrayList;
import java.util.Random;

public class SwitchOfMadness extends View {

    public interface OnChangeListener {
        void onChange(boolean on);
    }

    private static boolean DEBUG_MODE = false;
    private static final int LEVEL = 5, PATH_THICK = 30, PADDING = 100, OK_DISTANCE = 40, USER_SIZE = 80;

    private PointF startPoint, endPoint;
    private ArrayList<PointF> grid;
    float ex, ey;
    Path path;
    float[] pathApproximatePoints;

    public SwitchOfMadness(Context context) { super(context); }
    public SwitchOfMadness(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }
    public SwitchOfMadness(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    OnChangeListener onChangeListener;
    public void setOnChangeListener(OnChangeListener onChangeListener){
        this.onChangeListener=onChangeListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initialize();
    }
    private void initialize() {
        createGrid();
        createAndSetPath();
        updateUserPoint(startPoint.x, startPoint.y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            release=true;
        }else{
            double d = Math.sqrt(Math.pow(ex - event.getX(), 2) + Math.pow(ey-event.getY(), 2));
            if(d<OK_DISTANCE){
                updateUserPoint(event.getX(), event.getY());
            }
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(DEBUG_MODE){
            drawBackground(canvas);
            drawOkArea(canvas);
        }

        drawPath(canvas);
        drawProgress(canvas);
        drawUserPoint(canvas);

        if(DEBUG_MODE){
//            drawGrid(canvas);
            drawWarning(canvas);
        }
    }

    private void createGrid() {
        grid = new ArrayList<>();
        startPoint = new PointF(getPaddingX(), getPaddingY()+getPaddingHeight()/2);
        endPoint = new PointF(getPaddingX()+getPaddingWidth(), getPaddingY()+getPaddingHeight()/2);
        if(LEVEL==1){
            grid.add(new PointF(getPaddingX()+getPaddingWidth()/2, getPaddingY() + getPaddingHeight()/2));
        }else{
            float wUnit = getPaddingWidth() / (LEVEL+1), hUnit = getPaddingHeight()/(LEVEL-1);
            for (int j = 0; j < LEVEL; j++) {
                for (int i = 0; i < LEVEL; i++) {
                    this.grid.add(new PointF(getPaddingX() + wUnit + i * wUnit, getPaddingY() + j * hUnit));
                }
            }
        }
    }
    ArrayList<PointF> pathPoints;
    private void createAndSetPath() {
        Random random = new Random();
        pathPoints = new ArrayList<>();
        pathPoints.add(startPoint);
        for(int i=0; i<LEVEL; i++){
            PointF randomPoint = grid.get(random.nextInt(grid.size()));
            pathPoints.add(new PointF(grid.get(i).x, randomPoint.y));
        }
        pathPoints.add(endPoint);

        path = U_Path.getCurvePath(pathPoints);
        pathApproximatePoints = path.approximate(0.05f);
    }

    private void drawGrid(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.getContext().getResources().getColor(R.color.black, this.getContext().getTheme()));
        paint.setStrokeWidth(PATH_THICK);

        for(PointF point : grid) canvas.drawPoint(point.x, point.y, paint);
        canvas.drawPoint(startPoint.x, startPoint.y, paint);
        canvas.drawPoint(endPoint.x, endPoint.y, paint);
    }
    private void drawBackground(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.getContext().getResources().getColor(R.color.gray, this.getContext().getTheme()));
        canvas.drawRect(new Rect(0,0, this.getWidth(), this.getHeight()), paint);
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
        paint.setStrokeWidth(OK_DISTANCE*2);

        for(int i=1; i<pathApproximatePoints.length; i+=2){
            if(pathApproximatePoints[i-1]<1 || pathApproximatePoints[i]<1) continue;
            canvas.drawPoint(pathApproximatePoints[i-1], pathApproximatePoints[i], paint);
        }
    }

    private void drawPath(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.getContext().getResources().getColor(R.color.dark_gray, this.getContext().getTheme()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(PATH_THICK);
        canvas.drawPath(path, paint);
    }
    private void drawProgress(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.getContext().getResources().getColor(R.color.gray, this.getContext().getTheme()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(PATH_THICK);
        Path clip = new Path();

        clip.moveTo(ex, 0);
        clip.lineTo(this.getWidth(), 0);
        clip.lineTo(this.getWidth(), this.getHeight());
        clip.lineTo(ex, this.getHeight());

        clip.close();
        canvas.save();
        canvas.clipPath(clip);
        canvas.drawPath(path, paint);
        canvas.restore();
    }
    private void drawUserPoint(Canvas canvas) {
        Paint paint = new Paint();
        if(on){
            paint.setColor(this.getContext().getResources().getColor(R.color.black_gray, this.getContext().getTheme()));
        }else{
            paint.setColor(this.getContext().getResources().getColor(R.color.middle_gray, this.getContext().getTheme()));
        }
        paint.setStrokeWidth(USER_SIZE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPoint(ex, ey, paint);
    }

    private boolean isOffThePath() {
        for(int i=1; i<pathApproximatePoints.length; i+=2){
            if(pathApproximatePoints[i-1]<1 || pathApproximatePoints[i]<1) continue;
            double d = Math.sqrt(Math.pow(ex - pathApproximatePoints[i-1], 2) + Math.pow(ey-pathApproximatePoints[i], 2));
            if(d<OK_DISTANCE) return false;
        }
        return true;
    }
    boolean block = false, release = true, on = false;
    private void updateUserPoint(float x, float y) {
        if(!block && release){
            ex = x;
            ey = y;
            invalidate();

            double d = Math.sqrt(Math.pow(ex - endPoint.x, 2) + Math.pow(ey-endPoint.y, 2));
            boolean on = d<OK_DISTANCE;
            if(on != this.on && onChangeListener!=null) onChangeListener.onChange(on);
            this.on = on;

            if(isOffThePath()){
                block = true;
                release = false;
                AnimatorSet animatorSet = new AnimatorSet();
                ValueAnimator translationX = ValueAnimator.ofFloat(ex, startPoint.x);
                translationX.setDuration(300);
                translationX.addUpdateListener(animation -> {
                    ex = (float) animation.getAnimatedValue();
                });
                ValueAnimator translationY = ValueAnimator.ofFloat(ey, startPoint.y);
                translationY.setDuration(300);
                translationY.addUpdateListener(animation -> {
                    ey = (float) animation.getAnimatedValue();
                    invalidate();
                });
                translationY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        block = false;
                    }
                });
//                animatorSet.playTogether(translationX, translationY);
//                animatorSet.start();


                Random random = new Random();
                ArrayList<PointF> pathPoints2 = new ArrayList<>();
                pathPoints2.add(startPoint);
                for(int i=0; i<LEVEL; i++){
                    PointF randomPoint = grid.get(random.nextInt(grid.size()));
                    pathPoints2.add(new PointF(grid.get(i).x, randomPoint.y));
                }
                pathPoints2.add(endPoint);
//                Path newPath = U_Path.getCurvePath(pathPoints);

                ObjectAnimator objectAnimator = new ObjectAnimator();
//                objectAnimator.setTarget(pathPoints);
//                objectAnimator.setPropertyName("pathData");
                objectAnimator.setObjectValues(pathPoints, pathPoints2);
                objectAnimator.setEvaluator(new PathEvaluator());
                objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        pathPoints = (ArrayList<PointF>) animation.getAnimatedValue();
                        path = U_Path.getCurvePath(pathPoints);
                    }
                });
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        pathApproximatePoints = path.approximate(0.05f);
                    }
                });
                objectAnimator.setDuration(300);

                animatorSet.playTogether(translationX, translationY, objectAnimator);
                animatorSet.start();
            }
        }
    }

    // 적오
    // 1. Evaluator로 int, float 외에 내가 원하는것의 애니메이션을 만들 수 있어!
    // 2. 이건 Path 의 예시야

    public class PathEvaluator implements TypeEvaluator<ArrayList<PointF>> {

        @Override
        public ArrayList<PointF> evaluate(float fraction, ArrayList<PointF> startValue, ArrayList<PointF> endValue) {
            ArrayList<PointF> result = new ArrayList<>();
            for(int i=0; i<startValue.size(); i++){
                result.add(new PointF(
                        startValue.get(i).x + (endValue.get(i).x - startValue.get(i).x)*fraction,
                        startValue.get(i).y + (endValue.get(i).y - startValue.get(i).y)*fraction
                ));
            }
            return result;
        }
    }


    public void resetPath(){ createAndSetPath(); invalidate(); }
    public void switchDebugMode() { DEBUG_MODE = !DEBUG_MODE; invalidate(); }

    private float getPaddingWidth(){ return this.getWidth()-PADDING*2; }
    private float getPaddingHeight(){ return this.getHeight()-PADDING*2; }
    private float getPaddingX(){ return PADDING; }
    private float getPaddingY(){ return PADDING; }
}
