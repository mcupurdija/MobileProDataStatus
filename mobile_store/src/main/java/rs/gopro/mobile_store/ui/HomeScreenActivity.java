package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ws.CompanyKsoapWs;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomeScreenActivity extends ActionMenuActivity {
    private static String TAG = "HomeScreenActivity";
    private static String SESSION_PREFS = "SessionPrefs";
    
    private OnClickListener btnListener = new OnClickListener() {
        public void onClick(View v) {
        	TextView tText = (TextView) findViewById(R.id.textView1);
        	CompanyKsoapWs companyKsoapWs = new CompanyKsoapWs();
        	companyKsoapWs.execute((Void)null);
        	tText.setText(companyKsoapWs.getCompany());
        }
    };
    
    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		Log.i(TAG, "Loga activity created.");
		setContentView(R.layout.home_screen);
		
		SharedPreferences settings = getSharedPreferences(SESSION_PREFS, 0);
	    boolean userLogged = settings.getBoolean("user_logged", false);
	    
	    if (!userLogged) {
	    	Intent loginScreen = new Intent(this, LoginActivity.class);
	    	startActivity(loginScreen);
	    }
		
	    Log.i(TAG, "Bas da vidim kada je usao ovde!");
	    // attach listener to login button
    	Button btnLogin = (Button)findViewById(R.id.button1);
    	btnLogin.setOnClickListener(btnListener);
    }

}
