package rs.gopro.mobile_store.ws.model.domain;

import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.ActionPlan;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class CustomerActionPlanDomain extends Domain {
	
	public String customer_no;
	public String business_unit_no;
	public String item_no;
	public String item_type;
	public String line_turnover;
	
	private static final String[] COLUMNS = new String[] { "customer_no", "business_unit_no", "item_no", "item_type", "line_turnover" };

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ActionPlan.CUSTOMER_NO, getCustomer_no());
		contentValues.put(ActionPlan.BUSINESS_UNIT_NO, getBusiness_unit_no());
		contentValues.put(ActionPlan.ITEM_NO, getItem_no());
		contentValues.put(ActionPlan.ITEM_TYPE, getItem_type());
		contentValues.put(ActionPlan.LINE_TURNOVER, WsDataFormatEnUsLatin.toDoubleFromWs(getLine_turnover()));
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		return null;
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

	public String getItem_type() {
		return item_type;
	}

	public void setItem_type(String item_type) {
		this.item_type = item_type;
	}

	public String getLine_turnover() {
		return line_turnover;
	}

	public void setLine_turnover(String line_turnover) {
		this.line_turnover = line_turnover;
	}

}
