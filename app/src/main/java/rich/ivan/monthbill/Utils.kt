package rich.ivan.monthbill

import java.lang.IllegalArgumentException

object Utils {

    /**
     * Return minimum bill value from [bills].
     */
    fun getMaxBillValue(bills: Array<Double>): Double {
        if (bills.isEmpty()) {
            throw IllegalArgumentException("The length of the bill array should be more than 0.")
        }

        if (bills.size == 1) {
            return bills[0]
        }

        var maxBillValue: Double = bills[0]
        for (i in 1 until bills.size) {
            if (bills[i] > maxBillValue) {
                maxBillValue = bills[i]
            }
        }
        return maxBillValue
    }

    /**
     * Return maximum bill value from [bills].
     */
    fun getMinBillValue(bills: Array<Double>): Double {
        if (bills.isEmpty()) {
            throw IllegalArgumentException("The length of the bill array should be more than 0")
        }

        if (bills.size == 1) {
            return bills[0]
        }

        var minBillValue: Double = bills[0]
        for (i in 1 until bills.size) {
            if (bills[i] < minBillValue) {
                minBillValue = bills[i]
            }
        }
        return minBillValue
    }
}