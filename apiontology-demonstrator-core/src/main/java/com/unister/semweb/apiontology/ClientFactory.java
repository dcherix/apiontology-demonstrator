package com.unister.semweb.apiontology;

import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import com.google.common.collect.Maps;

public class ClientFactory {
	private Map<String, Client> clients;

	private static final ClientFactory INSTANCE = new ClientFactory();

	private ClientFactory() {
		this.clients = Maps.newHashMap();
	}

	public static ClientFactory getInstance() {
		return INSTANCE;
	}

	public synchronized Client getClient(String url) {
		if (!clients.containsKey(url)) {
			loadClient(url);
		}
		return clients.get(url);
	}

	private void loadClient(String url) {
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient("http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL",
				Thread.currentThread().getContextClassLoader());
		this.clients.put(url, client);
	}
}
