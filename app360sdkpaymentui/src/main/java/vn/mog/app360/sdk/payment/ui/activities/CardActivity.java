package vn.mog.app360.sdk.payment.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.mog.app360.sdk.payment.CardRequest;
import vn.mog.app360.sdk.payment.data.CardTransaction;
import vn.mog.app360.sdk.payment.interfaces.CardRequestListener;
import vn.mog.app360.sdk.payment.ui.AmountConverter;
import vn.mog.app360.sdk.payment.ui.Vendor;
import vn.mog.app360.sdk.payment.ui.util.ButtonView;
import vn.mog.app360.sdk.payment.ui.widget.VendorSpinerAdapter;
import vn.mog.app360.sdk.payment.utils.Const;
import vn.mog.app360.sdk.payment.utils.Util;
import vn.mog.app360.sdk.payment.ui.R;

public class CardActivity extends Activity {
    private Button payBtn;
    private EditText cardSerialEdit;
    private EditText cardCodeEdit;
    private Spinner vendorSelect;
    private ImageView backBtn;
    private SharedPreferences pref;
    private VendorSpinerAdapter vendorAdapter;
    private CardRequest.Builder builder;
    private AmountConverter converter;
    private String lang;
    private Boolean onclick = true;
    private List<ButtonView> descButtons;
    /**
     * Contains descButtons
     */
    private LinearLayout descContainer;
    private String payload = "";
    private CardRequestListener cardListener = new CardRequestListener() {
        @Override
        public void onSuccess(CardTransaction transaction) {
            Log.d("", "transacton " + transaction);
            Intent returnIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putSerializable(Const.RESULT_CARD_BUNDLE_KEY, transaction);
            returnIntent.putExtras(extras);
            setResult(RESULT_OK, returnIntent);
            finish();
        }

        @Override
        public void onFailure(Throwable e) {
            Intent returnIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putSerializable(Const.RESULT_ERROR_BUNDLE_KEY, e);
            returnIntent.putExtras(extras);
            setResult(Const.RESULT_ERROR, returnIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_mwork_activity_card);
        // FIXME common class for shared pref
        this.pref = getSharedPreferences("com.mwork.payment.pref", 0);
        this.lang = this.pref.getString("com.mwork.payment.lang", "en");
        Util.setLanguage(this, this.lang);

        payBtn = (Button) findViewById(R.id.com_mwork_btn_pay);
        payBtn.setOnClickListener(new PayBtnOnClickListener());

        cardSerialEdit = (EditText) findViewById(R.id.com_mwork_edit_card_serial);
        cardCodeEdit = (EditText) findViewById(R.id.com_mwork_edit_card_code);

        backBtn = (ImageView) findViewById(R.id.com_mwork_app_icon);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        vendorSelect = (Spinner) findViewById(R.id.com_mwork_spn_card);
        vendorAdapter = new VendorSpinerAdapter(this, R.layout.com_mwork_vendor_spinner_item, Arrays.asList(Vendor.CARDVENDORS));
        vendorSelect.setAdapter(vendorAdapter);

        Intent it = getIntent();
        Bundle bd = it.getExtras();
        converter = (AmountConverter) bd.getSerializable(Const.AMOUNT_CONVERTER_BUNDLE_KEY);
        payload = bd.getString(Const.PAYLOAD_BUNDLE_KEY);

        descButtons = new ArrayList<>();
        for (int cardAmount : bd.getIntArray(Const.CARD_AMOUNT_BUNDLE_KEY)) {
            ButtonView btn = new ButtonView(cardAmount, 0, converter.cardAmountToString(cardAmount));
            descButtons.add(btn);
        }

        initLayoutInfo();
    }

    private void initLayoutInfo() {
        View info = findViewById(R.id.com_mwork_layout_info);
        descContainer = ((LinearLayout) info.findViewById(R.id.com_mwork_des_container));
        final ImageView triangle = (ImageView) info.findViewById(R.id.com_mwork_triangle);

        RelativeLayout btnInfo = (RelativeLayout) info.findViewById(R.id.com_mwork_btn_show_info);
        btnInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onclick) {
                    onclick = true;
                    triangle.setImageResource(R.drawable.com_mwork_ic_down);
                    descContainer.setVisibility(View.VISIBLE);
                } else {
                    onclick = false;
                    triangle.setImageResource(R.drawable.com_mwork_ic_triangle);
                    descContainer.setVisibility(View.GONE);
                }
            }
        });

        initDescButtons();
    }

    private void initDescButtons() {
        if (descButtons.size() > 0) {

            for (int i = 0; i < descButtons.size(); i++) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.com_mwork_des_button, null);
                TextView sendButton = (TextView) view
                        .findViewById(R.id.com_mwork_text_number);
                final int amount = descButtons.get(i).getmAmount();
                sendButton.setText(amount + "VND");
                TextView amountButton = (TextView) view
                        .findViewById(R.id.com_mwork_text_amount);
                amountButton.setText(descButtons.get(i).getmDes());

                descContainer.addView(view);
            }
        }
    }

    private class PayBtnOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            String cardCodeText = cardCodeEdit.getText().toString();
            String cardSerialText = cardSerialEdit.getText().toString();
            Vendor vendorText = (Vendor)
                    vendorSelect.getAdapter().getItem(vendorSelect.getSelectedItemPosition());

            if (TextUtils.isEmpty(cardCodeText)) {
                Toast.makeText(CardActivity.this, R.string.empty_card_code,
                        Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(cardSerialText)) {
                Toast.makeText(CardActivity.this, R.string.empty_card_serial,
                        Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(vendorText.getVendor())) {
                Toast.makeText(CardActivity.this, R.string.empty_card_vendor,
                        Toast.LENGTH_LONG).show();
            } else {
                builder = new CardRequest.Builder();
                CardRequest card = builder.setCardCode(cardCodeText)
                        .setCardSerial(cardSerialText)
                        .setCardVendor(CardTransaction.CardVendor.parseString(vendorText.getVendor()))
                        .setPayload(payload)
                        .setListener(cardListener)
                        .build();
                card.execute();
            }
        }
    }
}
