package com.unister.semweb.apiontology.util;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;



public class TestService {
	public static void main(String[] args) throws Exception {

		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient("http://localhost:8181/", Thread.currentThread().getContextClassLoader());
		Class<?> containerClass = Thread.currentThread().getContextClassLoader().loadClass("com.example.companydata.CompanyData");
		Method m = containerClass.getMethod("setCompany", String.class);
		System.out.println(Arrays.toString(m.getParameterTypes()));
		Object container = containerClass.newInstance();
		m.invoke(container, "Xerox");
		Object[] r = client.invokeWrapped("CompanyData", container);
	}
}
