package com.unister.semweb.apiontology;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.xml.namespace.QName;

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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clarkparsia.owlapiv3.XSD;
import com.unister.semweb.apiontology.data.OntologyUtils;
import com.unister.semweb.apiontology.demonstrator.api.owl.GD;
import com.unister.semweb.apiontology.util.Utils;

public class ServiceDiscovery {

	private static final transient Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

	private OntologyUtils dao;

	public ServiceDiscovery(OntologyUtils dao) {
		super();
		this.dao = dao;
		this.dao.init();
	}

	public void discover(String wsdlUrl) {
		logger.info("Processing wsdl url {}", wsdlUrl);
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(wsdlUrl, Thread.currentThread().getContextClassLoader());

		Endpoint endpoint = client.getEndpoint();

		for (ServiceInfo info : endpoint.getService().getServiceInfos()) {
			for (BindingInfo binding : info.getBindings()) {
				if (binding.getBindingId().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) {
					for (BindingOperationInfo operation : binding.getOperations()) {
						this.addService(operation, wsdlUrl);
					}
				}
			}
		}

	}

	public void addService(BindingOperationInfo operation, String wsdlUrl) {
		QName qName = operation.getOperationInfo().getName();
		logger.info("Processing Operation {}", qName);
		IRI serviceIri = IRI.create(qName.getNamespaceURI() + qName.getLocalPart());
		dao.addWebService(serviceIri, wsdlUrl, qName.getLocalPart());
		processInputMessage(serviceIri, operation.getInput());
		processOutputMessage(serviceIri, operation.getOutput());
	}

	private void processInputMessage(IRI serviceIri, BindingMessageInfo bindingMessageInfo) {
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
						} else if (paramType.equals(Long.class) || paramType.equals(Integer.class)) {
							datatype = XSD.LONG;
						} else if (paramType.equals(Float.class)) {
							datatype = XSD.FLOAT;
						} else {
							datatype = XSD.BASE_64_BINARY;
						}

						IRI paramIri = IRI.create(base + localPart + "#",
								m.getName().replaceAll("set", "").replaceAll("get", ""));
						dao.addPrefix(paramIri);
						dao.addParam(paramIri, datatype, serviceIri, true, partClass);
						logger.info("Add param {}", paramIri);
					}
				}
			}
		}
	}

	private void processOutputMessage(IRI serviceIri, BindingMessageInfo bindingMessageInfo) {
		for (MessagePartInfo part : bindingMessageInfo.getMessageParts()) {
			String base = part.getElementQName().getNamespaceURI();
			String localPart = part.getElementQName().getLocalPart();
			Class<?> partClass = part.getTypeClass();
			processClass(serviceIri, base, localPart, partClass, partClass);
		}
	}

	private void processClass(IRI serviceIri, String base, String localPart, Class<?> containerClass,
			Class<?> partClass) {
		for (Method m : partClass.getDeclaredMethods()) {
			if (m.getName().startsWith("get")) {
				Class<?> returnType = m.getReturnType();
				OWLDatatype datatype = null;
				String suffix = m.getName().replaceAll("set", "").replaceAll("get", "");
				IRI paramIri = IRI.create(base+"#"+localPart + "#",suffix);
				if (returnType.equals(String.class)) {
					datatype = XSD.STRING;
				} else if (returnType.equals(Double.class)) {
					datatype = XSD.DOUBLE;
				} else if (returnType.equals(Long.class) || returnType.equals(Integer.class)) {
					datatype = XSD.LONG;
				} else if (returnType.equals(Float.class)) {
					datatype = XSD.FLOAT;
				} else {
					processClass(serviceIri, base, localPart, containerClass, returnType);
					continue;
				}

				dao.addPrefix(paramIri);
				dao.addParam(paramIri, datatype, serviceIri, false, containerClass, Utils.classMethod2Literal(partClass, m));
				logger.info("Add param {}", paramIri);
			}
		}
	}

	public static void main(String[] args)
			throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		OWLOntology ontology = OWLManager.createOWLOntologyManager().createOntology();
		ServiceDiscovery sd = new ServiceDiscovery(new OntologyUtils(ontology));
		sd.discover("http://ws.cdyne.com/ip2geo/ip2geo.asmx?wsdl");
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLObjectProperty hasParam = factory.getOWLObjectProperty(GD.HAS_PARAMETER);
		OWLObjectSomeValuesFrom someValues = factory.getOWLObjectSomeValuesFrom(hasParam,
				factory.getOWLClass(IRI.create("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP#ZIP")));
		ontology.getOWLOntologyManager().addAxiom(ontology, factory.getOWLEquivalentClassesAxiom(
				factory.getOWLClass(IRI.create("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP")), someValues));

		OWLObjectSomeValuesFrom someValues2 = factory.getOWLObjectSomeValuesFrom(hasParam,
				factory.getOWLClass(IRI.create("http://ws.cdyne.com/WeatherWS/GetCityForecastByZIP#ZIP")));
		ontology.getOWLOntologyManager().addAxiom(ontology, factory.getOWLEquivalentClassesAxiom(
				factory.getOWLClass(IRI.create("http://ws.cdyne.com/WeatherWS/GetCityForecastByZIP")), someValues2));

		ontology.getOWLOntologyManager().saveOntology(ontology, new ManchesterOWLSyntaxOntologyFormat(),
				new FileOutputStream("testms"));
	}
}
