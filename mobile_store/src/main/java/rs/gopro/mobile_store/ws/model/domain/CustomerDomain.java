package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;

public class CustomerDomain extends Domain {

	public String customer_no;
	public String name;
	public String name2;
	public String address;
	public String city;
	public String phone;
	public String mobile;
	public String global_dimension;
	public String credit_limit_lcy;
	public String payment_terms_code;
	public String sales_person_no;
	public String priority;
	public String vat_reg_no;
	public String post_code;
	public String email;
	public String primary_contact_id;
	public String company_id;
	public String channel_oran;
	public String sml;
	public String adopted_potential;
	public String focus_customer;
	public String division;
	public String gross_profit_pfep;
	public String number_of_blue_coat;
	public String number_of_grey_coat;
	public String contact_company_no;
	public String balance_lcy;
	public String sales_lcy;
	public String balance_due_lcy;
	public String internal_balance_due_lcy;
	public String turnover_in_last_3m;
	public String turnover_in_last_6m;
	public String turnover_in_last_12m;
	public String turnover_generated_3;
	public String turnover_generated_2;
	public String turnover_generated_1;
	public String number_of_diff_items_3;
	public String number_of_diff_items_2;
	public String number_of_diff_items_1;
	public String orsy_shelf_count_at_cust;
	public String customer_12_months_plan;
	public String avarage_payment_days;
	public String number_of_salespersons_working_with_customer;
	public String days_since_oldest_open_invoice;
	public String next_15_days_invoice_due_amount;
	public String next_15_days_due_invoice_count;
	public String financial_control_status;

