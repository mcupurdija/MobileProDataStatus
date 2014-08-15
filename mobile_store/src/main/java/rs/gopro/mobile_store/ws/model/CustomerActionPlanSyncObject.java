package rs.gopro.mobile_store.ws.model;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract.ActionPlan;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerBusinessUnits;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.CustomerActionPlanDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public class CustomerActionPlanSyncObject extends SyncObject {

	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.CUSTOMER_ACTION_PLAN_SYNC_OBJECT";
	public static String TAG = "CustomerActionPlanSyncObject";

	private String pCSVString;
	private String pCustomerNoa46;
	private String pBusinessUnit;
	private String pSalespersonCode;
	private Integer hasActionPlan;

	public static final Creator<CustomerActionPlanSyncObject> CREATOR = new Creator<CustomerActionPlanSyncObject>() {

		@Override
		public CustomerActionPlanSyncObject createFromParcel(Parcel source) {
			return new CustomerActionPlanSyncObject(source);
		}

		@Override
		public CustomerActionPlanSyncObject[] newArray(int size) {
			return new CustomerActionPlanSyncObject[size];
		}

	};

	public CustomerActionPlanSyncObject() {
		super();
	}

	public CustomerActionPlanSyncObject(String pCSVString,
			String pCustomerNoa46, String pBusinessUnit,
			String pSalespersonCode, Integer hasActionPlan) {
		super();
		this.pCSVString = pCSVString;
		this.pCustomerNoa46 = pCustomerNoa46;
		this.pBusinessUnit = pBusinessUnit;
		this.pSalespersonCode = pSalespersonCode;
		this.hasActionPlan = hasActionPlan;
	}

	public CustomerActionPlanSyncObject(Parcel parcel) {
		super(parcel);
		setpCSVString(parcel.readString());
		setpCustomerNoa46(parcel.readString());
		setpBusinessUnit(parcel.readString());
		setpSalespersonCode(parcel.readString());
		setHasActionPlan(parcel.readInt());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpCSVString());
		dest.writeString(getpCustomerNoa46());
		dest.writeString(getpBusinessUnit());
		dest.writeString(getpSalespersonCode());
		dest.writeInt(getHasActionPlan());
	}

	@Override
	public String getWebMethodName() {
		return "GetCustomerActionPlan";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();

		PropertyInfo pCSVStringInfo = new PropertyInfo();
		pCSVStringInfo.setName("pCSVString");
		pCSVStringInfo.setValue(pCSVString);
		pCSVStringInfo.setType(String.class);
		properties.add(pCSVStringInfo);
		
		PropertyInfo pCustomerNoa46Info = new PropertyInfo();
		pCustomerNoa46Info.setName("pCustomerNoa46");
		pCustomerNoa46Info.setValue(pCustomerNoa46);
		pCustomerNoa46Info.setType(String.class);
		properties.add(pCustomerNoa46Info);
		
		PropertyInfo pBusinessUnitInfo = new PropertyInfo();
		pBusinessUnitInfo.setName("pBusinessUnit");
		pBusinessUnitInfo.setValue(pBusinessUnit);
		pBusinessUnitInfo.setType(String.class);
		properties.add(pBusinessUnitInfo);
		
		PropertyInfo pSalespersonCodeInfo = new PropertyInfo();
		pSalespersonCodeInfo.setName("pSalespersonCode");
		pSalespersonCodeInfo.setValue(pSalespersonCode);
		pSalespersonCodeInfo.setType(String.class);
		properties.add(pSalespersonCodeInfo);
		
		PropertyInfo hasActionPlanInfo = new PropertyInfo();
		hasActionPlanInfo.setName("hasActionPlan");
		hasActionPlanInfo.setValue(hasActionPlan);
		hasActionPlanInfo.setType(Integer.class);
		properties.add(hasActionPlanInfo);

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
		
		// BRISANJE STARIH PODATAKA I AZURIRANJE DATUMA POSLEDNJE SINHRONIZACIJE U ODGOVARAJUCOJ TABELI
		ContentValues cv = new ContentValues();
		if (pBusinessUnit.equals("")) {
			contentResolver.delete(ActionPlan.CONTENT_URI, ActionPlan.CUSTOMER_NO + "=?", new String[] { pCustomerNoa46 });
			cv.put(Customers.LAST_ACTION_PLAN_SYNC_DATE, DateUtils.formatDbDate(new Date()));
			contentResolver.update(Customers.CONTENT_URI, cv, Customers.CUSTOMER_NO + "=?", new String[] { pCustomerNoa46 });
		} else {
			contentResolver.delete(ActionPlan.CONTENT_URI, ActionPlan.CUSTOMER_NO + "=? AND " + ActionPlan.BUSINESS_UNIT_NO + "=?", new String[] { pCustomerNoa46, pBusinessUnit });
			cv.put(CustomerBusinessUnits.LAST_ACTION_PLAN_SYNC_DATE, DateUtils.formatDbDate(new Date()));
			contentResolver.update(CustomerBusinessUnits.CONTENT_URI, cv, CustomerBusinessUnits.CUSTOMER_NO + "=? AND " + CustomerBusinessUnits.UNIT_NO + "=?", new String[] { pCustomerNoa46, pBusinessUnit } );
		}
		
		int hasActionPlan = Integer.valueOf(soapResponse.getPropertyAsString("hasActionPlan"));
		if (hasActionPlan == 0) {
			return 0;
		}
		List<CustomerActionPlanDomain> parsedDomains = CSVDomainReader.parse(new StringReader(soapResponse.getPropertyAsString("pCSVString")), CustomerActionPlanDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedDomains);
		int numOfInserted = contentResolver.bulkInsert(ActionPlan.CONTENT_URI, valuesForInsert);
		return numOfInserted;
	}

	public String getpCSVString() {
		return pCSVString;
	}

	public void setpCSVString(String pCSVString) {
		this.pCSVString = pCSVString;
	}

	public String getpCustomerNoa46() {
		return pCustomerNoa46;
	}

	public void setpCustomerNoa46(String pCustomerNoa46) {
		this.pCustomerNoa46 = pCustomerNoa46;
	}

	public String getpBusinessUnit() {
		return pBusinessUnit;
	}

	public void setpBusinessUnit(String pBusinessUnit) {
		this.pBusinessUnit = pBusinessUnit;
	}

	public String getpSalespersonCode() {
		return pSalespersonCode;
	}

	public void setpSalespersonCode(String pSalespersonCode) {
		this.pSalespersonCode = pSalespersonCode;
	}

	public Integer getHasActionPlan() {
		return hasActionPlan;
	}

	public void setHasActionPlan(Integer hasActionPlan) {
		this.hasActionPlan = hasActionPlan;
	}

}
