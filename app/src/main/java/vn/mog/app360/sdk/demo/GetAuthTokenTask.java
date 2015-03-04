package vn.mog.app360.sdk.demo;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;

import java.io.IOException;

/**
 */
public class GetAuthTokenTask extends AsyncTask<Void, Void, Void> {
    private Activity activity;
    private String email;
    private GetAuthTokenTaskCallback cb;

    public GetAuthTokenTask(Activity activity, String email, GetAuthTokenTaskCallback cb) {
        this.activity = activity;
        this.email = email;
        this.cb = cb;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String token = fetchToken();
            if (token != null) {
                cb.onSuccess(token);
            } else {
                cb.onFailure(new NullPointerException());
            }
        } catch (Exception e) {
            cb.onFailure(e);
        }
        return null;
    }

    private String fetchToken() throws GoogleAuthException, IOException {
        String scope = "oauth2:profile email";
        return GoogleAuthUtil.getToken(activity, email, scope);
    }

    public static interface GetAuthTokenTaskCallback {
        public void onSuccess(String token);

        public void onFailure(Exception e);
    }
}
