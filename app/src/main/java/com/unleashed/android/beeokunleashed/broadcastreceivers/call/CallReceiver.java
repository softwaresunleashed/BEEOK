package com.unleashed.android.beeokunleashed.broadcastreceivers.call;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.unleashed.android.beeokunleashed.constants.Constants;
import com.unleashed.android.beeokunleashed.databases.BlockedCallsDB;
import com.unleashed.android.beeokunleashed.databases.RecordCallsDB;
import com.unleashed.android.beeokunleashed.services.RecorderService;
import com.unleashed.android.beeokunleashed.utils.FileHandling;
import com.unleashed.android.beeokunleashed.utils.SharedPrefs;
import com.unleashed.android.beeokunleashed.utils.Utils;

import java.lang.reflect.Method;
import java.util.Date;


/**
 * Created by gupta on 8/10/2015.
 */
public class CallReceiver extends CallStateReceiver {

    BlockedCallsDB blockedCallsDB;
    RecordCallsDB recordCallsDB;

    Context mContext;
    Intent mRecorderServiceIntent;


    String mIncomingFilePath;
    String mOutgoingFilePath;

    @Override
    public void onReceive(Context context, Intent intent) {


        // A little housekeeping doesnt harm anyone. :)
        mContext = context;
        mRecorderServiceIntent = new Intent("com.unleashed.android.beeokunleashed.services");
        mRecorderServiceIntent.setClass(mContext, RecorderService.class);

        // Get an instance of the BlockedCallsDB here.
        blockedCallsDB = new BlockedCallsDB(mContext);

        // Get an instance of the BlockedCallsDB here.
        recordCallsDB = new RecordCallsDB(mContext);



        if(intent.getAction().equals("android.intent.action.PHONE_STATE")
                || intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {

            Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:onReceive() - Phone_State or New Outgoing Call");

            super.onReceive(context, intent);


        }

//        // This following code will run only once at bootup to start the recorder service.
//        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
//            Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:onReceive() - BOOT_COMPLETED");
//
//                // Start the service on Bootup.
//                Intent i = new Intent("com.unleashed.android.spyrecorderunleashed.services");
//                i.setClass(context, RecorderService.class);
//                context.startService(i);
//
//            Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:onReceive() - Recorder Service Started Successfuly");
//        }

    }

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:onIncomingCallStarted() - number: " + number);

        // Need to pass Application Context always.
        boolean CallBlockStatus = SharedPrefs.ReadFromSharedPrefFile(ctx, Constants.PREFS_NAME, "isCallBlockerEnabled");

        // Check if Incoming call is from a blocked number. If yes, Send Call hangup signal.
        if(CallBlockStatus && isNumberPresentInCallsBlockedDB(ctx, number)) {
            Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:onIncomingCallStarted() - match found. Hanging up.");

            killCall(ctx);

            return;
        }

        // Need to Check if All Calls Recording is enabled.
        boolean isAllCallsRecordEnabled = SharedPrefs.ReadFromSharedPrefFile(ctx, Constants.PREFS_NAME, "isAllCallsRecordEnabled");
        if(!isAllCallsRecordEnabled){
            /*
            * We need to perform following filtering only when "All Call Recording Enabled" option is false
            * */


            // Need to pass Application Context always.
            boolean CallRecordStatus = SharedPrefs.ReadFromSharedPrefFile(ctx, Constants.PREFS_NAME, "isRecordCallsEnabled");
            // Check if Incoming call is from a Defined number.
            if(!CallRecordStatus) {
                Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:onIncomingCallStarted() - Call  ");

                return;
            }

            if( ! isNumberPresentInCallsRecordDB(ctx, number)){
                // The number is not present in Record database, so no need to record this call.
                return;
            }

        }



        // Start the recording stuff here.
        String filename =  Utils.populateFileName(ctx, Constants.FILE_PATH_INCOMING_TAG, number, start);


        FileHandling fh = new FileHandling();
        String completeFilePath ;

        completeFilePath = fh.CreateFile(   Constants.APP_ROOT_FOLDER_DATA /* ctx.getPackageName() */ + "/" + Constants.RECORDED_MEDIA_FOLDER_VOICE_CALLS,
                                            filename,
                                            FileHandling.StorageLocation.External);


        mRecorderServiceIntent.putExtra(Constants.INTENT_PARAM_FILE_NAME, completeFilePath);
        mContext.startService(mRecorderServiceIntent);

    }


    private boolean isNumberPresentInCallsRecordDB(Context ctx, String number) {
        Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:isNumberPresentInCallsRecordDB() - number: " + number);

        boolean result = false;

        Cursor record_calls = null;

        record_calls = recordCallsDB.retrieveAllRecords();     // Get all records from RecordCaller DB
        if(record_calls != null && record_calls.getCount() != 0){

            record_calls.moveToFirst();
            do {

                String blockedPhnNum = record_calls.getString(1);       // Blocked phPhone Numbers is second column of table after ID

                // Compare if the incoming number is among the blocked list.
                Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:isNumberPresentInCallsBlockedDB() - Comparing Number : " + number + " with Blocked Number : " + blockedPhnNum);
                if(number.contains(blockedPhnNum)){
                    result = true;
                    break;
                }

            }while (record_calls.moveToNext());
        }

        return result;
    }


