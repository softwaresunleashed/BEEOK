package com.unleashed.android.beeokunleashed.ui.fragements;


import android.app.FragmentManager;
import android.content.Intent;
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
public class TellFriendViaPersonalEmail extends Fragment {


    public TellFriendViaPersonalEmail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tell_friend_via_personal_email, container, false);

        if (getResources().getInteger(R.integer.host_ads) == 1) {
            // Initialize the google ads via common api.
            GoogleAdMob.init_google_ads(rootView, R.id.adView_tell_a_friend);
            GoogleAdMob.init_google_ads(rootView, R.id.adView_tell_a_friend2);
        }


        invoke_email_a_friend_activity_chooser();

        return rootView;
    }

    private void invoke_email_a_friend_activity_chooser() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, "softwares.unleashed@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_body));

        startActivity(Intent.createChooser(intent, "Send Email"));


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


}
