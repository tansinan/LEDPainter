package com.ledpainter.ledpainter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by tansinan on 2016/3/12.
 */
public class PaintingView extends View implements View.OnTouchListener{

    Bitmap buffer;
    Bitmap pixelatedBuffer;
    protected int width = 0;
    protected int height = 0;
    protected int bufferWidth = 0;

    protected float lastTouchX = -1.0f;
    protected float lastTouchY = -1.0f;
    protected float touchX = -1.0f;
    protected float touchY = -1.0f;

    protected int brushColor = Color.WHITE;
    protected boolean inEraseMode = false;
    protected boolean pixelModeEnabled = false;
    protected int gridWidth = 5;
    protected int gridHeight = 5;
    protected int gridData[][];

    public boolean isPixelModeEnabled() {
        return pixelModeEnabled;
    }

    public void setPixelModeEnabled(boolean pixelModeEnabled) {
        this.pixelModeEnabled = pixelModeEnabled;
        invalidate();
    }

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
        gridData = new int[gridWidth][gridHeight];
    }
    public PaintingView(Context context,AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnTouchListener(this);
        gridData = new int[gridWidth][gridHeight];
    }

    protected void drawPixelModes(Canvas canvas) {
        float averageWidth = bufferWidth / gridWidth;
        float averageHeight = bufferWidth / gridHeight;

        for(int i = 0; i < gridWidth; i++) {
            for(int j = 0; j < gridHeight; j++) {
                int beginX = (int)(averageWidth * i);
                int beginY = (int)(averageHeight * j);
                int endX = (int)(averageWidth * (i + 1));
                int endY = (int)(averageHeight * (j + 1));
                int pixelCount = (endX - beginX) * (endY - beginY);
                int newRed = 0, newGreen = 0, newBlue = 0;
                for(int k = beginX; k < endX; k++) {
                    for (int l = beginY; l < endY; l++) {
                        int color = buffer.getPixel(k, l);
                        newRed += Color.red(color);
                        newGreen += Color.green(color);
                        newBlue += Color.blue(color);
                    }
                }
                newRed /= pixelCount;
                newGreen /= pixelCount;
                newBlue /= pixelCount;
                int newColor = Color.rgb(newRed, newGreen, newBlue);
                gridData[i][j] = newColor;
                for(int k = beginX; k < endX; k++) {
                    for (int l = beginY; l < endY; l++) {
                        pixelatedBuffer.setPixel(k, l, newColor);
                    }
                }
            }
        }
        canvas.drawBitmap(pixelatedBuffer, 0, 0, new Paint());
    }
    @Override
    protected void onDraw (Canvas canvas) {
        if(buffer == null) return;
        Canvas bufferCanvas = new Canvas(buffer);
        Paint p = new Paint();
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeJoin(Paint.Join.ROUND);
        if(inEraseMode)
            p.setColor(Color.BLACK);
        else
            p.setColor(brushColor);
        p.setStrokeWidth(30.0f);
        //canvas.drawRect(width * 0.25f, height * 0.25f, width * 0.75f, height * 0.75f, p);
        if(lastTouchX >= 0 && lastTouchY >= 0 &&
                touchX >= 0 && touchY >= 0 &&
                lastTouchX < buffer.getWidth() && lastTouchY < buffer.getHeight()) {
            //bufferCanvas.drawCircle(lastTouchX, lastTouchY, 50.0f, p);
            bufferCanvas.drawLine(lastTouchX, lastTouchY, touchX, touchY, p);
            canvas.drawText("Hello, world", 100.0f, 100.0f, p);
        }
        if(isPixelModeEnabled())
            drawPixelModes(canvas);
        else
            canvas.drawBitmap(buffer, 0, 0, p);
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh)
    {
        width = w;
        height = h;
        bufferWidth = Math.min(w, h);
        buffer = Bitmap.createBitmap(bufferWidth, bufferWidth, Bitmap.Config.ARGB_8888);
        pixelatedBuffer = Bitmap.createBitmap(bufferWidth, bufferWidth, Bitmap.Config.ARGB_8888);
        for(int i=0;i<buffer.getWidth();i++)
        {
            for(int j=0;j<buffer.getHeight();j++)
            {
                buffer.setPixel(i, j, Color.BLACK);
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
