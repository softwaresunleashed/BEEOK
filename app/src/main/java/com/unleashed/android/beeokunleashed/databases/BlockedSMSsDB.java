package com.unleashed.android.beeokunleashed.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by gupta on 8/4/2015.
 */

public class BlockedSMSsDB {

    private static final String Id = "Id";       // Automatic increment

    private static final String databasename = "BlockedSMSsDB";
    private static final String tablename = "BlockedSMSsTable";    // TableName should NEVER be 'table' 'Table' 'TABLE'
    private static final int databaseversion = 1;

    // DB Table's Columns - Configure as per need.
    public static final String Col1 = "PhnNums";
    public static final String Col2 = "Blocked";


   // "create table JobTable (Id integer primary key AUTOINCREMENT, " + "JobId text not null, PhnNums text not null, Msg text not null);";

    // Create Table SQL String
    private static final String create_table = "create table " + tablename + " (Id integer primary key AUTOINCREMENT, " + Col1 + " text not null, " + Col2 + " text not null" + ");";




    private final Context ct;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;




    public BlockedSMSsDB(Context context) {
        this.ct = context;
        dbHelper = new DatabaseHelper(ct);
    }


    // Declaring the connect() method to connect to the database
    public BlockedSMSsDB connect() /* throws SQLException */  {
        try{
            database = dbHelper.getWritableDatabase();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return this;
    }

    // Declaring the disconnect() method to close the database
    public void disconnect() {
        dbHelper.close();
    }

    // Declaring the insertRecord() method to add the record details into the database
    public long insertRecord(String col1_value, String col2_value) {
        ContentValues cv = new ContentValues();

        // Pack data into a ContentValue object

        cv.put(Col1, col1_value);
        cv.put(Col2, col2_value);

        // Connect to database before performing any operation.
        try {
            this.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return database.insert(tablename, null, cv);
    }

    // Declaring the retrieveAllRecords() method to retrieve all the employee details from the database.
    public Cursor retrieveAllRecords() {


        try{
            this.connect();

        }catch (Exception ex){
            ex.printStackTrace();
        }

       return database.query(tablename, new String[]{Id, Col1, Col2}, null, null, null, null, null);

    }

    // Declaring the retrieveRecord() method to retrieve the details of a particular record id.
    public Cursor retrieveRecord(String recordId) {

        try{
            this.connect();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        Cursor c = database.query(true, tablename, new String[]{Id, Col1, Col2}, Col1 + "=" + "\"" + recordId + "\"", null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Declaring the deleteRecord() method to delete an employee detail
    public boolean deleteRecord(String recordId){

        try{
            this.connect();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return database.delete(tablename, Col1 + "=" + "\"" + recordId + "\"", null) > 0;

    }


    // Declaring the updateEmployee() method to update an employee details from the database.
    // Update the record on the basis of ID....get the ID first and then call this function to update values of cols
    public boolean updateRecord(long id, String col1_value, String col2_value) throws SQLException {
        this.connect();

        ContentValues cvalues = new ContentValues();
        cvalues.put(Col1, col1_value);
        cvalues.put(Col2, col2_value);

        // Update the record on the basis of ID....get the ID first and then call this function to update values of cols
        return database.update(tablename, cvalues, Id + "=" + id, null) > 0;
    }

    // CLASS DatabaseHelper (internal class)
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context c) {
            super(c, databasename, null, databaseversion);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {

            database.execSQL(create_table);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {
            database.execSQL("DROP TABLE IF EXISTS " + tablename);
            onCreate(database);
        }
    }


}


