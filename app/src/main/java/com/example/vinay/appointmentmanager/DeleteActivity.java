package com.example.vinay.appointmentmanager;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class DeleteActivity extends AppCompatActivity {
    SQLite SQLite; // SQL query
    String date; // stores date

    Button delBtn; // delete button
    EditText appointmentNumberET; // gets the appointment number

    ArrayAdapter adapter;
    ListView listView; // displaying the Appointments

    //lists to store the appointments
    List<Appointment> listArr;
    ArrayList<String> arrayList;

    String appointmentNumber; //variable to store the value input from the appointment number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete); // select the delete layout

        Intent intent = getIntent();
        date = intent.getStringExtra("Date");  //get the selected date
        appointmentNumberET = (EditText) findViewById(R.id.appointmentNumberEditText);
        delBtn = (Button) findViewById(R.id.delButton); //initialising the button
        SQLite = new SQLite(this, null, null, 1);  //creates an instance of the SQLite
        listArr = SQLite.displayAppointments(date);
        arrayList = new ArrayList<>();
        for (int j = 0; j < listArr.size(); j++) {
            arrayList.add(j + 1 + ". " + listArr.get(j).getTime() + " " + listArr.get(j).getTitle());
        }

        adapter = new ArrayAdapter<String>(this, R.layout.activity_view, arrayList);
        listView = (ListView) findViewById(R.id.appointmentList);
        listView.setAdapter(adapter);

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointmentNumber = appointmentNumberET.getText().toString();
                if (appointmentNumber.equals(null) || appointmentNumber.equals("")) {
                    delBtn.setError("Please select a valid appointment number");//if there is no number entered by the user
                    appointmentNumberET.setText("");
                    return;
                } else {
                    try {
                        errorDialog("Would you like to delete event : “ " +
                                listArr.get(Integer.parseInt(appointmentNumber) - 1).getTitle() + " ”?");
                        // if the user entered number is wrong
                    } catch (IndexOutOfBoundsException e) {
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

    public void errorDialog(String error) //This function creates a  error dialog
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.DialogTheme);
        builder.setMessage(error);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getBaseContext(), "Deleted the " +
                                listArr.get(Integer.parseInt(appointmentNumber) - 1).getTitle() +
                                " appointment.", Toast.LENGTH_SHORT).show();
                        SQLite.deleteAppointments(date , listArr.get(Integer.parseInt(appointmentNumber)-1).getTitle());// SQL query
                        //adapter.notifyDataSetChanged(); //refreshes the list, NOT WORKING
                        dialog.dismiss();

                        //bad way to refresh
                        finish();
                        startActivity(getIntent());
                    }
                });
        builder.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}


