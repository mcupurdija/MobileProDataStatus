package rs.gopro.mobile_store.ui.components;

/**
 * 
 * Spinner item for custom adapter for spinners.
 * 
 * @author vladimirm
 *
 */
public class SpinnerItem {

	private int id;
	private String code;
	private String description;
	private String postCode;
	
	public SpinnerItem() {
	}

	public SpinnerItem(int id, String code, String description, String postCode) {
		super();
		this.id = id;
		this.code = code;
		this.description = description;
		this.postCode = postCode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

}
