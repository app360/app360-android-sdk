package vn.mog.app360.sdk.payment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Random;

import vn.mog.app360.sdk.payment.data.BankTransaction;
import vn.mog.app360.sdk.payment.data.CardTransaction;
import vn.mog.app360.sdk.payment.data.SmsTransaction;
import vn.mog.app360.sdk.payment.interfaces.PaymentFormListener;
import vn.mog.app360.sdk.payment.ui.activities.BankActivity;
import vn.mog.app360.sdk.payment.ui.activities.CardActivity;
import vn.mog.app360.sdk.payment.ui.activities.PaymentFormActivity;
import vn.mog.app360.sdk.payment.ui.activities.SmsActivity;
import vn.mog.app360.sdk.payment.ui.util.SmsAmountParser;
import vn.mog.app360.sdk.payment.utils.Const;

public class PaymentForm {
    public static final int REQUEST_CODE_CARD = new Random().nextInt(65536);
    public static final int REQUEST_CODE_SMS = new Random().nextInt(65536);
    public static final int REQUEST_CODE_BANK = new Random().nextInt(65536);
    public static final int REQUEST_CODE_PAYMENT = new Random().nextInt(65536);
    private PaymentFormListener listener;
    private String payload;
    private Activity activity;
    private int[] smsAmounts;
    private int[] bankAmounts;
    private int[] cardAmounts;
    private AmountConverter converter;
    private String appDes;

    private PaymentForm(Builder builder) {
        this.listener = builder.listener;
        this.payload = builder.payload;
        this.activity = builder.activity;
        this.converter = builder.converter;
        this.smsAmounts = builder.smsAmounts;
        this.bankAmounts = builder.bankAmounts;
        this.cardAmounts = builder.cardAmounts;
        this.appDes = builder.appDes;
    }

