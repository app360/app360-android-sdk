package vn.mog.app360.sdk.payment.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import vn.mog.app360.sdk.payment.BankRequest;
import vn.mog.app360.sdk.payment.data.BankTransaction;
import vn.mog.app360.sdk.payment.interfaces.BankRequestListener;
import vn.mog.app360.sdk.payment.ui.AmountConverter;
import vn.mog.app360.sdk.payment.ui.util.ButtonView;
import vn.mog.app360.sdk.payment.utils.Const;
import vn.mog.app360.sdk.payment.utils.Util;
import vn.mog.app360.sdk.payment.ui.R;

/**
 * Created by lethiem on 12/13/14.
 */
public class BankActivity extends Activity {
    public static final int WEBVIEW_REQUEST_CODE = new Random().nextInt(65536);
    private SharedPreferences pref;
    private String lang;
    private ImageView imageBack;
    private LinearLayout bankContainer;
    private List<ButtonView> arrayBtn;
    private AmountConverter converter;
    private BankRequest.Builder builder;
    private ProgressBar progressBar;
    private boolean isSuccess = false;
    private BankTransaction bankTransaction;
    private BankRequestListener bankListener = new BankRequestListener() {
        @Override
        public void onSuccess(BankTransaction transaction) {
            isSuccess = true;
            bankTransaction = transaction;
            progressBar.setVisibility(View.GONE);
            Intent it = new Intent(BankActivity.this, BankWebviewActivity.class);
            it.putExtra(Const.BANK_URL_BUNDLE_KEY, transaction.getPayUrl());
            startActivityForResult(it, WEBVIEW_REQUEST_CODE);
        }

        @Override
        public void onFailure(Throwable e) {
            finishActivityWithError(e);
        }
    };
    private String payload = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_mwork_activity_bank_list_amount);

        // FIXME common class for manage sharedpref
        this.pref = getSharedPreferences("com.mwork.payment.pref", 0);
        this.lang = this.pref.getString("com.mwork.payment.lang", "en");
        Util.setLanguage(this, this.lang);

        this.imageBack = (ImageView) findViewById(R.id.com_mwork_app_icon);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        this.progressBar = ((ProgressBar) findViewById(R.id.com_mwork_loading));
        progressBar.setVisibility(View.GONE);

        this.bankContainer = ((LinearLayout) findViewById(R.id.com_mwork_bank_container));

        Intent it = getIntent();
        Bundle bd = it.getExtras();
        String pload = bd.getString(Const.PAYLOAD_BUNDLE_KEY);
        if (pload != null) {
            payload = pload;
        }
        converter = (AmountConverter) bd.getSerializable(Const.AMOUNT_CONVERTER_BUNDLE_KEY);
        initArrayBtn(bd);
        initLayout();
        builder = new BankRequest.Builder();
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WEBVIEW_REQUEST_CODE) {
            finishActivity();
        }
    }

    private void finishActivity() {
        if (isSuccess) {
            Intent returnIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putSerializable(Const.RESULT_BANK_BUNDLE_KEY, bankTransaction);
            returnIntent.putExtras(extras);
            setResult(RESULT_OK, returnIntent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void initArrayBtn(Bundle bd) {
        int[] amountBank = bd.getIntArray(Const.BANK_AMOUNT_BUNDLE_KEY);
        arrayBtn = new ArrayList<>();

        for (int amount : amountBank) {
            ButtonView btn = new ButtonView(amount, 0, converter.bankAmountToString(amount));
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

            bankContainer.addView(view);
        }
    }

    private void finishActivityWithError(Throwable e) {
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
            BankRequest bank = builder.setAmount(amount)
                    .setListener(bankListener)
                    .setPayload(payload)
                    .build();
            bank.execute();
        }
    }
}
