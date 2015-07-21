package com.unister.semweb.apiontology.zipcodes.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.unister.semweb.apiontology.zipcodes.domain.GetZipcodeRequest;
import com.unister.semweb.apiontology.zipcodes.domain.ZipcodeResponse;
import com.unister.semweb.apiontology.zipcodes.service.MarshallingZipcodeService;

/**
 * ZIP code client.
 * 
 * @author m.priebe
 */
@Component
public class ZipcodeClient implements MarshallingZipcodeService {

    @Autowired
    private WebServiceTemplate wsTemplate;

    /**
     * Gets person list.
     */
    public ZipcodeResponse getFirstZipcode(GetZipcodeRequest request) {
        ZipcodeResponse response = (ZipcodeResponse) wsTemplate.marshalSendAndReceive(request);
        return response;

    }

}