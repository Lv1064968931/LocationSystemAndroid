package com.example.locationsystem.SelfDesignComponent;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class DragFloatActionButton extends ImageView {
        private int parentHeight;
        private int parentWidth;

        public DragFloatActionButton(Context context) {
            super(context);
        }

        public DragFloatActionButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        private int lastX;
        private int lastY;

        private boolean isDrag;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int rawX = (int) event.getRawX();
            int rawY = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    setPressed(true);
                    isDrag = false;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    lastX = rawX;
                    lastY = rawY;
                    ViewGroup parent;
                    if (getParent() != null) {
                        parent = (ViewGroup) getParent();
                        parentHeight = parent.getHeight();
                        parentWidth = parent.getWidth();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (parentHeight <= 0 || parentWidth == 0) {
                        isDrag = false;
                        break;
                    } else {
                        isDrag = true;
                    }
                    int dx = rawX - lastX;
                    int dy = rawY - lastY;
                    //这里修复一些华为手机无法触发点击事件
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    if (distance == 0) {
                        isDrag = false;
                        break;
                    }
                    float x = getX() + dx;
                    float y = getY() + dy;
                    //检测是否到达边缘 左上右下
                    x = x < 0 ? 0 : x > parentWidth - getWidth() ? parentWidth - getWidth() : x;
                    y = getY() < 0 ? 0 : getY() + getHeight() > parentHeight ? parentHeight - getHeight() : y;
                    setX(x);
                    setY(y);
                    lastX = rawX;
                    lastY = rawY;
                    Log.i("aa", "isDrag=" + isDrag + "getX=" + getX() + ";getY=" + getY() + ";parentWidth=" + parentWidth);
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isNotDrag()) {
                        //恢复按压效果
                        setPressed(false);
                        if (rawX >= parentWidth / 2) {
                            //靠右吸附
                            animate().setInterpolator(new DecelerateInterpolator())
                                    .setDuration(500)
                                    .xBy(parentWidth - getWidth() - getX())
                                    .start();
                        } else {
                            //靠左吸附
                            ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
                            oa.setInterpolator(new DecelerateInterpolator());
                            oa.setDuration(500);
                            oa.start();
                        }
                    }
                    break;
                default:
                    break;
            }
            //如果是拖拽则消s耗事件，否则正常传递即可。
            return !isNotDrag() || super.onTouchEvent(event);
        }

        private boolean isNotDrag() {
            return !isDrag && (getX() == 0|| (getX() == parentWidth - getWidth()));
        }

    //圆形按钮
    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap b =  ((BitmapDrawable)drawable).getBitmap();

        if(null == b)
        {
            return;
        }

        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();


        Bitmap roundBitmap =  getCroppedBitmap(bitmap, w);
        canvas.drawBitmap(roundBitmap, 0,0, null);

    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
                sbmp.getWidth() / 2+0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);


        return output;
    }
    }

