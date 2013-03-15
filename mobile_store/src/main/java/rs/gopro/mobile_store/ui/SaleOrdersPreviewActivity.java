package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.customlayout.ShowHideMasterLayout;
import rs.gopro.mobile_store.ui.widget.SaleOrderContextualMenu;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.MobileDeviceSalesDocumentSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SaleOrdersPreviewActivity extends BaseActivity implements
		SaleOrdersPreviewListFragment.Callbacks, SaleOrderLinesPreviewListFragment.Callbacks {

	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";
	private static final int SAVE_SALE_DOC = 0;
	private static final int VERIFY_SALE_DOC = 1;
//	public static final int CALL_INSERT = 1;
//	public static final int CALL_EDIT = 2;
	
	ActionMode actionMod;
	
	private Fragment saleOrderLinesFragment;
	private ShowHideMasterLayout mShowHideMasterLayout;
	private String saleOrderId;
	private ProgressDialog sendSaleOrderProgressDialog;
	
	private String[] orderConditionStatusOptions;
    private String[] financialControlStatusOptions;
    private String[] orderShipmentStatusOptions;
    private String[] orderValueStatusOptions;
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
			sendSaleOrderProgressDialog.dismiss();
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		System.out.println("STATUS IS: " + syncResult.getStatus());
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (MobileDeviceSalesDocumentSyncObject.BROADCAST_SYNC_ACTION
					.equalsIgnoreCase(broadcastAction)) {
				MobileDeviceSalesDocumentSyncObject deviceSalesDocumentSyncObject = (MobileDeviceSalesDocumentSyncObject) syncResult
						.getComplexResult();
				// AlertDialog successAlertDialog = new AlertDialog.Builder(
				// SaleOrdersPreviewActivity.this).create();

				int status1 = Integer.valueOf(deviceSalesDocumentSyncObject.getOrder_condition_status());
				int status2 = Integer.valueOf(deviceSalesDocumentSyncObject.getFinancial_control_status());
				int status3 = Integer.valueOf(deviceSalesDocumentSyncObject.getOrder_status_for_shipment());
				int status4 = Integer.valueOf(deviceSalesDocumentSyncObject.getOrder_value_status());
				
				final Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.dialog_sale_order_send_info);
				dialog.setTitle("Status poslate liferice");
				
				TextView text1 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_condition_status_spinner);
				text1.setText(orderConditionStatusOptions[status1]);
				
				TextView text2 = (TextView) dialog.findViewById(R.id.dialog_sale_order_financial_control_status_text);
				text2.setText(financialControlStatusOptions[status2]);
				
				TextView text3 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_status_for_shipment_text);
				text3.setText(orderShipmentStatusOptions[status3]);
				
				TextView text4 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_value_status_text);
				text4.setText(orderValueStatusOptions[status4]);
				
				Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
				// if button is clicked, close the custom dialog
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	 
				dialog.show();
			}
		} else {
			AlertDialog alertDialog = new AlertDialog.Builder(
					SaleOrdersPreviewActivity.this).create();

		    // Setting Dialog Title
		    alertDialog.setTitle(getResources().getString(R.string.dialog_title_error_in_sync));
		
		    // Setting Dialog Message
		    alertDialog.setMessage(syncResult.getResult());
		
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
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sale_orders);
		final FragmentManager fm = getSupportFragmentManager();

		orderConditionStatusOptions = getResources().getStringArray(R.array.order_condition_status_array);
		financialControlStatusOptions = getResources().getStringArray(R.array.financial_control_status_array);
		orderShipmentStatusOptions = getResources().getStringArray(R.array.order_status_for_shipment_array);
		orderValueStatusOptions = getResources().getStringArray(R.array.order_value_status_array);
		
		// something about swipe at portrait orientation
		mShowHideMasterLayout = (ShowHideMasterLayout) findViewById(R.id.sales_persons_preview_master_layout);
		if (mShowHideMasterLayout != null) {
			mShowHideMasterLayout.setFlingToExposeMasterEnabled(true);
		}

		// routes data from intent that called this activity to business logic
		routeIntent(getIntent(), savedInstanceState != null);

		if (savedInstanceState != null) {
			// @TODO needs to handle this

			saleOrderLinesFragment = fm
					.findFragmentById(R.id.fragment_sale_order_lines_list);
			updateDetailBackground();
		}
	}

	private void routeIntent(Intent intent, boolean updateSurfaceOnly) {
		// get URI from intent
		Uri uri = intent.getData();
		if (uri == null) {
			return;
		}

		if (intent.hasExtra(Intent.EXTRA_TITLE)) {
			setTitle(intent.getStringExtra(Intent.EXTRA_TITLE));
		}

		String mimeType = getContentResolver().getType(uri);

		if (MobileStoreContract.SaleOrders.CONTENT_TYPE.equals(mimeType)) {
			// Load a session list, hiding the tracks dropdown and the tabs
			if (!updateSurfaceOnly) {
				loadSalesOrdersList(uri, null);
				if (mShowHideMasterLayout != null) {
					mShowHideMasterLayout.showMaster(true,
							ShowHideMasterLayout.FLAG_IMMEDIATE);
				}
			}

		} else if (MobileStoreContract.SaleOrderLines.CONTENT_TYPE
				.equals(mimeType)) {
			// Load session details
			if (intent.hasExtra(EXTRA_MASTER_URI)) {
				if (!updateSurfaceOnly) {
					loadSalesOrdersList(
							(Uri) intent.getParcelableExtra(EXTRA_MASTER_URI),
							MobileStoreContract.SaleOrders.getSaleOrderId(uri));
					loadSaleOrderLines(uri);
				}
			} else {
				if (!updateSurfaceOnly) {
					loadSaleOrderLines(uri);
				}
			}
		}

		updateDetailBackground();
	}

	private void updateDetailBackground() {
		if (saleOrderLinesFragment == null) {
			View view = findViewById(R.id.fragment_sale_order_lines_list);
				view.setBackgroundResource(
							R.drawable.grey_frame_on_white_empty_sandbox);
		} else {
			findViewById(R.id.fragment_sale_order_lines_list)
					.setBackgroundResource(R.drawable.grey_frame_on_white);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void loadSalesOrdersList(Uri salesOrdersUri, String selectVisitId) {
		SaleOrdersPreviewListFragment fragment = new SaleOrdersPreviewListFragment();
		fragment.setSelectedSalesOrderId(selectVisitId);
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						salesOrdersUri)));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_sale_orders_list, fragment).commit();
	}

	private void loadSaleOrderLines(Uri visitUri) {
		SaleOrderLinesPreviewListFragment fragment = new SaleOrderLinesPreviewListFragment();
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						visitUri)));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_sale_order_lines_list, fragment).commit();
		saleOrderLinesFragment = fragment;
		updateDetailBackground();

		// If loading session details in portrait, hide the master pane
		if (mShowHideMasterLayout != null) {
			mShowHideMasterLayout.showMaster(false, 0);
		}
	}
	
	@Override
	public boolean onSaleOrderSelected(String saleOrderId) {
		if(actionMod != null){
			actionMod.finish();
		}
		loadSaleOrderLines(MobileStoreContract.SaleOrderLines.buildSaleOrderLinesUri(saleOrderId));
		this.saleOrderId = saleOrderId;
		return true;
	}

	@Override
	public void onSaleOrderIdAvailable(String saleOrderId) {
		this.saleOrderId = saleOrderId;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater menuInflater = getMenuInflater();
	    menuInflater.inflate(R.menu.sale_order_preview_main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mShowHideMasterLayout != null
					&& !mShowHideMasterLayout.isMasterVisible()) {
				// If showing the detail view, pressing Up should show the
				// master pane.
				mShowHideMasterLayout.showMaster(true, 0);
				return true;
			}
			break;
		case R.id.new_sale_order_action_menu_option:
			Intent newSaleOrderIntent = new Intent(Intent.ACTION_INSERT,
					MobileStoreContract.SaleOrders.CONTENT_URI);
//			startActivityForResult(newSaleOrderIntent, CALL_INSERT);
			startActivity(newSaleOrderIntent);
			return true;
		case R.id.edit_lines_sale_order_action_menu_option:
			// TODO sale order lines goes here
			if (saleOrderId == null) {
				return true;
			}
			Intent editSaleOrderIntent = new Intent(Intent.ACTION_EDIT,
					MobileStoreContract.SaleOrderLines.buildSaleOrderLinesUri(saleOrderId));
			startActivity(editSaleOrderIntent);
			return true;
		case R.id.verify_sale_order_action_menu_option:
			Intent verifyintent = new Intent(this, NavisionSyncService.class);
			MobileDeviceSalesDocumentSyncObject verifymobileDeviceSalesDocumentSyncObject = new MobileDeviceSalesDocumentSyncObject(Integer.valueOf(saleOrderId), VERIFY_SALE_DOC);
			//mobileDeviceSalesDocumentSyncObject.setpDocumentNote()
			verifyintent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, verifymobileDeviceSalesDocumentSyncObject);
			startService(verifyintent);
			sendSaleOrderProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.dialog_title_sale_order_verify), getResources().getString(R.string.dialog_body_sale_order_verify), true, true);
			return true;
		case R.id.send_sale_order_action_menu_option:
			// TODO sale order lines goes here
			if (saleOrderId == null) {
				return true;
			}
			Intent intent = new Intent(this, NavisionSyncService.class);
			MobileDeviceSalesDocumentSyncObject mobileDeviceSalesDocumentSyncObject = new MobileDeviceSalesDocumentSyncObject(Integer.valueOf(saleOrderId), SAVE_SALE_DOC);
			//mobileDeviceSalesDocumentSyncObject.setpDocumentNote()
			intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, mobileDeviceSalesDocumentSyncObject);
			startService(intent);
			sendSaleOrderProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.dialog_title_sale_order_send), getResources().getString(R.string.dialog_body_sale_order_send), true);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSaleOrderLongClick(String saleOrderId) {
		SaleOrderContextualMenu	contextualMenu = new SaleOrderContextualMenu(this, saleOrderId);
	  	actionMod = startActionMode(contextualMenu);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter mobileDeviceSalesDocumentSync = new IntentFilter(MobileDeviceSalesDocumentSyncObject.BROADCAST_SYNC_ACTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, mobileDeviceSalesDocumentSync);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == CALL_EDIT || requestCode == CALL_INSERT) {
//			if (requestCode == RESULT_OK) {
//				
//			}
//		}
//	}
}
