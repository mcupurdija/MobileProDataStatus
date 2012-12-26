package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContentProvider;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ws.CompanyKsoapWs;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NewVisitActivity extends BaseActivity {
    private static String TAG = "NewVisitActivity";
    
    EditText mCustomer;
    EditText mDate;
    EditText mLineNo;
    
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
		//setContentView(R.layout.activity_new_visit);
	     
    }
    
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
    	/*nextButton = (Button) parent.findViewById(R.id.next_button);
    	nextButton.setOnClickListener(this);*/
    	
    	return super.onCreateView(parent, name, context, attrs);
    	
//    	mCustomer = (EditText)
//        mDate = (EditText)
//        mLineNo = (EditText)
    }
    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
            return true;
		 case R.id.save:
             saveRecord();
             break;
		 case R.id.cancel:
			 NavUtils.navigateUpFromSameTask(this);
            return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void saveRecord() {
		ContentValues values = new ContentValues();
		  values.put(MobileStoreContract.Visits.CUSTOMER_NO, "K00001");
		  values.put(MobileStoreContract.Visits.VISIT_DATE, "2012-120=-20 16:00:00");
		  values.put(MobileStoreContract.Visits.LINE_NO, "1");
		  this.getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, values);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.add_new_menu, menu);
	    return true;
	}

}
