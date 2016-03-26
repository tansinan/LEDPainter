package com.ledpainter.ledpainter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by tansinan on 2016/3/12.
 */
public class PaintingView extends View implements View.OnTouchListener{

    Bitmap buffer;
    protected int width = 0;
    protected int height = 0;

    protected float lastTouchX = -1.0f;
    protected float lastTouchY = -1.0f;
    protected float touchX = -1.0f;
    protected float touchY = -1.0f;

    protected int brushColor = Color.BLACK;
    protected boolean inEraseMode = false;

    public void setBrushColor(int brushColor)
    {
        this.brushColor = brushColor;
    }
    public void setEraseModeEnabled(boolean eraseModeEnabled)
    {
        inEraseMode = eraseModeEnabled;
    }

    public PaintingView(Context context) {
        super(context);
        setOnTouchListener(this);
    }
    public PaintingView(Context context,AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw (Canvas canvas) {
        if(buffer == null) return;
        Canvas bufferCanvas = new Canvas(buffer);
        Paint p = new Paint();
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeJoin(Paint.Join.ROUND);
        if(inEraseMode)
            p.setColor(Color.WHITE);
        else
            p.setColor(brushColor);
        p.setStrokeWidth(10.0f);
        //canvas.drawRect(width * 0.25f, height * 0.25f, width * 0.75f, height * 0.75f, p);
        if(lastTouchX >= 0 && lastTouchY >= 0 &&
                touchX >= 0 && touchY >= 0 &&
                lastTouchX < buffer.getWidth() && lastTouchY < buffer.getHeight()) {
            //bufferCanvas.drawCircle(lastTouchX, lastTouchY, 50.0f, p);
            bufferCanvas.drawLine(lastTouchX, lastTouchY, touchX, touchY, p);
            canvas.drawText("Hello, world", 100.0f, 100.0f, p);
        }
        canvas.drawBitmap(buffer, 0, 0, p);
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh)
    {
        width = w;
        height = h;
        int bufferWidth = Math.min(w, h);
        buffer = Bitmap.createBitmap(bufferWidth, bufferWidth, Bitmap.Config.ARGB_8888);
        for(int i=0;i<buffer.getWidth();i++)
        {
            for(int j=0;j<buffer.getHeight();j++)
            {
                buffer.setPixel(i, j, Color.WHITE);
            }
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(/*event.getPointerCount() <= 0*/event.getActionMasked() == MotionEvent.ACTION_UP)
        {
            lastTouchX = lastTouchY = touchX = touchY = -1.0f;
            return true;
        }
        event.getPointerId(0);
        float x = event.getX();
        float y = event.getY();
        lastTouchX = touchX;
        lastTouchY = touchY;
        touchX = x;
        touchY = y;
        invalidate();
        return true;
    }
}
