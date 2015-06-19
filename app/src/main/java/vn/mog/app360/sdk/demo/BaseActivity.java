package vn.mog.app360.sdk.demo;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

import vn.mog.app360.sdk.demo.logger.Log;
import vn.mog.app360.sdk.demo.logger.LogFragment;
import vn.mog.app360.sdk.demo.logger.LogWrapper;
import vn.mog.app360.sdk.demo.logger.MessageOnlyLogFilter;

public abstract class BaseActivity extends ActionBarActivity {
    private static final String TAG = "BaseActivity";
    private static final Map<Integer, Class<? extends BaseActivity>> activityIndex = new HashMap<>();

    static {
        activityIndex.put(0, LoginActivity.class);
        activityIndex.put(1, PaymentActivity.class);
    }

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Crashlytics.start(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initializeLogging();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item,
                new String[]{"Login", "Payment"}));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class activity = activityIndex.get(position);
                drawerLayout.closeDrawers();
                if (!activity.isInstance(BaseActivity.this)) {
                    startActivity(new Intent(getApplicationContext(), activity));
                    finish();
                }
            }
        });

        drawerToggler = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.open_drawer,
                R.string.close_drawer
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggler);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggler.syncState();
    }

    /**
     * Create a chain of targets that will receive log data
     */
    private void initializeLogging() {

        // Using Log, front-end to the logging chain, emulates
        // android.util.log method signatures.

        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        // A filter that strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment mLogFragment = (LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_fragment);
        mLogFragment.getLogView().setBackgroundColor(Color.GRAY);
        msgFilter.setNext(mLogFragment.getLogView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggler.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggler.onConfigurationChanged(newConfig);
    }
}
