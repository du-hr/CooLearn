package ca.mcgill.ecse321.coolearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * The RequestListActivity allows the tutor to see the list of accepted and pending session requests.
 * The tutor can select a session request to see in more detail. When this session request is pressed,
 * an intent from RequestListActivity to ShowRequestActivity is created where important information such
 * as the userRoleId and the session request object are passed as extras of the intent.
 *
 * @author group 11
 * @version 1.1
 */
public class RequestListActivity extends AppCompatActivity {

    private ListView listView;
    private int userRoleId;
    private ArrayList<SessionRequest> requests = new ArrayList<>();
    private ArrayAdapter sessionRequestsAdapter;
    private String error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        setTitle("See Requests");
        listView = findViewById(R.id.listview);
        userRoleId = LoginActivity.getUserRoleId();

        seeListOfRequests();

        sessionRequestsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requests);
        listView.setAdapter(sessionRequestsAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RequestListActivity.this, ShowRequestActivity.class);
                intent.putExtra("sessionRequest", requests.get(position));
                startActivity(intent);
            }
        });

    }

    /**
     * Displays a long toast when an error occurs.
     */
    private void refreshErrorMessage() {
        if(error != null && error.length() != 0){
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Refreshes the list of requests by calling seeListOfRequests().
     * @param v
     */
    public void refreshList(View v){
        requests.clear();
        seeListOfRequests();
    }

    /**
     * Updates the list of pending and accepted session requests for the logged in tutor.
     * Sends a REST GET request to the coolearn-backend server and parses the JSONArray returned.
     * The JSONArray has the list of session requests for the logged in tutor, denoted by the userRoleId
     * which was initially by the LoginActivity to other activities. Each JSON object in the JSONArray
     * is parsed and a corresponding SessionRequest object is created and added to the list of requests.
     * The method notifies the sessionRequestsAdapter that the data set has changed (notifyDataSetChanged()), and
     * the list updates.
     *
     * If there is a failure, the error message is updated and displayed.
     *
     */
    public void seeListOfRequests(){
        error = "";
        HttpUtils.get("dashboard/" + userRoleId, new RequestParams(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try{
                    for(int i = 0; i < response.length(); i++){
                        JSONObject request = response.getJSONObject(i);
                        String status = request.getString("status");
                        String startTime =  request.getString("startTime");
                        String endTime = request.getString("endTime");
                        String date = request.getString("date");
                        int tutorId = request.getInt("tutorId");
                        int id = request.getInt("id");
                        String tutorName = request.getString("tutorName");
                        String courseName = request.getString("courseName");
                        int roomId = request.getInt("roomId");
                        JSONArray reviewIds = request.getJSONArray("reviewIds");
                        List<Integer> reviewIdsList = new ArrayList<>();
                        if(reviewIds.length() >= 1){
                            for(int j = 0; j < reviewIds.length(); j++){
                                reviewIdsList.add(reviewIds.getInt(i));
                            }
                        }
                        JSONArray studentIds = request.getJSONArray("studentIds");
                        JSONArray studentNames = request.getJSONArray("studentNames");
                        List<Integer> studentIdsList = new ArrayList<>();
                        List<String> studentNamesList = new ArrayList<>();
                        if(studentIds.length() >= 1){
                            for(int j = 0; j < studentIds.length(); j++){
                                studentIdsList.add(studentIds.getInt(j));
                                studentNamesList.add(studentNames.getString(j));
                            }
                        }
                        requests.add(new SessionRequest(id, status, startTime, endTime, date, tutorId, studentNamesList, studentIdsList, reviewIdsList, tutorName, courseName, roomId));
                    }
                    sessionRequestsAdapter.notifyDataSetChanged();
                }catch(Exception e){
                    error += e.getMessage();
                }
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


}
