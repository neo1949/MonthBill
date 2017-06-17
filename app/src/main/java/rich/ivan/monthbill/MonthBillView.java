package rich.ivan.monthbill;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author: Rich.Ivan
 * Created: 2017-06-14 14:49
 */

public class MonthBillView extends View {

    private int[] mMonthArray;
    private double[] mBillValues;

    /**
     * View宽度
     */
    private int mWidth;
    /**
     * View高度
     */
    private int mHeight;

    /**
     * 折线画笔
     */
    private Paint mLinePaint;

    /**
     * 文字画笔
     */
    private Paint mTextPaint;

    /**
     * 空心圆画笔
     */
    private Paint mWhiteCirclePaint;

    private Path mPath;

    /**
     * 基准宽度
     */
    private float mBaseWidth;

    /**
     * 相邻月份间隔宽度
     */
    private float mItemWidth;

    /**
     * 数据点最低y轴坐标
     */
    private float mMinLineHeight;

    /**
     * 数据点最高y轴坐标
     */
    private float mMaxLineHeight;

    /**
     * 折线最大可占用空间的高度,即折线波峰和波谷的差值,用来控制折线的陡峭程度
     */
    private float mMaxLineSpace;

    /**
     * 已出账单月份中最低消费金额
     */
    private double mMinBillValue;

    /**
     * 已出账单月份中最高消费金额
     */
    private double mMaxBillValue;

    /**
     * 消费金额的极差
     */
    private double mBillRange;

    public MonthBillView(Context context) {
        this(context, null);
    }

    public MonthBillView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.parseColor("#FF5722"));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#212121"));
        mTextPaint.setTextSize(48);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mWhiteCirclePaint = new Paint();
        mWhiteCirclePaint.setAntiAlias(true);
        mWhiteCirclePaint.setColor(Color.WHITE);
        mWhiteCirclePaint.setStyle(Paint.Style.FILL);

        mPath = new Path();
    }

    public void setMonthArray(int[] months) {
        mMonthArray = months;
    }

    public void setBillValues(double[] bills) {
        mBillValues = bills;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mBaseWidth = mWidth / 14f;
        mItemWidth = mBaseWidth * 3;
        mMaxLineSpace = mHeight / 4f;
        mMaxLineHeight = mHeight * 3 / 4f;
        mMinLineHeight = mHeight / 2f;

        mMaxBillValue = Utils.getMaxBillValue(mBillValues);
        mMinBillValue = Utils.getMinBillValue(mBillValues);
        mBillRange = mMaxBillValue - mMinBillValue;

        // 避免只有一个月消费金额或者最低最高消费金额相同造成mBillRange当除数为0的情况
        mBillRange = (mBillRange == 0 ? 1 : mBillRange);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPath.reset();

        // 绘制月份
        drawMonthText(canvas);

        // 观察折线可以看到折线的起点和终点y轴坐标与第一个月份点的y轴坐标相同
        // 根据当前可显示第一个月份的账单值显示确定当前的起点坐标
        float startY = (float) (mMaxLineHeight - (mBillValues[0] - mMinBillValue) /
                mBillRange * mMaxLineSpace);
        mPath.moveTo(0, startY);

        // 绘制第一条暗折线,此线的颜色通过修改Alpha值实现主线颜色变淡的效果
        mPath.lineTo(mBaseWidth, startY);
        mLinePaint.setAlpha(125);
        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLinePaint.setStrokeWidth(10);
        canvas.drawPath(mPath, mLinePaint);

        mPath.reset();
        mPath.moveTo(mBaseWidth, startY);
        mLinePaint.setAlpha(255);

        mTextPaint.setAlpha(125);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;

        int totalMonths = mMonthArray.length;
        int pointCount = Math.min(totalMonths, mBillValues.length);

        for (int i = 0; i < pointCount; i++) {
            float dx = mBaseWidth + mItemWidth * i;
            float dy = (float) (mMaxLineHeight - (mBillValues[i] - mMinBillValue) /
                    mBillRange * mMaxLineSpace);
            // 绘制实心圆
            canvas.drawCircle(dx, dy, 10, mLinePaint);
            // 绘制消费金额文字
            canvas.drawText(String.valueOf(mBillValues[i]), dx, dy - fontHeight, mTextPaint);
            mPath.lineTo(dx, dy);
        }

        mLinePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mLinePaint);

        mLinePaint.setStrokeWidth(5);
        mLinePaint.setAlpha(25);

        for (int i = pointCount; i < totalMonths; i++) {
            float dx = mBaseWidth + mItemWidth * i;
            float dy = mMinLineHeight + mMaxLineSpace * (i - pointCount + 1) / (totalMonths - pointCount);
            mPath.lineTo(dx, dy);
        }

        // 最后一段折线
        mPath.lineTo(mWidth, startY);

        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{20, 20}, 1);
        mLinePaint.setStrokeWidth(10);
        mLinePaint.setPathEffect(dashPathEffect);
        canvas.drawPath(mPath, mLinePaint);

        // 使用完分隔效果之后及时重置为空
        mLinePaint.setPathEffect(null);

        // 绘制空心圆部分
        for (int i = pointCount; i < totalMonths; i++) {
            float dx = mBaseWidth + mItemWidth * i;
            float dy = mMinLineHeight + mMaxLineSpace * (i - pointCount + 1) / (totalMonths - pointCount);
            canvas.drawCircle(dx, dy, 15, mLinePaint);
            canvas.drawCircle(dx, dy, 15, mWhiteCirclePaint);
        }
    }

    /**
     * 绘制月份文字
     */
    private void drawMonthText(Canvas canvas) {
        mTextPaint.setAlpha(255);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        float dx;
        float dy = mMaxLineHeight + fontHeight * 3 / 2;

        for (int i = 0; i < mMonthArray.length; i++) {
            dx = mBaseWidth + mItemWidth * i;
            canvas.drawText(mMonthArray[i] + "月", dx, dy, mTextPaint);
        }
    }
}
