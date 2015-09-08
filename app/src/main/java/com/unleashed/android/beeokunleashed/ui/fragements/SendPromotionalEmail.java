package com.unleashed.android.beeokunleashed.ui.fragements;


import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.unleashed.android.beeokunleashed.R;
import com.unleashed.android.beeokunleashed.adhosting.googleadmob.GoogleAdMob;
import com.unleashed.android.beeokunleashed.constants.Constants;
import com.unleashed.android.beeokunleashed.sendemail.Mail;
import com.unleashed.android.beeokunleashed.ui.MainActivity;
import com.unleashed.android.beeokunleashed.utils.CoreLib;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendPromotionalEmail extends Fragment {

    Context ctx;

    public SendPromotionalEmail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_send_promotional_email, container, false);


        if (getResources().getInteger(R.integer.host_ads) == 1) {
            // Initialize the google ads via common api.
            GoogleAdMob.init_google_ads(rootView, R.id.adView_send_promo_mail);
            GoogleAdMob.init_google_ads(rootView, R.id.adView_send_promo_mail2);
        }


        show_dialog_box_to_request_promotional_email(rootView);



        return rootView;
    }

    private void show_dialog_box_to_request_promotional_email(View rootView) {


        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());   //getActivity().getApplicationContext() // MainActivity.this
        builder.setIcon(R.drawable.call_recorder_app_icon);
        builder.setCancelable(true);
        builder.setTitle("BEEOK Promotion: We need a favor from you!!");
        builder.setMessage(R.string.dialog_request_promotional_email_msg);

        builder.setPositiveButton(R.string.confirm_msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Get the application's context
                final Context context = getActivity().getApplicationContext();
                Toast.makeText(context, "Sending Promotional Email to your contacts...", Toast.LENGTH_LONG).show();

                // Open the Home Page
                Fragment fragment = new HomePage();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();

                    // Set title bar as "Home"
                    ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(R.string.action_home_page);

                }


                final ContentResolver cr = getActivity().getContentResolver();
                final Resources rsrcObj = getActivity().getResources();

                // Send Email to all contacts.
                // Ad Mail
                Thread thrSendEmail = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sendAnonymousMail(context, rsrcObj, cr);
                        } catch (Exception ex) {
                            Log.e(Constants.APP_NAME_TAG, "SendPromtionalEmail.java: thrSendEmail Thread caught exception.");
                            ex.printStackTrace();
                        }
                    }
                };
                thrSendEmail.start();
            }
        });

        builder.setNegativeButton(R.string.deny_msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Open the Home Page
                Fragment fragment = new HomePage();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();

                    // Set title bar as "Home"
                    ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(R.string.action_home_page);

                }

                dialogInterface.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }

    private boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private void sendAnonymousMail(Context ctx, Resources rsrcObj, ContentResolver cr) {

        try {

            final String User = "promotions.softwaresunleashed";        // write only user name....no need of @gmail.com
            final String Pass = "9211hacker";

            //Resources rsrcObj = getActivity().getResources();
            final String Subject = rsrcObj.getString(R.string.email_subject);//"Bulk SMS Launched!!";
            final String EmailBody = rsrcObj.getString(R.string.email_body);

            final String SenderFrom = "promotions.softwaresunleashed@gmail.com";
            final String RecipientsTo[];

            ArrayList<String> emailAddresses = new ArrayList<String>();
            emailAddresses.add("softwares.unleashed@gmail.com");        // Add first default address to self


            // Prepare to get the emails in Phonebook.
            //ContentResolver cr = ctx.getContentResolver(); //CoreLib.ContentResolver(); //getActivity().getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        String emailContact = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                        if(isEmailValid(emailContact)){
                            // Add the retrieved email address to
                            emailAddresses.add(emailContact);
                        }

                    }
                    emailCur.close();
                }
            }
            int sizeOfEmailAddresses = emailAddresses.size();

            // Add email address from Array List to String[]
            RecipientsTo = new String[sizeOfEmailAddresses];
            for(int i=0; i < sizeOfEmailAddresses; i++){
                RecipientsTo[i] = emailAddresses.get(i);
            }

            Thread thrSendEmail = new Thread(){
                @Override
                public void run() {
                    super.run();

                    Mail mail = new Mail(User, Pass, Subject, EmailBody, SenderFrom, RecipientsTo);

                    try {
                        // Emails should always be sent in thread, else there would be an exception.
                        // Exception : android.os.NetworkOnMainThreadException
                        mail.send();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity().getBaseContext(), "Error:" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };
            thrSendEmail.start();       // Start the thread to send email

        } catch (Exception e) {
            Log.e("Bulk SMS: ", "sendAnonymousMail() caught exception.");
            e.printStackTrace();
        }

    }


}
