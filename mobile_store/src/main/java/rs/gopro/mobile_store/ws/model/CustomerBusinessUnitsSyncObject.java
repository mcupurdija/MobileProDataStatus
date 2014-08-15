package rs.gopro.mobile_store.ws.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.CustomerBusinessUnitsDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public class CustomerBusinessUnitsSyncObject extends SyncObject {

	public static String TAG = "CustomerBusinessUnitsSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.CUSTOMER_ADDRESSES_SYNC_ACTION";

	private String cSVString;
	private String customerNoa46;
	private String salespersonCode;

	public static final Creator<CustomerBusinessUnitsSyncObject> CREATOR = new Creator<CustomerBusinessUnitsSyncObject>() {

		@Override
		public CustomerBusinessUnitsSyncObject createFromParcel(Parcel source) {
			return new CustomerBusinessUnitsSyncObject(source);
		}

		@Override
		public CustomerBusinessUnitsSyncObject[] newArray(int size) {
			return new CustomerBusinessUnitsSyncObject[size];
		}

	};

	public CustomerBusinessUnitsSyncObject() {
		super();
	}

	public CustomerBusinessUnitsSyncObject(Parcel parcel) {
		super(parcel);
		setcSVString(parcel.readString());
		setCustomerNoa46(parcel.readString());
		setSalespersonCode(parcel.readString());

	}

	public CustomerBusinessUnitsSyncObject(String cSVString, String customerNoa46, String salespersonCode, Date dateModified) {
		super();
		this.cSVString = cSVString;
		this.customerNoa46 = customerNoa46;
		this.salespersonCode = salespersonCode;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getcSVString());
		dest.writeString(getCustomerNoa46());
		dest.writeString(getSalespersonCode());

	}

	@Override
	public String getWebMethodName() {
		return "GetCustomerBusinessUnits";
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
		return properties;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		List<CustomerBusinessUnitsDomain> businessUnitsDomains = CSVDomainReader.parse(new StringReader(soapResponse.toString()), CustomerBusinessUnitsDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, businessUnitsDomains);
		int numOfInserted = contentResolver.bulkInsert(MobileStoreContract.CustomerBusinessUnits.CONTENT_URI, valuesForInsert);
		return numOfInserted;
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

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
