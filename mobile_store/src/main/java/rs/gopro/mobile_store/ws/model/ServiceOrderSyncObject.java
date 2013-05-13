package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.MobileStoreContract.ServiceOrders;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

public class ServiceOrderSyncObject extends SyncObject {

	public static String TAG = "ServiceOrderSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.SET_SERVICE_ORDER_SYNC_ACTION";
	
	private String pCustomerNoa46;
	private String pAddress;
	private String pPostCode;
	private String pPhoneNoa46;
	private String pItemNoa46;
	private Integer pQuantityForReclamation = -1;
	private String pNote;
	private String pServiceHeaderNoa46;
	private int serviceOrderId = -1;
	
	public static final Creator<ServiceOrderSyncObject> CREATOR = new Creator<ServiceOrderSyncObject>() {

		@Override
		public ServiceOrderSyncObject createFromParcel(Parcel source) {
			return new ServiceOrderSyncObject(source);
		}

		@Override
		public ServiceOrderSyncObject[] newArray(int size) {
			return new ServiceOrderSyncObject[size];
		}

	};
	
	public ServiceOrderSyncObject() {
		super();
	}
	
	public ServiceOrderSyncObject(int serviceOrderId) {
		super();
		this.serviceOrderId = serviceOrderId;
	}
	
	public ServiceOrderSyncObject(String pCustomerNoa46, String pAddress,
			String pPostCode, String pPhoneNoa46, String pItemNoa46,
			Integer pQuantityForReclamation, String pNote) {
		super();
		this.pCustomerNoa46 = pCustomerNoa46;
		this.pAddress = pAddress;
		this.pPostCode = pPostCode;
		this.pPhoneNoa46 = pPhoneNoa46;
		this.pItemNoa46 = pItemNoa46;
		this.pQuantityForReclamation = pQuantityForReclamation;
		this.pNote = pNote;
	}

