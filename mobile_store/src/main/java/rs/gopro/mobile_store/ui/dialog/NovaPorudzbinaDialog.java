package rs.gopro.mobile_store.ui.dialog;

import java.util.ArrayList;
import java.util.Arrays;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerBusinessUnits;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class NovaPorudzbinaDialog extends DialogFragment implements BusinessUnitSelectDialog.BusinessUnitSelectDialogListener {

	private AutoCompleteTextView dialog_nova_porudzbina_kupac;
	private Button dialog_nova_porudzbina_bu;
	private ArrayAdapter<CharSequence> salesAdapter;
	private Spinner dialog_nova_porudzbina_vp;
	private Button dialogButtonOK;
	
	private int selectedCustomerId, selectedBusinessUnitId, hasBusinessUnits;
	private String selectedCustomerNo, selectedPotentialCustomerNo, selectedBusinessUnitNo, branchCode, buttonText, salesTypeValue;
	private boolean newSaleOrder = true;
	
	private CustomerAutocompleteCursorAdapter customerCursorAdapter;
	
	public NovaPorudzbinaDialog() {
		super();
	}
	
	public interface NovaPorudzbinaDialogListener {
		void onNovaPorudzbinaDialogFinished(int customerId, String customerNo, String potentialCustomerNo, String branchCode, int businessUnitId, String businessUnitNo, int salesType, boolean newSaleOrder);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.dialog_nova_porudzbina, container);
		
		if (savedInstanceState != null) {
			selectedCustomerId = savedInstanceState.getInt("selectedCustomerId", -1);
			selectedBusinessUnitId = savedInstanceState.getInt("selectedBusinessUnitId", -1);
			selectedCustomerNo = savedInstanceState.getString("selectedCustomerNo", null);
			selectedPotentialCustomerNo = savedInstanceState.getString("selectedPotentialCustomerNo", null);
			selectedBusinessUnitNo = savedInstanceState.getString("selectedBusinessUnitNo", null);
			branchCode = savedInstanceState.getString("branchCode", null);
			hasBusinessUnits = savedInstanceState.getInt("hasBusinessUnits", 0);
			buttonText = savedInstanceState.getString("buttonText", null);
			newSaleOrder = savedInstanceState.getBoolean("newSaleOrder", true);
		}
		
		dialog_nova_porudzbina_kupac = (AutoCompleteTextView) view.findViewById(R.id.dialog_nova_porudzbina_kupac);
		dialog_nova_porudzbina_bu = (Button) view.findViewById(R.id.dialog_nova_porudzbina_bu);
		dialog_nova_porudzbina_bu.setText(buttonText);
		dialogButtonOK = (Button) view.findViewById(R.id.dialogButtonOK);
		
		salesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.slc1_type_array, android.R.layout.simple_spinner_item);
		salesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dialog_nova_porudzbina_vp = (Spinner) view.findViewById(R.id.dialog_nova_porudzbina_vp);
		dialog_nova_porudzbina_vp.setAdapter(salesAdapter);
		
		int saleOrderId = getArguments().getInt("saleOrderId", -1);
		if (saleOrderId != -1) {
			newSaleOrder = false;
			String customerName = null;
			Cursor cursor = getActivity().getContentResolver().query(SaleOrders.buildSaleOrderUri(String.valueOf(saleOrderId)), new String[] { SaleOrders.CUSTOMER_ID, SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE, SaleOrders.SHORTCUT_DIMENSION_1_CODE }, null, null, null);
			if (cursor.moveToFirst()) {
				selectedCustomerId = cursor.getInt(0);
				selectedBusinessUnitNo = cursor.getString(1);
				salesTypeValue = cursor.getString(2);
				
				cursor = getActivity().getContentResolver().query(Customers.buildCustomersUri(String.valueOf(selectedCustomerId)), new String[] { Customers.CUSTOMER_NO, Customers.CONTACT_COMPANY_NO, Customers.NAME, Customers.GLOBAL_DIMENSION, Customers.HAS_BUSINESS_UNITS }, null, null, null);
				if (cursor.moveToFirst()) {
					selectedCustomerNo = cursor.getString(0);
					selectedPotentialCustomerNo = cursor.getString(1);
					customerName = cursor.getString(2);
					
					branchCode = cursor.getString(3);
					hasBusinessUnits = cursor.getInt(4);
					if (hasBusinessUnits == 0) {
						buttonText = getString(R.string.nemaPoslovnuJedinicu);
						dialog_nova_porudzbina_bu.setText(buttonText);
						dialog_nova_porudzbina_bu.setClickable(false);
					} else if (selectedBusinessUnitNo != null) {
						
						cursor = getActivity().getContentResolver().query(CustomerBusinessUnits.CONTENT_URI, new String[] { CustomerBusinessUnits._ID, CustomerBusinessUnits.ADDRESS, CustomerBusinessUnits.CITY }, CustomerBusinessUnits.UNIT_NO + "=?", new String[] { selectedBusinessUnitNo }, null);
						if (cursor.moveToFirst()) {
							selectedBusinessUnitId = cursor.getInt(0);
							String address = cursor.getString(1);
							String city = cursor.getString(2);
							buttonText = String.format("%s - %s, %s", selectedBusinessUnitNo, address, city);
							dialog_nova_porudzbina_bu.setText(buttonText);
							dialog_nova_porudzbina_bu.setClickable(true);
						}
					} else {
						buttonText = getString(R.string.izaberiPoslovnuJedinicu);
						dialog_nova_porudzbina_bu.setText(buttonText);
						dialog_nova_porudzbina_bu.setClickable(true);
					}
					dialog_nova_porudzbina_kupac.setText(selectedCustomerNo + " - " + customerName);
					dialog_nova_porudzbina_kupac.setFocusable(false);
				}
				ArrayList<String> salesTypeValuesList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.slc1_array)));
				dialog_nova_porudzbina_vp.setSelection(salesTypeValuesList.indexOf(salesTypeValue));
				cursor.close();
			}
		}
		getArguments().putInt("saleOrderId", -1);
		
		customerCursorAdapter = new CustomerAutocompleteCursorAdapter(getActivity(), null);
		dialog_nova_porudzbina_kupac.setAdapter(customerCursorAdapter);
		
		dialog_nova_porudzbina_kupac.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
				Cursor cursor = (Cursor) customerCursorAdapter.getItem(position);
				selectedCustomerId = cursor.getInt(0);
				selectedCustomerNo = cursor.getString(1);
				selectedPotentialCustomerNo = cursor.getString(5);
				
				branchCode = cursor.getString(8);
				hasBusinessUnits = cursor.getInt(9);
				if (hasBusinessUnits == 0) {
					buttonText = getString(R.string.nemaPoslovnuJedinicu);
					dialog_nova_porudzbina_bu.setText(buttonText);
					dialog_nova_porudzbina_bu.setClickable(false);
				} else {
					buttonText = getString(R.string.izaberiPoslovnuJedinicu);
					dialog_nova_porudzbina_bu.setText(buttonText);
					dialog_nova_porudzbina_bu.setClickable(true);
				}
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(dialog_nova_porudzbina_kupac.getWindowToken(), 0);
			}
		});
		
		dialog_nova_porudzbina_bu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialogBusinessUnitKupca();
			}
		});
		
		dialogButtonOK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (selectedCustomerId != 0) {
					if (hasBusinessUnits == 1 && selectedBusinessUnitId == 0) {
						toastError(R.string.obaveznaPoslovnaJedinica);
					} else {
						NovaPorudzbinaDialogListener activity = (NovaPorudzbinaDialogListener) getActivity();
						activity.onNovaPorudzbinaDialogFinished(selectedCustomerId, selectedCustomerNo, selectedPotentialCustomerNo, branchCode, selectedBusinessUnitId, selectedBusinessUnitNo, dialog_nova_porudzbina_vp.getSelectedItemPosition(), newSaleOrder);
						dismiss();
					}
				} else {
					toastError(R.string.kupac_nije_izabran);
				}
			}
		});
		
		if (newSaleOrder) {
			getDialog().setTitle(getString(R.string.menu_nova_specifikacija));
		} else {
			getDialog().setTitle(getString(R.string.contextual_edit_lines));
		}
		
		return view;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putInt("selectedCustomerId", selectedCustomerId);
		outState.putInt("selectedBusinessUnitId", selectedBusinessUnitId);
		outState.putString("selectedCustomerNo", selectedCustomerNo);
		outState.putString("selectedPotentialCustomerNo", selectedPotentialCustomerNo);
		outState.putString("selectedBusinessUnitNo", selectedBusinessUnitNo);
		outState.putString("branchCode", branchCode);
		outState.putInt("hasBusinessUnits", hasBusinessUnits);
		outState.putString("buttonText", buttonText);
		outState.putBoolean("newSaleOrder", newSaleOrder);
	}
	
	private void dialogBusinessUnitKupca() {
		if (selectedCustomerNo == null) {
			toastError(R.string.kupac_nije_izabran);
			return;
		}
		
		BusinessUnitSelectDialog busd = BusinessUnitSelectDialog.newInstance(selectedCustomerNo);
		busd.show(getActivity().getSupportFragmentManager(), "BUSINESS_UNIT_DIALOG");
	}
	
	private void toastError(int resId) {
		Toast toast = Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 100);
		toast.show();
	}

	@Override
	public void onBusinessUnitSelected(int unit_id, String address,
			String unit_no, String unit_name, String city, String post_code,
			String phone_no, String contact) {
		
		selectedBusinessUnitId = unit_id;
		selectedBusinessUnitNo = unit_no;
		
		buttonText = String.format("%s - %s, %s", unit_no, address, city);
		dialog_nova_porudzbina_bu.setText(buttonText);
	}

}
