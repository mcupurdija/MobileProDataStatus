package rs.gopro.mobile_store.ws.model;

import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class RealizedVisitsDomain extends Domain {

	private String sales_person_no;
	private String visit_date;
	public int potental_customer;
	private String customer_no;
	private String arrival_time;
	private String departure_time;
	private String entry_type;
	private String odometer;
	private String visit_result;
	public String note;

	private static final String[] COLUMNS = new String[] { "salesPerson_no", "visit_date", "potental_customer", "customer_no", "arrival_time", "departure_time", "entry_type", "odometer", "visit_result", "note" };

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Visits.SALES_PERSON_ID, getSales_person_no());
		contentValues.put(Visits.VISIT_DATE, getVisit_date());

		contentValues.put(Visits.CUSTOMER_NO, getCustomer_no());
		contentValues.put(Visits.ARRIVAL_TIME, getArrival_time());
		contentValues.put(Visits.DEPARTURE_TIME, getDeparture_time());
		contentValues.put(Visits.ENTRY_TYPE, getEntry_type());
		contentValues.put(Visits.ODOMETER, getOdometer());
		contentValues.put(Visits.VISIT_RESULT, getVisit_result());
		contentValues.put(Visits.VISIT_TYPE, Integer.valueOf(1));
		contentValues.put(Visits.IS_SENT, Integer.valueOf(1));
		contentValues.put(Visits.NOTE, getNote());
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSales_person_no() {
		return sales_person_no;
	}

	public void setSales_person_no(String sales_person_no) {
		this.sales_person_no = sales_person_no;
	}

	public String getVisit_date() {
		return visit_date;
	}

	public void setVisit_date(String visit_date) {
		this.visit_date = visit_date;
	}

	public String getArrival_time() {
		return arrival_time;
	}

	public void setArrival_time(String arrival_time) {
		this.arrival_time = arrival_time;
	}

	public String getDeparture_time() {
		return departure_time;
	}

	public void setDeparture_time(String departure_time) {
		this.departure_time = departure_time;
	}

	public String getEntry_type() {
		return entry_type;
	}

	public void setEntry_type(String entry_type) {
		this.entry_type = entry_type;
	}

	public String getOdometer() {
		return odometer;
	}

	public void setOdometer(String odometer) {
		this.odometer = odometer;
	}

	public String getVisit_result() {
		return visit_result;
	}

	public void setVisit_result(String visit_result) {
		this.visit_result = visit_result;
	}

	public String getCustomer_no() {
		return customer_no;
	}

	public void setCustomer_no(String customer_no) {
		this.customer_no = customer_no;
	}

	public int getPotental_customer() {
		return potental_customer;
	}

	public void setPotental_customer(int potental_customer) {
		this.potental_customer = potental_customer;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
