/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rs.gopro.mobile_store.ui;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.localytics.android.LocalyticsSession;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.AppSettings;
import rs.gopro.mobile_store.provider.MobileStoreContract.SyncLogs;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.util.VersionUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerSyncObject;
import rs.gopro.mobile_store.ws.model.MobileDeviceSetup;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;

/**
 * A base activity that handles common functionality in the app.
 */
public abstract class BaseActivity extends FragmentActivity {
    protected static final Integer ADD_VISIT_REQUEST_CODE = Integer.valueOf(1);
	protected String salesPersonNo;
	protected String salesPersonId;
	
	public LocalyticsSession localyticsSession;
	
	private BroadcastReceiver onNoticeMain = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPRes(syncResult, intent.getAction());
		}
	};
	
	private void onSOAPRes(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			ContentValues cv = new ContentValues();
	    	cv.put(AppSettings.APP_SYNC_WARNNING_DATE, DateUtils.toDbDate(new Date()));
	    	getContentResolver().update(AppSettings.CONTENT_URI, cv, "_id=1", null);
			
			MobileDeviceSetup mds = (MobileDeviceSetup) syncResult.getComplexResult();
			String appversion = mds.getAppVersion();
			String versionName = "";
			try {
				versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				LogUtils.LOGE("BaseActivity","",e);
			}
			
			if (!versionName.equals(appversion)) {
	    		DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_warn), "Verzija aplikacije " + versionName + " je zastarela! Potrebna verzija je " + appversion);			
	    	}
		}
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setup home action bar behavior
        getActionBar().setDisplayHomeAsUpEnabled(true); // from sherlock -> .setHomeButtonEnabled(true);
        getActionBar().setTitle(getString(R.string.app_name) + " v" + VersionUtils.getVersionName(getApplicationContext()));
        
        salesPersonId = SharedPreferencesUtil.getSalePersonId(this);
        salesPersonNo = SharedPreferencesUtil.getSalePersonNo(this);
        
        checkVersionAndSyncDates();
        
        this.localyticsSession = new LocalyticsSession(this.getApplicationContext());
        this.localyticsSession.open();
        this.localyticsSession.upload();
    }

    /**
     * Checks to prevent data difference between server and client.
     */
    protected void checkVersionAndSyncDates() {
    	String appversion = null;
    	Date appSyncDate = null;
    	Date customerSyncDate = null;
    	Date itemsSyncDate = null;
    	
    	Cursor appSettings = getContentResolver().query(AppSettings.CONTENT_URI, new String[] { AppSettings.APP_VERSION, AppSettings.APP_SYNC_WARNNING_DATE, AppSettings.CUSTOMERS_SYNC_WARNNING_DATE, AppSettings.ITEMS_SYNC_WARNNING_DATE }, "_id=1", null , null);
    	if (appSettings.moveToFirst()) {
    		appversion = appSettings.getString(0);
    		appSyncDate = DateUtils.getLocalDbDate(appSettings.getString(1));
        	customerSyncDate = DateUtils.getLocalDbDate(appSettings.getString(2));
        	itemsSyncDate = DateUtils.getLocalDbDate(appSettings.getString(3));
    	}
    	appSettings.close();
    	
    
    	long customer_days = DateUtils.getDateDiff(customerSyncDate, new Date(), TimeUnit.DAYS);
    	if (customer_days >= 1) {
	    	Cursor syncLogCustomers = getContentResolver().query(SyncLogs.CONTENT_URI, new String[] { SyncLogs.CREATED_DATE }, SyncLogs.SYNC_OBJECT_ID+"=? and " + SyncLogs.SYNC_OBJECT_STATUS + "=?", new String[] { CustomerSyncObject.TAG, SyncStatus.SUCCESS.toString() }, SyncLogs._ID+" desc limit 1");   	
	    	if (syncLogCustomers.moveToFirst()) {
				String createdDateFromDb = syncLogCustomers.getString(0);
				Date createdDate = DateUtils.getLocalDbDate(createdDateFromDb);
				long days = DateUtils.getDateDiff(createdDate, new Date(), TimeUnit.DAYS);
				if (days >= 1) {
					DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_warn), "Kupci nisu sinhronizovani '"+days+"' dan/a");			
				}
			} else {
				//DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_warn), "Kupci nisu sinhronizovani");
			}
	    	syncLogCustomers.close();
	    	
	    	ContentValues cv = new ContentValues();
	    	cv.put(AppSettings.CUSTOMERS_SYNC_WARNNING_DATE, DateUtils.toDbDate(new Date()));
	    	getContentResolver().update(AppSettings.CONTENT_URI, cv, "_id=1", null);
    	}
    	
    	long item_days = DateUtils.getDateDiff(itemsSyncDate, new Date(), TimeUnit.DAYS);
    	if (item_days >= 1) {
	    	Cursor syncLogItemsAction = getContentResolver().query(SyncLogs.CONTENT_URI, new String[] { SyncLogs.CREATED_DATE }, SyncLogs.SYNC_OBJECT_ID+" like ? and " + SyncLogs.SYNC_OBJECT_STATUS + "=?", new String[] { "Items%", SyncStatus.SUCCESS.toString() }, SyncLogs._ID+" desc limit 1");
	    	if (syncLogItemsAction.moveToFirst()) {
				String createdDateFromDb = syncLogItemsAction.getString(0);
				Date createdDate = DateUtils.getLocalDbDate(createdDateFromDb);
				long days = DateUtils.getDateDiff(createdDate, new Date(), TimeUnit.DAYS);
				if (days >= 1) {
					DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_warn), "Artikli nisu sinhronizovani '"+days+"' dan/a");			
				}
			}
	    	syncLogItemsAction.close();
	    	
	    	ContentValues cv = new ContentValues();
	    	cv.put(AppSettings.ITEMS_SYNC_WARNNING_DATE, DateUtils.toDbDate(new Date()));
	    	getContentResolver().update(AppSettings.CONTENT_URI, cv, "_id=1", null);
    	}
    	
    	
    	long app_days = DateUtils.getDateDiff(appSyncDate, new Date(), TimeUnit.DAYS);
    	if (app_days >= 1) {
	    	
			Intent intent = new Intent(this, NavisionSyncService.class);
			MobileDeviceSetup mobileDeviceSetupSyncObject = new MobileDeviceSetup();
			intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, mobileDeviceSetupSyncObject);
			startService(intent);
    	}
	}


	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : // home back, not do it on main
                if (this instanceof MainActivity) {
                    return false;
                }
                if (this instanceof HomeScreenActivity) {
                    return false;
                }
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            case R.id.create_main_activity : // next button
	            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
	            startActivity(intent);
	            return true;
            case R.id.create_settings_activity:
            	Intent settingsIntent = new Intent(getApplicationContext(), MobileStoreSettingsActivity.class);
            	startActivity(settingsIntent);
            	return true;
           
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
     */
    public static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        return intent;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    
    @Override
    protected void onPause() {
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(onNoticeMain);
    	
    	this.localyticsSession.close();
        this.localyticsSession.upload();
    	
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	IntentFilter mobDeviceSetup = new IntentFilter(MobileDeviceSetup.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNoticeMain, mobDeviceSetup);
    	
    	this.localyticsSession.open();
        this.localyticsSession.upload();
    }
    
    /**
     * Should be abstract but need to implement empty in so many classes.
     * It will be done eventually.
     * @param code
     * @param result
     */
    public void onSOAPResult(int code, String result) {
    }
}
