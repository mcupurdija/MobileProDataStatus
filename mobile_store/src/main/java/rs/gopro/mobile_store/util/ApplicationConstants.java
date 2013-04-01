package rs.gopro.mobile_store.util;

public class ApplicationConstants {

	// **** DIALOGS ****/

	public static final int DATE_PICKER_DIALOG = 10;

	public static final String SESSION_PREFS = "SessionPrefs";
	
	public static final Integer SUCCESS = 1;
	public static final Integer FAILURE = 2;
	
	public static final double VAT = 0.2;

	public static final int VISIT_PLANNED = 0;
	public static final int VISIT_RECORDED = 1;
	
	public static final int VISIT_STATUS_NEW = 0;
	public static final int VISIT_STATUS_STARTED = 1;
	public static final int VISIT_STATUS_FINISHED = 2;
	
	public static final int VISIT_TYPE_START_DAY = 1;
	public static final int VISIT_TYPE_CLOSURE = 2;
	public static final int VISIT_TYPE_NO_CLOSURE = 3;
	public static final int VISIT_TYPE_PAUSE = 4;
	public static final int VISIT_TYPE_END_DAY = 5;
	public static final int VISIT_TYPE_BACK_HOME = 6;
	
	public static final int SENT_DOCUMENTS_STATUS_HEADER_DOC_TYPE_ORDERS = 0;
	
	public static final String PRICE_AND_DISCOUNT_ARE_NOT_OK = "1";
	
	public static enum USER_ROLE {
		ADMIN, USER
	}

	public static enum SyncStatus{
		SUCCESS, FAILURE, IN_PROCCESS;	
	}
	
	public static enum OrderType {
		SALE_ORDER("sale_order"), SENT_ORDER("sent_order");
		
		private String type;
		
		private OrderType(String type){
			this.type = type;
		}

		public String getType() {
			return type;
		}
		
		public static OrderType find(String type){
			OrderType orderType = null;
			try {
				if(type != null && !type.isEmpty()){
				for(OrderType type2 : OrderType.values()){
					if(type2.getType().equalsIgnoreCase(type)){
						return type2;
					}
				}	
				}
			} catch (Exception e) {
			}
			return orderType;
		}
	}
	
	
}
