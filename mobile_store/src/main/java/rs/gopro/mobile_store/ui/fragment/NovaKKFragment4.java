package rs.gopro.mobile_store.ui.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerBusinessUnits;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrderLines;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.NovaKarticaKupcaMasterActivity;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.UIUtils;
import rs.gopro.mobile_store.util.exceptions.SaleOrderValidationException;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.model.MobileDeviceSalesDocumentSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NovaKKFragment4 extends Fragment implements LoaderCallbacks<Cursor> {

	public static final String TAG = "NovaKKFragment4";
	private static final int SAVE_SALE_DOC = 0;
	private static final int VERIFY_SALE_DOC = 1;
	
	private int saleOrderId, customerId, selectedContactId = -1, selectedShippingAddress = -1, transitCustomerId = -1, hasBusinessUnits;
	private String customerNo, customerName, defaultShippingAddress, defaultShippingCity, defaultShippingPostCode, defaultShippingPhone, appVersion;
	
	private NovaKKFragment4ContactListener mCallback;
	private NovaKKFragment4ShippingAddressListener mCallback2;
	private NovaKKFragment4SaleOrderUpdated mCallback3;
	
	private ProgressDialog sendSaleOrderProgressDialog;
	private String[] salesOptions;
	private String[] orderConditionStatusOptions;
    private String[] financialControlStatusOptions;
    private String[] orderShipmentStatusOptions;
    private String[] orderValueStatusOptions;
	
	private LinearLayout llSacuvaj, llVerifikuj, llPosalji;
	
	private EditText mKupac, mBrojDokumenta, mPoslovnaJedinica;
	
	private Spinner mTipDokumenta, mVrstaProdaje, mNacinObrade, mNacinIsporuke;
	private ArrayAdapter<CharSequence> tipDokumentaAdapter, vrstaProdajeAdapter, nacinObradeAdapter, nacinIsporukeAdapter;
	
	private Button mKontakt;
	private EditText mImeKontakta, mTelefonKontakta, mEmailAdreseKontakta;
	
	private Spinner mSifraLokacije, mPlacanje;
	private ArrayAdapter<CharSequence> sifraLokacijeAdapter, placanjeAdapter;
	private CheckBox mSakrijPopust, mDeklaracija;
	private EditText mBrojPorudzbine;
	
	private Button mAdresaIsporuke;
	private EditText mAdresaIsporukeAdresa, mAdresaIsporukePostanskiBroj, mAdresaIsporukeGrad, mAdresaIsporukeKontakt;
	
	private AutoCompleteTextView mKrajnjiKupac;
	private CustomerAutocompleteCursorAdapter transitCustomerAutoCompleteAdapter;
	private Spinner mStatusUslovaPorudzbine;
	private ArrayAdapter<CharSequence> statusUslovaPorudzbineAdapter;
	
	private EditText mPorukaZaCentralu, mPorukaNaDokumentu;
	
	private TextView mFinansijskiKontrolniStatus, mStatusPorudzbineZaIsporuku, mVrednostPorudzbineStatus;
	
	public static NovaKKFragment4 newInstance(int saleOrderId, int customerId, String appVersion) {
		NovaKKFragment4 frag = new NovaKKFragment4();
        Bundle args = new Bundle();
        args.putInt("saleOrderId", saleOrderId);
        args.putInt("customerId", customerId);
        args.putString("appVersion", appVersion);
        frag.setArguments(args);
        return frag;
    }
	
	public interface NovaKKFragment4ContactListener {
		void onSelectContact();
	}
	
	public interface NovaKKFragment4ShippingAddressListener {
		void onSelectShippingAddress();
	}
	
	public interface NovaKKFragment4SaleOrderUpdated {
		void onSaleOrderUpdated();
	}
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
			sendSaleOrderProgressDialog.dismiss();
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (MobileDeviceSalesDocumentSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				MobileDeviceSalesDocumentSyncObject deviceSalesDocumentSyncObject = (MobileDeviceSalesDocumentSyncObject) syncResult.getComplexResult();

				String quantity = MobileStoreContract.SaleOrderLines.QUANTITY;
				String price = MobileStoreContract.SaleOrderLines.PRICE;
				String discount = MobileStoreContract.SaleOrderLines.REAL_DISCOUNT;
				
				String[] projection = new String[] { "sum(" + quantity + "*(" + price + "-("+ price + "*(" + discount + "/100)))" + ")" };
				Cursor cursorSum = getActivity().getContentResolver().query(MobileStoreContract.SaleOrders.buildSaleOrderSaldo(), projection, Tables.SALE_ORDERS + "." + MobileStoreContract.SaleOrders._ID + "=?", new String[] { String.valueOf(saleOrderId) }, null);
				
				double saldo = 0;
				double pdv = 0;
				double total = 0;
				
				if (cursorSum.moveToFirst()) {
					saldo = cursorSum.getDouble(0);
					pdv = ApplicationConstants.VAT * saldo;
					total = saldo+pdv;
				}
				
				if (cursorSum != null && !cursorSum.isClosed()) {
					cursorSum.close();
				}
				
				final Dialog dialog = new Dialog(getActivity());
				dialog.setContentView(R.layout.dialog_sale_order_send_info);
				dialog.setTitle("Status verifikovane/poslate porudžbine");
				
				TextView text6 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_document_master_status_message);
				String mainStatusMessage = null;
				// do status message only if it is in send mode, if it is verification only do not do it
				Cursor cursorStatus = getActivity().getContentResolver().query(MobileStoreContract.SaleOrders.CONTENT_URI, new String[] {SaleOrders.DOCUMENT_TYPE, SaleOrders.SALES_ORDER_NO}, Tables.SALE_ORDERS + "." + MobileStoreContract.SaleOrders._ID + "=?", new String[] { String.valueOf(saleOrderId) }, null);
				if (deviceSalesDocumentSyncObject.getpVerifyOnly() == 0) {
					if (cursorStatus.moveToFirst()) {
						if (cursorStatus.isNull(1)) {
							mainStatusMessage = "Dokument nije uspešno poslat!";
							text6.setTextColor(Color.RED);
						} else {
							mainStatusMessage = "Dokument uspešno poslat! Broj dokumenta je: "+cursorStatus.getString(1);
							Cursor cursorAdHock = getActivity().getContentResolver().query(MobileStoreContract.SaleOrderLines.CONTENT_URI, new String[] {SaleOrderLines.SALE_ORDER_ID}, Tables.SALE_ORDER_LINES + "." + SaleOrderLines.SALE_ORDER_ID + "=? and " + Tables.SALE_ORDER_LINES + "." + SaleOrderLines.PRICE_DISCOUNT_STATUS+ "=?", new String[] { String.valueOf(saleOrderId), ApplicationConstants.PRICE_AND_DISCOUNT_ARE_NOT_OK }, null);
							if (cursorStatus.getInt(0) == 0 && cursorAdHock.moveToFirst()) {
								mainStatusMessage += "\n" +"Ova porudžbina biće pretvorena u Ad-Hoc Porudžbinu zbog cena van cenovnika i poslata na odobrenje!";
							}
							if (cursorAdHock != null && !cursorAdHock.isClosed()) {
								cursorAdHock.close();
							}
							text6.setTextColor(Color.BLACK);
						}
					}

				} else {
					Cursor cursorAdHock = getActivity().getContentResolver().query(MobileStoreContract.SaleOrderLines.CONTENT_URI, new String[] {SaleOrderLines.SALE_ORDER_ID}, Tables.SALE_ORDER_LINES + "." + SaleOrderLines.SALE_ORDER_ID + "=? and " + Tables.SALE_ORDER_LINES + "." + SaleOrderLines.PRICE_DISCOUNT_STATUS+ "=?", new String[] { String.valueOf(saleOrderId), ApplicationConstants.PRICE_AND_DISCOUNT_ARE_NOT_OK }, null);
					if (cursorStatus.moveToFirst() && cursorStatus.getInt(0) == 0 && cursorAdHock.moveToFirst()) {
//						mainStatusMessage = "Ova porudžbina je kandidat za Ad-Hoc Porudžbinu zbog cena van cenovnika i biće poslata na odobrenje!";
					}
					if (cursorAdHock != null && !cursorAdHock.isClosed()) {
						cursorAdHock.close();
					}
					text6.setTextColor(Color.BLACK);
				}
				if (cursorStatus != null && !cursorStatus.isClosed()) {
					cursorStatus.close();
				}
				int status1 = Integer.valueOf(deviceSalesDocumentSyncObject.getOrder_condition_status());
				int status2 = Integer.valueOf(deviceSalesDocumentSyncObject.getFinancial_control_status());
				int status3 = Integer.valueOf(deviceSalesDocumentSyncObject.getOrder_status_for_shipment());
				int status4 = Integer.valueOf(deviceSalesDocumentSyncObject.getOrder_value_status());
				
				text6.setText(mainStatusMessage);
				
				TextView text7 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_document_additional_status_message);
				double minDiff = 0.0;
				try {
					minDiff = Double.valueOf(WsDataFormatEnUsLatin.toDoubleFromWs(deviceSalesDocumentSyncObject.getMin_max_discount_total_amount_difference()));
				} catch (NumberFormatException nme) {
					LogUtils.LOGE(TAG, "", nme);
				}
				
				if (minDiff < 0.0) {
					text7.setVisibility(View.VISIBLE);
//					text6.setVisibility(View.VISIBLE);
					text7.setText("Korekcija porudžbine: " + UIUtils.formatDoubleForUI(minDiff));
				} else {
					text7.setVisibility(View.GONE);
//					text6.setVisibility(View.GONE);
				}
				
				TextView text1 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_condition_status_spinner);
				text1.setText(orderConditionStatusOptions[status1]);
				
				TextView text2 = (TextView) dialog.findViewById(R.id.dialog_sale_order_financial_control_status_text);
				text2.setText(financialControlStatusOptions[status2]);
				
				TextView text3 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_status_for_shipment_text);
				text3.setText(orderShipmentStatusOptions[status3]);
				
				TextView text4 = (TextView) dialog.findViewById(R.id.dialog_sale_order_order_value_status_text);
				text4.setText(orderValueStatusOptions[status4]);
				
				TextView text5 = (TextView) dialog.findViewById(R.id.dialog_sale_order_document_saldo);
				text5.setText(UIUtils.formatDoubleForUI(total));
				
				Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
				// if button is clicked, close the custom dialog
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	 
				dialog.show();
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_nova_kartica_kupca_4, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		saleOrderId = getArguments().getInt("saleOrderId", -1);
		customerId = getArguments().getInt("customerId", -1);
		appVersion = getArguments().getString("appVersion", null);
		
		getCustomerData(customerId);
		
		salesOptions = getResources().getStringArray(R.array.slc1_array);
		orderConditionStatusOptions = getResources().getStringArray(R.array.order_condition_status_array);
		financialControlStatusOptions = getResources().getStringArray(R.array.financial_control_status_array);
		orderShipmentStatusOptions = getResources().getStringArray(R.array.order_status_for_shipment_array);
		orderValueStatusOptions = getResources().getStringArray(R.array.order_value_status_array);
		
		llSacuvaj = (LinearLayout) view.findViewById(R.id.llSacuvaj);
		llSacuvaj.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					updateSaleOrder();
					((NovaKarticaKupcaMasterActivity) getActivity()).showToast("Dokument uspešno sačuvan");
					//getActivity().finish();
				} catch (SaleOrderValidationException e) {
					DialogUtil.showInfoDialog(getActivity(), "Greška", e.getMessage());
				}
			}
		});
		
		llVerifikuj = (LinearLayout) view.findViewById(R.id.llVerifikuj);
		llVerifikuj.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					updateSaleOrder();
					verifySaleOrder();
				} catch (SaleOrderValidationException e) {
					DialogUtil.showInfoDialog(getActivity(), "Greška", e.getMessage());
				}
			}
		});
		
		llPosalji = (LinearLayout) view.findViewById(R.id.llPosalji);
		llPosalji.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					updateSaleOrder();
					if (mTipDokumenta.getSelectedItemPosition() == 0) {
						if (mVrstaProdaje.getSelectedItemPosition() == 1) {
							telefonskiPozivDijalog(1);
						} else if (mVrstaProdaje.getSelectedItemPosition() == 2) {
							telefonskiPozivDijalog(2);
						} else {
							sendSaleOrder(0);
						}
					} else {
						sendSaleOrder(0);
					}
				} catch (SaleOrderValidationException e) {
					DialogUtil.showInfoDialog(getActivity(), "Greška", e.getMessage());
				}
			}
		});
		
		// KUPAC
		mKupac = (EditText) view.findViewById(R.id.edit_sale_order_customer);
		// BROJ DOKUMENTA
		mBrojDokumenta = (EditText) view.findViewById(R.id.edit_sale_order_document_no);
		// POSLOVNA JEDINICA
		mPoslovnaJedinica = (EditText) view.findViewById(R.id.edit_sale_order_business_unit_value);
		
		// TIP DOKUMENTA
		tipDokumentaAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sale_order_block_status_array, android.R.layout.simple_spinner_item);
		tipDokumentaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mTipDokumenta = (Spinner) view.findViewById(R.id.edit_sale_order_dokument_type_spinner);
		mTipDokumenta.setAdapter(tipDokumentaAdapter);
		// VRSTA PRODAJE
		vrstaProdajeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.slc1_type_array, android.R.layout.simple_spinner_item);
		vrstaProdajeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mVrstaProdaje = (Spinner) view.findViewById(R.id.edit_sale_order_slc1_spinner);
		mVrstaProdaje.setAdapter(vrstaProdajeAdapter);
		// NACIN OBRADE
		nacinObradeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.backorder_type_array, android.R.layout.simple_spinner_item);
		nacinObradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mNacinObrade = (Spinner) view.findViewById(R.id.edit_sale_order_backorder_spinner);
		mNacinObrade.setAdapter(nacinObradeAdapter);
		// NACIN ISPORUKE
		nacinIsporukeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.shipment_method_code_array_titles, android.R.layout.simple_spinner_item);
		nacinIsporukeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    mNacinIsporuke = (Spinner) view.findViewById(R.id.edit_sale_order_shipment_method_code);
	    mNacinIsporuke.setAdapter(nacinIsporukeAdapter);
	    
	    // KONTAKT
	    mKontakt = (Button) view.findViewById(R.id.edit_sale_order_contact_spinner);
	    mKontakt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onSelectContact();
			}
		});
	    // IME KONTAKTA
	    mImeKontakta = (EditText) view.findViewById(R.id.edit_sale_order_contact_name_text);
	    mImeKontakta.setFilters( new InputFilter[] { new InputFilter.LengthFilter(50)} );
	    // TELEFON KONTAKTA
	    mTelefonKontakta = (EditText) view.findViewById(R.id.edit_sale_order_contact_phone_text);
	    mTelefonKontakta.setFilters( new InputFilter[] { new InputFilter.LengthFilter(30)} );
	    // EMAIL ADRESE KONTAKTA
	    mEmailAdreseKontakta = (EditText) view.findViewById(R.id.edit_sale_order_contact_email_text);
	    mEmailAdreseKontakta.setFilters( new InputFilter[] { new InputFilter.LengthFilter(200)} );
	    
	    // SIFRA LOKACIJE
	    sifraLokacijeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.location_type_array, android.R.layout.simple_spinner_item);
	    sifraLokacijeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    mSifraLokacije = (Spinner) view.findViewById(R.id.edit_sale_order_location_spinner);
	    mSifraLokacije.setAdapter(sifraLokacijeAdapter);
	    // PLACANJE
	    placanjeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.payment_value_array, android.R.layout.simple_spinner_item);
	    placanjeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    mPlacanje = (Spinner) view.findViewById(R.id.edit_sale_order_payment_type_spinner);
	    mPlacanje.setAdapter(placanjeAdapter);
	    // SAKRIJ POPUST
	    mSakrijPopust = (CheckBox) view.findViewById(R.id.edit_sale_order_hide_discount_check_box);
	    // DEKLARACIJA
	    mDeklaracija = (CheckBox) view.findViewById(R.id.edit_sale_order_show_declaration_check_box);
	    // BROJ PORUDZBINE
	    mBrojPorudzbine = (EditText) view.findViewById(R.id.edit_sale_order_quote_no_edit_text);
	    mBrojPorudzbine.setFilters( new InputFilter[] { new InputFilter.LengthFilter(20)} );
	    
	    // ADRESA ISPORUKE
	    mAdresaIsporuke = (Button) view.findViewById(R.id.edit_sale_order_shipping_address_spinner);
	    mAdresaIsporuke.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback2.onSelectShippingAddress();
			}
		});
	    // ADRESA
	    mAdresaIsporukeAdresa = (EditText) view.findViewById(R.id.edit_sale_order_address_value);
	    // POSTANSKI BROJ
	    mAdresaIsporukePostanskiBroj = (EditText) view.findViewById(R.id.edit_sale_order_zip);
	    // GRAD
	    mAdresaIsporukeGrad = (EditText) view.findViewById(R.id.edit_sale_order_city);
	    // KONTAKT
	    mAdresaIsporukeKontakt = (EditText) view.findViewById(R.id.edit_sale_order_address_contact_value);
	    
	    // KRAJNJI KUPAC
	    transitCustomerAutoCompleteAdapter = new CustomerAutocompleteCursorAdapter(getActivity(), null);
	    mKrajnjiKupac = (AutoCompleteTextView) view.findViewById(R.id.edit_sale_order_transit_customer_value);
	    mKrajnjiKupac.setAdapter(transitCustomerAutoCompleteAdapter);
	    mKrajnjiKupac.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Cursor cursor = (Cursor) transitCustomerAutoCompleteAdapter.getItem(position);
				transitCustomerId = cursor.getInt(0);
			}
		});
	    // STATUS USLOVA PORUDZBINE
	    statusUslovaPorudzbineAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.order_condition_status_array, android.R.layout.simple_spinner_item);
	    statusUslovaPorudzbineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    mStatusUslovaPorudzbine = (Spinner) view.findViewById(R.id.edit_sale_order_order_condition_status_spinner);
	    mStatusUslovaPorudzbine.setAdapter(statusUslovaPorudzbineAdapter);
	    
	    // PORUKA ZA CENTRALU
	    mPorukaZaCentralu = (EditText) view.findViewById(R.id.edit_sale_order_headquarters_note_value);
	    mPorukaZaCentralu.setFilters( new InputFilter[] { new InputFilter.LengthFilter(300)} );
	    // PORUKA NA DOKUMENTU
	    mPorukaNaDokumentu = (EditText) view.findViewById(R.id.edit_sale_order_document_note_value);
	    mPorukaNaDokumentu.setFilters( new InputFilter[] { new InputFilter.LengthFilter(300)} );
	    
	    // FINANSIJSKI KONTROLNI STATUS
	    mFinansijskiKontrolniStatus = (TextView) view.findViewById(R.id.edit_sale_order_financial_control_status_text);
	    // STATUS PORUDZBINE ZA ISPORUKU
	    mStatusPorudzbineZaIsporuku = (TextView) view.findViewById(R.id.edit_sale_order_order_status_for_shipment_text);
	    // VREDNOST PORUDZBINE STATUS
	    mVrednostPorudzbineStatus = (TextView) view.findViewById(R.id.edit_sale_order_order_value_status_text);
	    
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mCallback = (NovaKKFragment4ContactListener) activity;
		mCallback2 = (NovaKKFragment4ShippingAddressListener) activity;
		mCallback3 = (NovaKKFragment4SaleOrderUpdated) activity;
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	IntentFilter intentFilter = new IntentFilter(MobileDeviceSalesDocumentSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, intentFilter);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
    }

	private void setContactUIData(int contactId, String contactName, String contactNo, String contactPhone, String contactEmail) {
		selectedContactId = contactId;
		
		if (contactId != -1) {
			StringBuilder sb = new StringBuilder();
			if (contactNo != null && contactNo.length() > 0) {
				sb.append(contactNo);
				sb.append(" - ");
			}
			if (contactName != null && contactName.length() > 0) {
				sb.append(contactName);
			}
			mKontakt.setText(sb.toString());
			if (contactName != null && contactName.length() > 0) {
				mImeKontakta.setText(contactName);
			}
			if (contactPhone != null && contactPhone.length() > 0) {
				mTelefonKontakta.setText(contactPhone);
			}
			if (contactEmail != null && contactEmail.length() > 0) {
				mEmailAdreseKontakta.setText(contactEmail);
			}
		} else {
			mKontakt.setText(getResources().getString(R.string.customer_contact_default_text));
			mImeKontakta.setText("");
			mTelefonKontakta.setText("");
			mEmailAdreseKontakta.setText("");
		}
	}
	
	private void setShippingAddressUIData(int addressId, String address, 
			String addressNo, String city, String postCode, String phoneNo, String contact) {
		selectedShippingAddress = addressId;
		
		if (addressId != -1) {
			StringBuilder sb = new StringBuilder();
			if (addressNo != null && addressNo.length() > 0) {
				sb.append(addressNo);
				sb.append(" - ");
			}
			if (address != null && address.length() > 0) {
				sb.append(address);
			}
			mAdresaIsporuke.setText(sb.toString());
			if (address != null && address.length() > 0) {
				mAdresaIsporukeAdresa.setText(address);
			}
			if (city != null && city.length() > 0) {
				mAdresaIsporukeGrad.setText(city);
			}
			if (postCode != null && postCode.length() > 0) {
				mAdresaIsporukePostanskiBroj.setText(postCode);
			}
			sb = new StringBuilder();
			if (contact != null && contact.length() > 0) {
				sb.append(contact);
				sb.append(" - ");
			}
			if (phoneNo != null && phoneNo.length() > 0) {
				sb.append(phoneNo);
			}
			mAdresaIsporukeKontakt.setText(sb.toString());
		} else {
			mAdresaIsporuke.setText(getResources().getString(R.string.customer_address_default_text));
			mAdresaIsporukeAdresa.setText(defaultShippingAddress);
			mAdresaIsporukeGrad.setText(defaultShippingCity);
			mAdresaIsporukePostanskiBroj.setText(defaultShippingPostCode);
			mAdresaIsporukeKontakt.setText(defaultShippingPhone);
		}
	}
	
	private void loadUIData(Cursor cursor) {
		
		if (customerNo != null && customerName != null) {
			mKupac.setText(String.format("%s - %s", customerNo, customerName));
		}
		
		if (!cursor.isNull(SaleOrderQuery.SALES_ORDER_DEVICE_NO)) {
			mBrojDokumenta.setText(cursor.getString(SaleOrderQuery.SALES_ORDER_DEVICE_NO));
		}
		
		if (!cursor.isNull(SaleOrderQuery.CUSTOMER_BUSINESS_UNIT_CODE)) {
			String businessUnitNo = cursor.getString(SaleOrderQuery.CUSTOMER_BUSINESS_UNIT_CODE);
			if (!businessUnitNo.equals("")) {
				Cursor businessUnitCursor = getActivity().getContentResolver().query(CustomerBusinessUnits.CONTENT_URI, new String[] { CustomerBusinessUnits.ADDRESS, CustomerBusinessUnits.CITY }, CustomerBusinessUnits.UNIT_NO + "=?", new String[] { businessUnitNo }, null);
				if (businessUnitCursor.moveToFirst()) {
					mPoslovnaJedinica.setText(String.format("%s - %s, %s", businessUnitNo, businessUnitCursor.getString(0), businessUnitCursor.getString(1)));
				}
			} else {
				mPoslovnaJedinica.setText(getString(R.string.nemaPoslovnuJedinicu));
			}
		} else if (hasBusinessUnits == 0) {
			mPoslovnaJedinica.setText(getString(R.string.nemaPoslovnuJedinicu));
		}
		
		if (!cursor.isNull(SaleOrderQuery.DOCUMENT_TYPE)) {
			mTipDokumenta.setSelection(cursor.getInt(SaleOrderQuery.DOCUMENT_TYPE));
		}
		
		if (!cursor.isNull(SaleOrderQuery.SHORTCUT_DIMENSION_1_CODE)) {
			ArrayList<String> vrstaProdajeLista = new ArrayList<String>(Arrays.asList(salesOptions));
			mVrstaProdaje.setSelection(vrstaProdajeLista.indexOf(cursor.getString(SaleOrderQuery.SHORTCUT_DIMENSION_1_CODE)));
		}
		
		if (!cursor.isNull(SaleOrderQuery.BACKORDER_SHIPMENT_STATUS)) {
			mNacinObrade.setSelection(cursor.getInt(SaleOrderQuery.BACKORDER_SHIPMENT_STATUS));
		}
		
		if (!cursor.isNull(SaleOrderQuery.SHIPMENT_METHOD_CODE)) {
			ArrayList<String> nacinIsporukeLista = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.shipment_method_code_array_values)));
			mNacinIsporuke.setSelection(nacinIsporukeLista.indexOf(cursor.getString(SaleOrderQuery.SHIPMENT_METHOD_CODE)));
		}
		
		if (!cursor.isNull(SaleOrderQuery.CONTACT_ID)) {
			onContactSelected(cursor.getInt(SaleOrderQuery.CONTACT_ID));
		} else {
			onContactSelected(-1);
		}
		
		if (!cursor.isNull(SaleOrderQuery.LOCATION_CODE)) {
			ArrayList<String> lokacijaLista = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.location_type_array)));
			mSifraLokacije.setSelection(lokacijaLista.indexOf(cursor.getString(SaleOrderQuery.LOCATION_CODE)));
		}
		
		if (!cursor.isNull(SaleOrderQuery.PAYMENT_OPTION)) {
			ArrayList<String> placanjeLista = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.payment_type_array)));
			mPlacanje.setSelection(placanjeLista.indexOf(cursor.getString(SaleOrderQuery.PAYMENT_OPTION)));
		}
		
		if (!cursor.isNull(SaleOrderQuery.HIDE_REBATE)) {
			mSakrijPopust.setChecked(cursor.getInt(SaleOrderQuery.HIDE_REBATE) == 1 ? true : false);
		}
		
		if (!cursor.isNull(SaleOrderQuery.FURTHER_SALE)) {
			mDeklaracija.setChecked(cursor.getInt(SaleOrderQuery.FURTHER_SALE) == 1 ? true : false);
		}
		
		if (!cursor.isNull(SaleOrderQuery.EXTERNAL_DOCUMENT_NO)) {
			mBrojPorudzbine.setText(cursor.getString(SaleOrderQuery.EXTERNAL_DOCUMENT_NO));
		}
		
		if (!cursor.isNull(SaleOrderQuery.SHIPP_TO_ADDRESS_ID)) {
			onAddressSelected(cursor.getInt(SaleOrderQuery.SHIPP_TO_ADDRESS_ID));
		} else {
			onAddressSelected(-1);
		}
		
		if (!cursor.isNull(SaleOrderQuery.CUST_USES_TRANSIT_CUST)) {
			transitCustomerId = cursor.getInt(SaleOrderQuery.CUST_USES_TRANSIT_CUST);
			Cursor customerCursor = getActivity().getContentResolver().query(MobileStoreContract.Customers.buildCustomersUri(String.valueOf(transitCustomerId)), CustomersQuery.PROJECTION, null, null, null);
			if (customerCursor.moveToFirst()) {
				mKrajnjiKupac.setText(String.format("%s - %s", customerCursor.getString(CustomersQuery.CUSTOMER_NO), customerCursor.getString(CustomersQuery.NAME)));
			}
		}
		
		if (!cursor.isNull(SaleOrderQuery.ORDER_CONDITION_STATUS)) {
			mStatusUslovaPorudzbine.setSelection(cursor.getInt(SaleOrderQuery.ORDER_CONDITION_STATUS));
		} else {
			mStatusUslovaPorudzbine.setSelection(0);
		}
		
		if (!cursor.isNull(SaleOrderQuery.NOTE1)) {
			mPorukaNaDokumentu.setText(cursor.getString(SaleOrderQuery.NOTE1));
		}
		
		if (!cursor.isNull(SaleOrderQuery.NOTE2)) {
			mPorukaZaCentralu.setText(cursor.getString(SaleOrderQuery.NOTE2));
		}
		
		if (!cursor.isNull(SaleOrderQuery.FIN_CONTROL_STATUS)) {
			mFinansijskiKontrolniStatus.setText(financialControlStatusOptions[cursor.getInt(SaleOrderQuery.FIN_CONTROL_STATUS)]);
		} else {
			mFinansijskiKontrolniStatus.setText("-");
		}
		
		if (!cursor.isNull(SaleOrderQuery.ORDER_STATUS_FOR_SHIPMENT)) {
			mStatusPorudzbineZaIsporuku.setText(orderShipmentStatusOptions[cursor.getInt(SaleOrderQuery.ORDER_STATUS_FOR_SHIPMENT)]);
		} else {
			mStatusPorudzbineZaIsporuku.setText("-");
		}
		
		if (!cursor.isNull(SaleOrderQuery.ORDER_VALUE_STATUS)) {
			mVrednostPorudzbineStatus.setText(orderValueStatusOptions[cursor.getInt(SaleOrderQuery.ORDER_VALUE_STATUS)]);
		} else {
			mVrednostPorudzbineStatus.setText("-");
		}
		
	}
	
	private ContentValues getInputData() throws SaleOrderValidationException {
		ContentValues localValues = new ContentValues();
		
		int document_type = mTipDokumenta.getSelectedItemPosition();
		localValues.put(MobileStoreContract.SaleOrders.DOCUMENT_TYPE, Integer.valueOf(document_type));
		
		if (isPotentialCustomer(customerId)) {
			if (document_type != 1) {
				throw new SaleOrderValidationException("Potencijalni kupac može biti izabran samo na ponudi!");
			}
		}
		
		localValues.put(MobileStoreContract.SaleOrders.SHORTCUT_DIMENSION_1_CODE, salesOptions[mVrstaProdaje.getSelectedItemPosition()]);
		
		int backorder_type = mNacinObrade.getSelectedItemPosition();
		if (backorder_type == 0) {
			throw new SaleOrderValidationException("Način obrade nije izabran!");
		}
		localValues.put(MobileStoreContract.SaleOrders.BACKORDER_SHIPMENT_STATUS, Integer.valueOf(backorder_type));
		
		localValues.put(MobileStoreContract.SaleOrders.SHIPMENT_METHOD_CODE, getResources().getStringArray(R.array.shipment_method_code_array_values)[mNacinIsporuke.getSelectedItemPosition()]);
		
		if (selectedContactId != -1) {
			localValues.put(MobileStoreContract.SaleOrders.CONTACT_ID, Integer.valueOf(selectedContactId));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.CONTACT_ID);
		}
		
		String contact_name = mImeKontakta.getText().toString().trim();
		if (contact_name != null && !contact_name.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.CONTACT_NAME, contact_name);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.CONTACT_NAME);
		}
		
		String contact_phone = mTelefonKontakta.getText().toString().trim();
		if (contact_phone != null && !contact_phone.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.CONTACT_PHONE, contact_phone);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.CONTACT_PHONE);
		}
		
		String contact_email = mEmailAdreseKontakta.getText().toString().trim().replace(";", ",");
		if (contact_email != null && !contact_email.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.CONTACT_EMAIL, contact_email);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.CONTACT_EMAIL);
		}
		
		localValues.put(MobileStoreContract.SaleOrders.LOCATION_CODE, mSifraLokacije.getSelectedItem().toString());
		
		String[] pay = getResources().getStringArray(R.array.payment_type_array);
		localValues.put(MobileStoreContract.SaleOrders.PAYMENT_OPTION, pay[mPlacanje.getSelectedItemPosition()]);
		
		localValues.put(MobileStoreContract.SaleOrders.HIDE_REBATE, mSakrijPopust.isChecked() ? 1 : 0);
		
		localValues.put(MobileStoreContract.SaleOrders.FURTHER_SALE, mDeklaracija.isChecked() ? 1 : 0);
		
		String order_no = mBrojPorudzbine.getText().toString().trim();
		if (order_no != null && !order_no.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.EXTERNAL_DOCUMENT_NO, order_no);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.EXTERNAL_DOCUMENT_NO);
		}
		
		if (selectedShippingAddress != -1) {
			localValues.put(MobileStoreContract.SaleOrders.SHIPP_TO_ADDRESS_ID, Integer.valueOf(selectedShippingAddress));
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.SHIPP_TO_ADDRESS_ID);
		}
		
		if (transitCustomerId != -1) {
			localValues.put(MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST, Integer.valueOf(transitCustomerId));
		}  else {
			localValues.putNull(MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST);
		}
		
		localValues.put(MobileStoreContract.SaleOrders.ORDER_CONDITION_STATUS, Integer.valueOf(mStatusUslovaPorudzbine.getSelectedItemPosition()));
		
		String doc_note = mPorukaNaDokumentu.getText().toString().trim();
		if (doc_note != null && !doc_note.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.NOTE1, doc_note);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.NOTE1);
		}
		
		String headq_note = mPorukaZaCentralu.getText().toString().trim();
		if (headq_note != null && !headq_note.equals("")) {
			localValues.put(MobileStoreContract.SaleOrders.NOTE2, headq_note);
		} else {
			localValues.putNull(MobileStoreContract.SaleOrders.NOTE2);
		}
		
		localValues.putNull(MobileStoreContract.SaleOrders.REQUESTED_DELIVERY_DATE);
		
		return localValues;
	}
	
	private void updateSaleOrder() throws SaleOrderValidationException {
		getActivity().getContentResolver().update(SaleOrders.buildSaleOrderUri(String.valueOf(saleOrderId)), getInputData(), null, null);
		
		// OBAVEZNO JE AZURIRATI SVE LINIJE GDE JE BACKORDER STATUS "0" ODABRANIM STATUSOM SA ZAGLAVLJA
		Cursor cursor = getActivity().getContentResolver().query(SaleOrderLines.buildSaleOrderLinesUri(String.valueOf(saleOrderId)), new String[] { Tables.SALE_ORDER_LINES + "." + SaleOrderLines._ID }, Tables.SALE_ORDER_LINES + "." + SaleOrderLines.BACKORDER_STATUS + "=?", new String[] { String.valueOf(0) }, null);
		int lineId;
		while (cursor.moveToNext()) {
			ContentValues cv = new ContentValues();
			lineId = cursor.getInt(0);
			cv.put(SaleOrderLines.BACKORDER_STATUS, mNacinObrade.getSelectedItemPosition());
			getActivity().getContentResolver().update(SaleOrderLines.CONTENT_URI, cv, Tables.SALE_ORDER_LINES + "." + SaleOrderLines._ID + "=?", new String[] { String.valueOf(lineId) });
		}
		cursor.close();
		mCallback3.onSaleOrderUpdated();
	}
	
	private void verifySaleOrder() {
		Intent verifyIntent = new Intent(getActivity(), NavisionSyncService.class);
		MobileDeviceSalesDocumentSyncObject verifymobileDeviceSalesDocumentSyncObject = new MobileDeviceSalesDocumentSyncObject(saleOrderId, VERIFY_SALE_DOC, appVersion, 0);
		verifyIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, verifymobileDeviceSalesDocumentSyncObject);
		getActivity().startService(verifyIntent);
		sendSaleOrderProgressDialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.dialog_title_sale_order_verify), getResources().getString(R.string.dialog_body_sale_order_verify), true, true);
	}
	
	private void sendSaleOrder(int vreme) {
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		updateOrderDate();
		MobileDeviceSalesDocumentSyncObject mobileDeviceSalesDocumentSyncObject = new MobileDeviceSalesDocumentSyncObject(Integer.valueOf(saleOrderId), SAVE_SALE_DOC, appVersion, vreme);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, mobileDeviceSalesDocumentSyncObject);
		getActivity().startService(intent);
		sendSaleOrderProgressDialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.dialog_title_sale_order_send), getResources().getString(R.string.dialog_body_sale_order_send), true, true);
	}
	
	private void updateOrderDate() {
		ContentValues cv = new ContentValues();
		cv.put(SaleOrders.ORDER_DATE, DateUtils.toDbDate(new Date()));
		getActivity().getContentResolver().update(MobileStoreContract.SaleOrders.buildSaleOrderUri(String.valueOf(saleOrderId)), cv, null, null);
	}
	
	private boolean isPotentialCustomer(int customerId) {
		Cursor potentialCustomerCursor = getActivity().getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, new String[] {Customers.CONTACT_COMPANY_NO}, "("+Customers.CONTACT_COMPANY_NO + " is null or " + Customers.CONTACT_COMPANY_NO + "='')" + " and " + Customers._ID + "=?" , new String[] { String.valueOf(customerId) }, null);
		if (potentialCustomerCursor.moveToFirst()) {
			return true;
		}
		potentialCustomerCursor.close();
		return false;
	}
	
	private void getCustomerData(int customerId) {
		Cursor cursor = getActivity().getContentResolver().query(Customers.buildCustomersUri(String.valueOf(customerId)), CustomersQuery.PROJECTION, null, null, null);
		if (cursor.moveToNext()) {
			customerNo = cursor.getString(CustomersQuery.CUSTOMER_NO);
			customerName = cursor.getString(CustomersQuery.NAME);
			defaultShippingAddress = cursor.getString(CustomersQuery.ADDRESS);
			defaultShippingCity = cursor.getString(CustomersQuery.CITY);
			defaultShippingPhone = cursor.getString(CustomersQuery.PHONE);
			defaultShippingPostCode = cursor.getString(CustomersQuery.POST_CODE);
			hasBusinessUnits = cursor.getInt(CustomersQuery.HAS_BUSINESS_UNITS);
		} else {
			LogUtils.LOGE(TAG, "NO CUSTOMER");
		}
		cursor.close();
	}
	
	public void telefonskiPozivDijalog(int tip) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_telefonski_poziv);
		
		final String[] types = getResources().getStringArray(R.array.slc1_type_array);
		final RadioGroup rgTelefonskiPoziv = (RadioGroup) dialog.findViewById(R.id.rgTelefonskiPoziv);
		final EditText dialog_trajanje_poziva_input = (EditText) dialog.findViewById(R.id.dialog_trajanje_poziva_input);
		Button dialogButtonOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
		
		dialogButtonOK.setOnClickListener(new OnClickListener() {
			
			int timeOnthePhone;
			
			@Override
			public void onClick(View v) {
				String input = dialog_trajanje_poziva_input.getText().toString();
				if (input.trim().length() > 0) {
					try {
						timeOnthePhone = Integer.parseInt(input);
					} catch (Exception e) {
						Toast.makeText(getActivity(), R.string.datum_format, Toast.LENGTH_SHORT).show();
					}
				} else {
					switch (rgTelefonskiPoziv.getCheckedRadioButtonId()) {
					case R.id.radio5:
						timeOnthePhone = 5;
						break;
					case R.id.radio10:
						timeOnthePhone = 10;
						break;
					case R.id.radio15:
						timeOnthePhone = 15;				
						break;
					case R.id.radio30:
						timeOnthePhone = 30;
						break;
					case R.id.radio45:
						timeOnthePhone = 45;
						break;
					default:
						break;
					}
				}
				
				if (timeOnthePhone > 0) {
					dialog.dismiss();
					sendSaleOrder(timeOnthePhone);
				} else {
					Toast.makeText(getActivity(), R.string.datum_format, Toast.LENGTH_SHORT).show();
					dialog_trajanje_poziva_input.setText("");
					dialog_trajanje_poziva_input.requestFocus();
				}
				
			}
		});
		
		switch (tip) {
		case 1:
			dialog.setTitle(types[1]);
			break;
		case 2:
			dialog.setTitle(types[2]);
			break;
		default:
			break;
		}
		
		dialog.show();
	}
	
	public void onContactSelected(int contact_id) {
		if (contact_id != -1) {
			Cursor contactCursor = getActivity().getContentResolver().query(MobileStoreContract.Contacts.CONTENT_URI, ContactQuery.PROJECTION, Tables.CONTACTS + "._id=?", new String[] { String.valueOf(contact_id)  }, null);
			if (contactCursor != null && contactCursor.moveToFirst()) {
				
				String name = contactCursor.getString(ContactQuery.NAME);
				String contact_no = contactCursor.getString(ContactQuery.CONTACT_NO);
				String email = contactCursor.getString(ContactQuery.EMAIL);
				String phone = contactCursor.getString(ContactQuery.PHONE);
				
				setContactUIData(contact_id, name, contact_no, phone, email);
			}
			contactCursor.close();
		} else {
			setContactUIData(-1, null, null, null, null);
		}
	}
	
	public void onAddressSelected(int customerAddressId) {
		if (customerAddressId != -1) {
			Cursor addressCursor = getActivity().getContentResolver().query(MobileStoreContract.CustomerAddresses.CONTENT_URI, CustomerAddressQuery.PROJECTION, Tables.CUSTOMER_ADDRESSES + "._id=?", new String[] { String.valueOf(customerAddressId)  }, null);
			if (addressCursor != null && addressCursor.moveToFirst()) {
				String address_no = addressCursor.getString(CustomerAddressQuery.ADDRESS_NO);
				String address = addressCursor.getString(CustomerAddressQuery.ADDRESS);
				String city = addressCursor.getString(CustomerAddressQuery.CITY);
				String post_code = addressCursor.getString(CustomerAddressQuery.POST_CODE);
				String contact = addressCursor.getString(CustomerAddressQuery.CONTANCT);
				String phone_no = addressCursor.getString(CustomerAddressQuery.PHONE_NO);
				
				setShippingAddressUIData(customerAddressId, address, address_no, city, post_code, phone_no, contact);
			}
			addressCursor.close();
		} else {
			setShippingAddressUIData(-1, null, null, null, null, null, null);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		return new CursorLoader(getActivity(), SaleOrders.buildSaleOrderUri(String.valueOf(saleOrderId)), SaleOrderQuery.PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor != null && cursor.moveToFirst()) {
			loadUIData(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	private interface SaleOrderQuery {
		String[] PROJECTION = new String[] { 
			MobileStoreContract.SaleOrders._ID,
			MobileStoreContract.SaleOrders.SALES_PERSON_ID,
			MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO,
			MobileStoreContract.SaleOrders.DOCUMENT_TYPE,
			MobileStoreContract.SaleOrders.CUSTOMER_ID,
			MobileStoreContract.SaleOrders.ORDER_DATE,
			MobileStoreContract.SaleOrders.LOCATION_CODE,
			MobileStoreContract.SaleOrders.SHORTCUT_DIMENSION_1_CODE,
			MobileStoreContract.SaleOrders.CURRENCY_CODE,
			MobileStoreContract.SaleOrders.EXTERNAL_DOCUMENT_NO,
			MobileStoreContract.SaleOrders.QUOTE_NO,
			MobileStoreContract.SaleOrders.BACKORDER_SHIPMENT_STATUS,
			MobileStoreContract.SaleOrders.ORDER_STATUS_FOR_SHIPMENT,
			MobileStoreContract.SaleOrders.FIN_CONTROL_STATUS,
			MobileStoreContract.SaleOrders.ORDER_CONDITION_STATUS,
			MobileStoreContract.SaleOrders.USED_CREDIT_LIMIT_BY_EMPLOYEE,
			MobileStoreContract.SaleOrders.ORDER_VALUE_STATUS,
			MobileStoreContract.SaleOrders.QUOTE_REALIZED_STATUS,
			MobileStoreContract.SaleOrders.SPECIAL_QUOTE,
			MobileStoreContract.SaleOrders.QUOTE_VALID_DATE_TO,
			MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST,
			MobileStoreContract.SaleOrders.SELL_TO_ADDRESS_ID,
			MobileStoreContract.SaleOrders.SHIPP_TO_ADDRESS_ID,
			MobileStoreContract.SaleOrders.REQUESTED_DELIVERY_DATE,
			MobileStoreContract.SaleOrders.CONTACT_ID,
			MobileStoreContract.SaleOrders.CONTACT_NAME,
			MobileStoreContract.SaleOrders.CONTACT_PHONE,
			MobileStoreContract.SaleOrders.CONTACT_EMAIL,
			MobileStoreContract.SaleOrders.PAYMENT_OPTION,
			MobileStoreContract.SaleOrders.CHECK_STATUS_PHONE,
			MobileStoreContract.SaleOrders.TOTAL,
			MobileStoreContract.SaleOrders.TOTAL_DISCOUNT,
			MobileStoreContract.SaleOrders.TOTAL_PDV,
			MobileStoreContract.SaleOrders.TOTAL_ITEMS,
			MobileStoreContract.SaleOrders.HIDE_REBATE,
			MobileStoreContract.SaleOrders.FURTHER_SALE,
			MobileStoreContract.SaleOrders.NOTE1,
			MobileStoreContract.SaleOrders.NOTE2,
			MobileStoreContract.SaleOrders.NOTE3,
			MobileStoreContract.SaleOrders.SHIPMENT_METHOD_CODE, 
			MobileStoreContract.SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE
		};
		
		int _ID = 0;
		int SALES_PERSON_ID = 1;
		int SALES_ORDER_DEVICE_NO = 2;
		int DOCUMENT_TYPE = 3;
		int CUSTOMER_ID = 4;
		int ORDER_DATE = 5;
		int LOCATION_CODE = 6;
		int SHORTCUT_DIMENSION_1_CODE = 7;
		int CURRENCY_CODE = 8;
		int EXTERNAL_DOCUMENT_NO = 9;
		int QUOTE_NO = 10;
		int BACKORDER_SHIPMENT_STATUS = 11;
		int ORDER_STATUS_FOR_SHIPMENT = 12;
		int FIN_CONTROL_STATUS = 13;
		int ORDER_CONDITION_STATUS = 14;
		int USED_CREDIT_LIMIT_BY_EMPLOYEE = 15;
		int ORDER_VALUE_STATUS = 16;
		int QUOTE_REALIZED_STATUS = 17;
		int SPECIAL_QUOTE = 18;
		int QUOTE_VALID_DATE_TO = 19;
		int CUST_USES_TRANSIT_CUST = 20;
		int SELL_TO_ADDRESS_ID = 21;
		int SHIPP_TO_ADDRESS_ID = 22;
		int REQUESTED_DELIVERY_DATE = 23;
		int CONTACT_ID = 24;
		int CONTACT_NAME = 25;
		int CONTACT_PHONE = 26;
		int CONTACT_EMAIL = 27;
		int PAYMENT_OPTION = 28;
		int CHECK_STATUS_PHONE = 29;
		int TOTAL = 30;
		int TOTAL_DISCOUNT = 31;
		int TOTAL_PDV = 32;
		int TOTAL_ITEMS = 33;
		int HIDE_REBATE = 34;
		int FURTHER_SALE = 35;
		int NOTE1 = 36;
		int NOTE2 = 37;
		int NOTE3 = 38;
		int SHIPMENT_METHOD_CODE = 39;
		int CUSTOMER_BUSINESS_UNIT_CODE = 40;
	}
	
	private interface CustomersQuery {
		String[] PROJECTION = new String[] { 
			MobileStoreContract.Customers._ID,
			MobileStoreContract.Customers.CUSTOMER_NO,
			MobileStoreContract.Customers.NAME,
			MobileStoreContract.Customers.PRIMARY_CONTACT_ID,
			MobileStoreContract.Customers.PHONE,
			MobileStoreContract.Customers.CITY,
			MobileStoreContract.Customers.ADDRESS,
			MobileStoreContract.Customers.POST_CODE,
			MobileStoreContract.Customers.CONTACT_COMPANY_NO,
			MobileStoreContract.Customers.EMAIL, 
			MobileStoreContract.Customers.HAS_BUSINESS_UNITS
		};
		
		int _ID = 0;
		int CUSTOMER_NO = 1;
		int NAME = 2;
		int PRIMARY_CONTACT_ID = 3;
		int PHONE = 4;
		int CITY = 5;
		int ADDRESS = 6;
		int POST_CODE = 7;
		int CONTACT_COMPANY_NO = 8;
		int EMAIL = 9;
		int HAS_BUSINESS_UNITS = 10;
	}
	
	private interface ContactQuery {

		String[] PROJECTION = { BaseColumns._ID,
				MobileStoreContract.Contacts.NAME,
				MobileStoreContract.Contacts.CONTACT_NO,
				MobileStoreContract.Contacts.EMAIL,
				MobileStoreContract.Contacts.PHONE
				};

//		int _ID = 0;
		int NAME = 1;
		int CONTACT_NO = 2;
		int EMAIL = 3;
		int PHONE = 4;
	}
	
	private interface CustomerAddressQuery {

		String[] PROJECTION = { BaseColumns._ID,
				MobileStoreContract.CustomerAddresses.ADDRESS_NO,
				MobileStoreContract.CustomerAddresses.ADDRESS,
				MobileStoreContract.CustomerAddresses.CITY,
				MobileStoreContract.CustomerAddresses.POST_CODE,
				MobileStoreContract.CustomerAddresses.CONTANCT,
				MobileStoreContract.CustomerAddresses.PHONE_NO };

//		int _ID = 0;
		int ADDRESS_NO = 1;
		int ADDRESS = 2;
		int CITY = 3;
		int POST_CODE = 4;
		int CONTANCT = 5;
		int PHONE_NO = 6;
	}

}
