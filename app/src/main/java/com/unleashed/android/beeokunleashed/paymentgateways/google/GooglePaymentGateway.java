package com.unleashed.android.beeokunleashed.paymentgateways.google.util;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.unleashed.android.beeokunleashed.R;
import com.unleashed.android.beeokunleashed.constants.Constants;

public class GooglePaymentGateway extends ActionBarActivity {

    private IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_payment_gateway);

        // Obfuscate the Public key
        String p1 = getResources().getString(R.string.p1);
        String p2 = getResources().getString(R.string.p2);
        String p3 = getResources().getString(R.string.p3);
        String p4 = getResources().getString(R.string.p4);

        // Build up key at runtime.
        String base64EncodedPublicKey = p4 + p3 + p2 + p1;

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);


        //Next, perform the service binding
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(Constants.APP_NAME_TAG, "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_google_payment_gateway, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unbind the Google Payment gateway service.
        if (mHelper != null) mHelper.dispose();
        mHelper = null;


    }
}
