package uk.ac.rothamsted.ide;

import com.google.gson.*;
import com.hp.hpl.jena.rdf.model.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.sadiframework.service.annotations.*;
import org.sadiframework.service.simple.SimpleSynchronousServiceServlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.nio.charset.StandardCharsets;

@Name("getCatchmentInfo")
@Description("NWFP rest API: Get information about the catchments based on the identifier")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getCatchmentInfo.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getCatchmentInfo.owl#Output")
public class GetCatchmentInfo extends SimpleSynchronousServiceServlet {

    private static final Logger log = Logger.getLogger(GetCatchmentInfo.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("*** SADI Service ***");
        log.info("Invoking SADI service: getCatchmentInfo");

        // Extract the catchment identifier from the input RDF
        int catchmentId = input.getRequiredProperty(Vocab.has_catchmentId).getResource().getRequiredProperty(Vocab.has_value).getInt();

        // create instance of the output model
        Model outputModel = output.getModel();

        try {
            // initiate GET request to the endpoint
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getCatchments";
            URL url = new URL(endPoint);
            long startTime = System.currentTimeMillis();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // set connection timeout to 5 seconds
            conn.setConnectTimeout(5000);
            // set content reading timeout to 20 seconds
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
                    // Read current unique identifier value
                    Literal idVal = outputModel.createTypedLiteral(element.get("Id").getAsInt());
                    // check if the current id matches the extracted id
                    if (idVal.getInt() == catchmentId){
                        String nameVal = element.get("Name").getAsString();
                        String displayNameVal = element.get("DisplayName").getAsString();
                        String validFromVal = getNullAsEmptyString(element.get("ValidFrom"));
                        String validUntilVal = getNullAsEmptyString(element.get("ValidUntil"));
                        String hydrologicalCatchmentAreaVal = getNullAsEmptyString(element.get("HydrologicalCatchmentArea"));
                        String fencedCatchmentAreaVal = getNullAsEmptyString(element.get("FencedCatchmentArea"));

                        // populate the output model with instances and literal values
                        Resource NameResource = outputModel.createResource();
                        NameResource.addProperty(Vocab.type, Vocab.Name);
                        NameResource.addLiteral(Vocab.has_value, nameVal);
                        output.addProperty(Vocab.has_name, NameResource);

                        Resource DisplayNameResource = outputModel.createResource();
                        DisplayNameResource.addProperty(Vocab.type, Vocab.DisplayName);
                        DisplayNameResource.addLiteral(Vocab.has_value, displayNameVal);
                        output.addProperty(Vocab.has_displayName, DisplayNameResource);

                        Resource ValidFromResource = outputModel.createResource();
                        ValidFromResource.addProperty(Vocab.type, Vocab.ValidFromDate);
                        ValidFromResource.addLiteral(Vocab.has_value, validFromVal);
                        output.addProperty(Vocab.is_validFrom, ValidFromResource);

                        Resource ValidUntilResource = outputModel.createResource();
                        ValidUntilResource.addProperty(Vocab.type, Vocab.ValidUntilDate);
                        ValidUntilResource.addLiteral(Vocab.has_value, validUntilVal);
                        output.addProperty(Vocab.is_validUntil, ValidUntilResource);

                        Resource HydrologicalCatchmentAreaResource = outputModel.createResource();
                        HydrologicalCatchmentAreaResource.addProperty(Vocab.type, Vocab.HydrologicalCatchmentArea);
                        HydrologicalCatchmentAreaResource.addLiteral(Vocab.has_value, hydrologicalCatchmentAreaVal);
                        output.addProperty(Vocab.has_hydrologicalCatchmentArea, HydrologicalCatchmentAreaResource);

                        Resource FencedCatchmentAreaResource = outputModel.createResource();
                        FencedCatchmentAreaResource.addProperty(Vocab.type, Vocab.FencedCatchmentArea);
                        FencedCatchmentAreaResource.addLiteral(Vocab.has_value, fencedCatchmentAreaVal);
                        output.addProperty(Vocab.has_fencedCatchmentArea, FencedCatchmentAreaResource);
                    }

                }
                log.info("getCatchmentInfo service completed.");
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
        public static final Property has_catchmentId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_catchmentId");
        public static final Property has_name = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_name");
        public static final Property has_displayName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_displayName");
        public static final Property is_validFrom = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#is_validFrom");
        public static final Property is_validUntil = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#is_validUntil");
        public static final Property has_hydrologicalCatchmentArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_hydrologicalCatchmentArea");
        public static final Property has_fencedCatchmentArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_fencedCatchmentArea");
        // Data property
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        // Resources
        public static final Resource Name = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Name");
        public static final Resource DisplayName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DisplayName");
        public static final Resource ValidFromDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ValidFromDate");
        public static final Resource ValidUntilDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ValidUntilDate");
        public static final Resource Catchment = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Catchment");
        public static final Resource CatchmentId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentId");
        public static final Resource FencedCatchmentArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FencedCatchmentArea");
        public static final Resource HydrologicalCatchmentArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#HydrologicalCatchmentArea");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getCatchmentInfo.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getCatchmentInfo.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }


}

