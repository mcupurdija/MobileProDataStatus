package rs.gopro.mobile_store.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerBusinessUnits;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.ui.dialog.AddressSelectDialog;
import rs.gopro.mobile_store.ui.dialog.AddressSelectDialog.AddressSelectDialogListener;
import rs.gopro.mobile_store.ui.dialog.BusinessUnitSelectDialog;
import rs.gopro.mobile_store.ui.dialog.BusinessUnitSelectDialog.BusinessUnitSelectDialogListener;
import rs.gopro.mobile_store.ui.dialog.ContactSelectDialog;
import rs.gopro.mobile_store.ui.dialog.ContactSelectDialog.ContactSelectDialogListener;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.DocumentUtils;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.exceptions.SaleOrderValidationException;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
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
import android.text.InputFilter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SaleOrderAddEditActivity  extends BaseActivity implements LoaderCallbacks<Cursor>, AddressSelectDialogListener, ContactSelectDialogListener, BusinessUnitSelectDialogListener {

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
		MobileStoreContract.SaleOrders.REQUESTED_DELIVERY_DATE,
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
		MobileStoreContract.SaleOrders.NOTE3,
		MobileStoreContract.SaleOrders.SHIPMENT_METHOD_CODE,
		MobileStoreContract.SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE
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
		MobileStoreContract.Customers.EMAIL, 
		MobileStoreContract.Customers.HAS_BUSINESS_UNITS
	};
	
	private static String DEFAULT_VIEW_TYPE = MobileStoreContract.SaleOrders.CONTENT_ITEM_TYPE;
	
	private static String DEFAULT_INSERT_EDIT_TYPE = MobileStoreContract.SaleOrders.CONTENT_TYPE;
	
	private static final int SALE_ORDER_HEADER_LOADER = 1;
