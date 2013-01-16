package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.components.CustomerAddressSpinnerAdapter;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.LogUtils;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class SaleOrderAddEditActivity  extends BaseActivity implements LoaderCallbacks<Cursor>, OnItemClickListener {

	private static final String TAG = "SaleOrderAddEditActivity";
	
	//private static String[] CUSTOMER_PROJECTION = new String[] { MobileStoreContract.Customers._ID, MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME };
	
	private static String[] CUSTOMER_ADDRESS_PROJECTION = new String[] { MobileStoreContract.CustomerAddresses._ID, MobileStoreContract.CustomerAddresses.ADDRESS_NO,  MobileStoreContract.CustomerAddresses.ADDRESS, MobileStoreContract.CustomerAddresses.CITY, MobileStoreContract.CustomerAddresses.POST_CODE, MobileStoreContract.CustomerAddresses.CONTANCT,  MobileStoreContract.CustomerAddresses.PHONE_NO };
	
	private static String[] CUSTOMER_ADDRESS_SPINNER_PROJECTION = new String[] {  MobileStoreContract.CustomerAddresses._ID, MobileStoreContract.CustomerAddresses.ADDRESS, MobileStoreContract.CustomerAddresses.CITY };
	
	private static String DEFAULT_VIEW_TYPE = MobileStoreContract.SaleOrders.CONTENT_ITEM_TYPE;
	
	private static String DEFAULT_INSERT_EDIT_TYPE = MobileStoreContract.SaleOrders.CONTENT_TYPE;
	
	private static final int SALE_ORDER_HEADER_LOADER = 1;
	private static final int SHIPPING_ADDRESS_LOADER = 2;
	private static final int BILLING_ADDRESS_LOADER = 3;
	
	private int mState;
    private Uri mUri;
    private Cursor mCursor;
    private String mViewType;
    private String selectedCustomerNo = null;
    
    private AutoCompleteTextView customerAutoComplete;
    private CustomerAutocompleteCursorAdapter customerAutoCompleteAdapter;
    private AutoCompleteTextView transitCustomerAutoComplete;
    private CustomerAutocompleteCursorAdapter transitCustomerAutoCompleteAdapter;
    private TextView documentNo;
    private Spinner documentType;
    private Spinner paymentType;
    
    private Spinner backorderType;
    private Spinner salesType;
    private Spinner locationType;
    private Spinner billingAddress;
    private Spinner shippingAddress;
    private CustomerAddressSpinnerAdapter billingAddressAdapter;
    private CustomerAddressSpinnerAdapter shippingAddressAdapter;
    
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
			getSupportLoaderManager().initLoader(SALE_ORDER_HEADER_LOADER, null, this);
			initComponents(action);
		} else if (Intent.ACTION_INSERT.equals(action)) {
			initComponents(action);
		} else if (Intent.ACTION_VIEW.equals(action))  {
			getSupportLoaderManager().initLoader(SALE_ORDER_HEADER_LOADER, null, this);
			initComponents(action);
			disableEditOfComponents();
		}
	}

	/**
	 * Disable UI components for edit.
	 */
	private void disableEditOfComponents() {
		documentType.setEnabled(false);
		paymentType.setEnabled(false);

		backorderType.setEnabled(false);
		salesType.setEnabled(false);
		locationType.setEnabled(false);
		customerAutoComplete.setEnabled(false);
		transitCustomerAutoComplete.setEnabled(false);
	}

	private void initComponents(String action) {
		customerAutoComplete = (AutoCompleteTextView) findViewById(R.id.edit_sale_order_customer_autocomplete);
		transitCustomerAutoComplete = (AutoCompleteTextView) findViewById(R.id.edit_sale_order_transit_customer_value);
		
		customerAutoCompleteAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		customerAutoComplete.setAdapter(customerAutoCompleteAdapter);
		
		customerAutoComplete.setOnItemClickListener(this);
		
		transitCustomerAutoCompleteAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		transitCustomerAutoComplete.setAdapter(transitCustomerAutoCompleteAdapter);
		
		ArrayAdapter<CharSequence> docAdapter = ArrayAdapter.createFromResource(this, R.array.sale_order_block_status_array, android.R.layout.simple_spinner_item);
		docAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		documentType = (Spinner) findViewById(R.id.edit_sale_order_dokument_type_spinner);
		documentType.setAdapter(docAdapter);
		
		documentNo = (TextView) findViewById(R.id.edit_sale_order_dokument_no_text);
		
		ArrayAdapter<CharSequence> backorderAdapter = ArrayAdapter.createFromResource(this, R.array.backorder_type_array, android.R.layout.simple_spinner_item);
		backorderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		backorderType = (Spinner) findViewById(R.id.edit_sale_order_backorder_spinner);
		backorderType.setAdapter(backorderAdapter);
		
		ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(this, R.array.location_type_array, android.R.layout.simple_spinner_item);
		locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		locationType = (Spinner) findViewById(R.id.edit_sale_order_location_spinner);
		locationType.setAdapter(locationAdapter);
		
		ArrayAdapter<CharSequence> salesAdapter = ArrayAdapter.createFromResource(this, R.array.slc1_type_array, android.R.layout.simple_spinner_item);
		salesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		salesType = (Spinner) findViewById(R.id.edit_sale_order_slc1_spinner);
		salesType.setAdapter(salesAdapter);
		
		ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(this, R.array.payment_type_array, android.R.layout.simple_spinner_item);
		paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		paymentType = (Spinner) findViewById(R.id.edit_sale_order_payment_type_spinner);
		paymentType.setAdapter(paymentAdapter);
		
		shippingAddress = (Spinner) findViewById(R.id.edit_sale_order_shipping_address_spinner);
		shippingAddressAdapter = new CustomerAddressSpinnerAdapter(this, null, 0);
		shippingAddress.setAdapter(billingAddressAdapter);
		getSupportLoaderManager().initLoader(SHIPPING_ADDRESS_LOADER, null, this);
		
		billingAddress = (Spinner) findViewById(R.id.edit_sale_order_address_invoice_spinner);
		billingAddressAdapter = new CustomerAddressSpinnerAdapter(this, null, 0);
		billingAddress.setAdapter(billingAddressAdapter);
		getSupportLoaderManager().initLoader(BILLING_ADDRESS_LOADER, null, this);
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = null;
		String customerFilter = "dummy";
		switch (id) {
		case SALE_ORDER_HEADER_LOADER:
			
			return null;
		case SHIPPING_ADDRESS_LOADER:
			if (selectedCustomerNo != null) {
				customerFilter = selectedCustomerNo;
			}
			cursorLoader = new CursorLoader(this, MobileStoreContract.CustomerAddresses.buildSearchByCustomerNoUri(customerFilter), CUSTOMER_ADDRESS_PROJECTION, null, null, MobileStoreContract.CustomerAddresses.DEFAULT_SORT);
			return cursorLoader;
		case BILLING_ADDRESS_LOADER:
			if (selectedCustomerNo != null) {
				customerFilter = selectedCustomerNo;	
			}
			cursorLoader = new CursorLoader(this, MobileStoreContract.CustomerAddresses.buildSearchByCustomerNoUri(customerFilter), CUSTOMER_ADDRESS_PROJECTION, null, null, MobileStoreContract.CustomerAddresses.DEFAULT_SORT);
			return cursorLoader;
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case SALE_ORDER_HEADER_LOADER:
			
			break;
		case SHIPPING_ADDRESS_LOADER:
			shippingAddressAdapter.swapCursor(data);
			break;
		case BILLING_ADDRESS_LOADER:
			billingAddressAdapter.swapCursor(data);
			break;
		default:
			break;
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
		case SALE_ORDER_HEADER_LOADER:
			break;
		case SHIPPING_ADDRESS_LOADER:
			shippingAddressAdapter.swapCursor(null);
			break;
		case BILLING_ADDRESS_LOADER:
			billingAddressAdapter.swapCursor(null);
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
		Cursor cursor = (Cursor)parent.getAdapter().getItem(position);
		int cuatomerNoPosition = cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
		this.selectedCustomerNo = cursor.getString(cuatomerNoPosition);
		getSupportLoaderManager().restartLoader(SHIPPING_ADDRESS_LOADER, null, this);
		getSupportLoaderManager().restartLoader(BILLING_ADDRESS_LOADER, null, this);
	}

}