	private static final String[] COLUMNS = new String[] { "customer_no", "name", "name2", "address", "city", "phone", "mobile", "global_dimension", "credit_limit_lcy", "payment_terms_code", "sales_person_no", "priority", "vat_reg_no",
			"post_code", "email", "primary_contact_id", "company_id", "channel_oran", "sml", "adopted_potential", "focus_customer", "division", "gross_profit_pfep", "number_of_blue_coat", "number_of_grey_coat", "contact_company_no", "balance_lcy", "sales_lcy",
			"balance_due_lcy", "internal_balance_due_lcy", "turnover_in_last_3m", "turnover_in_last_6m", "turnover_in_last_12m", "turnover_generated_3", "turnover_generated_2", "turnover_generated_1", "number_of_diff_items_3",
			"number_of_diff_items_2", "number_of_diff_items_1", "orsy_shelf_count_at_cust", "customer_12_months_plan", "avarage_payment_days", "number_of_salespersons_working_with_customer", "days_since_oldest_open_invoice",
			"next_15_days_invoice_due_amount", "next_15_days_due_invoice_count", "financial_control_status"

	};

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
		contentValues.put(MobileStoreContract.Customers.POST_CODE,  getPost_code());
		contentValues.put(MobileStoreContract.Customers.PHONE , getPhone());
		contentValues.put(MobileStoreContract.Customers.MOBILE , getMobile());
		contentValues.put(MobileStoreContract.Customers.EMAIL , getEmail());
		contentValues.put(MobileStoreContract.Customers.COMPANY_ID , getCompany_id());
		if ( getPrimary_contact_id() == null ||  getPrimary_contact_id().length() < 1) {
			contentValues.putNull(MobileStoreContract.Customers.PRIMARY_CONTACT_ID);
		} else {
			contentValues.put(MobileStoreContract.Customers.PRIMARY_CONTACT_ID , getPrimary_contact_id());
		}
		contentValues.put(MobileStoreContract.Customers.VAT_REG_NO , getVat_reg_no());
		contentValues.put(MobileStoreContract.Customers.CREDIT_LIMIT_LCY, WsDataFormatEnUsLatin.toDoubleFromWs(getCredit_limit_lcy()));
		contentValues.put(MobileStoreContract.Customers.BALANCE_LCY , WsDataFormatEnUsLatin.toDoubleFromWs(getBalance_lcy()));
		contentValues.put(MobileStoreContract.Customers.BALANCE_DUE_LCY , WsDataFormatEnUsLatin.toDoubleFromWs(getBalance_due_lcy()));
		contentValues.put(MobileStoreContract.Customers.PAYMENT_TERMS_CODE , getPayment_terms_code());
		contentValues.put(MobileStoreContract.Customers.PRIORITY, getPriority());
		contentValues.put(MobileStoreContract.Customers.GLOBAL_DIMENSION, getGlobal_dimension());
		contentValues.put(MobileStoreContract.Customers.CHANNEL_ORAN, getChannel_oran());
		contentValues.put(MobileStoreContract.Customers.BLOCKED_STATUS , "0");
		contentValues.put(MobileStoreContract.Customers.SML , getSml());
		contentValues.put(MobileStoreContract.Customers.INTERNAL_BALANCE_DUE_LCY, WsDataFormatEnUsLatin.toDoubleFromWs(getInternal_balance_due_lcy()));
		contentValues.put(MobileStoreContract.Customers.ADOPTED_POTENTIAL , WsDataFormatEnUsLatin.toDoubleFromWs(getAdopted_potential()));
		contentValues.put(MobileStoreContract.Customers.FOCUS_CUSTOMER , getFocus_customer());
		contentValues.put(MobileStoreContract.Customers.DIVISION , getDivision());
		contentValues.put(MobileStoreContract.Customers.NUMBER_OF_BLUE_COAT , getNumber_of_blue_coat());
		contentValues.put(MobileStoreContract.Customers.NUMBER_OF_GREY_COAT , getNumber_of_grey_coat());
		contentValues.put(MobileStoreContract.Customers.CONTACT_COMPANY_NO , contact_company_no);
		contentValues.put(MobileStoreContract.Customers.SYNC_OBJECT_BATCH , "1");
		contentValues.put(MobileStoreContract.Customers.SALE_PERSON_NO , getSales_person_no());
		contentValues.put(MobileStoreContract.Customers.SALES_LCY, WsDataFormatEnUsLatin.toDoubleFromWs(getSales_lcy()));
		contentValues.put(MobileStoreContract.Customers.GROSS_PROFIT_PFEP, WsDataFormatEnUsLatin.toDoubleFromWs(getGross_profit_pfep()));
		contentValues.put(MobileStoreContract.Customers.TURNOVER_IN_LAST_3M , WsDataFormatEnUsLatin.toDoubleFromWs(getTurnover_in_last_3m()));
		contentValues.put(MobileStoreContract.Customers.TURNOVER_IN_LAST_6M, WsDataFormatEnUsLatin.toDoubleFromWs(getTurnover_in_last_6m()));
		contentValues.put(MobileStoreContract.Customers.TURNOVER_IN_LAST_12M , WsDataFormatEnUsLatin.toDoubleFromWs(getTurnover_in_last_12m()));
		contentValues.put(MobileStoreContract.Customers.TURNOVER_GENERATED_3 , WsDataFormatEnUsLatin.toDoubleFromWs(getTurnover_generated_3()));
		contentValues.put(MobileStoreContract.Customers.TURNOVER_GENERATED_2 , WsDataFormatEnUsLatin.toDoubleFromWs(getTurnover_generated_2()));
		contentValues.put(MobileStoreContract.Customers.TURNOVER_GENERATED_1 , WsDataFormatEnUsLatin.toDoubleFromWs(getTurnover_generated_1()));
		contentValues.put(MobileStoreContract.Customers.NUMBER_OF_DIFF_ITEMS_3, getNumber_of_diff_items_3());
		contentValues.put(MobileStoreContract.Customers.NUMBER_OF_DIFF_ITEMS_2 , getNumber_of_diff_items_2());
		contentValues.put(MobileStoreContract.Customers.NUMBER_OF_DIFF_ITEMS_1, getNumber_of_diff_items_1());
		contentValues.put(MobileStoreContract.Customers.ORSY_SHELF_COUNT_AT_CUST , getOrsy_shelf_count_at_cust());
		contentValues.put(MobileStoreContract.Customers.CUSTOMER_12_MONTHS_PLAN , WsDataFormatEnUsLatin.toDoubleFromWs(getCustomer_12_months_plan()));
		contentValues.put(MobileStoreContract.Customers.AVARAGE_PAYMENT_DAYS , WsDataFormatEnUsLatin.toDoubleFromWs(getAvarage_payment_days()));
		contentValues.put(MobileStoreContract.Customers.NUMBER_OF_SALESPERSONS_WORKING_WITH_CUSTOMER , getNumber_of_salespersons_working_with_customer());
		contentValues.put(MobileStoreContract.Customers.DAYS_SINCE_OLDEST_OPEN_INVOICE , getDays_since_oldest_open_invoice());
		contentValues.put(MobileStoreContract.Customers.NEXT_15_DAYS_INVOICE_DUE_AMOUNT , WsDataFormatEnUsLatin.toDoubleFromWs(getNext_15_days_invoice_due_amount()));
		contentValues.put(MobileStoreContract.Customers.NEXT_15_DAYS_DUE_INVOICE_COUNT , getNext_15_days_due_invoice_count());
		contentValues.put(MobileStoreContract.Customers.FINANCIAL_CONTROL_STATUS, getFinancial_control_status());

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

