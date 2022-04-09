package interseptor;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import constants.Constants;
import org.slf4j.Logger;


public class ClientFilter implements IClientInterceptor {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ClientFilter.class);

    int numberOfRequests;
    long totalTimeForAllRequests;

    @Override
    public void interceptRequest(IHttpRequest theRequest) {
        // TODO document why this method is empty
    }

    @Override
    public void interceptResponse(IHttpResponse theResponse) {

        /*
         * checking if the response consists of CapabilityStatement for initial request and ignoring it for the condition
         * ***/
        if (!theResponse.getResponse().toString().contains(Constants.CAPABILITY_STATEMENT)) {
            /**
             *
             * getting the time taken to execute the request
             * ***/
            long responseInMilliSeconds = theResponse.getRequestStopWatch().getMillis();
            /**
             *
             * ending the current task
             * ***/
            theResponse.getRequestStopWatch().endCurrentTask();
            /**
             *
             * setting the values to totalTime
             * ***/
            setTotalTimeAndRequests(responseInMilliSeconds);
        }
    }


    /**
     * setting the milliseconds per request
     *
     * @param responseInMilliSeconds
     */
    protected void setTotalTimeAndRequests(long responseInMilliSeconds) {
        this.totalTimeForAllRequests += responseInMilliSeconds;
        this.numberOfRequests += 1;
    }

    /**
     * resetting the values
     *
     * @return
     */
    public void resetTotalTimeAndRequests() {
        this.numberOfRequests = 0;
        this.totalTimeForAllRequests = 0;
    }

    /**
     * getting the average response time
     */
    public long getAverageResponseTime() {
        if (this.numberOfRequests > 0) {
            return this.totalTimeForAllRequests / this.numberOfRequests;
        } else {
            return 0;
        }
    }
}
