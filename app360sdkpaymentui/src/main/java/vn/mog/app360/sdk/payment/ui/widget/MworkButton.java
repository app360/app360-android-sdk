package vn.mog.app360.sdk.payment.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import vn.mog.app360.sdk.payment.ui.util.UIUtil;

public class MworkButton extends Button {
    public MworkButton(Context context) {
        super(context);
    }

    public MworkButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!(isInEditMode()))
            UIUtil.setButtonTypeface(attrs, this);
    }

    public MworkButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!(isInEditMode()))
            UIUtil.setButtonTypeface(attrs, this);
    }
}