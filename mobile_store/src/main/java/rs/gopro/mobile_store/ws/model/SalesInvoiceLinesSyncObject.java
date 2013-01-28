package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;

import rs.gopro.mobile_store.util.exceptions.SOAPResponseException;
import android.content.ContentResolver;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class SalesInvoiceLinesSyncObject extends SyncObject {

	public static String TAG = "SalesInvoiceLinesSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.SALES_INVOICE_LINES_SYNC_ACTION";

	private String cSVString;
	private String salesInvoiceNoa46;
	private String customerNoa46;
	private Date postingDateFrom;
	private Date postingDateTo;

	public static final Creator<SalesInvoiceLinesSyncObject> CREATOR = new Creator<SalesInvoiceLinesSyncObject>() {

		@Override
		public SalesInvoiceLinesSyncObject createFromParcel(Parcel source) {
			return new SalesInvoiceLinesSyncObject(source);
		}

		@Override
		public SalesInvoiceLinesSyncObject[] newArray(int size) {
			return new SalesInvoiceLinesSyncObject[size];
		}
	};

	public SalesInvoiceLinesSyncObject() {
		super();
	}

	public SalesInvoiceLinesSyncObject(Parcel parcel) {
		super(parcel);
		setcSVString(parcel.readString());
		setSalesInvoiceNoa46(parcel.readString());
		setCustomerNoa46(parcel.readString());
		setPostingDateFrom(new Date(parcel.readLong()));
		setPostingDateTo(new Date(parcel.readLong()));
	}

	public SalesInvoiceLinesSyncObject(String cSVString, String salesInvoiceNoa46, String customerNoa46, Date postingDateFrom, Date postingDateTo) {
		super();
		this.cSVString = cSVString;
		this.salesInvoiceNoa46 = salesInvoiceNoa46;
		this.customerNoa46 = customerNoa46;
		this.postingDateFrom = postingDateFrom;
		this.postingDateTo = postingDateTo;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getcSVString());
		dest.writeString(getSalesInvoiceNoa46());
		dest.writeString(getCustomerNoa46());
		dest.writeLong(getPostingDateFrom().getTime());
		dest.writeLong(getPostingDateTo().getTime());
	

	}

	@Override
	public String getWebMethodName() {
		return "GetSalesInvoiceLines";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();

		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("pCSVString");
		cSVStringInfo.setValue(cSVString);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);

		PropertyInfo salesInvoiceNoa46Info = new PropertyInfo();
		salesInvoiceNoa46Info.setName("pSalesInvoiceNoa46");
		salesInvoiceNoa46Info.setValue(salesInvoiceNoa46);
		salesInvoiceNoa46Info.setType(String.class);
		properties.add(salesInvoiceNoa46Info);

		PropertyInfo customerNoa46Info = new PropertyInfo();
		customerNoa46Info.setName("pCustomerNoa46");
		customerNoa46Info.setValue(customerNoa46);
		customerNoa46Info.setType(String.class);
		properties.add(customerNoa46Info);

		PropertyInfo postingDateFromInfo = new PropertyInfo();
		postingDateFromInfo.setName("pPostingDateFrom");
		postingDateFromInfo.setValue(postingDateFrom);
		postingDateFromInfo.setType(Date.class);
		properties.add(postingDateFromInfo);

		PropertyInfo postingDateToInfo = new PropertyInfo();
		postingDateToInfo.setName("pPostingDateTo");
		postingDateToInfo.setValue(postingDateTo);
		postingDateToInfo.setType(Date.class);
		properties.add(postingDateToInfo);

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

	public String getSalesInvoiceNoa46() {
		return salesInvoiceNoa46;
	}

	public void setSalesInvoiceNoa46(String salesInvoiceNoa46) {
		this.salesInvoiceNoa46 = salesInvoiceNoa46;
	}

	public String getCustomerNoa46() {
		return customerNoa46;
	}

	public void setCustomerNoa46(String customerNoa46) {
		this.customerNoa46 = customerNoa46;
	}

	public Date getPostingDateFrom() {
		return postingDateFrom;
	}

	public void setPostingDateFrom(Date postingDateFrom) {
		this.postingDateFrom = postingDateFrom;
	}

	public Date getPostingDateTo() {
		return postingDateTo;
	}

	public void setPostingDateTo(Date postingDateTo) {
		this.postingDateTo = postingDateTo;
	}

}
