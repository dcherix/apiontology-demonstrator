package com.unister.semweb.apiontology.zipcodes.client;

import com.unister.semweb.apiontology.zipcodes.domain.GetZipcodeRequest;
import com.unister.semweb.apiontology.zipcodes.domain.ZipcodeResponse;


/**
 * ZIP code service interface.
 * 
 * @author m.priebe
 */
public interface MarshallingZipcodeService {

    String NAMESPACE_ZIP = "http://www.unister.de/zipcode/schema/beans";
    String GET_ZIPCODE_REQUEST = "get-zipcode-request";

    /**
     * Gets person list.
     */
    ZipcodeResponse getFirstZipcode(GetZipcodeRequest request);

}
