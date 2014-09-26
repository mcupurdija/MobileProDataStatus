package rs.gopro.mobile_store.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerBusinessUnits;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.dialog.AddressSelectDialog;
import rs.gopro.mobile_store.ui.dialog.AddressSelectDialog.AddressSelectDialogListener;
import rs.gopro.mobile_store.ui.dialog.ContactSelectDialog;
import rs.gopro.mobile_store.ui.dialog.ContactSelectDialog.ContactSelectDialogListener;
import rs.gopro.mobile_store.ui.fragment.NovaKKFragment1;
import rs.gopro.mobile_store.ui.fragment.NovaKKFragment2;
import rs.gopro.mobile_store.ui.fragment.NovaKKFragment2.NovaKKFragment2Listener;
import rs.gopro.mobile_store.ui.fragment.NovaKKFragment3;
import rs.gopro.mobile_store.ui.fragment.NovaKKFragment3.NovaKKFragment3Listener;
import rs.gopro.mobile_store.ui.fragment.NovaKKFragment4;
import rs.gopro.mobile_store.ui.fragment.NovaKKFragment4.NovaKKFragment4ContactListener;
import rs.gopro.mobile_store.ui.fragment.NovaKKFragment4.NovaKKFragment4SaleOrderUpdated;
import rs.gopro.mobile_store.ui.fragment.NovaKKFragment4.NovaKKFragment4ShippingAddressListener;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.DocumentUtils;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.PagerSlidingTabStrip;
import rs.gopro.mobile_store.util.VersionUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerActionPlanSyncObject;
import rs.gopro.mobile_store.ws.model.ItemQtySalesPriceAndDiscSyncObject;
import rs.gopro.mobile_store.ws.model.MobileDeviceSalesDocumentSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class NovaKarticaKupcaMasterActivity extends FragmentActivity implements NovaKKFragment2Listener, NovaKKFragment3Listener, NovaKKFragment4ContactListener, ContactSelectDialogListener, NovaKKFragment4ShippingAddressListener, AddressSelectDialogListener, NovaKKFragment4SaleOrderUpdated {

	public static final String TAG = "NovaKKMaster";
	public int sviArtikliUseCount;
	
	private ImageView ivHome;
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	private Cursor cursor;
	
	private int customerId, salesType, saleOrderId;
	private String mAction, customerNo, potentialCustomerNo, businessUnitNo, branchCode, salesPersonNo, appVersion;
	private String[] salesOptions;
	
	private SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	private Date lastActionPlanSyncDate = DateUtils.getWsDummyDate();
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (CustomerActionPlanSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				showToast("Akcioni plan uspešno ažuriran");
			} else if (ItemQtySalesPriceAndDiscSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				ItemQtySalesPriceAndDiscSyncObject syncObject = (ItemQtySalesPriceAndDiscSyncObject) syncResult.getComplexResult();
				
				if (syncObject.getpSubstituteItemNoa46() != null && !syncObject.getpSubstituteItemNoa46().equals("") && !syncObject.getpSubstituteItemNoa46().equals("anyType{}")) {
					DialogUtil.showInfoDialog(this, getString(R.string.dialog_title_sync_info), "Postoji zamenski artikal broj:\n" + syncObject.getpSubstituteItemNoa46());
				}
				if ((syncObject.getpMinimumSalesUnitQuantityTxt().length() > 0 && !syncObject.getpMinimumSalesUnitQuantityTxt().equals("anyType{}")) || (syncObject.getpOutstandingPurchaseLinesTxt().length() > 0) && !syncObject.getpOutstandingPurchaseLinesTxt().equals("anyType{}")) {
				    String outstanding = syncObject.getpOutstandingPurchaseLinesTxt().equals("anyType{}") ? "" : "Poruka: " + syncObject.getpOutstandingPurchaseLinesTxt().replace("\\n", "\n");
				    String minimum = syncObject.getpMinimumSalesUnitQuantityTxt().equals("anyType{}") ? "" : syncObject.getpMinimumSalesUnitQuantityTxt().replace("\\n", "\n");
				    DialogUtil.showInfoDialog(this, getString(R.string.dialog_title_sync_info), minimum + "\n" + outstanding);
				}
			}
		} else {
			DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_error_in_sync), syncResult.getResult());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_nova_kartica_kupca_master);
		mAction = getIntent().getAction();
		
		salesOptions = getResources().getStringArray(R.array.slc1_array);
		
		Bundle intentBundle = getIntent().getExtras();
		customerId = intentBundle.getInt("customerId");
		customerNo = intentBundle.getString("customerNo");
		potentialCustomerNo = intentBundle.getString("potentialCustomerNo");
		branchCode = intentBundle.getString("branchCode");
		businessUnitNo = intentBundle.getString("businessUnitNo");
		salesType = intentBundle.getInt("salesType");
		salesPersonNo = intentBundle.getString("salesPersonNo");
		sviArtikliUseCount = intentBundle.getInt("sviArtikliUseCount");

		ivHome = (ImageView) findViewById(R.id.ivHome);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getSupportFragmentManager());

		pager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
		tabs.setIndicatorColor(0xFFC74B46);
		
		ivHome.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//getContentResolver().delete(SaleOrders.CONTENT_URI, SaleOrders._ID + "=?", new String[] { String.valueOf(saleOrderId) });
				finish();
			}
		});
		
		if (Intent.ACTION_INSERT.equals(mAction)) {
			saleOrderId = createNewSaleOrder();
		} else if (Intent.ACTION_EDIT.equals(mAction)) {
			saleOrderId = getIntent().getIntExtra("saleOrderId", -1);
			ContentValues cv = new ContentValues();
			if (businessUnitNo != null) {
				cv.put(SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE, businessUnitNo);
			} else {
				cv.putNull(SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE);
			}
			cv.put(SaleOrders.SHORTCUT_DIMENSION_1_CODE, salesOptions[salesType]);
			getContentResolver().update(SaleOrders.buildSaleOrderUri(String.valueOf(saleOrderId)), cv, null, null);
		} else {
			finish();
		}
		
		getLastActionPlanSyncDate();
		try {
			if (lastActionPlanSyncDate.before(shortDateFormat.parse(shortDateFormat.format(new Date())))) {
				syncActionPlan();
			}
		} catch (Exception e) {
		}
	}
	
	private int createNewSaleOrder() {
		
		ContentValues cv = new ContentValues();
		
		cv.put(SaleOrders.SALES_ORDER_DEVICE_NO, DocumentUtils.generateSaleOrderDeviceNo(salesPersonNo));
		cv.putNull(SaleOrders.SALES_ORDER_NO);
		cv.put(SaleOrders.DOCUMENT_TYPE, 0);
		cv.put(SaleOrders.CUSTOMER_ID, customerId);
		cv.put(SaleOrders.ORDER_DATE, DateUtils.toDbDate(new Date()));
		if (businessUnitNo != null) {
			cv.put(MobileStoreContract.SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE, businessUnitNo);
		} else {
			cv.putNull(MobileStoreContract.SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE);
		}
		
		cv.put(SaleOrders.LOCATION_CODE, getResources().getStringArray(R.array.location_type_array)[0]);
		cv.put(SaleOrders.SHORTCUT_DIMENSION_1_CODE, salesOptions[salesType]);
		cv.put(SaleOrders.BACKORDER_SHIPMENT_STATUS, 0);
		cv.put(SaleOrders.ORDER_CONDITION_STATUS, 0);
		cv.put(SaleOrders.SHIPMENT_METHOD_CODE, getResources().getStringArray(R.array.shipment_method_code_array_values)[0]);
		cv.put(SaleOrders.SALES_PERSON_ID, 1);
		cv.put(SaleOrders.PAYMENT_OPTION, getResources().getStringArray(R.array.payment_type_array)[0]);
		
		Uri uri = getContentResolver().insert(SaleOrders.CONTENT_URI, cv);
		return (int) ContentUris.parseId(uri);
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "DETALJI - " + customerNo, "ARTIKLI", "KORPA", "ZAGLAVLJE" };

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return NovaKKFragment1.newInstance(customerId, customerNo, salesPersonNo, businessUnitNo);
				case 1:
					return NovaKKFragment2.newInstance(saleOrderId, customerId, customerNo, businessUnitNo, branchCode);
				case 2:
					return NovaKKFragment3.newInstance(saleOrderId, customerId, customerNo);
				case 3:
					return NovaKKFragment4.newInstance(saleOrderId, customerId, appVersion);
				default:
					return null;
			}
		}

	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	
    	appVersion = VersionUtils.getVersionName(getApplicationContext());
    	
    	IntentFilter intentFilter = new IntentFilter(CustomerActionPlanSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, intentFilter);
    	IntentFilter itemQtySalesPriceAndDiscSync = new IntentFilter(ItemQtySalesPriceAndDiscSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, itemQtySalesPriceAndDiscSync);
    	IntentFilter mobileDeviceSalesDocumentSync = new IntentFilter(MobileDeviceSalesDocumentSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, mobileDeviceSalesDocumentSync);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
    }

	@Override
	protected void onStop() {

		ContentValues cv = new ContentValues();
		cv.put(SaleOrders.SVI_ARTIKLI_COUNTER, sviArtikliUseCount);
		getContentResolver().update(SaleOrders.buildSaleOrderUri(String.valueOf(saleOrderId)), cv, null, null);
		
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		confirmExit();
	}
    
    private void confirmExit() {
    	final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(R.string.potvrdaIzlaska);
		alertDialog.setMessage("Da li želite da zatvorite porudžbinu?");
	    alertDialog.setIcon(R.drawable.ic_launcher);
	    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "DA", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
	    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NE", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});
	    alertDialog.show();
    }

	public void showToast(String text) {
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);  
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	/**
	 * 
	 * @return true if sale order is sent, otherwise false
	 */
	public boolean isSent() {
		Cursor cursor = getContentResolver().query(MobileStoreContract.SaleOrders.CONTENT_URI, new String[] { SaleOrders.SALES_ORDER_NO }, Tables.SALE_ORDERS + "." + MobileStoreContract.SaleOrders._ID + "=?", new String[] { String.valueOf(saleOrderId) }, null);
		if (cursor.moveToFirst()) {
			if (!cursor.isNull(0)) {
				return true;
			}
		}
		cursor.close();
		return false;
	}
    
    private void getLastActionPlanSyncDate() {
    	
    	if (businessUnitNo != null) {
			cursor = getContentResolver().query(CustomerBusinessUnits.CONTENT_URI, new String[] { CustomerBusinessUnits.LAST_ACTION_PLAN_SYNC_DATE }, CustomerBusinessUnits.CUSTOMER_NO + "=? AND " + CustomerBusinessUnits.UNIT_NO + "=?", new String[] { customerNo, businessUnitNo }, null);
			if (cursor.moveToFirst()) {
				String date = cursor.getString(0);
				if (date != null) {
					lastActionPlanSyncDate = DateUtils.getLocalDbShortDate(date);
				}
			}
		} else {
			cursor = getContentResolver().query(Customers.CONTENT_URI, new String[] { Customers.LAST_ACTION_PLAN_SYNC_DATE }, Customers._ID + "=?", new String[] { String.valueOf(customerId) }, null);
			if (cursor.moveToFirst()) {
				String date = cursor.getString(0);
				if (date != null) {
					lastActionPlanSyncDate = DateUtils.getLocalDbShortDate(date);
				}
			}
		}
		cursor.close();
    }

	private void syncActionPlan() {
		String businessUnit = (businessUnitNo != null) ? businessUnitNo : "";
		Intent intent = new Intent(this, NavisionSyncService.class);
		CustomerActionPlanSyncObject customerActionPlanSyncObject = new CustomerActionPlanSyncObject("", customerNo, businessUnit, salesPersonNo, Integer.valueOf(0));
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, customerActionPlanSyncObject);
		startService(intent);
	}
	
	@Override
	public void onItemAdded() {
		NovaKKFragment3 f3 = (NovaKKFragment3) getSupportFragmentManager().findFragmentByTag(getFragmentTagByPosition(2));
		if (f3 != null) {
			f3.restartLoader();
			f3.updateBasketSum();
		} else {
			LogUtils.LOGE(TAG, "Cant find fragment");
		}
	}
	
	@Override
	public void onBasketUpdated() {
		NovaKKFragment2 f2 = (NovaKKFragment2) getSupportFragmentManager().findFragmentByTag(getFragmentTagByPosition(1));
		if (f2 != null) {
			f2.updateBasketSum();
		} else {
			LogUtils.LOGE(TAG, "Cant find fragment");
		}
	}
	
	@Override
	public void onSelectContact() {
		if (potentialCustomerNo == null) {
			LogUtils.LOGE(TAG, "CUSTOMER NO");
			return;
		}
		ContactSelectDialog addressSelectDialog = new ContactSelectDialog();
		Intent tempIntent = new Intent(Intent.ACTION_VIEW, null);
		tempIntent.putExtra(ContactSelectDialog.EXTRA_POTENTIAL_CUSTOMER_NO, potentialCustomerNo);
		tempIntent.putExtra(ContactSelectDialog.EXTRA_DIALOG_ID, 0);
		addressSelectDialog.setArguments(BaseActivity.intentToFragmentArguments(tempIntent));
		addressSelectDialog.show(getSupportFragmentManager(), "CONTACT_DIALOG");
	}
	
	@Override
	public void onContactSelected(int dialogId, int contact_id, String contact_name, String contact_phone, String contact_email) {
		NovaKKFragment4 f4 = (NovaKKFragment4) getSupportFragmentManager().findFragmentByTag(getFragmentTagByPosition(3));
		if (f4 != null) {
			f4.onContactSelected(contact_id);
		} else {
			LogUtils.LOGE(TAG, "Cant find fragment");
		}
	}
	
	@Override
	public void onSelectShippingAddress() {
		if (customerNo == null) {
			LogUtils.LOGE(TAG, "CUSTOMER NO");
			return;
		}
		AddressSelectDialog addressSelectDialog = new AddressSelectDialog();
		Intent tempIntent = new Intent(Intent.ACTION_VIEW, null);
		tempIntent.putExtra(AddressSelectDialog.EXTRA_CUSTOMER_NO, customerNo);
		tempIntent.putExtra(AddressSelectDialog.EXTRA_DIALOG_ID, 1);
		addressSelectDialog.setArguments(BaseActivity.intentToFragmentArguments(tempIntent));
		addressSelectDialog.show(getSupportFragmentManager(), "ADDRESS_DIALOG_SHIPPING");
	}
	
	@Override
	public void onAddressSelected(int dialogId, int address_id, String address, String address_no, String city, String post_code, String phone_no, String contact) {
		NovaKKFragment4 f4 = (NovaKKFragment4) getSupportFragmentManager().findFragmentByTag(getFragmentTagByPosition(3));
		if (f4 != null) {
			f4.onAddressSelected(address_id);
		} else {
			LogUtils.LOGE(TAG, "Cant find fragment");
		}
	}
	
	@Override
	public void onSaleOrderUpdated() {
		NovaKKFragment3 f3 = (NovaKKFragment3) getSupportFragmentManager().findFragmentByTag(getFragmentTagByPosition(2));
		if (f3 != null) {
			f3.restartLoader();
		} else {
			LogUtils.LOGE(TAG, "Cant find fragment");
		}
	}
	
	private String getFragmentTagByPosition(int fragmentPosition)
	{
	     return "android:switcher:" + R.id.pager + ":" + fragmentPosition;
	}

}
