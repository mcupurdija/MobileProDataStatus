package rs.gopro.mobile_store.ui.dialog;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.ServiceOrders;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.ui.components.ItemAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.LogUtils;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ServiceOrderDialog extends DialogFragment implements OnEditorActionListener, OnClickListener {

	private static final String TAG = "ServiceOrderDialog";
	
	private static final String SERVICE_ORDER_SCHEME = "settings";
	private static final String SERVICE_ORDER_AUTHORITY = "service_order";
	public static final Uri SERVICE_ORDER_URI = new Uri.Builder().scheme(SERVICE_ORDER_SCHEME).authority(SERVICE_ORDER_AUTHORITY).build();

	
	private int dialogId;
	private String dialogTitle;
	private String valueCaption;
	private int defaultOption = 0;
	private int salesPersonId = -1; 
	
    public interface ServiceOrderDialogListener {
        void onFinishServiceOrderDialog(int service_order_id);
    }

    private int customerId = -1;
    private AutoCompleteTextView mCustomer;
    private CustomerAutocompleteCursorAdapter mCustomerAdapter;
    private int itemId = -1;
    private AutoCompleteTextView mItem;
    private ItemAutocompleteCursorAdapter mItemAdapter;
    
    private EditText mAddress;
    private EditText mPostCode;
    private EditText mPhone;
    private EditText mQuantity;
    
    private EditText mNoteText;
    private EditText mReclamationText;
    
    private Button mSubmitButton;
    private Button mCancelButton;

    public ServiceOrderDialog() {
        // Empty constructor required for DialogFragment
    }
    
	public ServiceOrderDialog(int dialogId, String dialogTitle, int salesPersonId) {
		super();
		this.dialogId = dialogId;
		this.dialogTitle = dialogTitle;
		this.salesPersonId = salesPersonId;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("DIALOG_TITLE", dialogTitle);
		outState.putString("VALUE_CAPTION", valueCaption);
		outState.putInt("DIALOG_ID", dialogId);
		outState.putInt("DEFAULT_OPTION", defaultOption);
		outState.putInt("SALES_PERSON_ID", salesPersonId);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			dialogTitle = savedInstanceState.getString("DIALOG_TITLE");
			valueCaption = savedInstanceState.getString("VALUE_CAPTION");
			dialogId = savedInstanceState.getInt("DIALOG_ID");
			defaultOption = savedInstanceState.getInt("DEFAULT_OPTION");
			salesPersonId = savedInstanceState.getInt("SALES_PERSON_ID");
		}
        View view = inflater.inflate(R.layout.fragment_create_service_order, container);
        
        mCustomer = (AutoCompleteTextView) view.findViewById(R.id.service_order_customer_autocomplete);
        mCustomerAdapter = new CustomerAutocompleteCursorAdapter(getActivity(), null);
        mCustomer.setAdapter(mCustomerAdapter);
        mCustomer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Cursor cursor = (Cursor) mCustomerAdapter.getItem(arg2);
				customerId = cursor.getInt(0);
				if (mAddress != null) {
					mAddress.setText(cursor.getString(6));
				}
				if (mPostCode != null) {
					mPostCode.setText(cursor.getString(7));
				}
			}
		});
        
        mAddress = (EditText) view.findViewById(R.id.service_order_address_edittext);
        
        mPostCode = (EditText) view.findViewById(R.id.service_order_post_code_edittext);
        
        mPhone = (EditText) view.findViewById(R.id.service_order_phone_edittext);

        mItem = (AutoCompleteTextView) view.findViewById(R.id.service_order_item_no_autocomplete);
        mItemAdapter = new ItemAutocompleteCursorAdapter(getActivity(), null);
        mItem.setAdapter(mItemAdapter);
        mItem.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Cursor cursor = (Cursor) mItemAdapter.getItem(arg2);
				itemId = cursor.getInt(0);
			}
		});
        
        mQuantity = (EditText) view.findViewById(R.id.service_order_item_quantity_edittext);
        
        mReclamationText = (EditText) view.findViewById(R.id.service_order_reclamation_edittext);
        mReclamationText.setInputType(InputType.TYPE_CLASS_TEXT);
        
        mNoteText = (EditText) view.findViewById(R.id.service_order_note_edittext);
        mNoteText.setInputType(InputType.TYPE_CLASS_TEXT);
        
        mSubmitButton = (Button) view.findViewById(R.id.button_submit_dialog);
        //mSubmitButton.setText("OK");
        mCancelButton = (Button) view.findViewById(R.id.button_cancel_dialog);
        getDialog().setTitle(dialogTitle);

        // Show soft keyboard automatically
        mNoteText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mNoteText.setOnEditorActionListener(this);
        mSubmitButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ServiceOrderDialog.this.dismiss();
			}
		});
        
        mCustomer.requestFocus();
        
        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            sendInputValues();
            return true;
        }
        return false;
    }

	private void sendInputValues() {
		// Return input text to activity
		ServiceOrderDialogListener activity = (ServiceOrderDialogListener) getActivity();

		String address = mAddress.getText().toString();
		String post_code = mPostCode.getText().toString();
		String phone = mPhone.getText().toString();
		
		String quantity = mQuantity.getText().toString().replace('.', ',');
		double quantity_double = 0.0;
		try {
			quantity_double = Double.valueOf(quantity);
		} catch (NumberFormatException nex) {
			LogUtils.LOGE(TAG, "Bad number", nex);
		}
		
		String reclamation = mReclamationText.getText().toString();
		
		String note = mNoteText.getText().toString();
		
		
		ContentValues cv = new ContentValues();
		
		cv.put(ServiceOrders.ADDRESS, address);
		cv.put(ServiceOrders.PHONE_NO, phone);
		cv.put(ServiceOrders.POST_CODE, post_code);
		cv.put(ServiceOrders.QUANTITY_FOR_RECLAMATION, quantity_double);
		cv.put(ServiceOrders.CUSTOMER_ID, customerId);
		cv.put(ServiceOrders.ITEM_ID, itemId);
		cv.put(ServiceOrders.NOTE, note);
		cv.put(ServiceOrders.RECLAMATION_DESCRIPTION, reclamation);
		cv.put(ServiceOrders.SALES_PERSON_ID, salesPersonId);
		
		Uri uriAfterInsert = getActivity().getContentResolver().insert(ServiceOrders.CONTENT_URI, cv);
		int service_order_id = Integer.valueOf(ServiceOrders.getServiceOrderId(uriAfterInsert));
		
		activity.onFinishServiceOrderDialog(service_order_id);
		this.dismiss();
	}

	@Override
	public void onClick(View v) {
		sendInputValues();
	}
	
	/**
	 * I don't like it but it is the only way...
	 */
	@Override
	public void onDestroyView() {
	  if (getDialog() != null && getRetainInstance())
	    getDialog().setDismissMessage(null);
	  super.onDestroyView();
	}

	public int getDefaultOption() {
		return defaultOption;
	}

	public void setDefaultOption(int defaultOption) {
		this.defaultOption = defaultOption;
	}
}
