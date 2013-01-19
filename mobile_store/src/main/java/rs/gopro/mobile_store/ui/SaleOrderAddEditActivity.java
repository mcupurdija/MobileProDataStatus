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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SaleOrderAddEditActivity  extends BaseActivity implements LoaderCallbacks<Cursor>, OnItemClickListener {

	private static final String TAG = "SaleOrderAddEditActivity";
	
	public static final String EXTRA_CUSTOMER_ID = "rs.gopro.mobile_store.extra.CUSTOMER_ID";
	
	//private static String[] CUSTOMER_PROJECTION = new String[] { MobileStoreContract.Customers._ID, MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME };
	
	private static String[] CUSTOMER_ADDRESS_PROJECTION = new String[] { 
		MobileStoreContract.CustomerAddresses._ID, 
		MobileStoreContract.CustomerAddresses.ADDRESS_NO,  
		MobileStoreContract.CustomerAddresses.ADDRESS, 
		MobileStoreContract.CustomerAddresses.CITY, 
		MobileStoreContract.CustomerAddresses.POST_CODE, 
		MobileStoreContract.CustomerAddresses.CONTANCT,  
		MobileStoreContract.CustomerAddresses.PHONE_NO 
	};
	private static String[]  SALES_ORDER_PROJECTION = new String[] { 
		MobileStoreContract.SaleOrders._ID,
		MobileStoreContract.SaleOrders.SALES_PERSON_ID,
		MobileStoreContract.SaleOrders.SALES_ORDER_NO,
		MobileStoreContract.SaleOrders.DOCUMENT_TYPE,
		MobileStoreContract.SaleOrders.CUSTOMER_ID,
		MobileStoreContract.SaleOrders.ORDER_DATE,
		MobileStoreContract.SaleOrders.LOCATION_CODE,
		MobileStoreContract.SaleOrders.SHORTCUT_DIMENSION_1_CODE,
		MobileStoreContract.SaleOrders.CURRENCY_CODE,
		MobileStoreContract.SaleOrders.EXTERNAL_DOCUMENT_NO,
		MobileStoreContract.SaleOrders.QUOTE_NO,
		MobileStoreContract.SaleOrders.BACKORDER_SHIPMENT_STATUS,
		MobileStoreContract.SaleOrders.ORDER_STATUS_FOR_SHIPMENT,
		MobileStoreContract.SaleOrders.FIN_CONTROL_STATUS,
		MobileStoreContract.SaleOrders.ORDER_CONDITION_STATUS,
		MobileStoreContract.SaleOrders.USED_CREDIT_LIMIT_BY_EMPLOYEE,
		MobileStoreContract.SaleOrders.ORDER_VALUE_STATUS,
		MobileStoreContract.SaleOrders.QUOTE_REALIZED_STATUS,
		MobileStoreContract.SaleOrders.SPECIAL_QUOTE,
		MobileStoreContract.SaleOrders.QUOTE_VALID_DATE_TO,
		MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST,
		MobileStoreContract.SaleOrders.CUSTOMER_ADDRESS_ID,
		MobileStoreContract.SaleOrders.CONTACT_PHONE,
		MobileStoreContract.SaleOrders.PAYMENT_OPTION,
		MobileStoreContract.SaleOrders.CHECK_STATUS_PHONE,
		MobileStoreContract.SaleOrders.TOTAL,
		MobileStoreContract.SaleOrders.TOTAL_DISCOUNT,
		MobileStoreContract.SaleOrders.TOTAL_PDV,
		MobileStoreContract.SaleOrders.TOTAL_ITEMS,
		MobileStoreContract.SaleOrders.HIDE_REBATE,
		MobileStoreContract.SaleOrders.FURTHER_SALE,
		MobileStoreContract.SaleOrders.NOTE1,
		MobileStoreContract.SaleOrders.NOTE2,
		MobileStoreContract.SaleOrders.NOTE3
	};
	private static String DEFAULT_VIEW_TYPE = MobileStoreContract.SaleOrders.CONTENT_ITEM_TYPE;
	
	private static String DEFAULT_INSERT_EDIT_TYPE = MobileStoreContract.SaleOrders.CONTENT_TYPE;
	
	private static final int SALE_ORDER_HEADER_LOADER = 1;
	private static final int SHIPPING_ADDRESS_LOADER = 2;
	private static final int BILLING_ADDRESS_LOADER = 3;
	
	private String mAction;
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
    
    private EditText shippingAddressField;
    private EditText shippingAddressCity;
    private EditText shippingAddressPostalCode;
    private EditText shippingAddressContact;
    
    private EditText billingAddressField;
    private EditText billingAddressCity;
    private EditText billingAddressPostalCode;
    private EditText billingAddressContact;
    
    private Uri editUri;
    
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
		mAction = intent.getAction();
		if (mAction == null) {
			LogUtils.LOGE(TAG, "Activity called without action!");
			return;
		}
		// get URI from intent
		mUri = intent.getData();
		if (mUri == null) {
			LogUtils.LOGE(TAG, "Activity called without URI!");
			return;
		}
		// check uri mime type
		mViewType = getContentResolver().getType(mUri);
		// wrong uri, only working with single item
		if (!mViewType.equals(DEFAULT_VIEW_TYPE) && !mViewType.equals(DEFAULT_INSERT_EDIT_TYPE)) {
			LogUtils.LOGE(TAG, "Activity called with wrong URI! URI:"+mUri.toString());
			return;
		}
		
		// check action and route from there
		if (Intent.ACTION_EDIT.equals(mAction)) {
			getSupportLoaderManager().initLoader(SALE_ORDER_HEADER_LOADER, null, this);
			editUri = mUri;
			initComponents(mAction, null);
		} else if (Intent.ACTION_INSERT.equals(mAction)) {
			int customerId = intent.getIntExtra(EXTRA_CUSTOMER_ID, -1);
			if (customerId != -1) {
				initComponents(mAction, Integer.valueOf(customerId));
			} else {
				initComponents(mAction, null);
			}
		} else if (Intent.ACTION_VIEW.equals(mAction))  {
			getSupportLoaderManager().initLoader(SALE_ORDER_HEADER_LOADER, null, this);
			initComponents(mAction, null);
			disableEditOfComponents();
		}
	}

	private void loadData(Cursor data, String action) {
		// TODO Auto-generated method stub
		
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

	private void initComponents(String action, Integer customerId) {
		customerAutoComplete = (AutoCompleteTextView) findViewById(R.id.edit_sale_order_customer_autocomplete);
		transitCustomerAutoComplete = (AutoCompleteTextView) findViewById(R.id.edit_sale_order_transit_customer_value);
		
		customerAutoCompleteAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		//customerAutoCompleteAdapter.getFilter().filter("ovde ide customer");
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
		shippingAddress.setAdapter(shippingAddressAdapter);
		getSupportLoaderManager().initLoader(SHIPPING_ADDRESS_LOADER, null, this);
		
		billingAddress = (Spinner) findViewById(R.id.edit_sale_order_address_invoice_spinner);
		billingAddressAdapter = new CustomerAddressSpinnerAdapter(this, null, 0);
		billingAddress.setAdapter(billingAddressAdapter);
		getSupportLoaderManager().initLoader(BILLING_ADDRESS_LOADER, null, this);
		
		shippingAddressField = (EditText) findViewById(R.id.edit_sale_order_address_value);
	    shippingAddressCity = (EditText) findViewById(R.id.edit_sale_order_city);
	    shippingAddressPostalCode = (EditText) findViewById(R.id.edit_sale_order_zip);
	    shippingAddressContact = (EditText) findViewById(R.id.edit_sale_order_address_contact_value);
	    
	    billingAddressField = (EditText) findViewById(R.id.edit_sale_order_address_invoice_value);
	    billingAddressCity = (EditText) findViewById(R.id.edit_sale_address_invoice_order_city);
	    billingAddressPostalCode = (EditText) findViewById(R.id.edit_sale_orde_address_invoice_zip);
	    billingAddressContact = (EditText) findViewById(R.id.edit_sale_order_address_invoice_contact_value);
		
		// TODO here after setup load predefined data
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = null;
		String customerFilter = "dummy";
		switch (id) {
		case SALE_ORDER_HEADER_LOADER:
			cursorLoader = new CursorLoader(this, editUri, SALES_ORDER_PROJECTION, null, null, null);
			return cursorLoader;
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
			loadData(data, null);
			break;
		case SHIPPING_ADDRESS_LOADER:
			if (data.moveToFirst()) {
				shippingAddressAdapter.swapCursor(data);
				loadShippingAddressValues(data);
			}
			break;
		case BILLING_ADDRESS_LOADER:
			if (data.moveToFirst()) {
				billingAddressAdapter.swapCursor(data);
				loadBillingAddressValues(data);
			}
			break;
		default:
			break;
		}

	}

	private void loadShippingAddressValues(Cursor data) {
		// TODO Auto-generated method stub
		
	}

	private void loadBillingAddressValues(Cursor data) {
		// TODO Auto-generated method stub
		
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
