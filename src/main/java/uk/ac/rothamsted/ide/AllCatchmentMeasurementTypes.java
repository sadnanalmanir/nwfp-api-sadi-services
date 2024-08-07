package uk.ac.rothamsted.ide;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.rdf.model.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.sadiframework.service.annotations.*;
import org.sadiframework.service.simple.SimpleSynchronousServiceServlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Name("allCatchmentMeasurementTypes")
@Description("NWFP rest API: List all measurement types of the catchments by their unique identifiers")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/allCatchmentMeasurementTypes.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/allCatchmentMeasurementTypes.owl#Output")
public class AllCatchmentMeasurementTypes extends SimpleSynchronousServiceServlet {

    private static final Logger log = Logger.getLogger(AllCatchmentMeasurementTypes.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("*** SADI Service ***");
        log.info("Invoking SADI service: allCatchmentMeasurementTypes");

        // create instance of the output model
        Model outputModel = output.getModel();

        try {
            // initiate GET request to the endpoint
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getCatchmentMeasurementTypes";
            URL url = new URL(endPoint);
            long startTime = System.currentTimeMillis();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // set connection timeout to 2 seconds
            conn.setConnectTimeout(5000);
            // set content reading timeout to 5 seconds
            conn.setReadTimeout(20000);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            log.info("Request URL: " + url);

            // gather response from the request
            int status = conn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                log.info("'GET' Request is Successful. Http Status Code: " + status);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuilder response = new StringBuilder();
                log.info("Reading response...");
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                log.info("Done.");
                conn.disconnect();
                log.info("URL Connection closed.");
                long endTime = System.currentTimeMillis();
                log.info("Round trip response time = " + (endTime - startTime) + " ms");
                log.info("API Response Data: " + response);

                // Deserialize response data
                JsonArray jsonArray = new Gson().fromJson(response.toString(), JsonArray.class);
                Iterator<JsonElement> elementIterator = jsonArray.iterator();
                JsonObject element;

                // Read each unique identifier value
                while (elementIterator.hasNext()) {
                    element = elementIterator.next().getAsJsonObject();
                    // read identifier as integer typed literal
                    Literal typeIdVal = outputModel.createTypedLiteral(element.get("type_id").getAsInt());
                    // populate the output model with instances and literal values
                    Resource catchmentMeasurementTypeResource = outputModel.createResource();
                    Resource catchmentMeasurementTypeIdResource = outputModel.createResource();
                    catchmentMeasurementTypeIdResource.addProperty(Vocab.type, Vocab.CatchmentMeasurementTypeId);
                    catchmentMeasurementTypeIdResource.addLiteral(Vocab.has_value, typeIdVal);
                    catchmentMeasurementTypeResource.addProperty(Vocab.has_catchmentMeasurementTypeId, catchmentMeasurementTypeIdResource);
                    catchmentMeasurementTypeResource.addProperty(Vocab.type,output);
                }
                log.info("allCatchmentMeasurementTypes service completed.");
            } else if (status > 299){
                log.info("Error executing the GET method at " + endPoint);
            }
        } catch (Exception e) {
            log.info(e);
        }


    }

    public static final class Vocab {
        private static final Model m_model = ModelFactory.createDefaultModel();
        // Object properties
        public static final Property type = m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        public static final Property has_catchmentMeasurementTypeId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_catchmentMeasurementTypeId");
        // Data property
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        // Resources
        public static final Resource CatchmentMeasurementTypeId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentMeasurementTypeId");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/allCatchmentMeasurementTypes.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/allCatchmentMeasurementTypes.owl#Output");
    }
}

