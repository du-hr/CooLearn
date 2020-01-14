package ca.mcgill.ecse321.coolearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * The LoginActivity enables a user to login to the CooLearn system by
 * entering their username and password, which are then verified
 * by the CooLearn backend before bringing the user to the main activity.
 *
 * When a successful login occurs, the LoginActivity conveys the login information
 * to the Main Activity using 'extras' stored in the intent. Once a user has logged in,
 * their user id can be accessed with getIntent().getIntExtra("userRoleId",-1) and
 * their name can be accessed with getIntent().getStringExtra("userName") from within
 * the main activity.
 *
 * @author Group 11
 * @version 1.1
 */
public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginBtn;
    private TextView errorDisp;
    public static final LoginCredentials CREDENTIALS = new LoginCredentials();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.editEmail);
        password = (EditText) findViewById(R.id.editPassword);
        loginBtn = (Button) findViewById(R.id.submitButton);
        errorDisp = (TextView) findViewById(R.id.loginError);
        errorDisp.setText("");
        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                validate(email.getText().toString(), password.getText().toString());
            }
        });
    }



    /**
     * Get the user role id of the currently logged in user
     * @return the user role id of the currently logged in user
     */
    public static int getUserRoleId() {
        return CREDENTIALS.getUserRoleId();
    }

    /**
     * Get the name of the currently logged in user
     * @return the name of the currently logged in user
     */
    public static String getUserName() {
        return CREDENTIALS.getUserName();
    }


    /**
     * Validates a user's credentials & sends them to the main activity if successful.
     * Displays any errors in credentials if the credentials given are incorrect.
     * @param username The username given by the user
     * @param password The password given by the user
     */
    private void validate(String username, String password) {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("emailAddress", username);
            jsonParams.put("password", password);
            jsonParams.put("type", "Tutor");
        } catch (Exception e){e.printStackTrace();}
        HttpUtils.post(getApplicationContext(),"login", jsonParams,  new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                int id;
                String name;
                try {
                    id = response.getInt("id");
                    name = response.getString("name");
                } catch (Exception e) {
                    System.out.println("error encountered parsing json");
                    return;
                }
                CREDENTIALS.setUserName(name);
                CREDENTIALS.setUserRoleId(id);
                startActivity(intent);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    errorDisp.setText(errorResponse.getString("message"));
                } catch (Exception e) {
                    errorDisp.setText(errorResponse + "");
                }
            }
        });
    }
}
