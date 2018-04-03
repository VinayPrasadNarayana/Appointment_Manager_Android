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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public class MoveActivity extends AppCompatActivity {
    SQLite SQLite; // SQL query
    String date; // stores date

    EditText appointmentNumberET; // gets the appointment number

    //list view stuff
    ArrayAdapter adapter;
    ListView listView;

    //lists to store the resulting appointments
    List<Appointment> listArr;
    ArrayList<String> arrayList;

    String appointmentNumber; // to store the value input from the user

    //Update popup
    PopupWindow popupWindow;

    //Move Popup
    Button moveBtn; //move button
    CalendarView calendarView;
    String popupDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move); // move layout

        Intent intent = getIntent();
        date = intent.getStringExtra("Date"); //get the selected date
        appointmentNumberET = (EditText) findViewById(R.id.appointmentNumberEditText);
        moveBtn = (Button) findViewById(R.id.MoveButton); //initialising the button
        SQLite = new SQLite(this, null, null, 1); //creates an instance of the SQLite
        listArr = SQLite.displayAppointments(date); // display the appointments
        arrayList = new ArrayList<>();
        for (int j = 0; j < listArr.size(); j++) {
            arrayList.add(j + 1 + ". " + listArr.get(j).getTime() + " " + listArr.get(j).getTitle());
        }
        adapter = new ArrayAdapter<String>(this, R.layout.activity_view, arrayList);
        listView = (ListView) findViewById(R.id.appointmentList);
        listView.setAdapter(adapter);
        moveBtn.setOnClickListener(new View.OnClickListener() { // when the move button is clicked
            @Override
            public void onClick(View v) {
                appointmentNumber = appointmentNumberET.getText().toString();
                if (appointmentNumber.equals(null) || appointmentNumber.equals("")) {
                    moveBtn.setError("Please select a valid appointment number"); // checks if the user has enter any number
                    appointmentNumberET.setText("");
                    return;
                } else {
                    try { // calls a method if the user entered value is correct
                        moveAppointmentPopup(v);
                    } catch (IndexOutOfBoundsException e) { // displays if the user entered number is nt valid

                        appointmentNumberET.setText("");
                        Toast.makeText(getBaseContext(), "There's no appointment numbered " + appointmentNumber +
                                ". Please try again with a valid number.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        appointmentNumberET.setText("");
                        Toast.makeText(getBaseContext(), "Invalid input. Please try again with a valid number.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void moveAppointmentPopup (View v) { // method to move an appointment
        try {
            LayoutInflater inflater = (LayoutInflater) MoveActivity.this  //get an instance of layout inflater
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.activity_move_update, //initiate the view
                    (ViewGroup) findViewById(R.id.movePopupView));

            popupWindow = new PopupWindow(layout, 1100, 1800 ,  true);  //initialize a size for the popup

            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);    // display the popup in the center

            calendarView = (CalendarView) layout.findViewById(R.id.calendarViewPopup); // finds the date select on the calender
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String dateSelected = simpleDateFormat.format(new GregorianCalendar(year, month, dayOfMonth).getTime());
                    popupDate = dateSelected;
                }
            });

            //Updates the selected appointment
            moveBtn = (Button) layout.findViewById(R.id.moveButton);
            moveBtn.setOnClickListener(new View.OnClickListener() { // when the move button is clicked
                @Override
                public void onClick(View v) {
                    try {
                        SQLite.moveAppointment(listArr.get(Integer.parseInt(appointmentNumber) - 1) , popupDate);
                        //refreshes the page
                        finish();
                        startActivity(getIntent());
                    }catch (IndexOutOfBoundsException e){
                        Toast.makeText(getBaseContext(), "Couldn't find the specified appointment in the database." , Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(getBaseContext(), "Invalid input. Please try again with a valid number." , Toast.LENGTH_SHORT).show();
                    }
                    popupWindow.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


