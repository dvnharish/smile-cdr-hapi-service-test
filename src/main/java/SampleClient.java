import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import constants.Constants;
import interseptor.ClientFilter;
import service.ProxyService;
import util.SourceReader;

public class SampleClient {

    public static void main(String[] args) throws IllegalAccessException {
        String fileName = Constants.FILE_NAME;
        FhirContext fhirContext = FhirContext.forR4();
        IGenericClient client = fhirContext.newRestfulGenericClient(Constants.SERVER_BASE);
        ProxyService proxyService = new ProxyService(client, new ClientFilter(), new SourceReader(fileName), Constants.TOTAL_NUMBER_OF_ITERATIONS, Constants.ITERATION_INDEX);
        proxyService.executeService();


//		// Create a FHIR client
//		FhirContext fhirContext = FhirContext.forR4();
//		IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
//		ClientFilter clientFilter = new ClientFilter();
//		client.registerInterceptor(new LoggingInterceptor(false));
//		client.registerInterceptor(clientFilter);
//		// Search for Patient resources
//		Bundle response = client
//				.search()
//				.forResource("Patient")
//				.returnBundle(Bundle.class)
//				.execute();
//		response.getEntry().forEach(a->
//				{
//					System.out.println(a.getResource().getNamedProperty("name").getValues().get(0).getNamedProperty("family").getValues());
//					System.out.println(a.getResource().getNamedProperty("name").getValues().get(0).getNamedProperty("given").getValues());
//					System.out.println(a.getResource().getNamedProperty("birthDate").getValues());
//				}
//		);
    }


}
