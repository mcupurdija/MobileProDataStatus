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

public class CustomerBalanceSyncObject extends SyncObject {

	public static String TAG = "CustomerBalanceSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.CUSTOMER_BALANCE_SYNC_ACTION";
	
	private String pCustomerNo, pBalanceCommission, pBalance, pBalanceDue;
	
	public static final Creator<CustomerBalanceSyncObject> CREATOR = new Creator<CustomerBalanceSyncObject>() {

		@Override
		public CustomerBalanceSyncObject createFromParcel(Parcel source) {
			return new CustomerBalanceSyncObject(source);
		}

		@Override
		public CustomerBalanceSyncObject[] newArray(int size) {
			return new CustomerBalanceSyncObject[size];
		}

	};
	
	public CustomerBalanceSyncObject() {
		super();
	}

	public CustomerBalanceSyncObject(String pCustomerNo, String pBalanceCommission, String pBalance, String pBalanceDue) {
		super();
		this.pCustomerNo = pCustomerNo;
		this.pBalanceCommission = pBalanceCommission;
		this.pBalance = pBalance;
		this.pBalanceDue = pBalanceDue;
	}

	public CustomerBalanceSyncObject(Parcel parcel) {
		super(parcel);
		setpCustomerNo(parcel.readString());
		setpBalanceCommission(parcel.readString());
		setpBalance(parcel.readString());
		setpBalanceDue(parcel.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpCustomerNo());
		dest.writeString(getpBalanceCommission());
		dest.writeString(getpBalance());
		dest.writeString(getpBalanceDue());
	}

	@Override
	public String getWebMethodName() {
		return "GetCustomerBalance";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		
		PropertyInfo pCustomerNoInfo = new PropertyInfo();
		pCustomerNoInfo.setName("pCustomerNo");
		pCustomerNoInfo.setValue(pCustomerNo);
		pCustomerNoInfo.setType(String.class);
		properties.add(pCustomerNoInfo);
		
		PropertyInfo pBalanceCommissionInfo = new PropertyInfo();
		pBalanceCommissionInfo.setName("pBalanceCommission");
		pBalanceCommissionInfo.setValue(pBalanceCommission);
		pBalanceCommissionInfo.setType(String.class);
		properties.add(pBalanceCommissionInfo);
		
		PropertyInfo pBalanceInfo = new PropertyInfo();
		pBalanceInfo.setName("pBalance");
		pBalanceInfo.setValue(pBalance);
		pBalanceInfo.setType(String.class);
		properties.add(pBalanceInfo);
		
		PropertyInfo pBalanceDueInfo = new PropertyInfo();
		pBalanceDueInfo.setName("pBalanceDue");
		pBalanceDueInfo.setValue(pBalanceDue);
		pBalanceDueInfo.setType(String.class);
		properties.add(pBalanceDueInfo);
		
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
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {
		
		this.pBalanceCommission = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pBalanceCommission"));
		this.pBalance = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pBalance"));
		this.pBalanceDue = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pBalanceDue"));
		
		return 0;
	}

	public String getpCustomerNo() {
		return pCustomerNo;
	}

	public void setpCustomerNo(String pCustomerNo) {
		this.pCustomerNo = pCustomerNo;
	}

	public String getpBalanceCommission() {
		return pBalanceCommission;
	}

	public void setpBalanceCommission(String pBalanceCommission) {
		this.pBalanceCommission = pBalanceCommission;
	}

	public String getpBalance() {
		return pBalance;
	}

	public void setpBalance(String pBalance) {
		this.pBalance = pBalance;
	}

	public String getpBalanceDue() {
		return pBalanceDue;
	}

	public void setpBalanceDue(String pBalanceDue) {
		this.pBalanceDue = pBalanceDue;
	}

}
