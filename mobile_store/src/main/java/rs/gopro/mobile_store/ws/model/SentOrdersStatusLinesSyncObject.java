package rs.gopro.mobile_store.ws.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.SentOrdersStatusLines;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.SentOrdersStatusLinesDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public class SentOrdersStatusLinesSyncObject extends SyncObject {

	public static String TAG = LogUtils
			.makeLogTag(SentOrdersStatusLinesSyncObject.class);
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.SENT_ORDERS_STATUS_LINES_SYNC_ACTION";

	private String cSVString;
	private Integer pDocumentType;
	private String pDocumentNoa46;
	private String customerNoa46;
	private String pSalespersonCode;
	private int sentOrderId;

	public static final Creator<SentOrdersStatusLinesSyncObject> CREATOR = new Creator<SentOrdersStatusLinesSyncObject>() {

		@Override
		public SentOrdersStatusLinesSyncObject createFromParcel(Parcel source) {
			return new SentOrdersStatusLinesSyncObject(source);
		}

		@Override
		public SentOrdersStatusLinesSyncObject[] newArray(int size) {
			return new SentOrdersStatusLinesSyncObject[size];
		}
	};

	public SentOrdersStatusLinesSyncObject() {
		super();
	}

	public SentOrdersStatusLinesSyncObject(Parcel parcel) {
		super(parcel);
		setcSVString(parcel.readString());
		setpDocumentType(parcel.readInt());
		setpDocumentNoa46(parcel.readString());
		setCustomerNoa46(parcel.readString());
		setpSalespersonCode(parcel.readString());
		this.sentOrderId = parcel.readInt();
	}
	
	

	public SentOrdersStatusLinesSyncObject(String cSVString,
			Integer pDocumentType, String pDocumentNoa46, String customerNoa46,
			String pSalespersonCode, int sentOrderId) {
		super();
		this.cSVString = cSVString;
		this.pDocumentType = pDocumentType;
		this.pDocumentNoa46 = pDocumentNoa46;
		this.customerNoa46 = customerNoa46;
		this.pSalespersonCode = pSalespersonCode;
		this.sentOrderId = sentOrderId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getcSVString());
		dest.writeInt(getpDocumentType());
		dest.writeString(getpDocumentNoa46());
		dest.writeString(getCustomerNoa46());
		dest.writeString(getpSalespersonCode());
		dest.writeInt(sentOrderId);
	}

	@Override
	public String getWebMethodName() {
		return "GetSalesLines";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();

		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("pCSVString");
		cSVStringInfo.setValue(cSVString);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);

		PropertyInfo documentTypeParam = new PropertyInfo();
		documentTypeParam.setName("pDocumentType");
		documentTypeParam.setValue(pDocumentType);
		documentTypeParam.setType(Integer.class);
		properties.add(documentTypeParam);
		
		PropertyInfo documentNoa46 = new PropertyInfo();
		documentNoa46.setName("pDocumentNoa46");
		documentNoa46.setValue(pDocumentNoa46);
		documentNoa46.setType(String.class);
		properties.add(documentNoa46);

		PropertyInfo customerNoa46Info = new PropertyInfo();
		customerNoa46Info.setName("pCustomerNoa46");
		customerNoa46Info.setValue(customerNoa46);
		customerNoa46Info.setType(String.class);
		properties.add(customerNoa46Info);

		PropertyInfo salespersonCode = new PropertyInfo();
		salespersonCode.setName("pSalespersonCode");
		salespersonCode.setValue(pSalespersonCode);
		salespersonCode.setType(String.class);
		properties.add(salespersonCode);

		return properties;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		List<SentOrdersStatusLinesDomain> parsedItems = CSVDomainReader.parse(new StringReader(soapResponse.toString()), SentOrdersStatusLinesDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedItems);
		int del_res = 0;
		if (valuesForInsert.length > 0) {
			del_res = contentResolver.delete(SentOrdersStatusLines.CONTENT_URI, SentOrdersStatusLines.SENT_ORDER_STATUS_ID+"=?", new String[] { String.valueOf(sentOrderId) });
			LogUtils.LOGD(TAG, "Deleted, pre bulk insert, rows:"+del_res);
		}
		int numOfInserted = contentResolver.bulkInsert(MobileStoreContract.SentOrdersStatusLines.CONTENT_URI, valuesForInsert);
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

	public String getpDocumentNoa46() {
		return pDocumentNoa46;
	}

	public void setpDocumentNoa46(String salesInvoiceNoa46) {
		this.pDocumentNoa46 = salesInvoiceNoa46;
	}

	public String getCustomerNoa46() {
		return customerNoa46;
	}

	public void setCustomerNoa46(String customerNoa46) {
		this.customerNoa46 = customerNoa46;
	}
	
	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		return 0;
	}

	public Integer getpDocumentType() {
		return pDocumentType;
	}

	public void setpDocumentType(Integer pDocumentType) {
		this.pDocumentType = pDocumentType;
	}

	public String getpSalespersonCode() {
		return pSalespersonCode;
	}

	public void setpSalespersonCode(String pSalespersonCode) {
		this.pSalespersonCode = pSalespersonCode;
	}

}
