package rs.gopro.mobile_store.ui.dialog;

import rs.gopro.mobile_store.R;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EditFieldDialog extends DialogFragment implements OnEditorActionListener, OnClickListener {

	private int dialogId;
	private String dialogTitle;
	private String valueCaption;
	private int editValueInputType;
	
    public interface EditNameDialogListener {
        void onFinishEditDialog(int id, String inputText);
    }

    private EditText mEditText;
    private TextView mEditCaption;
    private Button mSubmitButton;
    private Button mCancelButton;
    private TextView mError;

    public EditFieldDialog() {
        // Empty constructor required for DialogFragment
    }
    
    public EditFieldDialog(String dialogTitle, String valueCaption) {
		super();
		this.dialogTitle = dialogTitle;
		this.valueCaption = valueCaption;
		this.editValueInputType = InputType.TYPE_CLASS_TEXT;
    }

	public EditFieldDialog(String dialogTitle, String valueCaption,
			int editValueInputType) {
		super();
		this.dialogTitle = dialogTitle;
		this.valueCaption = valueCaption;
		this.editValueInputType = editValueInputType;
	}

	public EditFieldDialog(int dialogId, String dialogTitle,
			String valueCaption, int editValueInputType) {
		super();
		this.dialogId = dialogId;
		this.dialogTitle = dialogTitle;
		this.valueCaption = valueCaption;
		this.editValueInputType = editValueInputType;
	}
	
	public EditFieldDialog(int dialogId, String dialogTitle, String valueCaption) {
		super();
		this.dialogId = dialogId;
		this.dialogTitle = dialogTitle;
		this.valueCaption = valueCaption;
		this.editValueInputType = InputType.TYPE_CLASS_TEXT;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("DIALOG_TITLE", dialogTitle);
		outState.putString("VALUE_CAPTION", valueCaption);
		outState.putInt("DIALOG_INPUT_TYPE", editValueInputType);
		outState.putInt("DIALOG_ID", dialogId);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			dialogTitle = savedInstanceState.getString("DIALOG_TITLE");
			valueCaption = savedInstanceState.getString("VALUE_CAPTION");
			editValueInputType = savedInstanceState.getInt("DIALOG_INPUT_TYPE");
			dialogId = savedInstanceState.getInt("DIALOG_ID");
		}
        View view = inflater.inflate(R.layout.fragment_edit_field, container);
        mEditText = (EditText) view.findViewById(R.id.txt_value);
        mEditText.setInputType(editValueInputType);
        mEditCaption = (TextView) view.findViewById(R.id.lbl_caption);
        mSubmitButton = (Button) view.findViewById(R.id.button_submit_dialog);
        mError = (TextView) view.findViewById(R.id.lbl_error);
        //mSubmitButton.setText("OK");
        mCancelButton = (Button) view.findViewById(R.id.button_cancel_dialog);
        mEditCaption.setText(valueCaption);
        getDialog().setTitle(dialogTitle);

        // Show soft keyboard automatically
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditText.setOnEditorActionListener(this);
        mSubmitButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditFieldDialog.this.dismiss();
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
		EditNameDialogListener activity = (EditNameDialogListener) getActivity();
		String inputValue = mEditText.getText().toString();
		if (inputValue == null || inputValue.length() < 1) {
			mError.setVisibility(View.VISIBLE);
			mError.setText("Unestite vrednost!");
		} else {
			mError.setVisibility(View.GONE);
			activity.onFinishEditDialog(dialogId, inputValue);
			this.dismiss();
		}	
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
}
