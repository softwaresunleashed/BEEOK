package com.unleashed.android.beeokunleashed.ui.fragements;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.unleashed.android.beeokunleashed.R;
import com.unleashed.android.beeokunleashed.adhosting.googleadmob.GoogleAdMob;
import com.unleashed.android.beeokunleashed.constants.Constants;
import com.unleashed.android.beeokunleashed.databases.BlockedCallsDB;
import com.unleashed.android.beeokunleashed.utils.SharedPrefs;
import com.unleashed.android.beeokunleashed.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallBlocker extends Fragment{

    private BlockedCallsDB blockedCallsDB;      // Blocked Calls Database object
    private Context mContext;


    private ArrayAdapter<String> BlockedCallsDBAdapter; /** Declaring an ArrayAdapter to set items to ListView */
    private ArrayList<String> blockedCallsList;     /** Items entered by the user is stored in this ArrayList variable */

    public static boolean isCallBlockerEnabled;
    // UI Elements
    private Button btn_addToBlackList;
    private EditText editText_newFileInput;
    private ListView lv_blocked_numbers;
    private ToggleButton togbtn_callblocker;



    public CallBlocker() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Code to lock the fragment to Portrait mode.
        Activity a = getActivity();
        if(a != null)
            a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }

    private void initializeDB(Context context) {
        // Create Database during
        blockedCallsDB = new BlockedCallsDB(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_call_blocker, container, false);

        mContext = rootView.getContext().getApplicationContext();



        // Initialize the DB at the very beginning.
        // Always remember to pass Application Context to DB
        initializeDB(rootView.getContext().getApplicationContext());


        // Toggle Button to Enable / Disable Call Blocking.
        togbtn_callblocker = (ToggleButton)rootView.findViewById(R.id.togBtn_CallBlocker);
        // Set the state of the block calls toggle button as set in the shared preference file.
        togbtn_callblocker.setChecked(SharedPrefs.ReadFromSharedPrefFile(mContext, Constants.PREFS_NAME, "isCallBlockerEnabled"));
        togbtn_callblocker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isEnabled) {

                // Write current state to a shared pref file.
                SharedPrefs.WriteToSharedPrefFile(mContext, Constants.PREFS_NAME, "isCallBlockerEnabled", isEnabled);
            }
        });


        // Initialize the Add to contact list button.
        btn_addToBlackList = (Button)rootView.findViewById(R.id.btn_add_to_blacklist);
        btn_addToBlackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_dialog_add_to_blacklist(rootView);
            }
        });

        if (getResources().getInteger(R.integer.host_ads) == 1) {
            // Initialize the google ads via common api.
            GoogleAdMob.init_google_ads(rootView, R.id.adView_call_blocker);
            GoogleAdMob.init_google_ads(rootView, R.id.adView_call_blocker2);
        }

        invoke_call_blocker(rootView);

        return rootView;
    }

    private void show_dialog_add_to_blacklist(final View rootView) {


            // When creating a Dialog inside a fragment, take the context of Fragment.
            AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());  //getActivity().getApplicationContext()  // MainActivity.this
            builder.setIcon(R.drawable.call_recorder_app_icon);
            builder.setCancelable(true);
            builder.setTitle(R.string.add_to_blacklist);
            builder.setMessage(R.string.blacklist_dialog_msg);

            // Adding a text box to Dialog Box
            editText_newFileInput = new EditText(rootView.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(   LinearLayout.LayoutParams.MATCH_PARENT,
                                                                            LinearLayout.LayoutParams.MATCH_PARENT);
            editText_newFileInput.setInputType(InputType.TYPE_CLASS_PHONE);     // Set the type of edit box as Phonenumber
            editText_newFileInput.setLayoutParams(lp);
            editText_newFileInput.setText("");
            builder.setView(editText_newFileInput);

            // Skip File Rename Dialog box
            builder.setNeutralButton(R.string.add, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Get the new filename from the text box
                    String phNumber = editText_newFileInput.getText().toString();
                    String blocked_number = "yes";

					// Add number only if it is not empty
                    if(!phNumber.isEmpty()){
                        blockedCallsDB.insertRecord(phNumber, blocked_number);


                        // Update the List View with Numbers to be blocked
                        BlockedCallsDBAdapter.add(phNumber);
                        lv_blocked_numbers.setAdapter(BlockedCallsDBAdapter);
						editText_newFileInput.setText("");
                    }


                }
            });

            // Dismiss Dialog
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Stay in the app, dont have to do anything
                    dialogInterface.dismiss();
                }
            });

            try{

                AlertDialog alert = builder.create();
                alert.show();
            }catch (Exception ex){
                Log.e(Constants.APP_NAME_TAG, "CallBlocker.java: show_dialog_add_to_blacklist() caught exception.");
                ex.printStackTrace();
            }





    }

    private void invoke_call_blocker(final View rootView) {
        /** Items entered by the user is stored in this ArrayList variable */
        blockedCallsList = new ArrayList<String>();


        //display_alert_dialog_for_callblocker_paid_option(rootView);
        lv_blocked_numbers = (ListView)rootView.findViewById(R.id.listView_blocked_numbers);
        lv_blocked_numbers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                Context cntx = rootView.getContext(); //getActivity(); //getBaseContext(); //getApplication();
                final CharSequence[] items = {"Remove From List", "Cancel"};
                final int pos = position;


                AlertDialog.Builder builder = new AlertDialog.Builder(cntx);
                builder.setIcon(R.drawable.call_recorder);
                builder.setCancelable(true);
                builder.setTitle("Action:");


                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {


                        switch (item) {

                            case 0: // "Remove from List"

                                // Get Phonenumber which is being deleted
                                String getDeletedNumber = lv_blocked_numbers.getItemAtPosition(pos).toString();
                                // Get the ID mapped to this number , and request Sqlite DB wrapper to delete it from DB
                                Cursor cur_blockedcalls = blockedCallsDB.retrieveRecord(getDeletedNumber);
                                if (cur_blockedcalls != null) {
                                    cur_blockedcalls.moveToFirst();
									// Get the ID of that particular record
									String phnNumToBeRemovedFromDB = cur_blockedcalls.getString(1);
                                    do {
                                        
                                        blockedCallsDB.deleteRecord(phnNumToBeRemovedFromDB);

                                    } while (cur_blockedcalls.moveToNext());

                                }


                                // Remove the number from List of Blocked Numbers
								for(int i = blockedCallsList.size()-1 ; i >= 0; i--)
                                {
                                    if(getDeletedNumber.equals(lv_blocked_numbers.getItemAtPosition(i).toString())){
                                        blockedCallsList.remove(i);
                                    }
                                }
                                //blockedCallsList.remove(pos);
                                BlockedCallsDBAdapter.notifyDataSetChanged();

                                break;

                            case 1: // "Cancel"
                                dialog.cancel();
                                break;
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


                return false;
            }
        });


        // Update the List View with Numbers to be blocked
        Cursor blocked_contacts = blockedCallsDB.retrieveAllRecords();
        if(blocked_contacts != null && blocked_contacts.getCount() != 0){

            blocked_contacts.moveToFirst();
            do {

                String blockedPhnNum = blocked_contacts.getString(1);       // Blocked phPhone Numbers is second column of table after ID
                blockedCallsList.add(blockedPhnNum);        // Add blocked number to the list (that will be linked to adapter.

            }while (blocked_contacts.moveToNext());
        }

        /** Defining the ArrayAdapter to set items to ListView */
        BlockedCallsDBAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, blockedCallsList);

        //ListAdapter la_blocked_phnum_from_db = new ListAdapter();
        lv_blocked_numbers.setAdapter(BlockedCallsDBAdapter);


    }




    private void display_alert_dialog_for_callblocker_paid_option(View rootView){
        // When creating a Dialog inside a fragment, take the context of Fragment.
        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());  //getActivity().getApplicationContext()  // MainActivity.this
        builder.setIcon(R.drawable.call_recorder);
        builder.setCancelable(true);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.dialog_call_blocker_message);
        builder.setInverseBackgroundForced(true);

        builder.setNegativeButton(R.string.ok_got_it, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Stay in the app, dont have to do anything
                dialogInterface.dismiss();
            }
        });

        try{

            AlertDialog alert = builder.create();
            alert.show();
        }catch (Exception ex){
            Log.e(Constants.APP_NAME_TAG, "CallBlocker.java: invoke_call_blocker() caught exception.");
            ex.printStackTrace();
        }
    }

}
