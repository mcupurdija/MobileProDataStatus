package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Invoices;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class SalesDocumentsDomain extends Domain {

	public String customer_no;
	public String posting_date;
	public String document_type;
	public String invoice_no;
	public String sales_person_no;
	public String open;
	public String due_date;
	public String remaining_amount;
	public String original_amount;
	public String prices_include_vat;

	private static final String[] COLUMNS = new String[] { "customer_no", "posting_date", "document_type", "invoice_no", "sales_person_no", "open", "due_date", "remaining_amount", "original_amount", "prices_include_vat" };

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Invoices.CUSTOMER_NO, getCustomer_no());
		contentValues.put(Invoices.POSTING_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(getPosting_date()));
		contentValues.put(Invoices.DOCUMENT_TYPE, getDocument_type());
		contentValues.put(Invoices.INVOICE_NO, getInvoice_no());
		contentValues.put(Invoices.REMAINING_AMOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(getRemaining_amount()));
		contentValues.put(Invoices.SALE_PERSON_NO, getSales_person_no());
		contentValues.put(Invoices.OPEN, getOpen());
		contentValues.put(Invoices.DUE_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(getDue_date()));
		contentValues.put(Invoices.ORIGINAL_AMOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(getOriginal_amount()));
		contentValues.put(Invoices.PRICES_INCLUDE_VAT, WsDataFormatEnUsLatin.toDoubleFromWs(getPrices_include_vat()));

		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		dataHolders.add(new RowItemDataHolder(Tables.CUSTOMERS, Invoices.CUSTOMER_NO, getCustomer_no(), Invoices.CUSTOMER_ID));
		dataHolders.add(new RowItemDataHolder(Tables.SALES_PERSONS, Invoices.SALE_PERSON_NO, getSales_person_no(), Invoices.SALES_PERSON_ID));
		return dataHolders;
	}

	public String getCustomer_no() {
		return customer_no;
	}

	public void setCustomer_no(String customer_no) {
		this.customer_no = customer_no;
	}

	public String getPosting_date() {
		return posting_date;
	}

	public void setPosting_date(String posting_date) {
		this.posting_date = posting_date;
	}

	public String getDocument_type() {
		return document_type;
	}

	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}

	public String getInvoice_no() {
		return invoice_no;
	}

	public void setInvoice_no(String invoice_no) {
		this.invoice_no = invoice_no;
	}

	public String getRemaining_amount() {
		return remaining_amount;
	}

	public void setRemaining_amount(String remaining_amount) {
		this.remaining_amount = remaining_amount;
	}

	public String getSales_person_no() {
		return sales_person_no;
	}

	public void setSales_person_no(String sales_person_no) {
		this.sales_person_no = sales_person_no;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getDue_date() {
		return due_date;
	}

	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}

	public String getOriginal_amount() {
		return original_amount;
	}

	public void setOriginal_amount(String original_amount) {
		this.original_amount = original_amount;
	}

	public String getPrices_include_vat() {
		return prices_include_vat;
	}

	public void setPrices_include_vat(String prices_include_vat) {
		this.prices_include_vat = prices_include_vat;
	}

}
