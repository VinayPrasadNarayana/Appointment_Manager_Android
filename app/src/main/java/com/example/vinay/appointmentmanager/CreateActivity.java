package com.example.vinay.appointmentmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    EditText titleET, timeET, detailsET; // for buttons on the create layout
    Button saveBtn;
    SQLite dbHandler; //SQL Query

    //Thesaurus stuff
    EditText thesaurusInput;
    Button thesaurusBtn , thesaurusBtn2;

    ThesaurusAdapter thesaurusAdapter;
    ListView synonymlist; //list view to store the synonyms
    PopupWindow popupWindow;

    //variables to store the input from the text box
    private String inputWord;
    //constant for the thesaurus service key
    public static final String THESAURUS_KEY = "sBqiWR9kIgBuCNcztmRd";
    //variable to store the language
    private String lang = "en_US";

    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create); // layout

        Intent intent = getIntent();
        date = intent.getStringExtra("Date"); // gets the date selected bu the user
        Toast.makeText(getBaseContext() , date , Toast.LENGTH_SHORT).show(); // makes a toast of the date

        //initializing the edit text boxes
        titleET = (EditText) findViewById(R.id.titleEditText); //The title of the appointment
        timeET = (EditText) findViewById(R.id.timeEditText);//The time of the appointment
        detailsET = (EditText) findViewById(R.id.detailsEditText);//The details of the appointment
        saveBtn = (Button) findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(this);

        //initialize the text box and 2 buttons in the thesaurus area
        thesaurusInput = (EditText) findViewById(R.id.wordInput);
        thesaurusBtn = (Button) findViewById(R.id.thesaurusButton);
        thesaurusBtn.setOnClickListener(this);
        thesaurusBtn2 = (Button) findViewById(R.id.thesaurusButton2);
        thesaurusBtn2.setOnClickListener(this);

        dbHandler = new SQLite(this, null, null, 1); //create a new database handler. null can be passed because the helper has all the constants
       // dbHandler.clearTable("appointments");
        printDatabase();// calls the function
    }

    public void printDatabase(){ //This method prints the current database
        String dbString = dbHandler.databaseToString();
        Toast.makeText(getBaseContext() , dbString , Toast.LENGTH_LONG).show();
        titleET.setText(""); timeET.setText("");detailsET.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton : {
                String time = timeET.getText().toString(); // gets the user entered time
                String title = titleET.getText().toString();// gets the user entered title
                String details = detailsET.getText().toString();// gets the user entered details

                if (TextUtils.isEmpty(time)){ // displays the message is the time is empty
                    timeET.setError("Please set a time for the appointment.");
                    return;

                }else if (TextUtils.isEmpty(title)) {// displays the message is the title is empty
                    titleET.setError("Please set a title for the appointment.");
                    return;

                }else if(TextUtils.isEmpty(details)) {// displays the message is the details is empty
                    detailsET.setError("Please set a details for the appointment.");
                    return;

                }else {
                    Appointment appointment = new Appointment(date, time, title, details);
                    int i = dbHandler.createAppointment(appointment); // calls the method from SQLite and returns the value

                    if (i == 1) {
                        errorDialog("Appointment " + title + " on " + date + " was created successfully."); // if the database was created
                        printDatabase();// calls the function
                    } else if (i == -1) {
                        errorDialog("Appointment "+ title +" already exists, please choose a different event title");// if the database was not created
                    }
                }
                break;
            }
            case R.id.thesaurusButton :{
                inputWord = thesaurusInput.getText().toString();

                if(inputWord.equals(null) || inputWord.equals("")){
                    thesaurusInput.setError("Please enter a word and press the button");
                } else{
                    resultPopUp(v);
                }


                thesaurusInput.setText("");
                break;

            }

            case R.id.thesaurusButton2 : {

                int startSelection=detailsET.getSelectionStart();
                int endSelection=detailsET.getSelectionEnd();

                String selectedText = detailsET.getText().toString().substring(startSelection, endSelection);
                Toast.makeText(getBaseContext(),"You selected the word \"" + selectedText + "\"",Toast.LENGTH_SHORT).show();

                inputWord = selectedText;
                resultPopUp(v);
                break;
            }
        }
    }

    public void errorDialog(String error) //This function creates a dialog box
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.DialogTheme);
        builder.setMessage(error);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    ////////
    //Helper method to determine if Internet connection is available.
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     *
     * This function creates a popup window that will display all the results of the
     * returned XML
     *
     * @param v The current view instance is passed
     */
    public void resultPopUp (View v) {

        try {
            //get an instance of layout inflater
            LayoutInflater inflater = (LayoutInflater) CreateActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //initiate the view
            View layout = inflater.inflate(R.layout.list_view_thesaurus,
                    (ViewGroup) findViewById(R.id.popUpList));

            //initialize a size for the popup
            popupWindow = new PopupWindow(layout, 1100, 1100 ,  true);
            // display the popup in the center
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            //Get reference to our ListView
            synonymlist = (ListView) layout.findViewById(R.id.synonymList);

		    /*
		     * If network is available download the xml from the Internet.
		     * If not toast internet error and close the popup
		    */
            if(isNetworkAvailable()){

                SitesDownloadTask download = new SitesDownloadTask();
                download.execute();
            }else{

                Toast.makeText(getBaseContext() , "No internet Connection. Please connect " +
                        "your device to the internet and try again" , Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * AsyncTask that will download the xml file for us and store it locally.
     * After the download is done we'll parse the local file.
     */
    private class SitesDownloadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            //Download the file
            try {
                DownloadFromUrl("http://thesaurus.altervista.org/thesaurus/v1?word=" + inputWord +
                                "&language="+ lang +"&%20key="+ THESAURUS_KEY +"&output=xml",
                        openFileOutput("synonyms.xml", Context.MODE_PRIVATE));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            //setup our Adapter and set it to the ListView.
            thesaurusAdapter = new ThesaurusAdapter(CreateActivity.this, -1,
                    ThesaurusXMLPullParser.getSynonymsFromFile(CreateActivity.this));
            synonymlist.setAdapter(thesaurusAdapter);

        }
    }

    /**
     * This method will try to download the xml form the internet
     * @param URL URL to make the request
     * @param fos The name to store the XML file
     */
    public static void DownloadFromUrl(String URL, FileOutputStream fos) {
        try {

            java.net.URL url = new URL(URL); //URL of the file

			/* Open a connection to that URL. */
            URLConnection connection = url.openConnection();


            //input stream that'll read from the connection
            InputStream is = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            //buffer output stream that'll write to the xml file
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            //write to the file while reading
            byte data[] = new byte[1024];
            int count;
            //loop and read the current chunk
            while ((count = bis.read(data)) != -1) {
                //write this chunk
                bos.write(data, 0, count);
            }

            bos.flush();
            bos.close();

        } catch (IOException e) {
        }
    }
}