package rs.gopro.mobile_store.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.components.ContactSpinnerAdapter;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.ui.dialog.AddressSelectDialog;
import rs.gopro.mobile_store.ui.dialog.AddressSelectDialog.AddressSelectDialogListener;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.exceptions.SaleOrderValidationException;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SaleOrderAddEditActivity  extends BaseActivity implements LoaderCallbacks<Cursor>, OnItemClickListener, AddressSelectDialogListener {

	private static final String TAG = "SaleOrderAddEditActivity";
	
	public static final String EXTRA_CUSTOMER_ID = "rs.gopro.mobile_store.extra.CUSTOMER_ID";
	
	public static final String LOADED_CONTENT_VALUES = "LOADED_CONTENT_VALUES";
	
	private static final int BILLING_ADDRESS_SELECTOR = 0;
	private static final int SHIPPING_ADDRESS_SELECTOR = 1;
	
	//private static String[] CUSTOMER_PROJECTION = new String[] { MobileStoreContract.Customers._ID, MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME };
	
	private static String[]  SALES_ORDER_PROJECTION = new String[] { 
		MobileStoreContract.SaleOrders._ID,
		MobileStoreContract.SaleOrders.SALES_PERSON_ID,
		MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO,
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
		MobileStoreContract.SaleOrders.CONTACT_EMAIL,
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
		MobileStoreContract.Contacts.PHONE,
		MobileStoreContract.Contacts.EMAIL
	};
	
	private static String[]  CUSTOMER_PROJECTION = new String[] { 
		MobileStoreContract.Customers._ID,
		MobileStoreContract.Customers.CUSTOMER_NO,
		MobileStoreContract.Customers.NAME,
		MobileStoreContract.Customers.PRIMARY_CONTACT_ID,
		MobileStoreContract.Customers.PHONE,
		MobileStoreContract.Customers.CITY,
		MobileStoreContract.Customers.ADDRESS,
		MobileStoreContract.Customers.POST_CODE,
		MobileStoreContract.Customers.CONTACT_COMPANY_NO,
		MobileStoreContract.Customers.EMAIL
	};
	
	private static String DEFAULT_VIEW_TYPE = MobileStoreContract.SaleOrders.CONTENT_ITEM_TYPE;
	
	private static String DEFAULT_INSERT_EDIT_TYPE = MobileStoreContract.SaleOrders.CONTENT_TYPE;
	
	private static final int SALE_ORDER_HEADER_LOADER = 1;
	private static final int CONTACT_HEADER_LOADER = 4;
	private static final int CUSTOMER_HEADER_LOADER = 5;
	private static final int CUSTOMER_HEADER_LOADER_TRANSIT = 6;
	
	private static final int SALE_ORDER_INSERT_TOKEN = 0x1;
	private static final int SALE_ORDER_UPDATE_TOKEN = 0x2;
    
    private AutoCompleteTextView customerAutoComplete;
    private CustomerAutocompleteCursorAdapter customerAutoCompleteAdapter;
    private AutoCompleteTextView transitCustomerAutoComplete;
    private CustomerAutocompleteCursorAdapter transitCustomerAutoCompleteAdapter;
    private Spinner contactSpinner;
    private ContactSpinnerAdapter contactSpinnerAdapter;
    
    private TextView documentNo;
    private Spinner documentType;
    private ArrayAdapter<CharSequence> docAdapter;
    private Spinner paymentType;
    private ArrayAdapter<CharSequence> paymentAdapter;
    private Spinner backorderType;
    private ArrayAdapter<CharSequence> backorderAdapter;
    private Spinner salesType;
    private ArrayAdapter<CharSequence> salesAdapter;
    private Spinner locationType;
    private ArrayAdapter<CharSequence> locationAdapter;
    private Button shippingAddressSelector;
    private Button billingAddressSelector;
    private EditText contactName;
    private EditText contactPhone;
    private EditText contactEmail;
    
    private EditText shippingAddressField;
    private EditText shippingAddressCity;
    private EditText shippingAddressPostalCode;
    private EditText shippingAddressContact;
    
    private EditText billingAddressField;
    private EditText billingAddressCity;
    private EditText billingAddressPostalCode;
    private EditText billingAddressContact;
    
    private CheckBox hideDiscount;
    private CheckBox showDeclaration;
    
    private EditText orderNo;
    
    private EditText documentNote;
    private EditText headquartersNote;
    
    private ArrayAdapter<CharSequence> orderConditionStatusAdapter;
    private Spinner orderConditionStatus;
    private String[] financialControlStatusOptions;
    private TextView financialControlStatus;
    private String[] orderShipmentStatusOptions;
    private TextView orderShipmentStatus;
    private String[] orderValueStatusOptions;
    private TextView orderValueStatus;

    private String mAction;
    private Uri mUri;
    private String mViewType;
    private String selectedCustomerNo = null;
    
    private int shippingAddressId = -1;
    private int billingAddressId = -1;
    private int contactId = -1;
    
    private String orderDate = null;
    
    private StatementHandler statementHandler;
    
	public SaleOrderAddEditActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_sale_order);

		statementHandler = new StatementHandler(this);
		
		// routes data from intent that called this activity to business logic
		routeIntent(getIntent(), savedInstanceState);
	}
	
	/**
	 * Routes activity on passed data through intent.
	 * It is ran only in onCreate().
	 * @param intent
	 * @param b
	 */
	private void routeIntent(Intent intent, Bundle savedInstanceState) {
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
		
		boolean savedInstanceStateStatus = savedInstanceState != null;
		
		if (savedInstanceState != null) {
			//initialLoadedContentValues = (ContentValues) savedInstanceState.get(LOADED_CONTENT_VALUES);
		} else {
			//shouldLoadInitialValues = true; // saved instance null
		}
		
		// check action and route from there
		if (Intent.ACTION_EDIT.equals(mAction)) {
//			loadForEditUri = mUri;
			initComponents(!savedInstanceStateStatus);
			getSupportLoaderManager().restartLoader(SALE_ORDER_HEADER_LOADER, null, this);		
		} else if (Intent.ACTION_INSERT.equals(mAction)) {
			initComponents(!savedInstanceStateStatus);
			int customerId = intent.getIntExtra(EXTRA_CUSTOMER_ID, -1);
			ContentValues contentValues = new ContentValues();
			if (customerId != -1) {
				contentValues.put(MobileStoreContract.SaleOrders.CUSTOMER_ID, Integer.valueOf(customerId));
			} else {
				contentValues.putNull(MobileStoreContract.SaleOrders.CUSTOMER_ID);
			}
			statementHandler.startInsert(SALE_ORDER_INSERT_TOKEN, null, mUri, contentValues);
//			mUri = getContentResolver().insert(mUri, contentValues);
//			getSupportLoaderManager().initLoader(SALE_ORDER_HEADER_LOADER, null, this);
		} else if (Intent.ACTION_VIEW.equals(mAction))  {
			initComponents(!savedInstanceStateStatus);
			disableEditOfComponents();
			getSupportLoaderManager().restartLoader(SALE_ORDER_HEADER_LOADER, null, this);
		}
	}
	
	private void initComponents(boolean firstRun) {
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

	    contactName = (EditText) findViewById(R.id.edit_sale_order_contact_name_text);
	    contactPhone = (EditText) findViewById(R.id.edit_sale_order_contact_phone_text);
	    orderNo = (EditText) findViewById(R.id.edit_sale_order_quote_no_edit_text);
	    contactEmail = (EditText) findViewById(R.id.edit_sale_order_contact_email_text);
		
		contactSpinner = (Spinner) findViewById(R.id.edit_sale_order_contact_no_text);
		contactSpinnerAdapter = new ContactSpinnerAdapter(this, null, 0);
		contactSpinner.setAdapter(contactSpinnerAdapter);
		getSupportLoaderManager().initLoader(CONTACT_HEADER_LOADER, null, this);
		contactSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Cursor selectedData = (Cursor) contactSpinnerAdapter.getItem(arg2);
				loadContactValues(selectedData);
				//getSupportLoaderManager().restartLoader(SHIPPING_ADDRESS_LOADER, null, SaleOrderAddEditActivity.this);		
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	    
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
		
		paymentAdapter = ArrayAdapter.createFromResource(this, R.array.payment_value_array, android.R.layout.simple_spinner_item);
		paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		paymentType = (Spinner) findViewById(R.id.edit_sale_order_payment_type_spinner);
		paymentType.setAdapter(paymentAdapter);
		
		// before spinner button because we use them to set value to this fields
		shippingAddressField = (EditText) findViewById(R.id.edit_sale_order_address_value);
		shippingAddressField.setFocusable(false);
	    shippingAddressCity = (EditText) findViewById(R.id.edit_sale_order_city);
	    shippingAddressCity.setFocusable(false);
	    shippingAddressPostalCode = (EditText) findViewById(R.id.edit_sale_order_zip);
	    shippingAddressPostalCode.setFocusable(false);
	    shippingAddressContact = (EditText) findViewById(R.id.edit_sale_order_address_contact_value);
	    shippingAddressContact.setFocusable(false);
	    // before spinner button because we use them to set value to this fields
	    billingAddressField = (EditText) findViewById(R.id.edit_sale_order_address_invoice_value);
	    billingAddressField.setFocusable(false);
	    billingAddressCity = (EditText) findViewById(R.id.edit_sale_address_invoice_order_city);
	    billingAddressCity.setFocusable(false);
	    billingAddressPostalCode = (EditText) findViewById(R.id.edit_sale_orde_address_invoice_zip);
	    billingAddressPostalCode.setFocusable(false);
	    billingAddressContact = (EditText) findViewById(R.id.edit_sale_order_address_invoice_contact_value);
	    billingAddressContact.setFocusable(false);
		
		shippingAddressSelector = (Button) findViewById(R.id.edit_sale_order_shipping_address_spinner);
		shippingAddressSelector.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogSetShippingAddress();
			}
		});
		
		billingAddressSelector = (Button) findViewById(R.id.edit_sale_order_address_invoice_spinner);
		billingAddressSelector.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogSetBillingAddress();
			}
		});
	    
	    hideDiscount = (CheckBox) findViewById(R.id.edit_sale_order_hide_discount_check_box);
	    showDeclaration = (CheckBox) findViewById(R.id.edit_sale_order_show_declaration_check_box);
	    
	    documentNote = (EditText) findViewById(R.id.edit_sale_order_document_note_value);
	    headquartersNote = (EditText) findViewById(R.id.edit_sale_order_headquarters_note_value);
	    
	    orderConditionStatusAdapter = ArrayAdapter.createFromResource(this, R.array.order_condition_status_array, android.R.layout.simple_spinner_item);
	    orderConditionStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    orderConditionStatus = (Spinner) findViewById(R.id.edit_sale_order_order_condition_status_spinner);
	    orderConditionStatus.setAdapter(orderConditionStatusAdapter);
		
		financialControlStatusOptions = getResources().getStringArray(R.array.financial_control_status_array);
		orderShipmentStatusOptions = getResources().getStringArray(R.array.order_status_for_shipment_array);
		orderValueStatusOptions = getResources().getStringArray(R.array.order_value_status_array);
		
		financialControlStatus = (TextView) findViewById(R.id.edit_sale_order_financial_control_status_text);
		orderShipmentStatus = (TextView) findViewById(R.id.edit_sale_order_order_status_for_shipment_text);
		orderValueStatus = (TextView) findViewById(R.id.edit_sale_order_order_value_status_text);
	}

	private void loadData(Cursor data, String action) {
		int customer_id = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CUSTOMER_ID))) {
			customer_id = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CUSTOMER_ID));
		}
		
		int transit_customer_id = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST))) {
			transit_customer_id = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST));
		}
		
		String document_no = null;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO))) {	
			document_no = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO));
		} else {
			document_no = "LIF/"+salesPersonNo+"/"+DateUtils.toTempCodeFormat(new Date())+"-"+ data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders._ID));
		}
		
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.ORDER_DATE))) {	
			orderDate = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.ORDER_DATE));
		}
		
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
		String contact_email = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CONTACT_EMAIL));
		String order_no = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.QUOTE_NO));
		
		int backorder_status = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.BACKORDER_SHIPMENT_STATUS))) {
			backorder_status = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.BACKORDER_SHIPMENT_STATUS));
		}
		
		String shortcut_dimension1 = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SHORTCUT_DIMENSION_1_CODE));
		
		String location_code = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.LOCATION_CODE));
		
		String payment_option = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.PAYMENT_OPTION));
		
		int sell_to_address_id = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SELL_TO_ADDRESS_ID))) {
			sell_to_address_id = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SELL_TO_ADDRESS_ID));
		}
		
		int contact_id = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CONTACT_ID))) {
			contact_id = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CONTACT_ID));
		}
		
		int shipp_to_address_id = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SHIPP_TO_ADDRESS_ID))) {
			shipp_to_address_id = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SHIPP_TO_ADDRESS_ID));
		}
		
		int hide_discount = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.HIDE_REBATE))) {
			hide_discount = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.HIDE_REBATE));
		}
		hideDiscount.setChecked(hide_discount == 1 ? true : false);
		
		int show_declaration = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.FURTHER_SALE))) {
			show_declaration = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.FURTHER_SALE));
		}
		showDeclaration.setChecked(show_declaration == 1 ? true : false);
		
		String document_note = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.NOTE1));
		String headquarters_note = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.NOTE2));
		
		documentNote.setText(document_note);
		headquartersNote.setText(headquarters_note);
		
		int order_condition_status = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.ORDER_CONDITION_STATUS))) {
			order_condition_status = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.ORDER_CONDITION_STATUS));
			orderConditionStatus.setSelection(order_condition_status);
		} else {
			orderConditionStatus.setSelection(0);
		}
		
		int financial_control_status = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.FIN_CONTROL_STATUS))) {
			financial_control_status = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.FIN_CONTROL_STATUS));
			financialControlStatus.setText(financialControlStatusOptions[financial_control_status]);
		} else {
			financialControlStatus.setText("-");
		}
		
		int order_status_for_shipment = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.ORDER_STATUS_FOR_SHIPMENT))) {
			order_status_for_shipment = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.ORDER_STATUS_FOR_SHIPMENT));
			orderShipmentStatus.setText(orderShipmentStatusOptions[order_status_for_shipment]);
		} else {
			orderShipmentStatus.setText("-");
		}
		
		int order_value_status = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.ORDER_VALUE_STATUS))) {
			order_value_status = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.ORDER_VALUE_STATUS));
			orderValueStatus.setText(orderValueStatusOptions[order_value_status]);
		} else {
			orderValueStatus.setText("-");
		}
		
		// globals because in init customer it will load combo box
		
		contactId = contact_id;
				
		if (customer_id != -1) {
			initCustomerLoad(customer_id, "MAIN");
		}
		
		if (transit_customer_id != -1) {
			initCustomerLoad(transit_customer_id, "TRANSIT");
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
		
		if (contact_email != null) {
			contactEmail.setText(contact_email);
		}
		
		if (order_no != null) {
			orderNo.setText(order_no);
		}
		
		if (backorder_status != -1) {
			backorderType.setSelection(backorder_status);
		}
		
		if (shortcut_dimension1 != null) {
			ArrayList<String> slc1ids = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.slc1_array)));
			int spinnerPosition = slc1ids.indexOf(shortcut_dimension1);
			if (spinnerPosition != -1) {
				salesType.setSelection(spinnerPosition);
			} else {
				LogUtils.LOGE(TAG, "No position for value:"+shortcut_dimension1);
			}
		}

		if (payment_option != null) {
			ArrayList<String> paymentArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.payment_type_array)));
			int spinnerPosition = paymentArray.indexOf(payment_option);
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
		
		if (contact_id != -1) {
			int spinnerPosition =  contactSpinnerAdapter.getIdPostition(contact_id);
			if (spinnerPosition != -1) {
				contactSpinner.setSelection(spinnerPosition);
			}
		}
		
		billingAddressId = sell_to_address_id;
		loadAndSetCustomerAddressData(BILLING_ADDRESS_SELECTOR, billingAddressId);
		
		shippingAddressId = shipp_to_address_id;
		loadAndSetCustomerAddressData(SHIPPING_ADDRESS_SELECTOR, shippingAddressId);
		
	}
	
	/**
	 * Disable UI components for edit. Only for view part.
	 */
	private void disableEditOfComponents() {
		documentType.setFocusable(false);
		orderNo.setFocusable(false);
	    contactName.setFocusable(false);
	    contactPhone.setFocusable(false);
	    contactEmail.setFocusable(false);
		backorderType.setFocusable(false);
		salesType.setFocusable(false);
		locationType.setFocusable(false);
		paymentType.setFocusable(false);
		customerAutoComplete.setFocusable(false);
		transitCustomerAutoComplete.setFocusable(false);
		shippingAddressSelector.setFocusable(false);
		billingAddressSelector.setFocusable(false);
		contactSpinner.setFocusable(false);
		hideDiscount.setFocusable(false);
		showDeclaration.setFocusable(false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = null;
//		String customerFilter = "dummy";
		switch (id) {
		case SALE_ORDER_HEADER_LOADER:
			cursorLoader = new CursorLoader(this, mUri, SALES_ORDER_PROJECTION, null, null, null);
			return cursorLoader;
		case CONTACT_HEADER_LOADER:
			//int contactId = args.getInt("PRIMARY_CONTACT_ID");
			String potentialCustomerNo = args == null ? null : args.getString("POTENTIAL_CUSTOMER_NO");
			cursorLoader = new CursorLoader(this, MobileStoreContract.Contacts.CONTENT_URI, CONTACT_PROJECTION, Tables.CONTACTS+"."+Contacts.COMPANY_NO+"=?", new String[] { potentialCustomerNo == null ? "" : potentialCustomerNo }, null);
			return cursorLoader;
		case CUSTOMER_HEADER_LOADER:
			int customerId = args.getInt("CUSTOMER_ID");
			cursorLoader = new CursorLoader(this, MobileStoreContract.Customers.buildCustomersUri(String.valueOf(customerId)), CUSTOMER_PROJECTION, null, null, null);
			return cursorLoader;
		case CUSTOMER_HEADER_LOADER_TRANSIT:
			int customerTransitId = args.getInt("CUSTOMER_ID");
			cursorLoader = new CursorLoader(this, MobileStoreContract.Customers.buildCustomersUri(String.valueOf(customerTransitId)), CUSTOMER_PROJECTION, null, null, null);
			return cursorLoader;
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case SALE_ORDER_HEADER_LOADER:
			if (data != null && data.moveToFirst()) {
				loadData(data, null);
			} else {
				LogUtils.LOGE(TAG, "Cursor empty for URI:"+mUri.toString());
			}
			break;
		case CONTACT_HEADER_LOADER:
			if (data != null && data.moveToFirst()) {
				contactSpinnerAdapter.swapCursor(data);
				
				if (contactId != -1) {
					int spinnerPosition =  contactSpinnerAdapter.getIdPostition(contactId);
					if (spinnerPosition != -1) {
						contactSpinner.setSelection(spinnerPosition);
						data.moveToPosition(spinnerPosition);
					}
					contactId = -1;
				}
				
				loadContactValues(data);
			} else {
				contactSpinnerAdapter.swapCursor(null);
				LogUtils.LOGE(TAG, "Cursor empty for CONTACT_HEADER_LOADER!");
			}
			break;
		case CUSTOMER_HEADER_LOADER:
			if (data != null && data.moveToFirst()) {
				loadCustomer(data);
			} else {
				LogUtils.LOGE(TAG, "Cursor empty for CUSTOMER_HEADER_LOADER!");
			}
			break;
		case CUSTOMER_HEADER_LOADER_TRANSIT:
			if (data != null && data.moveToFirst()) {
				loadCustomerTransit(data);
			} else {
				LogUtils.LOGE(TAG, "Cursor empty for CUSTOMER_HEADER_LOADER!");
			}
			break;
		default:
			data.close();
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
		case SALE_ORDER_HEADER_LOADER:
			break;
		case CONTACT_HEADER_LOADER:
			contactSpinnerAdapter.swapCursor(null);
			break;
		case CUSTOMER_HEADER_LOADER:
			break;
		default:
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
		final int emailIndex = data.getColumnIndexOrThrow(MobileStoreContract.Customers.EMAIL);
		final String result = data.getString(codeIndex) + " - " + data.getString(nameIndex);
		selectedCustomerNo = data.getString(codeIndex);
		CustomerAutocompleteCursorAdapter dummyAdapter = null;
		customerAutoComplete.setAdapter(dummyAdapter);
		customerAutoComplete.setText(result);
		// when this loads adapter wont run query, it is ran only on filter, so we must set id manually for latter use
		customerAutoCompleteAdapter.setIdForTitle(result, data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.Customers._ID)));
		customerAutoComplete.setAdapter(customerAutoCompleteAdapter);
		
		if (contactEmail != null) {
			contactEmail.setText("");
			if (!data.isNull(emailIndex)) {
				contactEmail.setText(data.getString(emailIndex));
			}
		}
		
		loadAndSetCustomerAddressData(BILLING_ADDRESS_SELECTOR, -1);
		loadAndSetCustomerAddressData(BILLING_ADDRESS_SELECTOR, -1);
	}

	private void loadCustomerTransit(Cursor data) {
		if (data.getCount() < 1) {
			LogUtils.LOGI(TAG, "No customer data!");
			return;
		}
		final int codeIndex = data.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
		final int nameIndex = data.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME);
		final String result = data.getString(codeIndex) + " - " + data.getString(nameIndex);
		//selectedCustomerNo = data.getString(codeIndex);
		CustomerAutocompleteCursorAdapter dummyAdapter = null;
		transitCustomerAutoComplete.setAdapter(dummyAdapter);
		transitCustomerAutoComplete.setText(result);
		// when this loads adapter wont run query, it is ran only on filter, so we must set id manually for latter use
		transitCustomerAutoCompleteAdapter.setIdForTitle(result, data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.Customers._ID)));
		transitCustomerAutoComplete.setAdapter(transitCustomerAutoCompleteAdapter);
	}
	
	private void loadContactValues(Cursor data) {
		if (data.getCount() < 1) {
			LogUtils.LOGI(TAG, "No contact data!");
			return;
		}
//		customerContactId = Integer.valueOf(data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.Contacts._ID)));
		//customerContactNo.setText(data.getString(data.getColumnIndexOrThrow(MobileStoreContract.Contacts.CONTACT_NO)));
		String contactName1 = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.Contacts.NAME));
		String contactName2 = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.Contacts.NAME2));
		String contactNameWhole = (contactName1 != null ? contactName1:"") + (contactName2 != null ? contactName2:"");
		// this field can be edited manually, not override it only on click 
		if (contactName.getText().toString().trim().equals("")) {
			contactName.setText(contactNameWhole);
		}
		String contactPhoneLocal = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.Contacts.PHONE));
		if (contactPhone.getText().toString().trim().equals("")) {
			contactPhone.setText(contactPhoneLocal != null?contactPhoneLocal:"");
		}
		String contactEmailLocal = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.Contacts.EMAIL));
		if (contactEmail.getText().toString().trim().equals("")) {
			contactEmail.setText(contactEmailLocal != null?contactEmailLocal:"");
		}
	}

	/**
	 * {@inheritDoc}
	 * This is done with restart loaders, because onclick when happens customer is changed.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
		Cursor cursor = (Cursor)parent.getAdapter().getItem(position);
		int customerNoPosition = cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
		int potentialCustomerNoPosition = cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.CONTACT_COMPANY_NO);
		String potentialCustomerNo = cursor.getString(potentialCustomerNoPosition);
		this.selectedCustomerNo = cursor.getString(customerNoPosition);
		int customerContactId = -1;
		if (!cursor.isNull(cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.PRIMARY_CONTACT_ID))) {
			customerContactId = cursor.getInt(cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.PRIMARY_CONTACT_ID));
		}
		
		Bundle customerIdbundle = new Bundle();
		customerIdbundle.putInt("CUSTOMER_ID", cursor.getInt(cursor.getColumnIndexOrThrow(MobileStoreContract.Customers._ID)));
		
		getSupportLoaderManager().restartLoader(CUSTOMER_HEADER_LOADER, customerIdbundle, this);
		
		//getSupportLoaderManager().restartLoader(BILLING_ADDRESS_LOADER, null, this);
		//getSupportLoaderManager().restartLoader(SHIPPING_ADDRESS_LOADER, null, this);
//		if (customerContactId != -1) {
		Bundle contactBundle = new Bundle();
		contactBundle.putInt("PRIMARY_CONTACT_ID", customerContactId);
		contactBundle.putString("POTENTIAL_CUSTOMER_NO", potentialCustomerNo);
		getSupportLoaderManager().restartLoader(CONTACT_HEADER_LOADER, contactBundle, this);
//		}
	}

	/**
	 * Restarts loader an gets data for customer. It is used to disable autocomplete when we have already customer id from db.
	 * Also on rotation it executes but it should not.
	 * If we change from reset to init, there is problem because init must be called from onCreate.
	 * So we are stucked with requery allways. Maybe should put from first requery in bundle and save for rotation.
	 * @param customer_contact_id
	 */
	private void initCustomerLoad(int customer_id, String identification) {
		Bundle customerIdbundle = new Bundle();
		customerIdbundle.putInt("CUSTOMER_ID", customer_id);
		if (identification.equals("MAIN")) {
			getSupportLoaderManager().restartLoader(CUSTOMER_HEADER_LOADER, customerIdbundle, this);
		} else if (identification.equals("TRANSIT")) {
			getSupportLoaderManager().restartLoader(CUSTOMER_HEADER_LOADER_TRANSIT, customerIdbundle, this);
		}
	}

	/**
	 * Restarts loader an gets data for primary contact. Also on rotation it executes but it should not.
	 * If we change from reset to init, there is problem because init must be called from onCreate.
	 * So we are stucked with requery allways. Maybe should put from first requery in bundle and save for rotation.
	 * @param customer_contact_id
	 */
	private void initCustomerContactLoad(int customer_contact_id) {
		Bundle contactBundle = new Bundle();
		contactBundle.putInt("PRIMARY_CONTACT_ID", customer_contact_id);
		getSupportLoaderManager().restartLoader(CONTACT_HEADER_LOADER, contactBundle, this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// if NOT view or FINISH save changes if there is any
		if (!Intent.ACTION_VIEW.equals(mAction) && !isFinishing()) {
			try {
				//statementHandler.startUpdate(SALE_ORDER_UPDATE_TOKEN, null, mUri, getInputData(), null, null);
				getContentResolver().update(mUri, getInputData(), null, null);
			} catch (SaleOrderValidationException e) {
				//statementHandler.cancelOperation(SALE_ORDER_UPDATE_TOKEN);
				LogUtils.LOGE(TAG, "Validation error.", e);
			}
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
//		outState.putParcelable(LOADED_CONTENT_VALUES, initialLoadedContentValues);
	}
	
	private ContentValues getInputData() throws SaleOrderValidationException {
		ContentValues localValues = new ContentValues();
		
		String customer_auto_complete = customerAutoComplete.getText().toString().trim();
		if (customerAutoCompleteAdapter.getIdForTitle(customer_auto_complete) != -1) {
			//Cursor customerItemCursor = (Cursor) customerAutoCompleteAdapter.getItem(customerAutoCompleteAdapter.getIdForTitle(customer_auto_complete));
			int customer_id = customerAutoCompleteAdapter.getIdForTitle(customer_auto_complete);//customerItemCursor.getInt(customerItemCursor.getColumnIndexOrThrow(MobileStoreContract.Customers._ID));
			localValues.put(MobileStoreContract.SaleOrders.CUSTOMER_ID, Integer.valueOf(customer_id));
		} else {
			throw new SaleOrderValidationException("Kupac nije izabran!");
			//localValues.putNull(MobileStoreContract.SaleOrders.CUSTOMER_ID);
		}
		
		String transfer_customer_auto_complete = transitCustomerAutoComplete.getText().toString().trim();
		if (transitCustomerAutoCompleteAdapter.getIdForTitle(transfer_customer_auto_complete) != -1) {
			//Cursor customerItemCursor = (Cursor) transitCustomerAutoCompleteAdapter.getItem(transitCustomerAutoCompleteAdapter.getIdForTitle(transfer_customer_auto_complete));
			int transit_customer_id = transitCustomerAutoCompleteAdapter.getIdForTitle(transfer_customer_auto_complete);//customerItemCursor.getInt(customerItemCursor.getColumnIndexOrThrow(MobileStoreContract.Customers._ID));
			localValues.put(MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST, Integer.valueOf(transit_customer_id));
		}  else {
			localValues.putNull(MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST);
		}
		
		String document_no = documentNo.getText().toString();
		localValues.put(MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO, document_no);
		
		if (orderDate == null) {
			orderDate = DateUtils.formatDbDate(new Date());
		}
		localValues.put(MobileStoreContract.SaleOrders.ORDER_DATE, orderDate);
		
		localValues.put(MobileStoreContract.SaleOrders.SALES_PERSON_ID, Integer.valueOf(salesPersonId));
		
		int document_type = documentType.getSelectedItemPosition();
		localValues.put(MobileStoreContract.SaleOrders.DOCUMENT_TYPE, Integer.valueOf(document_type));
		
		int backorder_type = backorderType.getSelectedItemPosition();
		if (backorder_type == 0) {
			throw new SaleOrderValidationException("Način obrade nije izabran!");
		}
		localValues.put(MobileStoreContract.SaleOrders.BACKORDER_SHIPMENT_STATUS, Integer.valueOf(backorder_type));
		
		String location = locationType.getSelectedItem().toString();
		localValues.put(MobileStoreContract.SaleOrders.LOCATION_CODE, location);
		
		int sales_shc1_type = salesType.getSelectedItemPosition();
		String[] shc1 = getResources().getStringArray(R.array.slc1_array);
		localValues.put(MobileStoreContract.SaleOrders.SHORTCUT_DIMENSION_1_CODE, shc1[sales_shc1_type]);
		
		int payment_type = paymentType.getSelectedItemPosition();
		String[] pay = getResources().getStringArray(R.array.payment_type_array);
		localValues.put(MobileStoreContract.SaleOrders.PAYMENT_OPTION, pay[payment_type]);
		
		
		if (billingAddressId != -1) {
			localValues.put(MobileStoreContract.SaleOrders.SELL_TO_ADDRESS_ID, Integer.valueOf(billingAddressId));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.SELL_TO_ADDRESS_ID);
		}
		
		
		if (shippingAddressId != -1) {
			localValues.put(MobileStoreContract.SaleOrders.SHIPP_TO_ADDRESS_ID, Integer.valueOf(shippingAddressId));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.SHIPP_TO_ADDRESS_ID);
		}
		
		long contact_id = contactSpinner.getSelectedItemId();
		if (contact_id != AdapterView.INVALID_ROW_ID) {
			localValues.put(MobileStoreContract.SaleOrders.CONTACT_ID, Long.valueOf(contact_id));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.CONTACT_ID);
		}
		
