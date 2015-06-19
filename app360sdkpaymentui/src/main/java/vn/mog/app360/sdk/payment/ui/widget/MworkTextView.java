package vn.mog.app360.sdk.payment.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import vn.mog.app360.sdk.payment.ui.util.UIUtil;

public class MworkTextView extends TextView {
    public MworkTextView(Context context) {
        super(context);
    }

    public MworkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!(isInEditMode()))
            UIUtil.setTypeface(attrs, this);
    }

    public MworkTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!(isInEditMode()))
            UIUtil.setTypeface(attrs, this);
    }
}