package com.unister.semweb.apiontology.demonstrator.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.unister.semweb.apiontology.demonstrator.api.exchange.ConfigurationObject;
import com.unister.semweb.apiontology.demonstrator.api.exchange.Constraint;
import com.unister.semweb.apiontology.demonstrator.api.exchange.Equivalence;

public class Mock {
	public static ConfigurationObject mock() {
		List<Constraint> constraints = Lists.newArrayList();
		Set<String> parameters = Sets.newHashSet();
		for (int i = 0; i < 3; i++) {
			Constraint c = new Constraint();
			c.setWebService("http://example.org/ws/" + i);
			Set<String> params = Sets.newHashSet();
			for (int j = 0; j < 5; j++) {
				params.add("http://example.org/ws/" + i + "#p" + j);
			}
			c.setParameters(params);
			parameters.addAll(params);
			constraints.add(c);
		}

		List<Equivalence> equivalences = Lists.newArrayList();
		for (String param : parameters) {
			Equivalence equivalence = new Equivalence();
			equivalence.setParameter(param);
			HashSet<String> others = Sets.newHashSet(parameters);
			others.remove(param);
			equivalence.setEqParameters(others);
			equivalences.add(equivalence);
		}

		ConfigurationObject co = new ConfigurationObject();
		co.setConstraints(constraints);
		co.setEquivalences(equivalences);

		StringBuilder builder = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(Mock.class.getClassLoader().getResourceAsStream("testms")))) {
			while (reader.ready()) {
				builder.append(reader.readLine()).append("\n");
			}
		} catch (IOException e) {

		}

		co.setDatamodel(StringEscapeUtils.escapeHtml4(builder.toString()));

		return co;
	}
}