    private boolean isNumberPresentInCallsBlockedDB(Context ctx, String number) {
        Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:isNumberPresentInBlockedCallsDB() - number: " + number);

        boolean result = false;

        Cursor blocked_calls = null;

        blocked_calls = blockedCallsDB.retrieveAllRecords();     // Get all records from CallBlocker DB
        if(blocked_calls != null && blocked_calls.getCount() != 0){

            blocked_calls.moveToFirst();
            do {

                String blockedPhnNum = blocked_calls.getString(1);       // Blocked phPhone Numbers is second column of table after ID

                // Compare if the incoming number is among the blocked list.
                Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:isNumberPresentInCallsBlockedDB() - Comparing Number : " + number + " with Blocked Number : " + blockedPhnNum);
                if(number.contains(blockedPhnNum)){
                    result = true;
                    break;
                }

            }while (blocked_calls.moveToNext());
        }

        return result;
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:onIncomingCallEnded() - number: " + number);

        mContext.stopService(mRecorderServiceIntent);
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:onOutgoingCallStarted() - number: " + number);


        // Need to Check if All Calls Recording is enabled.
        boolean isAllCallsRecordEnabled = SharedPrefs.ReadFromSharedPrefFile(ctx, Constants.PREFS_NAME, "isAllCallsRecordEnabled");
        if(!isAllCallsRecordEnabled){
            /*
            * We need to perform following filtering only when "All Call Recording Enabled" option is false
            * */


            // Need to pass Application Context always.
            boolean CallRecordStatus = SharedPrefs.ReadFromSharedPrefFile(ctx, Constants.PREFS_NAME, "isRecordCallsEnabled");
            // Check if Incoming call is from a Defined number.
            if(!CallRecordStatus) {
                Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:onIncomingCallStarted() - Call  ");

                return;
            }

            if( ! isNumberPresentInCallsRecordDB(ctx, number)){
                // The number is not present in Record database, so no need to record this call.
                return;
            }

        }


        String filename =  Utils.populateFileName(ctx, Constants.FILE_PATH_OUTGOING_TAG, number, start);

        FileHandling fh = new FileHandling();
        String completeFilePath ;

        completeFilePath = fh.CreateFile(   Constants.APP_ROOT_FOLDER_DATA /* ctx.getPackageName() */ + "/" + Constants.RECORDED_MEDIA_FOLDER_VOICE_CALLS,
                filename,
                FileHandling.StorageLocation.External);


        mRecorderServiceIntent.putExtra(Constants.INTENT_PARAM_FILE_NAME, completeFilePath);
        mContext.startService(mRecorderServiceIntent);

    }



    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:onOutgoingCallEnded() - number: " + number);

        mContext.stopService(mRecorderServiceIntent);

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.i(Constants.APP_NAME_TAG, "CallReceiver.java:onMissedCall() - number: " + number);

        mContext.stopService(mRecorderServiceIntent);

    }


//    /**
//     * package restart technique for ignoring calls
//     */
//    private void ignoreCallPackageRestart(Context context) {
//
//        try{
//
//            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            am.restartPackage("com.android.providers.telephony");
//            am.restartPackage("com.android.phone");
//        }catch (Exception ex){
//            Log.d(Constants.APP_NAME_TAG,"CallReceiver.java:ignoreCallPackageRestart() caught exception.");
//            ex.printStackTrace();
//        }
//
//    }

    public boolean killCall(Context context) {



//        try{
//            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            Class clazz = Class.forName(tm.getClass().getName());
//            Method m = clazz.getDeclaredMethod("getITelephony");
//            m.setAccessible(true);
//
//
//            ITelephony it = (ITelephony) m.invoke(tm);
//            //it.silenceRinger();
//            it.endCall();
//
//        }catch (Exception ex){
//                Log.d(Constants.APP_NAME_TAG,"CallReceiver.java:PhoneStateReceiver **" + ex.toString());
//                ex.printStackTrace();
//                return false;
//        }


        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);


            com.android.internal.telephony.ITelephony telephonyService = (ITelephony) methodGetITelephony.invoke(telephonyManager);

            // Silent the ringer
            //telephonyService.silenceRinger();
            // Stop the call.
            telephonyService.endCall();


//            // Invoke getITelephony() to get the ITelephony interface
//            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
//
//            // Get the endCall method from ITelephony
//            Class telephonyInterfaceClass =
//                    Class.forName(telephonyInterface.getClass().getName());
//            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
//
//            // Invoke endCall()
//            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d(Constants.APP_NAME_TAG,"CallReceiver.java:killCall() caught exception." + ex.toString());
            ex.printStackTrace();
            return false;
        }


        return true;
    }



}
