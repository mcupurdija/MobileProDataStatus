package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerAddresses;
import rs.gopro.mobile_store.ws.model.Domain;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;

public class CustomerAddressDomain extends Domain {

	public String customer_no;
	public String address_no;
	public String address;
	public String city;
	public String contact;
	public String phone_no;
	public String postal_code;

	private static final String[] COLUMNS = new String[] { "customer_no", "address_no", "address", "city", "contact", "phone_no", "postal_code" };

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(CustomerAddresses.CUSTOMER_NO, getCustomer_no());
		contentValues.put(CustomerAddresses.ADDRESS_NO, getAddress_no());
		contentValues.put(CustomerAddresses.ADDRESS, getAddress());
		contentValues.put(CustomerAddresses.CITY, getCity());
		contentValues.put(CustomerAddresses.CONTANCT, getContact());
		contentValues.put(CustomerAddresses.PHONE_NO, getPhone_no());
		contentValues.put(CustomerAddresses.POST_CODE, getPostal_code());

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

	public String getAddress_no() {
		return address_no;
	}

	public void setAddress_no(String address_no) {
		this.address_no = address_no;
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

}
