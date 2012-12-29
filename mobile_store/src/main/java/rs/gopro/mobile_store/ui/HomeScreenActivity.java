package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeScreenActivity extends BaseActivity implements OnClickListener{
    private static String TAG = "HomeScreenActivity";
    private static String SESSION_PREFS = "SessionPrefs";
    
    FirstSectionFragment firstSectionFragment;
    SecondSectionFragment secondSectionFragment;
    ThirdSectionFragment thirdSectionFragment;
    Button nextButton;
    
   /* private OnClickListener btnListener = new OnClickListener() {
        public void onClick(View v) {
        	TextView tText = (TextView) findViewById(R.id.textView1);
        	CompanyKsoapWs companyKsoapWs = new CompanyKsoapWs();
        	companyKsoapWs.execute((Void)null);
        	tText.setText(companyKsoapWs.getCompany());
        }
    };*/
    
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
		setContentView(R.layout.activity_home_screen);
		
		SharedPreferences settings = getSharedPreferences(SESSION_PREFS, 0);
	    boolean userLogged = settings.getBoolean("user_logged", false);
	    
	    if (userLogged) {
	    	Intent loginScreen = new Intent(this, LoginActivity.class);
	    	startActivity(loginScreen);
	    }
		
	    Log.i(TAG, "Bas da vidim kada je usao ovde!");
	   
	    final FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction fragmentTransaction = fm.beginTransaction();
	    firstSectionFragment = (FirstSectionFragment) fm.findFragmentById(R.id.first_section_fragment);
	    if(firstSectionFragment == null){
	    	firstSectionFragment = new FirstSectionFragment();
	    	 fragmentTransaction.add(R.id.first_section_fragment, firstSectionFragment);
	    }
	    secondSectionFragment = (SecondSectionFragment) fm.findFragmentById(R.id.second_section_fragment);
	    if(secondSectionFragment == null){
	    	secondSectionFragment = new SecondSectionFragment();
	    	 fragmentTransaction.add(R.id.second_section_fragment, secondSectionFragment);
		 
	    }
	    thirdSectionFragment = (ThirdSectionFragment) fm.findFragmentById(R.id.third_section_fragment);
	    if(thirdSectionFragment == null){
	    	thirdSectionFragment = new ThirdSectionFragment();
	    	 fragmentTransaction.add(R.id.third_section_fragment, thirdSectionFragment);
	    }
	    fragmentTransaction.commit();
	     
    }
    
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
    	/*nextButton = (Button) parent.findViewById(R.id.next_button);
    	nextButton.setOnClickListener(this);*/
    	
    	return super.onCreateView(parent, name, context, attrs);
    }

	@Override
	public void onClick(View v) {
		Intent intent =  new Intent(SESSION_PREFS);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_menu, menu);
	    return true;
	}
}
