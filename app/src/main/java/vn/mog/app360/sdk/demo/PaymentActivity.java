package vn.mog.app360.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import vn.mog.app360.sdk.demo.logger.Log;
import vn.mog.app360.sdk.payment.BankRequest;
import vn.mog.app360.sdk.payment.CardRequest;
import vn.mog.app360.sdk.payment.ResponseException;
import vn.mog.app360.sdk.payment.SmsRequest;
import vn.mog.app360.sdk.payment.StatusRequest;
import vn.mog.app360.sdk.payment.data.BankTransaction;
import vn.mog.app360.sdk.payment.data.CardTransaction;
import vn.mog.app360.sdk.payment.data.SmsTransaction;
import vn.mog.app360.sdk.payment.data.Transaction;
import vn.mog.app360.sdk.payment.interfaces.BankRequestListener;
import vn.mog.app360.sdk.payment.interfaces.CardRequestListener;
import vn.mog.app360.sdk.payment.interfaces.PaymentFormListener;
import vn.mog.app360.sdk.payment.interfaces.SmsRequestListener;
import vn.mog.app360.sdk.payment.interfaces.StatusRequestListener;
import vn.mog.app360.sdk.payment.ui.AmountConverter;
import vn.mog.app360.sdk.payment.ui.PaymentForm;
import vn.mog.app360.sdk.payment.utils.Const;

public class PaymentActivity extends BaseActivity {
    private static final String TAG = "PaymentActivity";
    private StatusRequestListener statusListener = new StatusRequestListener() {
        @Override
        public void onFinish(Transaction transaction) {
            String message = getResources().getString(
                    R.string.transaction_status,
                    transaction.getTransactionId(),
                    transaction.getStatus(),
                    transaction.getPayload());
            Log.d(TAG, message);
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof ResponseException) {
                String code = ((ResponseException) e).getCode();
                String detail = ((ResponseException) e).getDetail();
                Log.d(TAG, getResources().getString(R.string.payment_fail_msg, code, detail));
            } else {
                Log.d(TAG, "Failed to check transaction. error=" + e.getMessage(), e);
            }
        }
    };
    private PaymentFormListener listener = new PaymentFormListener() {
        @Override
        public void onFinish(Transaction transaction) {
            Log.d("PaymentFormListener", "Transaction=" + transaction);
            checkTransId(transaction.getTransactionId());
        }

        @Override
        public void onCancel() {
            Log.d("PaymentFormListener", "onCancel");
        }

        @Override
        public void onError(Throwable e) {
            Log.d("PaymentFormListener", "onError", e);
        }
    };
    private PaymentForm paymentForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startCardPayment(View view) {
        AmountConverter converter = new MyAmountConverter();
        paymentForm = new PaymentForm.Builder(this)
                .setListener(listener)
                .setPayload("payload")
                .setConverter(converter)
                .setCardAmounts(10000, 15000, 20000)
                .build();
        paymentForm.showCardForm();
    }

    public void cardPaymentViaApi(View view) {
        CardRequest cardRequest = new CardRequest.Builder()
                .setCardCode("CARD_CODE/PIN")
                .setCardSerial("CARD_SERIAL")
                .setCardVendor(CardTransaction.CardVendor.VIETTEL)
                .setPayload("payload")
                .setListener(new MyRequestListener())
                .build();
        cardRequest.execute();
    }

    public void startSmsPayment(View view) {
        AmountConverter converter = new MyAmountConverter();
        paymentForm = new PaymentForm.Builder(this)
                .setPayload("sdfsdf")
                .setSMSAmounts(Const.SmsAmount.AMOUNT_1000, Const.SmsAmount.AMOUNT_500)
                .setConverter(converter)
                .setListener(listener)
                .build();
        paymentForm.showSmsForm();
    }

    public void smsPaymentViaApi(View view) {
        SmsRequest smsRequest = new SmsRequest.Builder()
                .setAmounts(Const.SmsAmount.AMOUNT_1000, Const.SmsAmount.AMOUNT_15000)
                .setPayload("payload")
                .setListener(new MyRequestListener())
                .build();
        smsRequest.execute();
    }

    public void startPaymentForm(View view) {
        AmountConverter converter = new MyAmountConverter();
        paymentForm = new PaymentForm.Builder(this)
                .setListener(listener)
                .setPayload("payload")
                .setSMSAmounts(Const.SmsAmount.AMOUNT_15000, Const.SmsAmount.AMOUNT_500)
                .setBankAmounts(50000, 100000, 200000)
                .setCardAmounts(10000, 15000, 20000)
                .setConverter(converter)
                .setAppDescription("Chúc mừng năm mới")
                .build();
        paymentForm.showPaymentForm();
    }

    public void startBankPayment(View view) {
        AmountConverter converter = new MyAmountConverter();
        paymentForm = new PaymentForm.Builder(this)
                .setListener(listener)
                .setPayload("sdfsdf")
                .setConverter(converter)
                .setBankAmounts(50000, 100000, 200000)
                .build();
        paymentForm.showBankForm();
    }

    public void bankPaymentViaApi(View view) {
        BankRequest bankRequest = new BankRequest.Builder()
                .setAmount(25000)
                .setPayload("payload")
                .setListener(new MyRequestListener())
                .build();
        bankRequest.execute();
    }

    public void checkTransId(String transId) {
        StatusRequest statusRequest = new StatusRequest.Builder()
                .setListener(statusListener)
                .setTransactionId(transId)
                .build();
        statusRequest.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (paymentForm != null) {
            paymentForm.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private static class MyAmountConverter implements AmountConverter {
        @Override
        public String smsAmountToString(int amount) {
            return amount * 7.5 + " xu";
        }

        @Override
        public String bankAmountToString(int amount) {
            return amount * 10 + " xu";
        }

        @Override
        public String cardAmountToString(int amount) {
            return amount * 9 + " xu";
        }
    }

    private static class MyRequestListener implements CardRequestListener, SmsRequestListener, BankRequestListener {
        @Override
        public void onSuccess(CardTransaction transaction) {
            Log.d(TAG, "onSuccess=" + transaction);
        }

        @Override
        public void onSuccess(SmsTransaction transaction) {
            Log.d(TAG, "onSuccess=" + transaction);
        }

        @Override
        public void onSuccess(BankTransaction transaction) {
            Log.d(TAG, "onSuccess=" + transaction);
        }

        @Override
        public void onFailure(Throwable e) {
            Log.d(TAG, "onFailure", e);
        }
    }
}
