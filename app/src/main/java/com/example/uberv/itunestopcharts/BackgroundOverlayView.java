package com.example.uberv.itunestopcharts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class BackgroundOverlayView extends AppCompatImageView {

    private Paint mPaint;
    private Bitmap mOverlayImage;
    private int mClipOffset;
    private Bitmap mBase;

    public BackgroundOverlayView(Context context) {
        super(context);
        init();
    }

    public BackgroundOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackgroundOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setImagePair(Bitmap base, Bitmap overlay) {
        mBase=base;
        mOverlayImage = overlay;
        setImageBitmap(mBase);
    }

    public void setOverlayOffset(int overlayOffset) {
        mClipOffset = overlayOffset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mOverlayImage == null) {
            return;
        }
        //Draw base image first, clipped to the top section
        // We clip the base image to avoid unnecessary overdraw in
        // the bottom section of the view.
        canvas.save();
        canvas.clipRect(getLeft(), getTop(), getRight(), mClipOffset);
        super.onDraw(canvas);
        canvas.restore();

        //Obtain the matrix used to scale the base image, and apply it
        // to the blurred overlay image so the two match up
        final Matrix matrix = getImageMatrix();
        canvas.save();
        canvas.clipRect(getLeft(), mClipOffset, getRight(), getBottom());
        canvas.drawBitmap(mOverlayImage, matrix, mPaint);
        canvas.restore();
    }
}
