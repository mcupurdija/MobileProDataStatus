package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Users;
import rs.gopro.mobile_store.util.PropertiesUtil;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SalespersonSetupSyncObject;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
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
//	private static String SESSION_PREFS = "SessionPrefs";

	private Button btnRegister;
	
	private OnClickListener btnLoginListener = new OnClickListener() {
		public void onClick(View v) {
			EditText editTextUser = (EditText) findViewById(R.id.editTextUsername);
			EditText editTextPass = (EditText) findViewById(R.id.editTextPassword);
			boolean loginSignal = doLogin(editTextUser.getText().toString(), editTextPass.getText().toString());
			if (loginSignal) {
				try {
					Log.i(TAG, "User logged successfully.");
					finish();
				} catch (Throwable e) {
					Log.e(TAG, "Cannot finish!", e);
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
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "Log activity created.");
		setContentView(R.layout.activity_login);

		// hide before login no need to see options
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		// attach listener to login button
		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(btnLoginListener);

		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnRegister.setVisibility(View.GONE);
		btnRegister.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(LoginActivity.this);
				dialog.setContentView(R.layout.dialog_register_new_user);
				dialog.setTitle("Unesite šifru korisnika");
				
				final EditText text1 = (EditText) dialog.findViewById(R.id.dialog_register_user_no);

				
				Button dialogButton = (Button) dialog.findViewById(R.id.dialogRegisterUserButtonOK);
				// if button is clicked, close the custom dialog
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						String user_no = text1.getText().toString();
						SalespersonSetupSyncObject salespersonSetupSyncObject = new SalespersonSetupSyncObject(user_no);
						Intent syncSalesPerson = new Intent(LoginActivity.this, NavisionSyncService.class);
						syncSalesPerson.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, salespersonSetupSyncObject);
						startService(syncSalesPerson);
						dialog.dismiss();
					}
				});
	 
				dialog.show();
				
			}
		});
	}
	@Override
	public void onBackPressed() {
	    // Do Here what ever you want do on back press;
	}
	private boolean doLogin(String username, String pass) {
		/*
		 * SharedPreferences settings = getSharedPreferences(SESSION_PREFS,
		 * MODE_PRIVATE); SharedPreferences.Editor editor = settings.edit();
		 * editor.putString("username", username);
		 * editor.putBoolean("user_logged", true); editor.commit();
		 */
		if (PropertiesUtil.getLoginSwitch(getAssets())) {
			SharedPreferencesUtil.addSalePersonId(getApplicationContext(), "1");
			addSalesPersonNo("1");
			return true;
		}
		String passFromDB = getPassword(username);
		if (passFromDB != null && passFromDB.equals(pass)) {
			SharedPreferencesUtil.setUserLoginStatus(getApplicationContext(), true);
			return true;
		}
		return false;
	}

	private String getPassword(String username) {
		String password = null;
		Cursor cursor = getContentResolver().query(Users.buildUsernameUri(), UsersQuery.PROJECTION, Users.USERNAME + "= ?", new String[] { username }, null);
		boolean hasEntry = cursor.moveToFirst();
		if (hasEntry) {
			password = cursor.getString(UsersQuery.PASSWORD);
			String salePersonId = cursor.getString(UsersQuery.SALES_PERSON_ID);
			SharedPreferencesUtil.addSalePersonId(getApplicationContext(), salePersonId);
			String userRole = cursor.getString(UsersQuery.NAME);
			SharedPreferencesUtil.addUserRole(getApplicationContext(), userRole);
			addSalesPersonNo(salePersonId);
		} else {
			Log.i(this.getClass().getName(), "Username " + username + " does not exist");
		}
		return password;
	}
	private void addSalesPersonNo(String salePersonId) {
		String salesPersonNo = "";
		Cursor salespersoncursor = getContentResolver().query(MobileStoreContract.SalesPerson.CONTENT_URI, new String[] { MobileStoreContract.SalesPerson.SALE_PERSON_NO }, "_ID=?", new String[] { salePersonId }, null);
		if (salespersoncursor.moveToFirst()) {
			salesPersonNo = salespersoncursor.getString(0);
		}
		SharedPreferencesUtil.addSalePersonNo(getApplicationContext(), salesPersonNo);
	}

	/**
	 * 
	 * @author aleksandar
	 *
	 */
	public interface UsersQuery {
		String[] PROJECTION = { Users._ID, Users.USERNAME, Users.PASSWORD, Users.LAST_LOGIN, Users.SALES_PERSON_ID, Users.NAME };

		int _ID = 0;
		int USERNAME = 1;
		int PASSWORD = 2;
		int LAST_LOGIN = 3;
		int SALES_PERSON_ID = 4;
		int NAME = 5;
	}

}
