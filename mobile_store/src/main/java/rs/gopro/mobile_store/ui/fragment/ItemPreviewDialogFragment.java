package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ItemAvailPerLocationSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
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
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ItemPreviewDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private static final String TAG = LogUtils.makeLogTag(ItemPreviewDialogFragment.class);

	private static final String IS_STOCK_SYNC_COMPLETED = "is_stock_sync_completed";
	private static final String IS_STOCK_SYNC_STARTED = "is_stock_sync_started";

	private Uri mItemUri;
	private boolean isStockSyncCompleted = false;
	private boolean isStockSyncStarted = false;
	
	private boolean allShown = false;

	private TextView itemNo;
	private TextView description;
	private TextView unitOfMeasure;
	private TextView categoryCode;
	private TextView groupCode;
	private TextView itemAvailability;
	private TextView itemAvailabilityLabel;
	private TextView outstandingPurchaseLines;
	
	private ProgressBar pbLoading;

	private ItemAvailPerLocationSyncObject itemAvailabilitySyncObject;

	private BroadcastReceiver onNotice = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			pbLoading.setVisibility(View.GONE);
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
				if (ItemAvailPerLocationSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(intent.getAction())) {
					ItemAvailPerLocationSyncObject returnValue = (ItemAvailPerLocationSyncObject) syncResult.getComplexResult();
					
					if (returnValue.getpAvailQtyTxt() != null && !returnValue.getpAvailQtyTxt().equals("anyType{}")) {
						String availibilityMessage = returnValue.getpAvailQtyTxt().replace("\\n", "\n");
						String[] splitmessage = availibilityMessage.split("\n");
						if (splitmessage.length > 1) {
							availibilityMessage = "";
							for (int i = 0; i < splitmessage.length; i++) {
								availibilityMessage += "\n" + splitmessage[i];
							}
						}
						itemAvailability.setText(availibilityMessage.trim());
					} else {
						itemAvailability.setText("-");
					}
					
					/*
					if (returnValue.getpAvailableQtyPerLocFilterTxt() != null && !returnValue.getpAvailableQtyPerLocFilterTxt().equals("anyType{}")) {
						String availibilityMessage = returnValue.getpAvailableQtyPerLocFilterTxt().replace("\\n", "\n");
						String[] splitmessage = availibilityMessage.split("\n");
						if (splitmessage.length > 1) {
							availibilityMessage = "";
							for (int i = 1; i < splitmessage.length; i++) {
								availibilityMessage += "\n" + splitmessage[i];
							}
						}
						itemAvailability.setText(availibilityMessage);
					} else {
						itemAvailability.setText("-");
					}
					// set outstanding purchase lines
					if (returnValue.getpOutstandingPurchaseLinesTxt() != null && !returnValue.getpOutstandingPurchaseLinesTxt().equals("anyType{}")) {
						String outstandingPurchaseLinesMessage = returnValue.getpOutstandingPurchaseLinesTxt().replace("\\n", "\n");
						String[] splitmessage = outstandingPurchaseLinesMessage.split("\n");
						if (splitmessage.length > 1) {
							outstandingPurchaseLinesMessage = "";
							for (int i = 1; i < splitmessage.length; i++) {
								outstandingPurchaseLinesMessage += "\n" + splitmessage[i];
							}
						}
						outstandingPurchaseLines.setText(outstandingPurchaseLinesMessage);
					} else {
						outstandingPurchaseLines.setText("-");
					}
					*/
				}
			} else {
				itemAvailability.setText("-");
				outstandingPurchaseLines.setText("-");
			}
		}
	};

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
		return inflater.inflate(R.layout.dialog_fragment_item, container);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		final Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
		mItemUri = intent.getData();
		if (mItemUri == null) {
			return;
		}

		if (savedInstanceState != null) {
			isStockSyncCompleted = savedInstanceState.getBoolean(IS_STOCK_SYNC_COMPLETED);
			isStockSyncStarted = savedInstanceState.getBoolean(IS_STOCK_SYNC_STARTED);
		}
		
		itemNo = (TextView) view.findViewById(R.id.item_no_value);
		description = (TextView) view.findViewById(R.id.item_desc_value);
		unitOfMeasure = (TextView) view.findViewById(R.id.item_unit_of_measure_value);
		categoryCode = (TextView) view.findViewById(R.id.item_category_code_value);
		groupCode = (TextView) view.findViewById(R.id.item_group_code_value);
		itemAvailability = (TextView) view.findViewById(R.id.item_availability_value);
		itemAvailabilityLabel = (TextView) view.findViewById(R.id.item_availability_label);
		outstandingPurchaseLines = (TextView) view.findViewById(R.id.item_outstanding_purchase_lines_value);
		pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);
		
		itemAvailability.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				allShown = !allShown;
				if (allShown) {
					doSynchronizationAll(MobileStoreContract.Items.getItemNo(mItemUri));
					setItemAvailabilityLabel(getString(R.string.item_availability_label2));
				} else {
					doSynchronization(MobileStoreContract.Items.getItemNo(mItemUri));
					setItemAvailabilityLabel(getString(R.string.item_availability_label));
				}
			}
		});
		
		itemAvailabilityLabel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				allShown = !allShown;
				if (allShown) {
					doSynchronizationAll(MobileStoreContract.Items.getItemNo(mItemUri));
					setItemAvailabilityLabel(getString(R.string.item_availability_label2));
				} else {
					doSynchronization(MobileStoreContract.Items.getItemNo(mItemUri));
					setItemAvailabilityLabel(getString(R.string.item_availability_label));
				}
			}
		});
		
		if (!isStockSyncStarted) {
			String itemNo = MobileStoreContract.Items.getItemNo(mItemUri);
			doSynchronization(itemNo);
		}
	}

	private void setItemAvailabilityLabel(String text) {
		SpannableString mySpannableString = new SpannableString(text);
		mySpannableString.setSpan(new UnderlineSpan(), 0, mySpannableString.length(), 0);
		itemAvailabilityLabel.setText(mySpannableString);
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

		IntentFilter intentFilter = new IntentFilter(ItemAvailPerLocationSyncObject.BROADCAST_SYNC_ACTION);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), mItemUri, ItemsQuery.PROJECTION,
				null, null, null);
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
		itemNo.setText(itemNoString);
		description.setText(descString);
		unitOfMeasure.setText(unitOfMeasureString);
		categoryCode.setText(categoryCodeString);
		groupCode.setText(groupCodeString);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(IS_STOCK_SYNC_COMPLETED, isStockSyncCompleted);
		outState.putBoolean(IS_STOCK_SYNC_STARTED, isStockSyncStarted);
		super.onSaveInstanceState(outState);
	}

	private void doSynchronization(final String itemNo) {
		isStockSyncStarted = true;

		pbLoading.setVisibility(View.VISIBLE);
		itemAvailabilitySyncObject = new ItemAvailPerLocationSyncObject(itemNo, "", 0, "");
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemAvailabilitySyncObject);
		getActivity().startService(intent);
	}
	
	private void doSynchronizationAll(final String itemNo) {
		isStockSyncStarted = true;

		pbLoading.setVisibility(View.VISIBLE);
		itemAvailabilitySyncObject = new ItemAvailPerLocationSyncObject(itemNo, "", 1, "");
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemAvailabilitySyncObject);
		getActivity().startService(intent);
	}

	private interface ItemsQuery {
		// int _TOKEN = 0x8;

		String[] PROJECTION = { BaseColumns._ID, Items.ITEM_NO,
				Items.DESCRIPTION, Items.UNIT_OF_MEASURE, Items.CATEGORY_CODE,
				Items.GROUP_CODE };

		// int _ID = 0;
		int ITEM_NO = 1;
		int DESCRIPTION = 2;
		int UNIT_OF_MEASURE = 3;
		int CATEGORY_CODE = 4;
		int GROUP_CODE = 5;
	}
}
