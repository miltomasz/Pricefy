package com.plumya.pricefy.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by miltomasz on 15/07/18.
 */

public class ProgressCircleView extends View {

    private int sweepAngle = 0;
    private int startAngle = 270;
    private int step;

    private RectF oval;

    private Paint progressCirclePaint;

    public ProgressCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        progressCirclePaint = new Paint();
        progressCirclePaint.setAntiAlias(true);
        progressCirclePaint.setStyle(Paint.Style.STROKE);
        progressCirclePaint.setStrokeWidth(30.0F);
        progressCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        progressCirclePaint.setColor(Color.BLUE);

    }

    public void updateProgress() {
        this.sweepAngle += step;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // draw background line
//        canvas.drawArc(mRectF, 0, 360, false, mPaintBackground);
        // draw progress line
//        canvas.drawArc(oval, startAngle, sweepAngle, false, progressCirclePaint);

        // TODO Auto-generated method stub
        float width = (float) getWidth();
        float height = (float) getHeight();
        float radius;

        if (width > height) {
            radius = height / 4;
        } else {
            radius = width / 4;
        }

        radius = 170.0F;

        Path path = new Path();
        path.addCircle(width / 2, height / 2, radius, Path.Direction.CW);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(50);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        float center_x, center_y;
        final RectF oval = new RectF();
        paint.setStyle(Paint.Style.STROKE);

        center_x = width / 2;
        center_y = height / 2;

        oval.set(center_x - radius,
                center_y - radius,
                center_x + radius,
                center_y + radius);
        canvas.drawArc(oval, 90, 120, false, paint);
    }
}
