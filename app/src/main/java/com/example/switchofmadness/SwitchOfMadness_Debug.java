package com.example.switchofmadness;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class SwitchOfMadness_Debug extends SwitchOfMadness {

    public SwitchOfMadness_Debug(Context context) { super(context); }
    public SwitchOfMadness_Debug(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }
    public SwitchOfMadness_Debug(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override
    public void draw(Canvas canvas) {
        drawBackground(canvas);
        drawOkArea(canvas);
        super.draw(canvas);
        drawGrid(canvas);
        drawWarning(canvas);
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
        paint.setStrokeWidth(TOLERANCE *2);

        for(int i=1; i<pathApproximatePoints.length; i+=2){
            if(pathApproximatePoints[i-1]<1 || pathApproximatePoints[i]<1) continue;
            canvas.drawPoint(pathApproximatePoints[i-1], pathApproximatePoints[i], paint);
        }
    }
    private void drawGrid(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.getContext().getResources().getColor(R.color.black, this.getContext().getTheme()));
        paint.setStrokeWidth(PATH_THICK);

        for(PointF point : grid) canvas.drawPoint(point.x, point.y, paint);
        canvas.drawPoint(startPoint.x, startPoint.y, paint);
        canvas.drawPoint(endPoint.x, endPoint.y, paint);
    }
}
