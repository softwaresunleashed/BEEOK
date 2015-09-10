package com.unleashed.android.beeokunleashed.broadcastreceivers.sms;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.unleashed.android.beeokunleashed.constants.Constants;
import com.unleashed.android.beeokunleashed.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sudhanshu on 08/09/15.
 */
public abstract class SMSStateReceiver extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingSMS(Context ctx, String phNumber, String msg){}



    @Override
    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();



        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();


                    Log.i(Constants.APP_NAME_TAG, "SMSStateReceiver.java:onReceive() - senderNum: " + senderNum + "; message: " + message);

                    // Pass the info to derived classes
                    onIncomingSMS(context,senderNum, message);


                } // end for loop
            } // bundle is null

        } catch (Exception ex) {
            Log.e(Constants.APP_NAME_TAG, "SMSReceiver.java:onReceive() caught exception");
            ex.printStackTrace();

        }

    }



}
