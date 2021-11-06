package com.example.switchofmadness;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SwitchOfMadness extends View {

    private static final boolean DEBUG_MODE = true;
    private static final int LEVEL = 3, LINE_THICK = 30, PADDING = 100;

    private Point startPoint, endPoint;
    private ArrayList<Point> grid;

    public SwitchOfMadness(Context context) { super(context); initialize();}
    public SwitchOfMadness(Context context, @Nullable AttributeSet attrs) { super(context, attrs); initialize();}
    public SwitchOfMadness(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); initialize();}

    public void initialize(){
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.createGrid();
    }

    private void createGrid() {
        this.grid = new ArrayList<>();
        this.startPoint = new Point(getPaddingX(), getPaddingY()+getPaddingHeight()/2);
        this.endPoint = new Point(getPaddingX()+getPaddingWidth(), getPaddingY()+getPaddingHeight()/2);
        if(LEVEL==1){
            this.grid.add(new Point(getPaddingX()+getPaddingWidth()/2, getPaddingY() + getPaddingHeight()/2));
        }else{
            int wUnit = this.getPaddingWidth() / (LEVEL+1), hUnit = this.getPaddingHeight()/(LEVEL-1);
            for (int i = 0; i < LEVEL; i++) {
                for (int j = 0; j < LEVEL; j++) {
                    this.grid.add(new Point(getPaddingX() + wUnit + i * wUnit, getPaddingY() + j * hUnit));
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.setPadding(50,50,50,50);
        if(DEBUG_MODE) drawBackground(canvas);
        if(DEBUG_MODE) drawPoints(canvas);
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

    private int getPaddingWidth(){ return this.getWidth()-PADDING*2; }
    private int getPaddingHeight(){ return this.getHeight()-PADDING*2; }
    private int getPaddingX(){ return PADDING; }
    private int getPaddingY(){ return PADDING; }
}
