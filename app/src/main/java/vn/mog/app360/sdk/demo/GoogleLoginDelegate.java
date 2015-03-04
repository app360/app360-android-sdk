package vn.mog.app360.sdk.demo;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.widget.Toast;

import vn.mog.app360.sdk.demo.logger.Log;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GoogleLoginDelegate {
    private static final String TAG = "GoogleLoginDelegate";
    private final Activity activity;
    private String email;
    GetAuthTokenTask.GetAuthTokenTaskCallback authTokenCallback;

    public GoogleLoginDelegate(final Activity activity) {
        this.activity = activity;
    }

    void getUsername() {
        new GetAuthTokenTask(activity, email, authTokenCallback).execute();
    }

    public void loginGoogle(final GoogleLoginListener listener,
                            final int pickAccountRequestCode,
                            final int recoverPlayServicesRequestCode,
                            final int recoverAuthRequestCode) {
        this.authTokenCallback = new GetAuthTokenTask.GetAuthTokenTaskCallback() {
            @Override
            public void onSuccess(String token) {
                listener.onLogin(token);
            }

            @Override
            public void onFailure(final Exception e) {
                // Because this call comes from the AsyncTask, we must ensure that the following
                // code instead executes on the UI thread.
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (e instanceof GooglePlayServicesAvailabilityException) {
                            // The Google Play services APK is old, disabled, or not present.
                            // Show a dialog created by Google Play services that allows
                            // the user to update the APK
                            int statusCode = ((GooglePlayServicesAvailabilityException) e)
                                    .getConnectionStatusCode();
                            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                                    activity,
                                    recoverPlayServicesRequestCode);
                            dialog.show();
                        } else if (e instanceof UserRecoverableAuthException) {
                            // Unable to authenticate, such as when the user has not yet granted
                            // the app access to the account, but the user can fix this.
                            // Forward the user to an activity in Google Play services.
                            Intent intent = ((UserRecoverableAuthException) e).getIntent();
                            activity.startActivityForResult(intent, recoverAuthRequestCode);
                        } else {
                            Log.d(TAG, e.getMessage(), e);
                        }
                    }
                });
            }
        };

        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        activity.startActivityForResult(intent, pickAccountRequestCode);
    }

    void handlePickAccountResult(int resultCode, Intent data) {
        // Receiving a result from AccountPicker
        if (resultCode == Activity.RESULT_OK) {
            email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            getUsername();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Notify users that they must pick an account to proceed.
            Toast.makeText(activity, R.string.pick_account, Toast.LENGTH_SHORT).show();
        }
    }

    void handleErrorRecovery() {
        getUsername();
    }

    interface GoogleLoginListener {
        void onLogin(String accessToken);
    }
}