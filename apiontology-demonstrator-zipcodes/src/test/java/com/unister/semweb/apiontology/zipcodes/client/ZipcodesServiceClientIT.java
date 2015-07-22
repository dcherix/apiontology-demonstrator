package com.unister.semweb.apiontology.zipcodes.client;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.unister.semweb.apiontology.zipcodes.domain.GetZipcodeRequest;
import com.unister.semweb.apiontology.zipcodes.domain.ZipcodeResponse;

/**
 * 
 * @author m.priebe
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/main/resources/spring/zipcodes-context.xml","/spring/jetty-context.xml"})
public class ZipcodesServiceClientIT {

    @Autowired
    protected DefaultMarshallingZipcodeService client;

    @Test
    public void testZipcodeSOAPResponseClientForNewYork() {
        assertNotNull("Client is null.", client);
        GetZipcodeRequest request = new GetZipcodeRequest();
        request.setCity("New York");
        ZipcodeResponse response = client.getFirstZipcode(request);
        assertNotNull("Response is null.", response);
        assertEquals("10001", response.getZipcode());
    }

    @Test
    public void testZipcodeSOAPResponseClientForNothin() {
        assertNotNull("Client is null.", client);
        ZipcodeResponse response = client.getFirstZipcode(new GetZipcodeRequest());
        assertNotNull("Response is null.", response);
        assertEquals("n/a", response.getZipcode());
    }

}
