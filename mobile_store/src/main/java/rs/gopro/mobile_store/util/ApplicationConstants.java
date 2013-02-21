package rs.gopro.mobile_store.util;

public class ApplicationConstants {

	// **** DIALOGS ****/

	public static final int DATE_PICKER_DIALOG = 10;

	public static final String SESSION_PREFS = "SessionPrefs";
	
	public static final Integer SUCCESS = 1;
	public static final Integer FAILURE = 2;

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
