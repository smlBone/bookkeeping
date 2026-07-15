package org.mf.bookkeeping.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class DualColorProgressBar extends View {

    private Paint paint;
    private RectF leftRect;
    private RectF rightRect;

    private int leftColor = 0xFFFF4444; // 红色（支出）
    private int rightColor = 0xFF4CAF50; // 绿色（收入）
    private float ratio = 0.5f; // 左右比例，0.0-1.0，表示左侧占比
    private float cornerRadius = 8f; // 圆角半径

    public DualColorProgressBar(Context context) {
        super(context);
        init();
    }

    public DualColorProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DualColorProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        leftRect = new RectF();
        rightRect = new RectF();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        if (width == 0 || height == 0) return;

        float leftWidth = width * ratio;

        leftRect.set(0, 0, leftWidth, height);
        rightRect.set(leftWidth, 0, width, height);

        paint.setColor(leftColor);
        canvas.drawRoundRect(leftRect, cornerRadius, cornerRadius, paint);

        paint.setColor(rightColor);
        canvas.drawRoundRect(rightRect, cornerRadius, cornerRadius, paint);
    }

    public void setRatio(float ratio) {
        this.ratio = Math.max(0f, Math.min(1f, ratio));
        invalidate();
    }

    public void setLeftColor(int color) {
        this.leftColor = color;
        invalidate();
    }

    public void setRightColor(int color) {
        this.rightColor = color;
        invalidate();
    }

    public void setColors(int leftColor, int rightColor) {
        this.leftColor = leftColor;
        this.rightColor = rightColor;
        invalidate();
    }

    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        invalidate();
    }

    public float getRatio() {
        return ratio;
    }

    public int getLeftColor() {
        return leftColor;
    }

    public int getRightColor() {
        return rightColor;
    }
}
