package com.unister.semweb.apiontology.zipcodes.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.unister.semweb.apiontology.zipcodes.domain.GetZipcodeRequest;
import com.unister.semweb.apiontology.zipcodes.domain.ZipcodeResponse;

/**
 * ZIP code client.
 * 
 * @author m.priebe
 */
@Service
public class DefaultMarshallingZipcodeService implements MarshallingZipcodeService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private WebServiceTemplate wsTemplate;
    
    @Autowired
    private UsCityZipService usZipService;

    /**
     * Get ZipCodeResponse.
     */
    @PayloadRoot(localPart=GET_ZIPCODE_REQUEST,namespace=NAMESPACE_ZIP)
    public ZipcodeResponse getFirstZipcode(GetZipcodeRequest request) {
        logger.info("Get Zipcode with Request '{}'",request );
        String zipCode = usZipService.getZipcodeForCity(request.getCity());
        logger.info("Create new Response with zipCode '{}'",zipCode);
        ZipcodeResponse zipcodeResponse = new ZipcodeResponse();
        zipcodeResponse.setZipcode(zipCode);
        return zipcodeResponse;
    }

}