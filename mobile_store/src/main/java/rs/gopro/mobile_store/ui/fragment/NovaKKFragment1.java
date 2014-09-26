package rs.gopro.mobile_store.ui.fragment;

import java.util.Date;
import java.util.Locale;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerBusinessUnits;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.ui.NovaKarticaKupcaMasterActivity;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.AssetUtil;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.UIUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ElectronicCardCustomerSyncObject;
import rs.gopro.mobile_store.ws.model.SalespersonUpdateSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NovaKKFragment1 extends Fragment implements LoaderCallbacks<Cursor> {

	private int customerId;
	private String customerNo, salesPersonNo, businessUnitNo;
	
	private NovaKarticaKupcaMasterActivity masterActivity;
	
	private LinearLayout llSync;
	private ProgressBar pbLoading;
	
	private TextView tvImeKupca, tvSifraKupca, tvImeKupca2, tvPoslovnaJedinica, tvAdresaKupca, tvGradKupca, tvPostanskiBrojKupca, 
		tvTelefonKupca, tvMobilniKupca, tvEmailKupca, tvMaticniBrojKupca, tvBrojPrimarnogKontaktaKupca, tvPoreskiBrojKupca, 
		tvKreditniLimitKupca, tvSaldoKupca, tvWRKupcaLabel, tvDospeliSaldoKupca, tvDospeliSaldoIVKupca, tvSifraUslovaPlacanjaKupca, 
		tvPrioritetKupca, tvBransaKupca, tvKanalKupca, tvStatusKupca, tvSMLKupca, tvUsvojeniPotencijalKupca, tvFokusKupca, 
		tvDivizijaKupca, tvBrojPlavihMantilaKupcaKupca, tvBrojSivihMantilaKupcaKupca, tvPromet3Kupca, tvPromet6Kupca, tvPromet12Kupca, 
		tvPrometProslaKupca, tvPrometPre2Kupca, tvPrometPre3Kupca, tvPrometPreYTMKupca, tvUkupanProfitKupca, tvSopstveniPrometKupca;
	private View vBojaKupca1, vBojaKupca2;
	
	public static NovaKKFragment1 newInstance(int customerId, String customerNo, String salesPersonNo, String businessUnitNo) {
		NovaKKFragment1 frag = new NovaKKFragment1();
        Bundle args = new Bundle();
        args.putInt("customerId", customerId);
        args.putString("customerNo", customerNo);
        args.putString("salesPersonNo", salesPersonNo);
        args.putString("businessUnitNo", businessUnitNo);
        frag.setArguments(args);
        return frag;
    }
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (SalespersonUpdateSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				checkForWrData();
			} else if (ElectronicCardCustomerSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				
				ContentValues cv = new ContentValues();
				cv.put(Customers.LAST_ECC_SYNC_DATE, DateUtils.toDbDate(new Date()));
				getActivity().getContentResolver().update(Customers.buildCustomersUri(String.valueOf(customerId)), cv, null, null);
			
				pbLoading.setVisibility(View.GONE);
				masterActivity.showToast("EKK uspešno sinhronizovan");
			}
		} else {
			openWebReportingLoginPage();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_nova_kartica_kupca_1, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		masterActivity = ((NovaKarticaKupcaMasterActivity) getActivity());
		
		customerId = getArguments().getInt("customerId", -1);
		customerNo = getArguments().getString("customerNo", null);
		salesPersonNo = getArguments().getString("salesPersonNo", null);
		businessUnitNo = getArguments().getString("businessUnitNo", null);
		
		llSync = (LinearLayout) view.findViewById(R.id.llSync);
		pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);
		
		tvImeKupca = (TextView) view.findViewById(R.id.tvImeKupca);
		tvImeKupca.setPaintFlags(tvImeKupca.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		tvSifraKupca = (TextView) view.findViewById(R.id.tvSifraKupca);
		tvImeKupca2 = (TextView) view.findViewById(R.id.tvImeKupca2);
		tvPoslovnaJedinica = (TextView) view.findViewById(R.id.tvPoslovnaJedinica);
		tvAdresaKupca = (TextView) view.findViewById(R.id.tvAdresaKupca);
		tvGradKupca = (TextView) view.findViewById(R.id.tvGradKupca);
		tvPostanskiBrojKupca = (TextView) view.findViewById(R.id.tvPostanskiBrojKupca);
		tvTelefonKupca = (TextView) view.findViewById(R.id.tvTelefonKupca);
		tvMobilniKupca = (TextView) view.findViewById(R.id.tvMobilniKupca);
		tvEmailKupca = (TextView) view.findViewById(R.id.tvEmailKupca);
		tvMaticniBrojKupca = (TextView) view.findViewById(R.id.tvMaticniBrojKupca);
		tvBrojPrimarnogKontaktaKupca = (TextView) view.findViewById(R.id.tvBrojPrimarnogKontaktaKupca);
		tvPoreskiBrojKupca = (TextView) view.findViewById(R.id.tvPoreskiBrojKupca);
		tvKreditniLimitKupca = (TextView) view.findViewById(R.id.tvKreditniLimitKupca);
		tvSaldoKupca = (TextView) view.findViewById(R.id.tvSaldoKupca);
		tvWRKupcaLabel = (TextView) view.findViewById(R.id.tvWRKupcaLabel);
		tvDospeliSaldoKupca = (TextView) view.findViewById(R.id.tvDospeliSaldoKupca);
		tvDospeliSaldoIVKupca = (TextView) view.findViewById(R.id.tvDospeliSaldoIVKupca);
		tvSifraUslovaPlacanjaKupca = (TextView) view.findViewById(R.id.tvSifraUslovaPlacanjaKupca);
		tvPrioritetKupca = (TextView) view.findViewById(R.id.tvPrioritetKupca);
		tvBransaKupca = (TextView) view.findViewById(R.id.tvBransaKupca);
		tvKanalKupca = (TextView) view.findViewById(R.id.tvKanalKupca);
		tvStatusKupca = (TextView) view.findViewById(R.id.tvStatusKupca);
		tvSMLKupca = (TextView) view.findViewById(R.id.tvSMLKupca);
		tvUsvojeniPotencijalKupca = (TextView) view.findViewById(R.id.tvUsvojeniPotencijalKupca);
		tvFokusKupca = (TextView) view.findViewById(R.id.tvFokusKupca);
		tvDivizijaKupca = (TextView) view.findViewById(R.id.tvDivizijaKupca);
		tvBrojPlavihMantilaKupcaKupca = (TextView) view.findViewById(R.id.tvBrojPlavihMantilaKupcaKupca);
		tvBrojSivihMantilaKupcaKupca = (TextView) view.findViewById(R.id.tvBrojSivihMantilaKupcaKupca);
		tvPromet3Kupca = (TextView) view.findViewById(R.id.tvPromet3Kupca);
		tvPromet6Kupca = (TextView) view.findViewById(R.id.tvPromet6Kupca);
		tvPromet12Kupca = (TextView) view.findViewById(R.id.tvPromet12Kupca);
		tvPrometProslaKupca = (TextView) view.findViewById(R.id.tvPrometProslaKupca);
		tvPrometPre2Kupca = (TextView) view.findViewById(R.id.tvPrometPre2Kupca);
		tvPrometPre3Kupca = (TextView) view.findViewById(R.id.tvPrometPre3Kupca);
		tvPrometPreYTMKupca = (TextView) view.findViewById(R.id.tvPrometPreYTMKupca);
		tvUkupanProfitKupca = (TextView) view.findViewById(R.id.tvUkupanProfitKupca);
		tvSopstveniPrometKupca = (TextView) view.findViewById(R.id.tvSopstveniPrometKupca);
		vBojaKupca1 = (View) view.findViewById(R.id.vBojaKupca1);
		vBojaKupca2 = (View) view.findViewById(R.id.vBojaKupca2);
		
		llSync.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pbLoading.setVisibility(View.VISIBLE);
				ekkSync();
			}
		});
		
		tvWRKupcaLabel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checkForWrData();
			}
		});
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
    }

    @Override
	public void onResume() {
		super.onResume();
		IntentFilter salesPersonSyncObject = new IntentFilter(SalespersonUpdateSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, salesPersonSyncObject);
    	IntentFilter eccSyncObject = new IntentFilter(ElectronicCardCustomerSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, eccSyncObject);
	}
	
	private void checkForWrData() {
		String wrPassword;
		
		Cursor cursor = getActivity().getContentResolver().query(MobileStoreContract.SalesPerson.CONTENT_URI, SalesPersonQuery.PROJECTION, MobileStoreContract.SalesPersonsColumns.SALE_PERSON_NO + "=?", new String[] { salesPersonNo }, null);
		if (cursor.moveToFirst()) {
			wrPassword = cursor.getString(SalesPersonQuery.WR_PASSWORD);
			if (wrPassword != null) {
				setupPassAndOpenWebReporting(salesPersonNo, wrPassword);
			} else {
				azurirajPodatkeProdavca(salesPersonNo);
			}
		}
    }
	
	private void setupPassAndOpenWebReporting(String salesPersonNo, String wrPassword) {
    	String salt = ApplicationConstants.SALT;
		String hashedPassword = AssetUtil.computeMD5Hash(salt + wrPassword.toUpperCase(Locale.getDefault()) + salt);
		String url = String.format(Locale.getDefault(), ApplicationConstants.WEB_REPORTING_BASE_URL + "goproreporting/Login.aspx?mpu=%s&mpp=%s&mpr=%d&mprd=%s", 
				salesPersonNo, 
				hashedPassword, 
				11, 
				customerNo);
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
	
	private void ekkSync() {
		String businessUnit = (businessUnitNo != null) ? businessUnitNo : "";
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		ElectronicCardCustomerSyncObject electronicCardCustomerSyncObject = new ElectronicCardCustomerSyncObject("", customerNo, businessUnit, "", "");
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, electronicCardCustomerSyncObject);
		getActivity().startService(intent);
	}
	
	private void azurirajPodatkeProdavca(String salesPersonNo) {
		SalespersonUpdateSyncObject salespersonSetupSyncObject = new SalespersonUpdateSyncObject(salesPersonNo);
		Intent syncSalesPerson = new Intent(getActivity(), NavisionSyncService.class);
		syncSalesPerson.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, salespersonSetupSyncObject);
		getActivity().startService(syncSalesPerson);
	}
	
	private void openWebReportingLoginPage() {
    	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationConstants.WEB_REPORTING_BASE_URL)));
    }
	
	@SuppressWarnings("deprecation")
	private void setValues(Cursor cursor) {
		
		if (!cursor.isNull(CustomerQuery.NAME)) {
			tvImeKupca.setText(cursor.getString(CustomerQuery.NAME));
		}
		
		if (!cursor.isNull(CustomerQuery.CUSTOMER_NO)) {
			tvSifraKupca.setText(cursor.getString(CustomerQuery.CUSTOMER_NO));
		}
		
		if (!cursor.isNull(CustomerQuery.NAME_2)) {
			tvImeKupca2.setText(cursor.getString(CustomerQuery.NAME_2));
		}
		
		if (!cursor.isNull(CustomerQuery.HAS_BUSINESS_UNITS)) {
			if (cursor.getInt(CustomerQuery.HAS_BUSINESS_UNITS) == 0) {
				tvPoslovnaJedinica.setText(getString(R.string.nemaPoslovnuJedinicu));
			} else {
				if (businessUnitNo != null && !businessUnitNo.equals("")) {
					Cursor businessUnitCursor = getActivity().getContentResolver().query(CustomerBusinessUnits.CONTENT_URI, new String[] { CustomerBusinessUnits.ADDRESS, CustomerBusinessUnits.CITY }, CustomerBusinessUnits.UNIT_NO + "=?", new String[] { businessUnitNo }, null);
					if (businessUnitCursor.moveToFirst()) {
						tvPoslovnaJedinica.setText(String.format("%s - %s, %s", businessUnitNo, businessUnitCursor.getString(0), businessUnitCursor.getString(1)));
					}
				} else {
					tvPoslovnaJedinica.setText(getString(R.string.nemaPoslovnuJedinicu));
				}
			}
		}
		
		if (!cursor.isNull(CustomerQuery.ADDRESS)) {
			tvAdresaKupca.setText(cursor.getString(CustomerQuery.ADDRESS));
		}
		
		if (!cursor.isNull(CustomerQuery.CITY)) {
			tvGradKupca.setText(cursor.getString(CustomerQuery.CITY));
		}
		
		if (!cursor.isNull(CustomerQuery.POST_CODE)) {
			tvPostanskiBrojKupca.setText(cursor.getString(CustomerQuery.POST_CODE));
		}
		
		if (!cursor.isNull(CustomerQuery.PHONE)) {
			tvTelefonKupca.setText(cursor.getString(CustomerQuery.PHONE));
		}
		
		if (!cursor.isNull(CustomerQuery.MOBILE)) {
			tvMobilniKupca.setText(cursor.getString(CustomerQuery.MOBILE));
		}
		
		if (!cursor.isNull(CustomerQuery.EMAIL)) {
			tvEmailKupca.setText(cursor.getString(CustomerQuery.EMAIL));
		}
		
		if (!cursor.isNull(CustomerQuery.COMPANY_ID)) {
			tvMaticniBrojKupca.setText(cursor.getString(CustomerQuery.COMPANY_ID));
		}
		
		if (!cursor.isNull(CustomerQuery.PRIMARY_CONTACT_ID)) {
			tvBrojPrimarnogKontaktaKupca.setText(cursor.getString(CustomerQuery.PRIMARY_CONTACT_ID));
		}
		
		if (!cursor.isNull(CustomerQuery.VAR_REG_NO)) {
			tvPoreskiBrojKupca.setText(cursor.getString(CustomerQuery.VAR_REG_NO));
		}
		
		if (!cursor.isNull(CustomerQuery.CUSTOMER_COLOR)) {
			switch (Integer.valueOf(cursor.getString(CustomerQuery.CUSTOMER_COLOR))) {
				case 0:
					vBojaKupca1.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_white_rect));
					vBojaKupca2.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_white_rect));
					break;
				case 1:
					vBojaKupca1.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_black_rect));
					vBojaKupca2.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_black_rect));
					break;
				case 2:
					vBojaKupca1.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_dark_red_rect));
					vBojaKupca2.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_dark_red_rect));
					break;
				case 3:
					vBojaKupca1.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_gray_rect));
					vBojaKupca2.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_green_rect));
					break;
				case 4:
					vBojaKupca1.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_green_rect));
					vBojaKupca2.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_green_rect));
					break;
				case 5:
					vBojaKupca1.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_red_rect));
					vBojaKupca2.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_red_rect));
					break;
				case 6:
					vBojaKupca1.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_yellow_rect));
					vBojaKupca2.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_yellow_rect));
					break;
				case 7:
					vBojaKupca1.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_gray_rect));
					vBojaKupca2.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_yellow_rect));
					break;
				case 8:
					vBojaKupca1.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_gray_rect));
					vBojaKupca2.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_red_rect));
					break;
				default:
					vBojaKupca1.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_white_rect));
					vBojaKupca2.setBackgroundDrawable(getResources().getDrawable(R.drawable.customer_white_rect));
					break;
			}
		}
		
		if (!cursor.isNull(CustomerQuery.CREDIT_LIMIT_LCY)) {
			tvKreditniLimitKupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.CREDIT_LIMIT_LCY)));
		}
		
		if (!cursor.isNull(CustomerQuery.BALANCE_LCY)) {
			tvSaldoKupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.BALANCE_LCY)));
		}
		
		if (!cursor.isNull(CustomerQuery.BALANCE_DUE_LCY)) {
			tvDospeliSaldoKupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.BALANCE_DUE_LCY)));
		}
		
		if (!cursor.isNull(CustomerQuery.INTERNAL_BALANCE_DUE_LCY)) {
			tvDospeliSaldoIVKupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.INTERNAL_BALANCE_DUE_LCY)));
		}
		
		if (!cursor.isNull(CustomerQuery.PAYMENT_TERMS_CODE)) {
			tvSifraUslovaPlacanjaKupca.setText(cursor.getString(CustomerQuery.PAYMENT_TERMS_CODE));
		}
		
		if (!cursor.isNull(CustomerQuery.PRIORITY)) {
			tvPrioritetKupca.setText(cursor.getString(CustomerQuery.PRIORITY));
		}
		
		if (!cursor.isNull(CustomerQuery.GLOBAL_DIMENSION)) {
			tvBransaKupca.setText(cursor.getString(CustomerQuery.GLOBAL_DIMENSION));
		}

		if (!cursor.isNull(CustomerQuery.CHANNEL_ORAN)) {
			tvKanalKupca.setText(cursor.getString(CustomerQuery.CHANNEL_ORAN));
		}
		
		if (!cursor.isNull(CustomerQuery.BLOCKED_STATUS)) {
			tvStatusKupca.setText(cursor.getString(CustomerQuery.BLOCKED_STATUS));
		}
		
		if (!cursor.isNull(CustomerQuery.SML)) {
			tvSMLKupca.setText(cursor.getString(CustomerQuery.SML));
		}
		
		if (!cursor.isNull(CustomerQuery.ADOPTED_POTENTIAL)) {
			tvUsvojeniPotencijalKupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.ADOPTED_POTENTIAL)));
		}
		
		if (!cursor.isNull(CustomerQuery.FOCUS_CUSTOMER)) {
			tvFokusKupca.setText(cursor.getString(CustomerQuery.FOCUS_CUSTOMER));
		}

		if (!cursor.isNull(CustomerQuery.DIVISION)) {
			tvDivizijaKupca.setText(cursor.getString(CustomerQuery.DIVISION));
		}
		
		if (!cursor.isNull(CustomerQuery.NUMBER_OF_BLUE_COAT)) {
			tvBrojPlavihMantilaKupcaKupca.setText(cursor.getString(CustomerQuery.NUMBER_OF_BLUE_COAT));
		}
		
		if (!cursor.isNull(CustomerQuery.NUMBER_OF_GREY_COAT)) {
			tvBrojSivihMantilaKupcaKupca.setText(cursor.getString(CustomerQuery.NUMBER_OF_GREY_COAT));
		}
		
		if (!cursor.isNull(CustomerQuery.TURNOVER_IN_LAST_3M)) {
			tvPromet3Kupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.TURNOVER_IN_LAST_3M)));
		}
		
		if (!cursor.isNull(CustomerQuery.TURNOVER_IN_LAST_6M)) {
			tvPromet6Kupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.TURNOVER_IN_LAST_6M)));
		}
		
		if (!cursor.isNull(CustomerQuery.TURNOVER_IN_LAST_12M)) {
			tvPromet12Kupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.TURNOVER_IN_LAST_12M)));
		}
		
		if (!cursor.isNull(CustomerQuery.TURNOVER_GENERATED_1)) {
			tvPrometProslaKupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.TURNOVER_GENERATED_1)));
		}
		
		if (!cursor.isNull(CustomerQuery.TURNOVER_GENERATED_2)) {
			tvPrometPre2Kupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.TURNOVER_GENERATED_2)));
		}
		
		if (!cursor.isNull(CustomerQuery.TURNOVER_GENERATED_3)) {
			tvPrometPre3Kupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.TURNOVER_GENERATED_3)));
		}
		
		if (!cursor.isNull(CustomerQuery.TURNOVER_YTM)) {
			tvPrometPreYTMKupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.TURNOVER_YTM)));
		}
		
		if (!cursor.isNull(CustomerQuery.GROSS_PROFIT_PFEP)) {
			tvUkupanProfitKupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.GROSS_PROFIT_PFEP)));
		}
		
		if (!cursor.isNull(CustomerQuery.APR_CUSTOMER_TURNOVER)) {
			tvSopstveniPrometKupca.setText(UIUtils.formatDoubleForUI(cursor.getDouble(CustomerQuery.APR_CUSTOMER_TURNOVER)));
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), Customers.CONTENT_URI, CustomerQuery.PROJECTION, Customers._ID + "=?", new String[] { String.valueOf(customerId) }, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor != null && cursor.moveToFirst()) {
			setValues(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	private interface SalesPersonQuery {

		String[] PROJECTION = { BaseColumns._ID,
				MobileStoreContract.SalesPerson.SALE_PERSON_NO,
				MobileStoreContract.SalesPerson.WR_USERNAME,
				MobileStoreContract.SalesPerson.WR_PASSWORD
				};

//		int _ID = 0;
//		int SALE_PERSON_NO = 1;
//		int WR_USERNAME = 2;
		int WR_PASSWORD = 3;
	}
	
	private interface CustomerQuery {
		String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.Customers.CUSTOMER_NO,
                MobileStoreContract.Customers.NAME,
                MobileStoreContract.Customers.NAME_2,
                MobileStoreContract.Customers.ADDRESS,
                MobileStoreContract.Customers.POST_CODE,
                MobileStoreContract.Customers.PHONE,
                MobileStoreContract.Customers.MOBILE,
                MobileStoreContract.Customers.EMAIL,
                MobileStoreContract.Customers.COMPANY_ID,
                MobileStoreContract.Customers.PRIMARY_CONTACT_ID,
                MobileStoreContract.Customers.VAT_REG_NO,
                MobileStoreContract.Customers.CREDIT_LIMIT_LCY,
                MobileStoreContract.Customers.BALANCE_LCY,
                MobileStoreContract.Customers.BALANCE_DUE_LCY,
                MobileStoreContract.Customers.PAYMENT_TERMS_CODE,
                MobileStoreContract.Customers.PRIORITY,
                MobileStoreContract.Customers.GLOBAL_DIMENSION,
                MobileStoreContract.Customers.CHANNEL_ORAN,
                MobileStoreContract.Customers.BLOCKED_STATUS,
                MobileStoreContract.Customers.SML,
                MobileStoreContract.Customers.INTERNAL_BALANCE_DUE_LCY,
                MobileStoreContract.Customers.ADOPTED_POTENTIAL,
                MobileStoreContract.Customers.FOCUS_CUSTOMER,
                MobileStoreContract.Customers.DIVISION,
                MobileStoreContract.Customers.NUMBER_OF_BLUE_COAT,
                MobileStoreContract.Customers.NUMBER_OF_GREY_COAT,
                MobileStoreContract.Customers.CITY,
                MobileStoreContract.Customers.TURNOVER_GENERATED_1,
                MobileStoreContract.Customers.TURNOVER_GENERATED_2,
                MobileStoreContract.Customers.TURNOVER_GENERATED_3,
                MobileStoreContract.Customers.TURNOVER_IN_LAST_6M,
                MobileStoreContract.Customers.TURNOVER_IN_LAST_3M,
                MobileStoreContract.Customers.TURNOVER_IN_LAST_12M,
                MobileStoreContract.Customers.TURNOVER_YTM,
                MobileStoreContract.Customers.GROSS_PROFIT_PFEP,
                MobileStoreContract.Customers.APR_CUSTOMER_TURNOVER,
                MobileStoreContract.Customers.CUSTOMER_COLOR, 
                MobileStoreContract.Customers.HAS_BUSINESS_UNITS
        };

//      int _ID = 0;
        int CUSTOMER_NO = 1;
        int NAME = 2;
        int NAME_2 = 3;
        int ADDRESS = 4;
        int POST_CODE = 5;
        int PHONE = 6;
        int MOBILE = 7;
        int EMAIL = 8;
        int COMPANY_ID = 9;
        int PRIMARY_CONTACT_ID = 10;
        int VAR_REG_NO = 11;
        int CREDIT_LIMIT_LCY = 12;
        int BALANCE_LCY = 13;
        int BALANCE_DUE_LCY = 14;
        int PAYMENT_TERMS_CODE = 15;
        int PRIORITY = 16;
        int GLOBAL_DIMENSION = 17;
        int CHANNEL_ORAN = 18;
        int BLOCKED_STATUS = 19;
        int SML = 20;
        int INTERNAL_BALANCE_DUE_LCY = 21;
        int ADOPTED_POTENTIAL = 22;
        int FOCUS_CUSTOMER = 23;
        int DIVISION = 24;
        int NUMBER_OF_BLUE_COAT = 25;
        int NUMBER_OF_GREY_COAT = 26;
        int CITY = 27;
        int TURNOVER_GENERATED_1 = 28;
		int TURNOVER_GENERATED_2 = 29;
		int TURNOVER_GENERATED_3 = 30;
		int TURNOVER_IN_LAST_6M = 31;
		int TURNOVER_IN_LAST_3M = 32;
		int TURNOVER_IN_LAST_12M = 33;
		int TURNOVER_YTM = 34;
		int GROSS_PROFIT_PFEP = 35;
		int APR_CUSTOMER_TURNOVER = 36;
		int CUSTOMER_COLOR = 37;
		int HAS_BUSINESS_UNITS = 38;
	}

}
