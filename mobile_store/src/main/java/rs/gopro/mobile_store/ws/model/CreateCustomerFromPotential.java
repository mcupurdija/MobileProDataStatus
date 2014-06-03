package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.os.Parcel;

public class CreateCustomerFromPotential extends SyncObject {
	
	public static String TAG = "CreateCustomerFromPotential";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.CREATE_CUSTOMER_FROM_POTENTIAL_SYNC_ACTION";
	
	private String pPotentialCustomerNoa46;
	
	public static final Creator<CreateCustomerFromPotential> CREATOR = new Creator<CreateCustomerFromPotential>() {

		@Override
		public CreateCustomerFromPotential createFromParcel(Parcel source) {
			return new CreateCustomerFromPotential(source);
		}

		@Override
		public CreateCustomerFromPotential[] newArray(int size) {
			return new CreateCustomerFromPotential[size];
		}
	};

	public CreateCustomerFromPotential() {
		super();
	}

	public CreateCustomerFromPotential(String pPotentialCustomerNoa46) {
		super();
		this.pPotentialCustomerNoa46 = pPotentialCustomerNoa46;
	}

	public CreateCustomerFromPotential(Parcel source) {
		super(source);
		setpPotentialCustomerNoa46(source.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpPotentialCustomerNoa46());
	}

	@Override
	public String getWebMethodName() {
		return "CreateCustomerFromPotential";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>();

		PropertyInfo customerNo = new PropertyInfo();
		customerNo.setName("pPotentialCustomerNoa46");
		customerNo.setValue(pPotentialCustomerNoa46);
		customerNo.setType(String.class);
		properies.add(customerNo);
		
		return properies;
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getBroadcastAction() {
		return BROADCAST_SYNC_ACTION;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapPrimitive soapResponse) throws CSVParseException {
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		return 0;
	}

	public String getpPotentialCustomerNoa46() {
		return pPotentialCustomerNoa46;
	}

	public void setpPotentialCustomerNoa46(String pPotentialCustomerNoa46) {
		this.pPotentialCustomerNoa46 = pPotentialCustomerNoa46;
	}

}
