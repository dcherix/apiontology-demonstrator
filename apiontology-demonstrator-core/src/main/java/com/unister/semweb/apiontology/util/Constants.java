package com.unister.semweb.apiontology.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.semanticweb.owlapi.model.IRI;

import com.unister.semweb.apiontology.demonstrator.api.owl.GD;

public class Constants {
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hh:mm.ss");
	public static final IRI ADM_ONTOLOGY;
	public static final IRI GD_ONTOLOGY = IRI.create(GD.NAMESPACE);

	static {
		ADM_ONTOLOGY = IRI.create(GD.NAMESPACE, "adm" + sdf.format(Calendar.getInstance().getTime()));
	}
}
