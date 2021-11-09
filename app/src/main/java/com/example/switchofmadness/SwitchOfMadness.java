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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class SwitchOfMadness extends View {

    // User Variable. Edit OK!
    protected static final int PADDING = 100;
    protected static final int PATH_NODE_COUNT = 7, PATH_THICK = 30, TOLERANCE = 40, THUMB_SIZE = 80;
    protected static final int RESET_ANIMATION_DURATION = 300;

    // System Variable. DO NOT EDIT BELOW
    protected ArrayList<PointF> grid;
    protected PointF startPoint, endPoint;

    private Path path;
    private ArrayList<PointF> pathPoints;
    protected float[] pathApproximatePoints;
    private static final float PATH_POINT_ACCEPTABLE_ERROR = 0.05f;

    private float eventX, eventY;
    private boolean eventAcceptBlock = false, transactionReleased = true, isSwitchOn = false;
    private final ArrayList<OnChangeListener> onChangeListeners = new ArrayList<>();

    public SwitchOfMadness(Context context) { super(context); }
    public SwitchOfMadness(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }
    public SwitchOfMadness(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initialize();
    }
    private void initialize() {
        createGrid();
        updatePath(createRandomPathPoints());
        updateUserPoint(startPoint.x, startPoint.y);
    }
    private void createGrid() {
        grid = new ArrayList<>();
        startPoint = new PointF(getPaddingX(), getPaddingY()+getPaddingHeight()/2);
        endPoint = new PointF(getPaddingX()+getPaddingWidth(), getPaddingY()+getPaddingHeight()/2);
        if(PATH_NODE_COUNT ==1){
            grid.add(new PointF(getPaddingX()+getPaddingWidth()/2, getPaddingY() + getPaddingHeight()/2));
        }else{
            float wUnit = getPaddingWidth() / (PATH_NODE_COUNT +1), hUnit = getPaddingHeight()/(PATH_NODE_COUNT -1);
            for (int j = 0; j < PATH_NODE_COUNT; j++) {
                for (int i = 0; i < PATH_NODE_COUNT; i++) {
                    this.grid.add(new PointF(getPaddingX() + wUnit + i * wUnit, getPaddingY() + j * hUnit));
                }
            }
        }
    }
    private void updatePath(ArrayList<PointF> randomPathPoints) {
        pathPoints = randomPathPoints;
        path = U_Path.getCurvePath(pathPoints);
        pathApproximatePoints = path.approximate(PATH_POINT_ACCEPTABLE_ERROR);
    }
    private ArrayList<PointF> createRandomPathPoints() {
        Random random = new Random();
        ArrayList<PointF> pathPoints = new ArrayList<>();
        pathPoints.add(startPoint);
        for(int i = 0; i< PATH_NODE_COUNT; i++){
            PointF randomPoint = grid.get(random.nextInt(grid.size()));
            pathPoints.add(new PointF(grid.get(i).x, randomPoint.y));
        }
        pathPoints.add(endPoint);
        return pathPoints;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawPath(canvas);
        drawProgress(canvas);
        drawUserPoint(canvas);
    }
    private void drawPath(Canvas canvas) {
        Paint paint = getPaint(R.color.dark_gray, PATH_THICK);
        canvas.drawPath(path, paint);
    }
    private void drawProgress(Canvas canvas) {
        Paint paint = getPaint(R.color.gray, PATH_THICK);
        canvas.save();
        canvas.clipPath(getProgressClip());
        canvas.drawPath(path, paint);
        canvas.restore();
    }
    private void drawUserPoint(Canvas canvas) {
        int color = isSwitchOn? R.color.black_gray : R.color.middle_gray;
        Paint paint = getPaint(color, THUMB_SIZE);
        canvas.drawPoint(eventX, eventY, paint);
    }
    private Path getProgressClip() {
        Path clip = new Path();
        clip.moveTo(eventX, 0);
        clip.lineTo(this.getWidth(), 0);
        clip.lineTo(this.getWidth(), this.getHeight());
        clip.lineTo(eventX, this.getHeight());
        clip.close();
        return clip;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP) transactionReleased =true;
        else{
            double d = getDistance(eventX, eventY, event.getX(), event.getY());
            if(d < TOLERANCE) updateUserPoint(event.getX(), event.getY()); // 경로 끝을 클릭하는 경우 방지
        }
        return true;
    }
    private void updateUserPoint(float x, float y) {
        if(!eventAcceptBlock && transactionReleased){ // 틀린경우 다시 이벤트 받으려면, 애니메이션 끝 &  한번 때기 필요
            eventX = x;
            eventY = y;
            invalidate();

            checkChangeAndCallback();
            checkOffThePathAndAnimate();
        }
    }
    private void checkChangeAndCallback() {
        double d = getDistance(eventX, eventY, endPoint.x, endPoint.y);
        boolean newIsSwitchOn = d< TOLERANCE;
        if(newIsSwitchOn != this.isSwitchOn){
            for(OnChangeListener onChangeListener : onChangeListeners)  onChangeListener.onChange(newIsSwitchOn);
        }
        this.isSwitchOn = newIsSwitchOn;
    }
    private void checkOffThePathAndAnimate() {
        if(isOffThePath()){
            eventAcceptBlock = true;
            transactionReleased = false;

            AnimatorSet animatorSet = new AnimatorSet();

            ValueAnimator translationX = ValueAnimator.ofFloat(eventX, startPoint.x);
            translationX.addUpdateListener(animation -> eventX = (float) animation.getAnimatedValue());

            ValueAnimator translationY = ValueAnimator.ofFloat(eventY, startPoint.y);
            translationY.addUpdateListener(animation -> eventY = (float) animation.getAnimatedValue());

            ObjectAnimator objectAnimator = new ObjectAnimator();
            objectAnimator.setObjectValues(pathPoints, this.createRandomPathPoints());
            objectAnimator.setEvaluator(new PathEvaluator());
            objectAnimator.addUpdateListener(animation -> {
                updatePath((ArrayList<PointF>) animation.getAnimatedValue());
                invalidate();
            });
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    eventAcceptBlock = false;
                }
            });

            animatorSet.playTogether(translationX, translationY, objectAnimator);
            animatorSet.setDuration(RESET_ANIMATION_DURATION);
            animatorSet.start();
        }
    }
    boolean isOffThePath() {
        for(int i=1; i<pathApproximatePoints.length; i+=2){
            if(pathApproximatePoints[i-1]<1 || pathApproximatePoints[i]<1) continue;
            double d = getDistance(eventX, eventY, pathApproximatePoints[i-1], pathApproximatePoints[i]);
            if(d< TOLERANCE) return false;
        }
        return true;
    }

    public void addOnChangeListener(OnChangeListener onChangeListener){ onChangeListeners.add(onChangeListener); }

    private float getPaddingWidth(){ return this.getWidth()-PADDING*2; }
    private float getPaddingHeight(){ return this.getHeight()-PADDING*2; }
    private float getPaddingX(){ return PADDING; }
    private float getPaddingY(){ return PADDING; }

    private int getColor(int color) { return this.getContext().getResources().getColor(color, this.getContext().getTheme()); }
    private Paint getPaint(int color, int width) {
        Paint paint = new Paint();
        paint.setColor(this.getColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
        return paint;
    }

    private double getDistance(float x1, float y1, float x2, float y2) { return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)); }

    // Inner Class
    public interface OnChangeListener {
        void onChange(boolean on);
    }

    public static class PathEvaluator implements TypeEvaluator<ArrayList<PointF>> {
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
}
