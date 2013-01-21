package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.MobileStoreContract.ItemsColumns;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.ui.ItemsListFragment;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ItemQuantitySyncObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.provider.BaseColumns;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;	
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ItemPreviewDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{
	
	
	private static final String STOCK_STATUS = "stock_status";
	private static final String IS_STOCK_SYNC_COMPLETED = "is_stock_sync_completed";
	private static final String IS_STOCK_SYNC_STARTED = "is_stock_sync_started";

	private Uri mItemUri;
	private String stockStatusValue;
	private boolean isStockSyncCompleted = false;
	private boolean isStockSyncStarted = false;
	
	private TextView itemNo;
	private TextView description;
	private FrameLayout stockStatusFrame;
	private TextView unitOfMeasure;
	private TextView categoryCode;
	private TextView groupCode;
	private TextView campaingStatus;
	private TextView overstockStatus;
	private TextView conntectedSpecShipItem;
	private TextView unitSalePriceEur;
	private TextView unitSalePriceDin;
	private TextView campaignCode;
	private TextView campaignStartDate;
	private TextView campaingEndDate;
	
	private ResultReceiver mReceiver;
	private ItemQuantitySyncObject itemQuantitySyncObject;
	private Activity activity;
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 final Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
		 mItemUri = intent.getData();
	        if (mItemUri == null) {
	            return;
	        }
	        
	        if(savedInstanceState != null){
	        	stockStatusValue = savedInstanceState.getString(STOCK_STATUS);
	        	isStockSyncCompleted = savedInstanceState.getBoolean(IS_STOCK_SYNC_COMPLETED);
	        	isStockSyncStarted = savedInstanceState.getBoolean(IS_STOCK_SYNC_STARTED);
	        }
	        
	        if(!isStockSyncStarted){
	        	String itemId =	MobileStoreContract.Items.getItemId(mItemUri);
	        	doSynchronization(itemId);
	        }
	     
	}
	
	
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mItemUri == null) {
            return;
        }
        // Start background query to load vendor details
        getLoaderManager().initLoader(0, null, this);
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_fragment_item, container);
		itemNo = (TextView) view.findViewById(R.id.item_no_value);
		description = (TextView) view.findViewById(R.id.item_desc_value);
		unitOfMeasure = (TextView) view.findViewById(R.id.item_unit_of_measure_value);
		categoryCode = (TextView) view.findViewById(R.id.item_category_code_value);
		groupCode = (TextView) view.findViewById(R.id.item_group_code_value);
		campaingStatus = (TextView) view.findViewById(R.id.item_campaign_status_value);
		overstockStatus = (TextView) view.findViewById(R.id.item_overstock_status_value);
		conntectedSpecShipItem = (TextView) view.findViewById(R.id.item_connected_spec_ship_item_value);
		unitSalePriceEur = (TextView) view.findViewById(R.id.item_unit_sales_price_eur_value);
		unitSalePriceDin = (TextView) view.findViewById(R.id.item_unit_sales_price_din_value);
		campaignCode = (TextView) view.findViewById(R.id.item_campaign_code_value);
		campaignStartDate = (TextView) view.findViewById(R.id.item_campaign_start_date_value);
		campaingEndDate = (TextView) view.findViewById(R.id.item_campaign_end_date_value);
		stockStatusFrame = (FrameLayout) view.findViewById(R.id.item_stock_status_holder);
		if(!isStockSyncCompleted){
			addWaitingLayoutToFrame();
		}else{
			addStockStatusToFrame(stockStatusValue);
		}
		return view;
	}

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle(getString(R.string.item_dialog_title));
		return dialog;
	}
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), mItemUri, ItemsQuery.PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		buildUiFromCursor(data);
		
	}


	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public void buildUiFromCursor(Cursor cursor) {
		if (getActivity() == null) {
			return;
		}
		if (!cursor.moveToFirst()) {
			return;
		}
		String itemNoString = cursor.getString(ItemsQuery.ITEM_NO);
		String descString = cursor.getString(ItemsQuery.DESCRIPTION);
		String unitOfMeasureString = cursor.getString(ItemsQuery.UNIT_OF_MEASURE);
		String categoryCodeString = cursor.getString(ItemsQuery.CATEGORY_CODE);
		String groupCodeString = cursor.getString(ItemsQuery.GROUP_CODE);
		String campaignStatusString = cursor.getString(ItemsQuery.CAMPAIGN_STATUS);
		String overstockStatusString = cursor.getString(ItemsQuery.OVERSTOCK_STATUS);
		String connetectedSpecShipItemString = cursor.getString(ItemsQuery.CONNECTED_SPEC_SHIP_ITEM);
		String unitSalesPriceEurString = String.valueOf(cursor.getDouble(ItemsQuery.UNIT_SALES_PRICE_EUR));
		String unitSalesPriceDinString = String.valueOf(cursor.getDouble(ItemsQuery.UNIT_SALES_PRICE_DIN));
		String campaignCodeString = cursor.getString(ItemsQuery.CAMPAIGN_CODE);
		String campaignStartDateString = cursor.getString(ItemsQuery.CMPAIGN_START_DATE);
		String campaignEndDateString = cursor.getString(ItemsQuery.CAMPAIGN_END_DATE);
		itemNo.setText(itemNoString);
		description.setText(descString);
		unitOfMeasure.setText(unitOfMeasureString);
		categoryCode.setText(categoryCodeString);
		groupCode.setText(groupCodeString);
		campaignCode.setText(campaignCodeString);
		campaignStartDate.setText(campaignStartDateString);
		campaingEndDate.setText(campaignEndDateString);
		campaingStatus.setText(campaignStatusString);
		overstockStatus.setText(overstockStatusString);
		conntectedSpecShipItem.setText(connetectedSpecShipItemString);
		unitSalePriceDin.setText(unitSalesPriceDinString);
		unitSalePriceEur.setText(unitSalesPriceEurString);
	}    
	
	/*@Override
	public void stockStatusUpdate(String stockStatus) {
		this.stockStatusValue = stockStatus;
		addStockStatusToFrame(stockStatus);
	}*/

