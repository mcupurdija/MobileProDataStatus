package rs.gopro.mobile_store.ui.fragment;

import static rs.gopro.mobile_store.util.LogUtils.makeLogTag;

import java.util.HashMap;
import java.util.Map;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.ui.components.ItemAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ItemQtySalesPriceAndDiscSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SaleOrderAddEditLineFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = makeLogTag(SaleOrderAddEditLineFragment.class);
	
	private Uri mSaleOrderLinesUri;
	private int mSelectedSaleOrderLineId;
	private int mSelectedSaleOrderId;
	private int salesPersonId;
	private String salesPersonNo;
	private int documentId = -1;
	private int itemId = -1;
	private String itemNo;
	private int itemCampaignStatus;
	private String documentNo;
	private Integer documentType;
	private int customerId;
	private String customerNo;
	private boolean isServiceCalled = false;
	
	private ItemAutocompleteCursorAdapter itemAutocompleteAdapter;
	ArrayAdapter<CharSequence> backorderAdapter;
	ArrayAdapter<CharSequence> campaignStatusAdapter;
	ArrayAdapter<CharSequence> quoteRefusedAdapter;
	
	private AutoCompleteTextView mItemAutocomplete;
	
	private TextView mDocumentNo;
	private TextView mDocumentType;
	private TextView mCustomer;
	
	private EditText mQuantity;
	private EditText mQuantityAvailable;
	
	private EditText mPrice;
	private EditText mPriceEur;
	
	private EditText mDiscount;
	private EditText mDiscountMin;
	private EditText mDiscountMax;
	
	private CheckBox mAvailableToWholeShipment;
	private Spinner mBackorderStatus;
	private Spinner mQuoteRefused;
	private Spinner mCampaignStatus;
	
	private Button loadItemData;
	private Button saveData;
	
	private Map<Integer, Boolean> loaderState = new HashMap<Integer, Boolean>();
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		System.out.println("STATUS IS: " + syncResult.getStatus());
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (ItemQtySalesPriceAndDiscSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				ItemQtySalesPriceAndDiscSyncObject syncObject = (ItemQtySalesPriceAndDiscSyncObject) syncResult.getComplexResult();
				mDiscountMin.setText(syncObject.getpMinimumDiscountPctAsTxt());
				mDiscountMax.setText(syncObject.getpMaximumDiscountPctAsTxt());
				mQuantityAvailable.setText(syncObject.getpQuantityAsTxt());
				mDiscount.setText(syncObject.getpDiscountPctAsTxt());
				mPrice.setText(syncObject.getpSalesPriceRSDAsTxt());
				mPriceEur.setText(syncObject.getpSalesPriceEURAsTxt());
			}
		}
	}
	
	public interface Callbacks {
		public void onSaleOrderLineIdAvailable(String saleOrderLineId);
		
		public void onSaleOrderLineSaved();
	}
	
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onSaleOrderLineIdAvailable(String saleOrderLineId) {} {
		}
		
		@Override
		public void onSaleOrderLineSaved() {
		}
		
	};
	
	private Callbacks mCallbacks = sDummyCallbacks;
	
	public SaleOrderAddEditLineFragment() {
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        final Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
        mSaleOrderLinesUri = intent.getData();
        if (mSaleOrderLinesUri == null) {
            return;
        }

        salesPersonId = Integer.valueOf(SharedPreferencesUtil.getSalePersonId(getActivity())).intValue();
        
        // Start background query to load sales line details
        getLoaderManager().initLoader(SaleOrderLinesQuery._TOKEN, null, this);
        
        LogUtils.LOGI(TAG, "Created SaleOrderLinesPreviewListFragment");
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_sale_oreder_line_add_edit, null);
        
        itemAutocompleteAdapter = new ItemAutocompleteCursorAdapter(getActivity(), null);
        mItemAutocomplete = (AutoCompleteTextView) rootView.findViewById(R.id.so_line_item_no_value);
        mItemAutocomplete.setAdapter(itemAutocompleteAdapter);
        
        mCustomer = (TextView) rootView.findViewById(R.id.so_line_header_document_customer_label);
        mDocumentNo = (TextView) rootView.findViewById(R.id.so_line_header_document_no_label);
        mDocumentType = (TextView) rootView.findViewById(R.id.so_line_header_document_type_label);
        
        mQuantity = (EditText) rootView.findViewById(R.id.so_line_item_quantity_value);
        mQuantityAvailable = (EditText) rootView.findViewById(R.id.so_line_item_max_quantity_value);
        mQuantityAvailable.setFocusable(false);
        
        mPrice = (EditText) rootView.findViewById(R.id.so_line_item_price_value);
        mPrice.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				calculatePriceWithDiscount();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
        mPriceEur = (EditText) rootView.findViewById(R.id.so_line_item_sugg_price_value);
        mPrice.setFocusable(false);
        mPriceEur.setFocusable(false);
        
        mDiscount = (EditText) rootView.findViewById(R.id.so_line_item_discount_value);
        mDiscount.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				calculatePriceWithDiscount();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
        mDiscountMin = (EditText) rootView.findViewById(R.id.so_line_item_min_discount_value);
        mDiscountMin.setFocusable(false);
        mDiscountMax = (EditText) rootView.findViewById(R.id.so_line_item_max_discount_value);
        mDiscountMax.setFocusable(false);
        
        mAvailableToWholeShipment = (CheckBox) rootView.findViewById(R.id.so_line_avail_to_whole_ship_check_box);
        
        backorderAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.backorder_type_array, android.R.layout.simple_spinner_item);
		backorderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mBackorderStatus = (Spinner) rootView.findViewById(R.id.so_line_backorder_spinner);
		mBackorderStatus.setAdapter(backorderAdapter);
		
		campaignStatusAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.item_camp_status_array, android.R.layout.simple_spinner_item);
		campaignStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCampaignStatus = (Spinner) rootView.findViewById(R.id.so_line_item_campaign_spinner);
		mCampaignStatus.setAdapter(campaignStatusAdapter);
		
		quoteRefusedAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.quote_refused_type_array, android.R.layout.simple_spinner_item);
		quoteRefusedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mQuoteRefused = (Spinner) rootView.findViewById(R.id.so_line_quote_refused_spinner);
        mQuoteRefused.setAdapter(quoteRefusedAdapter);
        
        loadItemData = (Button)rootView.findViewById(R.id.so_line_read_item_data_button);
        loadItemData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isServiceCalled = true;

				getActivity().getSupportLoaderManager().restartLoader(ItemQuery._TOKEN, null,SaleOrderAddEditLineFragment.this);
				getActivity().getSupportLoaderManager().restartLoader(SaleOrderQuery._TOKEN, null,SaleOrderAddEditLineFragment.this);
				
			}
		});
        saveData = (Button)rootView.findViewById(R.id.so_line_save_button);
        saveData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveForm();
			}
		});
        
        return rootView;
    }
	
	protected void calculatePriceWithDiscount() {
		String price = mPrice.getText().toString();
		String discount = mDiscount.getText().toString();
		if (price == null || discount == null || price.length() < 1 || discount.length() < 1) {
			return;
		}
		try {
			double price_d = Double.parseDouble(price);
			double discount_d = Double.parseDouble(discount);
			double discounted_price = price_d * (discount_d/100);
			mPriceEur.setText(String.valueOf(discounted_price));
		} catch (NumberFormatException e) {
			LogUtils.LOGE(TAG, "Not cool formats.", e);
		}
	}

	protected void saveForm() {
		ContentValues localValues = new ContentValues();
		
		String item_auto_complete = mItemAutocomplete.getText().toString().trim();
		if (itemAutocompleteAdapter.getIdForTitle(item_auto_complete) != -1) {
			//Cursor customerItemCursor = (Cursor) customerAutoCompleteAdapter.getItem(customerAutoCompleteAdapter.getIdForTitle(customer_auto_complete));
			int item_id = itemAutocompleteAdapter.getIdForTitle(item_auto_complete);//customerItemCursor.getInt(customerItemCursor.getColumnIndexOrThrow(MobileStoreContract.Customers._ID));
			localValues.put(MobileStoreContract.SaleOrderLines.ITEM_ID, Integer.valueOf(item_id));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrderLines.ITEM_ID);
		}

		String quantity = mQuantity.getText().toString().trim();
		if (quantity != null && !quantity.equals("")) {
			localValues.put(MobileStoreContract.SaleOrderLines.QUANTITY, Double.valueOf(quantity));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrderLines.QUANTITY);
		}
		
		String quantity_available = mQuantityAvailable.getText().toString().trim();
		if (quantity_available != null && !quantity_available.equals("")) {
			localValues.put(MobileStoreContract.SaleOrderLines.QUANTITY_AVAILABLE, Double.valueOf(quantity_available));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrderLines.QUANTITY_AVAILABLE);
		}
		
		String price = mPrice.getText().toString().trim();
		if (price != null && !price.equals("")) {
			localValues.put(MobileStoreContract.SaleOrderLines.PRICE, Double.valueOf(price));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrderLines.PRICE);
		}
		
		String price_eur = mPriceEur.getText().toString().trim();
		if (price_eur != null && !price_eur.equals("")) {
			localValues.put(MobileStoreContract.SaleOrderLines.PRICE_EUR, Double.valueOf(price_eur));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrderLines.PRICE_EUR);
		}
		
		String discount = mDiscount.getText().toString().trim();
		if (discount != null && !discount.equals("")) {
			localValues.put(MobileStoreContract.SaleOrderLines.REAL_DISCOUNT, Double.valueOf(discount));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrderLines.REAL_DISCOUNT);
		}
		
		String discountMin = mDiscountMin.getText().toString().trim();
		if (discountMin != null && !discountMin.equals("")) {
			localValues.put(MobileStoreContract.SaleOrderLines.MIN_DISCOUNT, Double.valueOf(discountMin));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrderLines.MIN_DISCOUNT);
		}
		
		String discountMax = mDiscountMax.getText().toString().trim();
		if (discountMax != null && !discountMax.equals("")) {
			localValues.put(MobileStoreContract.SaleOrderLines.MAX_DISCOUNT, Double.valueOf(discountMax));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrderLines.MAX_DISCOUNT);
		}
		
		int backorder_status = mBackorderStatus.getSelectedItemPosition();
		localValues.put(MobileStoreContract.SaleOrderLines.BACKORDER_STATUS, Integer.valueOf(backorder_status));
		
		int campaign_status = mCampaignStatus.getSelectedItemPosition();
		localValues.put(MobileStoreContract.SaleOrderLines.CAMPAIGN_STATUS, Integer.valueOf(campaign_status));
		
		int quote_refused = mQuoteRefused.getSelectedItemPosition();
		localValues.put(MobileStoreContract.SaleOrderLines.QUOTE_REFUSED_STATUS, Integer.valueOf(quote_refused));
		
		getActivity().getContentResolver().update(MobileStoreContract.SaleOrderLines.buildSaleOrderLinesUri(String.valueOf(mSelectedSaleOrderId)), localValues, MobileStoreContract.SaleOrderLines._ID+"=?", new String[] { String.valueOf(mSelectedSaleOrderLineId) });
		
		mCallbacks.onSaleOrderLineSaved();
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }
	
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }
	
    @Override
    public void onResume() {
    	super.onResume();
    	IntentFilter itemQtySalesPriceAndDiscSync = new IntentFilter(ItemQtySalesPriceAndDiscSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, itemQtySalesPriceAndDiscSync);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		switch (id) {
		case SaleOrderLinesQuery._TOKEN:
			loaderState.put(SaleOrderLinesQuery._TOKEN, false);
			return new CursorLoader(getActivity(), mSaleOrderLinesUri, SaleOrderLinesQuery.PROJECTION, null, null,
	                null);
		case ItemQuery._TOKEN:
			loaderState.put(ItemQuery._TOKEN, false);
			if (itemId != -1) { 
				return new CursorLoader(getActivity(), MobileStoreContract.Items.buildItemUri(String.valueOf(itemId)), ItemQuery.PROJECTION, null, null,
						null);
			} else {
				LogUtils.LOGE(TAG, "Item not selected!");
				return null;
			}
		case SaleOrderQuery._TOKEN:
			loaderState.put(SaleOrderQuery._TOKEN, false);
			if (documentId != -1) { 
				return new CursorLoader(getActivity(), MobileStoreContract.SaleOrders.buildSaleOrderUri(String.valueOf(documentId)), SaleOrderQuery.PROJECTION, null, null,
						null);
			} else {
				LogUtils.LOGE(TAG, "Document not loaded!");
				return null;
			}
		case CustomerQuery._TOKEN:
			loaderState.put(CustomerQuery._TOKEN, false);
			if (documentId != -1) { 
				return new CursorLoader(getActivity(), MobileStoreContract.Customers.buildCustomersUri(String.valueOf(customerId)), CustomerQuery.PROJECTION, null, null,
						null);
			} else {
				LogUtils.LOGE(TAG, "Customer not loaded!");
				return null;
			}
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
		case SaleOrderLinesQuery._TOKEN:
			loaderState.put(SaleOrderLinesQuery._TOKEN, true);
			buildUiFromCursor(cursor);
			break;
		case ItemQuery._TOKEN:
			loaderState.put(ItemQuery._TOKEN, true);
			buildItem(cursor);
			break;
		case SaleOrderQuery._TOKEN:
			loaderState.put(SaleOrderQuery._TOKEN, true);
			buildSaleOrder(cursor);
			break;
		case CustomerQuery._TOKEN:
			loaderState.put(CustomerQuery._TOKEN, true);
			buildCustomer(cursor);
			break;
		default:
			cursor.close();
			break;
		}
	}

	private void buildCustomer(Cursor cursor) {
		if (getActivity() == null) {
            return;
        }

        if (!cursor.moveToFirst()) {
            return;
        }
		
        if (!cursor.isNull(CustomerQuery.CUSTOMER_NO)) {
        	customerNo = cursor.getString(CustomerQuery.CUSTOMER_NO);
        }
        
        checkLoaderState();
	}

	private void buildSaleOrder(Cursor cursor) {
		if (getActivity() == null) {
            return;
        }

        if (!cursor.moveToFirst()) {
            return;
        }
		
        
        if (!cursor.isNull(SaleOrderQuery.DOCUMENT_TYPE)) {
        	documentType = cursor.getInt(SaleOrderQuery.DOCUMENT_TYPE);
        }
        if (!cursor.isNull(SaleOrderQuery.CUSTOMER_ID)) {
        	customerId = cursor.getInt(SaleOrderQuery.CUSTOMER_ID);
        }
        
        Cursor cursorCustomer = getActivity().getContentResolver().query(MobileStoreContract.Customers.buildCustomersUri(String.valueOf(customerId)), CustomerQuery.PROJECTION, null, null, null);
        buildCustomer(cursorCustomer);
        
        Cursor cursorSalesPerson = getActivity().getContentResolver().query(MobileStoreContract.SalesPerson.buildSalesPersonUri(salesPersonId), SalesPersonQuery.PROJECTION, null, null, null);
        buildSalesPerson(cursorSalesPerson);
        
        checkLoaderState();
	}

	private void buildSalesPerson(Cursor cursor) {
		if (getActivity() == null) {
            return;
        }

        if (!cursor.moveToFirst()) {
            return;
        }
        
        if (!cursor.isNull(SalesPersonQuery.SALE_PERSON_NO)) {
        	salesPersonNo = cursor.getString(SalesPersonQuery.SALE_PERSON_NO);
        }
	}
	
	private void buildItem(Cursor cursor) {
		if (getActivity() == null) {
            return;
        }

        if (!cursor.moveToFirst()) {
            return;
        }
        
        if (!cursor.isNull(ItemQuery.ITEM_NO)) {
        	itemNo = cursor.getString(ItemQuery.ITEM_NO);
        }
        
        if (!cursor.isNull(ItemQuery.ITEM_CAMPAIGN_STATUS)) {
        	itemCampaignStatus = cursor.getInt(ItemQuery.ITEM_CAMPAIGN_STATUS);
        }
        
        checkLoaderState();
	}

	private void checkLoaderState() {
		if (isServiceCalled && loaderState.get(SaleOrderQuery._TOKEN) && loaderState.get(ItemQuery._TOKEN)) {
			doWsAction();
			isServiceCalled = false;
		}
	}
	
	private void doWsAction() {
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		ItemQtySalesPriceAndDiscSyncObject itemQtySalesPriceAndDiscSyncObject = new ItemQtySalesPriceAndDiscSyncObject(itemNo, "001", itemCampaignStatus, customerNo, "", Double.valueOf(0), salesPersonNo, documentType, -1, "", "", "", "", "", "");
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemQtySalesPriceAndDiscSyncObject);
		getActivity().startService(intent);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	public void buildUiFromCursor(Cursor cursor) {
        if (getActivity() == null) {
            return;
        }

        if (!cursor.moveToFirst()) {
            return;
        }
        
        if (!cursor.isNull(SaleOrderLinesQuery.SALE_ORDER_ID)) { 
        	documentId = cursor.getInt(SaleOrderLinesQuery.SALE_ORDER_ID);
        }
        
        mDocumentNo.setText(documentNo);
        mDocumentType.setText(String.valueOf(documentType));
        mCustomer.setText(customerNo);
        
        int item_id = -1;
        if (!cursor.isNull(SaleOrderLinesQuery.ITEM_ID)) {
        	itemId = item_id = cursor.getInt(SaleOrderLinesQuery.ITEM_ID);
        	Cursor c = getActivity().getContentResolver().query(MobileStoreContract.Items.buildItemUri(String.valueOf(item_id)), new String[] {MobileStoreContract.Items.ITEM_NO, MobileStoreContract.Items.DESCRIPTION}, null, null, null);
        	if (c.moveToFirst()) {
	        	final int codeIndex = c
	    				.getColumnIndexOrThrow(MobileStoreContract.Items.ITEM_NO);
	    		final int nameIndex = c
	    				.getColumnIndexOrThrow(MobileStoreContract.Items.DESCRIPTION);
	    		final String result = c.getString(codeIndex) + " - "
	    				+ c.getString(nameIndex);
	        	mItemAutocomplete.setText(result);
	        	itemAutocompleteAdapter.setIdForTitle(result, cursor.getInt(cursor.getColumnIndexOrThrow(MobileStoreContract.SaleOrderLines.ITEM_ID)));
        	}
        }
        
        double quantity = -1;
        if (!cursor.isNull(SaleOrderLinesQuery.QUANTITY)) {
        	quantity = cursor.getDouble(SaleOrderLinesQuery.QUANTITY);
        	mQuantity.setText(String.valueOf(quantity));
        }
        
        double quantity_available = -1;
        if (!cursor.isNull(SaleOrderLinesQuery.QUANTITY_AVAILABLE)) {
        	quantity_available = cursor.getDouble(SaleOrderLinesQuery.QUANTITY_AVAILABLE);
        	mQuantityAvailable.setText(String.valueOf(quantity_available));
        }
        
        double price = -1;
        if (!cursor.isNull(SaleOrderLinesQuery.PRICE)) {
        	price = cursor.getDouble(SaleOrderLinesQuery.PRICE);
        	mPrice.setText(String.valueOf(price));
        }
        
        double price_eur = -1;
        if (!cursor.isNull(SaleOrderLinesQuery.PRICE_EUR)) {
        	price_eur = cursor.getDouble(SaleOrderLinesQuery.PRICE_EUR);
        	mPriceEur.setText(String.valueOf(price_eur));
        }
        
        double discount = -1;
        if (!cursor.isNull(SaleOrderLinesQuery.REAL_DISCOUNT)) {
        	discount = cursor.getDouble(SaleOrderLinesQuery.REAL_DISCOUNT);
        	mDiscount.setText(String.valueOf(discount));
        }
        
        double discount_min = -1;
        if (!cursor.isNull(SaleOrderLinesQuery.MIN_DISCOUNT)) {
        	discount_min = cursor.getDouble(SaleOrderLinesQuery.MIN_DISCOUNT);
        	mDiscountMin.setText(String.valueOf(discount_min));
        }
        
        double discount_max = -1;
        if (!cursor.isNull(SaleOrderLinesQuery.MAX_DISCOUNT)) {
        	discount_max = cursor.getDouble(SaleOrderLinesQuery.MAX_DISCOUNT);
        	mDiscountMax.setText(String.valueOf(discount_max));
        }
        
        int available_to_whole_shipment = -1;
        if (!cursor.isNull(SaleOrderLinesQuery.AVAILABLE_TO_WHOLE_SHIPMENT)) {
        	available_to_whole_shipment = cursor.getInt(SaleOrderLinesQuery.AVAILABLE_TO_WHOLE_SHIPMENT);
        }
        mAvailableToWholeShipment.setChecked(available_to_whole_shipment == 1 ? true : false);
        
        int backorder_status = -1;
		if (!cursor.isNull(SaleOrderLinesQuery.BACKORDER_STATUS)) {
			backorder_status = cursor.getInt(SaleOrderLinesQuery.BACKORDER_STATUS);
			mBackorderStatus.setSelection(backorder_status);
		}
        
		int quote_refused = -1;
		if (!cursor.isNull(SaleOrderLinesQuery.QUOTE_REFUSED_STATUS)) {
			quote_refused = cursor.getInt(SaleOrderLinesQuery.QUOTE_REFUSED_STATUS);
			mQuoteRefused.setSelection(quote_refused);
		}
		
		int capaign_status = -1;
		if (!cursor.isNull(SaleOrderLinesQuery.LINE_CAMPAIGN_STATUS)) {
			capaign_status = cursor.getInt(SaleOrderLinesQuery.LINE_CAMPAIGN_STATUS);
			mCampaignStatus.setSelection(capaign_status);
		}
		
		mSelectedSaleOrderLineId = cursor.getInt(SaleOrderLinesQuery._ID);
		mSelectedSaleOrderId = cursor.getInt(SaleOrderLinesQuery.SALE_ORDER_ID);
        mCallbacks.onSaleOrderLineIdAvailable(String.valueOf(mSelectedSaleOrderLineId));
        
        LogUtils.LOGI(TAG, "Loaded sale order line id: " + String.valueOf(mSelectedSaleOrderLineId));
    }
	
	private interface SaleOrderLinesQuery {
		int _TOKEN = 0x6;

        String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.SaleOrderLines.SALE_ORDER_ID,
                //MobileStoreContract.SaleOrderLines.ITEM_NO,
                //MobileStoreContract.SaleOrderLines.DESCRIPTION,
                MobileStoreContract.SaleOrderLines.LINE_NO,
                MobileStoreContract.SaleOrderLines.QUANTITY,
                MobileStoreContract.SaleOrderLines.QUANTITY_AVAILABLE,
                MobileStoreContract.SaleOrderLines.PRICE,
                MobileStoreContract.SaleOrderLines.PRICE_EUR,
                MobileStoreContract.SaleOrderLines.REAL_DISCOUNT,
                MobileStoreContract.SaleOrderLines.MIN_DISCOUNT,
                MobileStoreContract.SaleOrderLines.MAX_DISCOUNT,
                //MobileStoreContract.SaleOrderLines.CAMPAIGN_CODE,
                MobileStoreContract.SaleOrderLines.BACKORDER_STATUS,
                MobileStoreContract.SaleOrderLines.AVAILABLE_TO_WHOLE_SHIPMENT,
                MobileStoreContract.SaleOrderLines.QUOTE_REFUSED_STATUS,
                MobileStoreContract.SaleOrderLines.VERIFY_STATUS,
                MobileStoreContract.SaleOrderLines.LINE_CAMPAIGN_STATUS,
                MobileStoreContract.SaleOrderLines.PRICE_DISCOUNT_STATUS,
                MobileStoreContract.SaleOrderLines.ITEM_ID
        };

        int _ID = 0;
        int SALE_ORDER_ID = 1;
        //int ITEM_NO = 2;
        //int DESCRIPTION = 3;
        int LINE_NO = 2;
        int QUANTITY = 3;
        int QUANTITY_AVAILABLE = 4;
        int PRICE = 5;
        int PRICE_EUR = 6;
        int REAL_DISCOUNT = 7;
        int MIN_DISCOUNT = 8;
        int MAX_DISCOUNT = 9;
        //int CAMPAIGN_CODE = 12;
        int BACKORDER_STATUS = 10;
        int AVAILABLE_TO_WHOLE_SHIPMENT = 11;
        int QUOTE_REFUSED_STATUS = 12;
        int VERIFY_STATUS = 13;
        int LINE_CAMPAIGN_STATUS = 14;
        int PRICE_DISCOUNT_STATUS = 15;
        int ITEM_ID = 16;
	}
	
	private interface CustomerQuery {
		int _TOKEN = 0x9;

        String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.Customers.CUSTOMER_NO,
        };

        int _ID = 0;
        int CUSTOMER_NO = 1;
	}
	
	private interface SaleOrderQuery {
		int _TOKEN = 0x8;

        String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.SaleOrders.SALES_ORDER_NO,
                MobileStoreContract.SaleOrders.DOCUMENT_TYPE,
                MobileStoreContract.SaleOrders.CUSTOMER_ID
        };

        int _ID = 0;
        int SALES_ORDER_NO = 1;
        int DOCUMENT_TYPE = 2;
        int CUSTOMER_ID = 3;
	}
	
	private interface ItemQuery {
		int _TOKEN = 0x7;

        String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.Items.ITEM_NO,
                MobileStoreContract.Items.CAMPAIGN_STATUS
        };

        int _ID = 0;
        int ITEM_NO = 1;
        int ITEM_CAMPAIGN_STATUS = 2;
	}
	
	private interface SalesPersonQuery {
		//int _TOKEN = 0x10;

        String[] PROJECTION = {
                MobileStoreContract.SalesPerson.SALE_PERSON_NO,
        };

        int SALE_PERSON_NO = 0;
	}
}
