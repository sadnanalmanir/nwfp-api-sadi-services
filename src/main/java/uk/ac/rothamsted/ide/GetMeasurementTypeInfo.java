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
import java.util.Iterator;
import java.nio.charset.StandardCharsets;

@Name("getMeasurementTypeInfo")
@Description("NWFP rest API: Get information about the types of measurements based on the identifier")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getMeasurementTypeInfo.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getMeasurementTypeInfo.owl#Output")
public class GetMeasurementTypeInfo extends SimpleSynchronousServiceServlet {

    private static final Logger log = Logger.getLogger(GetMeasurementTypeInfo.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("*** SADI Service ***");
        log.info("Invoking SADI service:  getMeasurementTypeInfo");

        // Extract the measurementTypeId from the input RDF:
        int measurementTypeId = input.getRequiredProperty(Vocab.has_measurementTypeId).getResource().getRequiredProperty(Vocab.has_value).getInt();

        // create instance of the output model
        Model outputModel = output.getModel();

        try {
            // initiate GET request to the endpoint
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getMeasurementTypes";
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
                JsonArray jsonArray = new Gson().fromJson(response.toString(), JsonArray.class);
                Iterator<JsonElement> elementIterator = jsonArray.iterator();
                JsonObject element;

                // Read each unique identifier value
                while (elementIterator.hasNext()) {
                    element = elementIterator.next().getAsJsonObject();
                    // Read current unique identifier value
                    Literal idVal = outputModel.createTypedLiteral(element.get("Id").getAsInt());
                    // check if the current id matches the extracted id
                    if (idVal.getInt() == measurementTypeId) {
                        String displayNameVal = getNullAsEmptyString(element.get("DisplayName"));
                        String nameVal = getNullAsEmptyString(element.get("Name"));
                        String unitVal = getNullAsEmptyString(element.get("Unit"));
                        String displayUnitVal = getNullAsEmptyString(element.get("DisplayUnit"));
                        String lLOVal = getNullAsEmptyString(element.get("LLO"));
                        String uLOVal = getNullAsEmptyString(element.get("ULO"));
                        String groupVal = getNullAsEmptyString(element.get("Group"));
                        String systemSetQualityVal = getNullAsEmptyString(element.get("SystemSetQuality"));

                        // populate the output model with instances and literal values
                        Resource DisplayNameResource = outputModel.createResource();
                        DisplayNameResource.addProperty(Vocab.type, Vocab.DisplayName);
                        DisplayNameResource.addLiteral(Vocab.has_value, displayNameVal);
                        output.addProperty(Vocab.has_displayName, DisplayNameResource);

                        Resource NameResource = outputModel.createResource();
                        NameResource.addProperty(Vocab.type, Vocab.Name);
                        NameResource.addLiteral(Vocab.has_value, nameVal);
                        output.addProperty(Vocab.has_name, NameResource);

                        Resource UnitResource = outputModel.createResource();
                        UnitResource.addProperty(Vocab.type, Vocab.Unit);
                        UnitResource.addLiteral(Vocab.has_value, unitVal);
                        output.addProperty(Vocab.has_unit, UnitResource);

                        Resource DisplayUnitResource = outputModel.createResource();
                        DisplayUnitResource.addProperty(Vocab.type, Vocab.DisplayUnit);
                        DisplayUnitResource.addLiteral(Vocab.has_value, displayUnitVal);
                        output.addProperty(Vocab.has_displayUnit, DisplayUnitResource);

                        Resource LLOResource = outputModel.createResource();
                        LLOResource.addProperty(Vocab.type, Vocab.LLO);
                        LLOResource.addLiteral(Vocab.has_value, lLOVal);
                        output.addProperty(Vocab.has_llo, LLOResource);

                        Resource ULOResource = outputModel.createResource();
                        ULOResource.addProperty(Vocab.type, Vocab.ULO);
                        ULOResource.addLiteral(Vocab.has_value, uLOVal);
                        output.addProperty(Vocab.has_ulo, ULOResource);

                        Resource GroupResource = outputModel.createResource();
                        GroupResource.addProperty(Vocab.type, Vocab.Group);
                        GroupResource.addLiteral(Vocab.has_value, groupVal);
                        output.addProperty(Vocab.has_group, GroupResource);

                        Resource SystemSetQualityResource = outputModel.createResource();
                        SystemSetQualityResource.addProperty(Vocab.type, Vocab.SystemSetQuality);
                        SystemSetQualityResource.addLiteral(Vocab.has_value, systemSetQualityVal);
                        output.addProperty(Vocab.has_systemSetQuality, SystemSetQualityResource);
                    }
                }
                log.info("getMeasurementTypeInfo service completed.");
            } else if (status > 299){
                log.info("Error executing the POST method at " + endPoint);
            }
        } catch (Exception e) {
            log.info(e);
        }
    }

    public static final class Vocab {
        private static final Model m_model = ModelFactory.createDefaultModel();
        // Object properties
        public static final Property type = m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        public static final Property has_measurementTypeId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_measurementTypeId");
        public static final Property has_displayName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_displayName");
        public static final Property has_name = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_name");
        public static final Property has_unit = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_unit");
        public static final Property has_displayUnit = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_displayUnit");
        public static final Property has_llo = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_llo");
        public static final Property has_ulo = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_ulo");
        public static final Property has_group = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_group");
        public static final Property has_systemSetQuality = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_systemSetQuality");
        // Data property
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        // Resources
        public static final Resource MeasurementTypeId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#MeasurementTypeId");
        public static final Resource DisplayName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DisplayName");
        public static final Resource Name = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Name");
        public static final Resource Unit = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Unit");
        public static final Resource DisplayUnit = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DisplayUnit");
        public static final Resource LLO = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#LLO");
        public static final Resource ULO = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ULO");
        public static final Resource Group = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Group");
        public static final Resource SystemSetQuality = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#SystemSetQuality");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementTypeInfo.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementTypeInfo.owl#Output");
    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

