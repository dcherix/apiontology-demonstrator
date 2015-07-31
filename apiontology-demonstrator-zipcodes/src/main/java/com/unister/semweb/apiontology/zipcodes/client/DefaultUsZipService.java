package com.unister.semweb.apiontology.zipcodes.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.SourceExtractor;

/**
 * 
 * @author m.priebe
 *
 */
public class DefaultUsZipService implements UsCityZipService {

    private static String BASE_URL = "http://www.webservicex.net/uszip.asmx/GetInfoByCity?USCity={city}";

    private RestTemplate restTemplate;

    private final Logger logger;

    /**
     * Constructor.
     * 
     * @param restTemplate
     */
    public DefaultUsZipService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    /**
     * Returns the zip code for the given US city {@link String}.
     * 
     * @param city
     * @return {@link String}
     */
    @Override
    public String getZipcodeForCity(String city) {
        logger.info("GetZipcode for City '{}'",city);
        Map<String, String> params = new HashMap<>();
        params.put("city", city);
        NewDataSet response = restTemplate.getForObject(BASE_URL, NewDataSet.class, params);
        if(response != null && response.getTables() != null) {
            if(response.getTables().isEmpty()) {
                logger.warn("Empty Response for city '{}'!");
            } else {
                Table t = response.getTables().get(0);
                if(t == null) {
                    logger.error("Empty Table 'null' for city '{}'!",city);
                } else {
                    String zip = t.getZip();
                    logger.info("Received zip '{}' for city '{}'",zip,city);
                    return zip;
                }
            }
        }
        logger.warn("No Zip Code available for city with name '{}', return n/a!",city);
        return "n/a";
    }
    

    /**
     * {@link SourceExtractor} implementation used to extract the zip code.
     * 
     * @see SourceExtractor
     * 
     * @author m.priebe
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "NewDataSet")
    public static class NewDataSet {

        @XmlElement(name = "Table")
        private List<Table> tables;

        /**
         * @return the tables
         */
        public List<Table> getTables() {
            return tables;
        }

        /**
         * @param tables
         *            the tables to set
         */
        public void setTables(List<Table> tables) {
            this.tables = tables;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Table {

        @XmlElement(name = "CITY")
        private String city;

        @XmlElement(name = "STATE")
        private String state;

        @XmlElement(name = "ZIP")
        private String zip;

        @XmlElement(name = "AREA_CODE")
        private String areaCode;

        @XmlElement(name = "TIME_ZONE")
        private String timeZone;

        /**
         * @return the city
         */
        public String getCity() {
            return city;
        }

        /**
         * @param city
         *            the city to set
         */
        public void setCity(String city) {
            this.city = city;
        }

        /**
         * @return the state
         */
        public String getState() {
            return state;
        }

        /**
         * @param state
         *            the state to set
         */
        public void setState(String state) {
            this.state = state;
        }

        /**
         * @return the zip
         */
        public String getZip() {
            return zip;
        }

        /**
         * @param zip
         *            the zip to set
         */
        public void setZip(String zip) {
            this.zip = zip;
        }

        /**
         * @return the areaCode
         */
        public String getAreaCode() {
            return areaCode;
        }

        /**
         * @param areaCode
         *            the areaCode to set
         */
        public void setAreaCode(String areaCode) {
            this.areaCode = areaCode;
        }

        /**
         * @return the timeZone
         */
        public String getTimeZone() {
            return timeZone;
        }

        /**
         * @param timeZone
         *            the timeZone to set
         */
        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return String.format("Table [city=%s, state=%s, zip=%s, areaCode=%s, timeZone=%s]", city, state, zip,
                    areaCode, timeZone);
        }

    }



}
