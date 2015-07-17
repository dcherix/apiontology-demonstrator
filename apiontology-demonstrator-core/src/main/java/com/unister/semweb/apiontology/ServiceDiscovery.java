package com.unister.semweb.apiontology;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.DefaultOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.clarkparsia.owlapiv3.XSD;
import com.unister.semweb.apiontology.data.WebServiceDAO;
import com.unister.semweb.apiontology.demonstrator.api.owl.GD;

public class ServiceDiscovery {

	private WebServiceDAO dao;

	public ServiceDiscovery(WebServiceDAO dao) {
		super();
		this.dao = dao;
		this.dao.init();
	}

	public void discover(String wsdlUrl) {
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(wsdlUrl, Thread.currentThread()
				.getContextClassLoader());

		Endpoint endpoint = client.getEndpoint();

		for (ServiceInfo info : endpoint.getService().getServiceInfos()) {
			for (BindingInfo binding : info.getBindings()) {
				if (binding.getBindingId().equals(
						"http://schemas.xmlsoap.org/wsdl/soap12/")) {
					for (BindingOperationInfo operation : binding
							.getOperations()) {
						this.addService(operation);
					}
				}
			}
		}

	}

	public void addService(BindingOperationInfo operation) {
		IRI serviceIri = IRI.create(operation.getOperationInfo().getName()
				.getNamespaceURI()
				+ operation.getOperationInfo().getName().getLocalPart());
		dao.addWebService(serviceIri);
		processMessage(true, serviceIri, operation.getInput());
		processMessage(false, serviceIri, operation.getOutput());
	}

	private void processMessage(boolean isInput, IRI serviceIri,
			BindingMessageInfo bindingMessageInfo) {
		for (MessagePartInfo part : bindingMessageInfo.getMessageParts()) {
			String base = part.getElementQName().getNamespaceURI();
			String localPart = part.getElementQName().getLocalPart();
			Class<?> partClass = part.getTypeClass();
			for (Method m : partClass.getDeclaredMethods()) {
				if (m.getName().startsWith("set")) {
					for (Class<?> paramType : m.getParameterTypes()) {
						OWLDatatype datatype = null;
						if (paramType.equals(String.class)) {
							datatype = XSD.STRING;
						} else if (paramType.equals(Double.class)) {
							datatype = XSD.DOUBLE;
						} else if (paramType.equals(Long.class)
								|| paramType.equals(Integer.class)) {
							datatype = XSD.LONG;
						} else if (paramType.equals(Float.class)) {
							datatype = XSD.FLOAT;
						} else {
							datatype = XSD.BASE_64_BINARY;
						}
						dao.addParam(IRI.create(base + localPart + "#", m
								.getName().replaceAll("set", "")), datatype,
								serviceIri, isInput);
					}
				}
			}
		}
	}

	public static void main(String[] args) throws OWLOntologyCreationException,
			OWLOntologyStorageException, FileNotFoundException {
		OWLOntology ontology = OWLManager.createOWLOntologyManager()
				.createOntology();
		ServiceDiscovery sd = new ServiceDiscovery(new WebServiceDAO(ontology));
		sd.discover("http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL");
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLObjectProperty hasParam = factory.getOWLObjectProperty(GD.HAS_PARAMETER);
		OWLObjectSomeValuesFrom someValues = factory.getOWLObjectSomeValuesFrom(hasParam, factory.getOWLClass(IRI.create("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP#ZIP")));
		ontology.getOWLOntologyManager().addAxiom(ontology, factory.getOWLSubClassOfAxiom(factory.getOWLClass(IRI.create("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP")), someValues));
		ontology.getOWLOntologyManager().saveOntology(ontology,
				new DefaultOntologyFormat(),
				new FileOutputStream("test"));
	}
}
