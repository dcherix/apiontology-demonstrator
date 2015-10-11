package com.unister.semweb.apiontology;

import org.apache.cxf.endpoint.Client;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

public class ClientFactoryTest {

	@Test
	public void test() {
		ClientFactory factory = ClientFactory.getInstance();
		assertThat(factory, is(not(nullValue())));
	}

	@Test
	public void testLoad() {
		ClientFactory factory = ClientFactory.getInstance();
		Client client = factory.getClient("http://wsf.cdyne.com/WeatherWS/Weather.asmx?wsdl");
		assertThat(client, is(not(nullValue())));
	}
	
	@Test
	public void testReLoad() {
		ClientFactory factory = ClientFactory.getInstance();
		Client client = factory.getClient("http://wsf.cdyne.com/WeatherWS/Weather.asmx?wsdl");
		assertThat(client, is(not(nullValue())));
		client = factory.getClient("http://wsf.cdyne.com/WeatherWS/Weather.asmx?wsdl");
		assertThat(client, is(not(nullValue())));
	}
	
	@Test
	public void getClassTest() {
		ClientFactory factory = ClientFactory.getInstance();
		Client client = factory.getClient("http://wsf.cdyne.com/WeatherWS/Weather.asmx?wsdl");
		Class<?> retrievedClass = factory.getClass("com.cdyne.ws.weatherws.ArrayOfForecast");
		assertThat(client, is(not(nullValue())));
		assertThat(retrievedClass, is(not(nullValue())));
	}
}
