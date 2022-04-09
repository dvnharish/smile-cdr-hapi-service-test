import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import constants.Constants;
import interseptor.ClientFilter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import service.ProxyService;
import util.SourceReader;


public class ProxyTestCase {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ProxyTestCase.class);


    static final String fileName = Constants.FILE_NAME;
    static final ClientFilter clientFilter = new ClientFilter();
    static List<String> familyNames;
    static ProxyService proxyService;
    List<Long> captureTime;

    @BeforeClass
    public static void openBrowser() {
        FhirContext fhirContext = FhirContext.forR4();
        IGenericClient client = fhirContext.newRestfulGenericClient(Constants.SERVER_BASE);
        proxyService = new ProxyService(client, clientFilter, new SourceReader(fileName), Constants.TOTAL_NUMBER_OF_ITERATIONS, Constants.ITERATION_INDEX);
        populateFamilyNames();
    }

    @Before
    public void resetValues() {
        clientFilter.resetTotalTimeAndRequests();
        this.captureTime = new ArrayList();
    }

    public static void populateFamilyNames() {
        SourceReader sourceReader = new SourceReader(fileName);
        familyNames = sourceReader.loadNames();
    }

    @Test
    public void testExecute() throws IllegalAccessException {
        this.captureTime = proxyService.executeService();
        log.info("Time Captured "+this.captureTime);
        Assert.assertTrue(this.captureTime.get(0) > this.captureTime.get(1));
        Assert.assertTrue(this.captureTime.get(1) < this.captureTime.get(2));
        Assert.assertTrue(this.captureTime.get(2) > this.captureTime.get(1));
    }

    @Test
    public void testAverageResponseTime() {
        boolean callWithCache;
        for (int i = 0; i < Constants.TOTAL_NUMBER_OF_ITERATIONS; i++) {
            callWithCache = i == Constants.ITERATION_INDEX;
            boolean finalCallWithCache = callWithCache;
            familyNames.forEach(name -> proxyService.callFhirForPatientNameSearch(name, finalCallWithCache));
            this.captureTime.add(clientFilter.getAverageResponseTime());

        }
        log.info("Time Captured "+this.captureTime);

        Assert.assertTrue(this.captureTime.get(0) > this.captureTime.get(1));
        Assert.assertTrue(this.captureTime.get(1) < this.captureTime.get(2));
        Assert.assertTrue(this.captureTime.get(2) > this.captureTime.get(1));

    }


}
