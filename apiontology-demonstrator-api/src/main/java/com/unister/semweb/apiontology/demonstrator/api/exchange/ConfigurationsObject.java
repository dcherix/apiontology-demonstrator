package com.unister.semweb.apiontology.demonstrator.api.exchange;

import java.util.List;

public class ConfigurationsObject {

	private List<Constraint> constraints;
	private List<Equivalence> equivalences;
	public List<Constraint> getConstraints() {
		return constraints;
	}
	public List<Equivalence> getEquivalences() {
		return equivalences;
	}
	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}
	public void setEquivalences(List<Equivalence> equivalences) {
		this.equivalences = equivalences;
	}

}
