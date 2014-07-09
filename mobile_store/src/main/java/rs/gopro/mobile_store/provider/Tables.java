package rs.gopro.mobile_store.provider;

/**
 * Specify database table
 */
public interface Tables {
	String USERS = "users";
	String SALES_PERSONS = "sales_persons";
	String CUSTOMERS = "customers";
	String CONTACTS = "contacts";
	String CONTACTS_EXPORT = "contacts left outer join sales_persons on contacts.sales_person_id = sales_persons._id";
	String ITEMS = "items";
	String VISITS = "visits";
	String USERS_ROLE = "users_role";
	String VISITS_JOIN_CUSTOMERS = "visits left outer join customers on visits.customer_id = customers._id";
	String SYNC_LOGS = "sync_logs";
	String ELECTRONIC_CARD_CUSTOMER = "electronic_card_customer";
	String CUSTOMER_TRADE_AGREEMENT = "customer_trade_agreement";
	String CUSTOMER_ADDRESSES = "customer_addresses";
	String SENT_ORDERS_STATUS = "sent_orders_status";
	String SERVICE_ORDERS = "service_orders";
	String APP_SETTINGS = "app_settings";
	String CITIES = "cities";
	
	String INVOICES = "invoices";
	String INVOICES_JOIN_CUSTOMER = "invoices left outer join customers on invoices.customer_id = customers._id";
	String INVOICE_LINES = "invoice_lines";
	String INVOICE_LINES_REPORT = "invoice_lines left outer join items on invoice_lines.item_no = items._id";
	String SALE_ORDERS = "sale_orders";
	String SENT_ORDERS_STATUS_LINES = "sent_orders_status_lines";
	String SENT_ORDERS_STATUS_LINES_REPORT = "sent_orders_status_lines left outer join items on sent_orders_status_lines.item_id = items._id";
	String SALE_ORDERS_JOIN_CUSTOMERS = "sale_orders left outer join customers on sale_orders.customer_id = customers._id";
	String SALE_ORDERS_EXPORT = "sale_orders left outer join customers on sale_orders.customer_id = customers._id " +
			"left outer join sales_persons on sale_orders.sales_person_id = sales_persons._id " +
			"left outer join customer_addresses ca1 on sale_orders.sell_to_address_id = ca1._id " +
			"left outer join customer_addresses ca2 on sale_orders.shipp_to_address_id = ca2._id " +
			"left outer join contacts con on sale_orders.contact_id = con._id";
	String SALE_ORDER_LINES = "sale_order_lines";
	String SALE_ORDER_LINES_JOIN_ITEMS = "sale_order_lines left outer join items on sale_order_lines.item_id = items._id";
	String SALE_ORDER_LINES_EXPORT = "sale_order_lines left outer join items on sale_order_lines.item_id = items._id " +
			"inner join sale_orders on sale_order_lines.sale_order_id = sale_orders._id";
	
	String USERS_JOIN_USERS_ROLE = "users left outer join users_role on users.users_role_id = users_role._id";
	
	String EL_CARD_CUSTOMER_JOIN_CUSTOMER_JOIN_ITEM = "electronic_card_customer inner join customers on electronic_card_customer.customer_id = customers._id inner join items on electronic_card_customer.item_id = items._id";
	String CUSTOMER_AGREEMENT_JOIN_CUSTOMER = "customer_trade_agreement inner join customers on customer_trade_agreement.customer_id = customers._id";
	
	String VISITS_EXPORT = "visits left outer join customers on visits.customer_id = customers._id " +
			"left outer join sales_persons on visits.sales_person_id = sales_persons._id ";
	String SALE_ORDERS_SALDO = "sale_orders inner join sale_order_lines on sale_order_lines.sale_order_id = sale_orders._id";
	String SENT_ORDERS_STATUS_JOIN_CUSTOMERS = "sent_orders_status left outer join customers on sent_orders_status.customer_id = customers._id";
	String CUSTOMERS_EXPORT = "customers left outer join sales_persons on customers.sales_person_id = sales_persons._id";
	String CUSTOMERS_UPDATE_EXPORT = "customers left outer join sales_persons on customers.sales_person_id = sales_persons._id " +
			"left outer join contacts on customers.primary_contact_id = contacts._id";
	String SERVICE_ORDERS_EXPORT = "service_orders left outer join customers on service_orders.customer_id = customers._id " +
			"left outer join items on service_orders.item_id = items._id " +
			"left outer join sales_persons on service_orders.sales_person_id = sales_persons._id";
}