	public ServiceOrderSyncObject(Parcel source) {
		super(source);
		setpCustomerNoa46(source.readString());
		setpAddress(source.readString());
		setpPostCode(source.readString());
		setpPhoneNoa46(source.readString());
		setpItemNoa46(source.readString());
		setpQuantityForReclamation(source.readInt());
		setpNote(source.readString());
		setpServiceHeaderNoa46(source.readString());
		serviceOrderId = source.readInt();
	}
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpCustomerNoa46());
		dest.writeString(getpAddress());
		dest.writeString(getpPostCode());
		dest.writeString(getpPhoneNoa46());
		dest.writeString(getpItemNoa46());
		dest.writeInt(getpQuantityForReclamation());
		dest.writeString(getpNote());
		dest.writeString(getpServiceHeaderNoa46());
		dest.writeInt(serviceOrderId);
	}

	@Override
	public String getWebMethodName() {
		return "SetServiceOrder";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		
		loadData(serviceOrderId);
		
		PropertyInfo customerNoa46 = new PropertyInfo();
		customerNoa46.setName("pCustomerNoa46");
		customerNoa46.setValue(pCustomerNoa46);
		customerNoa46.setType(String.class);
		properties.add(customerNoa46);
		
		PropertyInfo address = new PropertyInfo();
		address.setName("pAddress");
		address.setValue(pAddress);
		address.setType(String.class);
		properties.add(address);
		
		PropertyInfo postCode = new PropertyInfo();
		postCode.setName("pPostCode");
		postCode.setValue(pPostCode);
		postCode.setType(String.class);
		properties.add(postCode);
		
		PropertyInfo phoneNoa46 = new PropertyInfo();
		phoneNoa46.setName("pPhoneNoa46");
		phoneNoa46.setValue(pPhoneNoa46);
		phoneNoa46.setType(String.class);
		properties.add(phoneNoa46);
		
		PropertyInfo itemNoa46 = new PropertyInfo();
		itemNoa46.setName("pItemNoa46");
		itemNoa46.setValue(pItemNoa46);
		itemNoa46.setType(String.class);
		properties.add(itemNoa46);
		
		PropertyInfo quantityForReclamation = new PropertyInfo();
		quantityForReclamation.setName("pQuantityForReclamation");
		quantityForReclamation.setValue(pQuantityForReclamation);
		quantityForReclamation.setType(Integer.class);
		properties.add(quantityForReclamation);
		
		PropertyInfo note = new PropertyInfo();
		note.setName("pNote");
		note.setValue(pNote);
		note.setType(String.class);
		properties.add(note);
		
		PropertyInfo serviceHeaderNoa46 = new PropertyInfo();
		serviceHeaderNoa46.setName("pServiceHeaderNoa46");
		serviceHeaderNoa46.setValue(pServiceHeaderNoa46);
		serviceHeaderNoa46.setType(String.class);
		properties.add(serviceHeaderNoa46);
		
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

	private void loadData(int serviceOrderId) {
		Cursor cursorServiceOrder = context.getContentResolver().query(ServiceOrders.buildServiceOrdersExportUri(), ServiceOrderQuery.PROJECTION, Tables.SERVICE_ORDERS + "._ID=?", new String[] { String.valueOf(serviceOrderId) }, null);
		// must be only one member, if not big error
		if (cursorServiceOrder.moveToFirst()) {
			setpCustomerNoa46(cursorServiceOrder.getString(ServiceOrderQuery.CUSTOMER_NO));
			setpAddress(cursorServiceOrder.getString(ServiceOrderQuery.ADDRESS));
			setpPostCode(cursorServiceOrder.getString(ServiceOrderQuery.POST_CODE));
			setpPhoneNoa46(cursorServiceOrder.getString(ServiceOrderQuery.PHONE_NO));
			setpItemNoa46(cursorServiceOrder.getString(ServiceOrderQuery.ITEM_NO));
			setpQuantityForReclamation(Double.valueOf(cursorServiceOrder.getDouble(ServiceOrderQuery.QUANTITY_FOR_RECLAMATION)).intValue());
			setpNote(cursorServiceOrder.getString(ServiceOrderQuery.NOTE));
		} else {
			LogUtils.LOGE(TAG, "This should never happen!!!");
		}
	}
	
	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapPrimitive soapResponse) throws CSVParseException {
		this.pServiceHeaderNoa46 = soapResponse.toString();
		ContentValues cv = new ContentValues();
		cv.put(ServiceOrders.SERVICE_ORDER_NO, pServiceHeaderNoa46);
		
		int result = context.getContentResolver().update(ServiceOrders.CONTENT_URI, cv, Tables.SERVICE_ORDERS + "._ID=?", new String[] { String.valueOf(serviceOrderId) });
		
		return result;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		this.pServiceHeaderNoa46 = soapResponse.getPropertyAsString("pServiceHeaderNoa46");
		return 1;
	}

	private interface ServiceOrderQuery {
		String[] PROJECTION = {
			Tables.SERVICE_ORDERS + "." + ServiceOrders._ID,
			Tables.CUSTOMERS + "." + Customers.CUSTOMER_NO,
			Tables.SERVICE_ORDERS + "." + ServiceOrders.ADDRESS,
			Tables.SERVICE_ORDERS + "." + ServiceOrders.POST_CODE,
			Tables.SERVICE_ORDERS + "." + ServiceOrders.PHONE_NO,
			Tables.ITEMS + "." + Items.ITEM_NO,
			Tables.SERVICE_ORDERS + "." + ServiceOrders.QUANTITY_FOR_RECLAMATION,
			Tables.SERVICE_ORDERS + "." + ServiceOrders.NOTE
		};
		
//		int ID = 0;
		int CUSTOMER_NO = 1;
		int ADDRESS = 2;
		int POST_CODE = 3;
		int PHONE_NO = 4;
		int ITEM_NO = 5;
		int QUANTITY_FOR_RECLAMATION = 6;
		int NOTE = 7;
	}
	
	public String getpCustomerNoa46() {
		return pCustomerNoa46;
	}

	public void setpCustomerNoa46(String pCustomerNoa46) {
		this.pCustomerNoa46 = pCustomerNoa46;
	}

	public String getpAddress() {
		return pAddress;
	}

	public void setpAddress(String pAddress) {
		this.pAddress = pAddress;
	}

	public String getpPostCode() {
		return pPostCode;
	}

	public void setpPostCode(String pPostCode) {
		this.pPostCode = pPostCode;
	}

	public String getpPhoneNoa46() {
		return pPhoneNoa46;
	}

	public void setpPhoneNoa46(String pPhoneNoa46) {
		this.pPhoneNoa46 = pPhoneNoa46;
	}

	public String getpItemNoa46() {
		return pItemNoa46;
	}

	public void setpItemNoa46(String pItemNoa46) {
		this.pItemNoa46 = pItemNoa46;
	}

	public Integer getpQuantityForReclamation() {
		return pQuantityForReclamation;
	}

	public void setpQuantityForReclamation(Integer pQuantityForReclamation) {
		this.pQuantityForReclamation = pQuantityForReclamation;
	}

	public String getpNote() {
		return pNote;
	}

	public void setpNote(String pNote) {
		this.pNote = pNote;
	}

	public String getpServiceHeaderNoa46() {
		return pServiceHeaderNoa46;
	}

	public void setpServiceHeaderNoa46(String pServiceHeaderNoa46) {
		this.pServiceHeaderNoa46 = pServiceHeaderNoa46;
	}

}
