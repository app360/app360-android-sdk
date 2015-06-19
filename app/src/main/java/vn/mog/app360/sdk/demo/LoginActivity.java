package vn.mog.app360.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;

import vn.mog.app360.sdk.App360SDK;
import vn.mog.app360.sdk.InitListener;
import vn.mog.app360.sdk.callback.App360Callback;
import vn.mog.app360.sdk.demo.logger.Log;
import vn.mog.app360.sdk.scopedid.BuildHelper;
import vn.mog.app360.sdk.scopedid.Profile;
import vn.mog.app360.sdk.scopedid.SaveCallback;
import vn.mog.app360.sdk.scopedid.ScopedUser;
import vn.mog.app360.sdk.scopedid.SessionManager;
import vn.mog.app360.sdk.scopedid.SessionService;
import vn.mog.app360.sdk.scopedid.data.App360Properties;
import vn.mog.app360.sdk.scopedid.data.Campaign;
import vn.mog.app360.sdk.scopedid.data.UpdateUrl;

import static vn.mog.app360.sdk.demo.FacebookLoginDelegate.FacebookListener;
import static vn.mog.app360.sdk.demo.GoogleLoginDelegate.GoogleLoginListener;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final int REQUEST_CODE_FACEBOOK_LOGIN = 1001;
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
    private static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1003;
    private final FacebookLoginDelegate facebookLoginDelegate = new FacebookLoginDelegate(this, REQUEST_CODE_FACEBOOK_LOGIN);
    private final GoogleLoginDelegate googleLoginDelegate = new GoogleLoginDelegate(this);
    private String linkService;
    private final SaveCallback linkCallback = new SaveCallback() {
        @Override
        public void onSuccess() {
            ScopedUser currentUser = ScopedUser.getCurrentUser();
            Log.i(TAG, "Link succeeded");
            logUserChannel(currentUser);
            if ("facebook".equals(linkService)) {
                adapter.addProfile(currentUser.getFacebookProfile());
            } else {
                adapter.addProfile(currentUser.getGoogleProfile());
            }
        }

        @Override
        public void onFailure(Exception e) {
            Log.d(TAG, "Save failed", e);
            Toast.makeText(LoginActivity.this, "Link denied", Toast.LENGTH_SHORT).show();
        }
    };
    private final SaveCallback unlinkCallback = new SaveCallback() {
        @Override
        public void onSuccess() {
            ScopedUser currentUser = ScopedUser.getCurrentUser();
            Log.i(TAG, "Unlink succeeded");
            logUserChannel(currentUser);

            adapter.removeProfile(linkService);
        }

        @Override
        public void onFailure(Exception e) {
            Log.d(TAG, "Save failed", e);
        }
    };
    private final FacebookListener facebookListener = new FacebookListener() {
        @Override
        public void onLogin(String accessToken) {
            if (isLinking()) {
                LoginActivity.this.linkService = "facebook";
                ScopedUser.getCurrentUser().linkFacebook(accessToken, linkCallback);
            } else {
                SessionManager.createSession("facebook", accessToken, new SessionCallback());
            }
        }

        private boolean isLinking() {
            return SessionManager.getCurrentSession() != null;
        }
    };
    private final GoogleLoginListener googleLoginListener = new GoogleLoginListener() {
        @Override
        public void onLogin(String accessToken) {
            if (isLinking()) {
                LoginActivity.this.linkService = "google";
                ScopedUser.getCurrentUser().linkGoogle(accessToken, linkCallback);
            } else {
                SessionManager.createSession("google", accessToken, new SessionCallback());
            }
        }

        private boolean isLinking() {
            return SessionManager.getCurrentSession() != null;
        }
    };
    private App360Adapter adapter;
    private MaterialDialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.title_activity_login);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new App360Adapter(this);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bottom);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        toolbar.inflateMenu(R.menu.menu_login);

        App360SDK.initialize("1004015958365193309493", "4QkZUK5MjaOVANqV7Vr4zZB2SdEai1ggtaVibysugqIurO28", getApplicationContext(), new InitListener() {
            @Override
            public void onSuccess() {
                SessionService.Session session = SessionManager.getCurrentSession();
                if (SessionManager.getCurrentSession() == null) {
                    loginDialog = buildLoginDialog().show();
                } else {
                    Log.d(TAG, "Current session: " + session);

                    ScopedUser currentUser = ScopedUser.getCurrentUser();
                    logUserChannel(currentUser);
                    logBuildChannel();

                    adapter.addProfile(currentUser.getFacebookProfile());
                    adapter.addProfile(currentUser.getGoogleProfile());

                    App360SDK.getUpdateUrl(new App360Callback<UpdateUrl>() {
                        @Override
                        public void onSuccess(UpdateUrl updateUrl) {
                            if (updateUrl != null) {
                                Log.d(TAG, "Update url " + updateUrl.getUpdateUrl());
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.d(TAG, "Failed to retrieve update URL " + t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    private MaterialDialog.Builder buildLoginDialog() {
        CharSequence[] items = {"Google+", "Facebook", "Anonymous"};

        MaterialDialog.ListCallback callback = new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                if (text.equals("Anonymous")) {
                    SessionManager.createSession(null, new SessionCallback());
                } else if (text.equals("Facebook")) {
                    facebookLoginDelegate.loginFacebook(facebookListener);
                } else if (text.equals("Google+")) {
                    googleLoginDelegate.loginGoogle(googleLoginListener, REQUEST_CODE_PICK_ACCOUNT,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR, REQUEST_CODE_RECOVER_FROM_AUTH_ERROR);
                }
            }
        };

        return new MaterialDialog.Builder(this)
                .title("Login As...")
                .items(items)
                .itemsCallback(callback);
    }

    private void logUserChannel(ScopedUser currentUser) {
        String scopedId = currentUser.getId();
        String channel = currentUser.getChannel();
        String subChannel = currentUser.getSubChannel();
        Log.d(TAG, getResources().getString(R.string.login_toast, scopedId, channel, subChannel));
    }

    private void logBuildChannel() {

        try {
            App360Properties properties = App360Properties.getProperties(this);
            Campaign campaign = properties.getCampaign();
            Log.d(TAG, String.format("Build channel is %s, build sub_channel is %s",
                    BuildHelper.getChannel(), BuildHelper.getSubChannel()));
            Log.d(TAG, String.format("Campaign utm_campaign : %s, utm_source is %s, utm_medium is %s, utm_content is %s, utm_term is %s",
                    campaign.getUtmCampaign(), campaign.getUtmSource(), campaign.getUtmMedium(), campaign.getUtmContent(), campaign.getUtmTerm()));
        } catch (IOException e) {
            Log.e(TAG, "Cannot read build's channel config");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_link_facebook) {
            facebookLoginDelegate.loginFacebook(facebookListener);
            return true;
        }

        if (id == R.id.action_link_google) {
            googleLoginDelegate.loginGoogle(googleLoginListener, REQUEST_CODE_PICK_ACCOUNT,
                    REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR, REQUEST_CODE_RECOVER_FROM_AUTH_ERROR);
            return true;
        }

        if (id == R.id.action_logout) {
            App360SDK.clearSession();
            adapter.removeProfile("facebook");
            adapter.removeProfile("google");
            if (loginDialog == null) {
                loginDialog = buildLoginDialog().build();
            }
            loginDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            googleLoginDelegate.handlePickAccountResult(resultCode, data);
        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            googleLoginDelegate.handleErrorRecovery();
        } else if (requestCode == REQUEST_CODE_FACEBOOK_LOGIN) {
            facebookLoginDelegate.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    static class ProfileOnClickListener implements View.OnClickListener {
        private final Profile profile;
        private LoginActivity loginActivity;

        public ProfileOnClickListener(LoginActivity loginActivity, Profile profile) {
            this.loginActivity = loginActivity;
            this.profile = profile;
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onclick" + profile.getService());
            if ("facebook".equals(profile.getService())) {
                loginActivity.facebookLoginDelegate.logoutFacebook();
                loginActivity.linkService = "facebook";
                ScopedUser.getCurrentUser().unlinkFacebook(loginActivity.unlinkCallback);
            } else if ("google".equals(profile.getService())) {
                loginActivity.linkService = "google";
                ScopedUser.getCurrentUser().unlinkGoogle(loginActivity.unlinkCallback);
            }
        }
    }

    private class SessionCallback implements SessionManager.SessionCallback {
        @Override
        public void onSuccess() {
            SessionService.Session session = SessionManager.getCurrentSession();
            Log.d(TAG, "Current session: " + session);

            ScopedUser currentUser = ScopedUser.getCurrentUser();
            logUserChannel(currentUser);
            logBuildChannel();

            adapter.addProfile(currentUser.getFacebookProfile());
            adapter.addProfile(currentUser.getGoogleProfile());
        }

        @Override
        public void onFailure(Exception e) {
            Log.d(TAG, "onFailure " + e);
        }
    }
}
