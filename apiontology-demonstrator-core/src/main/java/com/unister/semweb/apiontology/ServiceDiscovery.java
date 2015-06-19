package com.unister.semweb.apiontology;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.semanticweb.owlapi.model.IRI;

public class ServiceDiscovery {


	public void discover(String wsdlUrl){
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(wsdlUrl, Thread.currentThread().getContextClassLoader());


		Endpoint endpoint = client.getEndpoint();

		for(ServiceInfo info : endpoint.getService().getServiceInfos()){
			for(BindingInfo binding: info.getBindings()){
				if(binding.getBindingId().equals("http://schemas.xmlsoap.org/wsdl/soap12/")){
					for(BindingOperationInfo operation: binding.getOperations()){
						this.addService(operation);
					}
				}
			}
		}


	}

	public void addService(BindingOperationInfo operation){
		IRI serviceIri = IRI.create(operation.getOperationInfo().getName().getNamespaceURI()+operation.getOperationInfo().getName().getLocalPart());
		System.out.println(serviceIri);
		for(MessagePartInfo part:operation.getInput().getMessageParts()){
			String base = part.getElementQName().getNamespaceURI();
			String localPart = part.getElementQName().getLocalPart();
			Class<?> partClass = part.getTypeClass();
			for(Method m:partClass.getDeclaredMethods()){
				if(m.getName().startsWith("set")){
					System.out.println(IRI.create(base+localPart+"#",m.getName().replaceAll("set", "")));
					System.out.println(Arrays.toString(m.getParameterTypes()));
				}
			}
		}

		System.out.println();
	}

	public static void main(String[] args) {
		ServiceDiscovery sd = new ServiceDiscovery();
		sd.discover("http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL");
	}
}
