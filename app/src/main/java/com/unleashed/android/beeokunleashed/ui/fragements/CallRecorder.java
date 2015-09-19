package com.unleashed.android.beeokunleashed.ui.fragements;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.unleashed.android.beeokunleashed.R;
import com.unleashed.android.beeokunleashed.adhosting.googleadmob.GoogleAdMob;
import com.unleashed.android.beeokunleashed.constants.Constants;
import com.unleashed.android.beeokunleashed.databases.RecordCallsDB;
import com.unleashed.android.beeokunleashed.utils.SharedPrefs;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallRecorder extends Fragment {

    private RecordCallsDB recordCallsDB;      // Record Calls Database object
    private Context mContext;


    private ArrayAdapter<String> RecordCallsDBAdapter; /** Declaring an ArrayAdapter to set items to ListView */
    private ArrayList<String> recordCallsList;     /** Items entered by the user is stored in this ArrayList variable */

    public static boolean isRecordCallsEnabled;
    // UI Elements
    private Button btn_addToRecordList;
    private TextView tv_longkeypress;
    private EditText editText_newFileInput;
    private ListView lv_record_numbers;
    private ToggleButton togbtn_recordcalls;
    private CheckBox cb_record_all_calls;



    public CallRecorder() {
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
        recordCallsDB = new RecordCallsDB(context);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_call_recorder, container, false);

  		mContext = rootView.getContext().getApplicationContext();



        // Initialize the DB at the very beginning.
        // Always remember to pass Application Context to DB
        initializeDB(mContext);






        if (getResources().getInteger(R.integer.host_ads) == 1) {
            // Initialize the google ads via common api.
            GoogleAdMob.init_google_ads(rootView, R.id.adView_call_recorder);
            GoogleAdMob.init_google_ads(rootView, R.id.adView_call_recorder2);
        }


        invoke_call_recording(rootView);

        return rootView;
    }

    private void show_dialog_add_to_recordcallslist(final View rootView) {


            // When creating a Dialog inside a fragment, take the context of Fragment.
            AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());  //getActivity().getApplicationContext()  // MainActivity.this
            builder.setIcon(R.drawable.call_recorder);
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
                    String smsOiginAddress = editText_newFileInput.getText().toString();
                    String blocked_number = "yes";

                    // Add number only if it is not empty
                    if(!smsOiginAddress.isEmpty()){
                        recordCallsDB.insertRecord(smsOiginAddress, blocked_number);

                        // Update the List View with Numbers to be blocked
                        RecordCallsDBAdapter.add(smsOiginAddress);
                        lv_record_numbers.setAdapter(RecordCallsDBAdapter);
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
    private void invoke_call_recording(final View rootView) {

        tv_longkeypress = (TextView)rootView.findViewById(R.id.textView_LongPressToDeleteMsg);

        // Toggle Button to Enable / Disable Call Blocking.
        togbtn_recordcalls = (ToggleButton)rootView.findViewById(R.id.togBtn_CallRecorder);
        // Set the state of the block calls toggle button as set in the shared preference file.
        togbtn_recordcalls.setChecked(SharedPrefs.ReadFromSharedPrefFile(mContext, Constants.PREFS_NAME, "isRecordCallsEnabled"));
        togbtn_recordcalls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isEnabled) {

                // Write current state to a shared pref file.
                SharedPrefs.WriteToSharedPrefFile(mContext, Constants.PREFS_NAME, "isRecordCallsEnabled", isEnabled);
            }
        });

        // Initialize the Add to contact list button.
        btn_addToRecordList = (Button)rootView.findViewById(R.id.btn_add_to_recordlist);
        btn_addToRecordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_dialog_add_to_recordcallslist(rootView);
            }
        });




        /** Items entered by the user is stored in this ArrayList variable */
        recordCallsList = new ArrayList<String>();



        lv_record_numbers = (ListView)rootView.findViewById(R.id.listView_record_numbers);
        lv_record_numbers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

                                // Get SMSnumber which is being deleted
                                String getDeletedNumber = lv_record_numbers.getItemAtPosition(pos).toString();
                                // Get the ID mapped to this number , and request Sqlite DB wrapper to delete it from DB
                                Cursor cur_blockedsmss = recordCallsDB.retrieveRecord(getDeletedNumber);
                                if (cur_blockedsmss != null) {
                                    cur_blockedsmss.moveToFirst();
                                    // Get the ID of that particular record
                                    String phnNumToBeRemovedFromDB = cur_blockedsmss.getString(1);
                                    do {
                                        recordCallsDB.deleteRecord(phnNumToBeRemovedFromDB);

                                    } while (cur_blockedsmss.moveToNext());

                                }


                                // Remove the number from List of Blocked Numbers
                                for(int i = recordCallsList.size()-1 ; i >= 0; i--)
                                {
                                    if(getDeletedNumber.equals(lv_record_numbers.getItemAtPosition(i).toString())){
                                        recordCallsList.remove(i);
                                    }
                                }

                                RecordCallsDBAdapter.notifyDataSetChanged();

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
        Cursor blocked_contacts = recordCallsDB.retrieveAllRecords();
        if(blocked_contacts != null && blocked_contacts.getCount() != 0){

            blocked_contacts.moveToFirst();
            do {

                String blockedPhnNum = blocked_contacts.getString(1);       // Blocked phPhone Numbers is second column of table after ID
                recordCallsList.add(blockedPhnNum);        // Add blocked number to the list (that will be linked to adapter.

            }while (blocked_contacts.moveToNext());
        }

        /** Defining the ArrayAdapter to set items to ListView */
        RecordCallsDBAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, recordCallsList);

        //ListAdapter la_blocked_phnum_from_db = new ListAdapter();
        lv_record_numbers.setAdapter(RecordCallsDBAdapter);


        // Handle checkbox for Record All Calls.
        cb_record_all_calls = (CheckBox)rootView.findViewById(R.id.cb_record_all_calls);
        cb_record_all_calls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enabled) {
                if(enabled){
                    // Hide Button + List
                    btn_addToRecordList.setVisibility(View.INVISIBLE);
                    lv_record_numbers.setVisibility(View.INVISIBLE);
                    tv_longkeypress.setVisibility(View.INVISIBLE);

                }else{
                    // Show Button + List
                    btn_addToRecordList.setVisibility(View.VISIBLE);
                    lv_record_numbers.setVisibility(View.VISIBLE);
                    tv_longkeypress.setVisibility(View.VISIBLE);
                }

                // Write current state to a shared pref file.
                SharedPrefs.WriteToSharedPrefFile(mContext, Constants.PREFS_NAME, "isAllCallsRecordEnabled", enabled);
            }
        });
        boolean isAllCallsRecordEnabled = SharedPrefs.ReadFromSharedPrefFile(mContext, Constants.PREFS_NAME, "isAllCallsRecordEnabled");
        cb_record_all_calls.setChecked(isAllCallsRecordEnabled);       // by default record all calls.





    }


}
