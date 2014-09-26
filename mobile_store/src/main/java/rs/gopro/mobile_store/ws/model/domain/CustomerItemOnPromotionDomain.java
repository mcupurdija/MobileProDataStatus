package rs.gopro.mobile_store.ws.model.domain;

import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.ItemsOnPromotion;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class CustomerItemOnPromotionDomain extends Domain {

	public String item_no;
	public String branch_code;
	public String valid_from_date;
	public String valid_to_date;
	public String price;
	public String comment;
	public String discount;
	
	private static final String[] COLUMNS = new String[] { "item_no", "branch_code", "valid_from_date", "valid_to_date", "price", "comment", "discount" };
	
	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ItemsOnPromotion.ITEM_NO, getItem_no());
		contentValues.put(ItemsOnPromotion.BRANCH_CODE, getBranch_code());
		contentValues.put(ItemsOnPromotion.VALID_FROM_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(getValid_from_date()));
		contentValues.put(ItemsOnPromotion.VALID_TO_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(getValid_to_date()));
		contentValues.put(ItemsOnPromotion.PRICE, WsDataFormatEnUsLatin.toDoubleFromWs(getPrice()));
		contentValues.put(ItemsOnPromotion.COMMENT, getComment());
		contentValues.put(ItemsOnPromotion.DISCOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(getDiscount()));
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		return null;
	}

	public String getItem_no() {
		return item_no;
	}

	public void setItem_no(String item_no) {
		this.item_no = item_no;
	}

	public String getBranch_code() {
		return branch_code;
	}

	public void setBranch_code(String branch_code) {
		this.branch_code = branch_code;
	}

	public String getValid_from_date() {
		return valid_from_date;
	}

	public void setValid_from_date(String valid_from_date) {
		this.valid_from_date = valid_from_date;
	}

	public String getValid_to_date() {
		return valid_to_date;
	}

	public void setValid_to_date(String valid_to_date) {
		this.valid_to_date = valid_to_date;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

}
