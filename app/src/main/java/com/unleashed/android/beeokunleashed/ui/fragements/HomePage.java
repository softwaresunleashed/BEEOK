package com.unleashed.android.beeokunleashed.ui.fragements;


import android.app.FragmentManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.unleashed.android.beeokunleashed.R;
import com.unleashed.android.beeokunleashed.adhosting.googleadmob.GoogleAdMob;
import com.unleashed.android.beeokunleashed.constants.Constants;
import com.unleashed.android.beeokunleashed.ui.MainActivity;
import com.unleashed.android.beeokunleashed.ui.customgrid.CustomGridAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePage extends Fragment {


    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private GridView grid;

    public HomePage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);

        if (getResources().getInteger(R.integer.host_ads) == 1) {
            // Initialize the google ads via common api.
            //GoogleAdMob.init_google_ads(rootView, R.id.adView_home_page);
            //GoogleAdMob.init_google_ads(rootView, R.id.adView_home_page2);
        }


        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.grid_items);
        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.grid_icons);
        // Draw GRID on screen
        draw_grid_view(rootView);

        return rootView;
    }

    private void draw_grid_view(View rootView) {

        CustomGridAdapter grid_adapter = new CustomGridAdapter(rootView.getContext(), navMenuTitles, navMenuIcons);
        grid = (GridView) rootView.findViewById(R.id.gridView_homepage);

        grid.setAdapter(grid_adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Open the corresponding fragment.
                displayView(position);
            }
        });

    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new MicrophoneRecorder();
                break;
            case 1:
                fragment = new CallRecorder();
                break;
            case 2:
                fragment = new CallBlocker();
                break;
            case 3:
                fragment = new SMSBlocker();
                break;
            case 4:
                fragment = new RateAppOnGooglePlayStore();
                break;
            case 5:
                fragment = new SendPromotionalEmail();
                break;
            case 6:
                fragment = new TellFriendViaPersonalEmail();
                break;
            case 7:
                fragment = new AboutApp();
                break;
            case 8:
                fragment = new ExitApp();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(navMenuTitles[position]);

        } else {
            // error in creating fragment
            Log.i(Constants.APP_NAME_TAG, "HomePage.java:displayView() ...Error in creating fragment");
        }
    }
}
