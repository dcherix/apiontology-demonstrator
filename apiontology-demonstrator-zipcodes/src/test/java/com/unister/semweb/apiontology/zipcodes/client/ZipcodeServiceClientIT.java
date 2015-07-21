package com.unister.semweb.apiontology.zipcodes.client;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
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
@ContextConfiguration(locations={"/spring/jetty-context.xml",
        "/spring/client-context.xml","/spring/uszip-client-context.xml"})
public class ZipcodeServiceClientIT {

    final Logger logger = LoggerFactory.getLogger(ZipcodeServiceClientIT.class);

    @Autowired
    protected ZipcodeClient client;

    @Test
    @DirtiesContext
    public void testZipcodeSOAPResponseClient() {
        assertNotNull("Client is null.", client);
        ZipcodeResponse response = client.getFirstZipcode(new GetZipcodeRequest());
        logger.debug("Received response from server.");
        assertNotNull("Response is null.", response);
    }

}
