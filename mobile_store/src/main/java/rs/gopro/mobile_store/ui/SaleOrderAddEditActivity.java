package rs.gopro.mobile_store.ui;

import java.lang.ref.WeakReference;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.components.CustomerAddressSpinnerAdapter;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.LogUtils;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
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
		MobileStoreContract.SaleOrders.SELL_TO_ADDRESS_ID,
		MobileStoreContract.SaleOrders.SHIPP_TO_ADDRESS_ID,
		MobileStoreContract.SaleOrders.CONTACT_ID,
		MobileStoreContract.SaleOrders.CONTACT_NAME,
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
	
	private static String[]  CONTACT_PROJECTION = new String[] { 
		MobileStoreContract.Contacts._ID,
		MobileStoreContract.Contacts.CONTACT_NO,
		MobileStoreContract.Contacts.NAME,
		MobileStoreContract.Contacts.NAME2,
		MobileStoreContract.Contacts.PHONE
	};
	
	private static String[]  CUSTOMER_PROJECTION = new String[] { 
		MobileStoreContract.Customers._ID,
		MobileStoreContract.Customers.CUSTOMER_NO,
		MobileStoreContract.Customers.NAME,
		MobileStoreContract.Customers.PRIMARY_CONTACT_ID,
		MobileStoreContract.Customers.PHONE
	};
	
	private static String DEFAULT_VIEW_TYPE = MobileStoreContract.SaleOrders.CONTENT_ITEM_TYPE;
	
	private static String DEFAULT_INSERT_EDIT_TYPE = MobileStoreContract.SaleOrders.CONTENT_TYPE;
	
	private static final int SALE_ORDER_HEADER_LOADER = 1;
	private static final int SHIPPING_ADDRESS_LOADER = 2;
	private static final int BILLING_ADDRESS_LOADER = 3;
	private static final int CONTACT_HEADER_LOADER = 4;
	private static final int CUSTOMER_HEADER_LOADER = 5;
	
	private static final int SALE_ORDER_INSERT_TOKEN = 0x1;
	
	private String mAction;
    private Uri mUri;
    private String mViewType;
    private String selectedCustomerNo = null;
    
    private AutoCompleteTextView customerAutoComplete;
    private CustomerAutocompleteCursorAdapter customerAutoCompleteAdapter;
    private AutoCompleteTextView transitCustomerAutoComplete;
    private CustomerAutocompleteCursorAdapter transitCustomerAutoCompleteAdapter;
    private TextView documentNo;
    private Spinner documentType;
    ArrayAdapter<CharSequence> docAdapter;
    private Spinner paymentType;
    ArrayAdapter<CharSequence> paymentAdapter;
    private Spinner backorderType;
    ArrayAdapter<CharSequence> backorderAdapter;
    private Spinner salesType;
    ArrayAdapter<CharSequence> salesAdapter;
    private Spinner locationType;
    ArrayAdapter<CharSequence> locationAdapter;
    private Spinner billingAddress;
    private Spinner shippingAddress;
    private CustomerAddressSpinnerAdapter billingAddressAdapter;
    private CustomerAddressSpinnerAdapter shippingAddressAdapter;
    private TextView customerContactNo;
    private EditText contactName;
    private EditText contactPhone;
    
    private EditText shippingAddressField;
    private EditText shippingAddressCity;
    private EditText shippingAddressPostalCode;
    private EditText shippingAddressContact;
    
    private EditText billingAddressField;
    private EditText billingAddressCity;
    private EditText billingAddressPostalCode;
    private EditText billingAddressContact;
    
