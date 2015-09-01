package com.unleashed.android.beeokunleashed.ui.fragements;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.unleashed.android.beeokunleashed.R;
import com.unleashed.android.beeokunleashed.adhosting.googleadmob.GoogleAdMob;
import com.unleashed.android.beeokunleashed.paymentgateways.paypal.PayPal;

/**
 * A simple {@link Fragment} subclass.
 */
public class Donate extends Fragment {

    private Context mContext;
    private ImageButton imgbtn_donate;
    private PayPal payPalObj;

    public Donate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_donate, container, false);

        mContext = rootView.getContext();

        // Host Ads - Decide here.
        if (getResources().getInteger(R.integer.host_ads) == 1) {
            // Initialize the google ads via common api.
            GoogleAdMob.init_google_ads(rootView, R.id.adView_donate);
        }

        init_donation_fragment(rootView);

        return rootView;
    }

    private void init_donation_fragment(View rootView) {

        // If PayPal Gateway is enabled.
        if (getResources().getInteger(R.integer.PG_PAYPAL) == 1){

            payPalObj = new PayPal();

            // Initialize the config parameters.
            payPalObj.InitConfiguration(mContext);

            imgbtn_donate = (ImageButton)rootView.findViewById(R.id.imgBtn_makedonation);
            imgbtn_donate.setVisibility(View.VISIBLE);
            imgbtn_donate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Show PayPal Payment Activity
                    payPalObj.StartPaymentActivity(mContext);
                }
            });
        }


        // If Google Payment Gateway is enabled
        if (getResources().getInteger(R.integer.PG_GOOGLE) == 1){


        }



    }

    @Override
    public void onDestroyView() {

        exit_donation_fragment();

        super.onDestroyView();
    }

    private void exit_donation_fragment() {

        // For PayPal Payment Gateway
        if (getResources().getInteger(R.integer.PG_PAYPAL) == 1) {
            payPalObj.StopPaymentService(mContext);
        }


        // If Google Payment Gateway is enabled
        if (getResources().getInteger(R.integer.PG_GOOGLE) == 1){


        }

    }

}
