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
	String USERS_ROLE = "users_role";
	String VISITS_JOIN_CUSTOMERS = "visits left outer join customers on visits.customer_id = customers._id";
	
	
	String INVOICES = "invoices";
	String INVOICE_LINES = "invoice_lines";
	String SALE_ORDERS = "sale_orders";
	String SALE_ORDERS_JOIN_CUSTOMERS = "sale_orders left outer join customers on sale_orders.customer_id = customers._id";
	String SALE_ORDER_LINES = "sale_order_lines";
	String SALE_ORDER_LINES_JOIN_ITEMS = "sale_order_lines left outer join items on sale_order_lines.item_id = items._id";
	
	String USERS_JOIN_USERS_ROLE = "users left outer join users_role on users.users_role_id = users_role._id";
}
