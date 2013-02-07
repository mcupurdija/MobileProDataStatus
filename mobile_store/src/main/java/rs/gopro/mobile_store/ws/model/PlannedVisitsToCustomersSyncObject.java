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
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public abstract class PlannedVisitsToCustomersSyncObject extends SyncObject {

	
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.PLANNED_VISITS_TO_CUS_SYNC_ACTION";

	private String cSVString;
	private String salespersonCode;
	private Date visitDateFrom;
	private Date visitDateTo;
	private String customerNoa46;

	public PlannedVisitsToCustomersSyncObject() {
		super();
	}

	public PlannedVisitsToCustomersSyncObject(Parcel parcel) {
		super(parcel);
		setcSVString(parcel.readString());
		setSalespersonCode(parcel.readString());
		setVisitDateFrom(new Date(parcel.readLong()));
		setVisitDateTo(new Date(parcel.readLong()));
		setCustomerNoa46(parcel.readString());

	}

	public PlannedVisitsToCustomersSyncObject(String cSVString, String salespersonCode, Date visitDateFrom, Date visitDateTo, String customerNoa46) {
		super();
		this.cSVString = cSVString;
		this.salespersonCode = salespersonCode;
		this.visitDateFrom = visitDateFrom;
		this.visitDateTo = visitDateTo;
		this.customerNoa46 = customerNoa46;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getcSVString());
		dest.writeString(getSalespersonCode());
		dest.writeLong(getVisitDateFrom().getTime());
		dest.writeLong(getVisitDateTo().getTime());
		dest.writeString(getCustomerNoa46());

	}

	@Override
	public String getWebMethodName() {
		return "GetPlannedVisitsToCustomers";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();

		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("pCSVString");
		cSVStringInfo.setValue(cSVString);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);

		PropertyInfo salespersonCodeInfo = new PropertyInfo();
		salespersonCodeInfo.setName("pSalespersonCode");
		salespersonCodeInfo.setValue(salespersonCode);
		salespersonCodeInfo.setType(String.class);
		properties.add(salespersonCodeInfo);
		
		PropertyInfo visitDateFromInfo = new PropertyInfo();
		visitDateFromInfo.setName("pVisitDateFrom");
		visitDateFromInfo.setValue(visitDateFrom);
		visitDateFromInfo.setType(Date.class);
		properties.add(visitDateFromInfo);

		PropertyInfo visitDateToInfo = new PropertyInfo();
		visitDateToInfo.setName("pVisitDateTo");
		visitDateToInfo.setValue(visitDateTo);
		visitDateToInfo.setType(Date.class);
		properties.add(visitDateToInfo);

		PropertyInfo customerNoa46Info = new PropertyInfo();
		customerNoa46Info.setName("pCustomerNoa46");
		customerNoa46Info.setValue(customerNoa46);
		customerNoa46Info.setType(String.class);
		properties.add(customerNoa46Info);

		return properties;
	}

	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive result) throws CSVParseException {
		List<PlannedVisitsDomain> parsedDomains = CSVDomainReader.parse(new StringReader(result.toString()), PlannedVisitsDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedDomains);
		int numOfInserted = contentResolver.bulkInsert(MobileStoreContract.Visits.CONTENT_URI, valuesForInsert);
		return numOfInserted;
	}
	
	protected int parseAndSave(ContentResolver contentResolver, SoapObject result) throws CSVParseException {
		return 0;
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

	public String getSalespersonCode() {
		return salespersonCode;
	}

	public void setSalespersonCode(String salespersonCode) {
		this.salespersonCode = salespersonCode;
	}

	public Date getVisitDateFrom() {
		return visitDateFrom;
	}

	public void setVisitDateFrom(Date visitDateFrom) {
		this.visitDateFrom = visitDateFrom;
	}

	public Date getVisitDateTo() {
		return visitDateTo;
	}

	public void setVisitDateTo(Date visitDateTo) {
		this.visitDateTo = visitDateTo;
	}

	public String getCustomerNoa46() {
		return customerNoa46;
	}

	public void setCustomerNoa46(String customerNoa46) {
		this.customerNoa46 = customerNoa46;
	}

}
