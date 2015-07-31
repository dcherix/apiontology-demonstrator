package com.unister.semweb.apiontology;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.unister.semweb.apiontology.data.WebServiceDAO;
import com.unister.semweb.apiontology.demonstrator.api.owl.GD;
import com.unister.semweb.apiontology.util.Constants;

public class ApplicationDataModel {
	@Resource
	private ServiceDiscovery discovery;
	@Resource
	private WebServiceDAO dao;
	@Resource
	private OWLOntologyManager manager;

	public OWLOntology addWebService(String service) {
		discovery.discover(service);
		return manager.getOntology(Constants.ADM_ONTOLOGY);
	}

	public OWLOntology addMandatoryParam(String webservice, List<String> params) {
		dao.addConstraints(webservice, params);
		return manager.getOntology(Constants.ADM_ONTOLOGY);
	}

	public void runExperiment(Map<String, String> params) throws OWLOntologyStorageException {
		int run = 0;

		try {
			OWLOntology baseOntology = manager.getOntology(Constants.ADM_ONTOLOGY);
			baseOntology.getOWLOntologyManager().saveOntology(baseOntology, IRI.create(new File("adm")));
			boolean solvable = true;
			while (solvable) {
				OWLOntology ontology = manager.createOntology(IRI.create(GD.NAMESPACE, "experiment-" + "_" + run),
						Sets.newHashSet(baseOntology));
				RunningOntology ro = new RunningOntology(ontology);
				ro.init();
				List<IRI> parameters = Lists.newLinkedList();
				for (Entry<String, String> param : params.entrySet()) {
					parameters.add(ro.addParam(IRI.create(param.getKey()), param.getValue()));
				}
				OWLNamedIndividual service = ro.addRequest(parameters);
				Set<OWLClass> classes = ro.getClasses(service);
				if (classes == null || classes.isEmpty()) {
					solvable = false;
				}

				for (Iterator<OWLClass> it = classes.iterator(); it.hasNext();) {
					Object[] results = ro.invoke(service, it.next());

				}

				baseOntology = ontology;
				// ontology.getOWLOntologyManager().saveOntology(ontology,
				// IRI.create(file));
			}
		} catch (OWLOntologyCreationException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processResults(Object[] results) {
		for (Object result : results) {
			// result.getClass()
		}
	}

}
