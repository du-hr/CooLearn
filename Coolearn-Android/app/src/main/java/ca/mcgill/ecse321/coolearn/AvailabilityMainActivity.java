package ca.mcgill.ecse321.coolearn;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Intent;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Represents an activity to modify availabilities on the coolearn system
 * Includes the ability to add, delete, and edit availabilities associated
 * with the currently logged in user
 * @author project group 11
 * @version 1.1
 */
public class AvailabilityMainActivity extends AppCompatActivity {
    private Button addAV;
    private List<String> availabilities = new ArrayList<String>();
    private List<Integer> avIDs = new ArrayList<Integer>();
    private ArrayAdapter<String> adapter;

    /**
     * Initializes the activity, rendering the existing availabilities from the
     * coolearn server, and creating buttons to add/edit/delete them, as well as
     * a back button
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_availability);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "No new notifications", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Spinner spinner = findViewById(R.id.avSpinner);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, availabilities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        addAV = findViewById(R.id.addAV);
        //Initializes the actions of the buttons
        addAV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_add_av();
            }
        });
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AvailabilityMainActivity.this, DashboardActivity.class);
                startActivity(i);
            }
        });

        Button editAvailBtn = findViewById(R.id.editAvailBtn);
        editAvailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_av();
            }
        });

        Button deleteBtn = (Button) findViewById(R.id.deleteAV);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedAv();
            }
        });

        refreshLists(this.getCurrentFocus());
    }

    /**
     * Deletes whichever availability is selected by the user
     */
    public void deleteSelectedAv() {
        Spinner spinner = (Spinner) findViewById(R.id.avSpinner);
        if (spinner.getSelectedItemPosition() == -1) {
            return;
        }
        int id = avIDs.get(spinner.getSelectedItemPosition());
        HttpUtils.delete("dashboard/" + LoginActivity.getUserRoleId() +
                        "/availabilities/" + id, new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                refreshLists(getCurrentFocus());
            }

            @Override
            public void onFailure(int status, Header[] headers, byte[] resp, Throwable e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This method calls refresh list to set up drop down menu to display the availabilities
     * @param view the current view
     */
    public void refreshLists(View view) {
        refreshList(adapter, availabilities);
    }

    /**
     * This method gets the list of availabilities, refreshes error message and notifies adapters of change
     * @param adapter
     * @param avs list of availabilities obtained from Database
     * This method also sets avIDs to the id of availabilities
     */
    private void refreshList(final ArrayAdapter<String> adapter, final List<String> avs) {
        int userRoleId = LoginActivity.getUserRoleId();
        HttpUtils.get("dashboard/"+ userRoleId +"/availabilities", new RequestParams(),  new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                avs.clear();
                avIDs.clear();
                for(int i=0; i < response.length(); i++) {
                    JSONObject av = response.optJSONObject(i);
                    String s = "";
                    try {
                        String day = av.getString("dayOfWeek");
                        s += day.substring(0,1).toUpperCase();
                        s += day.substring(1).toLowerCase();
                        s += "s from ";
                        String start = av.getString("startTime");
                        String end = av.getString("endTime");
                        s += start.substring(0,start.length()-3) + "h to ";
                        s += end.substring(0,end.length()-3) + "h";
                    } catch (Exception e) {s+=e.getMessage();}
                    avs.add(s);
                    avIDs.add(response.optJSONObject(i).optInt("id"));
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }

        });
    }

    /**
     * This method allows you to move to the add availability page
     */
    public void open_add_av() {
        Intent intent = new Intent(this, AddAvailability.class);
        intent.putExtra("availId",-1);
        startActivity(intent);
    }

    /**
     * This method loads the data for an availability and moves to add availability activity
     */
    public void edit_av() {
        Spinner spinner = findViewById(R.id.avSpinner);
        if (spinner.getSelectedItemPosition() == -1) {
            return;
        }
        int id = avIDs.get(spinner.getSelectedItemPosition());
        Intent intent = new Intent(this, AddAvailability.class);
        intent.putExtra("availId",id);
        startActivity(intent);
    }

    /**
     * Adds options to the drop down menu
     * Not called here, but overridden to interface with our dropdown menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.availability_menu, menu);
        return true;
    }

    /**
     * When an option is selected, save the id for later use
     * @param item the item that was selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
