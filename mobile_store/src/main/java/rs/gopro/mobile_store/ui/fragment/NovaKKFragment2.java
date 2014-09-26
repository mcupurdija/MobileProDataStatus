package rs.gopro.mobile_store.ui.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.ActionPlan;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.MobileStoreContract.ItemsColumns;
import rs.gopro.mobile_store.provider.MobileStoreContract.ItemsOnPromotion;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrderLines;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.NovaKarticaKupcaMasterActivity;
import rs.gopro.mobile_store.ui.components.ItemAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.util.UIUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.model.ItemQtySalesPriceAndDiscSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidquery.AQuery;
import com.readystatesoftware.viewbadger.BadgeView;

public class NovaKKFragment2 extends Fragment implements LoaderCallbacks<Cursor> {

	public static final String TAG = "NovaKKFragment2";
	
	public static final int LIST_RED = Color.parseColor("#C74B46");
	public static final int LIST_BLUE = Color.parseColor("#007FFF");
	public static final int LIST_BLACK = Color.BLACK;
	public static final int EKK_RED = Color.RED;
	public static final int EKK_ORANGE = Color.parseColor("#FF7F00");
	public static final int EKK_YELLOW = Color.parseColor("#EAEA00");
	public static final int EKK_GREEN = Color.parseColor("#00D900");
	public static final int EKK_GRAY = Color.GRAY;
	public static final int EKK_BLUE = Color.parseColor("#007FFF");
	public static final int EKK_WHITE = Color.WHITE;
	
	private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	private SimpleDateFormat dateFormatUI = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
	
	private int saleOrderId, customerId, currLeftLoaderId = -1, currRightLoaderId = -1, intendedRightLoaderId = -1, selectedItemId, minQty, campaignStatus, itemType, colorFilterPosition;
	private String customerNo, businessUnitNo, branchCode, selectedItemNo, mFilterLeft, mFilterRight, leftListSelectedItemNo, reg_cena, ovs_akc_cena;
	private boolean kesirajSlike, odabranPromoArtikal, sviArtikliItemSelected;
	private ArrayList<String> mKKList, mAKKList, mMSList, mAPList;
	
	private NovaKarticaKupcaMasterActivity masterActivity;
	private NovaKKFragment2Listener mCallback;
	
	private RelativeLayout rlKKbottomContainer, rlKKRightStavke;
	private EditText etKKFilterLeft, etKKFilterRight, etKolicina, etKolicinaNaStanju, etRabat, etRabatMax;
	private LinearLayout llPromocije, llTaskovi, llSviArtikli, llSlika;
	private BadgeView badgeSviArtikli, badgePromocije, badgeTaskovi;
	private ToggleButton tbLeftKK, tbLeftAKK, tbLeftMS, tbLeftAP, tbLeftOVS, tbLeftAKC;
	private ToggleButton tbRightBack, tbRightAKCA, tbRightVeza;
	private LayoutParams tbLeftAKCparams;
	private ProgressBar pbLeft, pbRight, pbBottom;
	private ImageView ivPromo;
	private ListView lvKKLeft, lvKKRight;
	private TextView tvNisteOdabraliArtikal, tvStatus, tvSifraArtikla, tvSpacer, tvOpisArtikla, tvCena, tvCenaRabat, tvVrednost, tvSumaKorpe;
	private FrameLayout flKKsaProm;
	private Button bSub, bAdd, bMin, bPreuzmiCenu, bDodajArtikal, bKKsaProm;
	private CheckBox cbOsnCena;
	private GradientDrawable gradientDrawable;
	private Spinner sKKfilter;

	private ItemAutocompleteCursorAdapter itemAutocompleteAdapter;
	private CursorAdapter kkCursorAdapter, akkCursorAdapter, msCursorAdapter, apCursorAdapter, ovsCursorAdapter, akcCursorAdapter, akcaCursorAdapter, vezaCursorAdapter, promoCursorAdapter;
	private InputMethodManager imm;
	
