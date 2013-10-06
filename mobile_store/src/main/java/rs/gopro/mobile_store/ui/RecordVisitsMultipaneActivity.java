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
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SetRealizedVisitsToCustomersSyncObject;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
//	private static final String VISITS_FILTER_IS_DAY_OPEN = Tables.VISITS+"."+MobileStoreContract.Visits.VISIT_RESULT+"="+ApplicationConstants.VISIT_TYPE_START_DAY;
//	private static final String VISITS_FILTER_IS_DAY_CLOSED = Tables.VISITS+"."+MobileStoreContract.Visits.VISIT_RESULT+"="+ApplicationConstants.VISIT_TYPE_END_DAY;
//	private static final String VISITS_FILTER_IS_BACK_HOME = Tables.VISITS+"."+MobileStoreContract.Visits.VISIT_RESULT+"="+ApplicationConstants.VISIT_TYPE_BACK_HOME;
	private static final String VISITS_FILTER_IS_VISIT_OPEN = Tables.VISITS+"."+MobileStoreContract.Visits.VISIT_STATUS+"="+ApplicationConstants.VISIT_STATUS_STARTED;
	
	private static final int VISITS_RESULT_START_DAY = ApplicationConstants.VISIT_TYPE_START_DAY;
	private static final int VISITS_RESULT_END_DAY = ApplicationConstants.VISIT_TYPE_END_DAY;
	private static final int VISITS_RESULT_BACK_HOME = ApplicationConstants.VISIT_TYPE_BACK_HOME;
	private static final int VISITS_RESULT_BREAK = ApplicationConstants.VISIT_TYPE_PAUSE;
	
	public static final int RECORD_VISIT_ARRIVAL = 0;
	public static final int RECORD_VISIT_FIXED_ACTIVITIES = 1;
	
	private ActionMode actionMod;

	private Uri mVisitListUri;
	private String visitDateFilter;
	private Button filterVisitDateButton;
	private int currentVisitResult = -1;
	private String selectedVisitId = null;
	private int dialogId;
	private Fragment planRealizationFragmentDetail;
	private ShowHideMasterLayout mShowHideMasterLayout;
	
	private OnDateSetListener visitFilterDateSetListener;

//	private BroadcastReceiver onNotice = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			SyncResult syncResult = intent
//					.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
//			onSOAPResult(syncResult, intent.getAction());
//			// itemLoadProgressDialog.dismiss();
//		}
//	};
//
//	protected void onSOAPResult(SyncResult syncResult, String broadcastAction) {
//		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
//			if (SetPlannedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION
//					.equalsIgnoreCase(broadcastAction)) {
//				
//			} else if (PlannedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION
//					.equalsIgnoreCase(broadcastAction)) {
//				
//			}
//		} else {
//			DialogUtil.showInfoDialog(RecordVisitsMultipaneActivity.this,getResources().getString(R.string.dialog_title_error_in_sync),syncResult.getResult());
//		}
//	}

	public RecordVisitsMultipaneActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_realization);
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

			planRealizationFragmentDetail = fm
					.findFragmentById(R.id.fragment_realization_detail);
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
		if (planRealizationFragmentDetail == null) {
			findViewById(R.id.fragment_realization_detail)
					.setBackgroundResource(
							R.drawable.grey_frame_on_white_empty_sandbox);
		} else {
			findViewById(R.id.fragment_realization_detail)
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
				.replace(R.id.fragment_realization_list, fragment).commit();

	}

	private void loadVisitDetail(Uri visitUri) {
		RecordVisitDetailFragment fragment = new RecordVisitDetailFragment();
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						visitUri)));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_realization_detail, fragment).commit();
		planRealizationFragmentDetail = fragment;
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
		selectedVisitId = visitId;
		return true;
	}

	@Override
	public void onVisitLongClick(String visitId, String visitType) {
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
			if (isVisitOpen()) {
        		DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Postoji otvorena poseta!");
        		return true;
        	}
			if (!isVisitRecordCreated(currentVisitResult)) {
				dialogId = RecordVisitsMultipaneActivity.RECORD_VISIT_FIXED_ACTIVITIES;
        		showDialog();
			} else {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Početak dana je zabeležen!");
			}
			return true;
        case R.id.day_end_record_visit:
        	currentVisitResult = VISITS_RESULT_END_DAY;
        	if (isVisitOpen()) {
        		DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Postoji otvorena poseta!");
        		return true;
        	}
        	if (!isVisitRecordCreated(currentVisitResult)) {
        		if (isRecordedVisitDayStart()) {
        			dialogId = RecordVisitsMultipaneActivity.RECORD_VISIT_FIXED_ACTIVITIES;
            		showDialog();
        		} else {
        			DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Dan nije započet!");
        		}
			} else {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Kraj dana je zabeležen!");
			}
        	return true;
        case R.id.day_end_back_home_visit:
        	currentVisitResult = VISITS_RESULT_BACK_HOME;
        	if (isVisitOpen()) {
        		DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Postoji otvorena poseta!");
        		return true;
        	}
        	if (!isVisitRecordCreated(currentVisitResult)) {
        		if (isRecordedVisitDayEnd()) {
        			dialogId = RecordVisitsMultipaneActivity.RECORD_VISIT_FIXED_ACTIVITIES;
            		showDialog();
        		} else {
        			DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Dan nije završen!");
        		}
			} else {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Dolazak kući je zabeležen!");
			}
        	return true;
        case R.id.day_break_visit:
        	currentVisitResult = VISITS_RESULT_BREAK;
        	if (isVisitOpen()) {
        		DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Postoji otvorena poseta!");
        		return true;
        	}
//        	if (!checkForRecordedVisit(currentVisitResult)) {
        	if (isRecordedVisitDayStart()) {
        		dialogId = RecordVisitsMultipaneActivity.RECORD_VISIT_ARRIVAL;
        		showDialog();
			} else {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Dan nije započet!");
//				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Odmor je zabeležen!");
			}
        	return true;
        case R.id.new_record_visit:
        	if (isVisitOpen()) {
        		DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Postoji otvorena poseta!");
        		return true;
        	}
        	Intent newRecordVisit = new Intent(this, AddVisitActivity.class);
        	// newRecordVisit.put(AddVisitActivity.VISIT_ID, null);
        	newRecordVisit.putExtra(AddVisitActivity.VISIT_TYPE, "0");
        	startActivity(newRecordVisit);
        	return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private boolean isVisitRecordCreated(int visitSubType) {
		boolean signal = false;
		Cursor cursor = getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, new String[] { MobileStoreContract.Visits._ID }, VISITS_DATE_FILTER + " and " + VISITS_RESULT_FILTER, new String[] { rs.gopro.mobile_store.util.DateUtils.toDbDate(new Date()), String.valueOf(visitSubType) }, null);
		// there is already back home entry!
		if (cursor.moveToFirst()) {
			signal = true;
		}
		cursor.close();
		return signal;
	}
	
	private boolean isVisitOpen() {
		boolean signal = false;
		Cursor cursor = getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, new String[] { MobileStoreContract.Visits._ID }, VISITS_DATE_FILTER + " and " + VISITS_FILTER_IS_VISIT_OPEN, new String[] { rs.gopro.mobile_store.util.DateUtils.toDbDate(new Date()) }, null);
		// there is already back home entry!
		if (cursor.moveToFirst()) {
			signal = true;
		}
		cursor.close();
		return signal;
	}
	
	private boolean isRecordedVisitDayStart() {
		return isVisitRecordCreated(ApplicationConstants.VISIT_TYPE_START_DAY);
	}

	private boolean isRecordedVisitDayEnd() {
		return isVisitRecordCreated(ApplicationConstants.VISIT_TYPE_END_DAY);
	}
	
