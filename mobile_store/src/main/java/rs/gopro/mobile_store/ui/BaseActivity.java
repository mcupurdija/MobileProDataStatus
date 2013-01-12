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

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import android.content.Intent;
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
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setup home action bar behavior
        getActionBar().setDisplayHomeAsUpEnabled(true); // from sherlock -> .setHomeButtonEnabled(true);
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
            case R.id.create_insert_visit_activity: 
            	Intent newVisitIntent = new Intent(getApplicationContext(), AddVisitActivity.class);
            	startActivityForResult(newVisitIntent, ADD_VISIT_REQUEST_CODE);
            	return true;
            case R.id.new_sale_order_action_menu_option:
            	Intent newSaleOrderIntent = new Intent(Intent.ACTION_INSERT, MobileStoreContract.SaleOrders.CONTENT_URI);
            	startActivity(newSaleOrderIntent);
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
