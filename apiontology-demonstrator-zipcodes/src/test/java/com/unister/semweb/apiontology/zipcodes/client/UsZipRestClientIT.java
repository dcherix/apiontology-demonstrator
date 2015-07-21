package com.unister.semweb.apiontology.zipcodes.client;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/uszip-client-context.xml")
public class UsZipRestClientIT {

    @Autowired
    private UsZipRestClient client;

    @Test
    public void testGetZipCodeForNewYork() {
        String zipCode = client.getZipCodeForUsCity("New York");
        assertNotNull(zipCode);
        assertEquals("10001", zipCode);
    }

    @Test
    public void testGetZipCodeForNowhere() {
        String zipCode = client.getZipCodeForUsCity("blablabla");
        assertNotNull(zipCode);
        assertEquals("n/a", zipCode);
    }

}
