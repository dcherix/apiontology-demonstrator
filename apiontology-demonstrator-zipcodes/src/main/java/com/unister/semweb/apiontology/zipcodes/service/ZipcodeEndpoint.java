package com.unister.semweb.apiontology.zipcodes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.unister.semweb.apiontology.zipcodes.client.UsZipRestClient;
import com.unister.semweb.apiontology.zipcodes.domain.GetZipcodeRequest;
import com.unister.semweb.apiontology.zipcodes.domain.ZipcodeResponse;

/**
 * ZIP code service endpoint.
 * 
 * @author m.priebe
 */
@Endpoint
public class ZipcodeEndpoint implements MarshallingZipcodeService {

    @Autowired
    private UsZipRestClient usZipClient;
    
    /**
     * Gets Zip Codes.
     */
    @PayloadRoot(localPart = GET_PERSONS_REQUEST, namespace = NAMESPACE)
    public ZipcodeResponse getFirstZipcode(GetZipcodeRequest request) {
        String zipCode = usZipClient.getZipCodeForUsCity(request.getCity());
        
        ZipcodeResponse zipcodeResponse = new ZipcodeResponse();
        zipcodeResponse.setZipcode(zipCode);
        return zipcodeResponse;
    }

}
