package rs.gopro.mobile_store.ws.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.CustomerTradeAgreementDomain;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public class CustomerTradeAgreementSyncObject extends SyncObject {
	public static String TAG = "CustomerTradeAgreement";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.CUSTOMER_TRADE_AGREEMENT_ACTION";
	
	private String mCSVString; 
	private String customerNoa46;

	
	public static final Creator<CustomerTradeAgreementSyncObject> CREATOR = new Creator<CustomerTradeAgreementSyncObject>() {

		@Override
		public CustomerTradeAgreementSyncObject createFromParcel(Parcel source) {
			return new CustomerTradeAgreementSyncObject(source);
		}

		@Override
		public CustomerTradeAgreementSyncObject[] newArray(int size) {
			return new CustomerTradeAgreementSyncObject[size];
		}

	};
	
	
	public CustomerTradeAgreementSyncObject() {
		super();
	}
	
	public CustomerTradeAgreementSyncObject(Parcel parcel){
		super(parcel);
	    setmCSVString(parcel.readString());
		setCustomerNoa46(parcel.readString());
	}
	
	
	public CustomerTradeAgreementSyncObject(String mCSVString, String customerNoa46) {
		super();
		this.mCSVString = mCSVString;
		this.customerNoa46 = customerNoa46;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(getStatusMessage());
		dest.writeString(getmCSVString());
		dest.writeString(getCustomerNoa46());

	}

	@Override
	public String getWebMethodName() {
		return "GetCustomerTradeAgreement";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();

		PropertyInfo pCSVString = new PropertyInfo();
		pCSVString.setName("pCSVString");
		pCSVString.setValue(mCSVString);
		pCSVString.setType(String.class);
		properties.add(pCSVString);
		
		PropertyInfo customerNoa46Info = new PropertyInfo();
		customerNoa46Info.setName("pCustomerNoa46");
		customerNoa46Info.setValue(customerNoa46);
		customerNoa46Info.setType(String.class);
		properties.add(customerNoa46Info);
		return properties;
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
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
	List<CustomerTradeAgreementDomain> domains = CSVDomainReader.parse(new StringReader(soapResponse.toString()), CustomerTradeAgreementDomain.class);
	ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, domains);
	int numOfInserted = contentResolver.bulkInsert(MobileStoreContract.CustomerTradeAgreemnt.CONTENT_URI, valuesForInsert);
	return numOfInserted;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}


	public String getCustomerNoa46() {
		return customerNoa46;
	}


	public void setCustomerNoa46(String customerNoa46) {
		this.customerNoa46 = customerNoa46;
	}

	public String getmCSVString() {
		return mCSVString;
	}

	public void setmCSVString(String mCSVString) {
		this.mCSVString = mCSVString;
	}
	
	

}
