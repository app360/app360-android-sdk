package vn.mog.app360.sdk.payment.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import vn.mog.app360.sdk.payment.ui.util.UIUtil;

public class MworkEditText extends EditText {
    public MworkEditText(Context context) {
        super(context);
    }

    public MworkEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!(isInEditMode()))
            UIUtil.setTypeface(attrs, this);
    }

    public MworkEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!(isInEditMode()))
            UIUtil.setTypeface(attrs, this);
    }
}