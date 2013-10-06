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

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.SyncLogs;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.ws.model.CustomerSyncObject;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * A base activity that handles common functionality in the app.
 */
public abstract class BaseActivity extends FragmentActivity {
    protected static final Integer ADD_VISIT_REQUEST_CODE = Integer.valueOf(1);
	protected String salesPersonNo;
	protected String salesPersonId;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setup home action bar behavior
        getActionBar().setDisplayHomeAsUpEnabled(true); // from sherlock -> .setHomeButtonEnabled(true);
        salesPersonId = SharedPreferencesUtil.getSalePersonId(this);
        salesPersonNo = SharedPreferencesUtil.getSalePersonNo(this);
        
        checkVersionAndSyncDates();
    }

    /**
     * Checks to prevent data difference between server and client.
     */
    protected void checkVersionAndSyncDates() {
    	Cursor syncLogCustomers = getContentResolver().query(SyncLogs.CONTENT_URI, new String[] { SyncLogs.CREATED_DATE }, SyncLogs.SYNC_OBJECT_ID+"=? and " + SyncLogs.SYNC_OBJECT_STATUS + "=?", new String[] { CustomerSyncObject.TAG, SyncStatus.SUCCESS.toString() }, SyncLogs._ID+" desc limit 1");
    	Cursor syncLogItemsAction = getContentResolver().query(SyncLogs.CONTENT_URI, new String[] { SyncLogs.CREATED_DATE }, SyncLogs.SYNC_OBJECT_ID+" like ? and " + SyncLogs.SYNC_OBJECT_STATUS + "=?", new String[] { "Items%", SyncStatus.SUCCESS.toString() }, SyncLogs._ID+" desc limit 1");
 	
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
    	
    	if (syncLogItemsAction.moveToFirst()) {
			String createdDateFromDb = syncLogItemsAction.getString(0);
			Date createdDate = DateUtils.getLocalDbDate(createdDateFromDb);
			long days = DateUtils.getDateDiff(createdDate, new Date(), TimeUnit.DAYS);
			if (days >= 1) {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_warn), "Artikli nisu sinhronizovani '"+days+"' dan/a");			
			}
		}
    	syncLogItemsAction.close();
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
                NavUtils.navigateUpFromSameTask(this);
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
    
    /**
     * Should be abstract but need to implement empty in so many classes.
     * It will be done eventually.
     * @param code
     * @param result
     */
    public void onSOAPResult(int code, String result) {
    }
}
