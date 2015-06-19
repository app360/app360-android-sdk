package vn.mog.app360.sdk.payment.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import vn.mog.app360.sdk.payment.SmsRequest;
import vn.mog.app360.sdk.payment.data.SmsTransaction;
import vn.mog.app360.sdk.payment.interfaces.SmsRequestListener;
import vn.mog.app360.sdk.payment.ui.AmountConverter;
import vn.mog.app360.sdk.payment.ui.Vendor;
import vn.mog.app360.sdk.payment.ui.util.ButtonView;
import vn.mog.app360.sdk.payment.ui.widget.VendorSpinerAdapter;
import vn.mog.app360.sdk.payment.utils.Const;
import vn.mog.app360.sdk.payment.ui.R;

public class SmsActivity extends Activity {
    private ProgressBar progressBar;
    private ScrollView layoutSMS;
    private AmountConverter converter;
    private SmsTransaction smsTransaction;
    private Spinner vendorSelect;
    private VendorSpinerAdapter vendorAdapter;
    private ArrayList<ButtonView> arrayBtn;
    private LinearLayout container;
    private String payload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_mwork_activity_sms);

        ImageView backBtn = (ImageView) findViewById(R.id.com_mwork_app_icon);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressBar = ((ProgressBar) findViewById(R.id.com_mwork_loading));
        layoutSMS = ((ScrollView) findViewById(R.id.com_mwork_layout_sms));
        container = ((LinearLayout) findViewById(R.id.container));

        vendorSelect = (Spinner) findViewById(R.id.com_mwork_spn_card);
        vendorAdapter = new VendorSpinerAdapter(this, R.layout.com_mwork_vendor_spinner_item, Arrays.asList(Vendor.SMSVENDORS));
        vendorSelect.setAdapter(vendorAdapter);

        Intent it = getIntent();
        Bundle bd = it.getExtras();
        converter = (AmountConverter) bd.getSerializable(Const.AMOUNT_CONVERTER_BUNDLE_KEY);
        payload = bd.getString(Const.PAYLOAD_BUNDLE_KEY);

        initArrayBtn(bd);
        initLayout();
    }

    private void initArrayBtn(Bundle bd) {
        int[] amountBank = bd.getIntArray(Const.SMS_AMOUNT_BUNDLE_KEY);
        arrayBtn = new ArrayList<>();

        for (int amount : amountBank) {
            ButtonView btn = new ButtonView(amount, 0, converter.smsAmountToString(amount));
            arrayBtn.add(btn);
        }
    }

    private void initLayout() {
        if (arrayBtn.size() <= 0) {
            return;
        }
        for (ButtonView btn : arrayBtn) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.com_mwork_sms_button, null);

            final int amount = btn.getmAmount();
            TextView sendButton = (TextView) view.findViewById(R.id.com_mwork_text_number);
            sendButton.setText(amount + " VND");
            sendButton.setOnClickListener(new AmountOnClickListener(amount));

            TextView amountButton = (TextView) view.findViewById(R.id.com_mwork_text_amount);
            amountButton.setText(btn.getmDes());
            amountButton.setOnClickListener(new AmountOnClickListener(amount));

            container.addView(view);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void finishActivity() {
        Intent returnIntent = new Intent();
        Bundle extras = new Bundle();
        extras.putSerializable(Const.RESULT_SMS_BUNDLE_KEY, smsTransaction);
        returnIntent.putExtras(extras);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void finishActivity(Throwable e) {
        Intent returnIntent = new Intent();
        Bundle extras = new Bundle();
        extras.putSerializable(Const.RESULT_ERROR_BUNDLE_KEY, e);
        returnIntent.putExtras(extras);
        setResult(Const.RESULT_ERROR, returnIntent);
        finish();
    }

    private class AmountOnClickListener implements View.OnClickListener {
        private final int amount;

        public AmountOnClickListener(int amount) {
            this.amount = amount;
        }

        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            SmsRequest request = new SmsRequest.Builder().setAmounts(amount).setPayload(payload)
                    .setSmsVendor(SmsTransaction.SMSVendor.getSmsVendorFromString(Arrays.asList(Vendor.SMSVENDORS).get(vendorSelect.getSelectedItemPosition()).getVendor().toLowerCase()))
                    .setListener(new SmsRequestListener() {
                        @Override
                        public void onSuccess(SmsTransaction transaction) {
                            smsTransaction = transaction;
                            SmsManager.getDefault().sendTextMessage(transaction.getServices().get(0).getRecipient(), null, transaction.getSyntax(), null, null);
                            finishActivity();
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            finishActivity(e);
                        }
                    }).build();
            request.execute();
        }
    }
}
