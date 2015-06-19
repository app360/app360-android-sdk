package vn.mog.app360.sdk.payment.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import vn.mog.app360.sdk.payment.ui.Vendor;
import vn.mog.app360.sdk.payment.ui.R;

/**
 * Created by lethiem on 2/7/15.
 */
public class VendorSpinerAdapter extends ArrayAdapter<Vendor> {
    private List<Vendor> vendors;
    private LayoutInflater inflater;

    public VendorSpinerAdapter(Context context, int resource, List<Vendor> objects) {
        super(context, resource, objects);
        this.vendors = objects;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = this.inflater.inflate(
                    R.layout.com_mwork_vendor_spinner_item, null);
            holder = new ViewHolder();
            holder.name = ((MworkTextView) convertView
                    .findViewById(R.id.com_mwork_text_vendor));
            holder.imageVendor = ((ImageView) convertView
                    .findViewById(R.id.com_mwork_img_vendor));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Vendor lang = (Vendor) getItem(position);
        holder.name.setText(lang.getVendor());
        holder.imageVendor.setImageResource(lang.getFlagRes());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        DropDownViewHolder holder = null;
        if (convertView == null) {
            convertView = this.inflater.inflate(
                    R.layout.com_mwork_vendor_spinner_dropdown_item, null);
            holder = new DropDownViewHolder();
            holder.name = ((MworkTextView) convertView
                    .findViewById(R.id.com_mwork_text_vendor));
            holder.imageVendor = ((ImageView) convertView
                    .findViewById(R.id.com_mwork_img_vendor));
            convertView.setTag(holder);
        } else {
            holder = (DropDownViewHolder) convertView.getTag();
        }
        Vendor lang = (Vendor) getItem(position);
        holder.name.setText(lang.getVendor());
        holder.imageVendor.setImageResource(lang.getFlagRes());
        return convertView;
    }

    private class DropDownViewHolder {
        MworkTextView name;
        ImageView imageVendor;
    }

    private class ViewHolder {
        MworkTextView name;
        ImageView imageVendor;
    }
}

