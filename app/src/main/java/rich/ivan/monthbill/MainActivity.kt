package rich.ivan.monthbill

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    //            private val mBillValues: Array<Double> = arrayOf(494.61)
//    private val mBillValues: Array<Double> = arrayOf(494.61, 494.61)
//    private val mBillValues: Array<Double> = arrayOf(494.61, 637.96)
//    private val mBillValues: Array<Double> = arrayOf(494.61, 637.96, 1218.03)
//    private val mBillValues: Array<Double> = arrayOf(494.61, 637.96, 1218.03, 1037.84)
//    private val mBillValues: Array<Double> = arrayOf(494.61, 637.96, 1218.03, 1037.84, 1000.90)
    private val mBillValues: Array<Double> = arrayOf(494.61, 637.96, 1218.03, 637.96, 890.24)

    private val mMonthArray: Array<Int> = arrayOf(2, 3, 4, 5, 6)

    private lateinit var mMonthBillView: MonthBillView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mMonthBillView = findViewById(R.id.month_bill_view)
        mMonthBillView.setMonthArray(mMonthArray)
        mMonthBillView.setBillValues(mBillValues)
        mMonthBillView.postInvalidate()
    }
}