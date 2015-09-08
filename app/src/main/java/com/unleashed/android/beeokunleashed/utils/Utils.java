package com.unleashed.android.beeokunleashed.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import com.unleashed.android.beeokunleashed.constants.Constants;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gupta on 8/12/2015.
 */
public class Utils {

//    static String contact_name = "";

    public static String populateFileName(final Context ctx, String in_out_tag, String number, Date date){

        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm");

        if(number == null){
            number = "number_not_found";
        }

        String filePath;
        filePath = in_out_tag
                + number
                + "_"
                + dateFormat.format(date)
                + ".3gp";

//
//        if(ctx != null){
//            Thread thrGetPhoneName = new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    contact_name = getContactName(ctx, number);
//
//                }
//            });
//
//            thrGetPhoneName.start();    // start the thread
//
//            try{
//                thrGetPhoneName.join();    // wait for thread to finish only then proceed further.
//            }catch (InterruptedException ex){
//                Log.e(Constants.APP_NAME_TAG, "Utils.java:populateFileName() ...exception caught in thread joining.");
//            }
//
//        }
//
//        String filePath;
//        filePath = in_out_tag
//                + ((ctx!=null)?contact_name:number)
//                + "_"
//                + dateFormat.format(date)
//                + ".3gp";

//        filePath = in_out_tag
//                    + number + "_"
//                    + date.getYear() + "_"
//                    + String.format("%02d", (date.getMonth() + 1)) + "_"
//                    + String.format("%02d", (date.getDay())) + "_"
//                    +date.getHours() + ":"
//                    + date.getMinutes()
//                    + ".3gp";


        return filePath;
    }


    public static String getNameForNumberFromPhoneBook(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public static void renameFileName(String OldFileName, String NewFileName) {
//        String currentFileName = OldFileName.substring(OldFileName.lastIndexOf("/"), OldFileName.length());
//        currentFileName = currentFileName.substring(1);


        File from = new File(OldFileName);
        File to = new File(NewFileName);

        // Check if the file exists before renaming it.
        if(from.exists()){
            from.renameTo(to);
        }

    }

}
