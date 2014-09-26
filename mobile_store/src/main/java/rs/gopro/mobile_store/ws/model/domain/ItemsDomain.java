package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class ItemsDomain extends Domain {
	/*
	 * 1 No. Code 20 2 Description Text 50 3 Base Unit of Measure Code 10 4 Item
	 * Category Code Code 10 5 Product Group Code Code 10 6 Item Campaign Status
	 * Option 7 OverStock Status Option 8 Item connected SpecShipItem Code 20 9
	 * Campaign Sales Price (RSD) Decimal 10 Campaign Code Code 10 11 Campaign
	 * Starting Date Date 12 Campaign Ending Date Date
	 */
	// private String _id;
	public String item_no;
	public String description;
	public String description2;
	public String unit_of_measure;
	//public String category_code;
	//public String group_code;
	public String campaign_status;
	public String overstock_status;
	public String inventory_item_category;
	public String connected_spec_ship_item;
	public String unit_sales_price_eur;
	public String unit_sales_price_din;
	public String min_qty;
	public String bom_items;
	public String linked_items;
	
	//public String campaign_code;
	//public String cmpaign_start_date;
	//public String campaign_end_date;

	// private String created_date;
	// private String created_by;
	// private String updated_date;
	// private String updated_by;

	public ItemsDomain() {
	}

	private static final String[] COLUMNS = new String[] { "item_no", "description", "unit_of_measure", "campaign_status", "overstock_status", "inventory_item_category", "connected_spec_ship_item", "min_qty", "unit_sales_price_din", "bom_items", "linked_items" };

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
		//contentValues.put(Items.CATEGORY_CODE, getCategory_code());
		//contentValues.put(Items.GROUP_CODE, getGroup_code());
		contentValues.put(Items.CAMPAIGN_STATUS, getCampaign_status());
		contentValues.put(Items.OVERSTOCK_STATUS, getOverstock_status());
		contentValues.put(Items.INVENTORY_ITEM_CATEGORY, getInventory_item_category());
		contentValues.put(Items.CONNECTED_SPEC_SHIP_ITEM, getConnected_spec_ship_item());
		// TODO data conversion
		contentValues.put(Items.UNIT_SALES_PRICE_DIN, WsDataFormatEnUsLatin.toDoubleFromWs(getUnit_sales_price_din().length() < 1 ? "0" : getUnit_sales_price_din()));
		if (!getMin_qty().equals("0")) {
			contentValues.put(Items.MIN_QTY, getMin_qty());
		} else {
			contentValues.put(Items.MIN_QTY, 1);
		}
		contentValues.put(Items.BOM_ITEMS, getBom_items());
		contentValues.put(Items.LINKED_ITEMS, getLinked_items());
		
		//contentValues.put(Items.CAMPAIGN_CODE, getCampaign_code());
		// TODO data conversion
		//contentValues.put(Items.CMPAIGN_START_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(getCmpaign_start_date().length() < 1 ? DateUtils.getDbDummyDate() : getCmpaign_start_date()));
		//contentValues.put(Items.CAMPAIGN_END_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(getCampaign_end_date().length() < 1 ? DateUtils.getDbDummyDate() : getCampaign_end_date()));
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		return dataHolders;
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

	public String getOverstock_status() {
		return overstock_status;
	}

	public void setOverstock_status(String overstock_status) {
		this.overstock_status = overstock_status;
	}

	public String getInventory_item_category() {
		return inventory_item_category;
	}

	public void setInventory_item_category(String inventory_item_category) {
		this.inventory_item_category = inventory_item_category;
	}

	public String getUnit_sales_price_din() {
		return unit_sales_price_din;
	}

	public void setUnit_sales_price_din(String unit_sales_price_din) {
		this.unit_sales_price_din = unit_sales_price_din;
	}

	public String getMin_qty() {
		return min_qty;
	}

	public void setMin_qty(String min_qty) {
		this.min_qty = min_qty;
	}

	public String getBom_items() {
		return bom_items;
	}

	public void setBom_items(String bom_items) {
		this.bom_items = bom_items;
	}

	public String getLinked_items() {
		return linked_items;
	}

	public void setLinked_items(String linked_items) {
		this.linked_items = linked_items;
	}

}
