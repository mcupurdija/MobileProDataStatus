package rs.gopro.mobile_store.ws.model;

import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import android.content.ContentValues;

public class ItemsDomain extends Domain {
	/* 	1	No.	Code	20
	2	Description	Text	50
	3	Base Unit of Measure	Code	10
	4	Item Category Code	Code	10
	5	Product Group Code	Code	10
	6	Item Campaign Status	Option	
	7	OverStock Status	Option	
	8	Item connected SpecShipItem	Code	20
	9	Campaign Sales Price (RSD)	Decimal	
	10	Campaign Code	Code	10
	11	Campaign Starting Date	Date	
	12	Campaign Ending Date	Date */
//	private String _id;
	public String item_no;
	public String description;
	public String description2;
	public String unit_of_measure;
	public String category_code;
	public String group_code;
	public String campaign_status;
	public String overstock_status;
	public String connected_spec_ship_item;
	public String unit_sales_price_eur;
	public String unit_sales_price_din;
	public String campaign_code;
	public String cmpaign_start_date;
	public String campaign_end_date;
//	private String created_date;
//	private String created_by;
//	private String updated_date;
//	private String updated_by;
	
	public ItemsDomain() {
	}
	
	private static final String[] COLUMNS = new String[] {"item_no", "description", "unit_of_measure", "category_code", "group_code", "campaign_status", "overstock_status", "connected_spec_ship_item", "unit_sales_price_din", "campaign_code", "cmpaign_start_date", "campaign_end_date"};
	
	
	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Items.ITEM_NO, getItem_no());
		contentValues.put(Items.DESCRIPTION, getDescription());
		contentValues.put(Items.UNIT_OF_MEASURE, getUnit_of_measure());
		contentValues.put(Items.CATEGORY_CODE, getCategory_code());
		contentValues.put(Items.GROUP_CODE, getGroup_code());
		contentValues.put(Items.CAMPAIGN_STATUS, getCampaign_status());
		contentValues.put(Items.OVERSTOCK_STATUS, getOverstock_status());
		contentValues.put(Items.CONNECTED_SPEC_SHIP_ITEM, getConnected_spec_ship_item());
		// TODO data conversion
		contentValues.put(Items.UNIT_SALES_PRICE_DIN, getUnit_sales_price_din());
		contentValues.put(Items.CAMPAIGN_CODE, getCampaign_code());
		// TODO data conversion
		contentValues.put(Items.CMPAIGN_START_DATE, getCmpaign_start_date());
		contentValues.put(Items.CAMPAIGN_END_DATE, getCampaign_end_date());
		return contentValues;
	}
	
	public String getItem_no() {
		return item_no;
	}
	public void setItem_no(String item_no) {
		this.item_no = item_no;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription2() {
		return description2;
	}
	public void setDescription2(String description2) {
		this.description2 = description2;
	}
	public String getUnit_of_measure() {
		return unit_of_measure;
	}
	public void setUnit_of_measure(String unit_of_measure) {
		this.unit_of_measure = unit_of_measure;
	}
	public String getCategory_code() {
		return category_code;
	}
	public void setCategory_code(String category_code) {
		this.category_code = category_code;
	}
	public String getGroup_code() {
		return group_code;
	}
	public void setGroup_code(String group_code) {
		this.group_code = group_code;
	}
	public String getCampaign_status() {
		return campaign_status;
	}
	public void setCampaign_status(String campaign_status) {
		this.campaign_status = campaign_status;
	}
	public String getConnected_spec_ship_item() {
		return connected_spec_ship_item;
	}
	public void setConnected_spec_ship_item(String connected_spec_ship_item) {
		this.connected_spec_ship_item = connected_spec_ship_item;
	}
	public String getUnit_sales_price_eur() {
		return unit_sales_price_eur;
	}
	public void setUnit_sales_price_eur(String unit_sales_price_eur) {
		this.unit_sales_price_eur = unit_sales_price_eur;
	}
	public String getUnit_sales_price_din() {
		return unit_sales_price_din;
	}
	public void setUnit_sales_price_din(String unit_sales_price_din) {
		this.unit_sales_price_din = unit_sales_price_din;
	}
	public String getCampaign_code() {
		return campaign_code;
	}
	public void setCampaign_code(String campaign_code) {
		this.campaign_code = campaign_code;
	}
	public String getCmpaign_start_date() {
		return cmpaign_start_date;
	}
	public void setCmpaign_start_date(String cmpaign_start_date) {
		this.cmpaign_start_date = cmpaign_start_date;
	}
	public String getCampaign_end_date() {
		return campaign_end_date;
	}
	public void setCampaign_end_date(String campaign_end_date) {
		this.campaign_end_date = campaign_end_date;
	}
	public String getOverstock_status() {
		return overstock_status;
	}
	public void setOverstock_status(String overstock_status) {
		this.overstock_status = overstock_status;
	}
}
