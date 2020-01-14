package ca.mcgill.ecse321.coolearn;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * This activity enables the Tutor to update the prices they
 * charge for each course they Tutor.
 * @author group 11
 * @version 1.1
 */
public class UpdatePriceActivity extends AppCompatActivity {

    private EditText price;
    private Button updateButton;
    private List<String> courses = new ArrayList<String>();
    private List<Integer> courses_id = new ArrayList<Integer>();
    private ArrayAdapter<String> adapter;
    private String error = "";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_price);
        //Get dropdown with courses name
        Spinner spinner = (Spinner) findViewById(R.id.courses_name);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, courses);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshCurrentPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView status = (TextView) findViewById(R.id.priceDisplay);
                status.setText("Please Select a Course");
            }
        });
        price = (EditText) findViewById(R.id.newPrice);
        updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener(){
            /**
             * This method is an event handler which calls updatePrice when the button is clicked
             * @param view
             */
            @Override
            public void onClick(View view) {
                updatePrice(price.getText().toString());
            }
        });
        // Get initial content for spinners
        refreshLists(this.getCurrentFocus());
        //Get initial error state
        refreshErrorMessage();
    }

    /**
     * This method calls refresh list to set up drop down menu to display the courses name
     * @param view
     */
    public void refreshLists(View view) {
        refreshList(adapter, courses);
    }

    /**
     * This method gets the list of courses, refreshes error message and notifies adapters of change
     * @param adapter
     * @param courses list of courses obtained from Database
     * This method also sets courses_id to the id of courses
     */
    private void refreshList(final ArrayAdapter<String> adapter, final List<String> courses) {
        error = "";
        int userRoleId = LoginActivity.getUserRoleId();
        HttpUtils.get("dashboard/"+ userRoleId +"/courses", new RequestParams(),  new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                courses.clear();
                for(int i=0; i < response.length(); i++) {
                   courses.add(response.optJSONObject(i).optString("course"));
                   courses_id.add(response.optJSONObject(i).optInt("id"));
                }
                adapter.notifyDataSetChanged();
                refreshErrorMessage();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                }
                refreshErrorMessage();
            }

        });
    }

    /**
     * This method updates the price of a course by creating a PUT request
     * @param newPrice
     */
    private void updatePrice(String newPrice) {
        error = ""; //Reset error to empty string
        RequestParams rp = new RequestParams();
        rp.put("hourlyRate", Double.parseDouble(newPrice)); //create the request param
        Spinner courseNameSpinner = (Spinner) findViewById(R.id.courses_name);
        int courseID = courseNameSpinner.getSelectedItemPosition();
        int userRoleId = LoginActivity.getUserRoleId();
        HttpUtils.put("dashboard/"+ userRoleId +"/courses/" + courses_id.get(courseID), rp,  new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                TextView priceUpdate = (TextView) findViewById(R.id.priceDisplay);
                try {
                    priceUpdate.setText("Current Price: $" + String.format("%.2f",response.getDouble("hourlyRate")));
                } catch (JSONException je) {}
                price.getText().clear();
                refreshErrorMessage();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                }
                refreshErrorMessage();
            }
        });
    }

    /**
     * This method refreshes the error message by checking is the field is not empty or null
     * To set an error message, the field error needs to contain a value
     */
    private void refreshErrorMessage() {
        // set the error message
        TextView errorMessage = (TextView) findViewById(R.id.error);
        errorMessage.setText(error);

        if (error == null || error.length() == 0) {
            errorMessage.setVisibility(View.GONE);
        } else {
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method fills the dropdown with a tutor's course by calling the GET /dashboard/{userRoleId}/courses route
     */
    private void refreshCurrentPrice() {
        int userRoleId = LoginActivity.getUserRoleId();
        HttpUtils.get("dashboard/" + userRoleId + "/courses/", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Spinner spinner = (Spinner) findViewById(R.id.courses_name);
                int courseId = courses_id.get(spinner.getSelectedItemPosition());
                TextView priceDisp = (TextView) findViewById(R.id.priceDisplay);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject course = response.getJSONObject(i);
                        if (course.getInt("id") == courseId) {
                            priceDisp.setText("Current Price: $" + String.format("%.2f",course.getDouble("hourlyRate")));
                            break;
                        }
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                }
                refreshErrorMessage();
            }
        });
    }

}