@Override
public void onAttach(Activity activity) {
	super.onAttach(activity);
	this.activity = activity;
}

	private void addStockStatusToFrame(String stockStatus) {
		isStockSyncCompleted = true;
		if(activity == null){
			return;
		}
		stockStatusFrame.removeAllViews();
		TextView textView  = (TextView) activity.getLayoutInflater().inflate(R.layout.text_view_sotck_status, null);
		textView.setText(stockStatus);
		stockStatusFrame.addView(textView);
	}
	
	private void addWaitingLayoutToFrame() {
		View waitingSync =  activity.getLayoutInflater().inflate(R.layout.content_empty_waiting_sync, null);
		stockStatusFrame.addView(waitingSync);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    outState.putString(STOCK_STATUS, stockStatusValue);
	    outState.putBoolean(IS_STOCK_SYNC_COMPLETED, isStockSyncCompleted);
	    outState.putBoolean(IS_STOCK_SYNC_STARTED, isStockSyncStarted);
		super.onSaveInstanceState(outState);
	}

	
	private void doSynchronization(final String itemId) {
		mReceiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultData != null && resultData.containsKey(NavisionSyncService.SOAP_RESULT)) {
                	onSOAPResult(resultCode, resultData.getString(NavisionSyncService.SOAP_RESULT), itemId);
                }
                else {
                	if(activity != null){
                		Toast toast = Toast.makeText(activity, "Stock status is not sync!",Toast.LENGTH_SHORT );
                		toast.show();
                	}
                	onSOAPResult(resultCode, null, itemId);
                }
            }
            
        };
		isStockSyncStarted = true;
        itemQuantitySyncObject = new ItemQuantitySyncObject(itemId, "300", Integer.valueOf(-1));
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemQuantitySyncObject);
		intent.putExtra(NavisionSyncService.EXTRA_RESULT_RECEIVER, getResultReceiver());
		getActivity().startService(intent);
		
	}
	
	public ResultReceiver getResultReceiver() {
        return mReceiver;
    }
	
	
	public void onSOAPResult(int code, String result, String itemId) {
		stockStatusValue = result;
		addStockStatusToFrame(result);
	    }
	
	
	private interface ItemsQuery {
		int _TOKEN = 0x8;
		String[] PROJECTION = { 
				BaseColumns._ID, 
				Items.ITEM_NO,
				Items.DESCRIPTION,
				Items.DESCRIPTION2,
				Items.UNIT_OF_MEASURE,
				Items.CATEGORY_CODE,
				Items.GROUP_CODE,
				Items.CAMPAIGN_STATUS,
				Items.OVERSTOCK_STATUS,
				Items.CONNECTED_SPEC_SHIP_ITEM,
				Items.UNIT_SALES_PRICE_EUR,
				Items.UNIT_SALES_PRICE_DIN,
				Items.CAMPAIGN_CODE,
				Items.CMPAIGN_START_DATE,
				Items.CAMPAIGN_END_DATE
		};
		int _ID = 0;
		int ITEM_NO = 1;
		int DESCRIPTION = 2;
		int DESCRIPTION2 = 3;
		int UNIT_OF_MEASURE = 4;
		int CATEGORY_CODE = 5;
		int GROUP_CODE = 6;
		int CAMPAIGN_STATUS = 7;
		int OVERSTOCK_STATUS = 8;
		int CONNECTED_SPEC_SHIP_ITEM = 9;
		int UNIT_SALES_PRICE_EUR = 10;
		int UNIT_SALES_PRICE_DIN = 11;
		int CAMPAIGN_CODE = 12;
		int CMPAIGN_START_DATE = 13;
		int CAMPAIGN_END_DATE = 14;


	}


	


	

	

}
