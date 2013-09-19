package rs.gopro.mobile_store.ui.dialog;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.util.ApplicationConstants;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EditDepartureVisitDialog extends DialogFragment implements OnEditorActionListener, OnClickListener {

	private int dialogId;
	private String dialogTitle;
	private String valueCaption;
	private int defaultOption = 0;
	
    public interface EditDepartureVisitDialogListener {
        void onFinishEditDepartureVisitDialog(int id, int visitResult, String note);
    }

    private EditText mNoteText;
    private TextView mNoteCaption;
    private Button mSubmitButton;
    private Button mCancelButton;
    private TextView mVisitResultCaption;
    ArrayAdapter<CharSequence> mVisitResultAdapter;
    private Spinner mVisitResult;

    public EditDepartureVisitDialog() {
        // Empty constructor required for DialogFragment
    }
    
	public EditDepartureVisitDialog(int dialogId, String dialogTitle) {
		super();
		this.dialogId = dialogId;
		this.dialogTitle = dialogTitle;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("DIALOG_TITLE", dialogTitle);
		outState.putString("VALUE_CAPTION", valueCaption);
		outState.putInt("DIALOG_ID", dialogId);
		outState.putInt("DEFAULT_OPTION", defaultOption);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			dialogTitle = savedInstanceState.getString("DIALOG_TITLE");
			valueCaption = savedInstanceState.getString("VALUE_CAPTION");
			dialogId = savedInstanceState.getInt("DIALOG_ID");
			defaultOption = savedInstanceState.getInt("DEFAULT_OPTION");
		}
        View view = inflater.inflate(R.layout.fragment_edit_departure_visit, container);
        
        mVisitResultCaption = (TextView) view.findViewById(R.id.visit_result_caption);
        mVisitResultCaption.setText("Rezultat posete:");
        
        mVisitResultAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.record_visit_result_type_array, android.R.layout.simple_spinner_item);
        mVisitResultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mVisitResult = (Spinner) view.findViewById(R.id.visit_result_spinner);
        mVisitResult.setAdapter(mVisitResultAdapter);
        mVisitResult.setSelection(defaultOption);
        
        mNoteCaption = (TextView) view.findViewById(R.id.visit_note_caption);
        mNoteCaption.setText("Beleška nakon posete:");
        
        mNoteText = (EditText) view.findViewById(R.id.visit_note_edittext);
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
				EditDepartureVisitDialog.this.dismiss();
			}
		});
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
		EditDepartureVisitDialogListener activity = (EditDepartureVisitDialogListener) getActivity();

		int realization_option = ApplicationConstants.VISIT_TYPE_NO_CLOSURE;
		switch (mVisitResult.getSelectedItemPosition()) {
		case 0:
			realization_option = ApplicationConstants.VISIT_TYPE_CLOSURE;
			break;
		case 1:
			realization_option = ApplicationConstants.VISIT_TYPE_NO_CLOSURE;
			break;
		case 2:
			realization_option = ApplicationConstants.VISIT_TYPE_PAUSE;
			break;
		default:
			realization_option = ApplicationConstants.VISIT_TYPE_NO_CLOSURE;
			break;
		}
		activity.onFinishEditDepartureVisitDialog(dialogId, realization_option, mNoteText.getText().toString());
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
