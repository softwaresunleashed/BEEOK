package com.unleashed.android.beeokunleashed.ui;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.unleashed.android.beeokunleashed.R;
import com.unleashed.android.beeokunleashed.adhosting.adbuddiz.AdBuddizAd;
import com.unleashed.android.beeokunleashed.adhosting.appnext.AppNextAd;
import com.unleashed.android.beeokunleashed.adhosting.appszoom.AppsZoomAd;
import com.unleashed.android.beeokunleashed.constants.Constants;
import com.unleashed.android.beeokunleashed.ui.fragements.AboutApp;
import com.unleashed.android.beeokunleashed.ui.fragements.CallBlocker;
import com.unleashed.android.beeokunleashed.ui.fragements.CallRecorder;
import com.unleashed.android.beeokunleashed.ui.fragements.Donate;
import com.unleashed.android.beeokunleashed.ui.fragements.ExitApp;
import com.unleashed.android.beeokunleashed.ui.fragements.HomePage;
import com.unleashed.android.beeokunleashed.ui.fragements.MicrophoneRecorder;
import com.unleashed.android.beeokunleashed.ui.fragements.RateAppOnGooglePlayStore;
import com.unleashed.android.beeokunleashed.ui.fragements.SMSBlocker;
import com.unleashed.android.beeokunleashed.ui.fragements.SendPromotionalEmail;
import com.unleashed.android.beeokunleashed.ui.fragements.TellFriendViaPersonalEmail;
import com.unleashed.android.beeokunleashed.ui.slidingdrawer.adapter.NavDrawerListAdapter;
import com.unleashed.android.beeokunleashed.ui.slidingdrawer.model.NavDrawerItem;

import java.util.ArrayList;

/* ActionBarActivity */ /* AppCompatActivity */
public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;


    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Now that we are about to close the app,
        // run Garbage collector to return unused memory to Android.
        // It is Android OS decision if the GC will run now or later point in time.
        //System.gc();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        if(getResources().getInteger(R.integer.host_ads)==1) {
            // Call this utility function to trigger initiating Appszoom Interstetial Ads
            AppsZoomAd.init_appszoom_ads(this, MainActivity.this, false);

            // Call this utility function to trigger initiating AdBuddiz Interstetial Ads
            AdBuddizAd.init_adbuddiz_ads(this, MainActivity.this, false);

            // Call this utility function to trigger initiating AppNext Interstetial Ads
            AppNextAd.init_appsnext_ads(this, MainActivity.this, false);
        }

        // Start creating the Slider bar
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();


        // adding nav drawer items to array
        // Home Page
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Mic Record
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Call Record
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Call Blocker Functionality
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // SMS Blocker Functionality
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // Rate us on Google Play Store
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        // Send Promotional Email
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
        // Tell a Friend
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
        // About App
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
        // Donate
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));
        // Exit App
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[10], navMenuIcons.getResourceId(10, -1)));
        //--> this is for adding a round text view , can be used for showing number of notifications pending   -- , true, "22"
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));


        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);



        // enabling action bar app icon and behaving it as toggle button
        // Sudhanshu : V.V.V. Important note : Since we are using v7 of ActionBarActivity (see the imports section)
        // hence, we need to use getSupportActionBar() instead of getActionBar(), else getActionBar() will return NULL

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);


        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.call_recorder);
        //getSupportActionBar().setTitle(R.string.app_name);




        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME| ActionBar.DISPLAY_SHOW_TITLE  | ActionBar.DISPLAY_USE_LOGO); // Customize if you need to

       // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO); // Customize if you need to

        // Set the Title & Icon of action bar when navigating between pages.
        getSupportActionBar().setTitle(navMenuTitles[0]);
        //getSupportActionBar().setIcon(R.drawable.ic_home);


        // Set ActionBar Color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFE96264")));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.call_recorder, //nav menu toggle icon
                R.string.drawer_open, // nav drawer open - description for accessibility
                R.string.drawer_close // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {

                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());



        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);     // Display the Home Page screen as default.
            //mDrawerLayout.openDrawer(mDrawerList); // Open the sliding menu as well for the first time
        }
    }


    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
     private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomePage();
                break;
            case 1:
                fragment = new MicrophoneRecorder();
                break;
            case 2:
                fragment = new CallRecorder();
                break;
            case 3:
                fragment = new CallBlocker();
                break;
            case 4:
                fragment = new SMSBlocker();
                break;
            case 5:
                fragment = new RateAppOnGooglePlayStore();
                break;
            case 6:
                fragment = new SendPromotionalEmail();
                break;
            case 7:
                fragment = new TellFriendViaPersonalEmail();
                break;
            case 8:
                fragment = new AboutApp();
                break;
            case 9:
                fragment = new Donate();
                break;
            case 10:
                fragment = new ExitApp();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);


            // Set the Title & Icon of action bar when navigating between pages.
            getSupportActionBar().setTitle(navMenuTitles[position]);
           // getSupportActionBar().setIcon(navMenuIcons.getResourceId(position, -1));

            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            // error in creating fragment
            Log.e(Constants.APP_NAME_TAG, "MainActivity.java:displayView() ... Error in creating fragment");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        switch (id){

            // Respond to the action bar's Up/Home button
            // Or menu icon in action bar.
            // https://developer.android.com/training/basics/actionbar/adding-buttons.html
            case R.id.action_show_sliding_menu:
            case android.R.id.home:
               // NavUtils.navigateUpFromSameTask(this);
                if ( this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
                    this.mDrawerLayout.closeDrawer(this.mDrawerList);
                }
                else {
                    this.mDrawerLayout.openDrawer(this.mDrawerList);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }




//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
//            if ( this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
//                this.mDrawerLayout.closeDrawer(this.mDrawerList);
//            }
//            else {
//                this.mDrawerLayout.openDrawer(this.mDrawerList);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }





}