    /**
     * UI life cycle helper method. It parses the result when the payment form UI is finished and
     * execute relevant listener methods. Must be called on <code>onActivityResult</code> of the
     * <code>Activity</code> that starts the payment form.
     *
     * @param requestCode the requestCode of {@link android.app.Activity#onActivityResult(int, int,
     *                    android.content.Intent)}
     * @param resultCode  the resultCode of {@link android.app.Activity#onActivityResult(int, int,
     *                    android.content.Intent)}
     * @param data        the data of {@link android.app.Activity#onActivityResult(int, int,
     *                    android.content.Intent)}
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE_SMS && requestCode != REQUEST_CODE_BANK &&
                requestCode != REQUEST_CODE_CARD && requestCode != REQUEST_CODE_PAYMENT) {
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            Bundle bd = data.getExtras();

            BankTransaction bank = (BankTransaction) bd.getSerializable(Const.RESULT_BANK_BUNDLE_KEY);
            if (bank != null) {
                listener.onFinish(bank);
            }

            SmsTransaction sms = (SmsTransaction) bd.getSerializable(Const.RESULT_SMS_BUNDLE_KEY);
            if (sms != null) {
                listener.onFinish(sms);
            }

            CardTransaction card = (CardTransaction) bd.getSerializable(Const.RESULT_CARD_BUNDLE_KEY);
            if (card != null) {
                listener.onFinish(card);
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            listener.onCancel();
        } else if (resultCode == Const.RESULT_ERROR) {
            Throwable e = (Throwable) data.getExtras().getSerializable(Const.RESULT_ERROR_BUNDLE_KEY);
            listener.onError(e);
        }
    }

    /**
     * Show a payment form which allow user to choose between supported payment methods
     */
    public void showPaymentForm() { // FIXME throws IllegalArgumentException
        if (converter != null && cardAmounts != null && bankAmounts != null && smsAmounts != null) {
            Intent it = new Intent(activity, PaymentFormActivity.class);
            Bundle extras = new Bundle();
            extras.putSerializable(Const.AMOUNT_CONVERTER_BUNDLE_KEY, converter);
            extras.putIntArray(Const.CARD_AMOUNT_BUNDLE_KEY, cardAmounts);
            extras.putIntArray(Const.BANK_AMOUNT_BUNDLE_KEY, bankAmounts);
            extras.putString(Const.PAYLOAD_BUNDLE_KEY, payload);
            if (smsAmounts != null) {
                extras.putIntArray(Const.SMS_AMOUNT_BUNDLE_KEY, smsAmounts);
            }
            if (appDes != null) {
                extras.putString(Const.APP_DESCRIPTION_BUNDLE_KEY, appDes);
            }
            it.putExtras(extras);
            activity.startActivityForResult(it, REQUEST_CODE_PAYMENT);
        } else {
            Toast.makeText(activity, "Ban chưa nhập thông tin đầy đủ", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Show card payment form
     */
    public void showCardForm() {
        Intent it = new Intent(activity, CardActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(Const.AMOUNT_CONVERTER_BUNDLE_KEY, converter);
        extras.putIntArray(Const.CARD_AMOUNT_BUNDLE_KEY, cardAmounts);
        extras.putString(Const.PAYLOAD_BUNDLE_KEY, payload);
        it.putExtras(extras);
        activity.startActivityForResult(it, REQUEST_CODE_CARD);
    }

    /**
     * Show bank payment form
     */
    public void showBankForm() {
        if (bankAmounts != null && bankAmounts.length > 0) {
            Intent it = new Intent(activity, BankActivity.class);
            Bundle extras = new Bundle();
            extras.putSerializable(Const.AMOUNT_CONVERTER_BUNDLE_KEY, converter);
            extras.putIntArray(Const.BANK_AMOUNT_BUNDLE_KEY, bankAmounts);
            extras.putString(Const.PAYLOAD_BUNDLE_KEY, payload);
            it.putExtras(extras);
            activity.startActivityForResult(it, REQUEST_CODE_BANK);
        } else {
            Toast.makeText(activity, "Ban chưa nhập thông tin tiền", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Show SMS payment form
     */
    public void showSmsForm() {
        Intent it = new Intent(activity, SmsActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(Const.AMOUNT_CONVERTER_BUNDLE_KEY, converter);
        extras.putString(Const.PAYLOAD_BUNDLE_KEY, payload);
        if (smsAmounts != null) {
            extras.putIntArray(Const.SMS_AMOUNT_BUNDLE_KEY, smsAmounts);
        }
        it.putExtras(extras);
        activity.startActivityForResult(it, REQUEST_CODE_SMS);
    }

    public static class Builder {
        private PaymentFormListener listener;
        private String payload;
        private Activity activity;
        private int[] smsAmounts;
        private int[] bankAmounts;
        private int[] cardAmounts;
        private AmountConverter converter;
        private String appDes;

        /**
         * @param activity {@link android.app.Activity} that will start the payment form
         */
        public Builder(Activity activity) {
            this.activity = activity;
        }

        /**
         * @param converter {@link vn.mog.app360.sdk.payment.ui.AmountConverter} for the form
         * @return this instance of the builder
         */
        public Builder setConverter(AmountConverter converter) {
            this.converter = converter;
            return this;
        }

        /**
         * @param smsAmounts amounts to be displayed on SMS payment form
         * @return this instance of the builder
         * @deprecated use {@link #setSMSAmounts(int...)} instead
         */
        @Deprecated
        public Builder setSMSAmounts(Const.SmsAmount... smsAmounts) {
            this.smsAmounts = SmsAmountParser.getAmountArray(smsAmounts);
            return this;
        }

        /**
         * @param smsAmounts array of amounts to be displayed on SMS payment form
         * @return this instance of the builder
         */
        public Builder setSMSAmounts(int... smsAmounts) {
            this.smsAmounts = smsAmounts;
            return this;
        }

        /**
         * @param cardAmounts amounts to be displayed on card payment form
         * @return this instance of the builder
         */
        public Builder setCardAmounts(int... cardAmounts) {
            this.cardAmounts = cardAmounts;
            return this;
        }

        /**
         * @param bankAmounts amounts to be displayed on bank payment form
         * @return this instance of the builder
         */
        public Builder setBankAmounts(int... bankAmounts) {
            this.bankAmounts = bankAmounts;
            return this;
        }

        /**
         * @param listener listener for payment form events
         * @return this instance of the builder
         */
        public Builder setListener(PaymentFormListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * @param appDes app description to be displayed on the payment form
         * @return this instance of the builder
         */
        public Builder setAppDescription(String appDes) {
            this.appDes = appDes;
            return this;
        }

        /**
         * @param payload arbitrary string to be returned to client-side and server-side of the
         *                application. Optional.
         * @return this instance of the builder
         */
        public Builder setPayload(String payload) {
            this.payload = payload;
            return this;
        }

        /**
         * Construct a {@link PaymentForm} with parameters set by this builder.
         *
         * @return a <code>PaymentForm</code> ready to be displayed
         */
        public PaymentForm build() {
            return new PaymentForm(this);
        }
    }
}
