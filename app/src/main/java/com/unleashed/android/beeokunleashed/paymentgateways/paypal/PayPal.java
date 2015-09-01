package com.unleashed.android.beeokunleashed.paymentgateways.paypal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.unleashed.android.beeokunleashed.R;
import com.unleashed.android.beeokunleashed.constants.Constants;
import com.unleashed.android.beeokunleashed.ui.fragements.HomePage;

import org.json.JSONException;

import java.math.BigDecimal;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by sudhanshu on 30/08/15.
 */
public class PayPal extends AppCompatActivity{

    private static PayPalConfiguration config;      // For paypal configuration.
    private static Context mContext;


    public void InitConfiguration(Context context) {

        // Keep a copy of context supplied by parent class. Needed in other methods.
        mContext = context;

        config = new PayPalConfiguration()
                // Start with mock environment.
                // When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                // or live (ENVIRONMENT_PRODUCTION)
                .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)

                // Each app needs to be registered on paypal ,
                // the App ID is also called a Client ID.
                // it appears you are using the Classic API Username instead of the application client_id.
                // To fix, log in to developer.paypal.com,
                // navigate to Applications > My Apps > the name of your app.
                // Open the 'REST API CREDENTIALS ' window, and you'll see a sandbox and live client_id.
                // Copy the appropriate id into your app.
                .clientId(context.getResources().getString(R.string.BEEOK_PAYPAL_LIVE_APP_ID));


        // Start Paypal Service to handle payments.
        StartService(context);


    }

    private void StartService(Context context){

        Intent intent = new Intent(context, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        context.startService(intent);
    }

    public void StartPaymentActivity(Context context) {

        // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
        // Change PAYMENT_INTENT_SALE to
        //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
        //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
        //     later via calls from your server.
        PayPalPayment payment = new PayPalPayment(new BigDecimal("1.00"),
                                                    "USD",
                                                    "Donate A Coffee to Developer",
                                                    PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(context, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        try{
            // Start PayPal Activity
            //startActivityForResult(intent, 0);
            context.startActivity(intent);

        }catch (Exception ex){
            Log.e(Constants.APP_NAME_TAG, "PayPal.java:exception in ");
        }

    }


    public void StopPaymentService(Context context){
        context.stopService(new Intent(context, PayPalService.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i(Constants.APP_NAME_TAG, confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.
                    Toast.makeText(mContext, "Thank You!!! for appreciating our work.", Toast.LENGTH_SHORT).show();


                    // Return back to Home Screen
                    Fragment fragment = new HomePage();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_container, fragment).commit();
                    }

                } catch (JSONException e) {
                    Log.e(Constants.APP_NAME_TAG, "PayPal.java:onActivityResult() - an extremely unlikely failure occurred: ", e);
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(Constants.APP_NAME_TAG, "PayPal.java:onActivityResult() - The user canceled.");

            Toast.makeText(mContext, "Payment Cancelled by User.", Toast.LENGTH_SHORT).show();


        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i(Constants.APP_NAME_TAG, "PayPal.java:onActivityResult() - An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            Toast.makeText(mContext, "Invalid Payment Configuration detected. Try Again later.", Toast.LENGTH_SHORT).show();

        }

    }
}
