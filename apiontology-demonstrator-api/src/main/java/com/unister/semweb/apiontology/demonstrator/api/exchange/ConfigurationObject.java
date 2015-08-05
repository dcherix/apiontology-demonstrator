package com.unister.semweb.apiontology.demonstrator.api.exchange;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringEscapeUtils;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.unister.semweb.apiontology.demonstrator.api.owl.GD;

/**
 * Configuration Object
 *
 * @author d.cherix
 *
 */
public class ConfigurationObject {

	private static final transient Logger logger = LoggerFactory.getLogger(ConfigurationObject.class);

	private List<Constraint> constraints;
	private List<Equivalence> equivalences;
	private String datamodel;

	public String getDatamodel() {
		return datamodel;
	}

	public void setDatamodel(String datamodel) {
		this.datamodel = datamodel;
	}

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

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private List<Constraint> constraints;
		private List<Equivalence> equivalences;
		private String datamodel;

		public Builder datamodel(OWLOntology ontology, Map<String, String> prefixes) {

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				ManchesterOWLSyntaxOntologyFormat ontologyFormat = new ManchesterOWLSyntaxOntologyFormat();
				for (Entry<String, String> entry : prefixes.entrySet()) {
					ontologyFormat.setPrefix(entry.getValue(), entry.getKey());
				}
				ontology.getOWLOntologyManager().saveOntology(ontology, ontologyFormat, os);
				this.datamodel = StringEscapeUtils.escapeHtml4(os.toString("UTF-8"));

			} catch (OWLOntologyStorageException | UnsupportedEncodingException e) {
				logger.error("Error on writing ontology", e);
			}

			return this;
		}

		public Builder constraint(Constraint constraint) {
			if (constraints == null) {
				constraints = Lists.newLinkedList();
			}
			constraints.add(constraint);
			return this;
		}

		public Builder equivalence(Equivalence equivalence) {
			if (equivalences == null) {
				equivalences = Lists.newLinkedList();
			}
			equivalences.add(equivalence);
			return this;
		}

		public ConfigurationObject build() {
			ConfigurationObject co = new ConfigurationObject();
			co.setConstraints(constraints);
			co.setEquivalences(equivalences);
			co.setDatamodel(datamodel);
			return co;
		}
	}

}
