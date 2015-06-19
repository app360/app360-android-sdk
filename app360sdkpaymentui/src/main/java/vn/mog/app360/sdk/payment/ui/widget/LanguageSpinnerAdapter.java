package vn.mog.app360.sdk.payment.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import vn.mog.app360.sdk.payment.ui.R;

public class LanguageSpinnerAdapter extends ArrayAdapter<LanguageSpinnerAdapter.MworkLanguage> {
    private List<MworkLanguage> langs;
    private LayoutInflater inflater;

    public LanguageSpinnerAdapter(Context context, int textViewResourceId,
                                  List<MworkLanguage> objects) {
        super(context, textViewResourceId, objects);
        this.langs = objects;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = this.inflater.inflate(
                    R.layout.com_mwork_icon_spinner_item, null);
            holder = new ViewHolder();
            holder.name = ((MworkTextView) convertView
                    .findViewById(R.id.com_mwork_text_lang));
            holder.flag = ((ImageView) convertView
                    .findViewById(R.id.com_mwork_img_flag));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MworkLanguage lang = (MworkLanguage) getItem(position);
        holder.name.setText(lang.getName());
        holder.flag.setImageResource(lang.getFlagRes());
        return convertView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        DropDownViewHolder holder = null;
        if (convertView == null) {
            convertView = this.inflater.inflate(
                    R.layout.com_mwork_icon_spinner_dropdown_item, null);
            holder = new DropDownViewHolder();
            holder.name = ((MworkCheckedTextView) convertView
                    .findViewById(R.id.com_mwork_text_lang));
            holder.flag = ((ImageView) convertView
                    .findViewById(R.id.com_mwork_img_flag));
            convertView.setTag(holder);
        } else {
            holder = (DropDownViewHolder) convertView.getTag();
        }
        MworkLanguage lang = (MworkLanguage) getItem(position);
        holder.name.setText(lang.getName());
        holder.flag.setImageResource(lang.getFlagRes());
        return convertView;
    }

    public static class MworkLanguage {
        private String name;
        private int flagRes;

        public MworkLanguage(String name, int flagRes) {
            this.name = name;
            this.flagRes = flagRes;
        }

        public int getFlagRes() {
            return this.flagRes;
        }

        public String getName() {
            return this.name;
        }
    }

    private class DropDownViewHolder {
        MworkCheckedTextView name;
        ImageView flag;
    }

    private class ViewHolder {
        MworkTextView name;
        ImageView flag;
    }
}