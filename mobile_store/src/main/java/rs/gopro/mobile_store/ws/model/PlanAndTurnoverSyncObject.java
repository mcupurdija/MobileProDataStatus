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

public class PlanAndTurnoverSyncObject extends SyncObject {

	public static String TAG = "PlanAndTurnoverSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.PLAN_AND_TURNOVER_SYNC_ACTION";

	private String pSalespersonCode;
	private String pLastMonthPlanAsTxt;
	private String pLastMonthTurnoverAsTxt;
	private String pCurrentMonthTurnoverAsTxt;
	private String pCurrentMonthPlanAsTxt;
	
	public static final Creator<PlanAndTurnoverSyncObject> CREATOR = new Creator<PlanAndTurnoverSyncObject>() {
		
		@Override
		public PlanAndTurnoverSyncObject createFromParcel(Parcel source) {
			return new PlanAndTurnoverSyncObject(source);
		}

		@Override
		public PlanAndTurnoverSyncObject[] newArray(int size) {
			return new PlanAndTurnoverSyncObject[size];
		}

	};
	
	public PlanAndTurnoverSyncObject() {
		super();
	}
	
	public PlanAndTurnoverSyncObject(String pSalespersonCode) {
		super();
		this.pSalespersonCode = pSalespersonCode;
	}

	public PlanAndTurnoverSyncObject(Parcel parcel) {
		super(parcel);
		setpSalespersonCode(parcel.readString());
		setpCurrentMonthPlanAsTxt(parcel.readString());
		setpCurrentMonthTurnoverAsTxt(parcel.readString());
		setpLastMonthPlanAsTxt(parcel.readString());
		setpLastMonthTurnoverAsTxt(parcel.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpSalespersonCode());
		dest.writeString(getpCurrentMonthPlanAsTxt());
		dest.writeString(getpCurrentMonthTurnoverAsTxt());
		dest.writeString(getpLastMonthPlanAsTxt());
		dest.writeString(getpLastMonthTurnoverAsTxt());
	}

	@Override
	public String getWebMethodName() {
		return "GetSalespersonPlanAndTurnover";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>();
		
		PropertyInfo salespersonCode = new PropertyInfo();
		salespersonCode.setName("pSalespersonCode");
		salespersonCode.setValue(pSalespersonCode);
		salespersonCode.setType(String.class);
		properies.add(salespersonCode);
		
		PropertyInfo lastMonthPlan = new PropertyInfo();
		lastMonthPlan.setName("pLastMonthPlanAsTxt");
		lastMonthPlan.setValue(pLastMonthPlanAsTxt);
		lastMonthPlan.setType(String.class);
		properies.add(lastMonthPlan);
		
		PropertyInfo currentMonthPlanAsTxt = new PropertyInfo();
		currentMonthPlanAsTxt.setName("pCurrentMonthPlanAsTxt");
		currentMonthPlanAsTxt.setValue(pCurrentMonthPlanAsTxt);
		currentMonthPlanAsTxt.setType(String.class);
		properies.add(currentMonthPlanAsTxt);
		
		PropertyInfo lastMonthTurnoverAsTxt = new PropertyInfo();
		lastMonthTurnoverAsTxt.setName("pLastMonthTurnoverAsTxt");
		lastMonthTurnoverAsTxt.setValue(pLastMonthTurnoverAsTxt);
		lastMonthTurnoverAsTxt.setType(String.class);
		properies.add(lastMonthTurnoverAsTxt);
		
		PropertyInfo currentMonthTurnoverAsTxt = new PropertyInfo();
		currentMonthTurnoverAsTxt.setName("pCurrentMonthTurnoverAsTxt");
		currentMonthTurnoverAsTxt.setValue(pCurrentMonthTurnoverAsTxt);
		currentMonthTurnoverAsTxt.setType(String.class);
		properies.add(currentMonthTurnoverAsTxt);
		
		return properies;
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
	protected int parseAndSave(ContentResolver contentResolver,
			SoapPrimitive soapResponse) throws CSVParseException {
		
		return 0;
	}

	private void bindResponseProperties(SoapObject soapResponse) {

		this.pLastMonthPlanAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pLastMonthPlanAsTxt"));
		this.pLastMonthTurnoverAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pLastMonthTurnoverAsTxt"));
		this.pCurrentMonthPlanAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pCurrentMonthPlanAsTxt"));
		this.pCurrentMonthTurnoverAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pCurrentMonthTurnoverAsTxt"));
	}
	
	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		bindResponseProperties(soapResponse);
		return 0;
	}



	public String getpSalespersonCode() {
		return pSalespersonCode;
	}



	public void setpSalespersonCode(String pSalespersonCode) {
		this.pSalespersonCode = pSalespersonCode;
	}



	public String getpLastMonthPlanAsTxt() {
		return pLastMonthPlanAsTxt;
	}



	public void setpLastMonthPlanAsTxt(String pLastMonthPlanAsTxt) {
		this.pLastMonthPlanAsTxt = pLastMonthPlanAsTxt;
	}



	public String getpLastMonthTurnoverAsTxt() {
		return pLastMonthTurnoverAsTxt;
	}



	public void setpLastMonthTurnoverAsTxt(String pLastMonthTurnoverAsTxt) {
		this.pLastMonthTurnoverAsTxt = pLastMonthTurnoverAsTxt;
	}



	public String getpCurrentMonthTurnoverAsTxt() {
		return pCurrentMonthTurnoverAsTxt;
	}



	public void setpCurrentMonthTurnoverAsTxt(String pCurrentMonthTurnoverAsTxt) {
		this.pCurrentMonthTurnoverAsTxt = pCurrentMonthTurnoverAsTxt;
	}



	public String getpCurrentMonthPlanAsTxt() {
		return pCurrentMonthPlanAsTxt;
	}



	public void setpCurrentMonthPlanAsTxt(String pCurrentMonthPlanAsTxt) {
		this.pCurrentMonthPlanAsTxt = pCurrentMonthPlanAsTxt;
	}

}
