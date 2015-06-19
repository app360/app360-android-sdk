package vn.mog.app360.sdk.payment.ui.util;

import vn.mog.app360.sdk.payment.utils.Const;

public class SmsAmountParser {
    public static int[] getAmountArray(Const.SmsAmount... amountInputs) {
        if (amountInputs == null) {
            return new int[]{500, 1000, 2000, 3000, 4000, 5000, 10000, 15000};
        } else {
            int[] convertedArrays = new int[amountInputs.length];
            for (int i = 0; i < amountInputs.length; i++) {
                convertedArrays[i] = Integer.valueOf(amountInputs[i].toString());
            }
            return convertedArrays;
        }
    }
}
