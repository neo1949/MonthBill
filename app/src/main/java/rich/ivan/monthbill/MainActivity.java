package rich.ivan.monthbill;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

//    private double[] mBillValues = new double[]{494.61};
//    private double[] mBillValues = new double[]{494.61, 637.96};
//    private double[] mBillValues = new double[]{494.61, 637.96, 1218.03};
//    private double[] mBillValues = new double[]{494.61, 637.96, 1218.03, 1037.84};
    private double[] mBillValues = new double[]{494.61, 637.96, 1218.03, 1037.84, 1000.90};

    private int[] mMonthArray = new int[]{2, 3, 4, 5, 6};

    private MonthBillView mMonthBillView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMonthBillView = (MonthBillView) findViewById(R.id.month_bill_view);
        mMonthBillView.setMonthArray(mMonthArray);
        mMonthBillView.setBillValues(mBillValues);
        mMonthBillView.postInvalidate();
    }
}
