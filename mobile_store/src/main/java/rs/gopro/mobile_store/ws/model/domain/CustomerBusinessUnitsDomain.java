package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerBusinessUnits;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class CustomerBusinessUnitsDomain extends Domain {

	public String customer_no;
	public String unit_no;
	public String unit_name;
	public String address;
	public String city;
	public String contact;
	public String phone_no;
	public String postal_code;
	public String primary_alternative_address_no;
	public String primary_alternative_address_address;
	public String primary_alternative_address_post_code;
	public String primary_alternative_address_city;

	private static final String[] COLUMNS = new String[] { "customer_no", "unit_no", "unit_name", "address", "city", "contact", "phone_no", "postal_code",
		"primary_alternative_address_no", "primary_alternative_address_address", "primary_alternative_address_post_code", "primary_alternative_address_city" };

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(CustomerBusinessUnits.CUSTOMER_NO, getCustomer_no());
		contentValues.put(CustomerBusinessUnits.UNIT_NO, getUnit_no());
		contentValues.put(CustomerBusinessUnits.UNIT_NAME, getUnit_name());
		contentValues.put(CustomerBusinessUnits.ADDRESS, getAddress());
		contentValues.put(CustomerBusinessUnits.CITY, getCity());
		contentValues.put(CustomerBusinessUnits.CONTACT, getContact());
		contentValues.put(CustomerBusinessUnits.PHONE_NO, getPhone_no());
		contentValues.put(CustomerBusinessUnits.POST_CODE, getPostal_code());
		contentValues.put(CustomerBusinessUnits.PRIMARY_ALTERNATIVE_ADDRESS_NO, getPrimary_alternative_address_no());
		contentValues.put(CustomerBusinessUnits.PRIMARY_ALTERNATIVE_ADDRESS_ADDRESS, getPrimary_alternative_address_address());
		contentValues.put(CustomerBusinessUnits.PRIMARY_ALTERNATIVE_ADDRESS_POST_CODE, getPrimary_alternative_address_post_code());
		contentValues.put(CustomerBusinessUnits.PRIMARY_ALTERNATIVE_ADDRESS_CITY, getPrimary_alternative_address_city());

		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		//dataHolders.add(new RowItemDataHolder(Tables.CUSTOMERS, CustomerAddresses.CUSTOMER_NO, getCustomer_no(), Customers.COMPANY_ID));
		return dataHolders;
	}

	public String getCustomer_no() {
		return customer_no;
	}

	public void setCustomer_no(String customer_no) {
		this.customer_no = customer_no;
	}

	public String getUnit_no() {
		return unit_no;
	}

	public void setUnit_no(String unit_no) {
		this.unit_no = unit_no;
	}

	public String getUnit_name() {
		return unit_name;
	}

	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getPhone_no() {
		return phone_no;
	}

	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}

	public String getPrimary_alternative_address_no() {
		return primary_alternative_address_no;
	}

	public void setPrimary_alternative_address_no(
			String primary_alternative_address_no) {
		this.primary_alternative_address_no = primary_alternative_address_no;
	}

	public String getPrimary_alternative_address_address() {
		return primary_alternative_address_address;
	}

	public void setPrimary_alternative_address_address(
			String primary_alternative_address_address) {
		this.primary_alternative_address_address = primary_alternative_address_address;
	}

	public String getPrimary_alternative_address_post_code() {
		return primary_alternative_address_post_code;
	}

	public void setPrimary_alternative_address_post_code(
			String primary_alternative_address_post_code) {
		this.primary_alternative_address_post_code = primary_alternative_address_post_code;
	}

	public String getPrimary_alternative_address_city() {
		return primary_alternative_address_city;
	}

	public void setPrimary_alternative_address_city(
			String primary_alternative_address_city) {
		this.primary_alternative_address_city = primary_alternative_address_city;
	}

}
