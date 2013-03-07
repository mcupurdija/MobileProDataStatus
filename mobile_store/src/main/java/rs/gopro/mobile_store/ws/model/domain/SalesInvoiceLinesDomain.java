package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.InvoiceLine;
import rs.gopro.mobile_store.provider.MobileStoreContract.Invoices;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class SalesInvoiceLinesDomain extends Domain {

	String invoices_no;
	String line_no;
	String customer_no;
	String document_type;
	
	String item_no;
	String location_code;
	String description;
	
	String quantity;
	String unit_price;
	String vat_percent;
	String line_discount_percent;
	
	String line_discount_amount;
	String amount_including_vat;
	String inv_discount_amount;
	String unit_of_measure_code;
	String price_include_vat;

	private static final String[] COLUMNS = new String[] { "invoices_no", "line_no", "customer_no", "document_type", 
		"item_no", "location_code", "description", 
		"quantity", "unit_price", "vat_percent", "line_discount_percent",
		"line_discount_amount", "amount_including_vat", "inv_discount_amount", "unit_of_measure_code", "price_include_vat"};

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(Invoices.INVOICE_NO, invoices_no);
		contentValues.put(InvoiceLine.LINE_NO, line_no);
		contentValues.put(Customers.CUSTOMER_NO, customer_no);
		contentValues.put(InvoiceLine.TYPE, document_type);
		contentValues.put(Items.ITEM_NO, item_no);
		contentValues.put(InvoiceLine.LOCATION_CODE, location_code);
		contentValues.put(InvoiceLine.DESCRIPTION, description);
		contentValues.put(InvoiceLine.QUANTITY, quantity);
		contentValues.put(InvoiceLine.UNIT_PRICE, unit_price);
		contentValues.put(InvoiceLine.VAT_PERCENT, vat_percent);
		contentValues.put(InvoiceLine.LINE_DISCOUNT_PERCENT, line_discount_percent);
		contentValues.put(InvoiceLine.LINE_DISCOUNT_AMOUNT, line_discount_amount);
		contentValues.put(InvoiceLine.AMOUNT_INCLUDING_VAT, amount_including_vat);
		contentValues.put(InvoiceLine.INV_DISCOUNT_AMOUNT, inv_discount_amount);
		contentValues.put(InvoiceLine.UNIT_OF_MEASURE_CODE, unit_of_measure_code);
		contentValues.put(InvoiceLine.PRICE_INCLUDE_VAT, price_include_vat);

		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		dataHolders.add(new RowItemDataHolder(Tables.CUSTOMERS, Customers.CUSTOMER_NO, customer_no, InvoiceLine.CUSTOMER_ID));
		dataHolders.add(new RowItemDataHolder(Tables.INVOICES, Invoices.INVOICE_NO, invoices_no, InvoiceLine.INVOICES_ID));
		dataHolders.add(new RowItemDataHolder(Tables.ITEMS, Items.ITEM_NO, item_no, InvoiceLine.NO));
		return dataHolders;
	}

}
