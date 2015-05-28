package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class PotentialCustomerDomain extends Domain {

	public String customer_no;
	public String name;
	public String name2;
	public String address;
	public String address2;
	public String city;
	public String phone;
	public String customer_currency_code;
	public String sales_person_no;
	public String vat_reg_no;
	public String post_code;
	public String email;
	public String company_id;
	public String customer_link;
	public String position;
	public String mobile;
	
	private static final String[] COLUMNS = new String[] { "customer_no", "name", "name2", "address", "address2", "city", "phone",
		"customer_currency_code", "sales_person_no", "vat_reg_no", "post_code", "email", "mobile", "company_id", "customer_link", "position" };
	
	public PotentialCustomerDomain() {
	}

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Customers.CUSTOMER_NO, getCustomer_no());
		contentValues.put(Customers.NAME, getName());
		contentValues.put(Customers.NAME_2, getName2());
		contentValues.put(Customers.ADDRESS, getAddress());
		contentValues.put(Customers.ADDRESS_2, getAddress2());
		contentValues.put(Customers.CITY, getCity());
		contentValues.put(Customers.PHONE, getPhone());
		contentValues.put(Customers.CUSTOMER_CURRENCY_CODE, getCustomer_currency_code());
		contentValues.put(Customers.SALE_PERSON_NO, getSales_person_no());
		contentValues.put(Customers.VAT_REG_NO, getVat_reg_no());
		contentValues.put(Customers.POST_CODE, getPost_code());
		contentValues.put(Customers.EMAIL, getEmail());
		contentValues.put(Customers.COMPANY_ID, getCompany_id());
		contentValues.put(Customers.CUSTOMER_LINK, getCustomer_link());
		contentValues.put(Customers.CUSTOMER_POSITION, getPosition());
		contentValues.put(Customers.MOBILE, getMobile());
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> transferNoToIdList = new ArrayList<RowItemDataHolder>();
		transferNoToIdList.add(new RowItemDataHolder(Tables.SALES_PERSONS, Customers.SALE_PERSON_NO, getSales_person_no(), Customers.SALES_PERSON_ID));	
		return transferNoToIdList;
	}

	public String getCustomer_no() {
		return customer_no;
	}

	public void setCustomer_no(String customer_no) {
		this.customer_no = customer_no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCustomer_currency_code() {
		return customer_currency_code;
	}

	public void setCustomer_currency_code(String customer_currency_code) {
		this.customer_currency_code = customer_currency_code;
	}

	public String getSales_person_no() {
		return sales_person_no;
	}

	public void setSales_person_no(String sales_person_no) {
		this.sales_person_no = sales_person_no;
	}

	public String getVat_reg_no() {
		return vat_reg_no;
	}

	public void setVat_reg_no(String vat_reg_no) {
		this.vat_reg_no = vat_reg_no;
	}

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getCustomer_link() {
		return customer_link;
	}

	public void setCustomer_link(String customer_link) {
		this.customer_link = customer_link;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