//	private boolean isRecordedVisitBackHome() {
//		return isRecorderCreated(ApplicationConstants.VISIT_TYPE_BACK_HOME);
//	}
	
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
			cv.put(MobileStoreContract.Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_STARTED);
		} else {
			cv.put(MobileStoreContract.Visits.VISIT_TYPE, 1);
			cv.put(MobileStoreContract.Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_FINISHED);
		}
		Date newDate = new Date();
		cv.put(Visits.VISIT_DATE, DateUtils.toDbDate(newDate));
		cv.put(Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		// do not log departure on break
		if (visitSubType != VISITS_RESULT_BREAK) {
			cv.put(Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		}
		
		cv.put(Visits.SALES_PERSON_ID, salesPersonId);
		
		Uri newRecordUri = getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
		
		String visit_id = Visits.getVisitId(newRecordUri);
		
		sendRecordedVisit(visit_id);
		
		return true;
	}
	
	/**
	 * Shows input dialog. Result is on callback. Method onFinishEditDepartureVisitDialog.
	 */
	private void showDialog() {
		EditFieldDialog dialog = new EditFieldDialog(dialogId, "Realizacija", "Unesite kilometražu", InputType.TYPE_CLASS_NUMBER);
    	dialog.show(getSupportFragmentManager(), "FIXED_RECORD_DIALOG");
	}
	
    @Override
    public void onResume() {
    	super.onResume();
//    	IntentFilter plannedVisitsToCustomersSync = new IntentFilter(SetPlannedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION);
//    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, plannedVisitsToCustomersSync);
    }
    
    @Override
    public void onPause() {
        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
    }

	@Override
	public void onFinishEditDialog(int dialogId, String inputText) {
		switch (dialogId) {
		// here goes arrivals
		case RECORD_VISIT_ARRIVAL:
//			RecordVisitDetailFragment detailFragment = null;
//			if (planRealizationFragmentDetail != null) { // TODO here i went to sleep
//				detailFragment = (RecordVisitDetailFragment)visitsPlanFragmentDetail;
				if (isRecordedVisitDayStart()) {
					if (!isVisitSTarted()) {
						recordStartVisit(currentVisitResult, Integer.valueOf(inputText));
					} else {
						DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Pocetak posete je vec zabelezen!");
					}
				} else {
					DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Dan nije otvoren!");
				}
//			}
			break;
		// fixed activities = activities as shortcuts on action bar!
		case RECORD_VISIT_FIXED_ACTIVITIES:
			recordVisit(currentVisitResult, Integer.valueOf(inputText));
			break;
		default:
			LogUtils.LOGE(TAG, "Dialog finished with not implemented handler!");
			LogUtils.LOGE(TAG, String.valueOf(dialogId));
			break;
		}
		
	}

	/**
	 * Callback method on calling save departure dialog.
	 */
	@Override
	public void onFinishEditDepartureVisitDialog(int id, int visitResult, String note, int visitSubType) {
		// here goes departures
//		RecordVisitDetailFragment detailFragment = null;
//		if (planRealizationFragmentDetail != null) {
//			detailFragment = (RecordVisitDetailFragment)visitsPlanFragmentDetail;
		if (isPlannedVisit()) {
			if (!recordEndVisit(visitResult, note, visitSubType)) {
				LogUtils.LOGE(TAG, "Visit recording failed!");
			}
		} else {
			DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_record_visit), "Kraj posete je vec zabelezen!");
		}
