package rs.gopro.mobile_store.ui.fragment;

import static rs.gopro.mobile_store.util.LogUtils.makeLogTag;
import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.ui.components.ItemAutocompleteCursorAdapter;
import rs.gopro.mobile_store.ui.fragment.SaleOrderLinesAddEditPreviewListFragment.Callbacks;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SaleOrderAddEditLineFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = makeLogTag(SaleOrderAddEditLineFragment.class);
	
	private Uri mSaleOrderLinesUri;
	private int mSelectedSaleOrderLineId;
	private int salesPersonId;
	private int documentId;
	private String documentNo;
	private String documentType;
	private int customerId;
	private String customerNo;
	
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
	
	
	
	public interface Callbacks {
		public void onSaleOrderLineIdAvailable(String saleOrderLineId);
	}
	
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onSaleOrderLineIdAvailable(String saleOrderLineId) {} {
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
        mPriceEur = (EditText) rootView.findViewById(R.id.so_line_item_sugg_price_value);
        mPrice.setFocusable(false);
        mPriceEur.setFocusable(false);
        
        mDiscount = (EditText) rootView.findViewById(R.id.so_line_item_discount_value);
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
        
        return rootView;
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		return new CursorLoader(getActivity(), mSaleOrderLinesUri, SaleOrderLinesQuery.PROJECTION, null, null,
                null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		buildUiFromCursor(cursor);
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
        
        mDocumentNo.setText(documentNo);
        mDocumentType.setText(documentType);
        mCustomer.setText(customerNo);
        
        int item_id = -1;
        if (!cursor.isNull(SaleOrderLinesQuery.ITEM_ID)) {
        	item_id = cursor.getInt(SaleOrderLinesQuery.ITEM_ID);
        	Cursor c = getActivity().getContentResolver().query(MobileStoreContract.Items.buildItemUri(String.valueOf(item_id)), new String[] {MobileStoreContract.Items.ITEM_NO, MobileStoreContract.Items.DESCRIPTION}, null, null, null);
        	if (c.moveToFirst()) {
	        	final int codeIndex = c
	    				.getColumnIndexOrThrow(MobileStoreContract.Items.ITEM_NO);
	    		final int nameIndex = c
	    				.getColumnIndexOrThrow(MobileStoreContract.Items.DESCRIPTION);
	    		final String result = c.getString(codeIndex) + " - "
	    				+ c.getString(nameIndex);
	        	mItemAutocomplete.setText(result);
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
			mBackorderStatus.setSelection(capaign_status);
		}
		
		mSelectedSaleOrderLineId = cursor.getInt(SaleOrderLinesQuery._ID);
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
	
}
