package rich.ivan.monthbill

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min
import kotlin.properties.Delegates

class MonthBillView : View {

    private lateinit var mMonthArray: Array<Int>
    private lateinit var mBillValues: Array<Double>

    /**
     * View 宽度
     */
    private var mWidth by Delegates.notNull<Int>()

    /**
     * View 高度
     */
    private var mHeight by Delegates.notNull<Int>()

    /**
     * 折线画笔
     */
    private lateinit var mLinePaint: Paint

    /**
     * 文字画笔
     */
    private lateinit var mTextPaint: Paint

    /**
     * 空心圆画笔
     */
    private lateinit var mWhiteCirclePaint: Paint

    /**
     * 路径
     */
    private lateinit var mPath: Path

    /**
     * 路径阴影效果
     */
    private lateinit var mDashPathEffect: DashPathEffect

    /**
     * 基准宽度
     */
    private var mBaseWidth by Delegates.notNull<Float>()

    /**
     * 相邻月份间隔宽度
     */
    private var mItemWidth by Delegates.notNull<Float>()

    /**
     * 数据点最低y轴坐标
     */
    private var mMinLineHeight by Delegates.notNull<Float>()

    /**
     * 数据点最高y轴坐标
     */
    private var mMaxLineHeight by Delegates.notNull<Float>()

    /**
     * 折线最大可占用空间的高度,即折线波峰和波谷的差值,用来控制折线的陡峭程度
     */
    private var mMaxLineSpace by Delegates.notNull<Float>()

    /**
     * 已出账单月份中最低消费金额
     */
    private var mMinBillValue by Delegates.notNull<Double>()

    /**
     * 已出账单月份中最高消费金额
     */
    private var mMaxBillValue by Delegates.notNull<Double>()

    /**
     * 消费金额的极差
     */
    private var mBillRange by Delegates.notNull<Double>()

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint.color = Color.parseColor("#FF5722")

        mTextPaint = Paint()
        mTextPaint.isAntiAlias = true
        mTextPaint.color = Color.parseColor("#212121")
        mTextPaint.textSize = 48F
        mTextPaint.textAlign = Paint.Align.CENTER

        mWhiteCirclePaint = Paint()
        mWhiteCirclePaint.isAntiAlias = true
        mWhiteCirclePaint.color = Color.WHITE
        mWhiteCirclePaint.style = Paint.Style.FILL

