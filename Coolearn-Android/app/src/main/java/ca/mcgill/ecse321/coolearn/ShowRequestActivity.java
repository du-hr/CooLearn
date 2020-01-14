package ca.mcgill.ecse321.coolearn;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * The Show Page for a session request for the logged in tutor.
 * The tutor can view the details of the session, such as the date and time, the room number
 * and the student or students who have requested the session. The tutor can then decide if he or she
 * wants to accept or reject the session request if they haven't done so already.
 *
 * @author group 11
 * @version 1.1
 */
public class ShowRequestActivity extends AppCompatActivity {
    
    private String error = null;
    private int userRoleId;
    private SessionRequest request;
    private int sessionId;

    private TextView courseName;
    private TextView status;
    private Button acceptButton;
    private Button rejectButton;
    private TextView date;
    private TextView startTime;
    private TextView endTime;
    private TextView studentNames;
    private TextView roomNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "We love Marwan Kanaan :P", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setTitle("Show Request");

        //Get intent extras
        this.userRoleId = LoginActivity.getUserRoleId();
        this.request = getIntent().getParcelableExtra("sessionRequest");
        this.sessionId = request.getId();

        //Link TextView elements with instance variables
        courseName = findViewById(R.id.course);
        status = findViewById(R.id.status);
        acceptButton = findViewById(R.id.accept_button);
        rejectButton = findViewById(R.id.reject_button);
        date = findViewById(R.id.date);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        studentNames = findViewById(R.id.students);
        roomNumber = findViewById(R.id.roomNumber);

        //Set text in the TextView
        courseName.setText(request.getCourseName());
        status.setText(request.getStatus());
        date.setText(request.getDate());
        startTime.setText(request.getStartTime());
        endTime.setText(request.getEndTime());
        studentNames.setText(request.getStudents());
        roomNumber.setText("" + request.getRoomId());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.request_menu, menu);
        return true;
    }

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

    /**
     * Updates the error TextView on the ShowRequest page by
     * setting the visibility of tvError to GONE when there is no error to show
     * and to VISIBLE when there is an error to display
     */
    private void refreshErrorMessage() {
        // set the error message
        TextView tvError = (TextView) findViewById(R.id.error);
        tvError.setText(error);

        if (error == null || error.length() == 0) {
            tvError.setVisibility(View.GONE);
        } else {
            tvError.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Accepts a request if it is pending.
     * Updates error message otherwise.
     * @param v
     */
    public void acceptRequest(View v){
        error = "";
        HttpUtils.put("dashboard/" + userRoleId + "/" + sessionId + "/accept", new RequestParams(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                refreshErrorMessage();
                try {
                    refreshErrorMessage();
                    status.setText(response.getString("status"));
                    roomNumber.setText("" + response.getInt("roomId"));
                }catch(Exception e) {
                    error += e.getMessage();
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

    /**
     * Rejects a request if it is pending.
     * Updates error message otherwise.
     * @param v
     */
    public void rejectRequest(View v){
        error = "";
        HttpUtils.put("dashboard/" + userRoleId + "/" + sessionId + "/decline", new RequestParams(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                refreshErrorMessage();
                try {
                    refreshErrorMessage();
                    status.setText(response.getString("status"));
                }catch(Exception e) {
                    error += e.getMessage();
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
