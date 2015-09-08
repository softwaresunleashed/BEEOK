package com.unleashed.android.beeokunleashed.paymentgateways.google;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.unleashed.android.beeokunleashed.R;
import com.unleashed.android.beeokunleashed.constants.Constants;
import com.unleashed.android.beeokunleashed.paymentgateways.google.util.IabHelper;
import com.unleashed.android.beeokunleashed.paymentgateways.google.util.IabResult;
import com.unleashed.android.beeokunleashed.paymentgateways.google.util.Inventory;
import com.unleashed.android.beeokunleashed.paymentgateways.google.util.Purchase;

import java.util.ArrayList;



public class GooglePaymentGateway extends Activity {


    /* TODO :
    * Add this activity to AndroidManifest.xml file.
    * */
    private IabHelper mHelper;
    private Context context;
    private Activity activityContext;

    private String[] strArrayAdditionalSku;
    private String payload = "";
    private int RC_REQUEST = 20001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Code to show Title Bar of activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_google_payment_gateway);


        context = getApplicationContext();
        activityContext = this;         // Get current activity context

        Button btnBack = (Button)findViewById(R.id.btn_Back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();       // Close the activity.
            }
        });



        // Obfuscate the Public key
        String p1 = getResources().getString(R.string.p1);
        String p2 = getResources().getString(R.string.p2);
        String p3 = getResources().getString(R.string.p3);
        String p4 = getResources().getString(R.string.p4);

        // Build up key at runtime.
        String base64EncodedPublicKey = p4 + p3 + p2 + p1;

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);


        //Next, perform the service binding
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(Constants.APP_NAME_TAG, "Problem setting up In-app Billing: " + result);
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(Constants.APP_NAME_TAG, "GooglePaymentGateway.java: Setup successful. Querying inventory.");
                //mHelper.queryInventoryAsync(mGotInventoryListener);

                // Add products to query
                ArrayList<String> additionalSkuList = new ArrayList<String>();
                strArrayAdditionalSku  = getResources().getStringArray(R.array.in_app_product_codes);
                for(String strProductCode : strArrayAdditionalSku)
                {
                    additionalSkuList.add(strProductCode);
                }
                mHelper.queryInventoryAsync(true, additionalSkuList, mGotInventoryListener);
            }
        });


    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(Constants.APP_NAME_TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.e(Constants.APP_NAME_TAG, "GooglePlaymentGateway.java: Failed to query inventory: " + result);
                return;
            }

            Log.d(Constants.APP_NAME_TAG, "GooglePlaymentGateway.java:Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            final  RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radgrpcontainer_products);
            radioGroup.setOrientation(RadioGroup.VERTICAL);

            RadioButton[] radioButton = new RadioButton[strArrayAdditionalSku.length];

            int size = strArrayAdditionalSku.length;
            for(int index = 0; index < size; index++){
                radioButton[index] = new RadioButton(context);


                // Pull Out the Name of the Array of SKU Products
                TypedArray entriesTypedArray = getResources().obtainTypedArray(R.array.in_app_product_codes);
                TypedValue v1 = new TypedValue();
                entriesTypedArray.getValue(index, v1);      // this gives the "Name" associated with the product id

                // Set the Radio Button ID mapped to the SKU Products
                // This will be helpful to switch to respective radio button code when radio buttons are clicked.
                int id = v1.resourceId;
                radioButton[index].setId(id);
                radioButton[index].setTextColor(Color.BLACK);


                // Set the text of radio button
                String radText_description = inventory.getSkuDetails(strArrayAdditionalSku[index]).getTitle();
                String radText_price = inventory.getSkuDetails(strArrayAdditionalSku[index]).getPrice();
                radioButton[index].setText(radText_description + " ( " + radText_price + " )");

                //radioButton[index].setOnClickListener();
                radioGroup.addView(radioButton[index]);
            }

            // Code to handle user's selection of amount of donation.
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    // get selected radio button from radioGroup
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    /* TODO: for security, generate your payload here for verification. See the comments on
                    *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
                    *        an empty string, but on a production app you should carefully generate this. */

                    switch (selectedId){
                        case R.string.SKU_DONATE_COFFEE:


                            mHelper.launchPurchaseFlow(activityContext,
                                                        getResources().getString(R.string.SKU_DONATE_COFFEE),
                                                        RC_REQUEST,
                                                        mPurchaseFinishedListener,
                                                        payload);

                            break;
                        case R.string.SKU_DONATE_HALFCUP_COFFEE:
                            mHelper.launchPurchaseFlow(activityContext,
                                                        getResources().getString(R.string.SKU_DONATE_HALFCUP_COFFEE),
                                                        RC_REQUEST,
                                                        mPurchaseFinishedListener,
                                                        payload);
                            break;
                        case R.string.SKU_DONATE_TEA:
                            mHelper.launchPurchaseFlow(activityContext,
                                                        getResources().getString(R.string.SKU_DONATE_TEA),
                                                        RC_REQUEST,
                                                        mPurchaseFinishedListener,
                                                        payload);
                            break;
                    }

                    // Close the activity after selection is made
                    finish();

                }
            });

            Log.d(Constants.APP_NAME_TAG, "Initial inventory query finished; enabling main UI.");
        }
    };


    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(Constants.APP_NAME_TAG, "Purchase finished: " + result + ", purchase: " + purchase);


            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
//                complain("Error purchasing: " + result);
//                setWaitScreen(false);
                  return;
            }
//            if (!verifyDeveloperPayload(purchase)) {
//                complain("Error purchasing. Authenticity verification failed.");
//                setWaitScreen(false);
//                return;
//            }

            Log.d(Constants.APP_NAME_TAG, "Purchase successful.");

            // Add products to query
            //ArrayList<String> additionalSkuList = new ArrayList<String>();
            strArrayAdditionalSku  = getResources().getStringArray(R.array.in_app_product_codes);
            for(String strProductCode : strArrayAdditionalSku)
            {
                if (purchase.getSku().equals(strProductCode)) {
                    // Bought the product now Consume it.
                    Log.d(Constants.APP_NAME_TAG, "Purchase done. Starting consumption.");
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                    break;
                }

            }


//            else if (purchase.getSku().equals(SKU_PREMIUM)) {
//                // bought the premium upgrade!
//                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
//                alert("Thank you for upgrading to premium!");
//                mIsPremium = true;
//                updateUi();
//                setWaitScreen(false);
//            }
//            else if (purchase.getSku().equals(SKU_INFINITE_GAS)) {
//                // bought the infinite gas subscription
//                Log.d(TAG, "Infinite gas subscription purchased.");
//                alert("Thank you for subscribing to infinite gas!");
//                mSubscribedToInfiniteGas = true;
//                mTank = TANK_MAX;
//                updateUi();
//                setWaitScreen(false);
//            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(Constants.APP_NAME_TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(Constants.APP_NAME_TAG, "Consumption successful. Provisioning.");
//                mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
//                saveData();
//                alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");
            }
            else {
                Log.d(Constants.APP_NAME_TAG, "Consumption failed.");
            }
//            updateUi();
//            setWaitScreen(false);
            Log.d(Constants.APP_NAME_TAG, "End consumption flow.");
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_google_payment_gateway, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

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
