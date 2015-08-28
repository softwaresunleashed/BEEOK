package com.unleashed.android.beeokunleashed.ui.fragements;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.unleashed.android.beeokunleashed.R;
import com.unleashed.android.beeokunleashed.adhosting.googleadmob.GoogleAdMob;
import com.unleashed.android.beeokunleashed.constants.Constants;
import com.unleashed.android.beeokunleashed.recorder.AudioRecorder;
import com.unleashed.android.beeokunleashed.utils.FileHandling;
import com.unleashed.android.beeokunleashed.utils.Utils;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class MicrophoneRecorder extends Fragment {

    // Audio Recorder instance for recording voice from microphone
    private AudioRecorder audioRecorder;

    private Context context;
    private String completeFilePath ;
    private View converterView;
    private Thread thrProgressBarUpdate;
    private ProgressBar pb;

    public MicrophoneRecorder() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_microphone_recorder, container, false);

        // Keep a reference to the root view.
        converterView = rootView;

        if (getResources().getInteger(R.integer.host_ads) == 1) {
            // Initialize the google ads via common api.
            GoogleAdMob.init_google_ads(rootView, R.id.adView_microphone_recorder);
            GoogleAdMob.init_google_ads(rootView, R.id.adView_microphone_recorder2);
        }

        init_microphone_recordings(rootView);

        return rootView;

    }



    private boolean button_pressed_state = false;

    private void init_microphone_recordings(View converterView) {


        ImageButton imgbtn_microphone_recording = (ImageButton) converterView.findViewById(R.id.imgBtn_MicRec);

        imgbtn_microphone_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (button_pressed_state == false) {
                    // Recording was off, start recording now.
                    start_recording();
                }


                if (button_pressed_state == true) {
                    // Recording was on-going, stop recording now
                    stop_recording();

                }


                // Toggle the state of button
                button_pressed_state = !button_pressed_state;
            }
        });

    }



    private void start_recording() {

        progressbar_start();

        // Get the application's context
        context = getActivity().getApplicationContext();

        Date current_date = new Date();
        String filepath =  Utils.populateFileName(null, Constants.MIC_RECORD_TAG, "_", current_date);


        FileHandling fh = new FileHandling();


        completeFilePath = fh.CreateFile(Constants.APP_ROOT_FOLDER_DATA /* ctx.getPackageName() */ + "/" + Constants.RECORDED_MEDIA_FOLDER_MIC,
                      filepath,
                      FileHandling.StorageLocation.External);


        audioRecorder = new AudioRecorder(completeFilePath, MediaRecorder.AudioSource.MIC);

        audioRecorder.AudioRecorder_Start();

        Toast.makeText(context, "Recording...", Toast.LENGTH_LONG).show();

    }



    private void stop_recording() {

        progressbar_stop();

        audioRecorder.AudioRecorder_Stop();

        displayMessage(converterView, "Recording stored at \n");

        //Toast.makeText(context, "Recording stored at " + completeFilePath, Toast.LENGTH_LONG).show();
    }

    private String pathname_without_filename = "";
    private String filename_only = "";
    private EditText editText_newFileInput;

    private void displayMessage(View rootView, String message) {

        int lastIndexOfSlash = completeFilePath.lastIndexOf("/");
        pathname_without_filename = completeFilePath.substring(0, lastIndexOfSlash+1);

        filename_only = completeFilePath.substring(lastIndexOfSlash+1);
        //filename_only.substring(1); // increment by one to remove the "/" before the file name.

        // Restore preferences
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        boolean skipfiledialog = settings.getBoolean("skipfileinfodialog", false);

        if(skipfiledialog) {
            Toast.makeText(context, "File Saved...", Toast.LENGTH_LONG).show();
            return;
        }

        // When creating a Dialog inside a fragment, take the context of Fragment.
        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());  //getActivity().getApplicationContext()  // MainActivity.this
        builder.setIcon(R.drawable.call_recorder_app_icon);
        builder.setCancelable(true);
        builder.setTitle(R.string.app_name);
        builder.setMessage(message + pathname_without_filename);

        // Adding a text box to Dialog Box
        editText_newFileInput = new EditText(rootView.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText_newFileInput.setLayoutParams(lp);
        editText_newFileInput.setText(filename_only);
        builder.setView(editText_newFileInput);

//        // Do Not Show Again button
//        builder.setPositiveButton(R.string.open_recordings, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//
////                File file = new File(completeFilePath);
////                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////
////                intent.setDataAndType(Uri.fromFile(file), "video/*");
////                startActivity(intent);
//            }
//        });

        // Skip File Rename Dialog box
        builder.setNeutralButton(R.string.skip_always, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // We need an Editor object to make preference changes.
                // All objects are from android.context.Context
                SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("skipfileinfodialog", true);

                // Commit the edits!
                editor.commit();
            }
        });

        // Dismiss Dialog
        builder.setNegativeButton(R.string.save_file, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Get the new filename from the text box
                String newFileName = editText_newFileInput.getText().toString();

                String newCompleteFilePath = pathname_without_filename + newFileName;
                Utils.renameFileName(completeFilePath, newCompleteFilePath);

                // Stay in the app, dont have to do anything
                dialogInterface.dismiss();

            }
        });

        try{

            AlertDialog alert = builder.create();
            alert.show();
        }catch (Exception ex){
            Log.e(Constants.APP_NAME_TAG, "MicrophoneRecorder.java: invoke_call_blocker() caught exception.");
            ex.printStackTrace();
        }
    }


    private void progressbar_start() {

        final int[] current_progress = {0};

        pb = (ProgressBar)converterView.findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);

        thrProgressBarUpdate = new Thread(new Runnable() {
            @Override
            public void run() {

                while(current_progress[0] < 100){
                    current_progress[0]++;

                    if(current_progress[0] == 100)
                        current_progress[0] = 0;



                    pb.setProgress(current_progress[0]);

//                    // Update the progress bar and update the current value of time lapsed.
//                    new Handler().post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            pb.setProgress(current_progress[0]);
//                        }
//                    });



                    // Slowly update progress bar , sleep for some time
                    try{
                        Thread.sleep(200);
                    }catch (InterruptedException ex){
                        Log.e(Constants.APP_NAME_TAG, "MicrophoneRecorder.java: progressbar_start() caught exception.");
                        ex.printStackTrace();
                    }

                } // end while


            }
        });
        thrProgressBarUpdate.start();


    }


    private void progressbar_stop() {
        pb.setVisibility(View.GONE);

//        try{
//            if(thrProgressBarUpdate.isAlive())
//                thrProgressBarUpdate.stop();
//        }catch (Exception ex){
//            Log.e(Constants.APP_NAME_TAG, "MicrophoneRecorder.java: progress_stop() caught exception.");
//            ex.printStackTrace();
//        }


    }


}
