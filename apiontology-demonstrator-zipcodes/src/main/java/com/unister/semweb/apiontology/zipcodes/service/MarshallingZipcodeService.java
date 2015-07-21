package com.unister.semweb.apiontology.zipcodes.service;

import com.unister.semweb.apiontology.zipcodes.domain.GetZipcodeRequest;
import com.unister.semweb.apiontology.zipcodes.domain.ZipcodeResponse;


/**
 * ZIP code service interface.
 * 
 * @author m.priebe
 */
public interface MarshallingZipcodeService {

    String NAMESPACE = "http://www.unister.de/zipcode/schema/beans";
    String GET_PERSONS_REQUEST = "get-zipcode-request";

    /**
     * Gets person list.
     */
    ZipcodeResponse getFirstZipcode(GetZipcodeRequest request);

}