	public static NovaKKFragment2 newInstance(int saleOrderId, int customerId, String customerNo, String businessUnitNo, String branchCode) {
		NovaKKFragment2 frag = new NovaKKFragment2();
        Bundle args = new Bundle();
        args.putInt("saleOrderId", saleOrderId);
        args.putInt("customerId", customerId);
        args.putString("customerNo", customerNo);
        args.putString("businessUnitNo", businessUnitNo);
        args.putString("branchCode", branchCode);
        frag.setArguments(args);
        return frag;
    }
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			pbBottom.setVisibility(View.GONE);
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (ItemQtySalesPriceAndDiscSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {

				ItemQtySalesPriceAndDiscSyncObject syncObject = (ItemQtySalesPriceAndDiscSyncObject) syncResult.getComplexResult();
				etKolicinaNaStanju.setText(syncObject.getpQuantityAsTxt());
				etRabatMax.setText(syncObject.getpMaximumDiscountPctAsTxt());
				
				reg_cena = syncObject.getpSalesPriceRSDAsTxt();
				ovs_akc_cena = syncObject.getpRegularPriceRSDAsTxt();
				
				// za OVS i AKC artikle
				switch (campaignStatus) {
					case 0:
						cbOsnCena.setVisibility(View.GONE);
						break;
					case 1:
						cbOsnCena.setVisibility(View.VISIBLE);
						break;
					case 2:
						cbOsnCena.setVisibility(View.VISIBLE);
						break;
					default:
						break;
				}
				cbOsnCena.setChecked(false);
				tvCena.setText(reg_cena);
			}
		}
	}
	
	public interface NovaKKFragment2Listener {
		void onItemAdded();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 return inflater.inflate(R.layout.fragment_nova_kartica_kupca_2, container, false);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		masterActivity = ((NovaKarticaKupcaMasterActivity) getActivity());
		imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		saleOrderId = getArguments().getInt("saleOrderId", -1);
		customerId = getArguments().getInt("customerId", -1);
		customerNo = getArguments().getString("customerNo", null);
		businessUnitNo = getArguments().getString("businessUnitNo", null);
		branchCode = getArguments().getString("branchCode", null);
		
		kesirajSlike = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getString(R.string.key_ksa_switch), true);
		
		rlKKbottomContainer = (RelativeLayout) view.findViewById(R.id.rlKKbottomContainer);
		rlKKRightStavke = (RelativeLayout) view.findViewById(R.id.rlKKRightStavke);
		llSviArtikli = (LinearLayout) view.findViewById(R.id.llSviArtikli);
		llSlika = (LinearLayout) view.findViewById(R.id.llSlika);
		llPromocije = (LinearLayout) view.findViewById(R.id.llPromocije);
		llTaskovi = (LinearLayout) view.findViewById(R.id.llTaskovi);
		
		badgeSviArtikli = (BadgeView) view.findViewById(R.id.badgeSviArtikli);
		badgeSviArtikli.setBackgroundDrawable(customBadgeBackground(EKK_GRAY));
		badgeSviArtikli.setText(String.valueOf(masterActivity.sviArtikliUseCount));
		badgePromocije = (BadgeView) view.findViewById(R.id.badgePromocije);
		badgePromocije.setText(String.valueOf(itemsOnPromotionItemCount()));
		badgeTaskovi = (BadgeView) view.findViewById(R.id.badgeTaskovi);
		badgeTaskovi.setText(String.valueOf(taskoviItemCount()));
		
		etKKFilterLeft = (EditText) view.findViewById(R.id.etKKFilterLeft);
		etKKFilterRight = (EditText) view.findViewById(R.id.etKKFilterRight);
		
		pbLeft = (ProgressBar) view.findViewById(R.id.pbLeft);
		pbRight = (ProgressBar) view.findViewById(R.id.pbRight);
		pbBottom = (ProgressBar) view.findViewById(R.id.pbBottom);
		ivPromo = (ImageView) view.findViewById(R.id.ivPromo);
		
		lvKKLeft = (ListView) view.findViewById(R.id.lvKKLeft);
		lvKKRight = (ListView) view.findViewById(R.id.lvKKRight);
		
		tvNisteOdabraliArtikal = (TextView) view.findViewById(R.id.tvNisteOdabraliArtikal);
		tvStatus = (TextView) view.findViewById(R.id.tvStatus);
		tvSifraArtikla = (TextView) view.findViewById(R.id.tvSifraArtikla);
		tvSpacer = (TextView) view.findViewById(R.id.tvSpacer);
		tvOpisArtikla = (TextView) view.findViewById(R.id.tvOpisArtikla);
		etKolicina = (EditText) view.findViewById(R.id.etKolicina);
		etKolicinaNaStanju = (EditText) view.findViewById(R.id.etKolicinaNaStanju);
		etRabat = (EditText) view.findViewById(R.id.etRabat);
		etRabatMax = (EditText) view.findViewById(R.id.etRabatMax);
		tvCena = (TextView) view.findViewById(R.id.tvCena);
		tvCenaRabat = (TextView) view.findViewById(R.id.tvCenaRabat);
		tvVrednost = (TextView) view.findViewById(R.id.tvVrednost);
		tvSumaKorpe = (TextView) view.findViewById(R.id.tvSumaKorpe);
		cbOsnCena = (CheckBox) view.findViewById(R.id.cbOsnCena);
		
		gradientDrawable = new GradientDrawable();
		gradientDrawable.setStroke(1, Color.BLACK);
		gradientDrawable.setColor(Color.WHITE);
		
		itemAutocompleteAdapter = new ItemAutocompleteCursorAdapter(getActivity(), null);
		promoCursorAdapter = new ListPromoAdapter(getActivity());
		kkCursorAdapter = new ListKKAdapter(getActivity());
		akkCursorAdapter = new ListAKKAdapter(getActivity());
		msCursorAdapter = new ListMSAdapter(getActivity());
		apCursorAdapter = new ListAPAdapter(getActivity());
		ovsCursorAdapter = new ListOVSAdapter(getActivity());
		akcCursorAdapter = new ListAKCAdapter(getActivity());
		akcaCursorAdapter = new ListAKCAAdapter(getActivity());
		vezaCursorAdapter = new ListVezaAdapter(getActivity());
		
		lvKKLeft.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		//lvKKLeft.setFastScrollEnabled(true);
		lvKKRight.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		//lvKKRight.setFastScrollEnabled(true);
		
		// POCETNI PRIKAZ
		lvKKLeft.setAdapter(promoCursorAdapter);
		currLeftLoaderId = 10;
		rlKKRightStavke.setVisibility(View.GONE);
		ivPromo.setVisibility(View.VISIBLE);
		
		flKKsaProm = (FrameLayout) view.findViewById(R.id.flKKsaProm);
		bKKsaProm = (Button) view.findViewById(R.id.bKKsaProm);
		tbLeftKK = (ToggleButton) view.findViewById(R.id.tbLeftKK);
		tbLeftAKK = (ToggleButton) view.findViewById(R.id.tbLeftAKK);
		tbLeftMS = (ToggleButton) view.findViewById(R.id.tbLeftMS);
		tbLeftAP = (ToggleButton) view.findViewById(R.id.tbLeftAP);
		tbLeftOVS = (ToggleButton) view.findViewById(R.id.tbLeftOVS);
		tbLeftAKC = (ToggleButton) view.findViewById(R.id.tbLeftAKC);
		tbRightBack = (ToggleButton) view.findViewById(R.id.tbRightBack);
		tbRightAKCA = (ToggleButton) view.findViewById(R.id.tbRightAKCA);
		tbRightVeza = (ToggleButton) view.findViewById(R.id.tbRightVeza);
		
		sKKfilter = (Spinner) view.findViewById(R.id.sKKfilter);
		sKKfilter.setAdapter(new NovaKKcustomSpinnerAdapter(getActivity(), R.layout.nova_kk_custom_spinner, new String[6]));
		tbLeftAKCparams = (LayoutParams) tbLeftAKC.getLayoutParams();
		
		bSub = (Button) view.findViewById(R.id.bSub);
		bAdd = (Button) view.findViewById(R.id.bAdd);
		bMin = (Button) view.findViewById(R.id.bMin);
		bPreuzmiCenu = (Button) view.findViewById(R.id.bPreuzmiCenu);
		bDodajArtikal = (Button) view.findViewById(R.id.bDodajArtikal);
		
		tvCena.setText(UIUtils.formatDoubleForUI(0d));
		tvCenaRabat.setText(UIUtils.formatDoubleForUI(0d));
		tvVrednost.setText(UIUtils.formatDoubleForUI(0d));
		
		etKKFilterLeft.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				mFilterLeft = s.toString();
				if (currLeftLoaderId != -1) {
					Bundle args = new Bundle();
			        args.putString("filter", "L");
					getLoaderManager().restartLoader(currLeftLoaderId, args, NovaKKFragment2.this);
				}
			}
		});
		
		etKKFilterRight.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				mFilterRight = s.toString();
				if (currRightLoaderId != -1) {
					Bundle args = new Bundle();
			        args.putString("filter", "R");
					getLoaderManager().restartLoader(currRightLoaderId, args, NovaKKFragment2.this);
				}
			}
		});
		
		llSviArtikli.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sviArtikliDijalog();
			}
		});

		llSlika.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String sifraArtikla = tvSifraArtikla.getText().toString();
				if (sifraArtikla != null && sifraArtikla.length() > 0) {
					slikaDijalog(sifraArtikla);
				} else {
					masterActivity.showToast(getString(R.string.nisteOdabraliArtikal));
				}
			}
		});
		
		llPromocije.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				flKKsaProm.setVisibility(View.VISIBLE);
				
				lvKKLeft.setAdapter(promoCursorAdapter);
				lvKKRight.setAdapter(null);
				currLeftLoaderId = 10;
				currRightLoaderId = -1;
				intendedRightLoaderId = -1;
				etKKFilterLeft.setText("");
				getLoaderManager().restartLoader(currLeftLoaderId, null, NovaKKFragment2.this);
				
				tbLeftKK.setChecked(false);
				tbLeftAKK.setChecked(false);
				tbLeftMS.setChecked(false);
				tbLeftAP.setChecked(false);
				tbLeftOVS.setChecked(false);
				tbLeftAKC.setChecked(false);
				
				tbLeftKK.setClickable(true);
				tbLeftAKK.setClickable(true);
				tbLeftMS.setClickable(true);
				tbLeftAP.setClickable(true);
				tbLeftOVS.setClickable(true);
				tbLeftAKC.setClickable(true);
				tbRightAKCA.setClickable(true);
				tbRightVeza.setClickable(true);
				
				if (!ivPromo.isShown()) {
					rlKKRightStavke.setVisibility(View.GONE);
					ivPromo.setVisibility(View.VISIBLE);
				}
				if (tbRightBack.isShown()) {
					tbRightBack.setVisibility(View.GONE);
				}
				tvStatus.setText(getString(R.string.dugmePromocijeShort));
			}
		});
		
		llTaskovi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				// startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); // customerNo
				
				masterActivity.showToast("Opcija 'ZADACI' je trenutno u izradi!");
			}
		});
		
		bKKsaProm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				flKKsaProm.setVisibility(View.GONE);
				getArguments().putBoolean("bKKsaPromClicked", true);
				
				tbLeftKK.performClick();
			}
		});
		
		lvKKLeft.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				TextView sifraArtikla = (TextView) view.findViewById(R.id.tvNovaKKsifraArtikla);
				TextView opisArtikla = (TextView) view.findViewById(R.id.tvNovaKKopisArtikla);
				ExtraData extraData = (ExtraData) sifraArtikla.getTag();
				itemType = extraData.getItemType();
				if (itemType == 0) {
					leftListSelectedItemNo = sifraArtikla.getText().toString();
					tvSifraArtikla.setText(leftListSelectedItemNo);
					String opis = opisArtikla.getText().toString();
					tvOpisArtikla.setText(opis);
					if (opis != null && opis.length() > 0) {
						tvSpacer.setVisibility(View.VISIBLE);
					} else {
						tvSpacer.setVisibility(View.GONE);
					}
					selectedItemId = extraData.getItemId();
					selectedItemNo = extraData.getItemNo();
					minQty = extraData.getItemMinQty();
					campaignStatus = extraData.getCampaignStatus();
					clearFields();
					etKolicina.setText(String.valueOf(minQty));
					
					// za PROMO artikle
					double itemPrice = extraData.getItemPrice();
					if (itemPrice != 0d) {
						tvCena.setText(UIUtils.formatDoubleForUI(itemPrice));
						etRabat.setText(UIUtils.formatDoubleForUI(extraData.getItemDiscount()));
						odabranPromoArtikal = true;
					} else {
						odabranPromoArtikal = false;
					}
					
					lvKKLeft.setItemChecked(position, true);
					
					hideInfoMessage();
					
					switch (currRightLoaderId) {
						case 8:
							getLoaderManager().restartLoader(currRightLoaderId, null, NovaKKFragment2.this);
							break;
						case 9:
							getLoaderManager().restartLoader(currRightLoaderId, null, NovaKKFragment2.this);
							break;
					}
				} else {
					leftListSelectedItemNo = null;
					masterActivity.showToast("Odabrali ste grupu artikala");
				}
				
		        imm.hideSoftInputFromWindow(etKKFilterLeft.getWindowToken(), 0);
			}
		});
		
		lvKKRight.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				TextView sifraArtikla = (TextView) view.findViewById(R.id.tvNovaKKsifraArtikla);
				TextView opisArtikla = (TextView) view.findViewById(R.id.tvNovaKKopisArtikla);
				ExtraData extraData = (ExtraData) sifraArtikla.getTag();
				itemType = extraData.getItemType();
				if (itemType == 0) {
					tvSifraArtikla.setText(sifraArtikla.getText().toString());
					String opis = opisArtikla.getText().toString();
					tvOpisArtikla.setText(opis);
					if (opis != null && opis.length() > 0) {
						tvSpacer.setVisibility(View.VISIBLE);
					} else {
						tvSpacer.setVisibility(View.GONE);
					}
					selectedItemId = extraData.getItemId();
					selectedItemNo = extraData.getItemNo();
					minQty = extraData.getItemMinQty();
					campaignStatus = extraData.getCampaignStatus();
					clearFields();
					etKolicina.setText(String.valueOf(minQty));
					lvKKRight.setItemChecked(position, true);
					
					odabranPromoArtikal = false;
					
					hideInfoMessage();
				} else {
					masterActivity.showToast("Odabrali ste grupu artikala");
				}
				
				imm.hideSoftInputFromWindow(etKKFilterRight.getWindowToken(), 0);
			}
		});
		
		sKKfilter.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				colorFilterPosition = position;
				// samo za KK
				getLoaderManager().restartLoader(0, null, NovaKKFragment2.this);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		cbOsnCena.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if (isChecked) {
					tvCena.setText(ovs_akc_cena);
				} else {
					tvCena.setText(reg_cena);
				}
			}
		});
		
		etKolicina.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				calculateTotalPriceWithDiscount();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		tbLeftKK.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {

					lvKKLeft.setAdapter(kkCursorAdapter);
					lvKKRight.setAdapter(akkCursorAdapter);
					currLeftLoaderId = 0;
					currRightLoaderId = 1;
					intendedRightLoaderId = 1;
					etKKFilterLeft.setText("");
					etKKFilterRight.setText("");
					sKKfilter.setSelection(0);
					tbLeftAKCparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
					tbLeftAKCparams.addRule(RelativeLayout.LEFT_OF, R.id.sKKfilter);
					tbLeftAKC.setLayoutParams(tbLeftAKCparams);
					sKKfilter.setVisibility(View.VISIBLE);
					getLoaderManager().restartLoader(currRightLoaderId, null, NovaKKFragment2.this);
					
					tbRightBack.setTextOn(getString(R.string.dugmeAKK));
					tbRightBack.setTextOff(getString(R.string.dugmeAKK));
					if (!tbRightBack.isShown()) {
						tbRightBack.setVisibility(View.VISIBLE);
					}
					
					tbLeftAKK.setChecked(false);
					tbLeftMS.setChecked(false);
					tbLeftAP.setChecked(false);
					tbLeftOVS.setChecked(false);
					tbLeftAKC.setChecked(false);
					tbRightBack.setChecked(true);
					tbRightAKCA.setChecked(false);
					tbRightVeza.setChecked(false);
					
					tbLeftKK.setClickable(false);
					tbLeftAKK.setClickable(true);
					tbLeftMS.setClickable(true);
					tbLeftAP.setClickable(true);
					tbLeftOVS.setClickable(true);
					tbLeftAKC.setClickable(true);
					tbRightBack.setClickable(false);
					tbRightAKCA.setClickable(true);
					tbRightVeza.setClickable(true);
					
					if (ivPromo.isShown()) {
						rlKKRightStavke.setVisibility(View.VISIBLE);
						ivPromo.setVisibility(View.GONE);
					}
					tvStatus.setText(getString(R.string.dugmeAKK));
				}
			}
		});
		
		tbLeftAKK.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {

					lvKKLeft.setAdapter(akkCursorAdapter);
					lvKKRight.setAdapter(msCursorAdapter);
					currLeftLoaderId = 1;
					currRightLoaderId = 2;
					intendedRightLoaderId = 2;
					etKKFilterLeft.setText("");
					etKKFilterRight.setText("");
					tbLeftAKCparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
					tbLeftAKC.setLayoutParams(tbLeftAKCparams);
					sKKfilter.setVisibility(View.GONE);
					getLoaderManager().restartLoader(currRightLoaderId, null, NovaKKFragment2.this);
					
					tbRightBack.setTextOn(getString(R.string.dugmeMS));
					tbRightBack.setTextOff(getString(R.string.dugmeMS));
					if (!tbRightBack.isShown()) {
						tbRightBack.setVisibility(View.VISIBLE);
					}
					
					tbLeftKK.setChecked(false);
					tbLeftMS.setChecked(false);
					tbLeftAP.setChecked(false);
					tbLeftOVS.setChecked(false);
					tbLeftAKC.setChecked(false);
					tbRightBack.setChecked(true);
					tbRightAKCA.setChecked(false);
					tbRightVeza.setChecked(false);
					
					tbLeftKK.setClickable(true);
					tbLeftAKK.setClickable(false);
					tbLeftMS.setClickable(true);
					tbLeftAP.setClickable(true);
					tbLeftOVS.setClickable(true);
					tbLeftAKC.setClickable(true);
					tbRightBack.setClickable(false);
					tbRightAKCA.setClickable(true);
					tbRightVeza.setClickable(true);
					
					if (ivPromo.isShown()) {
						rlKKRightStavke.setVisibility(View.VISIBLE);
						ivPromo.setVisibility(View.GONE);
					}
					tvStatus.setText(getString(R.string.dugmeMS));
				}
			}
		});

		tbLeftMS.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					
					lvKKLeft.setAdapter(msCursorAdapter);
					lvKKRight.setAdapter(kkCursorAdapter);
					currLeftLoaderId = 2;
					currRightLoaderId = 0;
					intendedRightLoaderId = 0;
					etKKFilterLeft.setText("");
					etKKFilterRight.setText("");
					tbLeftAKCparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
					tbLeftAKC.setLayoutParams(tbLeftAKCparams);
					sKKfilter.setVisibility(View.GONE);
					getLoaderManager().restartLoader(currRightLoaderId, null, NovaKKFragment2.this);
					
					tbRightBack.setTextOn(getString(R.string.dugmeKK));
					tbRightBack.setTextOff(getString(R.string.dugmeKK));
					if (!tbRightBack.isShown()) {
						tbRightBack.setVisibility(View.VISIBLE);
					}
					
					tbLeftKK.setChecked(false);
					tbLeftAKK.setChecked(false);
					tbLeftAP.setChecked(false);
					tbLeftOVS.setChecked(false);
					tbLeftAKC.setChecked(false);
					tbRightBack.setChecked(true);
					tbRightAKCA.setChecked(false);
					tbRightVeza.setChecked(false);
					
					tbLeftKK.setClickable(true);
					tbLeftAKK.setClickable(true);
					tbLeftMS.setClickable(false);
					tbLeftAP.setClickable(true);
					tbLeftOVS.setClickable(true);
					tbLeftAKC.setClickable(true);
					tbRightBack.setClickable(false);
					tbRightAKCA.setClickable(true);
					tbRightVeza.setClickable(true);
					
					if (ivPromo.isShown()) {
						rlKKRightStavke.setVisibility(View.VISIBLE);
						ivPromo.setVisibility(View.GONE);
					}
					tvStatus.setText(getString(R.string.dugmeKK));
				}
			}
		});

		tbLeftAP.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					
					lvKKLeft.setAdapter(apCursorAdapter);
					lvKKRight.setAdapter(null);
					currLeftLoaderId = 3;
					currRightLoaderId = -1;
					intendedRightLoaderId = -1;
					etKKFilterLeft.setText("");
					tbLeftAKCparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
					tbLeftAKC.setLayoutParams(tbLeftAKCparams);
					sKKfilter.setVisibility(View.GONE);
					
					tbLeftKK.setChecked(false);
					tbLeftAKK.setChecked(false);
					tbLeftMS.setChecked(false);
					tbLeftOVS.setChecked(false);
					tbLeftAKC.setChecked(false);
					tbRightAKCA.setChecked(false);
					tbRightVeza.setChecked(false);
					
					tbLeftKK.setClickable(true);
					tbLeftAKK.setClickable(true);
					tbLeftMS.setClickable(true);
					tbLeftAP.setClickable(false);
					tbLeftOVS.setClickable(true);
					tbLeftAKC.setClickable(true);
					tbRightAKCA.setClickable(true);
					tbRightVeza.setClickable(true);
					
					if (tbRightBack.isShown()) {
						tbRightBack.setVisibility(View.GONE);
					}
					if (ivPromo.isShown()) {
						rlKKRightStavke.setVisibility(View.VISIBLE);
						ivPromo.setVisibility(View.GONE);
					}
				}
			}
		});

		tbLeftOVS.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					
					lvKKLeft.setAdapter(ovsCursorAdapter);
					lvKKRight.setAdapter(null);
					currLeftLoaderId = 4;
					currRightLoaderId = -1;
					intendedRightLoaderId = -1;
					etKKFilterLeft.setText("");
					tbLeftAKCparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
					tbLeftAKC.setLayoutParams(tbLeftAKCparams);
					sKKfilter.setVisibility(View.GONE);
					
					tbLeftKK.setChecked(false);
					tbLeftAKK.setChecked(false);
					tbLeftMS.setChecked(false);
					tbLeftAP.setChecked(false);
					tbLeftAKC.setChecked(false);
					tbRightAKCA.setChecked(false);
					tbRightVeza.setChecked(false);
					
					tbLeftKK.setClickable(true);
					tbLeftAKK.setClickable(true);
					tbLeftMS.setClickable(true);
					tbLeftAP.setClickable(true);
					tbLeftOVS.setClickable(false);
					tbLeftAKC.setClickable(true);
					tbRightAKCA.setClickable(true);
					tbRightVeza.setClickable(true);
					
					if (tbRightBack.isShown()) {
						tbRightBack.setVisibility(View.GONE);
					}
					if (ivPromo.isShown()) {
						rlKKRightStavke.setVisibility(View.VISIBLE);
						ivPromo.setVisibility(View.GONE);
					}
				}
			}
		});

		tbLeftAKC.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					
					lvKKLeft.setAdapter(akcCursorAdapter);
					lvKKRight.setAdapter(null);
					currLeftLoaderId = 5;
					currRightLoaderId = -1;
					intendedRightLoaderId = -1;
					etKKFilterLeft.setText("");
					tbLeftAKCparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
					tbLeftAKC.setLayoutParams(tbLeftAKCparams);
					sKKfilter.setVisibility(View.GONE);
					
					tbLeftKK.setChecked(false);
					tbLeftAKK.setChecked(false);
					tbLeftMS.setChecked(false);
					tbLeftAP.setChecked(false);
					tbLeftOVS.setChecked(false);
					tbRightAKCA.setChecked(false);
					tbRightVeza.setChecked(false);
					
					tbLeftKK.setClickable(true);
					tbLeftAKK.setClickable(true);
					tbLeftMS.setClickable(true);
					tbLeftAP.setClickable(true);
					tbLeftOVS.setClickable(true);
					tbLeftAKC.setClickable(false);
					tbRightAKCA.setClickable(true);
					tbRightVeza.setClickable(true);
					
					if (tbRightBack.isShown()) {
						tbRightBack.setVisibility(View.GONE);
					}
					if (ivPromo.isShown()) {
						rlKKRightStavke.setVisibility(View.VISIBLE);
						ivPromo.setVisibility(View.GONE);
					}
				}
			}
		});
		
		tbRightBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tbRightBack.setChecked(true);
				
				tbRightAKCA.setChecked(false);
				tbRightVeza.setChecked(false);
				
				tbRightAKCA.setClickable(true);
				tbRightVeza.setClickable(true);
				
				switch (intendedRightLoaderId) {
					case 0:
						lvKKRight.setAdapter(kkCursorAdapter);
						currRightLoaderId = intendedRightLoaderId;
						break;
					case 1:
						lvKKRight.setAdapter(akkCursorAdapter);
						currRightLoaderId = intendedRightLoaderId;
						break;
					case 2:
						lvKKRight.setAdapter(msCursorAdapter);
						currRightLoaderId = intendedRightLoaderId;
						break;
					default:
						break;
				}
				
				etKKFilterRight.setText("");
				getLoaderManager().restartLoader(currRightLoaderId, null, NovaKKFragment2.this);
			}
		});
		
		tbRightAKCA.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					
					if (selectedItemNo != null) {
						etKKFilterRight.setText("");
						getLoaderManager().restartLoader(8, null, NovaKKFragment2.this);
						lvKKRight.setAdapter(akcaCursorAdapter);
						
						currRightLoaderId = 8;
						
						tbRightBack.setChecked(false);
						tbRightVeza.setChecked(false);
						
						tbRightBack.setClickable(true);
						tbRightAKCA.setClickable(false);
						tbRightVeza.setClickable(true);
						
						if (ivPromo.isShown()) {
							rlKKRightStavke.setVisibility(View.VISIBLE);
							ivPromo.setVisibility(View.GONE);
						}
						
						tvStatus.setText(getString(R.string.dugmeAKCA));
					} else {
						tbRightAKCA.setChecked(false);
						masterActivity.showToast("Niste odabrali artikal iz suprotne liste");
					}
				}
			}
		});
		
		tbRightVeza.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					
					if (leftListSelectedItemNo != null) {
						etKKFilterRight.setText("");
						getLoaderManager().restartLoader(9, null, NovaKKFragment2.this);
						lvKKRight.setAdapter(vezaCursorAdapter);
						
						currRightLoaderId = 9;
						
						tbRightBack.setChecked(false);
						tbRightAKCA.setChecked(false);
						
						tbRightBack.setClickable(true);
						tbRightAKCA.setClickable(true);
						tbRightVeza.setClickable(false);
						
						if (ivPromo.isShown()) {
							rlKKRightStavke.setVisibility(View.VISIBLE);
							ivPromo.setVisibility(View.GONE);
						}
						
						tvStatus.setText(getString(R.string.dugmeVeza));
					} else {
						tbRightVeza.setChecked(false);
						masterActivity.showToast("Niste odabrali artikal iz suprotne liste");
					}
				}
			}
		});
		
		etRabat.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				calculatePriceWithDiscount();
			}
		});
		
		tvCena.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				calculatePriceWithDiscount();
			}
		});
		
		tvCenaRabat.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				calculateTotalPriceWithDiscount();
			}
		});
		
		bSub.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					int quantity = Integer.parseInt(etKolicina.getText().toString());
					quantity -= 1;
					if (quantity >= minQty) {
						etKolicina.setText(String.valueOf(quantity));
					}
				} catch (NumberFormatException nfe) {
					masterActivity.showToast(getString(R.string.celobrojnaVrednostError));
				} catch (Exception e) {
					LogUtils.LOGE(TAG, e.toString());
				}
			}
		});
		
		bAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					int quantity = Integer.parseInt(etKolicina.getText().toString());
					quantity += 1;
					etKolicina.setText(String.valueOf(quantity));
				} catch (NumberFormatException nfe) {
					masterActivity.showToast(getString(R.string.celobrojnaVrednostError));
				} catch (Exception e) {
					LogUtils.LOGE(TAG, e.toString());
				}
			}
		});

		bMin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					int quantity = Integer.parseInt(etKolicina.getText().toString());
					if (quantity < minQty) {
						etKolicina.setText(String.valueOf(minQty));
					} else {
						quantity += minQty;
						etKolicina.setText(String.valueOf(quantity));
					}
				} catch (NumberFormatException nfe) {
					masterActivity.showToast(getString(R.string.celobrojnaVrednostError));
				} catch (Exception e) {
					LogUtils.LOGE(TAG, e.toString());
				}
			}
		});

		bPreuzmiCenu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				pbBottom.setVisibility(View.VISIBLE);
				getItemQtySalesPriceAndDisc();
			}
		});

		bDodajArtikal.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (masterActivity.isSent()) {
					masterActivity.showToast(getString(R.string.dokumentPoslatError));
				} else {
					if (!tbRightAKCA.isChecked()) {
						Cursor cursor = getActivity().getContentResolver().query(Items.CONTENT_URI, ItemsQuery.PROJECTION, Items.CAMPAIGN_STATUS + "=? AND " + Items.BOM_ITEMS + " LIKE ?", new String[] { String.valueOf(2), "%|" + selectedItemNo + "|%" }, null);
						if (cursor.moveToFirst() && !ivPromo.isShown()) {
							prikaziDijalogZaAkcije(cursor.getCount());
						} else {
							addItemToBasket();
						}
					} else {
						addItemToBasket();
					}
				}
			}
		});
		
		getLoaderManager().initLoader(0, null, this);
		getLoaderManager().initLoader(1, null, this);
		getLoaderManager().initLoader(2, null, this);
		getLoaderManager().initLoader(3, null, this);
		getLoaderManager().initLoader(4, null, this);
		getLoaderManager().initLoader(5, null, this);
		getLoaderManager().initLoader(8, null, this);
		getLoaderManager().initLoader(9, null, this);
		getLoaderManager().initLoader(10, null, this);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mCallback = (NovaKKFragment2Listener) activity;
	}
	
	private void addItemToBasket() {
		
		ContentValues cv = new ContentValues();
		
		cv.put(SaleOrderLines.SALE_ORDER_ID, saleOrderId);
		cv.put(SaleOrderLines.LINE_NO, izracunajBrojLinije());
		cv.put(SaleOrderLines.ITEM_ID, selectedItemId);
		cv.put(SaleOrderLines.LOCATION_CODE, getResources().getStringArray(R.array.location_line_type_array)[0]);
		
		try {
			String kolicina = etKolicina.getText().toString().trim();
			if (kolicina != null && !kolicina.equals("")) {
				cv.put(MobileStoreContract.SaleOrderLines.QUANTITY, UIUtils.getDoubleFromUI(kolicina));
			} else {
				DialogUtil.showInfoErrorDialog(getActivity(), getString(R.string.kolicinaNijeUneta));
			}
			
			String kolicinaNaStanju = etKolicinaNaStanju.getText().toString().trim();
			if (kolicinaNaStanju != null && !kolicinaNaStanju.equals("")) {
				cv.put(MobileStoreContract.SaleOrderLines.QUANTITY_AVAILABLE, UIUtils.getDoubleFromUI(kolicinaNaStanju));
			} else {
				cv.putNull(MobileStoreContract.SaleOrderLines.QUANTITY_AVAILABLE);
			}
			
			String cena = tvCena.getText().toString().trim();
			if (cena != null && !cena.equals("")) {
				cv.put(MobileStoreContract.SaleOrderLines.PRICE, UIUtils.getDoubleFromUI(cena));
			} else {
				cv.putNull(MobileStoreContract.SaleOrderLines.PRICE);
			}
			
			if (cbOsnCena.isShown()) {
				if (cbOsnCena.isChecked()) {
					if (reg_cena != null && !reg_cena.equals("") && !reg_cena.equals("0")) {
						cv.put(MobileStoreContract.SaleOrderLines.PRICE_OTHER, UIUtils.getDoubleFromUI(reg_cena));
					} else {
						cv.putNull(MobileStoreContract.SaleOrderLines.PRICE_OTHER);
					}
					cv.put(MobileStoreContract.SaleOrderLines.PRICE_TYPE, ApplicationConstants.PRICE_TYPE_OSN);
				} else {
					if (ovs_akc_cena != null && !ovs_akc_cena.equals("") && !ovs_akc_cena.equals("0")) {
						cv.put(MobileStoreContract.SaleOrderLines.PRICE_OTHER, UIUtils.getDoubleFromUI(ovs_akc_cena));
					} else {
						cv.putNull(MobileStoreContract.SaleOrderLines.PRICE_OTHER);
					}
					cv.put(MobileStoreContract.SaleOrderLines.PRICE_TYPE, ApplicationConstants.PRICE_TYPE_AKC);
				}
			} else {
				cv.putNull(MobileStoreContract.SaleOrderLines.PRICE_OTHER);
			}
			
			String rabat = etRabat.getText().toString().trim();
			if (rabat != null && !rabat.equals("")) {
				cv.put(MobileStoreContract.SaleOrderLines.REAL_DISCOUNT, UIUtils.getDoubleFromUI(rabat.replace('.', ',')));
			} else {
				cv.putNull(MobileStoreContract.SaleOrderLines.REAL_DISCOUNT);
			}
			
			cv.putNull(MobileStoreContract.SaleOrderLines.MIN_DISCOUNT);
			
			String rabatMax = etRabatMax.getText().toString().trim();
			if (rabatMax != null && !rabatMax.equals("")) {
				cv.put(MobileStoreContract.SaleOrderLines.MAX_DISCOUNT, UIUtils.getDoubleFromUI(rabatMax));
			} else {
				cv.putNull(MobileStoreContract.SaleOrderLines.MAX_DISCOUNT);
			}
		} catch (Exception e) {
			LogUtils.LOGE(TAG, e.getMessage());
			return;
		}

		cv.put(SaleOrderLines.QUOTE_REFUSED_STATUS, 0);
		cv.put(SaleOrderLines.CAMPAIGN_STATUS, campaignStatus);
		
		Cursor cursor = getActivity().getContentResolver().query(SaleOrders.buildSaleOrderUri(String.valueOf(saleOrderId)), new String[] {SaleOrders.BACKORDER_SHIPMENT_STATUS}, null, null, null);
		if (cursor.moveToFirst()) {
			cv.put(SaleOrderLines.BACKORDER_STATUS, cursor.getInt(0));
		}
		cursor.close();
		
		getActivity().getContentResolver().insert(SaleOrderLines.CONTENT_URI, cv);
		updateBasketSum();
		
		if (!odabranPromoArtikal) {
			tbRightVeza.setChecked(true);
		}
		
		mCallback.onItemAdded();
		masterActivity.showToast("Artikal '" + tvSifraArtikla.getText().toString() + "' uspešno dodat u korpu");
	}
	
	private void prikaziDijalogZaAkcije(int actionCount) {
		
		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
		alertDialog.setTitle(R.string.item_dialog_title);
		alertDialog.setMessage("Artikal '" + tvSifraArtikla.getText().toString() + "' se nalazi u [" + actionCount + "] akcijskih paketa. Otvoriti ekran sa akcijama?");
	    alertDialog.setIcon(R.drawable.ic_launcher);
	    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "DA", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				tbRightAKCA.performClick();
			}
		});
	    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NE", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				addItemToBasket();
			}
		});
	    alertDialog.show();
	}

	public void sviArtikliDijalog() {
		
		final Dialog dialog = new Dialog(getActivity());
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.dialog_svi_artikli);
		//dialog.getWindow().getAttributes().verticalMargin = -1F;
		dialog.setTitle(getString(R.string.dugmeSviArtikli));
		
		final TextView tvObavestenje = (TextView) dialog.findViewById(R.id.tvObavestenje);
		
		AutoCompleteTextView mItemAutocomplete = (AutoCompleteTextView) dialog.findViewById(R.id.so_line_item_no_value);
        mItemAutocomplete.setAdapter(itemAutocompleteAdapter);
        mItemAutocomplete.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				
				try {
					Cursor cursor = (Cursor) itemAutocompleteAdapter.getItem(position);
					int itemId = cursor.getInt(0);
					String itemNo = cursor.getString(1);
					String itemDescription = cursor.getString(2);
					int itemMinQty = cursor.getInt(3);
					
					if (mKKList != null && mKKList.contains(itemNo)) {
						tvObavestenje.setVisibility(View.VISIBLE);
						tvObavestenje.setText("Artikal '" + itemNo + "' se nalazi u KK listi!");
					} else if (mAKKList != null && mAKKList.contains(itemNo)) {
						tvObavestenje.setVisibility(View.VISIBLE);
						tvObavestenje.setText("Artikal '" + itemNo + "' se nalazi u AKK listi!");
					} else if (mMSList != null && mMSList.contains(itemNo)) {
						tvObavestenje.setVisibility(View.VISIBLE);
						tvObavestenje.setText("Artikal '" + itemNo + "' se nalazi u MS listi!");
					} else if (mAPList != null && mAPList.contains(itemNo)) {
						tvObavestenje.setVisibility(View.VISIBLE);
						tvObavestenje.setText("Artikal '" + itemNo + "' se nalazi u AP listi!");
					} else {
						tvObavestenje.setVisibility(View.GONE);
						clearFields();
						selectedItemId = itemId;
						leftListSelectedItemNo = itemNo;
						tvSifraArtikla.setText(itemNo);
						tvOpisArtikla.setText(itemDescription);
						minQty = itemMinQty;
						etKolicina.setText(String.valueOf(itemMinQty));
						hideInfoMessage();
						sviArtikliItemSelected = true;
					}
					cursor.close();
				} catch (Exception e) {
					LogUtils.LOGE(TAG, e.getMessage());
				}
			}
		});
        
        final Button dialogButtonOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButtonOK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (sviArtikliItemSelected && !masterActivity.isSent()) {
					badgeSviArtikli.increment(1);
					masterActivity.sviArtikliUseCount++;
					sviArtikliItemSelected = false;
				}
				
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(dialogButtonOK.getWindowToken(), 0);
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	public void slikaDijalog(String sifraArtikla) {
		
		final Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.dialog_slika);
		
		ProgressBar pbPopupSlika = (ProgressBar) dialog.findViewById(R.id.pbPopupSlika);
		ImageView ivPopupSlika = (ImageView) dialog.findViewById(R.id.ivPopupSlika);
		ivPopupSlika.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		String imageUrl = String.format("%s%s.jpg", ApplicationConstants.IMAGES_BASE_URL, sifraArtikla);
		
		AQuery aq = new AQuery(getActivity());
		File file = aq.getCachedFile(imageUrl);
		if (file != null) {
			aq.id(ivPopupSlika).progress(pbPopupSlika).image(file, 0);
		} else {
			aq.id(ivPopupSlika).progress(pbPopupSlika).image(imageUrl, false, kesirajSlike, 0, R.drawable.image_not_available);
		}
		
		dialog.show();
	}
	
	private void calculatePriceWithDiscount() {
		
		String price = tvCena.getText().toString();
		String discount = etRabat.getText().toString();
		if (price == null || price.length() < 1) {
			price = "0";
		}
		if (discount == null || discount.length() < 1) {
			discount = "0";
		}
		try {
			double price_d = WsDataFormatEnUsLatin.parseForUIDouble(price);
			double discount_d = WsDataFormatEnUsLatin.parseForUIDouble(discount.replace('.', ','));
			double discounted_price = price_d - (price_d * (discount_d/100));
			tvCenaRabat.setText(UIUtils.formatDoubleForUI(discounted_price));
		} catch (NumberFormatException e) {
			LogUtils.LOGE(TAG, e.getMessage());
		}
	}
	
	private void calculateTotalPriceWithDiscount() {
		
		String qty = etKolicina.getText().toString();
		String priceWithDiscount = tvCenaRabat.getText().toString();
		
		try {
			double qty_d = UIUtils.getDoubleFromUI(qty);
			double priceWithDiscount_d = UIUtils.getDoubleFromUI(priceWithDiscount);
			double total_price = priceWithDiscount_d * qty_d;
			tvVrednost.setText(UIUtils.formatDoubleForUI(total_price));
		} catch (NumberFormatException e) {
			LogUtils.LOGE(TAG, e.getMessage());
		}
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	
    	updateBasketSum();
    	
    	IntentFilter itemQtySalesPriceAndDiscSync = new IntentFilter(ItemQtySalesPriceAndDiscSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, itemQtySalesPriceAndDiscSync);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
        
        getArguments().putString("suma", tvSumaKorpe.getText().toString());
    }
    
    @Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		
		if (getArguments().getBoolean("bKKsaPromClicked", false)) {
			flKKsaProm.setVisibility(View.GONE);
			tbLeftKK.performClick();
			tbLeftKK.setChecked(true);
		}
		
		lvKKLeft.clearChoices();
		lvKKRight.clearChoices();
		tvSumaKorpe.setText(getArguments().getString("suma", UIUtils.formatDoubleForUI(0d)));
	}
	
	private class ListKKAdapter extends CursorAdapter {
		
		public ListKKAdapter(Context context) {
            super(context, null, false);
        }

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.nova_kk_4c_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			TextView tvNovaKKsifraArtikla = (TextView) view.findViewById(R.id.tvNovaKKsifraArtikla);
			TextView tvNovaKKopisArtikla = (TextView) view.findViewById(R.id.tvNovaKKopisArtikla);
			View vNovaKKboja = (View) view.findViewById(R.id.vNovaKKboja);
			TextView tvNovaKKrb = (TextView) view.findViewById(R.id.tvNovaKKrb);

			try {
				tvNovaKKsifraArtikla.setText(cursor.getString(ElectronicCardCustomerQuery.ITEM_NO));
				tvNovaKKopisArtikla.setText(cursor.getString(ElectronicCardCustomerQuery.ITEM_DESCRIPTION));
				tvNovaKKrb.setText(cursor.getString(ElectronicCardCustomerQuery.SALE_PER_BRANCH_INDEX));

				int hasActions = cursor.getInt(ElectronicCardCustomerQuery.HAS_ACTIONS);
				int campaignStatus = cursor.getInt(ElectronicCardCustomerQuery.CAMPAIGN_STATUS);
				if (hasActions == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_RED);
					tvNovaKKopisArtikla.setTextColor(LIST_RED);
					tvNovaKKrb.setTextColor(LIST_RED);
				} else if (campaignStatus == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLUE);
					tvNovaKKopisArtikla.setTextColor(LIST_BLUE);
					tvNovaKKrb.setTextColor(LIST_BLUE);
				} else {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLACK);
					tvNovaKKopisArtikla.setTextColor(LIST_BLACK);
					tvNovaKKrb.setTextColor(LIST_BLACK);
				}
				
				tvNovaKKsifraArtikla.setTag(new ExtraData(cursor.getInt(ElectronicCardCustomerQuery._ID), cursor.getString(ElectronicCardCustomerQuery.ITEM_NO), cursor.getInt(ElectronicCardCustomerQuery.MIN_QTY), campaignStatus, 0, 0d, 0d));
				
				switch (Integer.valueOf(cursor.getString(ElectronicCardCustomerQuery.COLOR))) {
					case 0:
						vNovaKKboja.setBackgroundColor(EKK_RED);
						break;
					case 1:
						vNovaKKboja.setBackgroundColor(EKK_ORANGE);
						break;
					case 2:
						vNovaKKboja.setBackgroundColor(EKK_YELLOW);
						break;
					case 3:
						vNovaKKboja.setBackgroundColor(EKK_GREEN);
						break;
					case 4:
						vNovaKKboja.setBackgroundDrawable(gradientDrawable);
						break;
					case 5:
						vNovaKKboja.setBackgroundColor(EKK_BLUE);
						break;
					case 6:
						vNovaKKboja.setBackgroundColor(EKK_GRAY);
						break;
					default:
						vNovaKKboja.setBackgroundColor(EKK_WHITE);
						break;
				}
			} catch (Exception e) {
				vNovaKKboja.setBackgroundColor(EKK_RED);
			}
			
		}
	}
	
	private class ListAKKAdapter extends CursorAdapter {
		
		public ListAKKAdapter(Context context) {
            super(context, null, false);
        }

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.nova_kk_4c_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			TextView tvNovaKKsifraArtikla = (TextView) view.findViewById(R.id.tvNovaKKsifraArtikla);
			TextView tvNovaKKopisArtikla = (TextView) view.findViewById(R.id.tvNovaKKopisArtikla);
			View vNovaKKboja = (View) view.findViewById(R.id.vNovaKKboja);
			TextView tvNovaKKrb = (TextView) view.findViewById(R.id.tvNovaKKrb);

			try {
				tvNovaKKsifraArtikla.setText(cursor.getString(ElectronicCardCustomerQuery.ITEM_NO));
				tvNovaKKopisArtikla.setText(cursor.getString(ElectronicCardCustomerQuery.ITEM_DESCRIPTION));
				tvNovaKKrb.setText(cursor.getString(ElectronicCardCustomerQuery.SALE_PER_BRANCH_INDEX));
				
				int hasActions = cursor.getInt(ElectronicCardCustomerQuery.HAS_ACTIONS);
				int campaignStatus = cursor.getInt(ElectronicCardCustomerQuery.CAMPAIGN_STATUS);
				if (hasActions == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_RED);
					tvNovaKKopisArtikla.setTextColor(LIST_RED);
					tvNovaKKrb.setTextColor(LIST_RED);
				} else if (campaignStatus == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLUE);
					tvNovaKKopisArtikla.setTextColor(LIST_BLUE);
					tvNovaKKrb.setTextColor(LIST_BLUE);
				} else {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLACK);
					tvNovaKKopisArtikla.setTextColor(LIST_BLACK);
					tvNovaKKrb.setTextColor(LIST_BLACK);
				}
				
				tvNovaKKsifraArtikla.setTag(new ExtraData(cursor.getInt(ElectronicCardCustomerQuery._ID), cursor.getString(ElectronicCardCustomerQuery.ITEM_NO), cursor.getInt(ElectronicCardCustomerQuery.MIN_QTY), campaignStatus, 0, 0d, 0d));
				
				switch (Integer.valueOf(cursor.getString(ElectronicCardCustomerQuery.COLOR))) {
					case 0:
						vNovaKKboja.setBackgroundColor(EKK_RED);
						break;
					case 1:
						vNovaKKboja.setBackgroundColor(EKK_ORANGE);
						break;
					case 2:
						vNovaKKboja.setBackgroundColor(EKK_YELLOW);
						break;
					case 3:
						vNovaKKboja.setBackgroundColor(EKK_GREEN);
						break;
					case 4:
						vNovaKKboja.setBackgroundDrawable(gradientDrawable);
						break;
					case 5:
						vNovaKKboja.setBackgroundColor(EKK_BLUE);
						break;
					case 6:
						vNovaKKboja.setBackgroundColor(EKK_GRAY);
						break;
					default:
						vNovaKKboja.setBackgroundColor(EKK_WHITE);
						break;
				}
			} catch (Exception e) {
				vNovaKKboja.setBackgroundColor(EKK_RED);
			}
			
		}
	}
	
	private class ListMSAdapter extends CursorAdapter {
		
		public ListMSAdapter(Context context) {
            super(context, null, false);
        }

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.nova_kk_4c_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			TextView tvNovaKKsifraArtikla = (TextView) view.findViewById(R.id.tvNovaKKsifraArtikla);
			TextView tvNovaKKopisArtikla = (TextView) view.findViewById(R.id.tvNovaKKopisArtikla);
			View vNovaKKboja = (View) view.findViewById(R.id.vNovaKKboja);
			TextView tvNovaKKrb = (TextView) view.findViewById(R.id.tvNovaKKrb);

			try {
				tvNovaKKsifraArtikla.setText(cursor.getString(ElectronicCardCustomerQuery.ITEM_NO));
				tvNovaKKopisArtikla.setText(cursor.getString(ElectronicCardCustomerQuery.ITEM_DESCRIPTION));
				tvNovaKKrb.setText(cursor.getString(ElectronicCardCustomerQuery.SALE_PER_BRANCH_INDEX));
				
				int hasActions = cursor.getInt(ElectronicCardCustomerQuery.HAS_ACTIONS);
				int campaignStatus = cursor.getInt(ElectronicCardCustomerQuery.CAMPAIGN_STATUS);
				if (hasActions == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_RED);
					tvNovaKKopisArtikla.setTextColor(LIST_RED);
					tvNovaKKrb.setTextColor(LIST_RED);
				} else if (campaignStatus == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLUE);
					tvNovaKKopisArtikla.setTextColor(LIST_BLUE);
					tvNovaKKrb.setTextColor(LIST_BLUE);
				} else {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLACK);
					tvNovaKKopisArtikla.setTextColor(LIST_BLACK);
					tvNovaKKrb.setTextColor(LIST_BLACK);
				}
				
				tvNovaKKsifraArtikla.setTag(new ExtraData(cursor.getInt(ElectronicCardCustomerQuery._ID), cursor.getString(ElectronicCardCustomerQuery.ITEM_NO), cursor.getInt(ElectronicCardCustomerQuery.MIN_QTY), campaignStatus, 0, 0d, 0d));
				
				switch (Integer.valueOf(cursor.getString(ElectronicCardCustomerQuery.COLOR))) {
					case 0:
						vNovaKKboja.setBackgroundColor(EKK_RED);
						break;
					case 1:
						vNovaKKboja.setBackgroundColor(EKK_ORANGE);
						break;
					case 2:
						vNovaKKboja.setBackgroundColor(EKK_YELLOW);
						break;
					case 3:
						vNovaKKboja.setBackgroundColor(EKK_GREEN);
						break;
					case 4:
						vNovaKKboja.setBackgroundDrawable(gradientDrawable);
						break;
					case 5:
						vNovaKKboja.setBackgroundColor(EKK_BLUE);
						break;
					case 6:
						vNovaKKboja.setBackgroundColor(EKK_GRAY);
						break;
					default:
						vNovaKKboja.setBackgroundColor(EKK_WHITE);
						break;
				}
			} catch (Exception e) {
				vNovaKKboja.setBackgroundColor(EKK_RED);
			}
			
		}
	}
	
	private class ListAPAdapter extends CursorAdapter {
		
		public ListAPAdapter(Context context) {
            super(context, null, false);
        }

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.nova_kk_3c_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			TextView tvNovaKKsifraArtikla = (TextView) view.findViewById(R.id.tvNovaKKsifraArtikla);
			TextView tvNovaKKopisArtikla = (TextView) view.findViewById(R.id.tvNovaKKopisArtikla);
			TextView tvNovaKKrb = (TextView) view.findViewById(R.id.tvNovaKKrb);

			try {
				int isItem = cursor.getInt(ActionPlanQuery.ITEM_TYPE);
				if (isItem == 0) {
					tvNovaKKsifraArtikla.setText(cursor.getString(ActionPlanQuery.ITEM_NO));
					tvNovaKKopisArtikla.setText(cursor.getString(ActionPlanQuery.DESCRIPTION));
				} else {
					tvNovaKKsifraArtikla.setText("-");
					tvNovaKKopisArtikla.setText(cursor.getString(ActionPlanQuery.ITEM_NO));
				}
				tvNovaKKrb.setText(UIUtils.formatDoubleForUI(cursor.getDouble(ActionPlanQuery.LINE_TURNOVER)));
				
				int hasActions = cursor.getInt(ActionPlanQuery.HAS_ACTIONS);
				int campaignStatus = cursor.getInt(ActionPlanQuery.CAMPAIGN_STATUS);
				if (hasActions == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_RED);
					tvNovaKKopisArtikla.setTextColor(LIST_RED);
					tvNovaKKrb.setTextColor(LIST_RED);
				} else if (campaignStatus == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLUE);
					tvNovaKKopisArtikla.setTextColor(LIST_BLUE);
					tvNovaKKrb.setTextColor(LIST_BLUE);
				} else {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLACK);
					tvNovaKKopisArtikla.setTextColor(LIST_BLACK);
					tvNovaKKrb.setTextColor(LIST_BLACK);
				}
				
				tvNovaKKsifraArtikla.setTag(new ExtraData(cursor.getInt(ActionPlanQuery._ID), cursor.getString(ActionPlanQuery.ITEM_NO), cursor.getInt(ActionPlanQuery.MIN_QTY), campaignStatus, isItem, 0d, 0d));
			} catch (Exception e) {
				LogUtils.LOGE(TAG, e.getMessage());
			}
			
		}
	}
	
	private class ListOVSAdapter extends CursorAdapter {
		
		public ListOVSAdapter(Context context) {
            super(context, null, false);
        }

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.nova_kk_3c_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			TextView tvNovaKKsifraArtikla = (TextView) view.findViewById(R.id.tvNovaKKsifraArtikla);
			TextView tvNovaKKopisArtikla = (TextView) view.findViewById(R.id.tvNovaKKopisArtikla);
			TextView tvNovaKKrb = (TextView) view.findViewById(R.id.tvNovaKKrb);

			try {
				tvNovaKKsifraArtikla.setText(cursor.getString(ItemsQuery.ITEM_NO));
				tvNovaKKopisArtikla.setText(cursor.getString(ItemsQuery.DESCRIPTION));
				tvNovaKKrb.setText(UIUtils.formatDoubleForUI(cursor.getDouble(ItemsQuery.UNIT_SALES_PRICE_DIN)));
				
				int hasActions = cursor.getInt(ItemsQuery.HAS_ACTIONS);
				int campaignStatus = cursor.getInt(ItemsQuery.CAMPAIGN_STATUS);
				if (hasActions == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_RED);
					tvNovaKKopisArtikla.setTextColor(LIST_RED);
					tvNovaKKrb.setTextColor(LIST_RED);
				} else if (campaignStatus == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLUE);
					tvNovaKKopisArtikla.setTextColor(LIST_BLUE);
					tvNovaKKrb.setTextColor(LIST_BLUE);
				} else {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLACK);
					tvNovaKKopisArtikla.setTextColor(LIST_BLACK);
					tvNovaKKrb.setTextColor(LIST_BLACK);
				}
				
				tvNovaKKsifraArtikla.setTag(new ExtraData(cursor.getInt(ItemsQuery._ID), cursor.getString(ItemsQuery.ITEM_NO), cursor.getInt(ItemsQuery.MIN_QTY), campaignStatus, 0, 0d, 0d));
			} catch (Exception e) {
				LogUtils.LOGE(TAG, e.getMessage());
			}
			
		}
	}
	
	private class ListAKCAdapter extends CursorAdapter {
		
		public ListAKCAdapter(Context context) {
            super(context, null, false);
        }

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.nova_kk_3c_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			TextView tvNovaKKsifraArtikla = (TextView) view.findViewById(R.id.tvNovaKKsifraArtikla);
			TextView tvNovaKKopisArtikla = (TextView) view.findViewById(R.id.tvNovaKKopisArtikla);
			TextView tvNovaKKrb = (TextView) view.findViewById(R.id.tvNovaKKrb);

			try {
				tvNovaKKsifraArtikla.setText(cursor.getString(ItemsQuery.ITEM_NO));
				tvNovaKKopisArtikla.setText(cursor.getString(ItemsQuery.DESCRIPTION));
				tvNovaKKrb.setText(UIUtils.formatDoubleForUI(cursor.getDouble(ItemsQuery.UNIT_SALES_PRICE_DIN)));
				
				int hasActions = cursor.getInt(ItemsQuery.HAS_ACTIONS);
				int campaignStatus = cursor.getInt(ItemsQuery.CAMPAIGN_STATUS);
				if (hasActions == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_RED);
					tvNovaKKopisArtikla.setTextColor(LIST_RED);
					tvNovaKKrb.setTextColor(LIST_RED);
				} else if (campaignStatus == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLUE);
					tvNovaKKopisArtikla.setTextColor(LIST_BLUE);
					tvNovaKKrb.setTextColor(LIST_BLUE);
				} else {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLACK);
					tvNovaKKopisArtikla.setTextColor(LIST_BLACK);
					tvNovaKKrb.setTextColor(LIST_BLACK);
				}
				
				tvNovaKKsifraArtikla.setTag(new ExtraData(cursor.getInt(ItemsQuery._ID), cursor.getString(ItemsQuery.ITEM_NO), cursor.getInt(ItemsQuery.MIN_QTY), campaignStatus, 0, 0d, 0d));
			} catch (Exception e) {
				LogUtils.LOGE(TAG, e.getMessage());
			}
			
		}
	}
	
	private class ListAKCAAdapter extends CursorAdapter {
		
		public ListAKCAAdapter(Context context) {
            super(context, null, false);
        }

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.nova_kk_akca_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@Override
		public void bindView(View view, Context context, final Cursor cursor) {

			TextView tvNovaKKsifraArtikla = (TextView) view.findViewById(R.id.tvNovaKKsifraArtikla);
			TextView tvNovaKKopisArtikla = (TextView) view.findViewById(R.id.tvNovaKKopisArtikla);
			TextView tvNovaKKrb = (TextView) view.findViewById(R.id.tvNovaKKrb);
			TextView tvNovaKKslika = (TextView) view.findViewById(R.id.tvNovaKKslika);
			
			final String itemNo = cursor.getString(ItemsQuery.ITEM_NO);
			
			try {
				tvNovaKKsifraArtikla.setText(itemNo);
				tvNovaKKopisArtikla.setText(cursor.getString(ItemsQuery.DESCRIPTION));
				tvNovaKKrb.setText(UIUtils.formatDoubleForUI(cursor.getDouble(ItemsQuery.UNIT_SALES_PRICE_DIN)));
				
				int hasActions = cursor.getInt(ItemsQuery.HAS_ACTIONS);
				int campaignStatus = cursor.getInt(ItemsQuery.CAMPAIGN_STATUS);
				if (hasActions == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_RED);
					tvNovaKKopisArtikla.setTextColor(LIST_RED);
					tvNovaKKrb.setTextColor(LIST_RED);
					tvNovaKKslika.setTextColor(LIST_RED);
				} else if (campaignStatus == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLUE);
					tvNovaKKopisArtikla.setTextColor(LIST_BLUE);
					tvNovaKKrb.setTextColor(LIST_BLUE);
					tvNovaKKslika.setTextColor(LIST_BLUE);
				} else {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLACK);
					tvNovaKKopisArtikla.setTextColor(LIST_BLACK);
					tvNovaKKrb.setTextColor(LIST_BLACK);
					tvNovaKKslika.setTextColor(LIST_BLACK);
				}
				
				tvNovaKKsifraArtikla.setTag(new ExtraData(cursor.getInt(ItemsQuery._ID), cursor.getString(ItemsQuery.ITEM_NO), cursor.getInt(ItemsQuery.MIN_QTY), campaignStatus, 0, 0d, 0d));
			} catch (Exception e) {
				LogUtils.LOGE(TAG, e.getMessage());
			}
			
			tvNovaKKslika.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					slikaDijalog(itemNo);
				}
			});
			
		}
	}
	
	private class ListVezaAdapter extends CursorAdapter {
		
		public ListVezaAdapter(Context context) {
            super(context, null, false);
        }

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.nova_kk_veza_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			TextView tvNovaKKsifraArtikla = (TextView) view.findViewById(R.id.tvNovaKKsifraArtikla);
			TextView tvNovaKKopisArtikla = (TextView) view.findViewById(R.id.tvNovaKKopisArtikla);
			TextView tvNovaKKstatus = (TextView) view.findViewById(R.id.tvNovaKKstatus);
			
			String itemNo = cursor.getString(ItemsQuery.ITEM_NO);
			
			Cursor cur = getActivity().getContentResolver().query(Items.buildItemNoUri(leftListSelectedItemNo), new String[] { Items.LINKED_ITEMS }, null, null, null);
			if (cur.moveToFirst()) {
				String linkedItems = cur.getString(0);
				if (linkedItems != null) {
					String linkedItem = extractLinkedItemFromArray(itemNo, linkedItems);
					if (linkedItem != null) {
						String status = linkedItem.substring(linkedItem.length() - 1, linkedItem.length());
						switch (status) {
							case "L":
								tvNovaKKstatus.setText("\u2190");
								break;
							case "U":
								tvNovaKKstatus.setText("\u2191");					
								break;
							case "R":
								tvNovaKKstatus.setText("\u2192");
								break;
							case "D":
								tvNovaKKstatus.setText("\u2193");
								break;
							case "N":
								tvNovaKKstatus.setText("");
								break;
							default:
								break;
						}
					}
				}
			}
			cur.close();
			
			try {
				tvNovaKKsifraArtikla.setText(itemNo);
				tvNovaKKopisArtikla.setText(cursor.getString(ItemsQuery.DESCRIPTION));
				
				int hasActions = cursor.getInt(ItemsQuery.HAS_ACTIONS);
				int campaignStatus = cursor.getInt(ItemsQuery.CAMPAIGN_STATUS);
				if (hasActions == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_RED);
					tvNovaKKopisArtikla.setTextColor(LIST_RED);
					tvNovaKKstatus.setTextColor(LIST_RED);
				} else if (campaignStatus == 1) {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLUE);
					tvNovaKKopisArtikla.setTextColor(LIST_BLUE);
					tvNovaKKstatus.setTextColor(LIST_BLUE);
				} else {
					tvNovaKKsifraArtikla.setTextColor(LIST_BLACK);
					tvNovaKKopisArtikla.setTextColor(LIST_BLACK);
					tvNovaKKstatus.setTextColor(LIST_BLACK);
				}
				
				tvNovaKKsifraArtikla.setTag(new ExtraData(cursor.getInt(ItemsQuery._ID), cursor.getString(ItemsQuery.ITEM_NO), cursor.getInt(ItemsQuery.MIN_QTY), campaignStatus, 0, 0d, 0d));
			} catch (Exception e) {
				LogUtils.LOGE(TAG, e.getMessage());
			}
			
		}
	}
	
	private class ListPromoAdapter extends CursorAdapter {
		
		public ListPromoAdapter(Context context) {
            super(context, null, false);
        }

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.nova_kk_promo_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			String datumOdUI, datumDoUI;
			
			TextView tvNovaKKsifraArtikla = (TextView) view.findViewById(R.id.tvNovaKKsifraArtikla);
			TextView tvNovaKKopisArtikla = (TextView) view.findViewById(R.id.tvNovaKKopisArtikla);
			TextView tvNovaKKkolicina = (TextView) view.findViewById(R.id.tvNovaKKrb);
			TextView tvNovaKKred2 = (TextView) view.findViewById(R.id.tvNovaKKred2);
			TextView tvNovaKKred3 = (TextView) view.findViewById(R.id.tvNovaKKred3);
			
			try {
				String datumOd = cursor.getString(ItemsOnPromotionQuery.VALID_FROM_DATE);
				String datumDo = cursor.getString(ItemsOnPromotionQuery.VALID_TO_DATE);
				if (datumOd != null) {
					datumOdUI = dateFormatUI.format(dateTimeFormat.parse(datumOd));
				} else {
					datumOdUI = "/";
				}
				if (datumDo != null) {
					datumDoUI = dateFormatUI.format(dateTimeFormat.parse(datumDo));
				} else {
					datumDoUI = "/";
				}
				
				tvNovaKKsifraArtikla.setText(cursor.getString(ItemsOnPromotionQuery.ITEM_NO));
				tvNovaKKopisArtikla.setText(cursor.getString(ItemsOnPromotionQuery.DESCRIPTION));
				tvNovaKKkolicina.setText(UIUtils.formatDoubleForUI(cursor.getDouble(ItemsOnPromotionQuery.PRICE)));
				tvNovaKKred2.setText(String.format("%s - %s", datumOdUI, datumDoUI));
				tvNovaKKred3.setText(cursor.getString(ItemsOnPromotionQuery.COMMENT));
				
				/* ISKLJUCENO BOJENJE NA PROMO LISTI
				 * 
				int campaignStatus = cursor.getInt(ItemsOnPromotionQuery.CAMPAIGN_STATUS);
				switch (campaignStatus) {
					case 1:
						tvNovaKKsifraArtikla.setTextColor(LIST_BLUE);
						tvNovaKKopisArtikla.setTextColor(LIST_BLUE);
						break;
					case 2:
						tvNovaKKsifraArtikla.setTextColor(LIST_RED);
						tvNovaKKopisArtikla.setTextColor(LIST_RED);
						break;
					default:
						break;
				}
				*/
				
				tvNovaKKsifraArtikla.setTag(new ExtraData(cursor.getInt(ItemsOnPromotionQuery._ID), cursor.getString(ItemsOnPromotionQuery.ITEM_NO), cursor.getInt(ItemsOnPromotionQuery.MIN_QTY), campaignStatus, 0, cursor.getDouble(ItemsOnPromotionQuery.PRICE), cursor.getDouble(ItemsOnPromotionQuery.DISCOUNT)));
			} catch (Exception e) {
				LogUtils.LOGE(TAG, e.getMessage());
			}
			
		}
	}
	
	private class NovaKKcustomSpinnerAdapter extends ArrayAdapter<String> {
		
		private int[] colors = { EKK_WHITE, EKK_RED, EKK_ORANGE, EKK_YELLOW, EKK_GREEN, EKK_GRAY };
		
		public NovaKKcustomSpinnerAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
	    }
	     
	    @Override
	    public View getDropDownView(int position, View convertView,ViewGroup parent) {
	    	return getCustomView(position, convertView, parent);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	return getCustomView(position, convertView, parent);
	    }

		public View getCustomView(int position, View convertView, ViewGroup parent) {

		    LayoutInflater inflater = getLayoutInflater(null);
		    View row = inflater.inflate(R.layout.nova_kk_custom_spinner, parent, false);
		    
		    View vNovaKKspinnerBoja = row.findViewById(R.id.vNovaKKspinnerBoja);
		    vNovaKKspinnerBoja.setBackgroundColor(colors[position]);
		    
		    return row;
	    }

	}
	
	private int izracunajBrojLinije() {
		Cursor c = getActivity().getContentResolver().query(SaleOrderLines.buildSaleOrderLinesUri(String.valueOf(saleOrderId)), new String[] { SaleOrderLines.LINE_NO }, null, null, null);
		int lastLineNo = 0;
		if (c.moveToLast()) {
			lastLineNo = c.getInt(0);
		}
		c.close();
		return lastLineNo + 1;
	}
	
	private int itemsOnPromotionItemCount() {
		Cursor c = getActivity().getContentResolver().query(ItemsOnPromotion.CONTENT_URI, null, ItemsOnPromotion.BRANCH_CODE + "=?", new String[] { branchCode }, null);
		int count = c.getCount();
		c.close();
		return count;
	}
	
	private int taskoviItemCount() {
		Cursor c = getActivity().getContentResolver().query(Customers.buildCustomersUri(String.valueOf(customerId)), new String[] { Customers.TASK_COUNT }, null, null, null);
		int taskCount = 0;
		if (c.moveToFirst()) {
			taskCount = c.getInt(0);
		}
		c.close();
		return taskCount;
	}
	
	private int getDocumentType() {
		Cursor c = getActivity().getContentResolver().query(SaleOrders.buildSaleOrderUri(String.valueOf(saleOrderId)), new String[] { SaleOrders.DOCUMENT_TYPE }, null, null, null);
		int documentType = 0;
		if (c.moveToFirst()) {
			documentType = c.getInt(0);
		}
		c.close();
		return documentType;
	}
	
	public void updateBasketSum() {
		Cursor c = getActivity().getContentResolver().query(SaleOrderLines.buildSaleOrderLinesUri(String.valueOf(saleOrderId)), SaleOrderLinesQuery.PROJECTION, null, null, null);
		double sum = 0d, itemQty = 0d, itemPrice = 0d, itemDiscount = 0d, itemDiscountPrice = 0d, itemTotal = 0d;
		while (c.moveToNext()) {
			try {
				itemQty = c.getDouble(SaleOrderLinesQuery.QUANTITY);
				itemPrice = c.getDouble(SaleOrderLinesQuery.PRICE);
				itemDiscount = c.getDouble(SaleOrderLinesQuery.REAL_DISCOUNT);
				itemDiscountPrice = itemPrice - (itemPrice * (itemDiscount / 100));
				itemTotal = itemQty * itemDiscountPrice;
				sum += itemTotal;
			} catch (Exception e) {
				LogUtils.LOGE(TAG, e.getMessage());
			}
		}
		c.close();
		
		tvSumaKorpe.setText(UIUtils.formatDoubleForUI(sum));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
			case 0:
				if (colorFilterPosition > 0) {
					int color = 0;
					switch (colorFilterPosition) {
						case 1:
							color = 0;
							break;
						case 2:
							color = 1;
							break;
						case 3:
							color = 2;				
							break;
						case 4:
							color = 3;
							break;
						case 5:
							color = 6;
							break;
						default:
							break;
					}
					return new CursorLoader(getActivity(), KK_AKK_MS_FILTER(args), ElectronicCardCustomerQuery.PROJECTION, ElectronicCardCustomer.CUSTOMER_ID + "=? AND " + ElectronicCardCustomer.ENTRY_TYPE + "=?", new String[] { String.valueOf(customerId), String.valueOf(0) }, "CASE WHEN " + Tables.ELECTRONIC_CARD_CUSTOMER + "." + ElectronicCardCustomer.COLOR + "=" + color + " THEN 0 ELSE 1 END, " + ElectronicCardCustomer.DEFAULT_SORT);
				} else {
					return new CursorLoader(getActivity(), KK_AKK_MS_FILTER(args), ElectronicCardCustomerQuery.PROJECTION, ElectronicCardCustomer.CUSTOMER_ID + "=? AND " + ElectronicCardCustomer.ENTRY_TYPE + "=?", new String[] { String.valueOf(customerId), String.valueOf(0) }, ElectronicCardCustomer.DEFAULT_SORT);
				}
			case 1:
				return new CursorLoader(getActivity(), KK_AKK_MS_FILTER(args), ElectronicCardCustomerQuery.PROJECTION, ElectronicCardCustomer.CUSTOMER_ID + "=? AND " + ElectronicCardCustomer.ENTRY_TYPE + "=?", new String[] { String.valueOf(customerId), String.valueOf(1) }, ElectronicCardCustomer.DEFAULT_SORT);
			case 2:
				return new CursorLoader(getActivity(), KK_AKK_MS_FILTER(args), ElectronicCardCustomerQuery.PROJECTION, ElectronicCardCustomer.CUSTOMER_ID + "=? AND " + ElectronicCardCustomer.ENTRY_TYPE + "=?", new String[] { String.valueOf(customerId), String.valueOf(2) }, ElectronicCardCustomer.DEFAULT_SORT);
			case 3:
				if (businessUnitNo != null) {
					return new CursorLoader(getActivity(), AP_FILTER(args), ActionPlanQuery.PROJECTION, ActionPlan.CUSTOMER_NO + "=? AND " + ActionPlan.BUSINESS_UNIT_NO + "=?", new String[] { customerNo, businessUnitNo }, null);
				}
				return new CursorLoader(getActivity(), AP_FILTER(args), ActionPlanQuery.PROJECTION, ActionPlan.CUSTOMER_NO + "=?", new String[] { customerNo }, null);
			case 4:
				return new CursorLoader(getActivity(), OVS_FILTER(args), ItemsQuery.PROJECTION, Items.CAMPAIGN_STATUS + "=?", new String[] { String.valueOf(1) }, Items.DEFAULT_SORT);
			case 5:
				return new CursorLoader(getActivity(), AKC_FILTER(args), ItemsQuery.PROJECTION, Items.CAMPAIGN_STATUS + "=?", new String[] { String.valueOf(2) }, Items.DEFAULT_SORT);
			case 8:
				return new CursorLoader(getActivity(), AKC_FILTER(args), ItemsQuery.PROJECTION, Items.CAMPAIGN_STATUS + "=? AND " + Items.BOM_ITEMS + " LIKE ?", new String[] { String.valueOf(2), "%|" + selectedItemNo + "|%" }, Items.DEFAULT_SORT);
			case 9:
				if (leftListSelectedItemNo != null) {
					Cursor cur = getActivity().getContentResolver().query(Items.buildItemNoUri(leftListSelectedItemNo), new String[] { Items.LINKED_ITEMS }, null, null, null);
					if (cur.moveToFirst()) {
						String linkedItems = cur.getString(0);
						if (linkedItems != null) {
							return new CursorLoader(getActivity(), VEZA_FILTER(args), ItemsQuery.PROJECTION, Items.ITEM_NO + " IN " + buildLinkedItemsArray(linkedItems), null, Items.DEFAULT_SORT);
						}
					}
				}
				return new CursorLoader(getActivity(), Items.buildItemUri("0"), ItemsQuery.PROJECTION, null, null, null);
			case 10:
				return new CursorLoader(getActivity(), PROMO_FILTER(args), ItemsOnPromotionQuery.PROJECTION, ItemsOnPromotion.BRANCH_CODE + "=?", new String[] { branchCode }, null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if(cursor != null) {
			switch (loader.getId()) {
				case 0:
					kkCursorAdapter.swapCursor(cursor);
					if (mKKList == null) {
						mKKList = new ArrayList<String>();
						while (cursor.moveToNext()) {
							mKKList.add(cursor.getString(ElectronicCardCustomerQuery.ITEM_NO));
						}
					}
					break;
				case 1:
					akkCursorAdapter.swapCursor(cursor);
					if (mAKKList == null) {
						mAKKList = new ArrayList<String>();
						while (cursor.moveToNext()) {
							mAKKList.add(cursor.getString(ElectronicCardCustomerQuery.ITEM_NO));
						}
					}
					break;
				case 2:
					msCursorAdapter.swapCursor(cursor);
					if (mMSList == null) {
						mMSList = new ArrayList<String>();
						while (cursor.moveToNext()) {
							mMSList.add(cursor.getString(ElectronicCardCustomerQuery.ITEM_NO));
						}
					}
					break;
				case 3:
					apCursorAdapter.swapCursor(cursor);
					if (mAPList == null) {
						mAPList = new ArrayList<String>();
						int isItem;
						while (cursor.moveToNext()) {
							isItem = cursor.getInt(ActionPlanQuery.ITEM_TYPE);
							if (isItem == 0) {
								mAPList.add(cursor.getString(ActionPlanQuery.ITEM_NO));
							}
						}
					}
					break;
				case 4:
					ovsCursorAdapter.swapCursor(cursor);
					break;
				case 5:
					akcCursorAdapter.swapCursor(cursor);
					break;
				case 8:
					akcaCursorAdapter.swapCursor(cursor);
					break;
				case 9:
					vezaCursorAdapter.swapCursor(cursor);
					break;
				case 10:
					promoCursorAdapter.swapCursor(cursor);
					break;
			}
		}
		//lvKKLeft.performItemClick(lvKKLeft.getAdapter().getView(0, null, null), 0, lvKKLeft.getAdapter().getItemId(0));
		hideProgressBars();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
			case 0:
				kkCursorAdapter.swapCursor(null);
				break;
			case 1:
				akkCursorAdapter.swapCursor(null);
				break;
			case 2:
				msCursorAdapter.swapCursor(null);
				break;
			case 3:
				apCursorAdapter.swapCursor(null);
				break;
			case 4:
				ovsCursorAdapter.swapCursor(null);
				break;
			case 5:
				akcCursorAdapter.swapCursor(null);
				break;
			case 8:
				akcaCursorAdapter.swapCursor(null);
				break;
			case 9:
				vezaCursorAdapter.swapCursor(null);
				break;
			case 10:
				promoCursorAdapter.swapCursor(null);
				break;
			default:
				break;
		}
	}
	
	private String buildLinkedItemsArray(String itemArray) {
		String[] linkedItemsArray = itemArray.split("\\|");
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (String item : linkedItemsArray) {
			if (item.length() > 0) {
				item = item.substring(0, item.length() - 1);
			}
			sb.append("'" + item + "',");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}
	
	private String extractLinkedItemFromArray(String itemNo, String itemArray) {
		String[] linkedItemsArray = itemArray.split("\\|");
		for (String item : linkedItemsArray) {
			if (item.matches(itemNo + ".")) {
				return item;
			}
		}
		return null;
	}
	
	private Uri KK_AKK_MS_FILTER(Bundle args) {
		if (args != null) {
			String filter = args.getString("filter");
			if (filter.equals("L") && mFilterLeft != null && mFilterLeft.length() > 0) {
				return Uri.withAppendedPath(ElectronicCardCustomer.CONTENT_URI, Uri.encode(mFilterLeft));
			} else if (filter.equals("R") && mFilterRight != null && mFilterRight.length() > 0) {
				return Uri.withAppendedPath(ElectronicCardCustomer.CONTENT_URI, Uri.encode(mFilterRight));
			} else {
				return ElectronicCardCustomer.CONTENT_URI;
	        }
		} else {
			return ElectronicCardCustomer.CONTENT_URI;
		}
	}
	
	private Uri OVS_FILTER(Bundle args) {
		if (args != null) {
			String filter = args.getString("filter");
			if (filter.equals("L") && mFilterLeft != null && mFilterLeft.length() > 0) {
				return Items.buildCustomSearchUri(mFilterLeft, String.valueOf(1));
			} else {
				return Items.CONTENT_URI;
	        }
		} else {
			return Items.CONTENT_URI;
		}
	}
	
	private Uri AKC_FILTER(Bundle args) {
		if (args != null) {
			String filter = args.getString("filter");
			if (filter.equals("L") && mFilterLeft != null && mFilterLeft.length() > 0) {
				return Items.buildCustomSearchUri(mFilterLeft, String.valueOf(2));
			} else if (filter.equals("R") && mFilterRight != null && mFilterRight.length() > 0) {
				return Items.buildCustomSearchUri(mFilterRight, String.valueOf(2));
			} else {
				return Items.CONTENT_URI;
	        }
		} else {
			return Items.CONTENT_URI;
		}
	}
	
	private Uri VEZA_FILTER(Bundle args) {
		if (args != null) {
			String filter = args.getString("filter");
			if (filter.equals("R") && mFilterRight != null && mFilterRight.length() > 0) {
				return Items.buildAutocompleteSearchUri(mFilterRight);
			} else {
				return Items.CONTENT_URI;
	        }
		} else {
			return Items.CONTENT_URI;
		}
	}
	
	private Uri PROMO_FILTER(Bundle args) {
		if (args != null) {
			String filter = args.getString("filter");
			if (filter.equals("L") && mFilterLeft != null && mFilterLeft.length() > 0) {
				return ItemsOnPromotion.buildSearchUri(mFilterLeft);
			} else {
				return ItemsOnPromotion.CONTENT_URI;
	        }
		} else {
			return ItemsOnPromotion.CONTENT_URI;
		}
	}
	
	private Uri AP_FILTER(Bundle args) {
		if (args != null) {
			String filter = args.getString("filter");
			if (filter.equals("L") && mFilterLeft != null && mFilterLeft.length() > 0) {
				return ActionPlan.buildSearchUri(mFilterLeft);
			} else {
				return ActionPlan.CONTENT_URI;
	        }
		} else {
			return ActionPlan.CONTENT_URI;
		}
	}
	
	private void clearFields() {
		etKolicinaNaStanju.setText("");
		etRabat.setText("");
		etRabatMax.setText("");
		tvCena.setText(UIUtils.formatDoubleForUI(0d));
		tvCenaRabat.setText(UIUtils.formatDoubleForUI(0d));
		tvVrednost.setText(UIUtils.formatDoubleForUI(0d));
		cbOsnCena.setVisibility(View.GONE);
	}
	
	private void hideInfoMessage() {
		if (tvNisteOdabraliArtikal.isShown()) {
			tvNisteOdabraliArtikal.setVisibility(View.GONE);
		}
		if (!rlKKbottomContainer.isShown()) {
			rlKKbottomContainer.setVisibility(View.VISIBLE);
		}
	}
	
	private void hideProgressBars() {
		if (pbLeft.isShown()) {
			pbLeft.setVisibility(View.GONE);
		}
		if (pbRight.isShown()) {
			pbRight.setVisibility(View.GONE);
		}
	}
	
	private void getItemQtySalesPriceAndDisc() {

		String kolicina = etKolicina.getText().toString();
		if (kolicina == null || kolicina.equals("")) {
			DialogUtil.showInfoErrorDialog(getActivity(), getString(R.string.kolicinaNijeUneta));
			return;
		}
		
		String locationCode = getResources().getStringArray(R.array.location_line_type_array)[0];
		String salesPersonNo = SharedPreferencesUtil.getSalePersonNo(getActivity());
		int potentialCustomerSignal = isPotentialCustomer(customerId) == false ? 0 : 1;
		int documentType = getDocumentType();
		
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		ItemQtySalesPriceAndDiscSyncObject itemQtySalesPriceAndDiscSyncObject = new ItemQtySalesPriceAndDiscSyncObject(tvSifraArtikla.getText().toString(), locationCode, campaignStatus, Integer.valueOf(potentialCustomerSignal), customerNo, kolicina, salesPersonNo, documentType, "", 0, "", "", "", "", "", "", "", "", "");
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemQtySalesPriceAndDiscSyncObject);
		getActivity().startService(intent);
	}
	
	private boolean isPotentialCustomer(int customerId) {
		Cursor potentialCustomerCursor = getActivity().getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, new String[] {Customers.CONTACT_COMPANY_NO}, 
				"(" + Customers.CONTACT_COMPANY_NO + " is null or " + Customers.CONTACT_COMPANY_NO + "='')" + " and " + Customers._ID + "=?" , new String[] { String.valueOf(customerId) }, null);
		if (potentialCustomerCursor.moveToFirst()) {
			return true;
		}
		potentialCustomerCursor.close();
		return false;
	}
	
	private ShapeDrawable customBadgeBackground(int color) {
		
		int r = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
		float[] outerR = new float[] {r, r, r, r, r, r, r, r};
        
		RoundRectShape rr = new RoundRectShape(outerR, null, null);
		ShapeDrawable drawable = new ShapeDrawable(rr);
		drawable.getPaint().setColor(color);
		
		return drawable;
	}
	
	private class ExtraData {
		
		private int itemId, itemMinQty, campaignStatus, itemType;
		private double itemPrice, itemDiscount;
		private String itemNo;

		public ExtraData(int itemId, String itemNo, int itemMinQty, int campaignStatus, int itemType, double itemPrice, double itemDiscount) {
			super();
			this.itemId = itemId;
			this.itemNo = itemNo;
			this.itemMinQty = itemMinQty;
			this.campaignStatus = campaignStatus;
			this.itemType = itemType;
			this.itemPrice = itemPrice;
			this.itemDiscount = itemDiscount;
		}

		public int getItemId() {
			return itemId;
		}

		public int getItemMinQty() {
			if (itemMinQty == 0)
				return 1;
			else
				return itemMinQty;
		}

		public String getItemNo() {
			return itemNo;
		}

		public int getCampaignStatus() {
			return campaignStatus;
		}

		public int getItemType() {
			return itemType;
		}

		public double getItemPrice() {
			return itemPrice;
		}

		public double getItemDiscount() {
			return itemDiscount;
		}
	}

	private interface ElectronicCardCustomerQuery {
		String[] PROJECTION = new String[] {
				
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+BaseColumns._ID,
				Tables.CUSTOMERS+"."+ElectronicCardCustomer.CUSTOMER_NO,
				Tables.ITEMS+"."+ElectronicCardCustomer.ITEM_NO,
				Tables.ITEMS+"."+ItemsColumns.DESCRIPTION,
				Tables.ITEMS+"."+ItemsColumns.MIN_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.COLOR, 
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.SALE_PER_BRANCH_INDEX, 
				Tables.ITEMS+"."+BaseColumns._ID, 
				Tables.ITEMS+"."+ItemsColumns.CAMPAIGN_STATUS,
				Tables.ITEMS+"."+ItemsColumns.HAS_ACTIONS
		};
		
//		int _ID = 0;
//		int CUSTOMER_ID = 1;
		int ITEM_NO = 2;
		int ITEM_DESCRIPTION = 3;
		int MIN_QTY = 4;
		int COLOR = 5;
		int SALE_PER_BRANCH_INDEX = 6;
		int _ID = 7;
		int CAMPAIGN_STATUS = 8;
		int HAS_ACTIONS = 9;
	}
	
	private interface ItemsQuery {
		String[] PROJECTION = new String[] {
				
				Tables.ITEMS+"."+BaseColumns._ID,
				Tables.ITEMS+"."+Items.ITEM_NO,
				Tables.ITEMS+"."+Items.DESCRIPTION,
				Tables.ITEMS+"."+Items.UNIT_SALES_PRICE_DIN,
				Tables.ITEMS+"."+ItemsColumns.MIN_QTY, 
				Tables.ITEMS+"."+ItemsColumns.CAMPAIGN_STATUS,
				Tables.ITEMS+"."+ItemsColumns.HAS_ACTIONS
		};
		
		int _ID = 0;
		int ITEM_NO = 1;
		int DESCRIPTION = 2;
		int UNIT_SALES_PRICE_DIN = 3;
		int MIN_QTY = 4;
		int CAMPAIGN_STATUS = 5;
		int HAS_ACTIONS = 6;
	}
	
	private interface ActionPlanQuery {
		String[] PROJECTION = {
                Tables.ACTION_PLAN+"."+BaseColumns._ID, 
                Tables.ACTION_PLAN+"."+ActionPlan.ITEM_NO, 
                Tables.ACTION_PLAN+"."+ActionPlan.ITEM_TYPE, 
                Tables.ACTION_PLAN+"."+ActionPlan.LINE_TURNOVER, 
                Tables.ITEMS+"."+ItemsColumns.DESCRIPTION,
				Tables.ITEMS+"."+ItemsColumns.MIN_QTY, 
				Tables.ITEMS+"."+BaseColumns._ID, 
				Tables.ITEMS+"."+ItemsColumns.CAMPAIGN_STATUS,
				Tables.ITEMS+"."+ItemsColumns.HAS_ACTIONS
        };

//      int _ID = 0;
        int ITEM_NO = 1;
        int ITEM_TYPE = 2;
        int LINE_TURNOVER = 3;
        int DESCRIPTION = 4;
        int MIN_QTY = 5;
        int _ID = 6;
        int CAMPAIGN_STATUS = 7;
        int HAS_ACTIONS = 8;
	}
	
	private interface ItemsOnPromotionQuery {
		String[] PROJECTION = new String[] {
				
				Tables.ITEMS_ON_PROMOTION+"."+BaseColumns._ID,
				Tables.ITEMS_ON_PROMOTION+"."+ItemsOnPromotion.ITEM_NO,
				Tables.ITEMS_ON_PROMOTION+"."+ItemsOnPromotion.PRICE,
				Tables.ITEMS_ON_PROMOTION+"."+ItemsOnPromotion.VALID_FROM_DATE,
				Tables.ITEMS_ON_PROMOTION+"."+ItemsOnPromotion.VALID_TO_DATE,
				Tables.ITEMS_ON_PROMOTION+"."+ItemsOnPromotion.COMMENT,
				Tables.ITEMS_ON_PROMOTION+"."+ItemsOnPromotion.DISCOUNT,
				Tables.ITEMS+"."+ItemsColumns.DESCRIPTION,
				Tables.ITEMS+"."+ItemsColumns.MIN_QTY, 
				Tables.ITEMS+"."+BaseColumns._ID, 
				Tables.ITEMS+"."+ItemsColumns.CAMPAIGN_STATUS,
				Tables.ITEMS+"."+ItemsColumns.HAS_ACTIONS
		};
		
//		int _ID = 0;
		int ITEM_NO = 1;
		int PRICE = 2;
		int VALID_FROM_DATE = 3;
		int VALID_TO_DATE = 4;
		int COMMENT = 5;
		int DISCOUNT = 6;
		int DESCRIPTION = 7;
		int MIN_QTY = 8;
		int _ID = 9;
//		int CAMPAIGN_STATUS = 10;
//		int HAS_ACTIONS = 11;
	}
	
	private interface SaleOrderLinesQuery {

        String[] PROJECTION = {
                SaleOrderLines.QUANTITY,
                SaleOrderLines.PRICE, 
                SaleOrderLines.REAL_DISCOUNT
        };

        int QUANTITY = 0;
        int PRICE = 1;
        int REAL_DISCOUNT = 2;
	}

}