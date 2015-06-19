package vn.mog.app360.sdk.payment.ui;

import vn.mog.app360.sdk.payment.data.CardTransaction;
import vn.mog.app360.sdk.payment.data.SmsTransaction;
import vn.mog.app360.sdk.payment.ui.R;

public class Vendor {
    private String name;
    private int flagRes;

    public Vendor(String name, int flagRes) {
        this.name = name;
        this.flagRes = flagRes;
    }

    public int getFlagRes() {
        return this.flagRes;
    }

    public String getVendor() {
        return this.name;
    }

    public static Vendor[] SMSVENDORS = new Vendor[]{
            new Vendor(SmsTransaction.SMSVendor.VINAPHONE.toString(), R.drawable.logo_vinaphone),
            new Vendor(SmsTransaction.SMSVendor.VIETTEL.toString(), R.drawable.logo_viettel),
            new Vendor(SmsTransaction.SMSVendor.MOBIFONE.toString(), R.drawable.logo_mobifone)
    };
    public static Vendor[] CARDVENDORS = new Vendor[]{
            new Vendor(CardTransaction.CardVendor.VINAPHONE.toString(), R.drawable.logo_vinaphone),
            new Vendor(CardTransaction.CardVendor.VIETTEL.toString(), R.drawable.logo_viettel),
            new Vendor(CardTransaction.CardVendor.MOBIFONE.toString(), R.drawable.logo_mobifone),
            new Vendor(CardTransaction.CardVendor.GATE.toString(), R.drawable.gate),
            new Vendor(CardTransaction.CardVendor.ZING.toString(), R.drawable.zing),
            new Vendor(CardTransaction.CardVendor.VCOIN.toString(), R.drawable.vcoin),
            new Vendor(CardTransaction.CardVendor.BIT.toString(), R.drawable.bit),
    };
}
