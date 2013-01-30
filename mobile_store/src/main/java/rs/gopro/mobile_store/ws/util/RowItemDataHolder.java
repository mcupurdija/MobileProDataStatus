package rs.gopro.mobile_store.ws.util;

public class RowItemDataHolder {

	private String table;
	private String noColumn;
	private String noColumnValue;
	private String idColumn;

	public RowItemDataHolder(String table, String noColumn, String noColumnValue, String idColumn) {
		super();
		this.table = table;
		this.noColumn = noColumn;
		this.noColumnValue = noColumnValue;
		this.idColumn = idColumn;
	}

	public String getTable() {
		return table;
	}

	public String getNoColumn() {
		return noColumn;
	}

	public String getNoColumnValue() {
		return noColumnValue;
	}

	public String getIdColumn() {
		return idColumn;
	}

}
