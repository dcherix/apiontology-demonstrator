package com.unister.semweb.apiontology.util;

import java.sql.Date;

import org.semanticweb.owlapi.model.IRI;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.unister.semweb.apiontology.demonstrator.api.owl.GD;

public class Constants {
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hh:mm.ss");
	public static final IRI ADM_ONTOLOGY;

	static {
		ADM_ONTOLOGY = IRI.create(GD.NAMESPACE, "adm" + sdf.format(Calendar.getInstance().getTime()));
	}
}