//	private static final int CUSTOMER_HEADER_LOADER = 5;
//	private static final int CUSTOMER_HEADER_LOADER_TRANSIT = 6;
	
	private static final int SALE_ORDER_INSERT_TOKEN = 0x1;
	private static final int SALE_ORDER_UPDATE_TOKEN = 0x2;
    
    private AutoCompleteTextView customerAutoComplete;
    private CustomerAutocompleteCursorAdapter customerAutoCompleteAdapter;
    private AutoCompleteTextView transitCustomerAutoComplete;
    private CustomerAutocompleteCursorAdapter transitCustomerAutoCompleteAdapter;
    
    private Button businessUnitSelector;
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
    private Button contactSelector;
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
    
    private Button requestedDeliveryDate;
    private OnDateSetListener requestedDeliveryDateSetListener;
    
    private ArrayAdapter<CharSequence> shipmentMethodCodeAdapter;
    private Spinner shipmentMethodCode;
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
    private String selectedCustomerNo = null, selectedBusinessUnitNo = null;
    private String potentialCustomerNo = null;
    
    private int shippingAddressId = -1;
    private int billingAddressId = -1;
    private int contactId = -1;
    private int customerId = -1;
    private int transitCustomerId = -1;
    private int hasBusinessUnits;
    
    private String orderDate = null;
    
    private StatementHandler statementHandler;
    
	public SaleOrderAddEditActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_sale_order);
		//setContentView(R.layout.nova_porudzbina);

		statementHandler = new StatementHandler(this);
		
		requestedDeliveryDateSetListener = new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar input = Calendar.getInstance();
				input.set(year, monthOfYear, dayOfMonth);
				
				Calendar today = Calendar.getInstance();
				
				if (org.apache.commons.lang3.time.DateUtils.isSameDay(input, today)) {
					String date = DateUtils.formatDatePickerDate(year, monthOfYear, dayOfMonth);
					requestedDeliveryDate.setText(date);
				} else if (input.before(today)) {
				   DialogUtil.showInfoDialog(SaleOrderAddEditActivity.this, "Greska", "Ne mozete planirati unazad!");
				} else {
					String date = DateUtils.formatDatePickerDate(year, monthOfYear, dayOfMonth);
					requestedDeliveryDate.setText(date);
				}
			}
		};
		
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
		System.out.println(">>> " + mUri);
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
		customerAutoComplete.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Cursor cursor = (Cursor) customerAutoCompleteAdapter.getItem(arg2);
				customerId = cursor.getInt(0);
				
				hasBusinessUnits = cursor.getInt(9);
				if (hasBusinessUnits == 0) {
					businessUnitSelector.setText(R.string.nemaPoslovnuJedinicu);
					businessUnitSelector.setClickable(false);
				} else {
					businessUnitSelector.setText(R.string.izaberiPoslovnuJedinicu);
					businessUnitSelector.setClickable(true);
				}
				
				fillOtherCustomerData(customerId);
				loadAndSetCustomerAddressData(BILLING_ADDRESS_SELECTOR, -1);
				loadAndSetCustomerAddressData(SHIPPING_ADDRESS_SELECTOR, -1);
				loadAndSetContactData(0, -1);
			}
		});
		//customerAutoComplete.setOnItemClickListener(this);
		
		transitCustomerAutoCompleteAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		transitCustomerAutoComplete.setAdapter(transitCustomerAutoCompleteAdapter);
		transitCustomerAutoComplete.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Cursor cursor = (Cursor) transitCustomerAutoCompleteAdapter.getItem(arg2);
				transitCustomerId = cursor.getInt(0);
			}
		});
		
		
		docAdapter = ArrayAdapter.createFromResource(this, R.array.sale_order_block_status_array, android.R.layout.simple_spinner_item);
		docAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		documentType = (Spinner) findViewById(R.id.edit_sale_order_dokument_type_spinner);
		documentType.setAdapter(docAdapter);
		
		documentNo = (TextView) findViewById(R.id.edit_sale_order_dokument_no_text);

	    contactName = (EditText) findViewById(R.id.edit_sale_order_contact_name_text);
	    contactName.setFilters( new InputFilter[] { new InputFilter.LengthFilter(50)} );
	    contactPhone = (EditText) findViewById(R.id.edit_sale_order_contact_phone_text);
	    contactName.setFilters( new InputFilter[] { new InputFilter.LengthFilter(30)} );
	    orderNo = (EditText) findViewById(R.id.edit_sale_order_quote_no_edit_text);
	    orderNo.setFilters( new InputFilter[] { new InputFilter.LengthFilter(20)} );
	    contactEmail = (EditText) findViewById(R.id.edit_sale_order_contact_email_text);
	    contactEmail.setFilters( new InputFilter[] { new InputFilter.LengthFilter(200)} );
	    
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
		
	    contactSelector = (Button) findViewById(R.id.edit_sale_order_contact_spinner);
	    contactSelector.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogSetContact();
			}
		});
	    
	    businessUnitSelector = (Button) findViewById(R.id.edit_sale_order_business_unit_spinner);
	    businessUnitSelector.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogSetBusinessUnit();
			}
		});
	    
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
	    documentNote.setFilters( new InputFilter[] { new InputFilter.LengthFilter(300)} );
	    headquartersNote = (EditText) findViewById(R.id.edit_sale_order_headquarters_note_value);
	    headquartersNote.setFilters( new InputFilter[] { new InputFilter.LengthFilter(300)} );
	    
	    requestedDeliveryDate = (Button) findViewById(R.id.edit_sale_order_desired_delivery_date_text);
	    requestedDeliveryDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(0);
				return;
				
			}
		});
	    
	    shipmentMethodCodeAdapter = ArrayAdapter.createFromResource(this, R.array.shipment_method_code_array_titles, android.R.layout.simple_spinner_item);
	    shipmentMethodCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    shipmentMethodCode = (Spinner) findViewById(R.id.edit_sale_order_shipment_method_code);
	    shipmentMethodCode.setAdapter(shipmentMethodCodeAdapter);
	    
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

	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar calendar = Calendar.getInstance();
		if (id == 0) {
			DatePickerDialog deliveryDate = new DatePickerDialog(this, requestedDeliveryDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			return deliveryDate;
		}
		return super.onCreateDialog(id);
	}
	
	protected void fillOtherCustomerData(int customerId2) {
		Cursor cursor = getContentResolver().query(Customers.CONTENT_URI, new String[]{ Customers.CUSTOMER_NO, Customers.CONTACT_COMPANY_NO } , Customers._ID+"=?", new String[]{ String.valueOf(customerId2) }, null);
		if (cursor.moveToNext()) {
			selectedCustomerNo = cursor.getString(0);
			potentialCustomerNo = cursor.getString(1);
		} else {
			LogUtils.LOGE(TAG, "Big trouble in sale order!");
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

	private void loadData(Cursor data, String action) {
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CUSTOMER_ID))) {
			customerId = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CUSTOMER_ID));
			Cursor customerCursor = getContentResolver().query(MobileStoreContract.Customers.buildCustomersUri(String.valueOf(customerId)), CUSTOMER_PROJECTION, null, null, null);
			if (customerCursor.moveToFirst()) {
				final int codeIndex = customerCursor
						.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
				final int nameIndex = customerCursor
						.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME);
				final String result = customerCursor.getString(codeIndex) + " - "
						+ customerCursor.getString(nameIndex);
				customerAutoComplete.setText(result);
				
				hasBusinessUnits = customerCursor.getInt(10);
			}
			fillOtherCustomerData(customerId);
		}
		
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST))) {
			transitCustomerId = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST));
			Cursor customerCursor = getContentResolver().query(MobileStoreContract.Customers.buildCustomersUri(String.valueOf(transitCustomerId)), CUSTOMER_PROJECTION, null, null, null);
			if (customerCursor.moveToFirst()) {
				final int codeIndex = customerCursor
						.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
				final int nameIndex = customerCursor
						.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME);
				final String result = customerCursor.getString(codeIndex) + " - "
						+ customerCursor.getString(nameIndex);
				transitCustomerAutoComplete.setText(result);
			}
		}
		
		String document_no = null;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO))) {	
			document_no = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO));
		} else {
			// TODO generateSaleOrderDeviceNo
			document_no = DocumentUtils.generateSaleOrderDeviceNo(salesPersonNo);
//			Cursor docNoCursor = getContentResolver().query(SaleOrders.CONTENT_URI, new String[] { SaleOrders.SALES_ORDER_NO }, SaleOrders.SALES_ORDER_NO+"=?", new String[] { document_no }, null);
//			if (docNoCursor.moveToFirst()) {
//				
//			}
		}
		
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.ORDER_DATE))) {	
			orderDate = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.ORDER_DATE));
		}
		
		int document_type = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.DOCUMENT_TYPE))) {
			document_type = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.DOCUMENT_TYPE));
		}
		
		String contact_name = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CONTACT_NAME));
		String contact_phone = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CONTACT_PHONE));
		String contact_email = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CONTACT_EMAIL));
		String order_no = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.EXTERNAL_DOCUMENT_NO));
		
		int backorder_status = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.BACKORDER_SHIPMENT_STATUS))) {
			backorder_status = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.BACKORDER_SHIPMENT_STATUS));
		}
		
		String shortcut_dimension1 = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SHORTCUT_DIMENSION_1_CODE));
		
		String location_code = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.LOCATION_CODE));
		
		String payment_option = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.PAYMENT_OPTION));
		
		String shipment_method_code = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SHIPMENT_METHOD_CODE));
		
		int sell_to_address_id = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SELL_TO_ADDRESS_ID))) {
			sell_to_address_id = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SELL_TO_ADDRESS_ID));
		}
		
		int contact_id = -1;
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CONTACT_ID))) {
			contactId = contact_id = data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CONTACT_ID));
			Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI, new String[]{ Contacts.NAME }, Contacts._ID+"=?", new String[] { String.valueOf(contact_id) }, null);
			if (cursor.moveToFirst()) {
				contactSelector.setText(cursor.getString(0));
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		
		if (!data.isNull(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE))) {
			selectedBusinessUnitNo = data.getString(data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE));
			Cursor cursor = getContentResolver().query(CustomerBusinessUnits.CONTENT_URI, new String[] { CustomerBusinessUnits._ID, CustomerBusinessUnits.ADDRESS, CustomerBusinessUnits.CITY }, CustomerBusinessUnits.UNIT_NO + "=?", new String[] { selectedBusinessUnitNo }, null);
			if (cursor.moveToFirst()) {
				String address = cursor.getString(1);
				String city = cursor.getString(2);
				businessUnitSelector.setText(String.format("%s - %s, %s", selectedBusinessUnitNo, address, city));
				businessUnitSelector.setClickable(true);
			}
		} else {
			if (customerId != -1) {
				if (hasBusinessUnits == 0) {
					businessUnitSelector.setText(R.string.nemaPoslovnuJedinicu);
					businessUnitSelector.setClickable(false);
				} else {
					businessUnitSelector.setText(R.string.izaberiPoslovnuJedinicu);
					businessUnitSelector.setClickable(true);
				}
			}
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
		
		int delivery_date_column = data.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.REQUESTED_DELIVERY_DATE);
		if (!data.isNull(delivery_date_column)) {
			String delivery_date = DateUtils.formatDbDateForPresentation(data.getString(delivery_date_column));
			requestedDeliveryDate.setText(delivery_date);
		} else {
			requestedDeliveryDate.setText("");
		}
		
		if (shipment_method_code != null) {
			ArrayList<String> shipmentMethodCodeArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.shipment_method_code_array_values)));
			int shipmentMethodCodePosition = shipmentMethodCodeArray.indexOf(shipment_method_code);
			if (shipmentMethodCodePosition != -1) {
				shipmentMethodCode.setSelection(shipmentMethodCodePosition);
			} else {
				LogUtils.LOGE(TAG, "No shipment method code for value:" + shipmentMethodCodePosition);
			}
		}
		
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
		
		
				
