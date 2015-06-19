package vn.mog.app360.sdk.payment.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

import vn.mog.app360.sdk.payment.ui.util.UIUtil;

public class MworkCheckedTextView extends CheckedTextView {
    public MworkCheckedTextView(Context context) {
        super(context);
    }

    public MworkCheckedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!(isInEditMode()))
            UIUtil.setTypeface(attrs, this);
    }

    public MworkCheckedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!(isInEditMode()))
            UIUtil.setTypeface(attrs, this);
    }
}