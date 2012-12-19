package rs.gopro.mobile_store.provider;

/**
 * Specify database table
 */
public interface Tables {
	String USERS = "users";
	String SALES_PERSONS = "sales_persons";
	String CUSTOMERS = "customers";
	String CONTACTS = "contacts";
	String ITEMS = "items";
	String VISITS = "visits";
	String VISITS_JOIN_CUSTOMERS = "visits left outer join customers on visits.customer_id = customers._id";
	
	
	String INVOICES = "invoices";
	String INVOICE_LINES = "invoice_lines";
	String SALE_ORDERS = "sale_orders";
	String SALE_ORDERS_JOIN_CUSTOMERS = "sale_orders left outer join customers on sale_orders.customer_id = customers._id";
	String SALE_ORDER_LINES = "sale_order_lines";
}