//		if (customer_id != -1) {
//			initCustomerLoad(customer_id, "MAIN");
//		}
		
//		if (transit_customer_id != -1) {
//			initCustomerLoad(transit_customer_id, "TRANSIT");
//		}
		
		documentNo.setText(document_no);
		
		if (document_type != -1) {
			documentType.setSelection(document_type);
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
		
		billingAddressId = sell_to_address_id;
		loadAndSetCustomerAddressData(BILLING_ADDRESS_SELECTOR, billingAddressId);
		
		shippingAddressId = shipp_to_address_id;
		loadAndSetCustomerAddressData(SHIPPING_ADDRESS_SELECTOR, shippingAddressId);
		
		
//		loadAndSetContactData(0, contactId);
		
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
		contactSelector.setFocusable(false);
		hideDiscount.setFocusable(false);
		showDeclaration.setFocusable(false);
		requestedDeliveryDate.setFocusable(false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = null;
//		String customerFilter = "dummy";
		switch (id) {
		case SALE_ORDER_HEADER_LOADER:
			cursorLoader = new CursorLoader(this, mUri, SALES_ORDER_PROJECTION, null, null, null);
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
		default:
			break;
		}
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
		
		outState.putInt(EXTRA_CUSTOMER_ID, customerId);
		outState.putString("SELECTED_CUSTOMER_NO", selectedCustomerNo);
		outState.putString("POTENTIAL_CUSTOMER_NO", potentialCustomerNo);
		outState.putString("DOCUMENT_NO", documentNo.getText().toString());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		customerId = savedInstanceState.getInt(EXTRA_CUSTOMER_ID);
		documentNo.setText(savedInstanceState.getString("DOCUMENT_NO"));
		selectedCustomerNo = savedInstanceState.getString("SELECTED_CUSTOMER_NO");
		potentialCustomerNo = savedInstanceState.getString("POTENTIAL_CUSTOMER_NO");
	}

	private ContentValues getInputData() throws SaleOrderValidationException {
		ContentValues localValues = new ContentValues();
		
//		String customer_auto_complete = customerAutoComplete.getText().toString().trim();
		if (customerId != -1) {
			//Cursor customerItemCursor = (Cursor) customerAutoCompleteAdapter.getItem(customerAutoCompleteAdapter.getIdForTitle(customer_auto_complete));
//			int customer_id = customerAutoCompleteAdapter.getIdForTitle(customer_auto_complete);//customerItemCursor.getInt(customerItemCursor.getColumnIndexOrThrow(MobileStoreContract.Customers._ID));
			localValues.put(MobileStoreContract.SaleOrders.CUSTOMER_ID, Integer.valueOf(customerId));
		} else {
			throw new SaleOrderValidationException("Kupac nije izabran!");
			//localValues.putNull(MobileStoreContract.SaleOrders.CUSTOMER_ID);
		}
		
		if (hasBusinessUnits == 1 && selectedBusinessUnitNo == null) {
			throw new SaleOrderValidationException(getString(R.string.obaveznaPoslovnaJedinica));
		}
		
//		String transfer_customer_auto_complete = transitCustomerAutoComplete.getText().toString().trim();
		if (transitCustomerId != -1) {
			//Cursor customerItemCursor = (Cursor) transitCustomerAutoCompleteAdapter.getItem(transitCustomerAutoCompleteAdapter.getIdForTitle(transfer_customer_auto_complete));
//			int transit_customer_id = transitCustomerAutoCompleteAdapter.getIdForTitle(transfer_customer_auto_complete);//customerItemCursor.getInt(customerItemCursor.getColumnIndexOrThrow(MobileStoreContract.Customers._ID));
			localValues.put(MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST, Integer.valueOf(transitCustomerId));
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
		
		if (isPotentialCustomer(customerId)) {
			if (document_type != 1) {
				throw new SaleOrderValidationException("Potencijalni kupac može biti izabran samo na ponudi!");
			}
		}
		
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
		
		if (contactId != -1) {
			localValues.put(MobileStoreContract.SaleOrders.CONTACT_ID, Integer.valueOf(contactId));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.CONTACT_ID);
		}
		
//		if (customerContactId != null) {
//			localValues.put(MobileStoreContract.SaleOrders.CONTACT_ID, customerContactId);
//		} else {
//			localValues.putNull(MobileStoreContract.SaleOrders.CONTACT_ID);
//		}
		
		String contact_email = contactEmail.getText().toString().trim().replace(";", ",");
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
			localValues.put(MobileStoreContract.SaleOrders.EXTERNAL_DOCUMENT_NO, order_no);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.EXTERNAL_DOCUMENT_NO);
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
		
		int shipment_method_code = shipmentMethodCode.getSelectedItemPosition();
		localValues.put(MobileStoreContract.SaleOrders.SHIPMENT_METHOD_CODE, getResources().getStringArray(R.array.shipment_method_code_array_values)[shipment_method_code]);
		
		String delivery_date = requestedDeliveryDate.getText().toString().trim();
		if (!delivery_date.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.REQUESTED_DELIVERY_DATE, DateUtils.formatPickerInputForDb(delivery_date));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.REQUESTED_DELIVERY_DATE);
		}
		
		int order_condition_status = orderConditionStatus.getSelectedItemPosition();
		localValues.put(MobileStoreContract.SaleOrders.ORDER_CONDITION_STATUS, Integer.valueOf(order_condition_status));
		
		if (selectedBusinessUnitNo != null) {
			localValues.put(MobileStoreContract.SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE, selectedBusinessUnitNo);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE);
		}
		
		return localValues;
	}
	
	private boolean isPotentialCustomer(int customerId2) {
		Cursor potentialCustomerCursor = getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, new String[] {Customers.CONTACT_COMPANY_NO}, 
				"("+Customers.CONTACT_COMPANY_NO + " is null or " + Customers.CONTACT_COMPANY_NO + "='')" + " and " + Customers._ID + "=?" , new String[] { String.valueOf(customerId2) }, null);
		if (potentialCustomerCursor.moveToFirst()) {
			return true;
		}
		potentialCustomerCursor.close();
		return false;
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
		
		Cursor addressDefaultCursor = getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, DefaultCustomerAddressQuery.PROJECTION, Tables.CUSTOMERS+"." + Customers.CUSTOMER_NO + "=?", new String[] { String.valueOf( selectedCustomerNo )  }, null);
		if (addressDefaultCursor != null && addressDefaultCursor.moveToFirst()) {
			String address = addressDefaultCursor.getString(DefaultCustomerAddressQuery.ADDRESS);
			String city = addressDefaultCursor.getString(DefaultCustomerAddressQuery.CITY);
			String post_code = addressDefaultCursor.getString(DefaultCustomerAddressQuery.POST_CODE);
			String phone = addressDefaultCursor.getString(DefaultCustomerAddressQuery.PHONE);
			
			addressSelectDialog.setDefault_address(address);
			addressSelectDialog.setDefault_city(city);
			addressSelectDialog.setDefault_post_code(post_code);
			
			setCustomerAdressUIData(SHIPPING_ADDRESS_SELECTOR, -1, address, "", city, post_code, phone, "");
		}
		if (addressDefaultCursor != null && !addressDefaultCursor.isClosed()) {
			addressDefaultCursor.close();
		}
		
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
		
		Cursor addressDefaultCursor = getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, DefaultCustomerAddressQuery.PROJECTION, Tables.CUSTOMERS+"." + Customers.CUSTOMER_NO + "=?", new String[] { String.valueOf( selectedCustomerNo )  }, null);
		if (addressDefaultCursor != null && addressDefaultCursor.moveToFirst()) {
			String address = addressDefaultCursor.getString(DefaultCustomerAddressQuery.ADDRESS);
			String city = addressDefaultCursor.getString(DefaultCustomerAddressQuery.CITY);
			String post_code = addressDefaultCursor.getString(DefaultCustomerAddressQuery.POST_CODE);
			String phone = addressDefaultCursor.getString(DefaultCustomerAddressQuery.PHONE);
			
			addressSelectDialog.setDefault_address(address);
			addressSelectDialog.setDefault_city(city);
			addressSelectDialog.setDefault_post_code(post_code);
			
			setCustomerAdressUIData(BILLING_ADDRESS_SELECTOR, -1, address, "", city, post_code, phone, "");
		}
		if (addressDefaultCursor != null && !addressDefaultCursor.isClosed()) {
			addressDefaultCursor.close();
		}
		
		addressSelectDialog.setArguments(BaseActivity
				.intentToFragmentArguments(tempIntent));
		addressSelectDialog.show(getSupportFragmentManager(), "ADDRESS_DIALOG_BILLING");
	}
	
	private void dialogSetBusinessUnit() {
		if (selectedCustomerNo == null) {
			DialogUtil.showInfoDialog(this, "Upozorenje", "Kupac nije izabran!");
			return;
		}
		
		BusinessUnitSelectDialog busd = BusinessUnitSelectDialog.newInstance(selectedCustomerNo);
		busd.show(getSupportFragmentManager(), "BUSINESS_UNIT_DIALOG");
	}
	
	private void dialogSetContact() {
		if (potentialCustomerNo == null) {
			DialogUtil.showInfoDialog(this, "Upozorenje", "Kupac nije izabran!");
			return;
		}
		ContactSelectDialog addressSelectDialog = new ContactSelectDialog();
		Intent tempIntent = new Intent(Intent.ACTION_VIEW, null);
		
		tempIntent.putExtra(ContactSelectDialog.EXTRA_POTENTIAL_CUSTOMER_NO, potentialCustomerNo);
		tempIntent.putExtra(ContactSelectDialog.EXTRA_DIALOG_ID, 0);
		addressSelectDialog.setArguments(BaseActivity
				.intentToFragmentArguments(tempIntent));
		addressSelectDialog.show(getSupportFragmentManager(), "CONTACT_DIALOG");
	}
	
	@Override
	public void onAddressSelected(int dialogId, int address_id, String address,
			String address_no, String city, String post_code, String phone_no,
			String contact) {
//		if (address_id == -1) {
//			// to load default customer data
//			loadAndSetCustomerAddressData(dialogId, address_id);
//		} else {
//			// just take data from dialog
//			setCustomerAdressUIData(dialogId, address_id, address, address_no,
//					city, post_code, phone_no, contact);
//		}
		setCustomerAdressUIData(dialogId, address_id, address, address_no,
				city, post_code, phone_no, contact);
	}

	private void setContactUIData(int dialogId, int contact_id,
			String contact_name, String contact_phone, String contact_email) {
		switch (dialogId) {
		case 0:
			contactId = contact_id;
			
			contactName.setText("");
			contactPhone.setText("");
			contactEmail.setText("");

			
			contactName.setText(contact_name);
			contactPhone.setText(contact_phone);
			contactEmail.setText(contact_email);

			StringBuilder contactCaption = new StringBuilder();
			if (contact_name != null && contact_name.length() > 0) {
				contactCaption.append(contact_name);
			}
			if (contact_phone != null && contact_phone.length() > 0) {
				contactCaption.append(" - ").append(contact_phone);
			}
			if (contact_email != null && contact_email.length() > 0) {
				contactCaption.append(" - ").append(contact_email);
			}
			contactSelector.setText(contactCaption.toString());

			if (contact_id == -1) {
				contactSelector.setText(getResources().getString(R.string.customer_contact_default_text));
			}
			break;
		default:
			LogUtils.LOGE(TAG, "Contact dialog not supported!");
			break;
		}
	}
	
	private void setCustomerAdressUIData(int dialogId, int address_id,
			String address, String address_no, String city, String post_code, String phone_no,
			String contact) {
		switch (dialogId) {
		case BILLING_ADDRESS_SELECTOR:
			billingAddressId = address_id;
			
			billingAddressField.setText("");
			billingAddressCity.setText("");
			billingAddressPostalCode.setText("");
			billingAddressContact.setText("");
			
			billingAddressField.setText(address);
			billingAddressCity.setText(city);
			billingAddressPostalCode.setText(post_code);
			String contactExtend = "";
			if (phone_no != null && phone_no.length() > 0) {
				contactExtend = phone_no;
			}
			billingAddressContact.setText(contactExtend);
			StringBuilder addressCaption = new StringBuilder();
			if (address_no != null && address_no.length() > 0) {
				addressCaption.append(address_no);
			}
			if (city != null && city.length() > 0) {
				addressCaption.append(" - ").append(city);
			}
			if (address != null && address.length() > 0) {
				addressCaption.append(" - ").append(address);
			}
			billingAddressSelector.setText(addressCaption.toString());
			if (address_id == -1) {
				billingAddressSelector.setText(getResources().getString(R.string.customer_address_default_text));
			}
			break;
		case SHIPPING_ADDRESS_SELECTOR:
			shippingAddressId = address_id;
			
			shippingAddressField.setText("");
			shippingAddressCity.setText("");
			shippingAddressPostalCode.setText("");
			shippingAddressContact.setText("");
			
			shippingAddressField.setText(address);
			shippingAddressCity.setText(city);
			shippingAddressPostalCode.setText(post_code);
			shippingAddressContact.setText(phone_no);
			StringBuilder addressShippCaption = new StringBuilder();
			if (address_no != null && address_no.length() > 0) {
				addressShippCaption.append(address_no);
			}
			if (city != null && city.length() > 0) {
				addressShippCaption.append(" - ").append(city);
			}
			if (address != null && address.length() > 0) {
				addressShippCaption.append(" - ").append(address);
			}
			shippingAddressSelector.setText(addressShippCaption.toString());
			if (address_id == -1) {
				shippingAddressSelector.setText(getResources().getString(R.string.customer_address_default_text));
			}
			break;
		default:
			LogUtils.LOGE(TAG, "Address dialog not supported!");
			break;
		}
	}
	
	private void loadAndSetContactData(int contactType, int contact_id) {
		if (contact_id != -1) {
			Cursor contactCursor = getContentResolver().query(MobileStoreContract.Contacts.CONTENT_URI, ContactQuery.PROJECTION, Tables.CONTACTS+"._id=?", new String[] { String.valueOf(contact_id)  }, null);
			if (contactCursor != null && contactCursor.moveToFirst()) {
//				int address_id = contactCursor.getInt(ContactQuery._ID);
				String name = contactCursor.getString(ContactQuery.NAME);
				String email = contactCursor.getString(ContactQuery.EMAIL);
				String phone_no = contactCursor.getString(ContactQuery.PHONE);
				
				setContactUIData(contactType, contact_id, name, phone_no, email);
			} else {
				setContactUIData(contactType, -1, "", "", "");
			}
			if (contactCursor != null && !contactCursor.isClosed()) {
				contactCursor.close();
			}
		} else {
			setContactUIData(contactType, -1, "", "", "");
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
			} else {
				setCustomerAdressUIData(addressType, -1, "-", "", "", "", "", "");
			}
			if (addressCursor != null && !addressCursor.isClosed()) {
				addressCursor.close();
			}
		} else {
			// this check is here when this method is called not after dialog but from this activity
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
	
	@Override
	public void onContactSelected(int dialogId, int contact_id,
			String contact_name, String contact_phone, String contact_email) {
		setContactUIData(dialogId, contact_id, contact_name, contact_phone, contact_email);
	}
	
	@Override
	public void onBusinessUnitSelected(int unit_id, String address,
			String unit_no, String unit_name, String city, String post_code,
			String phone_no, String contact) {
		
		selectedBusinessUnitNo = unit_no;
		
		String buttonText = String.format("%s - %s, %s", unit_no, address, city);
		businessUnitSelector.setText(buttonText);
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
	
	private interface ContactQuery {

		String[] PROJECTION = { BaseColumns._ID,
				MobileStoreContract.Contacts.NAME,
				MobileStoreContract.Contacts.NAME2,
				MobileStoreContract.Contacts.ADDRESS,
				MobileStoreContract.Contacts.CITY,
				MobileStoreContract.Contacts.POST_CODE,
				MobileStoreContract.Contacts.EMAIL,
				MobileStoreContract.Contacts.PHONE
				};

//		int _ID = 0;
		int NAME = 1;
//		int NAME2 = 2;
//		int ADDRESS = 3;
//		int CITY = 4;
//		int POST_CODE = 5;
		int EMAIL = 6;
		int PHONE = 7;
	}
	
}
