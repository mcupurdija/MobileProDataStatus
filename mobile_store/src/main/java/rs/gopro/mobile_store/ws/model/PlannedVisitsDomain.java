package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class PlannedVisitsDomain extends Domain {

	public String sales_person_no;
	public String visit_date;
	public String arrival_time;
	public String is_deleted;
	public String departure_time;
	public String potental_customer;
	public String customer_no;
	
	public String note;

	private static final String[] COLUMNS = new String[] { "sales_person_no", "visit_date", "arrival_time", "is_deleted", "departure_time", "potential_customer", "customer_no", "note" };

	@Override
	public String[] getCSVMappingStrategy() {

		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Visits.SALE_PERSON_NO, getSales_person_no());
		contentValues.put(Visits.VISIT_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(getVisit_date()));

		contentValues.put(Visits.CUSTOMER_NO, getCustomer_no());
		contentValues.put(Visits.ARRIVAL_TIME,  WsDataFormatEnUsLatin.toDbTimeeFromWsString(getVisit_date(), getArrival_time()));
		contentValues.put(Visits.DEPARTURE_TIME,  WsDataFormatEnUsLatin.toDbTimeeFromWsString(getVisit_date(), getDeparture_time()));
		contentValues.put(Visits.NOTE, getNote());
		contentValues.put(Visits.VISIT_TYPE, Integer.valueOf(0));
		contentValues.put(Visits.IS_SENT, Integer.valueOf(1));
		contentValues.put(Visits.IS_DELETED, getIs_deleted());
		return contentValues;
	}

	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> transferNoToIdList = new ArrayList<RowItemDataHolder>();
		transferNoToIdList.add(new RowItemDataHolder(Tables.SALES_PERSONS, Visits.SALE_PERSON_NO, getSales_person_no(), Visits.SALES_PERSON_ID));
		transferNoToIdList.add(new RowItemDataHolder(Tables.CUSTOMERS, Visits.CUSTOMER_NO, getCustomer_no(), Visits.CUSTOMER_ID));
		return transferNoToIdList;
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

	public String getCustomer_no() {
		return customer_no;
	}

	public void setCustomer_no(String customer_no) {
		this.customer_no = customer_no;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(String is_deleted) {
		this.is_deleted = is_deleted;
	}

}
