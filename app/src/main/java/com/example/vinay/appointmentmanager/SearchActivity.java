package com.example.vinay.appointmentmanager;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Button searchBtn; // search button
    private EditText searchET; // stores user entered details

    SQLite SQLite; // SQL query

    //list view stuff
    AppointmentAdaptor appointmentAdaptor;
    ListView listView;

    //lists to store all the resulting appointments
    List<Appointment> listArr;
    //list to store matching appointments
    List<Appointment> listMatches;

    //variable to store the value input from the textbox
    String searchKeywords;

    //search popup stuff
    private PopupWindow popupWindow;
    TextView titleTV, timeTV, detailsTV, dateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search); // search layout

        //initialising the button and edit text
        searchBtn = (Button) findViewById(R.id.confirmButton);
        searchBtn.setOnClickListener(this);
        searchET = (EditText) findViewById(R.id.searchEditText);
        SQLite = new SQLite(this, null, null, 1); //creates an instance of the SQLite
        listArr = SQLite.displayAppointments(); //call the displayappointment method and store all the appointments in a list


        listView = (ListView) findViewById(R.id.searchList); //initialize the list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //adding a list item click listener
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayClickedSearch(appointmentAdaptor.getItem(position), view);
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.confirmButton: { // when search button is clicked

                try {
                    if (searchET.getText().toString().equals("") || searchET.getText().toString().equals(null)) { // checks if the user has entered something
                        searchET.setError("Please input a Keyword");
                    } else {
                        listMatches = new ArrayList<>(); //initialize a new list of apppointments
                        searchKeywords = searchET.getText().toString();  //assign the edit text value to the searchKeywords variable
                        for (Appointment appointment : listArr) { //see if the arraylist objectcts contain any of the keywords
                            if (appointment.getTitle().contains(searchKeywords)) {
                                listMatches.add(appointment); // adds the appointment into the array id the search keywork matches
                            }
                        }
                        appointmentAdaptor = new AppointmentAdaptor(getBaseContext(), -1, listMatches);
                        listView.setAdapter(appointmentAdaptor);
                        if (listMatches.size() == 0) {
                            Toast.makeText(getBaseContext(), "Couldn't find any matches", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Couldn't find any matches", Toast.LENGTH_SHORT).show();
                }
                searchET.setText("");
                break;
            }
        }
    }

    public void displayClickedSearch(Appointment appointment, View v) { //This function creates a  popup window that will display the selected appointment details

        LayoutInflater inflater = (LayoutInflater) SearchActivity.this   //get an instance of layoutinflater
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View layout = inflater.inflate(R.layout.activity_search_update,   //initiate the view
                (ViewGroup) findViewById(R.id.searchPopupView));

        //initialising the textviews in search popup
        titleTV = (TextView) layout.findViewById(R.id.searchedTitle);
        timeTV = (TextView) layout.findViewById(R.id.searchedTime);
        detailsTV = (TextView) layout.findViewById(R.id.searchedDetails);
        dateTV = (TextView) layout.findViewById(R.id.searchedDate);
        //initialize a size for the popup
        popupWindow = new PopupWindow(layout, 1200, 1600, true);
        // display the popup in the center
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        //setting the textviews
        titleTV.setText(appointment.getTitle());
        timeTV.setText(appointment.getTime());
        detailsTV.setText(appointment.getDetails());
        dateTV.setText(appointment.getDate());


    }
}


