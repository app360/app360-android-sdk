package vn.mog.app360.sdk.payment.ui.util;

import java.io.Serializable;

public class ButtonView implements Serializable {
    private int mAmount;
    private int mNumber;
    private String mDes;

    public ButtonView(int amount, int number, String des) {
        this.mAmount = amount;
        this.mNumber = number;
        this.mDes = des;
    }

    public int getmAmount() {
        return mAmount;
    }

    public void setmAmount(int mAmount) {
        this.mAmount = mAmount;
    }

    public int getmNumber() {
        return mNumber;
    }

    public void setmNumber(int mNumber) {
        this.mNumber = mNumber;
    }

    public String getmDes() {
        return mDes;
    }

    public void setmDes(String mDes) {
        this.mDes = mDes;
    }
}
