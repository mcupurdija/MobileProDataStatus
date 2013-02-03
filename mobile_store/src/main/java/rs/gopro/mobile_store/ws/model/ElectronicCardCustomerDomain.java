package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class ElectronicCardCustomerDomain extends Domain {

	public String customer_no;
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

	private static final String[] COLUMNS = new String[] { "customer_no", "item_no", "january_qty", "february_qty", "march_qty", "april_qty", "may_qty", "june_qty", "july_qty", "august_qty", "september_qty", "october_qty", "november_qty",
			"december_qty", "total_sale_qty_current_year", "total_sale_qty_prior_year", "total_turnover_current_year", "total_turnover_prior_year", "sales_line_counts_current_year", "sales_line_counts_prior_year" };

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ElectronicCardCustomer.CUSTOMER_NO,  getCustomer_no() );
		contentValues.put(ElectronicCardCustomer.ITEM_NO, getItem_no());
		contentValues.put(ElectronicCardCustomer.JANUARY_QTY, getJanuary_qty());
		contentValues.put(ElectronicCardCustomer.FEBRUARY_QTY, getFebruary_qty());
		contentValues.put(ElectronicCardCustomer.MARCH_QTY, getMarch_qty());
		contentValues.put(ElectronicCardCustomer.APRIL_QTY, getApril_qty());
		contentValues.put(ElectronicCardCustomer.MAY_QTY, getMay_qty() );
		contentValues.put(ElectronicCardCustomer.JUNE_QTY, getJune_qty());
		contentValues.put(ElectronicCardCustomer.JULY_QTY, getJuly_qty());
		contentValues.put(ElectronicCardCustomer.AUGUST_QTY, getAugust_qty());
		contentValues.put(ElectronicCardCustomer.SEPTEMBER_QTY, getSeptember_qty());
		contentValues.put(ElectronicCardCustomer.OCTOBER_QTY, getOctober_qty());
		contentValues.put(ElectronicCardCustomer.NOVEMBER_QTY, getNovember_qty() );
		contentValues.put(ElectronicCardCustomer.DECEMBER_QTY, getDecember_qty());
		contentValues.put(ElectronicCardCustomer.TOTAL_SALE_QTY_CURRENT_YEAR, getTotal_sale_qty_current_year());
		contentValues.put(ElectronicCardCustomer.TOTAL_SALE_QTY_PRIOR_YEAR, getTotal_sale_qty_prior_year());
		contentValues.put(ElectronicCardCustomer.TOTAL_TURNOVER_CURRENT_YEAR, getTotal_turnover_current_year());
		contentValues.put(ElectronicCardCustomer.TOTAL_TURNOVER_PRIOR_YEAR, getTotal_turnover_prior_year());
		contentValues.put(ElectronicCardCustomer.SALES_LINE_COUNTS_CURRENT_YEAR, getSales_line_counts_current_year());
		contentValues.put(ElectronicCardCustomer.SALES_LINE_COUNTS_PRIOR_YEAR, getSales_line_counts_prior_year());		
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

	
	
	
	
}
