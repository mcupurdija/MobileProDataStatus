package rs.gopro.mobile_store.ui;

import java.util.Calendar;
import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.customlayout.ShowHideMasterLayout;
import rs.gopro.mobile_store.ui.dialog.EditDepartureVisitDialog.EditDepartureVisitDialogListener;
import rs.gopro.mobile_store.ui.dialog.EditFieldDialog;
import rs.gopro.mobile_store.ui.dialog.EditFieldDialog.EditNameDialogListener;
import rs.gopro.mobile_store.ui.fragment.RecordVisitDetailFragment;
import rs.gopro.mobile_store.ui.fragment.RecordVisitsListFragment;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.PlannedVisitsToCustomersSyncObject;
import rs.gopro.mobile_store.ws.model.SetPlannedVisitsToCustomersSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;

public class RecordVisitsMultipaneActivity extends BaseActivity implements
		RecordVisitsListFragment.Callbacks, RecordVisitDetailFragment.Callbacks, EditNameDialogListener, EditDepartureVisitDialogListener {

	private static final String TAG = "RecordVisitsMultipaneActivity";
	public static final String RECORD_VISITS_INTENT = "rs.gopro.mobile_store.intent.action.RECORD_VISITS";
	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";
	private static final int VISIT_FILTER_DATE_PICKER = 1;
	private static final String VISITS_DATE_FILTER = "DATE("+Tables.VISITS+"."+MobileStoreContract.Visits.VISIT_DATE+")=DATE(?)";
	private static final String VISITS_RESULT_FILTER = Tables.VISITS+"."+MobileStoreContract.Visits.VISIT_RESULT+"=?";
	
	private static final int VISITS_RESULT_START_DAY = 0;
	private static final int VISITS_RESULT_END_DAY = 4;
	private static final int VISITS_RESULT_BACK_HOME = 5;
	private static final int VISITS_RESULT_BREAK = 3;
	
	public static final int RECORD_VISIT_ARRIVAL = 0;
	public static final int RECORD_VISIT_FIXED_ACTIVITIES = 1;
	
	private ActionMode actionMod;

	private Uri mVisitListUri;
	private String visitDateFilter;
	private Button filterVisitDateButton;
	private int currentVisitResult = -1;
	
	
	private Fragment visitsPlanFragmentDetail;
	private ShowHideMasterLayout mShowHideMasterLayout;
	
	private OnDateSetListener visitFilterDateSetListener;

	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent
					.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
			// itemLoadProgressDialog.dismiss();
		}
	};

	protected void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (SetPlannedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION
					.equalsIgnoreCase(broadcastAction)) {
				// TODO Auto-generated method stub
			} else if (PlannedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION
					.equalsIgnoreCase(broadcastAction)) {
				// TODO Auto-generated method stub
			}
		} else {
			DialogUtil.showInfoDialog(RecordVisitsMultipaneActivity.this,getResources().getString(R.string.dialog_title_error_in_sync),syncResult.getResult());
		}
	}

	public RecordVisitsMultipaneActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_visits);
		final FragmentManager fm = getSupportFragmentManager();

		visitFilterDateSetListener = new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				visitDateFilter = DateUtils.formatDbDate(calendar.getTime());
				filterVisitDateButton.setText(DateUtils.toUIDate(calendar
						.getTime()));
				loadVisitList(mVisitListUri, null, visitDateFilter);
			}
		};

		filterVisitDateButton = (Button) findViewById(R.id.date_filter_button);

		filterVisitDateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
					mVisitListUri = (Uri) intent
							.getParcelableExtra(EXTRA_MASTER_URI);
					loadVisitList(mVisitListUri,
							MobileStoreContract.Visits.getVisitId(uri), rs.gopro.mobile_store.util.DateUtils.toDbDate(new Date()));
					//TODO change this set to one method
					filterVisitDateButton.setText(DateUtils.toUIDate(new Date()));
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void loadVisitList(Uri visitsUri, String selectVisitId,
			String dateFilter) {
		RecordVisitsListFragment fragment = new RecordVisitsListFragment();
		Intent listIntent = new Intent(Intent.ACTION_VIEW, visitsUri);
		if (dateFilter != null) {
			listIntent
					.putExtra(RecordVisitsListFragment.EXTRA_DATE_FILTER, dateFilter);
		}
		fragment.setSelectedVisitId(selectVisitId);
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(listIntent));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_visitsplan_list, fragment).commit();

	}

	private void loadVisitDetail(Uri visitUri) {
		RecordVisitDetailFragment fragment = new RecordVisitDetailFragment();
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
	public void onVisitIdAvailable(String visitId) {

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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {	
		Calendar calendar = Calendar.getInstance();
		switch (id) {
		case VISIT_FILTER_DATE_PICKER:
			DatePickerDialog visitFilterDatePicker = new DatePickerDialog(this, visitFilterDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			return visitFilterDatePicker;
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater menuInflater = getMenuInflater();
	    menuInflater.inflate(R.menu.record_visit_multipane_menu, menu);
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
		case R.id.day_start_record_visit:
			currentVisitResult = VISITS_RESULT_START_DAY;
			if (!checkForRecordedVisit(currentVisitResult)) {
				showDialog();
			} else {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Početak dana je zabeležen!");
			}
			return true;
        case R.id.day_end_record_visit:
        	currentVisitResult = VISITS_RESULT_END_DAY;
        	if (!checkForRecordedVisit(currentVisitResult)) {
        		showDialog();
			} else {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Kraj dana je zabeležen!");
			}
        	return true;
        case R.id.day_end_back_home_visit:
        	currentVisitResult = VISITS_RESULT_BACK_HOME;
        	if (!checkForRecordedVisit(currentVisitResult)) {
        		showDialog();
			} else {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Dolazak kući je zabeležen!");
			}
        	return true;
        case R.id.day_break_visit:
        	currentVisitResult = VISITS_RESULT_BREAK;
        	if (!checkForRecordedVisit(currentVisitResult)) {
        		showDialog();
			} else {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Odmor je zabeležen!");
			}
        	return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private boolean checkForRecordedVisit(int visitSubType) {
		Cursor cursor = getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, new String[] { MobileStoreContract.Visits._ID }, VISITS_DATE_FILTER + " and " + VISITS_RESULT_FILTER, new String[] { rs.gopro.mobile_store.util.DateUtils.toDbDate(new Date()), String.valueOf(visitSubType) }, null);
		
		if (cursor.moveToFirst()) {
			return true;
		}
		
		return false;
	}

	private boolean recordVisit(int visitSubType, int odometer) {
		ContentValues cv = new ContentValues();
		
		cv.putNull(MobileStoreContract.Visits.CUSTOMER_ID);
		if (odometer == -1) {
			cv.putNull(MobileStoreContract.Visits.ODOMETER);
		} else {
			cv.put(MobileStoreContract.Visits.ODOMETER, odometer);
		}
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, visitSubType);
		// break will finish in detail fragment
		if (visitSubType == VISITS_RESULT_BREAK) {
			cv.put(MobileStoreContract.Visits.VISIT_TYPE, 0);
		} else {
			cv.put(MobileStoreContract.Visits.VISIT_TYPE, 1);
		}
		Date newDate = new Date();
		cv.put(Visits.VISIT_DATE, DateUtils.toDbDate(newDate));
		cv.put(Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		// do not log departure on break
		if (visitSubType != VISITS_RESULT_BREAK) {
			cv.put(Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		}
		getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
		
		return true;
	}
	
	private void showDialog() {
		EditFieldDialog dialog = new EditFieldDialog(RecordVisitsMultipaneActivity.RECORD_VISIT_FIXED_ACTIVITIES, "Realizacija", "Unesite kilometražu", InputType.TYPE_CLASS_NUMBER);
    	dialog.show(getSupportFragmentManager(), "FIXED_RECORD_DIALOG");
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

	@Override
	public void onFinishEditDialog(int dialogId, String inputText) {
		switch (dialogId) {
		case RECORD_VISIT_ARRIVAL:
			RecordVisitDetailFragment detailFragment = null;
			if (visitsPlanFragmentDetail != null) {
				detailFragment = (RecordVisitDetailFragment)visitsPlanFragmentDetail;
				if (!detailFragment.checkForRecordedVisit()) {
					detailFragment.recordStartVisit(Integer.valueOf(inputText));
				} else {
					DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Pocetak posete je vec zabelezen!");
				}
			}
			break;
		case RECORD_VISIT_FIXED_ACTIVITIES:
			recordVisit(currentVisitResult, Integer.valueOf(inputText));
			break;
		default:
			LogUtils.LOGE(TAG, "Dialog finished with not implemented handler!");
			LogUtils.LOGE(TAG, String.valueOf(dialogId));
			break;
		}
		
	}

	@Override
	public void onFinishEditDepartureVisitDialog(int id, int visitResult, String note) {
		RecordVisitDetailFragment detailFragment = null;
		if (visitsPlanFragmentDetail != null) {
			detailFragment = (RecordVisitDetailFragment)visitsPlanFragmentDetail;
			if (detailFragment.checkNotForRecordedVisit()) {
				detailFragment.recordEndVisit(visitResult, note);
			} else {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Kraj posete je vec zabelezen!");
			}
		}
	}
}
