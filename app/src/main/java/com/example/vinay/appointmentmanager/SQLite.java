package com.example.vinay.appointmentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vinay.appointmentmanager.Appointment;

import java.util.ArrayList;
import java.util.List;

public class SQLite extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DB.db";
    public static final String TABLE_APPOINTMENTS = "appointments";

    //Columns of the appointment table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DETAILS = "details";


    public SQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        //clears the database
        //context.deleteDatabase(DATABASE_NAME);
    }


    @Override
    public void onCreate(SQLiteDatabase db) { // if you want to create a new database this method can be used

        String query = " CREATE TABLE " + TABLE_APPOINTMENTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                COLUMN_DATE + " TEXT ," +
                COLUMN_TIME + " DATETIME ," +
                COLUMN_TITLE + " TEXT ," +
                COLUMN_DETAILS + " TEXT " +
                ");";

        db.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        onCreate(db);
    }


    public int createAppointment(Appointment appointment) { //Creates an appointment based on the date, time, title and the details.
        SQLiteDatabase db = getWritableDatabase();

        String sql = " SELECT * FROM " + TABLE_APPOINTMENTS + " WHERE "
                + COLUMN_DATE + "=\'" + appointment.getDate() + "\'" + " AND " + COLUMN_TITLE
                + "=\'" + appointment.getTitle() + "\';";  // query for the database

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor == null || !cursor.moveToFirst()) {

            ContentValues contentValues = new ContentValues();

            //stores the values
            contentValues.put(COLUMN_DATE, appointment.getDate());
            contentValues.put(COLUMN_TIME, appointment.getTime());
            contentValues.put(COLUMN_TITLE, appointment.getTitle());
            contentValues.put(COLUMN_DETAILS, appointment.getDetails());


            //insert the values into the database
            db.insert(TABLE_APPOINTMENTS, null, contentValues);
            db.close(); //restores the memory
            cursor.close();
            return 1;

        } else {

            return -1; //if an appointment exists with the same appointment name returns -1 and if not executes the insert query and returns 1
        }
    }


    public int updateAppointment(Appointment appointment, String time, String title, String details) { //This function updates an appointment based on the date, time, title and the details
        SQLiteDatabase db = getWritableDatabase();

        String sql = " SELECT * FROM " + TABLE_APPOINTMENTS + " WHERE "
                + COLUMN_DATE + "=\'" + appointment.getDate() + "\'" + " AND " +
                COLUMN_TITLE + "=\'" + appointment.getTitle() + "\';"; //query for the database

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor == null || !cursor.moveToFirst()) {

            return -1;   //If the update is not successful returns -1 and if successful updates the appointment and return 1

        } else {

            ContentValues contentValues = new ContentValues();

            //stores the values to be updated
            contentValues.put(COLUMN_TIME, time);
            contentValues.put(COLUMN_TITLE, title);
            contentValues.put(COLUMN_DETAILS, details);


            //insert the values into the database
            db.update(TABLE_APPOINTMENTS, contentValues, COLUMN_DATE + "='" + appointment.getDate() + "'" + " AND " +
                    COLUMN_TITLE + "='" + appointment.getTitle() + "'", null);
            db.close(); //restores the memory
            cursor.close();
            return 1;

        }
    }


    public int moveAppointment(Appointment appointment, String date) { //This function Moves an appointment to a different date
        SQLiteDatabase db = getWritableDatabase();

        String sql = " SELECT * FROM " + TABLE_APPOINTMENTS + " WHERE "
                + COLUMN_DATE + "=\'" + appointment.getDate() + "\'" + " AND " +
                COLUMN_TITLE + "=\'" + appointment.getTitle() + "\';"; //quesry for database

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor == null || !cursor.moveToFirst()) {

            return -1;

        } else {

            ContentValues contentValues = new ContentValues();

            //stores the values to be updated
            contentValues.put(COLUMN_DATE, date);

            //insert the values into the database
            db.update(TABLE_APPOINTMENTS, contentValues, COLUMN_DATE + "='" + appointment.getDate() + "'" + " AND " +
                    COLUMN_TITLE + "='" + appointment.getTitle() + "'", null);
            db.close(); //restores the memory
            cursor.close();
            return 1;

        }
    }


    public void deleteAppointments(String date) { //Searches and deletes all the appointments on a selected day
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_APPOINTMENTS + " WHERE " + COLUMN_DATE + "=\'" + date + "\';"); //SQL query
        db.close();
    }


    public void deleteAppointments(String date, String title) { //Searches and deletes a specific appointments on a selected day
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_APPOINTMENTS + " WHERE " + COLUMN_DATE + "=\'" + date + "\'"
                + " AND " + COLUMN_TITLE + "=\'" + title + "\';");
        db.close();
    }


    public String databaseToString() { //Goes through the database and returns the result in a string (Entire Database)

        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_APPOINTMENTS + " WHERE 1 "; // 1 means every condition is met

        //Cursor exposes results from a query on a SQLiteDatabase
        Cursor cursor = db.rawQuery(query, null);
        //move the cursor to the first row of the results
        cursor.moveToFirst();

        //See if there are anymore results
        while (!cursor.isAfterLast()) {

            if (cursor.getString(cursor.getColumnIndex("title")) != null) {
                dbString += cursor.getString(cursor.getColumnIndex("_id"));
                dbString += "~";
                dbString += cursor.getString(cursor.getColumnIndex("date"));
                dbString += "~";
                dbString += cursor.getString(cursor.getColumnIndex("time"));
                dbString += "~";
                dbString += cursor.getString(cursor.getColumnIndex("title"));
                dbString += "~";
                dbString += cursor.getString(cursor.getColumnIndex("details"));
                dbString += "\n";
            }
            cursor.moveToNext();
        }
        db.close();
        return dbString;
    }



    public List<Appointment> displayAppointments(String date) { //Goes through the database and returns the result for a single day

        List<Appointment> list = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_APPOINTMENTS + " WHERE " + COLUMN_DATE + "=\'" + date + "\'"
                + " ORDER BY " + COLUMN_TIME + " ASC";

        //Cursor exposes results from a query on a SQLiteDatabase
        Cursor cursor = db.rawQuery(query, null);
        //move the cursor to the first row of the results
        cursor.moveToFirst();

        //See if there are anymore results
        while (!cursor.isAfterLast()) {

            if (cursor.getString(cursor.getColumnIndex("title")) != null) {

                Appointment appointment = new Appointment(cursor.getString(cursor.getColumnIndex("date")),
                        cursor.getString(cursor.getColumnIndex("time")),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("details")));
                list.add(appointment);
            }
            cursor.moveToNext();
        }
        db.close();
        return list;
    }


    public List<Appointment> displayAppointments() { //Goes through the database and returns all  the appoints in the database
// used in the search function
        List<Appointment> list = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_APPOINTMENTS + ";";

        //Cursor exposes results from a query on a SQLiteDatabase
        Cursor cursor = db.rawQuery(query, null);
        //move the cursor to the first row of the results
        cursor.moveToFirst();

        //See if there are anymore results
        while (!cursor.isAfterLast()) {

            if (cursor.getString(cursor.getColumnIndex("title")) != null) {

                Appointment appointment = new Appointment(cursor.getString(cursor.getColumnIndex("date")),
                        cursor.getString(cursor.getColumnIndex("time")),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("details")));
                list.add(appointment);
            }
            cursor.moveToNext();
        }
        db.close();
        return list;
    }


    public void clearTable(String TABLE_NAME) { //Deletes the content in a table when the name of the table is passed

        SQLiteDatabase db = getWritableDatabase();
        String clearDBQuery = "DELETE FROM " + TABLE_NAME;
        db.execSQL(clearDBQuery);

    }
}