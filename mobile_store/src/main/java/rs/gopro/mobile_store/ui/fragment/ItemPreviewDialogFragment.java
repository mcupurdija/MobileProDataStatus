package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ItemAvailabilitySyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ItemPreviewDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{
	private static final String TAG = LogUtils.makeLogTag(ItemPreviewDialogFragment.class); 
	
//	private static final String STOCK_STATUS = "stock_status";
	private static final String IS_STOCK_SYNC_COMPLETED = "is_stock_sync_completed";
	private static final String IS_STOCK_SYNC_STARTED = "is_stock_sync_started";

	private Uri mItemUri;
//	private String stockStatusValue;
	private boolean isStockSyncCompleted = false;
	private boolean isStockSyncStarted = false;

	private TextView itemNo;
	private TextView description;
//	private FrameLayout stockStatusFrame;
	private TextView unitOfMeasure;
	private TextView categoryCode;
	private TextView groupCode;
	private TextView campaingStatus;
	private TextView overstockStatus;
	private TextView conntectedSpecShipItem;
	private TextView unitSalePriceDin;
	private TextView campaignCode;
	private TextView campaignStartDate;
	private TextView campaingEndDate;
	private TextView inventoryCategory;
	private TextView itemAvailability;

	private String[] inventoryCategoryOptions;
	
//	 private ResultReceiver mReceiver;
	private ItemAvailabilitySyncObject itemAvailabilitySyncObject;
//	private Activity activity;

	private BroadcastReceiver onNotice = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
				if (ItemAvailabilitySyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(intent.getAction())) {
					ItemAvailabilitySyncObject returnValue = (ItemAvailabilitySyncObject) syncResult.getComplexResult();
					if (returnValue.getpAvailableQtyPerLocFilterTxt() != null) {
						String availibilityMessage = returnValue.getpAvailableQtyPerLocFilterTxt().replace("\\n", "\n");
						String[] splitmessage = availibilityMessage.split("\n");
						if (splitmessage.length > 1) {
							availibilityMessage = "";
							for (int i=1;i<splitmessage.length;i++) {
								availibilityMessage +="\n" + splitmessage[i];
							}
						}
						itemAvailability.setText(availibilityMessage);
					}
				}
			} else {
				itemAvailability.setText("-");
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
		mItemUri = intent.getData();
		if (mItemUri == null) {
			return;
		}

		if (savedInstanceState != null) {
//			stockStatusValue = savedInstanceState.getString(STOCK_STATUS);
			isStockSyncCompleted = savedInstanceState.getBoolean(IS_STOCK_SYNC_COMPLETED);
			isStockSyncStarted = savedInstanceState.getBoolean(IS_STOCK_SYNC_STARTED);
		}

		if (!isStockSyncStarted) {
			String itemNo = MobileStoreContract.Items.getItemNo(mItemUri);
			doSynchronization(itemNo);
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
		unitSalePriceDin = (TextView) view.findViewById(R.id.item_unit_sales_price_din_value);
		campaignCode = (TextView) view.findViewById(R.id.item_campaign_code_value);
		campaignStartDate = (TextView) view.findViewById(R.id.item_campaign_start_date_value);
		campaingEndDate = (TextView) view.findViewById(R.id.item_campaign_end_date_value);
		inventoryCategory = (TextView) view.findViewById(R.id.item_inventory_category_value);
		itemAvailability = (TextView) view.findViewById(R.id.item_availability_value);
		
		inventoryCategoryOptions = getResources().getStringArray(R.array.inventory_category_options);
		
//		stockStatusFrame = (FrameLayout) view.findViewById(R.id.item_stock_status_holder);
//		if (!isStockSyncCompleted) {
//			addWaitingLayoutToFrame();
//		} else {
//			addStockStatusToFrame(stockStatusValue);
//		}
		return view;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle(getString(R.string.item_dialog_title));
		return dialog;
	}

	@Override
	public void onResume() {
		super.onResume();

		IntentFilter intentFilter = new IntentFilter(ItemAvailabilitySyncObject.BROADCAST_SYNC_ACTION);
		//registering broadcast receiver to listen BROADCAST_SYNC_ACTION broadcast 
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
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
		String inventory_category = null;
		int inventory_category_option = 0;
		if (!cursor.isNull(ItemsQuery.INVENTORY_ITEM_CATEGORY)) {
			inventory_category = cursor.getString(ItemsQuery.INVENTORY_ITEM_CATEGORY);
			try {
				inventory_category_option = Integer.valueOf(inventory_category);
			} catch (NumberFormatException ne) {
				inventory_category_option = 0;
				LogUtils.LOGE(TAG, "", ne);
			}
		}
		inventoryCategory.setText(inventoryCategoryOptions[inventory_category_option]);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
//		this.activity = activity;
	}

//	private void addStockStatusToFrame(String stockStatus) {
//		isStockSyncCompleted = true;
//		if (activity == null) {
//			return;
//		}
//		stockStatusFrame.removeAllViews();
//		TextView textView = (TextView) activity.getLayoutInflater().inflate(R.layout.text_view_sotck_status, null);
//		textView.setText(stockStatus);
//		stockStatusFrame.addView(textView);
//	}
//
//	private void addWaitingLayoutToFrame() {
//		View waitingSync = activity.getLayoutInflater().inflate(R.layout.content_empty_waiting_sync, null);
//		stockStatusFrame.addView(waitingSync);
//	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
//		outState.putString(STOCK_STATUS, stockStatusValue);
		outState.putBoolean(IS_STOCK_SYNC_COMPLETED, isStockSyncCompleted);
		outState.putBoolean(IS_STOCK_SYNC_STARTED, isStockSyncStarted);
		super.onSaveInstanceState(outState);
	}



	private void doSynchronization(final String itemNo) {
		isStockSyncStarted = true;
		// TODO convert to item no
//		Cursor itemCursor = getActivity().getContentResolver().query(MobileStoreContract.Items.CONTENT_URI, new String[] { Items.ITEM_NO }, 
//				"_ID=?", new String[] { itemId }, null);
//		String item_no = "";
//		if (itemCursor.moveToFirst()) {
//			item_no = itemCursor.getString(0);
			
			itemAvailabilitySyncObject = new ItemAvailabilitySyncObject(itemNo);
			Intent intent = new Intent(getActivity(), NavisionSyncService.class);
			intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemAvailabilitySyncObject);
			getActivity().startService(intent);
//		}
	}

//	public void onSOAPResult(int code, String result, String itemId) {
////		stockStatusValue = result;
////		addStockStatusToFrame(result);
//		itemAvailability.setText(result);
//	}
	
	
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
				Items.CAMPAIGN_END_DATE,
				Items.INVENTORY_ITEM_CATEGORY
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
//		int UNIT_SALES_PRICE_EUR = 10;
		int UNIT_SALES_PRICE_DIN = 11;
		int CAMPAIGN_CODE = 12;
		int CMPAIGN_START_DATE = 13;
		int CAMPAIGN_END_DATE = 14;
		int INVENTORY_ITEM_CATEGORY = 15;
	}
}
