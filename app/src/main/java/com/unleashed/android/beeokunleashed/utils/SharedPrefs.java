package com.unleashed.android.beeokunleashed.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.unleashed.android.beeokunleashed.constants.Constants;

/**
 * Created by sudhanshu on 06/09/15.
 */
public class SharedPrefs {



    public static void WriteToSharedPrefFile(Context context, String filename, String parameter, boolean value){

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = context.getSharedPreferences(filename /* Constants.PREFS_NAME */, 0);

        SharedPreferences.Editor editor = settings.edit();

        // write to file the parameter and its value
        editor.putBoolean(parameter, value);

        // Commit the edits!
        editor.commit();

    }


    public static boolean ReadFromSharedPrefFile(Context context, String filename, String parameter){
        // Restore preferences
        SharedPreferences settings = context.getSharedPreferences(filename /* Constants.PREFS_NAME */, 0);

        boolean retVal = settings.getBoolean(parameter, false);

        return retVal;

    }

}
