package service;

import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import constants.Constants;
import interseptor.ClientFilter;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import util.SourceReader;


public class ProxyService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ProxyService.class);


    private final IGenericClient client;
    private final ClientFilter clientFilter;
    private final List<String> familyNames;
    private final int maxIterations;
    private final int minCacheIterations;
    List<Long> avgTime = new ArrayList<>();


    /**
     * @param client - IGenericClient which is a Restful client from HAPI FHIR - Core Library
     * @param clientFilter - ClientFilter is the interceptor for intercepting the request and response
     * @param sourceReader - a util class for reading the file from resources and getting the information of lastnames
     * @param maxIterations - maximum number if times the service should run for all the lastnames in the file.
     * @param indexToRunWithCache - the index which the call should run with cache in the assignment case it is the second one.
     */
    public ProxyService(IGenericClient client, ClientFilter clientFilter, SourceReader sourceReader, int maxIterations, int indexToRunWithCache) {
        this.familyNames = sourceReader.loadNames();
        this.maxIterations = maxIterations;
        this.minCacheIterations = indexToRunWithCache;
        this.clientFilter = clientFilter;
        client.registerInterceptor(clientFilter);
        client.registerInterceptor(new LoggingInterceptor(false));
        this.client = client;
    }

    /**
     * This function will create a  CacheControlDirective for enabling or disabling the cache in the request.
     * @param familyName String
     * @param callWithCache boolean
     */
    public void callFhirForPatientNameSearch(String familyName, boolean callWithCache) {
        CacheControlDirective directive = new CacheControlDirective();
        directive.setNoCache(!callWithCache);
        callBundle(familyName, directive);
    }

    /**
     * This function will call the FHIR api with the resource of familyName and with cache.
     * @param familyName
     * @param directive
     */
    private void callBundle(String familyName, CacheControlDirective directive) {
        this.client.search().forResource(Constants.PATIENT).where(Patient.FAMILY.matches().value(familyName))
                .returnBundle(Bundle.class).cacheControl(directive).execute();
    }


    /**
     * This function will execute the service multiple number of times based on the minimum and maximum iteration with cache
     * if minimum number of iterations with cache are more than the maximum number of iterations then
     * @throws IllegalAccessException
     */
    public List<Long> executeService() throws IllegalAccessException {
        for (int i = 0; i < this.maxIterations; i++) {
            boolean callWithCache;
            if (this.minCacheIterations > this.maxIterations) {
                throw new IllegalAccessException(" min cache " + this.minCacheIterations + "  are more than maximum number of iterations " + this.maxIterations);
            }
            callWithCache = i == this.minCacheIterations;
            this.familyNames.forEach(familyName -> this.callFhirForPatientNameSearch(familyName, callWithCache));
            this.avgTime.add(this.clientFilter.getAverageResponseTime());
            logger.info("Average Time taken for running {} requests is {}",this.familyNames.size(),this.clientFilter.getAverageResponseTime());
            this.clientFilter.resetTotalTimeAndRequests();
        }
        return this.avgTime;

    }


}
