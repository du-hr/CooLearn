package ca.mcgill.ecse321.coolearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Represents the main dashboard of the app that
 * enables the user to access the various sub activities,
 * which for this version include updating availabilities,
 * changing prices, and viewing upcoming sessions
 * @author group 11
 * @version 1.1
 */
public class DashboardActivity extends AppCompatActivity {

    /**
     * Called automatically by superclass
     *
     * Initializes the page with a welcome message
     * tailored to the currently logged in user
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Create variables for content
        Button setPriceBtn = (Button) findViewById(R.id.setPriceButton);
        Button viewRequestBtn = (Button) findViewById(R.id.viewRequestButton);
        Button setAvailBtn = (Button) findViewById(R.id.updateAvailabilitiesButton);
        TextView welcomeMessage = (TextView) findViewById(R.id.welcomeMessage);
        String userName = LoginActivity.getUserName();
        if (userName == null) {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        welcomeMessage.setText("Welcome, " + userName);
        setPriceBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //go to price setting screen
                goToPage(UpdatePriceActivity.class);
            }
        });
        viewRequestBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //go to view request screen
                goToPage(RequestListActivity.class);
            }
        });
        setAvailBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //go to availability setting screen
                goToPage(AvailabilityMainActivity.class);
            }
        });
    }

    /**
     * This method is used to be redirect to the corresponding activity once a button is clicked
     * @param c Class to which you want to be redirected
     */
    private void goToPage(Class c) {
        Intent intent = new Intent(DashboardActivity.this, c);
        startActivity(intent);
    }
}