//    private Uri loadForEditUri;
    
    private StatementHandler statementHandler;
    
	public SaleOrderAddEditActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_sale_order);
		
		statementHandler = new StatementHandler(this);
		
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
//			loadForEditUri = mUri;
			initComponents(mAction);
			getSupportLoaderManager().initLoader(SALE_ORDER_HEADER_LOADER, null, this);		
		} else if (Intent.ACTION_INSERT.equals(mAction)) {
			initComponents(mAction);
			int customerId = intent.getIntExtra(EXTRA_CUSTOMER_ID, -1);
			ContentValues contentValues = null;
			if (customerId != -1) {
				contentValues = new ContentValues();
				contentValues.put(MobileStoreContract.SaleOrders.CUSTOMER_ID, Integer.valueOf(customerId));
			}
			statementHandler.startInsert(SALE_ORDER_INSERT_TOKEN, null, mUri, contentValues);
		} else if (Intent.ACTION_VIEW.equals(mAction))  {
			initComponents(mAction);
			disableEditOfComponents();
			getSupportLoaderManager().initLoader(SALE_ORDER_HEADER_LOADER, null, this);
		}
	}

	private void loadData(Cursor data, String action) {
		int customer_id = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CUSTOMER_ID))) {
			customer_id = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CUSTOMER_ID));
		}
		
		String document_no = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SALES_ORDER_NO));
		
		int document_type = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.DOCUMENT_TYPE))) {
			document_type = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.DOCUMENT_TYPE));
		}
		
		int customer_contact_id = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CONTACT_ID))) {
			customer_contact_id = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CONTACT_ID));
		}
		
		String contact_name = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CONTACT_NAME));
		String contact_phone = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CONTACT_PHONE));
		
		int backorder_status = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.BACKORDER_SHIPMENT_STATUS))) {
			backorder_status = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.BACKORDER_SHIPMENT_STATUS));
		}
		
		String shortcut_dimension1 = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SHORTCUT_DIMENSION_1_CODE));
		String payment_option = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.PAYMENT_OPTION));
		String location_code = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.LOCATION_CODE));
		
		int sell_to_address_id = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SELL_TO_ADDRESS_ID))) {
			sell_to_address_id = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SELL_TO_ADDRESS_ID));
		}
		
		int shipp_to_address_id = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SHIPP_TO_ADDRESS_ID))) {
			shipp_to_address_id = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SHIPP_TO_ADDRESS_ID));
		}
		
		if (customer_id != -1) {
			initCustomerLoad(customer_id);
		}
		
		documentNo.setText(document_no);
		
		if (document_type != -1) {
			documentType.setSelection(document_type);
		}
		
		if (customer_contact_id != -1) {
			initCustomerContactLoad(customer_contact_id);
		}
		
		if (contact_name != null) {
			contactName.setText(contact_name);
		}
		
		if (contact_phone != null) {
			contactPhone.setText(contact_phone);
		}
		
		if (backorder_status != -1) {
			backorderType.setSelection(backorder_status);
		}
		
		if (shortcut_dimension1 != null) {
			int spinnerPosition = salesAdapter.getPosition(shortcut_dimension1);
			if (spinnerPosition != -1) {
				salesType.setSelection(spinnerPosition);
			} else {
				LogUtils.LOGE(TAG, "No position for value:"+shortcut_dimension1);
			}
		}
		
		if (payment_option != null) {
			int spinnerPosition = paymentAdapter.getPosition(payment_option);
			if (spinnerPosition != -1) {
				paymentType.setSelection(spinnerPosition);
			} else {
				LogUtils.LOGE(TAG, "No position for value:"+payment_option);
			}
		}

		if (location_code != null) {
			int spinnerPosition = locationAdapter.getPosition(location_code);
			if (spinnerPosition != -1) {
				locationType.setSelection(spinnerPosition);
			} else {
				LogUtils.LOGE(TAG, "No position for value:"+location_code);
			}
		}
		
		if (sell_to_address_id != -1) {
			int spinnerPosition =  billingAddressAdapter.getIdPostition(sell_to_address_id);
			if (spinnerPosition != -1) {
				billingAddress.setSelection(spinnerPosition);
			} else {
				LogUtils.LOGE(TAG, "No position for value:"+sell_to_address_id);
			}
		}
		
		if (shipp_to_address_id != -1) {
			int spinnerPosition =  shippingAddressAdapter.getIdPostition(shipp_to_address_id);
			if (spinnerPosition != -1) {
				shippingAddress.setSelection(spinnerPosition);
			} else {
				LogUtils.LOGE(TAG, "No position for value:"+shipp_to_address_id);
			}
			shippingAddress.setSelection(shipp_to_address_id);
		}
		
	}

	/**
	 * Disable UI components for edit. Only for view part.
	 */
	private void disableEditOfComponents() {
		documentType.setFocusable(false);

	    contactName.setFocusable(false);
	    contactPhone.setFocusable(false);
		backorderType.setFocusable(false);
		salesType.setFocusable(false);
		locationType.setFocusable(false);
		paymentType.setFocusable(false);
		customerAutoComplete.setFocusable(false);
		transitCustomerAutoComplete.setFocusable(false);
		shippingAddress.setFocusable(false);
		billingAddress.setFocusable(false);
	}

	private void initComponents(String action) {
		customerAutoComplete = (AutoCompleteTextView) findViewById(R.id.edit_sale_order_customer_autocomplete);
		transitCustomerAutoComplete = (AutoCompleteTextView) findViewById(R.id.edit_sale_order_transit_customer_value);
		
		customerAutoCompleteAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		customerAutoComplete.setAdapter(customerAutoCompleteAdapter);
		
		customerAutoComplete.setOnItemClickListener(this);
		
		transitCustomerAutoCompleteAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		transitCustomerAutoComplete.setAdapter(transitCustomerAutoCompleteAdapter);
		
		docAdapter = ArrayAdapter.createFromResource(this, R.array.sale_order_block_status_array, android.R.layout.simple_spinner_item);
		docAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		documentType = (Spinner) findViewById(R.id.edit_sale_order_dokument_type_spinner);
		documentType.setAdapter(docAdapter);
		
		documentNo = (TextView) findViewById(R.id.edit_sale_order_dokument_no_text);
		
		customerContactNo = (TextView) findViewById(R.id.edit_sale_order_contact_no_text);
	    contactName = (EditText) findViewById(R.id.edit_sale_order_contact_name_text);
	    contactPhone = (EditText) findViewById(R.id.edit_sale_order_contact_phone_text);
		
		backorderAdapter = ArrayAdapter.createFromResource(this, R.array.backorder_type_array, android.R.layout.simple_spinner_item);
		backorderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		backorderType = (Spinner) findViewById(R.id.edit_sale_order_backorder_spinner);
		backorderType.setAdapter(backorderAdapter);
		
		locationAdapter = ArrayAdapter.createFromResource(this, R.array.location_type_array, android.R.layout.simple_spinner_item);
		locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		locationType = (Spinner) findViewById(R.id.edit_sale_order_location_spinner);
		locationType.setAdapter(locationAdapter);
		
		salesAdapter = ArrayAdapter.createFromResource(this, R.array.slc1_type_array, android.R.layout.simple_spinner_item);
		salesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		salesType = (Spinner) findViewById(R.id.edit_sale_order_slc1_spinner);
		salesType.setAdapter(salesAdapter);
		
		paymentAdapter = ArrayAdapter.createFromResource(this, R.array.payment_type_array, android.R.layout.simple_spinner_item);
		paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		paymentType = (Spinner) findViewById(R.id.edit_sale_order_payment_type_spinner);
		paymentType.setAdapter(paymentAdapter);
		
		shippingAddress = (Spinner) findViewById(R.id.edit_sale_order_shipping_address_spinner);
		shippingAddressAdapter = new CustomerAddressSpinnerAdapter(this, null, 0);
		shippingAddress.setAdapter(shippingAddressAdapter);
		getSupportLoaderManager().initLoader(SHIPPING_ADDRESS_LOADER, null, this);
		shippingAddress.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Cursor selectedData = (Cursor) shippingAddressAdapter.getItem(arg2);
				loadShippingAddressValues(selectedData);
				//getSupportLoaderManager().restartLoader(SHIPPING_ADDRESS_LOADER, null, SaleOrderAddEditActivity.this);	
			}
		});
		
		billingAddress = (Spinner) findViewById(R.id.edit_sale_order_address_invoice_spinner);
		billingAddressAdapter = new CustomerAddressSpinnerAdapter(this, null, 0);
		billingAddress.setAdapter(billingAddressAdapter);
		getSupportLoaderManager().initLoader(BILLING_ADDRESS_LOADER, null, this);
		billingAddress.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Cursor selectedData = (Cursor) billingAddressAdapter.getItem(arg2);
				loadBillingAddressValues(selectedData);
				//getSupportLoaderManager().restartLoader(BILLING_ADDRESS_LOADER, null, SaleOrderAddEditActivity.this);	
			}
		});
		
		shippingAddressField = (EditText) findViewById(R.id.edit_sale_order_address_value);
		shippingAddressField.setFocusable(false);
	    shippingAddressCity = (EditText) findViewById(R.id.edit_sale_order_city);
	    shippingAddressCity.setFocusable(false);
	    shippingAddressPostalCode = (EditText) findViewById(R.id.edit_sale_order_zip);
	    shippingAddressPostalCode.setFocusable(false);
	    shippingAddressContact = (EditText) findViewById(R.id.edit_sale_order_address_contact_value);
	    shippingAddressContact.setFocusable(false);
	    
	    billingAddressField = (EditText) findViewById(R.id.edit_sale_order_address_invoice_value);
	    billingAddressField.setFocusable(false);
	    billingAddressCity = (EditText) findViewById(R.id.edit_sale_address_invoice_order_city);
	    billingAddressCity.setFocusable(false);
	    billingAddressPostalCode = (EditText) findViewById(R.id.edit_sale_orde_address_invoice_zip);
	    billingAddressPostalCode.setFocusable(false);
	    billingAddressContact = (EditText) findViewById(R.id.edit_sale_order_address_invoice_contact_value);
	    billingAddressContact.setFocusable(false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = null;
		String customerFilter = "dummy";
		switch (id) {
		case SALE_ORDER_HEADER_LOADER:
			cursorLoader = new CursorLoader(this, mUri, SALES_ORDER_PROJECTION, null, null, null);
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
		case CONTACT_HEADER_LOADER:
			int contactId = args.getInt("PRIMARY_CONTACT_ID");
			cursorLoader = new CursorLoader(this, MobileStoreContract.Contacts.buildContactsUri(String.valueOf(contactId)), CONTACT_PROJECTION, null, null, null);
			return cursorLoader;
		case CUSTOMER_HEADER_LOADER:
			int customerId = args.getInt("CUSTOMER_ID");
			cursorLoader = new CursorLoader(this, MobileStoreContract.Customers.buildCustomersUri(String.valueOf(customerId)), CUSTOMER_PROJECTION, null, null, null);
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
			if (data != null && data.moveToFirst()) {
				shippingAddressAdapter.swapCursor(data);
				loadShippingAddressValues(data);
			}
			break;
		case BILLING_ADDRESS_LOADER:
			if (data != null && data.moveToFirst()) {
				billingAddressAdapter.swapCursor(data);
				loadBillingAddressValues(data);
			}
			break;
		case CONTACT_HEADER_LOADER:
			if (data != null && data.moveToFirst()) {
				loadContactValues(data);
			}
			break;
		case CUSTOMER_HEADER_LOADER:
			if (data != null && data.moveToFirst()) {
				loadCustomer(data);
			}
		default:
			data.close();
			break;
		}
	}

	private void loadCustomer(Cursor data) {
		if (data.getCount() < 1) {
			LogUtils.LOGI(TAG, "No customer data!");
			return;
		}
		final int codeIndex = data.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
		final int nameIndex = data.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME);
		final String result = data.getString(codeIndex) + " - " + data.getString(nameIndex);
		customerAutoComplete.setAdapter(null);
		customerAutoComplete.setText(result);
		customerAutoComplete.setAdapter(customerAutoCompleteAdapter);
	}

	private void loadContactValues(Cursor data) {
		if (data.getCount() < 1) {
			LogUtils.LOGI(TAG, "No customer address data!");
			return;
		}
		customerContactNo.setText(data.getString(data.getColumnIndexOrThrow(MobileStoreContract.Contacts.CONTACT_NO)));
		String contactName1 = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.Contacts.NAME));
		String contactName2 = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.Contacts.NAME2));
		contactName.setText((contactName1 != null ? contactName1:"") + (contactName2 != null ? contactName2:""));
		String contactPhoneLocal = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.Contacts.PHONE));
		contactPhone.setText(contactPhoneLocal != null?contactPhoneLocal:"");
	}

	private void loadShippingAddressValues(Cursor data) {
		if (data.getCount() < 1) {
			LogUtils.LOGI(TAG, "No shipping address data!");
			return;
		}
		String shippingAddressFieldLocal = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.CustomerAddresses.ADDRESS));
		String shippingAddressCityLocal = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.CustomerAddresses.CITY));
		String shippingAddressPostalCodeLocal = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.CustomerAddresses.POST_CODE));
		String shippingAddressContactLocal = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.CustomerAddresses.CONTANCT));
		shippingAddressField.setText(shippingAddressFieldLocal!=null?shippingAddressFieldLocal:"");
		shippingAddressCity.setText(shippingAddressCityLocal!=null?shippingAddressCityLocal:"");
		shippingAddressPostalCode.setText(shippingAddressPostalCodeLocal!=null?shippingAddressPostalCodeLocal:"");
		shippingAddressContact.setText(shippingAddressContactLocal!=null?shippingAddressContactLocal:"");
	}

	private void loadBillingAddressValues(Cursor data) {
		if (data.getCount() < 1) {
			LogUtils.LOGI(TAG, "No billing address data!");
			return;
		}
		String shippingAddressFieldLocal = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.CustomerAddresses.ADDRESS));
		String shippingAddressCityLocal = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.CustomerAddresses.CITY));
		String shippingAddressPostalCodeLocal = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.CustomerAddresses.POST_CODE));
		String shippingAddressContactLocal = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.CustomerAddresses.CONTANCT));
		billingAddressField.setText(shippingAddressFieldLocal!=null?shippingAddressFieldLocal:"");
		billingAddressCity.setText(shippingAddressCityLocal!=null?shippingAddressCityLocal:"");
		billingAddressPostalCode.setText(shippingAddressPostalCodeLocal!=null?shippingAddressPostalCodeLocal:"");
		billingAddressContact.setText(shippingAddressContactLocal!=null?shippingAddressContactLocal:"");
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
		case CONTACT_HEADER_LOADER:
			break;
		case CUSTOMER_HEADER_LOADER:
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
		Cursor cursor = (Cursor)parent.getAdapter().getItem(position);
		int customerNoPosition = cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);	
		this.selectedCustomerNo = cursor.getString(customerNoPosition);
		int customerContactId = cursor.getInt(cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.PRIMARY_CONTACT_ID));
		getSupportLoaderManager().restartLoader(SHIPPING_ADDRESS_LOADER, null, this);
		getSupportLoaderManager().restartLoader(BILLING_ADDRESS_LOADER, null, this);
		initCustomerContactLoad(customerContactId);
	}

	private void initCustomerLoad(int customer_id) {
		Bundle customerIdbundle = new Bundle();
		customerIdbundle.putInt("CUSTOMER_ID", customer_id);
		getSupportLoaderManager().initLoader(CUSTOMER_HEADER_LOADER, customerIdbundle, this);
	}

	private void initCustomerContactLoad(int customer_contact_id) {
		Bundle contactBundle = new Bundle();
		contactBundle.putInt("PRIMARY_CONTACT_ID", customer_contact_id);
		getSupportLoaderManager().initLoader(CONTACT_HEADER_LOADER, contactBundle, this);
	}
	
	private static class StatementHandler extends AsyncQueryHandler {

		//private LoaderManager supportLoaderManager;
		private WeakReference<SaleOrderAddEditActivity> mSaleOrderAddEditActivity;
		
		public StatementHandler(Context context) {
			super(context.getContentResolver());
			mSaleOrderAddEditActivity = new WeakReference<SaleOrderAddEditActivity>((SaleOrderAddEditActivity) context);
			//supportLoaderManager = lm;
		}
		
		@Override
		protected void onInsertComplete(int token, Object cookie, Uri uri) {
			switch (token) {
			case SALE_ORDER_INSERT_TOKEN:
				SaleOrderAddEditActivity activity = mSaleOrderAddEditActivity.get();
	            if (activity != null && !activity.isFinishing()) {
	            	activity.mUri = uri;
	            	activity.getSupportLoaderManager().initLoader(SALE_ORDER_HEADER_LOADER, null, activity);
	            }
				break;
			default:
				break;
			}
		}
		
		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
			// TODO Auto-generated method stub
			super.onDeleteComplete(token, cookie, result);
		}
		
		@Override
		protected void onUpdateComplete(int token, Object cookie, int result) {
			// TODO Auto-generated method stub
			super.onUpdateComplete(token, cookie, result);
		}
	}
}