        mPath = Path()
        mDashPathEffect = DashPathEffect(floatArrayOf(20F, 20F), 1F)
    }

    fun setMonthArray(months: Array<Int>) {
        mMonthArray = months
    }

    fun setBillValues(bills: Array<Double>) {
        mBillValues = bills
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h

        mBaseWidth = mWidth.toFloat().div(14F)
        mItemWidth = mBaseWidth.times(3)
        mMaxLineSpace = mHeight.div(4F)
        mMaxLineHeight = mHeight.toFloat().times(3F).div(4F)
        mMinLineHeight = mHeight.div(2F)

        mMaxBillValue = Utils.getMaxBillValue(mBillValues)
        mMinBillValue = Utils.getMinBillValue(mBillValues)
        mBillRange = mMaxBillValue.minus(mMinBillValue)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mode: Int = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize: Int = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode: Int = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize: Int = MeasureSpec.getSize(heightMeasureSpec)

        val fontMetrics: Paint.FontMetrics = mTextPaint.fontMetrics
        val fontHeight: Float = fontMetrics.descent - fontMetrics.ascent

        mWidth = widthSize

        mHeight = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            (fontHeight.times(4) + fontHeight.times(4)
                    + paddingTop + paddingBottom).toInt()
        }

        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPath.reset()

        // 绘制月份
        drawMonthText(canvas)

        // 观察折线可以看到折线的起点和终点y轴坐标与第一个月份点的y轴坐标相同
        // 根据当前可显示第一个月份的账单值显示确定当前的起点坐标
        // 只存在一个月账单时：mBillRange = 0
        val startY: Float = getCircleDy(mBillValues[0], mBillRange)
        mPath.moveTo(0F, startY)

        // 绘制第一条暗折线,此线的颜色通过修改Alpha值实现主线颜色变淡的效果
        mPath.lineTo(mBaseWidth, startY)
        mLinePaint.alpha = 125
        mLinePaint.style = Paint.Style.FILL_AND_STROKE
        mLinePaint.strokeWidth = 10F
        canvas?.drawPath(mPath, mLinePaint)

        mPath.reset()
        mPath.moveTo(mBaseWidth, startY)
        mLinePaint.alpha = 255

        mTextPaint.alpha = 125
        val fontMetrics: Paint.FontMetrics = mTextPaint.fontMetrics
        val fontHeight: Float = fontMetrics.descent.minus(fontMetrics.ascent)

        val totalMonths: Int = mMonthArray.size
        val pointCount: Int = min(totalMonths, mBillValues.size)

        for (i in 0 until pointCount) {
            val dx: Float = mBaseWidth.plus(mItemWidth.times(i))
            val dy = getCircleDy(mBillValues[i], mBillRange)
            // 绘制实心圆
            canvas?.drawCircle(dx, dy, 10F, mLinePaint)
            // 绘制消费金额文字
            canvas?.drawText(mBillValues[i].toString(), dx, dy.minus(fontHeight), mTextPaint)
            mPath.lineTo(dx, dy)
        }

        mLinePaint.style = Paint.Style.STROKE
        canvas?.drawPath(mPath, mLinePaint)

        mLinePaint.strokeWidth = 5F
        mLinePaint.alpha = 25

        for (i in pointCount until totalMonths) {
            val dx: Float = mBaseWidth.plus(mItemWidth.times(i))
            val dy: Float = mMinLineHeight.plus(mMaxLineSpace.times(
                    i.minus(pointCount).plus(1)).div(totalMonths.minus(pointCount)))
            mPath.lineTo(dx, dy)
        }

        // 最后一段折线
        mPath.lineTo(mWidth.toFloat(), startY)

        mLinePaint.strokeWidth = 10F
        mLinePaint.pathEffect = mDashPathEffect
        canvas?.drawPath(mPath, mLinePaint)

        // 使用完分隔效果之后及时重置为空
        mLinePaint.pathEffect = null

        // 绘制空心圆部分
        for (i in pointCount until totalMonths) {
            val dx: Float = mBaseWidth.plus(mItemWidth.times(i))
            val dy: Float = mMinLineHeight.plus(mMaxLineSpace.times(
                    i.minus(pointCount).plus(1)).div(totalMonths.minus(pointCount)))
            canvas?.drawCircle(dx, dy, 15F, mLinePaint)
            canvas?.drawCircle(dx, dy, 15F, mWhiteCirclePaint)
        }
    }

    /**
     * 绘制月份文字
     */
    private fun drawMonthText(canvas: Canvas?) {
        mTextPaint.alpha = 25
        mTextPaint.color = resources.getColor(R.color.colorSecondaryText)

        val fontMetrics: Paint.FontMetrics = mTextPaint.fontMetrics
        val fontHeight: Float = fontMetrics.descent.minus(fontMetrics.ascent)
        var dx: Float
        val dy: Float = mMaxLineHeight.plus(fontHeight.times(3).div(2))

        for (i in mMonthArray.indices) {
            dx = mBaseWidth.plus(mItemWidth.times(i))
            val text: String = context.resources.getString(R.string.month_index, mMonthArray[i])
            canvas?.drawText(text, dx, dy, mTextPaint)
        }
    }

    /**
     * 处理 [billRange] 可能为 0 的情况
     */
    private fun getCircleDy(currentBillValue: Double, billRange: Double): Float {
        return if (billRange <= 0) {
            mMaxLineHeight
        } else {
            (mMaxLineHeight - (currentBillValue.minus(mMinBillValue))
                    .div(mBillRange).times(mMaxLineSpace)).toFloat()
        }
    }
}