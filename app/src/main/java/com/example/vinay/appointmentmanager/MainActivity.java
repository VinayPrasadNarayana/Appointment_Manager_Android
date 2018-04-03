package com.example.vinay.appointmentmanager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button crtAppoBtn,editAppoBtn,delAppoBtn,moveAppoBtn,searchBtn; // 5 buttons as on main menu
    Button deleteAllBtn , selectDeleteBtn; // for when the delete button is clicked
    CalendarView calendarView; // for retriving date

    private String date; // storing the date
    PopupWindow popupWindow; // for delete menu

    SQLite SQLite; // WhereSQL is situated

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Main activity display

        //initializing the buttons and adding onclick
        crtAppoBtn = (Button) findViewById(R.id.create);
        crtAppoBtn.setOnClickListener(this); // listens to create button

        editAppoBtn = (Button) findViewById(R.id.ViewEdit);
        editAppoBtn.setOnClickListener(this); // listens to View and edit button

        delAppoBtn = (Button) findViewById(R.id.delete);
        delAppoBtn.setOnClickListener(this); // listens to delete button

        moveAppoBtn = (Button) findViewById(R.id.move);
        moveAppoBtn.setOnClickListener(this); // listens to move button

        searchBtn = (Button) findViewById(R.id.Search);
        searchBtn.setOnClickListener(this); // listens to Search button

        calendarView = (CalendarView) findViewById(R.id.calendar);
        calendarView.setMinDate(1463918226920L); // to display the calander in the year 2018
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dateSelected = simpleDateFormat.format(new GregorianCalendar(year, month, dayOfMonth).getTime());
                date = dateSelected;

            }
        });

        //initialize the default date  and assign it to the date variable in case if he user doesn't
        //click on any date.
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateSelected = simpleDateFormat.format(new Date(calendarView.getDate()));
        date = dateSelected;

        //creates an instance of the SQLite
        SQLite = new SQLite(this, null, null, 1);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.create :{ // when the create button is clicked

                Intent intent = new Intent(getBaseContext() , CreateActivity.class);
                intent.putExtra("Date" , date ); // format - dd/MM/yyyy
                startActivity(intent); //starts the create activity

                break;

            }
            case R.id.delete : {// when the delete button is clicked
                deleteAppointmentPopup(v);
                break;
            }
            case R.id.ViewEdit:{// when the View and edit button is clicked
                Intent intent = new Intent(getBaseContext() , EditActivity.class);
                intent.putExtra("Date" , date ); // format - dd/MM/yyyy
                startActivity(intent); //starts the View and edit activity
                break;
            }
            case R.id.move :{// when the move button is clicked
                Intent intent = new Intent(getBaseContext() , MoveActivity.class);
                intent.putExtra("Date" , date ); // format - dd/MM/yyyy
                startActivity(intent); //starts the move activity
                break;
            }
            case R.id.Search :{// when the create button is clicked

                Intent intent = new Intent(getBaseContext() , SearchActivity.class);
                startActivity(intent); //starts the search activity
                break;

            }
        }

    }

    private void deleteAppointmentPopup (View v) { // the window that pops up when the delete button is clicked

        try {
            //get an instance of layoutinflater
            LayoutInflater inflater = (LayoutInflater) MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //initiate the view
            final View layout = inflater.inflate(R.layout.popup,
                    (ViewGroup) findViewById(R.id.popupView));

            //initialize a size for the popup
            popupWindow = new PopupWindow(layout, 1100, 900 ,  true);
            // display the popup in the center
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            //Deletes all the appointments for a given date
            deleteAllBtn = (Button) layout.findViewById(R.id.delAllButton); // gets the delete button
            deleteAllBtn.setOnClickListener(new View.OnClickListener() { // checks if delete all button is clicked
                @Override
                public void onClick(View v) {
                    Toast.makeText(getBaseContext(),"Deleted all the appointments on "+ date,Toast.LENGTH_LONG).show();
                    SQLite.deleteAppointments(date); // performs this SQL Query
                    popupWindow.dismiss(); // dismisses the popup
                }
            });

            //Opens up the list of appointments for the given date
            selectDeleteBtn = (Button) layout.findViewById(R.id.selectedDelButton); // checks for delete button
            selectDeleteBtn.setOnClickListener(new View.OnClickListener() { // checks if delete button is clicked
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getBaseContext() , DeleteActivity.class);
                    intent.putExtra("Date" , date ); // format - dd/MM/yyyy

                    startActivity(intent); // starts the deleteactivity
                    popupWindow.dismiss(); // dismisses the popup
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
