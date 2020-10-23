package nl.haarlem.translations.zdstozgw.jpa.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EmulateParameter {
	@Id
	private String parameterName;
	private String parameterDescription;
	private String parameterValue;

	public String getParameterName() {
		return this.parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterDescription() {
		return this.parameterDescription;
	}

	public void setParameterDescription(String parameterDescription) {
		this.parameterDescription = parameterDescription;
	}

	public String getParameterValue() {
		return this.parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
}
