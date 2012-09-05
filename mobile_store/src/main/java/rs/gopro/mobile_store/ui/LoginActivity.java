package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * 
 * @author Vladimir Makevic
 *
 */
public class LoginActivity extends Activity {

    private static String TAG = "LoginActivity";
    private static String SESSION_PREFS = "SessionPrefs";
    
    private OnClickListener btnLoginListener = new OnClickListener() {
        public void onClick(View v) {
        	EditText editTextUser = (EditText) findViewById(R.id.editTextUsername);
        	EditText editTextPass = (EditText) findViewById(R.id.editTextPassword);
        	boolean loginSignal = doLogin(editTextUser.getText().toString(), editTextPass.getText().toString());
        	if (loginSignal) {
        		try {
					finalize();
					Log.i(TAG, "User logged successfully.");
				} catch (Throwable e) {
					Log.e(TAG, "Cannot finalize!",e);
					e.printStackTrace();
				}
        	} else {
        		TextView textViewError = (TextView) findViewById(R.id.textViewError);
        		textViewError.setText(R.string.login_error);
        	}
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
    	
    	Log.i(TAG, "Log activity created.");
		setContentView(R.layout.login);
    	
    	// hide before login no need to see options
    	ActionBar actionBar = getActionBar();
    	actionBar.hide();
    	
    	// attach listener to login button
    	Button btnLogin = (Button)findViewById(R.id.btnLogin);
    	btnLogin.setOnClickListener(btnLoginListener);

    }

    private boolean doLogin(String username, String pass) {
    	SharedPreferences settings = getSharedPreferences(SESSION_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.putBoolean("user_logged", true);
        editor.commit();
    	return true;
    }
    
}

