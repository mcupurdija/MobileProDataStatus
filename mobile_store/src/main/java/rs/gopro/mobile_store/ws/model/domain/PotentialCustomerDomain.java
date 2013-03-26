package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class PotentialCustomerDomain extends Domain {

	public String customer_no;
	public String name;
	public String name2;
	public String address;
	public String city;
	public String phone;
	public String mobile;	
	public String sales_person_no;
	public String vat_reg_no;
	public String post_code;
	public String email;
	public String company_no;
	public String company_id;
	public String global_dimension;
	public String number_of_blue_coat;
	public String number_of_grey_coat;
	public String department;
	public String position;
	
	private static final String[] COLUMNS = new String[] { "customer_no", "name", "name2", "address", "city", "phone", "mobile",
		"sales_person_no", "vat_reg_no", "post_code", "email", "company_no",
		"company_id", "global_dimension", "number_of_blue_coat", "number_of_grey_coat", "department", "position"
	};
	
	public PotentialCustomerDomain() {
	}

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(MobileStoreContract.Customers.CUSTOMER_NO, getCustomer_no());
		contentValues.put(MobileStoreContract.Customers.NAME, getName());
		contentValues.put(MobileStoreContract.Customers.NAME_2 , getName2() );
		contentValues.put(MobileStoreContract.Customers.ADDRESS , getAddress());
		contentValues.put(MobileStoreContract.Customers.CITY , getCity());

		contentValues.put(MobileStoreContract.Customers.PHONE , getPhone());
		contentValues.put(MobileStoreContract.Customers.MOBILE , getMobile());

		contentValues.put(MobileStoreContract.Customers.SALE_PERSON_NO , getSales_person_no());
		contentValues.put(MobileStoreContract.Customers.VAT_REG_NO , getVat_reg_no());
		contentValues.put(MobileStoreContract.Customers.POST_CODE,  getPost_code());
		contentValues.put(MobileStoreContract.Customers.EMAIL , getEmail());
		contentValues.put(MobileStoreContract.Customers.COMPANY_ID , getCompany_id());
		contentValues.put(MobileStoreContract.Customers.GLOBAL_DIMENSION, getGlobal_dimension());
		contentValues.put(MobileStoreContract.Customers.NUMBER_OF_BLUE_COAT , getNumber_of_blue_coat());
		contentValues.put(MobileStoreContract.Customers.NUMBER_OF_GREY_COAT , getNumber_of_grey_coat());
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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

	public String getGlobal_dimension() {
		return global_dimension;
	}

	public void setGlobal_dimension(String global_dimension) {
		this.global_dimension = global_dimension;
	}

	public String getNumber_of_blue_coat() {
		return number_of_blue_coat;
	}

	public void setNumber_of_blue_coat(String number_of_blue_coat) {
		this.number_of_blue_coat = number_of_blue_coat;
	}

	public String getNumber_of_grey_coat() {
		return number_of_grey_coat;
	}

	public void setNumber_of_grey_coat(String number_of_grey_coat) {
		this.number_of_grey_coat = number_of_grey_coat;
	}

}