//		}
	}

	/**
	 * Checks if there is already started visit. Cannot be in two places at same time.
	 * @return
	 */
	private boolean isVisitSTarted() {
    	boolean signal = false;
    	Cursor cursor = getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, new String[] { MobileStoreContract.Visits._ID }, MobileStoreContract.Visits.VISIT_STATUS+"=?", new String[] { String.valueOf(ApplicationConstants.VISIT_STATUS_STARTED) }, null);
		if (cursor.moveToFirst()) {
			signal = true;
		}
		cursor.close();
		return signal;
	}

    private boolean isPlannedVisit() {
		boolean signal = false;
    	Cursor cursor = getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, new String[] { MobileStoreContract.Visits._ID }, "visits._id=? and visits.visit_type=?", new String[] { selectedVisitId == null ? "":selectedVisitId, String.valueOf(ApplicationConstants.VISIT_PLANNED) }, null);
		if (cursor.moveToFirst()) {
			signal = true;
		}
		cursor.close();
		return signal;
	}
    
	private boolean recordStartVisit(int visitSubType, int odometer) {
		ContentValues cv = new ContentValues();

		if (visitSubType == VISITS_RESULT_BREAK) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Visits.SALES_PERSON_ID, salesPersonId);
			contentValues.putNull(Visits.CUSTOMER_ID);
			contentValues.put(Visits.POTENTIAL_CUSTOMER, 0);
			contentValues.put(Visits.VISIT_TYPE, ApplicationConstants.VISIT_PLANNED);
			contentValues.put(MobileStoreContract.Visits.IS_SENT, Integer.valueOf(0));
			contentValues.putNull(Visits.ODOMETER);
			contentValues.put(Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_NEW);

			Uri resultedUri = getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, contentValues);
			selectedVisitId = resultedUri.getPathSegments().get(1);
		}
		
		if (odometer == -1) {
			cv.putNull(MobileStoreContract.Visits.ODOMETER);
		} else {
			cv.put(MobileStoreContract.Visits.ODOMETER, odometer);
		}

		Date newDate = new Date();
		cv.put(Visits.VISIT_DATE, DateUtils.toDbDate(newDate));
		cv.put(Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		cv.put(Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_STARTED);
		int result = getContentResolver().update(MobileStoreContract.Visits.CONTENT_URI, cv, "visits._id=?", new String[] { selectedVisitId == null ? "":selectedVisitId });
		if (result == 0) {
			return false;
		}
		return true;
	}
    
	private boolean recordEndVisit(int visit_result, String note, int visitSubType) {
		if (selectedVisitId == null) {
			DialogUtil.showInfoDialog(this, "Upozorenje", "Poseta mora biti izabrana kako bi bila realizovana!\nIzaberite posetu za realizaciju u listi poseta sa leve strane!");
		}
		ContentValues cv = new ContentValues();
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, visit_result);
		cv.put(MobileStoreContract.Visits.ENTRY_SUBTYPE, visitSubType);
		cv.put(MobileStoreContract.Visits.NOTE, note);
		cv.put(MobileStoreContract.Visits.VISIT_TYPE, ApplicationConstants.VISIT_RECORDED);
		cv.put(Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_FINISHED);
		
		Date newDate = new Date();
		cv.put(Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		int result = getContentResolver().update(MobileStoreContract.Visits.CONTENT_URI, cv, "visits._id=?", new String[] { selectedVisitId == null ? "":selectedVisitId });
		if (result == 0) {
			return false;
		} else {
			sendRecordedVisit(selectedVisitId);
		}
		return true;
	}
	
    private void sendRecordedVisit(String selectedVisitId) {
    	int visitId = -1;
    	try {
    		visitId = Integer.valueOf(selectedVisitId == null ? "-1" : selectedVisitId);
    	} catch (NumberFormatException ne) {
    		LogUtils.LOGE(TAG, "", ne);
    		return;
    	}
    	SetRealizedVisitsToCustomersSyncObject visitsToCustomersSyncObject = new SetRealizedVisitsToCustomersSyncObject(visitId);
    	Intent intent = new Intent(this, NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, visitsToCustomersSyncObject);
		startService(intent);
	}
}
