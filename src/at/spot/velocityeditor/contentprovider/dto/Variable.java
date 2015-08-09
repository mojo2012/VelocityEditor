package at.spot.velocityeditor.contentprovider.dto;

public class Variable {
	private String	variable;
	private String	value;

	public Variable(String variable, String value) {
		this.variable = variable;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}
}
