package rich.ivan.monthbill;

/**
 * Author: Rich.Ivan
 * Email: 2625683516@qq.com
 * Created: 2017-06-14 15:00
 */

public final class Utils {

    public static double getMaxBillValue(double[] bills) {
        if (bills.length < 1) {
            throw new IllegalArgumentException("bill's array length should > 0");
        }

        if (bills.length == 1) {
            return bills[0];
        }

        double maxBillValue = bills[0];

        for (int i = 1; i < bills.length; i++) {
            if (bills[i] > maxBillValue) {
                maxBillValue = bills[i];
            }
        }

        return maxBillValue;
    }

    public static double getMinBillValue(double[] bills) {
        if (bills.length < 1) {
            throw new IllegalArgumentException("bill's array length should > 0");
        }

        if (bills.length == 1) {
            return bills[0];
        }

        double minBillValue = bills[0];

        for (int i = 1; i < bills.length; i++) {
            if (bills[i] < minBillValue) {
                minBillValue = bills[i];
            }
        }

        return minBillValue;
    }
}
