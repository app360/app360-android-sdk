package vn.mog.app360.sdk.payment.ui.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import vn.mog.app360.sdk.payment.ui.R;

public class UIUtil {
    public static Map<String, Typeface> typefaceCache = new HashMap();

    private static Typeface fileStreamTypeface(Context context, int resource) {
        Typeface tf = null;

        InputStream is = context.getResources().openRawResource(resource);
        String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/gmg_underground_tmp";
        File f = new File(path);
        if ((!(f.exists())) && (!(f.mkdirs()))) {
            return null;
        }

        String outPath = path + "/tmp.raw";
        try {
            byte[] buffer = new byte[is.available()];
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(outPath));

            int l = 0;
            while ((l = is.read(buffer)) > 0)
                bos.write(buffer, 0, l);

            bos.close();

            tf = Typeface.createFromFile(outPath);

            File f2 = new File(outPath);
            f2.delete();
        } catch (IOException e) {
            return null;
        }

        return tf;
    }

    public static void setTypeface(AttributeSet attrs, TextView textView) {
        Context context = textView.getContext();

        TypedArray values = context.obtainStyledAttributes(attrs,
                R.styleable.MworkTextView);
        String typefaceName = values.getString(0);

        if (typefaceCache.containsKey(typefaceName)) {
            textView.setTypeface((Typeface) typefaceCache.get(typefaceName));
        } else {
            Typeface typeface;
            try {
                typeface = fileStreamTypeface(context, R.raw.roboto_light);
            } catch (Exception e) {
                return;
            }

            typefaceCache.put(typefaceName, typeface);
            textView.setTypeface(typeface);
        }
    }

    public static void setButtonTypeface(AttributeSet attrs, Button btn) {
        Context context = btn.getContext();

        TypedArray values = context.obtainStyledAttributes(attrs,
                R.styleable.MworkTextView);
        String typefaceName = values.getString(0);

        if (typefaceCache.containsKey(typefaceName)) {
            btn.setTypeface((Typeface) typefaceCache.get(typefaceName));
        } else {
            Typeface typeface;
            try {
                typeface = fileStreamTypeface(context, R.raw.roboto_light);
            } catch (Exception e) {
                Log.v("TypeFace", typefaceName + " not found");
                return;
            }
            typefaceCache.put(typefaceName, typeface);
            btn.setTypeface(typeface);
        }
    }
}
