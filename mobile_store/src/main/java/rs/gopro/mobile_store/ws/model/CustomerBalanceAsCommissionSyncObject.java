package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import android.content.ContentResolver;
import android.os.Parcel;

public class CustomerBalanceAsCommissionSyncObject extends SyncObject {

	public static String TAG = "CustomerBalanceAsCommissionSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.CUSTOMER_BALANCE_AS_COMMISSION_SYNC_ACTION";
	
	private String pCustomerNo, pBalanceValue;
	
	public static final Creator<CustomerBalanceAsCommissionSyncObject> CREATOR = new Creator<CustomerBalanceAsCommissionSyncObject>() {

		@Override
		public CustomerBalanceAsCommissionSyncObject createFromParcel(Parcel source) {
			return new CustomerBalanceAsCommissionSyncObject(source);
		}

		@Override
		public CustomerBalanceAsCommissionSyncObject[] newArray(int size) {
			return new CustomerBalanceAsCommissionSyncObject[size];
		}

	};
	
	public CustomerBalanceAsCommissionSyncObject() {
		super();
	}
	
	public CustomerBalanceAsCommissionSyncObject(String pCustomerNo, String pBalanceValue) {
		super();
		this.pCustomerNo = pCustomerNo;
		this.pBalanceValue = pBalanceValue;
	}

	public CustomerBalanceAsCommissionSyncObject(Parcel parcel) {
		super(parcel);
		setpCustomerNo(parcel.readString());
		setpBalanceValue(parcel.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpCustomerNo());
		dest.writeString(getpBalanceValue());
	}

	@Override
	public String getWebMethodName() {
		return "GetCustomerBalanceAsCommission";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		
		PropertyInfo pCustomerNoInfo = new PropertyInfo();
		pCustomerNoInfo.setName("pCustomerNo");
		pCustomerNoInfo.setValue(pCustomerNo);
		pCustomerNoInfo.setType(String.class);
		properties.add(pCustomerNoInfo);
		
		PropertyInfo pBalanceValueInfo = new PropertyInfo();
		pBalanceValueInfo.setName("pBalanceValue");
		pBalanceValueInfo.setValue(pBalanceValue);
		pBalanceValueInfo.setType(String.class);
		properties.add(pBalanceValueInfo);
		
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

		this.pBalanceValue = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.toString());
		
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {
		return 0;
	}

	public String getpCustomerNo() {
		return pCustomerNo;
	}

	public void setpCustomerNo(String pCustomerNo) {
		this.pCustomerNo = pCustomerNo;
	}

	public String getpBalanceValue() {
		return pBalanceValue;
	}

	public void setpBalanceValue(String pBalanceValue) {
		this.pBalanceValue = pBalanceValue;
	}

}
