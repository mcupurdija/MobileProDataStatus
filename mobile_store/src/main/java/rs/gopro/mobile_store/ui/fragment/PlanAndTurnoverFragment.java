package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.util.UIUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.PlanAndTurnoverSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PlanAndTurnoverFragment extends Fragment {
	
	private TextView thisMonthPlan;
	private TextView thisMonthValue;
	
	private TextView lastMonthPlan;
	private TextView lastMonthValue;
	
	private Button syncPlanAndTurnoverButton;
	
	private double this_month_plan = 0.0;
	private double this_month_turnover = 0.0;
	private double last_month_plan = 0.0;
	private double last_month_turnover = 0.0;
	
	protected String salesPersonNo;
	protected String salesPersonId;
	
	private ProgressDialog planAndTurnoverProgressDialog;
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			planAndTurnoverProgressDialog.dismiss();
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	
	public PlanAndTurnoverFragment() {
	}
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (PlanAndTurnoverSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				PlanAndTurnoverSyncObject syncObject = (PlanAndTurnoverSyncObject) syncResult.getComplexResult();
				this_month_plan = UIUtils.getDoubleFromUI(syncObject.getpCurrentMonthPlanAsTxt());
				this_month_turnover = UIUtils.getDoubleFromUI(syncObject.getpCurrentMonthTurnoverAsTxt());
				last_month_plan = UIUtils.getDoubleFromUI(syncObject.getpLastMonthPlanAsTxt());
				last_month_turnover = UIUtils.getDoubleFromUI(syncObject.getpLastMonthTurnoverAsTxt());
				
				thisMonthPlan.setText(UIUtils.formatDoubleForUI(this_month_plan));
				thisMonthValue.setText(UIUtils.formatDoubleForUI(this_month_turnover));
				lastMonthPlan.setText(UIUtils.formatDoubleForUI(last_month_plan));
				lastMonthValue.setText(UIUtils.formatDoubleForUI(last_month_turnover));
			}
		} else {
			AlertDialog alertDialog = new AlertDialog.Builder(
					getActivity()).create();

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		salesPersonId = SharedPreferencesUtil.getSalePersonId(getActivity());
        salesPersonNo = SharedPreferencesUtil.getSalePersonNo(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_plan_and_turnover, container, false);
		
		thisMonthPlan = (TextView) rootView.findViewById(R.id.this_month_plan_value);
		thisMonthValue = (TextView) rootView.findViewById(R.id.this_month_turnover_value);
		lastMonthPlan = (TextView) rootView.findViewById(R.id.last_month_plan_value);
		lastMonthValue = (TextView) rootView.findViewById(R.id.last_month_turnover_value);
		
		thisMonthPlan.setText(UIUtils.formatDoubleForUI(this_month_plan));
		thisMonthValue.setText(UIUtils.formatDoubleForUI(this_month_turnover));
		lastMonthPlan.setText(UIUtils.formatDoubleForUI(last_month_plan));
		lastMonthValue.setText(UIUtils.formatDoubleForUI(last_month_turnover));
		
		syncPlanAndTurnoverButton = (Button) rootView.findViewById(R.id.sync_sales_person_plan_and_turnover_button);
	
		syncPlanAndTurnoverButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), NavisionSyncService.class);
				PlanAndTurnoverSyncObject syncObject = new PlanAndTurnoverSyncObject(salesPersonNo);
				intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, syncObject);
				getActivity().startService(intent);
				planAndTurnoverProgressDialog = ProgressDialog.show(getActivity(), getActivity().getResources().getString(R.string.dialog_title_payment_and_turnover_load), getActivity().getResources().getString(R.string.dialog_body_payment_and_turnover_load), true, true);
			}
		});
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			this_month_plan = savedInstanceState.getDouble("THIS_MONTH_PLAN");
			this_month_turnover = savedInstanceState.getDouble("THIS_MONTH_TURNOVER");
			last_month_plan = savedInstanceState.getDouble("LAST_MONTH_PLAN");
			last_month_turnover = savedInstanceState.getDouble("LAST_MONTH_TURNOVER");
			
			thisMonthPlan.setText(UIUtils.formatDoubleForUI(this_month_plan));
			thisMonthValue.setText(UIUtils.formatDoubleForUI(this_month_turnover));
			lastMonthPlan.setText(UIUtils.formatDoubleForUI(last_month_plan));
			lastMonthValue.setText(UIUtils.formatDoubleForUI(last_month_turnover));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putDouble("THIS_MONTH_PLAN", this_month_plan);
		outState.putDouble("THIS_MONTH_TURNOVER", this_month_turnover);
		outState.putDouble("LAST_MONTH_PLAN", last_month_plan);
		outState.putDouble("LAST_MONTH_TURNOVER", last_month_turnover);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onResume() {
		super.onResume();	
		IntentFilter planAndTurnoverSyncObject = new IntentFilter(PlanAndTurnoverSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, planAndTurnoverSyncObject);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
	}
}
