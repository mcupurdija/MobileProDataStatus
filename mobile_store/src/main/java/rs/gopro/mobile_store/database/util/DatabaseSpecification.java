package rs.gopro.mobile_store.database.util;

public class DatabaseSpecification {

	public static final String USER_TABLE = "USER";

	public static enum UserColumn {
		USER_ID("USER_ID"), USERNAME("USERNAME"), PASSWORD("PASSWORD");
		private String representation;

		private UserColumn(String columnName) {
			this.representation = columnName;
		}

		public String getRepresentation() {
			return representation;
		}

	}

}
