package vn.mog.app360.sdk.payment.ui;

import java.io.Serializable;

/**
 * Converter responsible for converting payment amount (in VND) to customized string to display on
 * the payment form. For example, it could convert 5000 to "5 diamonds" or "10 coins". See a
 * demonstration <a href="http://i.imgur.com/QvJS792.png">here</a>: on the left is the amount in
 * VND, the right the customized string.
 */
public interface AmountConverter extends Serializable {
    public String smsAmountToString(int amount);

    public String bankAmountToString(int amount);

    public String cardAmountToString(int amount);
}
