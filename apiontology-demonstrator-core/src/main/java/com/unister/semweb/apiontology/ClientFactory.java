package com.unister.semweb.apiontology;

import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class ClientFactory {

	private static final transient Logger logger = LoggerFactory.getLogger(ClientFactory.class);
	private Map<String, Client> clients;
	private Map<String, Class<?>> classes;

	private static final ClientFactory INSTANCE = new ClientFactory();

	private ClientFactory() {
		this.clients = Maps.newHashMap();
		this.classes = Maps.newHashMap();
	}

	public static ClientFactory getInstance() {
		return INSTANCE;
	}

	public synchronized Client getClient(String url) {
		if (!clients.containsKey(url) || clients.get(url) == null) {
			loadClient(url);
		}
		return clients.get(url);
	}

	private void loadClient(String url) {
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(url,
				Thread.currentThread().getContextClassLoader());
		this.clients.put(url, client);
	}

	public Class<?> getClass(String name){
		if(!classes.containsKey(name)){
			try {
				classes.put(name, Thread.currentThread().getContextClassLoader().loadClass(name));
			} catch (ClassNotFoundException e) {
				logger.error("Error on loading class",e);
			}
		}
		return classes.get(name);
	}

}
