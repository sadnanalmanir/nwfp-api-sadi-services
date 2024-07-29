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

@Name("allMeasurementTypesLong")
@Description("NWFP rest API: List all types of the measurements in the long-form by their unique identifiers")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/allMeasurementTypesLong.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/allMeasurementTypesLong.owl#Output")
public class AllMeasurementTypesLong extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(AllMeasurementTypesLong.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("*** SADI Service ***");
        log.info("Invoking SADI service:  allMeasurementTypesLong");
        // create instance of the output model
        Model outputModel = output.getModel();

        try {
            // initiate GET request to the endpoint
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getMeasurementTypesLong";
            URL url = new URL(endPoint);
            long startTime = System.currentTimeMillis();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
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
                JsonObject jsonObject = new Gson().fromJson(response.toString(), JsonObject.class);
                JsonArray idsJsonArray= jsonObject.get("Ids").getAsJsonArray();
                JsonArray namesJsonArray= jsonObject.get("Names").getAsJsonArray();
                JsonArray displayNamesJsonArray= jsonObject.get("DisplayNames").getAsJsonArray();
                JsonArray unitsJsonArray= jsonObject.get("Units").getAsJsonArray();
                JsonArray displayUnitsJsonArray= jsonObject.get("DisplayUnits").getAsJsonArray();
                JsonArray systemSetQualityJsonArray= jsonObject.get("SystemSetQuality").getAsJsonArray();

                // check if each array has the same size to match the corresponding index
                // e.g. Ids[0] correspond to Names[0], DisplayNames[0] and so on
                if (idsJsonArray.size() == namesJsonArray.size()
                        && idsJsonArray.size() == displayNamesJsonArray.size()
                        && idsJsonArray.size() == unitsJsonArray.size()
                        && idsJsonArray.size() == displayUnitsJsonArray.size()
                        && idsJsonArray.size() == systemSetQualityJsonArray.size()
                ) {
                    for (int i=0; i<idsJsonArray.size();i++) {
                        // read identifier as integer typed literal
                        Literal idVal = outputModel.createTypedLiteral(idsJsonArray.get(i).getAsInt());
                        // populate the output model with instances and literal values
                        Resource measurementTypeLongResource = outputModel.createResource();
                        Resource measurementTypeLongIdResource = outputModel.createResource();
                        measurementTypeLongIdResource.addProperty(Vocab.type, Vocab.MeasurementTypeLongId);
                        measurementTypeLongIdResource.addLiteral(Vocab.has_value, idVal);
                        measurementTypeLongResource.addProperty(Vocab.has_measurementTypeLongId, measurementTypeLongIdResource);
                        measurementTypeLongResource.addProperty(Vocab.type, output);
                    }
                    log.info("allMeasurementTypesLong service completed.");
                }
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
        public static final Property has_measurementTypeLongId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_measurementTypeLongId");
        // Data property
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        // Resources
        public static final Resource MeasurementTypeLongId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#MeasurementTypeLongId");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/allMeasurementTypesLong.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/allMeasurementTypesLong.owl#Output");

    }
}

