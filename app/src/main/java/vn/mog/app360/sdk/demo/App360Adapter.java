package vn.mog.app360.sdk.demo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vn.mog.app360.sdk.scopedid.Profile;

import static android.support.v7.widget.RecyclerView.Adapter;

/**
* Created by h1volt3 on 1/6/15.
*/
class App360Adapter extends Adapter<App360Adapter.ViewHolder> {
    List<Profile> profiles = new ArrayList<>(0);
    private LoginActivity loginActivity;

    public App360Adapter(final LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    public void addProfile(Profile profile) {
        if (profile == null) {
            return;
        }
        boolean matchFound = false;
        for (Profile addedProfile : this.profiles) {
            if (addedProfile.getService().equals(profile.getService())) {
                matchFound = true;
            }
        }
        if (!matchFound) {
            this.profiles.add(0, profile);
            this.notifyItemInserted(0);
        }
    }

    public void removeProfile(String profileType) {
        int i;
        for (i = 0; i < this.profiles.size(); i++) {
            if (this.profiles.get(i).getService().equals(profileType)) {
                this.profiles.remove(i);
                this.notifyItemRemoved(i);
                break;
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int index) {
        final Profile profile = this.profiles.get(index);

        viewHolder.textView.setText(profile.getFullName());
        viewHolder.actionView.setText("UNLINK " + profile.getService().toUpperCase());
        viewHolder.actionView.setOnClickListener(new LoginActivity.ProfileOnClickListener(loginActivity, profile));

        Picasso.with(loginActivity).load(profile.getProfileImage()).transform(new CircleTransformation()).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return this.profiles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView actionView;
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.my_text_view);
            this.imageView = (ImageView) itemView.findViewById(R.id.profile_image);
            this.actionView = (TextView) itemView.findViewById(R.id.login_btn);
        }
    }
}
