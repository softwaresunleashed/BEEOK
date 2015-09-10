package com.unleashed.android.beeokunleashed.broadcastreceivers.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.unleashed.android.beeokunleashed.constants.Constants;
import com.unleashed.android.beeokunleashed.databases.BlockedCallsDB;
import com.unleashed.android.beeokunleashed.databases.BlockedSMSsDB;
import com.unleashed.android.beeokunleashed.utils.SharedPrefs;

/**
 * Created by sudhanshu on 08/09/15.
 */
public class SMSReceiver  extends SMSStateReceiver {

    BlockedSMSsDB blockedSMSsDB;
    boolean SMSBlockStatus;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.APP_NAME_TAG, "SMSReceiver.java:onReceive() - context =" + context.toString());

        // Need to pass Application Context always.
        SMSBlockStatus = SharedPrefs.ReadFromSharedPrefFile(context, Constants.PREFS_NAME, "isSMSBlockerEnabled");
        if(!SMSBlockStatus){
            // If SMS Blocker is Disabled, return immediately.
            return;
        }

        // Get an instance of the BlockedCallsDB here.
        blockedSMSsDB = new BlockedSMSsDB(context);


        // Call SMSStateReceiver.java onReceive()
        super.onReceive(context, intent);
    }

    protected void onIncomingSMS(Context context, String smsNumber, String msg){

        // Check if Incoming call is from a blocked number. If yes, Send Call hangup signal.
        if(isNumberPresentInBlockedSMSsDB(context, smsNumber)) {
            Log.i(Constants.APP_NAME_TAG, "SMSReceiver.java:onReceive() - match found. Stoping SMS Broadcast.");


            deleteSMS(context, smsNumber, msg);

            // Dont let the broadcast reach other sub-components.
            //abortBroadcast();

            return;
        }

    }

    private boolean isNumberPresentInBlockedSMSsDB(Context ctx, String number) {
        Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:isNumberPresentInBlockedCallsDB() - number: " + number);

        boolean result = false;

        Cursor blocked_contacts = null;

        blocked_contacts = blockedSMSsDB.retrieveAllRecords();     // Get all records from CallBlocker DB
        if(blocked_contacts != null){

            blocked_contacts.moveToFirst();
            do {

                String blockedPhnNum = blocked_contacts.getString(1);       // Blocked phPhone Numbers is second column of table after ID

                // Compare if the incoming number is among the blocked list.
                Log.i(Constants.APP_NAME_TAG, "SMSReceiver.java:isNumberPresentInBlockedCallsDB() - Comparing Number : " + number + " with BlockedNumber : " + blockedPhnNum);
                if(number.contains(blockedPhnNum)){
                    result = true;
                    break;
                }

            }while (blocked_contacts.moveToNext());
        }

        return result;
    }



    public void deleteSMS(Context ctx,  String number, String message) {
        try {
            Uri uriSms = Uri.parse("content://sms");
            Cursor c = ctx.getContentResolver().query(uriSms,
                                                        new String[] { "_id", "thread_id", "address", "person", "date", "body" },
                                                        null,
                                                        null,
                                                        null);

            Log.i(Constants.APP_NAME_TAG, "c count......"+c.getCount());
            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);
                    String date = c.getString(3);


                    if (address.equals(number)) {

                        int rows = ctx.getContentResolver().delete(Uri.parse("content://sms/" + id), null,null);
                        //int rows = ctx.getContentResolver().delete(Uri.parse("content://sms/" + id), "date=?",new String[] { c.getString(4) });

                        break;
                    }
                } while (c.moveToNext());
            }

        } catch (Exception ex) {
            Log.e(Constants.APP_NAME_TAG, "SMSReceiver.java:deleteSMS() caught exception.");
            ex.printStackTrace();
        }
    }
}
