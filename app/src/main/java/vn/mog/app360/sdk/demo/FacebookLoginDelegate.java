package vn.mog.app360.sdk.demo;

import android.app.Activity;
import android.content.Intent;

import com.facebook.Session;
import com.facebook.SessionState;

import vn.mog.app360.sdk.demo.logger.Log;

public class FacebookLoginDelegate {
    private static final String TAG = "FacebookLoginDelegate";
    private final int requestCode;
    private final Activity activity;

    public FacebookLoginDelegate(final Activity activity, final int requestCode) {
        this.activity = activity;
        this.requestCode = requestCode;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Receiving a result from Facebook Login
        Session.getActiveSession()
                .onActivityResult(activity, requestCode, resultCode, data);
    }

    public void loginFacebook(final FacebookListener listener) {
        Session session = Session.openActiveSessionFromCache(activity);
        if (session == null) {
            session = Session.getActiveSession();
        }
        if (session == null) {
            session = Session.openActiveSessionFromCache(activity);
        }
        if (session == null || session.isClosed()) {
            session = new Session(activity);
            Session.setActiveSession(session);
        }
        if (!session.isOpened()) {
            Log.d(TAG, session.getState().toString());

            Session.StatusCallback statusCallback = new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState sessionState, Exception e) {
                    Log.d(TAG, sessionState.toString());
                    if (sessionState.isOpened()) {
                        listener.onLogin(session.getAccessToken());
                    }
                }
            };
            Session.OpenRequest openRequest = new Session.OpenRequest(activity)
                    .setRequestCode(requestCode)
                    .setCallback(statusCallback);
            session.openForRead(openRequest);
        } else {
            listener.onLogin(session.getAccessToken());
        }
    }

    public void logoutFacebook() {
        Session session = Session.getActiveSession();
        if (session != null) {
            session.closeAndClearTokenInformation();
        }
    }

    interface FacebookListener {
        void onLogin(String accessToken);
    }
}