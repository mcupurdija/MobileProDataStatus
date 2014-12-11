package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.MobileStoreContract.Methods;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.ui.components.ItemAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SetTeachingMethodSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddMethodActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

	public static final String TAG = "AddMethodActivity";
	
	private int methodId, selectedArtikalId = -1, selectedSkolaCustomerId = -1, selectedProfesorCustomerId = -1, selectedProfesor2CustomerId = -1;

	private AutoCompleteTextView acMetodArtikal, acMethodSkola, acMethodProfesor, acMethodProfesor2;
	private EditText etMethodPredmet, etMethodSkolskaGodina, etMethodKomentar;
	private Spinner sMethodRazred, sMethodVelicina;
	
	private ItemAutocompleteCursorAdapter itemAutocompleteCursorAdapter;
	private CustomerAutocompleteCursorAdapter customerAdapter;
	
	private ArrayAdapter<CharSequence> arrayRazred, arrayVelicina;
	
	protected BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (SetTeachingMethodSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				Toast.makeText(getApplicationContext(), "Metoda je uspešno sinhronizovana", Toast.LENGTH_SHORT).show();
				finish();
			}
		} else {
			DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_error_in_sync), syncResult.getResult());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_method);

		methodId = getIntent().getIntExtra("method_id", -1);

		acMetodArtikal = (AutoCompleteTextView) findViewById(R.id.acMetodArtikal);
		acMethodSkola = (AutoCompleteTextView) findViewById(R.id.acMethodSkola);
		etMethodPredmet = (EditText) findViewById(R.id.etMethodPredmet);
		sMethodRazred = (Spinner) findViewById(R.id.sMethodRazred);
		acMethodProfesor = (AutoCompleteTextView) findViewById(R.id.acMethodProfesor);
		acMethodProfesor2 = (AutoCompleteTextView) findViewById(R.id.acMethodProfesor2);
		sMethodVelicina = (Spinner) findViewById(R.id.sMethodVelicina);
		etMethodSkolskaGodina = (EditText) findViewById(R.id.etMethodSkolskaGodina);
		etMethodKomentar = (EditText) findViewById(R.id.etMethodKomentar);
		
		itemAutocompleteCursorAdapter = new ItemAutocompleteCursorAdapter(this, null);
		acMetodArtikal.setAdapter(itemAutocompleteCursorAdapter);
		acMetodArtikal.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) itemAutocompleteCursorAdapter.getItem(position);
				selectedArtikalId = cursor.getInt(0);
			}
		});
		
		customerAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		acMethodSkola.setAdapter(customerAdapter);
		acMethodSkola.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) customerAdapter.getItem(position);
				selectedSkolaCustomerId = cursor.getInt(0);
			}
		});
		acMethodProfesor.setAdapter(customerAdapter);
		acMethodProfesor.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) customerAdapter.getItem(position);
				selectedProfesorCustomerId = cursor.getInt(0);
			}
		});
		acMethodProfesor2.setAdapter(customerAdapter);
		acMethodProfesor2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) customerAdapter.getItem(position);
				selectedProfesor2CustomerId = cursor.getInt(0);
			}
		});
		
		arrayRazred = ArrayAdapter.createFromResource(this, R.array.skola_razred_array, android.R.layout.simple_spinner_item);
		arrayRazred.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sMethodRazred.setAdapter(arrayRazred);
		sMethodRazred.setSelection(0);
		
		arrayVelicina = ArrayAdapter.createFromResource(this, R.array.skola_velicina_array, android.R.layout.simple_spinner_item);
		arrayVelicina.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sMethodVelicina.setAdapter(arrayVelicina);
		sMethodVelicina.setSelection(0);
		
		if (Intent.ACTION_EDIT.equals(getIntent().getAction())) {
			getLoaderManager().initLoader(0, null, this);
		} else if (Intent.ACTION_INSERT.equals(getIntent().getAction())) {
			Cursor cursor = getContentResolver().query(Methods.buildMethodsUri(methodId), MethodQuery.PROJECTION, null, null, null);
			loadUi(cursor);
			cursor.close();
		} else {
			Cursor cursor = getContentResolver().query(Methods.CONTENT_URI, MethodQuery.PROJECTION, null, null, Tables.METHODS + "." + Methods._ID + " DESC");
			loadUi(cursor);
			cursor.close();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0, Menu.FIRST, Menu.NONE, "Resetuj formu").setIcon(R.drawable.ic_action_reset).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(0, Menu.FIRST + 1, Menu.NONE, "Otkaži").setIcon(R.drawable.navigation_cancel).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(0, Menu.FIRST + 2, Menu.NONE, "Sačuvaj").setIcon(R.drawable.navigation_accept).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case 1:
				resetForm();
				break;
			case 2:
				finish();
				break;
			case 3:
				submitForm();
				break;
			default:
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		outState.putInt("selectedArtikalId", selectedArtikalId);
		outState.putInt("selectedSkolaCustomerId", selectedSkolaCustomerId);
		outState.putInt("selectedProfesorCustomerId", selectedProfesorCustomerId);
		outState.putInt("selectedProfesor2CustomerId", selectedProfesor2CustomerId);
		
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
		selectedArtikalId = savedInstanceState.getInt("selectedArtikalId", -1);
		selectedSkolaCustomerId = savedInstanceState.getInt("selectedSkolaCustomerId", -1);
		selectedProfesorCustomerId = savedInstanceState.getInt("selectedProfesorCustomerId", -1);
		selectedProfesor2CustomerId = savedInstanceState.getInt("selectedProfesor2CustomerId", -1);
		
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (methodId != -1) {
			return new CursorLoader(this, Methods.buildMethodsUri(methodId), MethodQuery.PROJECTION, null, null, null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data != null) {
			loadUi(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	private void loadUi(Cursor data) {
		if (data.moveToFirst()) {
			etMethodPredmet.setText(data.getString(MethodQuery.SUBJECT));
			sMethodRazred.setSelection(data.getInt(MethodQuery.CLASS));
			sMethodVelicina.setSelection(data.getInt(MethodQuery.SCHOOL_SIZE));
			etMethodSkolskaGodina.setText(data.getString(MethodQuery.SCHOOL_YEAR));
			etMethodKomentar.setText(data.getString(MethodQuery.COMMENT));
			
			int metodArtikalId = data.getInt(MethodQuery.ITEM_ID);
			if (metodArtikalId > 0) {
				selectedArtikalId = metodArtikalId;
				acMetodArtikal.setText(String.format("%s - %s", data.getString(MethodQuery.ITEM_NO), data.getString(MethodQuery.ITEM_DESCRIPTION)));
			}
			
			int methodSkolaId = data.getInt(MethodQuery.SCHOOL_CUSTOMER_ID);
			if (methodSkolaId > 0) {
				selectedSkolaCustomerId = methodSkolaId;
				acMethodSkola.setText(String.format("%s - %s", data.getString(MethodQuery.SCHOOL_CUSTOMER_NO), data.getString(MethodQuery.SCHOOL_CUSTOMER_NAME)));
			}
			
			int methodProfesorId = data.getInt(MethodQuery.PROFESSOR1_CUSTOMER_ID);
			if (methodProfesorId > 0) {
				selectedProfesorCustomerId = methodProfesorId;
				acMethodProfesor.setText(String.format("%s - %s", data.getString(MethodQuery.PROFESSOR1_CUSTOMER_NO), data.getString(MethodQuery.PROFESSOR1_CUSTOMER_NAME)));
			}
			
			int methodProfesor2Id = data.getInt(MethodQuery.PROFESSOR2_CUSTOMER_ID);
			if (methodProfesor2Id > 0) {
				selectedProfesor2CustomerId = methodProfesor2Id;
				acMethodProfesor2.setText(String.format("%s - %s", data.getString(MethodQuery.PROFESSOR2_CUSTOMER_NO), data.getString(MethodQuery.PROFESSOR2_CUSTOMER_NAME)));
			}
		}
	}
	
	private void resetForm() {
		acMetodArtikal.setText("");
		acMethodSkola.setText("");
		etMethodPredmet.setText("");
		sMethodRazred.setSelection(0);
		acMethodProfesor.setText("");
		acMethodProfesor2.setText("");
		sMethodVelicina.setSelection(0);
		etMethodSkolskaGodina.setText("");
		etMethodKomentar.setText("");
		
		selectedArtikalId = -1;
		selectedSkolaCustomerId = -1;
		selectedProfesorCustomerId = -1;
		selectedProfesor2CustomerId = -1;
		
		acMetodArtikal.requestFocus();
	}

	private void submitForm() {
		
		if (acMetodArtikal.getText().length() == 0) {
			selectedArtikalId = -1;
		}
		if (acMethodSkola.getText().length() == 0) {
			selectedSkolaCustomerId = -1;
		}
		if (acMethodProfesor.getText().length() == 0) {
			selectedProfesorCustomerId = -1;
		}
		if (acMethodProfesor2.getText().length() == 0) {
			selectedProfesor2CustomerId = -1;
		}
		
		ContentValues cv = new ContentValues();
		
		if (selectedArtikalId == -1) {
			Toast.makeText(this, "Potrebno je da odaberete metodu!", Toast.LENGTH_LONG).show();
			acMetodArtikal.requestFocus();
			return;
		}
		if (selectedSkolaCustomerId == -1) {
			Toast.makeText(this, "Potrebno je da odaberete školu!", Toast.LENGTH_LONG).show();
			acMethodSkola.requestFocus();
			return;
		}
		
		cv.put(Methods.METHOD_ITEM_ID, selectedArtikalId);
		cv.put(Methods.SCHOOL_CUSTOMER_ID, selectedSkolaCustomerId);
		cv.put(Methods.PROFESSOR_CUSTOMER_ID, selectedProfesorCustomerId);
		cv.put(Methods.PROFESSOR_2_CUSTOMER_ID, selectedProfesor2CustomerId);
		
		cv.put(Methods.SUBJECT, etMethodPredmet.getText().toString());
		cv.put(Methods.CLASS, sMethodRazred.getSelectedItemPosition());
		cv.put(Methods.SCHOOL_SIZE, sMethodVelicina.getSelectedItemPosition());
		cv.put(Methods.SCHOOL_YEAR, etMethodSkolskaGodina.getText().toString());
		cv.put(Methods.COMMENT, etMethodKomentar.getText().toString());
		cv.put(Methods.SALESPERSON_CODE, salesPersonNo);
		
		if (Intent.ACTION_EDIT.equals(getIntent().getAction())) {
			getContentResolver().update(Methods.buildMethodsUri(methodId), cv, null, null);
			sinhronizujMetodu(methodId);
		} else {
			Uri uri = getContentResolver().insert(Methods.CONTENT_URI, cv);
			sinhronizujMetodu(Integer.valueOf(uri.getLastPathSegment()));
		}
	}
	
	private void sinhronizujMetodu(int methodId) {
		Intent serviceIntent = new Intent(this, NavisionSyncService.class);
		SetTeachingMethodSyncObject setTeachingMethodSyncObject = new SetTeachingMethodSyncObject(methodId);
		serviceIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, setTeachingMethodSyncObject);
		startService(serviceIntent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter setTeachingMethodSyncObject = new IntentFilter(SetTeachingMethodSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, setTeachingMethodSyncObject);
	}

	private interface MethodQuery {
		String[] PROJECTION = { 
			Tables.METHODS + "." + Methods._ID,
			Tables.METHODS + "." + Methods.SUBJECT,
			Tables.METHODS + "." + Methods.CLASS,
			Tables.METHODS + "." + Methods.SCHOOL_SIZE,
			Tables.METHODS + "." + Methods.SCHOOL_YEAR,
			Tables.METHODS + "." + Methods.COMMENT,
			Tables.ITEMS + "." + Items._ID,
			Tables.ITEMS + "." + Items.ITEM_NO,
			Tables.ITEMS + "." + Items.DESCRIPTION,
			"SCH." + Customers._ID,
			"SCH." + Customers.CUSTOMER_NO,
			"SCH." + Customers.NAME,
			"PR1." + Customers._ID,
			"PR1." + Customers.CUSTOMER_NO,
			"PR1." + Customers.NAME,
			"PR2." + Customers._ID,
			"PR2." + Customers.CUSTOMER_NO,
			"PR2." + Customers.NAME
		};
		
//		int _ID = 0;
		int SUBJECT = 1;
		int CLASS = 2;
		int SCHOOL_SIZE = 3;
		int SCHOOL_YEAR = 4;
		int COMMENT = 5;
		int ITEM_ID = 6;
		int ITEM_NO = 7;
		int ITEM_DESCRIPTION = 8;
		int SCHOOL_CUSTOMER_ID = 9;
		int SCHOOL_CUSTOMER_NO = 10;
		int SCHOOL_CUSTOMER_NAME = 11;
		int PROFESSOR1_CUSTOMER_ID = 12;
		int PROFESSOR1_CUSTOMER_NO = 13;
		int PROFESSOR1_CUSTOMER_NAME = 14;
		int PROFESSOR2_CUSTOMER_ID = 15;
		int PROFESSOR2_CUSTOMER_NO = 16;
		int PROFESSOR2_CUSTOMER_NAME = 17;
	}

}
