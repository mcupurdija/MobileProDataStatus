package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class ElectronicCardCustomerDomain extends Domain {

	public String customer_no;
	public String business_unit_no;
	public String item_no;
	public String january_qty;
	public String february_qty;
	public String march_qty;
	public String april_qty;
	public String may_qty;
	public String june_qty;
	public String july_qty;
	public String august_qty;
	public String september_qty;
	public String october_qty;
	public String november_qty;
	public String december_qty;
	public String total_sale_qty_current_year;
	public String total_sale_qty_prior_year;
	public String total_turnover_current_year;
	public String total_turnover_prior_year;
	public String sales_line_counts_current_year;
	public String sales_line_counts_prior_year;
	public String last_line_discount;
	public String color;
	public String entry_type;
	public String sorting_index;
	public String sale_per_branch_index;
	public String item_description;
	
	private static final String[] COLUMNS = new String[] { "customer_no", "business_unit_code", "item_no", "january_qty", "february_qty", "march_qty", "april_qty", "may_qty", "june_qty", "july_qty", "august_qty", "september_qty", "october_qty", "november_qty",
			"december_qty", "total_sale_qty_current_year", "total_sale_qty_prior_year", "total_turnover_current_year", "total_turnover_prior_year", "sales_line_counts_current_year", "sales_line_counts_prior_year", "last_line_discount", "color", "entry_type", 
			"sorting_index", "sale_per_branch_index" };

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ElectronicCardCustomer.CUSTOMER_NO, getCustomer_no());
		contentValues.put(ElectronicCardCustomer.BUSINESS_UNIT_NO, getBusiness_unit_no());
		contentValues.put(ElectronicCardCustomer.ITEM_NO, getItem_no());
		contentValues.put(ElectronicCardCustomer.JANUARY_QTY, WsDataFormatEnUsLatin.toDoubleFromWs(getJanuary_qty()));
		contentValues.put(ElectronicCardCustomer.FEBRUARY_QTY, WsDataFormatEnUsLatin.toDoubleFromWs(getFebruary_qty()));
		contentValues.put(ElectronicCardCustomer.MARCH_QTY, WsDataFormatEnUsLatin.toDoubleFromWs(getMarch_qty()));
		contentValues.put(ElectronicCardCustomer.APRIL_QTY, WsDataFormatEnUsLatin.toDoubleFromWs(getApril_qty()));
		contentValues.put(ElectronicCardCustomer.MAY_QTY, WsDataFormatEnUsLatin.toDoubleFromWs(getMay_qty()));
		contentValues.put(ElectronicCardCustomer.JUNE_QTY, WsDataFormatEnUsLatin.toDoubleFromWs(getJune_qty()));
		contentValues.put(ElectronicCardCustomer.JULY_QTY, WsDataFormatEnUsLatin.toDoubleFromWs(getJuly_qty()));
		contentValues.put(ElectronicCardCustomer.AUGUST_QTY, WsDataFormatEnUsLatin.toDoubleFromWs(getAugust_qty()));
		contentValues.put(ElectronicCardCustomer.SEPTEMBER_QTY, WsDataFormatEnUsLatin.toDoubleFromWs(getSeptember_qty()));
		contentValues.put(ElectronicCardCustomer.OCTOBER_QTY, WsDataFormatEnUsLatin.toDoubleFromWs(getOctober_qty()));
		contentValues.put(ElectronicCardCustomer.NOVEMBER_QTY, WsDataFormatEnUsLatin.toDoubleFromWs(getNovember_qty()));
		contentValues.put(ElectronicCardCustomer.DECEMBER_QTY, WsDataFormatEnUsLatin.toDoubleFromWs(getDecember_qty()));
		contentValues.put(ElectronicCardCustomer.TOTAL_SALE_QTY_CURRENT_YEAR, WsDataFormatEnUsLatin.toDoubleFromWs(getTotal_sale_qty_current_year()));
		contentValues.put(ElectronicCardCustomer.TOTAL_SALE_QTY_PRIOR_YEAR, WsDataFormatEnUsLatin.toDoubleFromWs(getTotal_sale_qty_prior_year()));
		contentValues.put(ElectronicCardCustomer.TOTAL_TURNOVER_CURRENT_YEAR, WsDataFormatEnUsLatin.toDoubleFromWs(getTotal_turnover_current_year()));
		contentValues.put(ElectronicCardCustomer.TOTAL_TURNOVER_PRIOR_YEAR, WsDataFormatEnUsLatin.toDoubleFromWs(getTotal_turnover_prior_year()));
		contentValues.put(ElectronicCardCustomer.SALES_LINE_COUNTS_CURRENT_YEAR, WsDataFormatEnUsLatin.toDoubleFromWs(getSales_line_counts_current_year()));
		contentValues.put(ElectronicCardCustomer.SALES_LINE_COUNTS_PRIOR_YEAR, WsDataFormatEnUsLatin.toDoubleFromWs(getSales_line_counts_prior_year()));
		contentValues.put(ElectronicCardCustomer.LAST_LINE_DISCOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(getLast_line_discount()));
		contentValues.put(ElectronicCardCustomer.COLOR, getColor());
		contentValues.put(ElectronicCardCustomer.ENTRY_TYPE, getEntry_type());
		contentValues.put(ElectronicCardCustomer.SORTING_INDEX, getSorting_index());
		contentValues.put(ElectronicCardCustomer.SALE_PER_BRANCH_INDEX, getSale_per_branch_index());
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> transferNoToIdList = new ArrayList<RowItemDataHolder>();
		transferNoToIdList.add(new RowItemDataHolder(Tables.CUSTOMERS, ElectronicCardCustomer.CUSTOMER_NO, getCustomer_no(), ElectronicCardCustomer.CUSTOMER_ID));
		transferNoToIdList.add(new RowItemDataHolder(Tables.ITEMS, ElectronicCardCustomer.ITEM_NO, getItem_no(), ElectronicCardCustomer.ITEM_ID));
		return transferNoToIdList;

	}

	public String getCustomer_no() {
		return customer_no;
	}

	public void setCustomer_no(String customer_no) {
		this.customer_no = customer_no;
	}

	public String getBusiness_unit_no() {
		return business_unit_no;
	}

	public void setBusiness_unit_no(String business_unit_no) {
		this.business_unit_no = business_unit_no;
	}

	public String getItem_no() {
		return item_no;
	}

	public void setItem_no(String item_no) {
		this.item_no = item_no;
	}

	public String getJanuary_qty() {
		return january_qty;
	}

	public void setJanuary_qty(String january_qty) {
		this.january_qty = january_qty;
	}

	public String getFebruary_qty() {
		return february_qty;
	}

	public void setFebruary_qty(String february_qty) {
		this.february_qty = february_qty;
	}

	public String getMarch_qty() {
		return march_qty;
	}

	public void setMarch_qty(String march_qty) {
		this.march_qty = march_qty;
	}

	public String getApril_qty() {
		return april_qty;
	}

	public void setApril_qty(String april_qty) {
		this.april_qty = april_qty;
	}

	public String getMay_qty() {
		return may_qty;
	}

	public void setMay_qty(String may_qty) {
		this.may_qty = may_qty;
	}

	public String getJune_qty() {
		return june_qty;
	}

	public void setJune_qty(String june_qty) {
		this.june_qty = june_qty;
	}

	public String getJuly_qty() {
		return july_qty;
	}

	public void setJuly_qty(String july_qty) {
		this.july_qty = july_qty;
	}

	public String getAugust_qty() {
		return august_qty;
	}

	public void setAugust_qty(String august_qty) {
		this.august_qty = august_qty;
	}

	public String getSeptember_qty() {
		return september_qty;
	}

	public void setSeptember_qty(String september_qty) {
		this.september_qty = september_qty;
	}

	public String getOctober_qty() {
		return october_qty;
	}

	public void setOctober_qty(String october_qty) {
		this.october_qty = october_qty;
	}

	public String getNovember_qty() {
		return november_qty;
	}

	public void setNovember_qty(String november_qty) {
		this.november_qty = november_qty;
	}

	public String getDecember_qty() {
		return december_qty;
	}

	public void setDecember_qty(String december_qty) {
		this.december_qty = december_qty;
	}

	public String getTotal_sale_qty_current_year() {
		return total_sale_qty_current_year;
	}

	public void setTotal_sale_qty_current_year(String total_sale_qty_current_year) {
		this.total_sale_qty_current_year = total_sale_qty_current_year;
	}

	public String getTotal_sale_qty_prior_year() {
		return total_sale_qty_prior_year;
	}

	public void setTotal_sale_qty_prior_year(String total_sale_qty_prior_year) {
		this.total_sale_qty_prior_year = total_sale_qty_prior_year;
	}

	public String getTotal_turnover_current_year() {
		return total_turnover_current_year;
	}

	public void setTotal_turnover_current_year(String total_turnover_current_year) {
		this.total_turnover_current_year = total_turnover_current_year;
	}

	public String getTotal_turnover_prior_year() {
		return total_turnover_prior_year;
	}

	public void setTotal_turnover_prior_year(String total_turnover_prior_year) {
		this.total_turnover_prior_year = total_turnover_prior_year;
	}

	public String getSales_line_counts_current_year() {
		return sales_line_counts_current_year;
	}

	public void setSales_line_counts_current_year(String sales_line_counts_current_year) {
		this.sales_line_counts_current_year = sales_line_counts_current_year;
	}

	public String getSales_line_counts_prior_year() {
		return sales_line_counts_prior_year;
	}

	public void setSales_line_counts_prior_year(String sales_line_counts_prior_year) {
		this.sales_line_counts_prior_year = sales_line_counts_prior_year;
	}

	public String getLast_line_discount() {
		return last_line_discount;
	}

	public void setLast_line_discount(String last_line_discount) {
		this.last_line_discount = last_line_discount;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getEntry_type() {
		return entry_type;
	}

	public void setEntry_type(String entry_type) {
		this.entry_type = entry_type;
	}

	public String getSorting_index() {
		return sorting_index;
	}

	public void setSorting_index(String sorting_index) {
		this.sorting_index = sorting_index;
	}

	public String getItem_description() {
		return item_description;
	}

	public void setItem_description(String item_description) {
		this.item_description = item_description;
	}

	public String getSale_per_branch_index() {
		return sale_per_branch_index;
	}

	public void setSale_per_branch_index(String sale_per_branch_index) {
		this.sale_per_branch_index = sale_per_branch_index;
	}
	
}
