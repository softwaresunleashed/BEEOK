package com.unleashed.android.beeokunleashed.ui.fragements;


import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unleashed.android.beeokunleashed.R;
import com.unleashed.android.beeokunleashed.adhosting.googleadmob.GoogleAdMob;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExitApp extends Fragment {


    public ExitApp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_exit_app, container, false);


        if (getResources().getInteger(R.integer.host_ads) == 1) {
            // Initialize the google ads via common api.
            GoogleAdMob.init_google_ads(rootView, R.id.adView_exit_app);
            GoogleAdMob.init_google_ads(rootView, R.id.adView_exit_app2);
        }

        show_dialog_to_exit_app(rootView);



        return rootView;
    }

    private void show_dialog_to_exit_app(View rootView) {

        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());  //getActivity().getApplicationContext()  // MainActivity.this
        builder.setIcon(R.drawable.call_recorder_app_icon);
        builder.setCancelable(true);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.dialog_exit_app_message);

        builder.setPositiveButton(R.string.confirm_msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Exit / Finish App
                getActivity().finish();
            }
        });

        builder.setNegativeButton(R.string.deny_msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Stay in the app, dont have to do anything
                dialogInterface.dismiss();

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
        });


        AlertDialog alert = builder.create();
        alert.show();

    }


}
