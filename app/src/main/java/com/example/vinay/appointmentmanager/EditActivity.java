package com.example.vinay.appointmentmanager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    SQLite SQLite; // SQL query
    String date; // stores date

    Button editBtn; // edit button
    EditText appointmentNumberET; // gets the appointment number

    //list view stuff
    ArrayAdapter adapter;
    ListView listView;

    //lists to store the resulting appointments
    List<Appointment> listArr;
    ArrayList<String> arrayList;

    //variable to store the value input from the textbox
    String appointmentNumber;

    //Update popup
    PopupWindow popupWindow;
    Button updateBtn; //update button
    EditText titleET, timeET, detailsET; // to get the new user entered details of the appointment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit); // edit layout
        Intent intent = getIntent();
        date = intent.getStringExtra("Date"); //get the selected date
        appointmentNumberET = (EditText) findViewById(R.id.appointmentNumberEditText);
        editBtn = (Button) findViewById(R.id.EditButton); //initialising the button
        SQLite = new SQLite(this, null, null, 1);  //creates an instance of the SQLite
        listArr = SQLite.displayAppointments(date);
        arrayList = new ArrayList<>();
        for (int j = 0; j < listArr.size(); j++) {
            arrayList.add(j + 1 + ". " + listArr.get(j).getTime() + " " + listArr.get(j).getTitle());
        }
        adapter = new ArrayAdapter<String>(this, R.layout.activity_view, arrayList);
        listView = (ListView) findViewById(R.id.appointmentList);
        listView.setAdapter(adapter);

        editBtn.setOnClickListener(new View.OnClickListener() { // when the edit button is clicked
            @Override
            public void onClick(View v) {
                appointmentNumber = appointmentNumberET.getText().toString();
                if (appointmentNumber.equals(null) || appointmentNumber.equals("")) {
                    editBtn.setError("Please select a valid appointment number"); // if no number is entered by the user
                    appointmentNumberET.setText("");
                    return;
                } else {
                    try {
                        updateAppointmentPopup(v);
                    } catch (IndexOutOfBoundsException e) { //if the number entered is not correct
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

    private void updateAppointmentPopup (View v) { // this popup window lets the user edit the appointment details

        try {
            LayoutInflater inflater = (LayoutInflater) EditActivity.this //get an instance of layoutinflater
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.activity_edit_update, //initiate the view
                    (ViewGroup) findViewById(R.id.updatePopupView));
            popupWindow = new PopupWindow(layout, 1100, 1650 ,  true);  //initialize a size for the popup
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0); // display the popup in the center

            //initialising the update popup button and edit texts
            titleET = (EditText) layout.findViewById(R.id.updateTitleEditText);
            timeET = (EditText) layout.findViewById(R.id.updateTimeEditText);
            detailsET = (EditText) layout.findViewById(R.id.updateDetailsEditText);

            //Updates the selected appointment
            updateBtn = (Button) layout.findViewById(R.id.updateButton);
            updateBtn.setOnClickListener(new View.OnClickListener() { // when update button is pressed
                @Override
                public void onClick(View v) {
                    try {
                        int success = SQLite.updateAppointment(listArr.get(Integer.parseInt(appointmentNumber) - 1),
                                timeET.getText().toString(), titleET.getText().toString(), detailsET.getText().toString());

                        if (success == 1) {
                            Toast.makeText(getBaseContext(), "Successfully updated the appointment", Toast.LENGTH_LONG).show();

                        } else if (success == -1) {
                            Toast.makeText(getBaseContext(), "There's no appointment numbered " + appointmentNumber +
                                    ". Please try again with a valid number.", Toast.LENGTH_SHORT).show();
                        }
                        //refreshes the page
                        finish();
                        startActivity(getIntent());

                    }catch (IndexOutOfBoundsException e){
                        Toast.makeText(getBaseContext(), "Couldn't find the specified appointment in the database." , Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(getBaseContext(), "Invalid input. Please try again with a valid number." , Toast.LENGTH_SHORT).show();
                    }
                    timeET.setText(""); titleET.setText(""); detailsET.setText("");
                    popupWindow.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