//		if (customerContactId != null) {
//			localValues.put(MobileStoreContract.SaleOrders.CONTACT_ID, customerContactId);
//		} else {
//			localValues.putNull(MobileStoreContract.SaleOrders.CONTACT_ID);
//		}
		
		String contact_email = contactEmail.getText().toString().trim();
		if (contact_email != null && !contact_email.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.CONTACT_EMAIL, contact_email);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.CONTACT_EMAIL);
		}
		
		String contact_phone = contactPhone.getText().toString().trim();
		if (contact_phone != null && !contact_phone.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.CONTACT_PHONE, contact_phone);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.CONTACT_PHONE);
		}
		
		String contact_name = contactName.getText().toString().trim();
		if (contact_name != null && !contact_name.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.CONTACT_NAME, contact_name);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.CONTACT_NAME);
		}
		
		localValues.put(MobileStoreContract.SaleOrders.HIDE_REBATE, hideDiscount.isChecked() ? 1 : 0);
		
		localValues.put(MobileStoreContract.SaleOrders.FURTHER_SALE, showDeclaration.isChecked() ? 1 : 0);
		
		String order_no = orderNo.getText().toString().trim();
		if (order_no != null && !order_no.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.QUOTE_NO, order_no);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.QUOTE_NO);
		}
		
		String doc_note = documentNote.getText().toString().trim();
		if (doc_note != null && !doc_note.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.NOTE1, doc_note);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.NOTE1);
		}
		
		String headq_note = headquartersNote.getText().toString().trim();
		if (headq_note != null && !headq_note.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.NOTE2, headq_note);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.NOTE2);
		}
		
		int order_condition_status = orderConditionStatus.getSelectedItemPosition();
		localValues.put(MobileStoreContract.SaleOrders.ORDER_CONDITION_STATUS, Integer.valueOf(order_condition_status));
		
		return localValues;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater menuInflater = getMenuInflater();
	    menuInflater.inflate(R.menu.sale_order_add_edit_main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cancel_sale_order_main_menu_option:
			if (mAction.equals(Intent.ACTION_INSERT)) {
				getContentResolver().delete(mUri, null, null);
			}
//			} else {
//				getContentResolver().update(mUri, initialLoadedContentValues, null, null);
//			}
			finish();
			return true;
		case R.id.save_sale_order_main_menu_option:
			try {
				getContentResolver().update(mUri, getInputData(), null, null);
				
        		CharSequence text = this.getResources().getString(R.string.toast_update_success);
        		int duration = Toast.LENGTH_SHORT;

        		Toast toast = Toast.makeText(this, text, duration);
        		toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 10);
        		toast.show();
        		
        		String document_id = MobileStoreContract.SaleOrders.getSaleOrderId(mUri);
        		Intent returnIntent = new Intent();
        		returnIntent.putExtra("saleOrderId", document_id);
        		setResult(RESULT_OK, returnIntent);    
        		
        		finish();
			} catch (SaleOrderValidationException e) {
				//finish();
				//statementHandler.cancelOperation(SALE_ORDER_UPDATE_TOKEN);
				LogUtils.LOGE(TAG, "Validation error.", e);
				
				AlertDialog alertDialog = new AlertDialog.Builder(
						SaleOrderAddEditActivity.this).create();

			    // Setting Dialog Title
			    alertDialog.setTitle(getResources().getString(R.string.dialog_title_error_in_sync));
			
			    // Setting Dialog Message
			    alertDialog.setMessage(e.getMessage());
			
			    // Setting Icon to Dialog
			    alertDialog.setIcon(R.drawable.ic_launcher);
			
			    // Setting OK Button
			    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	// Write your code here to execute after dialog closed
		            }
			    });
			
			    // Showing Alert Message
			    alertDialog.show();
			}
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Used for async initial insert an update on the end. It is not imperative that insert or update goes async,
	 * because user must wait for response of this operations. 
	 * But it is done as proof of concept. 
	 * @author vladimirm
	 *
	 */
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
	            	activity.getSupportLoaderManager().restartLoader(SALE_ORDER_HEADER_LOADER, null, activity);
	            }
				break;
			default:
				break;
			}
		}
		
		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
			super.onDeleteComplete(token, cookie, result);
		}
		
		@Override
		protected void onUpdateComplete(int token, Object cookie, int result) {
			switch (token) {
			case SALE_ORDER_UPDATE_TOKEN:
				SaleOrderAddEditActivity activity = mSaleOrderAddEditActivity.get();
	            if (activity != null && !activity.isFinishing()) {
	            	if (result > 0) {
	            		Context context = activity;
	            		CharSequence text = activity.getResources().getString(R.string.toast_update_success);
	            		int duration = Toast.LENGTH_SHORT;

	            		Toast toast = Toast.makeText(context, text, duration);
	            		toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	            		toast.show();
					}
	            }
				break;
			default:
				break;
			}
		}
	}

	private void dialogSetShippingAddress() {
		if (selectedCustomerNo == null) {
			DialogUtil.showInfoDialog(this, "Upozorenje", "Kupac nije izabran!");
			return;
		}
		AddressSelectDialog addressSelectDialog = new AddressSelectDialog();
		Intent tempIntent = new Intent(Intent.ACTION_VIEW,
				null);
		tempIntent.putExtra(AddressSelectDialog.EXTRA_CUSTOMER_NO, selectedCustomerNo);
		tempIntent.putExtra(AddressSelectDialog.EXTRA_DIALOG_ID, SHIPPING_ADDRESS_SELECTOR);
		addressSelectDialog.setArguments(BaseActivity
				.intentToFragmentArguments(tempIntent));
		addressSelectDialog.show(getSupportFragmentManager(), "ADDRESS_DIALOG_SHIPPING");
	}
	
	private void dialogSetBillingAddress() {
		if (selectedCustomerNo == null) {
			DialogUtil.showInfoDialog(this, "Upozorenje", "Kupac nije izabran!");
			return;
		}
		AddressSelectDialog addressSelectDialog = new AddressSelectDialog();
		Intent tempIntent = new Intent(Intent.ACTION_VIEW,
				null);
		tempIntent.putExtra(AddressSelectDialog.EXTRA_CUSTOMER_NO, selectedCustomerNo);
		tempIntent.putExtra(AddressSelectDialog.EXTRA_DIALOG_ID, BILLING_ADDRESS_SELECTOR);
		addressSelectDialog.setArguments(BaseActivity
				.intentToFragmentArguments(tempIntent));
		addressSelectDialog.show(getSupportFragmentManager(), "ADDRESS_DIALOG_BILLING");
	}
	
	@Override
	public void onAddressSelected(int dialogId, int address_id, String address,
			String address_no, String city, String post_code, String phone_no,
			String contact) {
		if (address_id == -1) {
			// to load default customer data
			loadAndSetCustomerAddressData(dialogId, address_id);
		} else {
			// just take data from dialog
			setCustomerAdressUIData(dialogId, address_id, address, address_no,
					city, post_code, phone_no, contact);
		}
	}

	private void setCustomerAdressUIData(int dialogId, int address_id,
			String address, String address_no, String city, String post_code, String phone_no,
			String contact) {
		switch (dialogId) {
		case BILLING_ADDRESS_SELECTOR:
			billingAddressId = address_id;
			billingAddressField.setText(address);
			billingAddressCity.setText(city);
			billingAddressPostalCode.setText(post_code);
			billingAddressContact.setText(contact + " - " + phone_no);
			billingAddressSelector.setText(address_no + " - " + city + " - " + address);
			if (address_id == -1) {
				billingAddressSelector.setText(getResources().getString(R.string.customer_address_default_text));
			}
			break;
		case SHIPPING_ADDRESS_SELECTOR:
			shippingAddressId = address_id;
			shippingAddressField.setText(address);
			shippingAddressCity.setText(city);
			shippingAddressPostalCode.setText(post_code);
			shippingAddressContact.setText(contact + " - " + phone_no);
			shippingAddressSelector.setText(address_no + " - " + city + " - " + address);
			if (address_id == -1) {
				shippingAddressSelector.setText(getResources().getString(R.string.customer_address_default_text));
			}
			break;
		default:
			LogUtils.LOGE(TAG, "Address dialog not supported!");
			break;
		}
	}
	
	private void loadAndSetCustomerAddressData(int addressType, int customerAddressId) {
		if (customerAddressId != -1) {
			Cursor addressCursor = getContentResolver().query(MobileStoreContract.CustomerAddresses.CONTENT_URI, CustomerAddressQuery.PROJECTION, Tables.CUSTOMER_ADDRESSES+"._id=?", new String[] { String.valueOf(customerAddressId)  }, null);
			if (addressCursor != null && addressCursor.moveToFirst()) {
				int address_id = addressCursor.getInt(CustomerAddressQuery._ID);
				String address_no = addressCursor.getString(CustomerAddressQuery.ADDRESS_NO);
				String address = addressCursor.getString(CustomerAddressQuery.ADDRESS);
				String city = addressCursor.getString(CustomerAddressQuery.CITY);
				String post_code = addressCursor.getString(CustomerAddressQuery.POST_CODE);
				String contact = addressCursor.getString(CustomerAddressQuery.CONTANCT);
				String phone_no = addressCursor.getString(CustomerAddressQuery.PHONE_NO);
				
				setCustomerAdressUIData(addressType, address_id, address, address_no, city, post_code, phone_no, contact);
			}
			if (addressCursor != null && !addressCursor.isClosed()) {
				addressCursor.close();
			}
		} else {
			if (selectedCustomerNo != null) {
				Cursor addressDefaultCursor = getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, DefaultCustomerAddressQuery.PROJECTION, Tables.CUSTOMERS+"." + Customers.CUSTOMER_NO + "=?", new String[] { String.valueOf( selectedCustomerNo )  }, null);
				if (addressDefaultCursor != null && addressDefaultCursor.moveToFirst()) {
					String address = addressDefaultCursor.getString(DefaultCustomerAddressQuery.ADDRESS);
					String city = addressDefaultCursor.getString(DefaultCustomerAddressQuery.CITY);
					String post_code = addressDefaultCursor.getString(DefaultCustomerAddressQuery.POST_CODE);
					String phone = addressDefaultCursor.getString(DefaultCustomerAddressQuery.PHONE);
					setCustomerAdressUIData(addressType, -1, address, "", city, post_code, phone, "");
				}
				if (addressDefaultCursor != null && !addressDefaultCursor.isClosed()) {
					addressDefaultCursor.close();
				}
			} else {
				setCustomerAdressUIData(addressType, -1, "-", "", "", "", "", "");
			}
		}
		
	}
	
	private interface CustomerAddressQuery {

		String[] PROJECTION = { BaseColumns._ID,
				MobileStoreContract.CustomerAddresses.ADDRESS_NO,
				MobileStoreContract.CustomerAddresses.ADDRESS,
				MobileStoreContract.CustomerAddresses.CITY,
				MobileStoreContract.CustomerAddresses.POST_CODE,
				MobileStoreContract.CustomerAddresses.CONTANCT,
				MobileStoreContract.CustomerAddresses.PHONE_NO };

		int _ID = 0;
		int ADDRESS_NO = 1;
		int ADDRESS = 2;
		int CITY = 3;
		int POST_CODE = 4;
		int CONTANCT = 5;
		int PHONE_NO = 6;
	}
	
	private interface DefaultCustomerAddressQuery {

		String[] PROJECTION = { BaseColumns._ID,
				MobileStoreContract.Customers.ADDRESS,
				MobileStoreContract.Customers.CITY,
				MobileStoreContract.Customers.POST_CODE,
				MobileStoreContract.Customers.PHONE
				};

//		int _ID = 0;
		int ADDRESS = 1;
		int CITY = 2;
		int POST_CODE = 3;
		int PHONE = 4;
	}
}