	public String getGlobal_dimension() {
		return global_dimension;
	}

	public void setGlobal_dimension(String global_dimension) {
		this.global_dimension = global_dimension;
	}

	public String getCredit_limit_lcy() {
		return credit_limit_lcy;
	}

	public void setCredit_limit_lcy(String credit_limit_lcy) {
		this.credit_limit_lcy = credit_limit_lcy;
	}

	public String getPayment_terms_code() {
		return payment_terms_code;
	}

	public void setPayment_terms_code(String payment_terms_code) {
		this.payment_terms_code = payment_terms_code;
	}

	public String getSales_person_no() {
		return sales_person_no;
	}

	public void setSales_person_no(String sales_person_no) {
		this.sales_person_no = sales_person_no;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
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

	public String getPrimary_contact_id() {
		return primary_contact_id;
	}

	public void setPrimary_contact_id(String primary_contact_id) {
		this.primary_contact_id = primary_contact_id;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getChannel_oran() {
		return channel_oran;
	}

	public void setChannel_oran(String channel_oran) {
		this.channel_oran = channel_oran;
	}

	public String getSml() {
		return sml;
	}

	public void setSml(String sml) {
		this.sml = sml;
	}

	public String getAdopted_potential() {
		return adopted_potential;
	}

	public void setAdopted_potential(String adopted_potential) {
		this.adopted_potential = adopted_potential;
	}

	public String getFocus_customer() {
		return focus_customer;
	}

	public void setFocus_customer(String focus_customer) {
		this.focus_customer = focus_customer;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getGross_profit_pfep() {
		return gross_profit_pfep;
	}

	public void setGross_profit_pfep(String gross_profit_pfep) {
		this.gross_profit_pfep = gross_profit_pfep;
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

	public String getBalance_lcy() {
		return balance_lcy;
	}

	public void setBalance_lcy(String balance_lcy) {
		this.balance_lcy = balance_lcy;
	}

	public String getSales_lcy() {
		return sales_lcy;
	}

	public void setSales_lcy(String sales_lcy) {
		this.sales_lcy = sales_lcy;
	}

	public String getBalance_due_lcy() {
		return balance_due_lcy;
	}

	public void setBalance_due_lcy(String balance_due_lcy) {
		this.balance_due_lcy = balance_due_lcy;
	}

	public String getInternal_balance_due_lcy() {
		return internal_balance_due_lcy;
	}

	public void setInternal_balance_due_lcy(String internal_balance_due_lcy) {
		this.internal_balance_due_lcy = internal_balance_due_lcy;
	}

	public String getTurnover_in_last_3m() {
		return turnover_in_last_3m;
	}

	public void setTurnover_in_last_3m(String turnover_in_last_3m) {
		this.turnover_in_last_3m = turnover_in_last_3m;
	}

	public String getTurnover_in_last_6m() {
		return turnover_in_last_6m;
	}

	public void setTurnover_in_last_6m(String turnover_in_last_6m) {
		this.turnover_in_last_6m = turnover_in_last_6m;
	}

	public String getTurnover_in_last_12m() {
		return turnover_in_last_12m;
	}

	public void setTurnover_in_last_12m(String turnover_in_last_12m) {
		this.turnover_in_last_12m = turnover_in_last_12m;
	}

	public String getTurnover_generated_3() {
		return turnover_generated_3;
	}

	public void setTurnover_generated_3(String turnover_generated_3) {
		this.turnover_generated_3 = turnover_generated_3;
	}

	public String getTurnover_generated_2() {
		return turnover_generated_2;
	}

	public void setTurnover_generated_2(String turnover_generated_2) {
		this.turnover_generated_2 = turnover_generated_2;
	}

	public String getTurnover_generated_1() {
		return turnover_generated_1;
	}

	public void setTurnover_generated_1(String turnover_generated_1) {
		this.turnover_generated_1 = turnover_generated_1;
	}

	public String getNumber_of_diff_items_3() {
		return number_of_diff_items_3;
	}

	public void setNumber_of_diff_items_3(String number_of_diff_items_3) {
		this.number_of_diff_items_3 = number_of_diff_items_3;
	}

	public String getNumber_of_diff_items_2() {
		return number_of_diff_items_2;
	}

	public void setNumber_of_diff_items_2(String number_of_diff_items_2) {
		this.number_of_diff_items_2 = number_of_diff_items_2;
	}

	public String getNumber_of_diff_items_1() {
		return number_of_diff_items_1;
	}

	public void setNumber_of_diff_items_1(String number_of_diff_items_1) {
		this.number_of_diff_items_1 = number_of_diff_items_1;
	}

	public String getOrsy_shelf_count_at_cust() {
		return orsy_shelf_count_at_cust;
	}

	public void setOrsy_shelf_count_at_cust(String orsy_shelf_count_at_cust) {
		this.orsy_shelf_count_at_cust = orsy_shelf_count_at_cust;
	}

	public String getCustomer_12_months_plan() {
		return customer_12_months_plan;
	}

	public void setCustomer_12_months_plan(String customer_12_months_plan) {
		this.customer_12_months_plan = customer_12_months_plan;
	}

	public String getAvarage_payment_days() {
		return avarage_payment_days;
	}

	public void setAvarage_payment_days(String avarage_payment_days) {
		this.avarage_payment_days = avarage_payment_days;
	}

	public String getNumber_of_salespersons_working_with_customer() {
		return number_of_salespersons_working_with_customer;
	}

	public void setNumber_of_salespersons_working_with_customer(String number_of_salespersons_working_with_customer) {
		this.number_of_salespersons_working_with_customer = number_of_salespersons_working_with_customer;
	}

	public String getDays_since_oldest_open_invoice() {
		return days_since_oldest_open_invoice;
	}

	public void setDays_since_oldest_open_invoice(String days_since_oldest_open_invoice) {
		this.days_since_oldest_open_invoice = days_since_oldest_open_invoice;
	}

	public String getNext_15_days_invoice_due_amount() {
		return next_15_days_invoice_due_amount;
	}

	public void setNext_15_days_invoice_due_amount(String next_15_days_invoice_due_amount) {
		this.next_15_days_invoice_due_amount = next_15_days_invoice_due_amount;
	}

	public String getNext_15_days_due_invoice_count() {
		return next_15_days_due_invoice_count;
	}

	public void setNext_15_days_due_invoice_count(String next_15_days_due_invoice_count) {
		this.next_15_days_due_invoice_count = next_15_days_due_invoice_count;
	}

	public String getFinancial_control_status() {
		return financial_control_status;
	}

	public void setFinancial_control_status(String financial_control_status) {
		this.financial_control_status = financial_control_status;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
