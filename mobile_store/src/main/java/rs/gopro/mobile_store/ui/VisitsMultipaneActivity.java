package rs.gopro.mobile_store.ui;

import java.util.Calendar;
import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.customlayout.ShowHideMasterLayout;
import rs.gopro.mobile_store.ui.widget.VisitContextualMenu;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.PlannedVisitsToCustomersSyncObject;
import rs.gopro.mobile_store.ws.model.PlannedVisitsToCustomersSyncObjectOut;
import rs.gopro.mobile_store.ws.model.SetPlannedVisitsToCustomersSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
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
import android.widget.DatePicker;

public class VisitsMultipaneActivity extends BaseActivity implements
		VisitListFragment.Callbacks, VisitDetailFragment.Callbacks {

	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";
	private static final int VISIT_SYNC_OUT_DATE_PICKER = 1;
	private static final int VISIT_SYNC_IN_DATE_PICKER = 2;
	private static final int VISIT_FILTER_DATE_PICKER = 3;
	
	ActionMode actionMod;
	
	private Uri mVisitListUri;
	private String visitDateFilter;
	private Button filterVisitDateButton;
	
	private Fragment visitsPlanFragmentDetail;
	private ShowHideMasterLayout mShowHideMasterLayout;
//	private String selectedVisitId;
	private OnDateSetListener visitSyncSendDateSetListener;
	private OnDateSetListener visitSyncReceiveDateSetListener;
	private OnDateSetListener visitFilterDateSetListener;

	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
//			itemLoadProgressDialog.dismiss();
		}
	};
	
	protected void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (SetPlannedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				// TODO Auto-generated method stub
			} else if (PlannedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				// TODO Auto-generated method stub
			}
		} else {
			AlertDialog alertDialog = new AlertDialog.Builder(
					VisitsMultipaneActivity.this).create();

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

		setContentView(R.layout.activity_visits);
		final FragmentManager fm = getSupportFragmentManager();
		
		visitSyncSendDateSetListener = new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				sendForSelectedDate(year, monthOfYear, dayOfMonth);
			}
		};
		
		visitSyncReceiveDateSetListener = new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				receiveFromSelectedDate(year, monthOfYear, dayOfMonth);
			}
		};
		
		visitFilterDateSetListener = new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year);calendar.set(Calendar.MONTH, monthOfYear);calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				visitDateFilter = DateUtils.formatDbDate(calendar.getTime());
				filterVisitDateButton.setText(DateUtils.toUIDate(calendar.getTime()));
				loadVisitList(mVisitListUri, null, visitDateFilter);
			}
		};
		
		filterVisitDateButton = (Button) findViewById(R.id.date_filter_button);
		
		filterVisitDateButton.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(VISIT_FILTER_DATE_PICKER);
				
			}
		});
		
		// something about swipe at portrait orientation
		mShowHideMasterLayout = (ShowHideMasterLayout) findViewById(R.id.show_hide_master_layout);
		if (mShowHideMasterLayout != null) {
			mShowHideMasterLayout.setFlingToExposeMasterEnabled(true);
		}
		
		
		
		// routes data from intent that called this activity to business logic
		routeIntent(getIntent(), savedInstanceState != null);

		if (savedInstanceState != null) {
			// @TODO needs to handle this

			visitsPlanFragmentDetail = fm
					.findFragmentById(R.id.fragment_visitsplan_detail);
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

		if (MobileStoreContract.Visits.CONTENT_TYPE.equals(mimeType)) {
			// Load a session list, hiding the tracks dropdown and the tabs
			if (!updateSurfaceOnly) {
				mVisitListUri = uri;
				loadVisitList(mVisitListUri, null, null);
				if (mShowHideMasterLayout != null) {
					mShowHideMasterLayout.showMaster(true,
							ShowHideMasterLayout.FLAG_IMMEDIATE);
				}
			}

		} else if (MobileStoreContract.Visits.CONTENT_ITEM_TYPE
				.equals(mimeType)) {
			// Load session details
			if (intent.hasExtra(EXTRA_MASTER_URI)) {
				if (!updateSurfaceOnly) {
					mVisitListUri = (Uri) intent.getParcelableExtra(EXTRA_MASTER_URI);
					loadVisitList(mVisitListUri,
							MobileStoreContract.Visits.getVisitId(uri), null);
					loadVisitDetail(uri);
				}
			} else {
				if (!updateSurfaceOnly) {
					loadVisitDetail(uri);
				}
			}
		}

		updateDetailBackground();
	}

	private void updateDetailBackground() {
		if (visitsPlanFragmentDetail == null) {
			findViewById(R.id.fragment_visitsplan_detail)
					.setBackgroundResource(
							R.drawable.grey_frame_on_white_empty_sandbox);
		} else {
			findViewById(R.id.fragment_visitsplan_detail)
					.setBackgroundResource(R.drawable.grey_frame_on_white);
		}
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
		case R.id.sync_visits:
//			doPlannedVisitSync();
			showDialog(VISIT_SYNC_IN_DATE_PICKER);
			return true;
        case R.id.create_insert_visit_activity: 
        	Intent newVisitIntent = new Intent(getApplicationContext(), AddVisitActivity.class);
        	startActivityForResult(newVisitIntent, ADD_VISIT_REQUEST_CODE);
        	return true;
        case R.id.menu_option_send_visit:
//        	SetPlannedVisitsToCustomersSyncObject plannedVisitsToCustomersSyncObject = new SetPlannedVisitsToCustomersSyncObject(Integer.valueOf(selectedVisitId));
//        	Intent intent = new Intent(this, NavisionSyncService.class);
//        	intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, plannedVisitsToCustomersSyncObject);
//			startService(intent);
        	showDialog(VISIT_SYNC_OUT_DATE_PICKER);
        	return true;
		/* case R.id.newrecord:
             final Intent intent = new Intent(this, NewVisitActivity.class);
             startActivity(intent);
             break;*/
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void loadVisitList(Uri visitsUri, String selectVisitId, String dateFilter) {
		VisitListFragment fragment = new VisitListFragment();
		Intent listIntent = new Intent(Intent.ACTION_VIEW,
				visitsUri);
		if (dateFilter != null) {
			listIntent.putExtra(VisitListFragment.EXTRA_DATE_FILTER, dateFilter);
		}
		fragment.setSelectedVisitId(selectVisitId);
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(listIntent));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_visitsplan_list, fragment).commit();
		
	}

	private void loadVisitDetail(Uri visitUri) {
		VisitDetailFragment fragment = new VisitDetailFragment();
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						visitUri)));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_visitsplan_detail, fragment).commit();
		visitsPlanFragmentDetail = fragment;
		updateDetailBackground();

		// If loading session details in portrait, hide the master pane
		if (mShowHideMasterLayout != null) {
			mShowHideMasterLayout.showMaster(false, 0);
		}
	}

	@Override
	public boolean onVisitSelected(String visitId) {
		//close action mode if user selected other item
		if(actionMod != null){
			actionMod.finish();
		}
		loadVisitDetail(MobileStoreContract.Visits.buildVisitUri(visitId));
//		selectedVisitId = visitId;
		return true;
	}
	
	
	@Override
	public void onVisitLongClick(String visitId, String visitType) {
		VisitContextualMenu	contextualMenu = new VisitContextualMenu(this, visitId, visitType);
	  	actionMod = startActionMode(contextualMenu);
	}  

	@Override
	public void onVisitIdAvailable(String visitId) {
//		selectedVisitId = visitId;
	}
	
	
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == ADD_VISIT_REQUEST_CODE.intValue()){
			if(resultCode == RESULT_OK){
				String visitId = data.getStringExtra(AddVisitActivity.VISIT_ID);
				loadVisitDetail(MobileStoreContract.Visits.buildVisitUri(visitId));
			}
		}
	}*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater menuInflater = getMenuInflater();
	    menuInflater.inflate(R.menu.visits_multipane_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
//	private void doPlannedVisitSync() {
//		Intent intent = new Intent(this, NavisionSyncService.class);
//		PlannedVisitsToCustomersSyncObject plannedVisitsToCustomersSyncObject = new PlannedVisitsToCustomersSyncObjectOut("", salesPersonNo, DateUtils.getWsDummyDate(), DateUtils.getWsDummyDate(), "", 0, 0);
//		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, plannedVisitsToCustomersSyncObject);
//		startService(intent);
//	}
	
	@Override
	protected Dialog onCreateDialog(int id) {	
		Calendar calendar = Calendar.getInstance();
		switch (id) {
		case VISIT_SYNC_OUT_DATE_PICKER:
			DatePickerDialog visitSendDatePicker = new DatePickerDialog(this, visitSyncSendDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			return visitSendDatePicker;
		case VISIT_SYNC_IN_DATE_PICKER:
			DatePickerDialog visitReceiveDatePicker = new DatePickerDialog(this, visitSyncReceiveDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			return visitReceiveDatePicker;
		case VISIT_FILTER_DATE_PICKER:
			DatePickerDialog visitFilterDatePicker = new DatePickerDialog(this, visitFilterDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			return visitFilterDatePicker;
		}
		return super.onCreateDialog(id);
	}
	
	protected void sendForSelectedDate(int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);calendar.set(Calendar.MONTH, monthOfYear);calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		SetPlannedVisitsToCustomersSyncObject visitsToCustomersSyncObject = new SetPlannedVisitsToCustomersSyncObject(calendar.getTime());
		Intent intent = new Intent(this, NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, visitsToCustomersSyncObject);
		startService(intent);
	}
	
	protected void receiveFromSelectedDate(int year, int monthOfYear,
			int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);calendar.set(Calendar.MONTH, monthOfYear);calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		PlannedVisitsToCustomersSyncObject receiveVisitsToCustomersSyncObject = new PlannedVisitsToCustomersSyncObjectOut("", salesPersonNo, calendar.getTime(), new Date(), "", Integer.valueOf(0), Integer.valueOf(-1));
		Intent intent = new Intent(this, NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, receiveVisitsToCustomersSyncObject);
		startService(intent);
	}
	
    @Override
    public void onResume() {
    	super.onResume();
    	IntentFilter plannedVisitsToCustomersSync = new IntentFilter(SetPlannedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, plannedVisitsToCustomersSync);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
    }
}
