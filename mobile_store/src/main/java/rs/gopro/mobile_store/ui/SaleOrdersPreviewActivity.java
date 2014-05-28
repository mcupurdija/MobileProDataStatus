package rs.gopro.mobile_store.ui;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrderLines;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.ui.customlayout.ShowHideMasterLayout;
import rs.gopro.mobile_store.ui.widget.SaleOrderContextualMenu;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DocumentUtils;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.UIUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.model.HistorySalesDocumentsSyncObject;
import rs.gopro.mobile_store.ws.model.MobileDeviceSalesDocumentSyncObject;
import rs.gopro.mobile_store.ws.model.NewSalesDocumentsSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

public class SaleOrdersPreviewActivity extends BaseActivity implements
		SaleOrdersPreviewListFragment.Callbacks, SaleOrderLinesPreviewListFragment.Callbacks {

	private static final String TAG = "SaleOrdersPreviewActivity";
	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";
	private static final int SAVE_SALE_DOC = 0;
	private static final int VERIFY_SALE_DOC = 1;
//	public static final int CALL_INSERT = 1;
//	public static final int CALL_EDIT = 2;
	private static final int NEW_SALE_ORDER_REQUEST_CODE = 1;
	private static final String SALE_ORDER_LIST_TAG = "SaleOrderListTag";
	private static final int SALE_OFFER = 1;
	private static final int GET_HISTORY_DIALOG = 0;
	private ActionMode actionMod;
	
	private Fragment saleOrderLinesFragment;
	private ShowHideMasterLayout mShowHideMasterLayout;
	private String saleOrderId;
	private ProgressDialog sendSaleOrderProgressDialog;
	
	private String[] orderConditionStatusOptions;
    private String[] financialControlStatusOptions;
    private String[] orderShipmentStatusOptions;
    private String[] orderValueStatusOptions;
    
    private CustomerAutocompleteCursorAdapter customerCursorAdapter;
    private String selectedCustomerNo = "";
	
    private OnDateSetListener getHistoryDateSet = new OnDateSetListener() {
    	public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    		
    		Date startDate = DateUtils.getFirstDayInMonth(monthOfYear, year);
    		Date endDate = DateUtils.getLastDayInMonth(monthOfYear, year);
    		// TODO call web service
    		getHistoryData(startDate, endDate, salesPersonNo);
    	};	
    };
    
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
			sendSaleOrderProgressDialog.dismiss();
		}
	};
	
	private void getHistoryData(Date startDate, Date endDate, String salesPersonNo) {
		
		sendSaleOrderProgressDialog = ProgressDialog.show(this, "Istorija porudžbina", "Istorija se preuzima sa servera", true, true);
		
		HistorySalesDocumentsSyncObject historySalesDocumentsSyncObject = new HistorySalesDocumentsSyncObject(startDate, endDate, salesPersonNo);
		
		Intent serviceIntent = new Intent(this, NavisionSyncService.class);
		serviceIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, historySalesDocumentsSyncObject);
		startService(serviceIntent);
	}
	
	private void preuzmiDokumenteSaServera(Integer tipDokumenta, String kupac) {
		
		Intent intent = new Intent(SaleOrdersPreviewActivity.this, NavisionSyncService.class);
		NewSalesDocumentsSyncObject newSalesDocumentsSyncObject = new NewSalesDocumentsSyncObject(tipDokumenta, kupac, DateUtils.getWsDummyDate(), DateUtils.getLastDayInMonth(11, 2014), salesPersonNo);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, newSalesDocumentsSyncObject);
		startService(intent);
	}
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (MobileDeviceSalesDocumentSyncObject.BROADCAST_SYNC_ACTION
					.equalsIgnoreCase(broadcastAction)) {
				MobileDeviceSalesDocumentSyncObject deviceSalesDocumentSyncObject = (MobileDeviceSalesDocumentSyncObject) syncResult
						.getComplexResult();
				// AlertDialog successAlertDialog = new AlertDialog.Builder(
				// SaleOrdersPreviewActivity.this).create();

				String quantity = MobileStoreContract.SaleOrderLines.QUANTITY;
				String price = MobileStoreContract.SaleOrderLines.PRICE;
				String discount = MobileStoreContract.SaleOrderLines.REAL_DISCOUNT;
				
				String[] projection = new String[] { "sum(" + quantity + "*(" + price + "-("+ price + "*(" + discount + "/100)))" + ")" };
				Cursor cursorSum = getContentResolver().query(MobileStoreContract.SaleOrders.buildSaleOrderSaldo(), projection, Tables.SALE_ORDERS + "." + MobileStoreContract.SaleOrders._ID + "=?", new String[] { saleOrderId }, null);
				
				double saldo = 0;
				
				double pdv = 0;
				
				double total = 0;
				
				if (cursorSum.moveToFirst()) {
					saldo = cursorSum.getDouble(0);
					pdv = ApplicationConstants.VAT*saldo;
					total = saldo+pdv;
				}
				
				if (cursorSum != null && !cursorSum.isClosed()) {
					cursorSum.close();
				}
				
				final Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.dialog_sale_order_send_info);
				dialog.setTitle("Status verifikovane/poslate porudžbine");
				
				TextView text6 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_document_master_status_message);
				String mainStatusMessage = null;
				// do status message only if it is in send mode, if it is verification only do not do it
				Cursor cursorStatus = getContentResolver().query(MobileStoreContract.SaleOrders.CONTENT_URI, new String[] {SaleOrders.DOCUMENT_TYPE, SaleOrders.SALES_ORDER_NO}, Tables.SALE_ORDERS + "." + MobileStoreContract.SaleOrders._ID + "=?", new String[] { saleOrderId }, null);
				if (deviceSalesDocumentSyncObject.getpVerifyOnly() == 0) {
					if (cursorStatus.moveToFirst()) {
						if (cursorStatus.isNull(1)) {
							mainStatusMessage = "Dokument nije uspešno poslat!";
							text6.setTextColor(Color.RED);
						} else {
							mainStatusMessage = "Dokument uspešno poslat! Broj dokumenta je: "+cursorStatus.getString(1);
							Cursor cursorAdHock = getContentResolver().query(MobileStoreContract.SaleOrderLines.CONTENT_URI, new String[] {SaleOrderLines.SALE_ORDER_ID}, Tables.SALE_ORDER_LINES + "." + SaleOrderLines.SALE_ORDER_ID + "=? and " + Tables.SALE_ORDER_LINES + "." + SaleOrderLines.PRICE_DISCOUNT_STATUS+ "=?", new String[] { saleOrderId, ApplicationConstants.PRICE_AND_DISCOUNT_ARE_NOT_OK }, null);
							if (cursorStatus.getInt(0) == 0 && cursorAdHock.moveToFirst()) {
								mainStatusMessage += "\n" +"Ova porudžbina biće pretvorena u Ad-Hoc Porudžbinu zbog cena van cenovnika i poslata na odobrenje!";
							}
							if (cursorAdHock != null && !cursorAdHock.isClosed()) {
								cursorAdHock.close();
							}
							text6.setTextColor(Color.BLACK);
						}
					}

				} else {
					Cursor cursorAdHock = getContentResolver().query(MobileStoreContract.SaleOrderLines.CONTENT_URI, new String[] {SaleOrderLines.SALE_ORDER_ID}, Tables.SALE_ORDER_LINES + "." + SaleOrderLines.SALE_ORDER_ID + "=? and " + Tables.SALE_ORDER_LINES + "." + SaleOrderLines.PRICE_DISCOUNT_STATUS+ "=?", new String[] { saleOrderId, ApplicationConstants.PRICE_AND_DISCOUNT_ARE_NOT_OK }, null);
					if (cursorStatus.moveToFirst() && cursorStatus.getInt(0) == 0 && cursorAdHock.moveToFirst()) {
//						mainStatusMessage = "Ova porudžbina je kandidat za Ad-Hoc Porudžbinu zbog cena van cenovnika i biće poslata na odobrenje!";
					}
					if (cursorAdHock != null && !cursorAdHock.isClosed()) {
						cursorAdHock.close();
					}
					text6.setTextColor(Color.BLACK);
				}
				if (cursorStatus != null && !cursorStatus.isClosed()) {
					cursorStatus.close();
				}
				int status1 = Integer.valueOf(deviceSalesDocumentSyncObject.getOrder_condition_status());
				int status2 = Integer.valueOf(deviceSalesDocumentSyncObject.getFinancial_control_status());
				int status3 = Integer.valueOf(deviceSalesDocumentSyncObject.getOrder_status_for_shipment());
				int status4 = Integer.valueOf(deviceSalesDocumentSyncObject.getOrder_value_status());
				
				text6.setText(mainStatusMessage);
				
				TextView text7 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_document_additional_status_message);
				double minDiff = 0.0;
				try {
					minDiff = Double.valueOf(WsDataFormatEnUsLatin.toDoubleFromWs(deviceSalesDocumentSyncObject.getMin_max_discount_total_amount_difference()));
				} catch (NumberFormatException nme) {
					LogUtils.LOGE(SALE_ORDER_LIST_TAG, "", nme);
				}
				
				if (minDiff < 0.0) {
					text7.setVisibility(View.VISIBLE);
//					text6.setVisibility(View.VISIBLE);
					text7.setText("Korekcija porudžbine: " + UIUtils.formatDoubleForUI(minDiff));
				} else {
					text7.setVisibility(View.GONE);
//					text6.setVisibility(View.GONE);
				}
				
				TextView text1 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_condition_status_spinner);
				text1.setText(orderConditionStatusOptions[status1]);
				
				TextView text2 = (TextView) dialog.findViewById(R.id.dialog_sale_order_financial_control_status_text);
				text2.setText(financialControlStatusOptions[status2]);
				
				TextView text3 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_status_for_shipment_text);
				text3.setText(orderShipmentStatusOptions[status3]);
				
				TextView text4 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_value_status_text);
				text4.setText(orderValueStatusOptions[status4]);
				
				TextView text5 = (TextView) dialog.findViewById(R.id.dialog_sale_order_document_saldo);
				text5.setText(UIUtils.formatDoubleForUI(total));
				
				Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
				// if button is clicked, close the custom dialog
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	 
				dialog.show();
			} else if (HistorySalesDocumentsSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				HistorySalesDocumentsSyncObject historySalesDocumentSyncObject = (HistorySalesDocumentsSyncObject) syncResult
						.getComplexResult();
				
				for (String document_no : historySalesDocumentSyncObject.getDocumentNumbers()) {
					//TODO call web service for items
					LogUtils.LOGI(TAG, document_no);
				}
			} else if (NewSalesDocumentsSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {

				
				
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

	private void loadSalesOrdersList(Uri salesOrdersUri, String selectSaleOrderId) {
		SaleOrdersPreviewListFragment fragment = new SaleOrdersPreviewListFragment();
		fragment.setSelectedSalesOrderId(selectSaleOrderId);
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						salesOrdersUri)));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_sale_orders_list, fragment, SALE_ORDER_LIST_TAG).commit();
	}

	private void loadSaleOrderLines(Uri linesUri) {
		SaleOrderLinesPreviewListFragment fragment = new SaleOrderLinesPreviewListFragment();
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						linesUri)));
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
			startActivityForResult(newSaleOrderIntent, NEW_SALE_ORDER_REQUEST_CODE);
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
			updateOrderDate(saleOrderId);
			MobileDeviceSalesDocumentSyncObject mobileDeviceSalesDocumentSyncObject = new MobileDeviceSalesDocumentSyncObject(Integer.valueOf(saleOrderId), SAVE_SALE_DOC);
			//mobileDeviceSalesDocumentSyncObject.setpDocumentNote()
			intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, mobileDeviceSalesDocumentSyncObject);
			startService(intent);
			sendSaleOrderProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.dialog_title_sale_order_send), getResources().getString(R.string.dialog_body_sale_order_send), true, true);
			return true;
		case R.id.clone_sale_order_menu_option:
			if (saleOrderId == null) {
				return true;
			}
			cloneDocument();
			return true;
		case R.id.get_new_sale_order_menu_option:
			PreuzmiPorudzbineDijalog();
			return true;
		case R.id.get_history_sale_order_menu_option:
			this.showDialog(GET_HISTORY_DIALOG);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	protected void PreuzmiPorudzbineDijalog() {
		final Dialog dialog = new Dialog(this);
		
		dialog.setContentView(R.layout.dialog_preuzmi_porudzbine);
		dialog.setTitle(R.string.get_new_sale_order_menu_option);
		
		AutoCompleteTextView acKupac = (AutoCompleteTextView) dialog.findViewById(R.id.dijalog_preuzmi_porudzbine_kupac_input);
		customerCursorAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		acKupac.setAdapter(customerCursorAdapter);
		
		final Spinner tipDokumenta = (Spinner) dialog.findViewById(R.id.sTipDokumenta);
		ArrayAdapter<CharSequence> arrayTipDokumenta = ArrayAdapter.createFromResource(this, R.array.dijalog_preuzmi_porudzbine, android.R.layout.simple_spinner_item);
		tipDokumenta.setAdapter(arrayTipDokumenta);
		tipDokumenta.setSelection(1);
		tipDokumenta.setEnabled(false);
		
		Button preuzmi = (Button) dialog.findViewById(R.id.dialogButtonPreuzmi);
		
		acKupac.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
				Cursor cursor = (Cursor) customerCursorAdapter.getItem(position);
				selectedCustomerNo = cursor.getString(1);
			}
		});
		
		preuzmi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendSaleOrderProgressDialog = ProgressDialog.show(SaleOrdersPreviewActivity.this, getString(R.string.get_new_sale_order_menu_option), "Dokumenti se preuzimaju sa servera", true, true);
				preuzmiDokumenteSaServera(tipDokumenta.getSelectedItemPosition(), selectedCustomerNo);

				dialog.dismiss();
			}
		});
		
		dialog.show();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case GET_HISTORY_DIALOG:
			Calendar calendar = Calendar.getInstance();
			DatePickerDialog datePickerDialog = new DatePickerDialog(this, getHistoryDateSet, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			try {
		        Field fields[] = datePickerDialog.getClass().getDeclaredFields();
		        for (Field field : fields) {
		        	 if (field.getName().equals("mDatePicker")) {
		        		 field.setAccessible(true);
		        	     DatePicker datePicker = (DatePicker) field.get(datePickerDialog);
		        	     Field datePickerFields[] = field.getType().getDeclaredFields();
		        	     for (Field dField : datePickerFields) {
				            if (dField.getName().equals("mDayPicker") || "mDaySpinner".equals(dField.getName()) || "mCalendarView".equals(dField.getName())) {
				            	dField.setAccessible(true);
				                Object dayPicker = new Object();
				                dayPicker = dField.get(datePicker);
				                ((View) dayPicker).setVisibility(View.GONE);
				            }
		        	     }
		        	 }
		        } 
			} catch (SecurityException e) {
		        LogUtils.LOGE(TAG, "", e);
		    } 
		    catch (IllegalArgumentException e) {
		    	LogUtils.LOGE(TAG, "", e);
		    } 
		    catch (IllegalAccessException e) {
		    	LogUtils.LOGE(TAG, "", e);
		    }
			
			return datePickerDialog;
		}
		return super.onCreateDialog(id);
	}
	
	private boolean updateOrderDate(String saleOrderId2) {
		ContentValues cv = new ContentValues();
		cv.put(SaleOrders.ORDER_DATE, DateUtils.toDbDate(new Date()));
		
		int result = getContentResolver().update(MobileStoreContract.SaleOrders.CONTENT_URI, cv, SaleOrders._ID+"=?", new String[] { saleOrderId2 });
		
		if (result > 0)
			return true;
		else 
			return false;
	}

	private void cloneDocument() {
		ContentValues documentHeaderContentValues = new ContentValues();
		Cursor documentHeaderCursor = getContentResolver().query(MobileStoreContract.SaleOrders.buildSaleOrderClone(), null, Tables.SALE_ORDERS+"._id=?", new String[] { saleOrderId }, null);
		if (documentHeaderCursor.moveToFirst()) {
			DatabaseUtils.cursorRowToContentValues(documentHeaderCursor, documentHeaderContentValues);
			documentHeaderContentValues.put(MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO, DocumentUtils.generateClonedSaleOrderDeviceNo(salesPersonNo));
			// save doc_no to save it in QUOTE_NO field, only if it is sales offer
			Object doc_no = documentHeaderContentValues.get(MobileStoreContract.SaleOrders.SALES_ORDER_NO);
			documentHeaderContentValues.putNull(MobileStoreContract.SaleOrders.SALES_ORDER_NO);
			documentHeaderContentValues.remove(MobileStoreContract.SaleOrders._ID);
			
			Object order_type  = documentHeaderContentValues.get(MobileStoreContract.SaleOrders.DOCUMENT_TYPE);
			if (doc_no != null && order_type != null && (Integer.valueOf((String)order_type)).intValue() == SALE_OFFER) {
				documentHeaderContentValues.put(MobileStoreContract.SaleOrders.QUOTE_NO, (String) doc_no);
			}
			Uri clonedDocumentUri = getContentResolver().insert(MobileStoreContract.SaleOrders.CONTENT_URI, documentHeaderContentValues);
			
			String clonedDocumentId = MobileStoreContract.SaleOrders.getSaleOrderId(clonedDocumentUri);
			if (clonedDocumentId != null) {
				Cursor documentLinesCursor = getContentResolver().query(MobileStoreContract.SaleOrderLines.CONTENT_URI, null, Tables.SALE_ORDER_LINES+"."+SaleOrderLines.SALE_ORDER_ID+"=?", new String[] { saleOrderId }, null);
				if (documentLinesCursor.moveToFirst()) {
					ContentValues documentLineContentValues;
					do {
						documentLineContentValues = new ContentValues();
						DatabaseUtils.cursorRowToContentValues(documentLinesCursor, documentLineContentValues);
						documentLineContentValues.remove(SaleOrderLines._ID);
						documentLineContentValues.put(SaleOrderLines.SALE_ORDER_ID, clonedDocumentId);
						// reset prices
						documentLineContentValues.putNull(SaleOrderLines.PRICE);
						getContentResolver().insert(SaleOrderLines.CONTENT_URI, documentLineContentValues);
					} while(documentLinesCursor.moveToNext());
					documentLinesCursor.close();
				}
			}
		}
		documentHeaderCursor.close();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == NEW_SALE_ORDER_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String saleOrderId = data.getStringExtra("saleOrderId");
				Fragment fragment = getSupportFragmentManager().findFragmentByTag(SALE_ORDER_LIST_TAG);
				if (fragment != null) {
					((SaleOrdersPreviewListFragment) fragment).setSelectedSalesOrderId(saleOrderId);
				}
			}
			if (resultCode == RESULT_CANCELED) {
			}
		}
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
		IntentFilter historyDocumentSync = new IntentFilter(HistorySalesDocumentsSyncObject.BROADCAST_SYNC_ACTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, historyDocumentSync);
		IntentFilter newSalesDocumentSync = new IntentFilter(NewSalesDocumentsSyncObject.BROADCAST_SYNC_ACTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, newSalesDocumentSync);
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
