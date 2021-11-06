package com.example.switchofmadness;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class SwitchOfMadness extends View {


    public SwitchOfMadness(Context context) {
        super(context);
    }

    public SwitchOfMadness(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchOfMadness(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint = new Paint();
        paint.setColor(this.getContext().getResources().getColor(R.color.black, this.getContext().getTheme()));
        canvas.drawRect(new Rect(0,0,this.getWidth(),this.getHeight()), paint);
    }

}
