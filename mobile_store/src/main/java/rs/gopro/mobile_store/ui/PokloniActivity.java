package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.GiftItems;
import rs.gopro.mobile_store.provider.MobileStoreContract.GiftItemsColumns;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.components.ItemAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SetGiftsForRealizedVisitsSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PokloniActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

	private int visitId, customerId, selectedItemId;
	private String visitDate, arrivalTime, customerNo, selectedItemNo;
	
	private AutoCompleteTextView acPokloniArtikl;
	private EditText etPokloniKolicina, etPokloniKomentar;
	private Button bPokloniDodaj;
	private ListView lvPokloni;
	private ProgressDialog progressDialog;

	private CursorAdapter cursorAdapter;
	private ItemAutocompleteCursorAdapter itemAdapter;
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			dismissProgressDialog();
			onSOAPResult(syncResult, intent.getAction());
		}
	};

	protected void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (SetGiftsForRealizedVisitsSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				
				DialogUtil.showInfoDialog(this, getString(R.string.dialog_title_sync_info), "Podaci uspešno sinhronizovani");
			}
		} else {
			DialogUtil.showInfoErrorDialog(this, syncResult.getResult());
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pokloni);
		
		visitId = getIntent().getIntExtra("visit_id", -1);
		customerId = getIntent().getIntExtra("customer_id", -1);
		customerNo = getIntent().getStringExtra("customer_no");
		visitDate = getIntent().getStringExtra("visit_date");
		arrivalTime = getIntent().getStringExtra("arrival_time");
		
		if (visitId == -1 || customerId == -1) {
			finish();
		}

		acPokloniArtikl = (AutoCompleteTextView) findViewById(R.id.acPokloniArtikl);
		itemAdapter = new ItemAutocompleteCursorAdapter(this, null);
		acPokloniArtikl.setAdapter(itemAdapter);
		acPokloniArtikl.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Cursor cursor = (Cursor) itemAdapter.getItem(position);
				
				selectedItemId = cursor.getInt(0);
				selectedItemNo = cursor.getString(1);
			}
		});
		
		etPokloniKolicina = (EditText) findViewById(R.id.etPokloniKolicina);
		etPokloniKomentar = (EditText) findViewById(R.id.etPokloniKomentar);
		bPokloniDodaj = (Button) findViewById(R.id.bPokloniDodaj);
		
		bPokloniDodaj.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (selectedItemId == -1) {
					showToast("Niste odabrali artikal!");
					return;
				}
				if (etPokloniKolicina.getText().toString().trim().length() == 0) {
					showToast("Niste uneli količinu!");
					return;
				}
				
				ContentValues cv = new ContentValues();
				cv.put(GiftItemsColumns.ITEM_ID, selectedItemId);
				cv.put(GiftItemsColumns.ITEM_NO, selectedItemNo);
				cv.put(GiftItemsColumns.ITEM_QUANTITY, etPokloniKolicina.getText().toString());
				cv.put(GiftItemsColumns.ITEM_NOTE, etPokloniKomentar.getText().toString());
				cv.put(GiftItemsColumns.SALES_PERSON_NO, salesPersonNo);
				cv.put(GiftItemsColumns.CUSTOMER_ID, customerId);
				cv.put(GiftItemsColumns.CUSTOMER_NO, customerNo);
				cv.put(GiftItemsColumns.POTENTIAL_CUSTOMER, isPotentialCustomer() ? 1 : 0);
				cv.put(GiftItemsColumns.VISIT_ID, visitId);
				cv.put(GiftItemsColumns.VISIT_DATE, visitDate);
				cv.put(GiftItemsColumns.VISIT_ARRIVAL_TIME, arrivalTime);
				getContentResolver().insert(GiftItems.CONTENT_URI, cv);
				
				cv = new ContentValues();
				cv.put(Visits.GIFT_STATUS, 1);
				getContentResolver().update(Visits.CONTENT_URI, cv, Tables.VISITS + "._id=?", new String[] { String.valueOf(visitId) });
				
				acPokloniArtikl.setText("");
				etPokloniKolicina.setText("");
				etPokloniKomentar.setText("");
				acPokloniArtikl.requestFocus();
			}
		});

		lvPokloni = (ListView) findViewById(R.id.lvPokloni);
		cursorAdapter = new PokloniAdapter(this);
		lvPokloni.setAdapter(cursorAdapter);
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0, Menu.FIRST, Menu.NONE, "Pošalji").setIcon(R.drawable.navigation_accept).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case 1:
				showProgressDialog("Molimo sačekajte...");
				SetGiftsForRealizedVisitsSyncObject setGiftsForRealizedVisitsSyncObject = new SetGiftsForRealizedVisitsSyncObject(visitId);
		    	Intent intent = new Intent(this, NavisionSyncService.class);
				intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, setGiftsForRealizedVisitsSyncObject);
				startService(intent);
				break;
			default:
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		outState.putInt("selectedItemId", selectedItemId);
		outState.putString("selectedItemNo", selectedItemNo);
		
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {

		LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		
		IntentFilter setGiftsForRealizedVisitsSyncObject = new IntentFilter(SetGiftsForRealizedVisitsSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, setGiftsForRealizedVisitsSyncObject);
		
		super.onResume();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
		selectedItemId = savedInstanceState.getInt("selectedItemId", -1);
		selectedItemNo = savedInstanceState.getString("selectedItemNo", null);
		
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	private boolean isPotentialCustomer() {
		Cursor cursor = getContentResolver().query(Customers.CONTENT_URI, new String[] {Customers.CONTACT_COMPANY_NO}, "("+Customers.CONTACT_COMPANY_NO + " IS NULL OR " + Customers.CONTACT_COMPANY_NO + "='')" + " AND " + Customers._ID + "=?" , new String[] { String.valueOf(customerId) }, null);
		if (cursor.moveToFirst()) {
			return true;
		}
		return false;
	}
	
	private void ConfirmDialog(final int id, final String name) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
	    adb.setTitle(getString(R.string.potvrda_brisanja) + " [" + name + "]");

	    adb.setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	izbrisiPoklon(id);
	        }
	    });

	    adb.setNegativeButton("Otkaži", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	dialog.dismiss();
	        }
	    });
	    adb.show();
	}
	
	private void izbrisiPoklon(int giftId) {
		getContentResolver().delete(GiftItems.buildGiftItemsUri(giftId), null, null);
		getLoaderManager().restartLoader(0, null, PokloniActivity.this);
		showToast(getString(R.string.stavkaUspesnoObrisana));
	}
	
	public void showProgressDialog(String text) {
		progressDialog = ProgressDialog.show(this, "Molimo sačekajte", text, true);
		progressDialog.setCancelable(true);
	}
	
	public void dismissProgressDialog() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	private void showToast(String text) {
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, GiftItems.buildGiftItemsVisitUri(visitId), GiftItemsQuery.PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (cursorAdapter != null) {
			cursorAdapter.swapCursor(data);
			lvPokloni.setSelection(lvPokloni.getAdapter().getCount() - 1);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (cursorAdapter != null) {
			cursorAdapter.swapCursor(null);
		}
	}

	private class PokloniAdapter extends CursorAdapter {
		public PokloniAdapter(Context context) {
			super(context, null, false);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.pokloni_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			TextView tvPokloniNaslov = (TextView) view.findViewById(R.id.tvPokloniNaslov);
			TextView tvPokloniKomentar = (TextView) view.findViewById(R.id.tvPokloniKomentar);
			TextView tvPokloniKolicina = (TextView) view.findViewById(R.id.tvPokloniKolicina);
			RelativeLayout layoutPokloniDelete = (RelativeLayout) view.findViewById(R.id.layoutPokloniDelete);
			
			final int id = cursor.getInt(GiftItemsQuery._ID);
			final String itemNo = cursor.getString(GiftItemsQuery.ITEM_NO);
			
			tvPokloniNaslov.setText(String.format("%s - %s", itemNo, cursor.getString(GiftItemsQuery.DESCRIPTION)));
			tvPokloniKomentar.setText(String.format("Komentar: %s", cursor.getString(GiftItemsQuery.ITEM_NOTE)));
			tvPokloniKolicina.setText(String.format("Količina: %d", cursor.getInt(GiftItemsQuery.ITEM_QUANTITY)));
			
			layoutPokloniDelete.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ConfirmDialog(id, itemNo);
				}
			});
		}

	}
	
	private interface GiftItemsQuery {

		String[] PROJECTION = {
				Tables.GIFT_ITEMS + "." + BaseColumns._ID,
				Tables.ITEMS + "." + Items.ITEM_NO,
				Tables.ITEMS + "." + Items.DESCRIPTION,
				Tables.GIFT_ITEMS + "." + GiftItems.ITEM_QUANTITY,
				Tables.GIFT_ITEMS + "." + GiftItems.ITEM_NOTE
		};

		int _ID = 0;
		int ITEM_NO = 1;
		int DESCRIPTION = 2;
		int ITEM_QUANTITY = 3;
		int ITEM_NOTE = 4;
	}
}
