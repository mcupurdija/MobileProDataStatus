package rs.gopro.mobile_store.gps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GpsModel {

	@SerializedName("SALESPERSONNO")
	@Expose
	private String salesPersonNo;
	
	@SerializedName("BUYERNAME")
	@Expose
	private String buyerName;
	
	@SerializedName("ENTRYTYPE")
	@Expose
	private String entryType;
	
	@SerializedName("LATITUDE")
	@Expose
	private String latitude;
	
	@SerializedName("LONGITUDE")
	@Expose
	private String longitude;
	
	@SerializedName("ACCURACY")
	@Expose
	private String accuracy;
	
	@SerializedName("DATE")
	@Expose
	private String date;
	
	@SerializedName("TIME")
	@Expose
	private String time;
	
	public String getSalesPersonNo() {
		return salesPersonNo;
	}
	public void setSalesPersonNo(String salesPersonNo) {
		this.salesPersonNo = salesPersonNo;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getEntryType() {
		return entryType;
	}
	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitide) {
		this.latitude = latitide;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

}
