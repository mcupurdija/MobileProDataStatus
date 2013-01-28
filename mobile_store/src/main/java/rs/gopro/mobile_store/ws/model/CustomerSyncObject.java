package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kobjects.util.Csv;
import org.ksoap2.serialization.PropertyInfo;

import rs.gopro.mobile_store.util.exceptions.SOAPResponseException;
import android.content.ContentResolver;
import android.os.Parcel;

public class CustomerSyncObject extends SyncObject {

	public static String TAG = "CustomerSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.CUSTOMER_SYNC_ACTION";

	private String cSVString;
	private String customerNoa46;
	private String salespersonCode;
	private Date dateModified;

	public static final Creator<CustomerSyncObject> CREATOR = new Creator<CustomerSyncObject>() {

		@Override
		public CustomerSyncObject createFromParcel(Parcel source) {
			return new CustomerSyncObject(source);
		}

		@Override
		public CustomerSyncObject[] newArray(int size) {
			return new CustomerSyncObject[size];
		}

	};

	public CustomerSyncObject() {
		super();
	}

	public CustomerSyncObject(Parcel parcel) {
		super(parcel);
		setcSVString(parcel.readString());
		setCustomerNoa46(parcel.readString());
		setSalespersonCode(parcel.readString());
		setDateModified(new Date(parcel.readLong()));

	}

	public CustomerSyncObject(String cSVString, String customerNoa46, String salespersonCode, Date dateModified) {
		super();
		this.cSVString = cSVString;
		this.customerNoa46 = customerNoa46;
		this.salespersonCode = salespersonCode;
		this.dateModified = dateModified;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(getcSVString());
		dest.writeString(getCustomerNoa46());
		dest.writeString(getSalespersonCode());
		dest.writeLong(getDateModified().getTime());

	}

	@Override
	public String getWebMethodName() {
		return "GetCustomers";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("pCSVString");
		cSVStringInfo.setValue(cSVString);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);

		PropertyInfo customerNoa46Info = new PropertyInfo();
		customerNoa46Info.setName("pCustomerNoa46");
		customerNoa46Info.setValue(customerNoa46);
		customerNoa46Info.setType(String.class);
		properties.add(customerNoa46Info);

		PropertyInfo salespersonCodeInfo = new PropertyInfo();
		salespersonCodeInfo.setName("pSalespersonCode");
		salespersonCodeInfo.setValue(salespersonCode);
		salespersonCodeInfo.setType(String.class);
		properties.add(salespersonCodeInfo);

		PropertyInfo dateModifiedInfo = new PropertyInfo();
		dateModifiedInfo.setName("pDateModified");
		dateModifiedInfo.setValue(dateModified);
		dateModifiedInfo.setType(Date.class);

		return properties;
	}

	@Override
	public void saveSOAPResponse(Object response, ContentResolver contentResolver) throws SOAPResponseException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getBroadcastAction() {
		return BROADCAST_SYNC_ACTION;
	}

	public String getcSVString() {
		return cSVString;
	}

	public void setcSVString(String cSVString) {
		this.cSVString = cSVString;
	}

	public String getCustomerNoa46() {
		return customerNoa46;
	}

	public void setCustomerNoa46(String customerNoa46) {
		this.customerNoa46 = customerNoa46;
	}

	public String getSalespersonCode() {
		return salespersonCode;
	}

	public void setSalespersonCode(String salespersonCode) {
		this.salespersonCode = salespersonCode;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

}
