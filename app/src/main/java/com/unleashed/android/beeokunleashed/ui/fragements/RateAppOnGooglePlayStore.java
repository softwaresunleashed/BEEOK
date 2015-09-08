package com.unleashed.android.beeokunleashed.ui.fragements;


import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unleashed.android.beeokunleashed.R;
import com.unleashed.android.beeokunleashed.adhosting.googleadmob.GoogleAdMob;

/**
 * A simple {@link Fragment} subclass.
 */
public class RateAppOnGooglePlayStore extends Fragment {


    public RateAppOnGooglePlayStore() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_rate_app_on_google_play_store, container, false);

        if (getResources().getInteger(R.integer.host_ads) == 1) {
            // Initialize the google ads via common api.
            GoogleAdMob.init_google_ads(rootView, R.id.adView_rate_app);
            GoogleAdMob.init_google_ads(rootView, R.id.adView_rate_app2);
        }

        rate_app_on_google_play_store();

        return rootView;
    }

    private void rate_app_on_google_play_store() {

        final String appPackageName = getActivity().getPackageName();
        final Intent openPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));

        if (hasHandlerForIntent(openPlayStore))
            startActivity(openPlayStore);
        else
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));


        // Open the Home Page
        Fragment fragment = new HomePage();
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // Set title bar as "Home"
            ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(R.string.action_home_page);


        }
    }

    private boolean hasHandlerForIntent(Intent intent)
    {
        return getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }


}
