package ca.mcgill.ecse321.coolearn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

/**
 * This activity allows the user to add or edit availabilities.
 * To specify edit mode, pass an id using intent extras,
 * with the name 'availId' from the calling activity representing
 * the id of the availability being edited.
 * If no id is specified, or if -1 is specified as the id,
 * the form will add an availability.
 */
public class AddAvailability extends AppCompatActivity {

    private static final String TAG = "AddAvailability";

    private Button startDateButton, endDateButton, startTimeButton, endTimeButton,submit_av;
    private TextView startDateTextView, endDateTextView, startTimeTextView, endTimeTextView, errorTextView;
    private Spinner DOW;
    private ArrayAdapter<String> adapter;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_av);
        id = getIntent().getIntExtra("availId",-1);

        //get the spinner from the xml.
        DOW = findViewById(R.id.DOW);
        //create a list of items for the spinner.
        String[] items = new String[]{"Please select a value", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        DOW.setAdapter(adapter);

        startDateButton = findViewById(R.id.startDateButton);
        endDateButton = findViewById(R.id.endDateButton);
        startTimeButton = findViewById(R.id.startTimeButton);
        endTimeButton = findViewById(R.id.endTimeButton);
        submit_av = findViewById(R.id.submit_av);

        startDateTextView = findViewById(R.id.startDateTextView);
        endDateTextView = findViewById(R.id.endDateTextView);
        startTimeTextView = findViewById(R.id.startTimeTextView);
        endTimeTextView = findViewById(R.id.endTimeTextView);
        errorTextView = findViewById(R.id.createAVError);

        if (id == -1) {
            submit_av.setText("CREATE");
        } else {
            submit_av.setText("UPDATE");
            HttpUtils.get("dashboard/"+LoginActivity.getUserRoleId()+"/availabilities",
                    new RequestParams(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject o = response.getJSONObject(i);
                                    if (o.getInt("id") == id) {
                                        startDateTextView.setText(o.getString("startDate"));
                                        endDateTextView.setText(o.getString("endDate"));
                                        String startTime = o.getString("startTime");
                                        String endTime = o.getString("endTime");
                                        startTimeTextView.setText(startTime.substring(0, startTime.length()-3));
                                        endTimeTextView.setText(endTime.substring(0,endTime.length()-3));
                                        String dow = o.getString("dayOfWeek");
                                        DOW.setSelection(adapter.getPosition(dow));
                                        break;
                                    }
                                } catch (JSONException je) {je.printStackTrace();}
                            }
                        }
                    });
        }


        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleStartDateButton();
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEndDateButton();
            }
        });
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleStartTimeButton();
            }
        });
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEndTimeButton();
            }
        });
        submit_av.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmitAVButton(startDateTextView.getText().toString(),endDateTextView.getText().toString(),
                        startTimeTextView.getText().toString(),endTimeTextView.getText().toString(), DOW.getSelectedItem().toString());
            }
        });
    }

    /**
     * Handle the functionality of the submit button.
     * @return void.
     */
    private void handleSubmitAVButton(String startDate, String endDate, String startTime, String endTime, String dayOfWeek){
        if (dayOfWeek.equals("Please select a value")) {
            errorTextView.setText("A day of the week must be selected");
            return;
        }
        JSONObject jsonParams = new JSONObject();
        //Add key value pairs to jsonparams
        try {
            jsonParams.put("startDate", startDate);
            jsonParams.put("endDate", endDate);
            jsonParams.put("startTime", startTime);
            jsonParams.put("endTime", endTime);
            jsonParams.put("dayOfWeek", dayOfWeek);
        } catch (Exception e){e.printStackTrace();}
        // Call Utility class here
        int id = LoginActivity.getUserRoleId();
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Do nothing?
                Intent intent = new Intent(AddAvailability.this, AvailabilityMainActivity.class);
                startActivity(intent);
                errorTextView.setText("Success!");
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String resp) {
                System.out.println("CAPTURED RESP");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    errorTextView.setText("ERROR: " + errorResponse.getString("message"));
                } catch (Exception e) {
                    errorTextView.setText("An error was encountered that could not be parsed: " + errorResponse.toString());
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String resp, Throwable throwable) {
                //This runs when update is called because of the void response being impossible to convert to json
                Intent intent = new Intent(AddAvailability.this, AvailabilityMainActivity.class);
                startActivity(intent);
            }
        };
        if (this.id == -1) {
            HttpUtils.post(getApplicationContext(), "dashboard/" + id + "/availabilities",
                    jsonParams, responseHandler);
        } else {
            HttpUtils.put(getApplicationContext(), "dashboard/"+id+"/availabilities/"+this.id,
                    jsonParams, responseHandler);
        }

    }

    /**
     * Handle the functionality of the start date button.
     * @return void.
     */

    private void handleStartDateButton() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);
        String currentVal = startDateTextView.getText().toString();
        if (currentVal.contains("-")) {
            String date[] = currentVal.split("-");
            YEAR = Integer.parseInt(date[0]);
            MONTH = Integer.parseInt(date[1]) - 1;
            DATE = Integer.parseInt(date[2]);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);
                String dateText = DateFormat.format("yyyy-MM-dd", calendar1).toString();

                startDateTextView.setText(dateText);
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }
    /**
     * Handle the functionality of the end date button.
     * @return void.
     */
    private void handleEndDateButton() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);
        String currentVal = endDateTextView.getText().toString();
        if (currentVal.contains("-")) {
            String date[] = currentVal.split("-");
            YEAR = Integer.parseInt(date[0]);
            MONTH = Integer.parseInt(date[1]) - 1;
            DATE = Integer.parseInt(date[2]);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);
                String dateText = DateFormat.format("yyyy-MM-dd", calendar1).toString();

                endDateTextView.setText(dateText);
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    /**
     * Handle the functionality of the start time button.
     * @return void.
     */
    private void handleStartTimeButton() {
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
        int MINUTE = calendar.get(Calendar.MINUTE);
        String currentVal = startTimeTextView.getText().toString();
        if (currentVal.contains(":")) {
            String[] time = currentVal.split(":");
            HOUR = Integer.parseInt(time[0]);
            MINUTE = Integer.parseInt(time[1]);
        }
        boolean is24HourFormat = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.i(TAG, "onTimeSet: " + hour +":"+ minute);
                System.out.println("onTimeSet: " + hour +":"+ minute);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, hour);
                calendar1.set(Calendar.MINUTE, minute);
                System.out.println(calendar1.getTime().toString());
                String dateText = DateFormat.format("HH:mm", calendar1).toString();
                startTimeTextView.setText(dateText);
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();
    }

    /**
     * Handle the functionality of the end time button.
     * @return void.
     */

    private void handleEndTimeButton() {
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
        int MINUTE = calendar.get(Calendar.MINUTE);
        String currentVal = endTimeTextView.getText().toString();
        if (currentVal.contains(":")) {
            String[] time = currentVal.split(":");
            HOUR = Integer.parseInt(time[0]);
            MINUTE = Integer.parseInt(time[1]);
        }
        boolean is24HourFormat = DateFormat.is24HourFormat(this);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.i(TAG, "onTimeSet: " + hour + minute);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, hour);
                calendar1.set(Calendar.MINUTE, minute);
                String dateText = DateFormat.format("HH:mm", calendar1).toString();
                endTimeTextView.setText(dateText);
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();
    }

}

