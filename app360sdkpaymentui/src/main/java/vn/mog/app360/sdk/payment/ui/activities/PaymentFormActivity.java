package vn.mog.app360.sdk.payment.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.mog.app360.sdk.payment.data.BankTransaction;
import vn.mog.app360.sdk.payment.data.CardTransaction;
import vn.mog.app360.sdk.payment.data.SmsTransaction;
import vn.mog.app360.sdk.payment.data.Transaction;
import vn.mog.app360.sdk.payment.interfaces.PaymentFormListener;
import vn.mog.app360.sdk.payment.ui.AmountConverter;
import vn.mog.app360.sdk.payment.ui.PaymentForm;
import vn.mog.app360.sdk.payment.ui.widget.LanguageSpinnerAdapter;
import vn.mog.app360.sdk.payment.utils.Const;
import vn.mog.app360.sdk.payment.utils.Util;
import vn.mog.app360.sdk.payment.ui.R;

public class PaymentFormActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private AmountConverter converter;
    private int[] smsAmounts;
    private int[] cardAmounts;
    private int[] bankAmounts;
    private String payload;
    private SharedPreferences pref;
    private String lang;
    private SmsTransaction smsTransaction;
    private BankTransaction bankTransaction;
    private CardTransaction cardTransaction;
    private PaymentFormListener listener = new PaymentFormListener() {
        @Override
        public void onFinish(Transaction transaction) {
            if (transaction instanceof SmsTransaction) {
                smsTransaction = (SmsTransaction) transaction;
            } else if (transaction instanceof CardTransaction) {
                cardTransaction = (CardTransaction) transaction;
            } else if (transaction instanceof BankTransaction) {
                bankTransaction = (BankTransaction) transaction;
            }
            finishActivity();
        }

        @Override
        public void onCancel() {
            Log.d("PaymentFormActivity", "onCancel");
        }

        @Override
        public void onError(Throwable e) {
            Log.d("PaymentFormActivity", "onError");
            finishActivity(e);
        }
    };
    private PaymentForm paymentForm;
    private PaymentForm.Builder formBuilder;

    private void finishActivity(Throwable e) {
        Intent returnIntent = new Intent();
        Bundle extras = new Bundle();
        extras.putSerializable(Const.RESULT_ERROR_BUNDLE_KEY, e);
        returnIntent.putExtras(extras);
        setResult(Const.RESULT_ERROR, returnIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.pref = getSharedPreferences("com.mwork.payment.pref", 0);
        this.lang = this.pref.getString("com.mwork.payment.lang", "en");
        Util.setLanguage(this, this.lang);

        setContentView(R.layout.com_mwork_activity_allform);

        formBuilder = new PaymentForm.Builder(this);

        ImageView backBtn = (ImageView) findViewById(R.id.com_mwork_app_icon);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent it = getIntent();
        Bundle bd = it.getExtras();
        payload = bd.getString(Const.PAYLOAD_BUNDLE_KEY);
        converter = (AmountConverter) bd.getSerializable(Const.AMOUNT_CONVERTER_BUNDLE_KEY);
        cardAmounts = bd.getIntArray(Const.CARD_AMOUNT_BUNDLE_KEY);
        bankAmounts = bd.getIntArray(Const.BANK_AMOUNT_BUNDLE_KEY);
        smsAmounts = bd.getIntArray(Const.SMS_AMOUNT_BUNDLE_KEY);

        String des = bd.getString(Const.APP_DESCRIPTION_BUNDLE_KEY);
        if (des != null) {
            ((TextView) findViewById(R.id.appDes)).setText(des);
        }

        initCardForm();
        initSmsForm();
        initBankForm();
        initLanguage();
    }

    private void initCardForm() {
        LinearLayout cardForm = (LinearLayout) findViewById(R.id.method_card);
        cardForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentForm = formBuilder
                        .setPayload(payload)
                        .setConverter(converter)
                        .setCardAmounts(cardAmounts)
                        .setListener(listener)
                        .build();
                paymentForm.showCardForm();
            }
        });
    }

    private void initSmsForm() {
        LinearLayout smsForm = (LinearLayout) findViewById(R.id.method_sms);
        smsForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentForm = formBuilder
                        .setPayload(payload)
                        .setConverter(converter)
                        .setSMSAmounts(smsAmounts)
                        .setListener(listener)
                        .build();
                paymentForm.showSmsForm();
            }
        });
    }

    private void initBankForm() {
        LinearLayout bankForm = (LinearLayout) findViewById(R.id.method_bank);
        bankForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentForm = formBuilder
                        .setPayload(payload)
                        .setConverter(converter)
                        .setBankAmounts(bankAmounts)
                        .setListener(listener)
                        .build();
                paymentForm.showBankForm();
            }
        });
    }

    private void initLanguage() {
        List langs = new ArrayList();
        langs.add(new LanguageSpinnerAdapter.MworkLanguage(getString(R.string.com_mwork_lang_en), R.drawable.ic_flag_us));
        langs.add(new LanguageSpinnerAdapter.MworkLanguage(getString(R.string.com_mwork_lang_vn), R.drawable.ic_flag_vn));
        LanguageSpinnerAdapter langAdapter = new LanguageSpinnerAdapter(this,
                R.layout.com_mwork_icon_spinner_item, langs);

        View logo = findViewById(R.id.com_mwork_logo);
        Spinner spnLang = (Spinner) logo.findViewById(R.id.com_mwork_spn_lang);
        spnLang.setAdapter(langAdapter);

        if (TextUtils.equals(this.lang, "en")) {
            spnLang.setSelection(0);
        } else if (TextUtils.equals(this.lang, "vi")) {
            spnLang.setSelection(1);
        }
        spnLang.setOnItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        paymentForm.onActivityResult(requestCode, resultCode, data);
    }

    private void finishActivity() {
        Intent returnIntent = new Intent();
        Bundle bd = new Bundle();
        bd.putSerializable(Const.RESULT_CARD_BUNDLE_KEY, cardTransaction);
        bd.putSerializable(Const.RESULT_SMS_BUNDLE_KEY, smsTransaction);
        bd.putSerializable(Const.RESULT_BANK_BUNDLE_KEY, bankTransaction);
        returnIntent.putExtras(bd);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int arg2, long arg3) {
        Intent intent;
        SharedPreferences.Editor editor = this.pref.edit();

        if ((TextUtils.equals(this.lang, "en")) && (arg2 == 0))
            return;

        if ((TextUtils.equals(this.lang, "vi")) && (arg2 == 1))
            return;

        switch (arg2) {
            case 0:
                editor.putString("com.mwork.payment.lang", "en");
                editor.commit();
                Util.setLanguage(this, "en");
                intent = getIntent();
                startActivity(intent);
                finish();
                break;
            case 1:
                editor.putString("com.mwork.payment.lang", "vi");
                editor.commit();
                Util.setLanguage(this, "vi");
                intent = getIntent();
                startActivity(intent);
                finish();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
