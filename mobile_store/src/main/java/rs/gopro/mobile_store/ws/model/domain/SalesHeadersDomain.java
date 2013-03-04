package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.SentOrdersStatus;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class SalesHeadersDomain extends Domain {

	public String document_type;
	public String document_no;
	public String customer_no;
	public String order_date;
	
	public String order_status_for_shipment;
	public String financial_control_status;
	public String order_condition_status;
	
	public String used_credit_limit_by_employee;
	public String order_value_status;
	public String special_quote;
	public String prices_include_vat;

	private static final String[] COLUMNS = new String[] { "document_type", "document_no", "customer_no", "order_date", 
		"order_status_for_shipment", "financial_control_status", "order_condition_status", 
		"used_credit_limit_by_employee", "order_value_status", "special_quote", "prices_include_vat" };

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(SentOrdersStatus.DOCUMENT_TYPE, getDocument_type());
		contentValues.put(SentOrdersStatus.SENT_ORDER_NO, getDocument_no());
		contentValues.put(Customers.CUSTOMER_NO, getCustomer_no());
		contentValues.put(SentOrdersStatus.ORDER_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(getOrder_date()));
		contentValues.put(SentOrdersStatus.ORDER_STATUS_FOR_SHIPMENT, getOrder_status_for_shipment());
		contentValues.put(SentOrdersStatus.FIN_CONTROL_STATUS, getFinancial_control_status());
		contentValues.put(SentOrdersStatus.ORDER_CONDITION_STATUS, getOrder_condition_status());
		contentValues.put(SentOrdersStatus.USED_CREDIT_LIMIT_BY_EMPLOYEE, getUsed_credit_limit_by_employee());
		contentValues.put(SentOrdersStatus.ORDER_VALUE_STATUS, getOrder_value_status());
		contentValues.put(SentOrdersStatus.SPECIAL_QUOTE, getSpecial_quote());
		contentValues.put(SentOrdersStatus.PRICES_INCLUDE_VAT, getPrices_include_vat());
		// TODO maybe put some external way to set up some static data
		// here sales person is irrelevant, all data is regenerated per request
		//contentValues.put(SentOrdersStatus.SALES_PERSON_ID, "");
		
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		dataHolders.add(new RowItemDataHolder(Tables.CUSTOMERS, Customers.CUSTOMER_NO, getCustomer_no(), SentOrdersStatus.CUSTOMER_ID));
		//dataHolders.add(new RowItemDataHolder(Tables.SALES_PERSONS, Invoices.SALE_PERSON_NO, getSales_person_no(), Invoices.SALES_PERSON_ID));
		return dataHolders;
	}

	public String getDocument_type() {
		return document_type;
	}

	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}

	public String getDocument_no() {
		return document_no;
	}

	public void setDocument_no(String document_no) {
		this.document_no = document_no;
	}

	public String getCustomer_no() {
		return customer_no;
	}

	public void setCustomer_no(String customer_no) {
		this.customer_no = customer_no;
	}

	public String getOrder_date() {
		return order_date;
	}

	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}

	public String getOrder_status_for_shipment() {
		return order_status_for_shipment;
	}

	public void setOrder_status_for_shipment(String order_status_for_shipment) {
		this.order_status_for_shipment = order_status_for_shipment;
	}

	public String getFinancial_control_status() {
		return financial_control_status;
	}

	public void setFinancial_control_status(String financial_control_status) {
		this.financial_control_status = financial_control_status;
	}

	public String getOrder_condition_status() {
		return order_condition_status;
	}

	public void setOrder_condition_status(String order_condition_status) {
		this.order_condition_status = order_condition_status;
	}

	public String getUsed_credit_limit_by_employee() {
		return used_credit_limit_by_employee;
	}

	public void setUsed_credit_limit_by_employee(
			String used_credit_limit_by_employee) {
		this.used_credit_limit_by_employee = used_credit_limit_by_employee;
	}

	public String getOrder_value_status() {
		return order_value_status;
	}

	public void setOrder_value_status(String order_value_status) {
		this.order_value_status = order_value_status;
	}

	public String getSpecial_quote() {
		return special_quote;
	}

	public void setSpecial_quote(String special_quote) {
		this.special_quote = special_quote;
	}

	public String getPrices_include_vat() {
		return prices_include_vat;
	}

	public void setPrices_include_vat(String prices_include_vat) {
		this.prices_include_vat = prices_include_vat;
	}
}
