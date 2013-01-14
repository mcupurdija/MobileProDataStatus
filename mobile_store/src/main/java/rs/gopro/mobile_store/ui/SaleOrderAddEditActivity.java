package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteAdapter;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.LogUtils;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.Spinner;
import android.widget.TextView;

public class SaleOrderAddEditActivity  extends BaseActivity implements LoaderCallbacks<Cursor> {

	private static final String TAG = "SaleOrderAddEditActivity";
	
	static String[] CUSTOMER_PROJECTION = new String[] { MobileStoreContract.Customers._ID, MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME };
	
	private static String DEFAULT_VIEW_TYPE = MobileStoreContract.SaleOrders.CONTENT_ITEM_TYPE;
	
	private static String DEFAULT_INSERT_EDIT_TYPE = MobileStoreContract.SaleOrders.CONTENT_TYPE;
	
	private int mState;
    private Uri mUri;
    private Cursor mCursor;
    private String mViewType;
    
    private AutoCompleteTextView customerAutoComplete;
    private CursorAdapter customerAutoCompleteAdapter;
    private AutoCompleteTextView transitCustomerAutoComplete;
    private CursorAdapter transitCustomerAutoCompleteAdapter;
    private TextView documentNo;
    private Spinner documentType;
    private Spinner paymentType;
    
	public SaleOrderAddEditActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_sale_order);
		
		// routes data from intent that called this activity to business logic
		routeIntent(getIntent(), savedInstanceState != null);
	}
	
	/**
	 * Routes activity on passed data through intent.
	 * It is ran only in onCreate().
	 * @param intent
	 * @param b
	 */
	private void routeIntent(Intent intent, boolean b) {
		// get action from intent
		String action = intent.getAction();
		if (action == null) {
			LogUtils.LOGE(TAG, "Activity called without action!");
			return;
		}
		// get URI from intent
		Uri uri = intent.getData();
		if (uri == null) {
			LogUtils.LOGE(TAG, "Activity called without URI!");
			return;
		}
		// check uri mime type
		mViewType = getContentResolver().getType(uri);
		// wrong uri, only working with single item
		if (!mViewType.equals(DEFAULT_VIEW_TYPE) && !mViewType.equals(DEFAULT_INSERT_EDIT_TYPE)) {
			LogUtils.LOGE(TAG, "Activity called with wrong URI! URI:"+uri.toString());
			return;
		}
		
		// check action and route from there
		if (Intent.ACTION_EDIT.equals(action)) {
			initComponents(action);
		} else if (Intent.ACTION_INSERT.equals(action)) {
			initComponents(action);
		} else if (Intent.ACTION_VIEW.equals(action))  {
			initComponents(action);
		}
	}

	private void initComponents(String action) {
		customerAutoComplete = (AutoCompleteTextView) findViewById(R.id.edit_sale_order_customer_autocomplete);
		transitCustomerAutoComplete = (AutoCompleteTextView) findViewById(R.id.edit_sale_order_transit_customer_value);
		
		customerAutoCompleteAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		customerAutoComplete.setAdapter(customerAutoCompleteAdapter);
		
		transitCustomerAutoCompleteAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		transitCustomerAutoComplete.setAdapter(transitCustomerAutoCompleteAdapter);
		
		ArrayAdapter<CharSequence> docAdapter = ArrayAdapter.createFromResource(this, R.array.sale_order_block_status_array, android.R.layout.simple_spinner_item);
		docAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		documentType = (Spinner) findViewById(R.id.edit_sale_order_dokument_type_spinner);
		documentType.setAdapter(docAdapter);
		
		documentNo = (TextView) findViewById(R.id.edit_sale_order_dokument_no_text);
		
		ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(this, R.array.sale_order_block_status_array, android.R.layout.simple_spinner_item);
		docAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		documentType = (Spinner) findViewById(R.id.edit_sale_order_dokument_type_spinner);
		documentType.setAdapter(paymentAdapter);
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}

}
